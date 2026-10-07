# YTE（Yunzhu Transit Extension）架构重构方案

> 本文档是重构**提案**，不包含任何已落地的代码改动。目标是把电梯外召部件与轿厢从
> 「一个样式一个方块 + 一个样式一个渲染器」的硬编码模式，迁移到 MTR 追加包风格的
> 数据驱动体系，同时收敛 Mixin、提升渲染效率、降低与其他 Mod 的冲突风险。

---

## 目录

1. [现状诊断](#一现状诊断)
2. [总体目标架构](#二总体目标架构)
3. [需求 1：数据驱动外召部件](#三需求-1数据驱动外召部件)
4. [需求 2：轿厢模型自定义](#四需求-2轿厢模型自定义)
5. [需求 3：架构、渲染效率与 Mixin 削减](#五需求-3架构渲染效率与-mixin-削减)
6. [实施路线图](#六实施路线图)
7. [风险与注意点](#七风险与注意点)

---

## 一、现状诊断

### 1.1 代码规模（fabric 为唯一真源，forge 由 `setupFiles` 任务从 fabric 拷贝）

| 维度 | 数量 |
|---|---|
| Java 文件（单 loader） | ~716 |
| block 类（`mod/block`） | **329** |
| render 类（`client/render`） | **232** |
| view 框架类（`client/view`） | 12 |
| Mixin | **16**（7 common + 9 client） |

每个「电梯外召样式」都是一套完整组合：

```
Block 子类 + BlockEntity 子类 + Render 子类 + 方块贴图 + item 贴图（+ 可能的屏幕/箭头贴图）
```

### 1.2 现有分层

```
top/xfunny/
  core/                          # 与 MTR-core 同构的数据/模拟层（YteMain/YteSimulator/YteLiftConfig...）
  entrypoint/                    # Forge/Fabric 入口
  mixin/                         # 16 个 Mixin
  mod/
    block/base/                  # LiftButtonsBase / LiftPanelBase / LiftDestinationDispatchTerminalBase
    block/                       # 329 个样式方块
    client/render/               # 232 个样式渲染器
    client/view/                 # 类 Android View 框架（RenderView/DirectRenderer/LinearLayout/FrameLayout/...）
    lift/                        # 业务状态机（到站灯策略、方向显示、运动曲线、门控、调度）
    packet/ registry/ util/ ...
```

优点：

- `client/view` 已经是一套相当不错的声明式渲染框架（`RenderView`/`DirectRenderer`/
  `LinearLayout`/`FrameLayout`/`ButtonView`/`LiftFloorDisplayView`/`LiftArrowView`/`TextView`）。
- `DirectRenderer` 已绕开 `MainRenderer.scheduleRender`，避免阴影 pass 重复绘制。
- 业务逻辑（`lift/` 包）已经抽得比较清晰：到站灯策略、方向显示、调度代价函数、
  运动曲线、门控状态机彼此解耦。

问题：

- 每个 `RenderXxx` 都在 **每一帧用 Java 硬编码重建整棵 View 树**（例如
  `RenderTestLiftButtons4.render()` 里 `new LinearLayout()` 全程在帧内 `new`），分配压力大。
- 样式爆炸：品牌 × 型号 × 横竖 × 奇偶 × 有无屏，全部展开成独立类。
- Mixin 集中在两个高风险点：`MixinLift` 用 `@Overwrite` 重写了整个 `Lift.tick()`，
  以及 4 个 Accessor/Invoker 型 Mixin 只是为了访问 MTR 内部字段。

### 1.3 Mixin 现状

| Mixin | 类型 | 风险 |
|---|---|---|
| `MixinLift` | `@Overwrite Lift.tick()` | **最高**，最易与其他 Mod 冲突 |
| `MixinLiftSchema` | Accessor | 中（依赖 MTR 字段名） |
| `MixinLiftFields` | Accessor/Invoker | 中 |
| `MixinNameColorDataBaseSchema` | Accessor | 中 |
| `MixinUpdateDataRequest` | `@Inject` TAIL | 低 |
| `MixinBlockLiftButtons` | `@Redirect` | 中 |
| `MixinBlockLiftPanelBase` | `@Inject` HEAD | 中 |
| `MixinRenderLiftButtons` | `@Redirect`/`@ModifyVariable` | 中 |
| `MixinRenderLiftPanel` | `@Redirect`/`@ModifyVariable` | 中 |
| `MixinRenderLifts` | `@Inject` | 中 |
| `MixinLiftDoorBlockEntity` | `@Inject` HEAD | 低 |
| `MixinLiftSelectionScreen` | 多个 `@Inject`/`@Redirect` | 中 |
| `MixinLiftCustomizationScreen` | 多个 `@Inject`/`@Unique` | 中 |
| `MixinBlockEntityRenderer` | `@ModifyVariable`/`@ModifyArg` | 中 |
| `MixinMainRenderer` | `@Inject` HEAD | 低 |
| `MixinDynamicTextureCache` | `@Inject` TAIL | 低 |

---

## 二、总体目标架构

把「外召部件」和「轿厢」都变成**资源包驱动的数据**，代码只保留几类通用宿主 + 一套解释器：

```
资源(JSON/贴图/模型) → Registry 解析 → BlockEntity 存 styleId → 一个通用 Renderer 按描述渲染
```

镜像 MTR 追加包的两条成熟管线：

- **需求 1** 参照 MTR `CustomResources`
  （`mtr_custom_resources.json` + `buildSrc/.../schema/resource/*.json` + `CustomResourceLoader`）。
- **需求 2** 参照 MTR 车辆模型体系
  （`VehicleModel` + `ModelProperties` + `ModelPropertiesPart` + `PartCondition` +
  `DoorAnimationType`），其中「活动可拓展部分」即 `ModelPropertiesPart` 的
  `condition`/`doorAnimationType`/`doorXMultiplier` 等字段。

核心不变量：

1. **fabric 是唯一源码真源**，forge 通过 `setupFiles` 任务从 fabric 拷贝。
2. 所有新代码走 `org.mtr.mapping` 抽象层，保持 fabric/forge 单源码兼容。
3. 业务逻辑（`lift/` 包）保持不动，只替换「方块宿主 + 渲染器」这一层。

---

## 三、需求 1：数据驱动外召部件

### 3.1 目标形态

4 个**种类块**（kind 影响交互与多格摆放，必须保留为不同 Block）：

```
yte:lift_button_fixture          # 外呼按钮 + 屏幕
yte:lift_panel_fixture           # 厅门侧面板
yte:hall_lantern_fixture         # 到站灯
yte:destination_dispatch_fixture # 目的层调度终端
```

每个 kind 只有 **1 个 Block + 1 个 BlockEntity + 1 个 Renderer**。样式（品牌/型号/
横竖/奇偶）全部由 `styleId`（存 BE NBT）决定。

迁移结果：329 个 block 类 → **4 个 Block + 4 个 BE + 4 个 Renderer**，其余全部迁移为
JSON 样式定义。

### 3.2 资源加载管线（仿 MTR）

新增 `top.xfunny.mod.client.CustomResourceLoader`（或 `LiftFixtureRegistry`）：

```java
ResourceManagerHelper.readAllResources(
    new Identifier("yte", "lift_fixtures.json"), inputStream -> {
        LiftFixturesResource fixtures = parse(inputStream);
        fixtures.iterate(f -> LIFT_FIXTURES_CACHE.put(f.getId(), f));
    });
```

样式在资源包里以 `assets/yte/lift_fixtures.json` 提供，可被追加包覆盖/叠加，完全复用
Minecraft 资源包体系，天然支持「追加包添加样式」。

### 3.3 JSON Schema 草案（新增 `schema/resource/liftFixture.json`）

```jsonc
{
  "id": "mitsubishi_nexway_button_1",
  "name": "三菱 NexWay 外呼 1",
  "kind": "BUTTON_PANEL",            // BUTTON_PANEL | PANEL | HALL_LANTERN | DISPATCH_TERMINAL

  "placement": {
    "odd": false,                    // 奇偶（单块/双块）
    "flat": false,                   // 贴墙薄片 or 凸出
    "voxel": [4, 0, 0, 12, 16, 1],   // 碰撞箱（沿用 getVoxelShapeByDirection 语义）
    "width": 8, "height": 16         // 渲染面尺寸（16 分制）
  },

  // 自发光/半透明渲染层
  "renderLayer": "EXTERIOR",         // EXTERIOR | INTERIOR | LIGHT_TRANSLUCENT ...

  "children": [                      // 复用现有 view 树语义，改为声明式
    {
      "type": "ButtonView",
      "id": "up",
      "texture": "textures/block/xxx_up.png",
      "dimension": 3,                // /16
      "margin": [0, 0, 0, 0],
      "gravity": "CENTER_HORIZONTAL",
      "defaultColor": "0xFFFFFFFF",
      "pressedColor": "0xFFFFCB3B",
      "hoverColor": "0xFFFF9933",
      "flip": [false, true]
    },
    {
      "type": "LiftFloorDisplayView",
      "id": "display_0",
      "font": "acmeled", "fontSize": 6, "color": "0xFFFF0000",
      "width": 3, "height": 3,
      "displayLength": [3, 0.05],    // 滚动字数阈值、速度
      "textureId": "nexway_display_0"
    },
    {
      "type": "LiftArrowView",
      "id": "arrow",
      "texture": "textures/block/mitsubishi_nexway_1_arrow.png",
      "animation": { "blink": true, "blinkSpeed": 0.5 }
    }
  ],

  // 到站灯策略（复用现有 LiftArrivalLanternPolicy 体系）
  "lanternPolicy": "mitsubishi_mpvf",

  // 调度终端专用：键位映射（复用 DefaultButtonsKeyMapping 的映射数据）
  "keyMapping": {
    "screen": "home",
    "buttons": { "number1": [0, 0, 0.3, 0.2] }
  }
}
```

### 3.4 代码结构

```
top/xfunny/mod/block/
  BlockLiftHallFixture.java          # 统一基类：kind 决定交互
  BlockLiftButtonFixture.java        # 4 个薄子类，仅差异 onUse2/摆放
  BlockLiftPanelFixture.java
  BlockHallLanternFixture.java
  BlockDestinationDispatchFixture.java
top/xfunny/mod/client/
  CustomResourceLoader.java          # 加载 lift_fixtures.json + lift_cars.json
  render/RenderLiftHallFixture.java  # 解释 children 声明，一帧内按树渲染
  resource/LiftFixtureResource.java  # 由 schema 生成
  resource/LiftFixturesResource.java
```

**渲染器**：启动时把 JSON 的 `children` **预编译成 View 树并缓存**（按 styleId），每帧只更新
动态叶子（`textureId` 的文字、`ButtonView` 的点亮态、`LiftFloorDisplayView` 的楼层）。
这同时解决需求 3 的渲染效率问题。

**选择样式**：做一个 `LiftFixtureStyleSelectorScreen`（照搬 MTR `DashboardList` + 现有
`LiftSelectionButtonWidget`），或「刷子轮换样式」。物品侧由 1 个 item + NBT `styleId` 承载。

---

## 四、需求 2：轿厢模型自定义

### 4.1 现状与差距

MTR 的 `LiftResource`（`CustomResources.lifts`）目前只有
`{id, name, color, textureResource}`——**只能换贴图，不能换模型**。轿厢模型是写死的
`ModelLift1`（`RenderLifts.render()` 里 `new ModelLift1(...).render(...)`）。

目标是：**轿厢模型 + 可动/可拓展部件 + 轿厢内屏幕 + 轿厢按钮**，即 MTR 车辆追加包的能力。

### 4.2 方案：YTE 自建 `lift_cars.json` 资源体系

定义 `LiftCarResource`（参考 `VehicleResource` 精简）：

```jsonc
{
  "id": "mitsubishi_nexway_car",
  "name": "...",
  "color": "#RRGGBB",
  "modelResource": "models/block/lift_car/nexway.bbmodel",
  "textureResource": "textures/block/lift_car/nexway.png",
  "modelPropertiesResource": ".../nexway.json",
  "positionDefinitionsResource": ".../nexway_positions.json",

  "floors": [ ],        // 可站区域（riding 判定，照搬 VehicleResource.floors）
  "doorways": [ ],      // 门洞（判定能否上下车）

  "models": [
    {
      "modelResource": "...",
      "textureResource": "...",
      "modelPropertiesResource": "...",
      "positionDefinitionsResource": "..."
    }
  ]
}
```

### 4.3 部件属性（「活动可拓展部分」）

采用 MTR `ModelPropertiesPart` 语义：

```jsonc
{
  "names": ["door_left"],              // BBModel 里的组名
  "condition": "DOORS_OPENED",         // NORMAL | DOORS_CLOSED | DOORS_OPENED | ...
  "renderStage": "EXTERIOR",           // LIGHT | INTERIOR | EXTERIOR ...
  "type": "DOORWAY",                   // NORMAL | DISPLAY | FLOOR | DOORWAY
  "doorAnimationType": "PLUG_SLOW",    // 门动画曲线
  "doorXMultiplier": 1, "doorZMultiplier": 0.4,
  "renderFromOpeningDoorTime": 0, "renderUntilOpeningDoorTime": 500
}
```

对轿厢，在 MTR `PartCondition` 之外**扩展 YTE 专属条件**（放 YTE 自己的枚举，不污染 MTR）：

```java
enum LiftCarPartCondition {
    NORMAL,
    DOORS_OPENED, DOORS_CLOSED,
    MOVING_UP, MOVING_DOWN, STOPPED,
    ARRIVED_AT_FLOOR,       // 到站灯
    CAR_CALL_LIT            // 轿厢按钮点亮
}
```

### 4.4 轿厢内屏幕 + 按钮

- **轿厢内屏幕**：`type: DISPLAY` 部件 + `displayType`，对齐 MTR `DisplayType`（楼层、
  方向、下一层）。渲染时读现有 `LiftDisplayState`（数据已足够）。
- **轿厢按钮**：`type: CAR_BUTTON` 部件 + `names` 映射到楼层按钮位，点亮条件绑定到
  `lift.hasInstruction(floorIndex)`。这是 MTR 轿厢模型没有的，需要 YTE 自定义
  `PartType.CAR_BUTTON`。

### 4.5 渲染管线（关键：只注入一个点）

MTR 在 `RenderLifts.render()` 里硬编码：

```java
new ModelLift1(...).render(storedMatrixTransformations, null,
    getLiftResource(lift.getStyle()).getTexture(), ...);
```

方案：用**一个 `@Redirect`** 替换这段 `ModelLift1.render` 调用为 YTE 的
`RenderLiftCar.render(...)`。当 `lift.getStyle()` 命中 `lift_cars.json` 时走自定义模型，
否则回退 `ModelLift1`。比 `@Overwrite RenderLifts.render` 的冲突风险低得多。

模型解析/优化复用 MTR 的 `DynamicVehicleModel` + `OptimizedModelWrapper`（按材质分组批渲染），
让轿厢渲染性能与车辆一致。

---

## 五、需求 3：架构、渲染效率与 Mixin 削减

### 5.1 目录重构（建议）

```
top/xfunny/
  core/                  # 与 MTR-core 同构的数据/模拟层（保留）
  mod/
    block/
      base/              # 外召部件合并后的 4 个统一 base
      lift/              # 轿厢/门相关块（保留少量）
      misc/              # 售票机、PIDS、闸机等非电梯块（不动）
    client/
      resource/          # 新增：JSON 资源解析（仿 MTR resource 包）
      render/
        lift/            # RenderLiftHallFixture, RenderLiftCar, RenderLiftDoor
        misc/
      view/              # 保留并强化（预编译、缓存）
    lift/                # 业务状态机（保留，已很清晰）
    packet/ registry/ util/ ...
  mixin/                 # 收敛到 5~7 个
```

### 5.2 Mixin 逐个处置（16 → 目标 5~7）

| Mixin | 现状 | 处置 |
|---|---|---|
| `MixinLiftSchema` / `MixinLiftFields` / `MixinNameColorDataBaseSchema` | Accessor/Invoker | **合并为 1 个 `MixinLiftAccess`**，或改用反射（`LiftDestinationDispatchTerminalBase` 已示范反射）彻底删除 |
| `MixinUpdateDataRequest` | `@Inject` TAIL 清理孤儿配置 | 保留（低风险），或改为事件订阅 |
| `MixinBlockLiftButtons` + `MixinBlockLiftPanelBase` | 让 MTR 识别 YTE linker | **删除**：外召部件改用自己的方块后，`onUse2` 自己判断 linker，不再需要注入 MTR 方块 |
| `MixinRenderLiftButtons` + `MixinRenderLiftPanel` | 司机模式隐藏 + 识别 linker | **删除**：统一 `RenderLiftHallFixture` 自含该逻辑 |
| `MixinRenderLifts` | 磁栅标记 + 显示替换 | **保留但收缩**：磁栅标记可保留；显示替换改走自定义轿厢渲染。若轿厢用 `@Redirect ModelLift1.render`，可与此处合并 |
| `MixinLift` | `@Overwrite tick()`（最大风险） | **重构**：把运动/门/方向逻辑抽到 `LiftMotionController`/`LiftDoorController`，MixinLift 只保留 `@Inject` 关键钩子（HEAD 读配置、RETURN 修正 doorValue），去掉 `@Overwrite` |
| `MixinLiftDoorBlockEntity` | 厅门只开在目标层 | 保留（`@Inject HEAD cancellable`，风险低） |
| `MixinLiftSelectionScreen` | 门控按钮 + 楼层长按/双击取消 | 保留（逻辑 UI，与渲染无关），但可考虑用事件替代 |
| `MixinLiftCustomizationScreen` | 专业模式 UI | 保留 |
| `MixinBlockEntityRenderer` / `MixinMainRenderer` / `MixinDynamicTextureCache` | 渲染接线 | `MixinMainRenderer` 可考虑在 `DirectRenderer` 内直接读 `OptimizedRenderer.renderingShadows()`，**删除**；其余保留 |

### 5.3 渲染效率

1. **View 树预编译 + 缓存**：`RenderLiftHallFixture` 按 `styleId` 缓存整棵树，每帧只 set
   动态字段（现在每帧 `new` 整棵树）。
2. **继续走 `DirectRenderer`**：避免 `MainRenderer.scheduleRender` 的阴影重复；把
   `shadowPass` 判断内联到 `DirectRenderer`，删 `MixinMainRenderer`。
3. **轿厢模型批渲染**：复用 MTR `OptimizedModelWrapper` 按材质分组，减少 draw call。
4. **保留/加强剔除**：`MixinBlockEntityRenderer` 已有 32 格距离剔除，可再加视锥剔除与
   「样式级」渲染缓存。

---

## 六、实施路线图

分 5 阶段，每阶段可独立发版：

1. **P0 架构梳理（不改行为）**
   - 确认 fabric 唯一真源；把 `client/newRender` 定为新渲染器落点；跑通 `setupFiles`。

2. **P1 外召部件数据化（需求 1）**
   - 新增 `CustomResourceLoader` + `lift_fixtures.json` schema + `RenderLiftHallFixture`。
   - 先把 `TestLiftButtons` / `TestLiftPanel` / `TestLiftHallLanterns` /
     `TestLiftDestinationDispatchTerminal` 四个测试件迁移验证，再批量迁移品牌样式。

3. **P2 轿厢自定义（需求 2）**
   - `lift_cars.json` + `RenderLiftCar` + 一个 `@Redirect` 替换 `ModelLift1`。
   - 先静态模型 → 门动画 → 车内屏 → 轿厢按钮。

4. **P3 Mixin 收敛（需求 3）**
   - 合并 Accessor、去 `@Overwrite`、删 4 个外召相关 Mixin，16 → 目标 7。

5. **P4 存量迁移与兼容**
   - 把 329 个旧块 / 232 个渲染器**作为「内置默认样式」迁移进 JSON**（保证旧存档方块 id
     通过 `Blocks`/`BlockEntityTypes` 别名映射到新体系，避免崩档），再逐步删旧类。

---

## 七、风险与注意点

- **存档兼容**：旧方块 id / BE 数据必须做**别名映射**（保留旧 id 注册，BE NBT 里补
  `styleId`）。否则升级即崩档。
- **MTR 内部类反射/字段名**：当前对 `LiftSchema.instructions/speed` 用反射，MTR 4.0.x
  是固定版本依赖，风险可控，但建议收敛到唯一 Accessor 入口。
- **`@Overwrite tick()`**：任何 MTR 小版本更新都可能破坏，务必在 P3 移除。
- **JSON 风格与 MTR 兼容**：schema 字段命名、`ResourceProvider` 机制尽量照搬 MTR，未来
  可直接让 MTR 追加包作者零学习成本迁移，甚至考虑**复用 MTR 的 `liftResource` 通道**
  而非另起炉灶。
- **多平台**：所有新代码走 `org.mtr.mapping` 抽象，保持 fabric/forge 单源码。
- **资源包加载时机**：`CustomResourceLoader` 需在资源重载（`F3+T` / 进入世界）时重新
  解析并清空 View 树缓存，避免样式残留。
