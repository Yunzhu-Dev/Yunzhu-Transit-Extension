# YTE 重构需求（REQUIREMENTS）

> 本文档是重构的**需求基准**，回答「要做什么、做成什么样、哪些已经定了」。
> 具体技术方案见同目录 `ARCHITECTURE.md`。
>
> 状态：进行中（P1 · HallCallBox 数据驱动外召）

---

## 1. 总体目标

把电梯外召部件与轿厢从「**一个型号 = 一个方块类 + 一个渲染器类**」的硬编码模式，
重构为「**引擎 + 数据**」的数据驱动架构，参照 MTR 追加包体系。

最终效果：玩家 / 追加包作者通过 **JSON + 资源包** 即可添加型号样式、自定义渲染与动画，
无需修改 mod 代码。

---

## 2. 背景与现状

- 单 loader 约 716 个 Java 文件，其中 329 个方块类、232 个渲染器类。
- 每个型号样式 = `Block 子类 + BlockEntity 子类 + Render 子类 + 贴图` 的固定组合。
- 逻辑散落在 `LiftButtonsBase` / `LiftPanelBase` / `LiftDestinationDispatchTerminalBase` 三个基类。
- Mixin 16 个（7 common + 9 client），其中 `MixinLift` 用 `@Overwrite` 重写了整个 `Lift.tick()`，冲突风险最高。
- fabric 为唯一源码真源，forge 由 `setupFiles` 任务从 fabric 拷贝。

---

## 3. 功能需求

### F1 · 数据驱动外召部件（核心）

- 合并四类外召部件：外呼按钮（lift_button）、面板（lift_panel）、到站灯（hall_lantern）、目的层调度终端（LiftDestinationDispatchTerminal）。
- 统一为 4 个 kind 块 + 通用渲染器，消除 329 个方块类 / 232 个渲染器类。
- 型号样式通过 **MTR 追加包体系**加载（资源包覆盖 JSON，如 `assets/yte/lift_fixtures.json`）。
- 首个落地：**HallCallBox**（自包含，不继承 LiftButtonsBase）。
- 所有型号个性化内容（贴图/模型/颜色/布局/文案）**抽离出代码，进入数据**。

### F2 · 轿厢模型自定义

- 自定义轿厢模型（参照 MTR 车辆追加包）。
- 可动 / 可拓展部件（门动画、条件部件）。
- 轿厢内屏幕自定义。
- 轿厢按钮渲染。

### F3 · 高级动画自定义

- PPT 幻灯片式**切换动画**：外呼渲染各元素支持入场 / 退场 / 切换 / 持续动画。
- **观察者模式**订阅状态变化 + **时间线补间引擎**执行动画。
- 动画策略**声明式写入型号数据**（JSON DSL：`trigger` / `transition` / `duration` / `easing` / `delay`）。

### F4 · 架构与兼容性

- 提升渲染效率：View 树预编译缓存、批渲染、距离/视锥剔除。
- 减少 / 调整 Mixin 注入点，降低与其他 Mod 冲突（目标 16 → 5~7）。
- 逻辑分层：`service`（纯逻辑）/ `data`（持久化）/ `block entity`（薄壳）。

---

## 4. 关键架构决策（已确定）

| # | 决策 | 说明 |
|---|---|---|
| 1 | fabric 唯一真源 | forge 由 `setupFiles` 任务拷贝，只改 fabric |
| 2 | HallCallBox 自包含 | 不再继承 LiftButtonsBase，摆脱旧基类 |
| 3 | 逻辑抽成 service | 呼梯 / 获取楼层 / 到站灯 → `lift/logic/` 下 `HallCallService` / `LiftFloorService` / `LiftLanternService`；BlockEntity 只做「数据容器 + 触发器」 |
| 4 | 行为 vs 参数边界 | 行为（屏显算法、按钮类型、灯策略、动画原语、easing）= **引擎有限枚举目录**；参数（颜色/贴图/位置/布局）= **纯数据** |
| 5 | 动画三段式 | ① 可观察状态仓库（在现有 `LiftDisplayState` 等上加变更事件）→ ② 中央 `AnimationManager` tick 时间线 → ③ 视图只画插值结果 |

### 逻辑分层示意

```
BlockEntity（薄壳：持有数据 + 回调里调 service）
        │ 调用
lift/logic/*（无状态 service：HallCallService / LiftFloorService / LiftLanternService）
        │ 读写
LiftFixtureData（可持久化数据：trackPositions / buttonPositions / pressedDirection / styleId + NBT）
```

---

## 5. 行为目录（引擎内保留的有限枚举）

> 关键边界：资源包/追加包**不能携带 Java 逻辑**。
> 因此「行为」必须在引擎内实现为有限枚举，「数据」只负责**选择 + 参数化**。

```
displayType   : TEXT | SEGMENTED | DOT_MATRIX | LCD | HIP43        # 屏显算法
buttonType    : MECHANICAL | TOUCH | LCD_SEGMENTED | DOT_MATRIX    # 按钮渲染
arrowAnim     : BLINK | SCROLL | NONE                              # 箭头动画
lanternPolicy : DEFAULT | HITACHI_GHL | MITSUBISHI_MPVF | NEXWAY   # 到站灯策略（已有）
transition    : FADE | SLIDE | SCALE | ROTATE | COLOR | UV_SCROLL | WIPE | TYPEWRITE | PULSE | FLIP
easing        : linear | easeIn | easeOut | easeInOut | cubic-bezier | spring
trigger       : onChange | onEnter | onExit | while | periodic
```

---

## 6. 非功能需求

- **性能**：无动画时视图只画稳态；时间线仅对 active 项 tick；不逐帧分配对象。
- **多平台**：所有新代码走 `org.mtr.mapping` 抽象，保持 fabric / forge 单源码。
- **存档兼容**：旧方块 id / BE 数据做别名映射，升级不崩档。
- **可扩展**：新增型号 = 新增数据；新增行为 = 引擎枚举加一个值 + 实现。

---

## 7. 实施阶段

| 阶段 | 内容 | 状态 |
|---|---|---|
| P0 | 基线构建验证 + 分支保护 | 待做 |
| P1 | HallCallBox 自包含 + 数据驱动外召（首个闭环） | **进行中** |
| P2 | 轿厢模型自定义 | 待做 |
| P3 | Mixin 收敛（16 → 5~7）、渲染效率 | 待做 |
| P4 | 存量 329 方块迁移为内置样式 + 存档兼容 | 待做 |

---

## 8. 开放问题（待拍板）

1. **样式选择交互**：独立选择器屏幕 vs 刷子轮换？（倾向前者）
2. **存档兼容**：旧方块 id 永久保留别名，还是仅过渡期？
3. **动画引擎落点**：本轮（P1）就做，还是 P1 先静态渲染、动画放后续阶段？
