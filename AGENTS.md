# AGENTS.md — Yunzhu Transit Extension

## 1. 项目定位

Yunzhu Transit Extension（模组 ID：`yte`）是 Minecraft Transit Railway（MTR）的装饰/功能扩展模组。其重点是电梯系统：轿厢按钮、层站按钮、门、到站灯、显示器、目的层分配、运行/消防/检修/救援逻辑，以及地铁站装饰内容。

本仓库是一个 Gradle 多模块工程，面向 Fabric 与 Forge。当前默认目标是 Minecraft `1.20.1`、MTR `4.0.0`、项目版本 `1.0.2-prerelease.3`；版本矩阵另包含 1.16.5、1.17.1、1.18.2、1.19.2、1.19.4、1.20.1、1.20.4。不要把默认版本误认为唯一支持版本。

项目语言主要为 Java（源/资源规模较大），构建脚本为 Groovy Gradle，辅助脚本为 PowerShell。Java 编译统一使用 UTF-8；Java toolchain 版本由 `buildSrc` 的 `BuildTools` 根据 Minecraft 版本决定。

## 2. 仓库地图与所有权

| 路径 | 作用 | 修改原则 |
| --- | --- | --- |
| `fabric/` | **权威共享实现**：绝大部分 Java 源码、资源、Mixin、Fabric 元数据 | 跨加载器功能优先在此修改。 |
| `forge/` | Forge 构建模块及 Forge 专有入口/元数据 | `setupFiles` 会从 `fabric` 覆盖其 `top/xfunny/{mod,mixin,core}`、`assets`、`data` 与 `yte.mixins.json`；不要手改这些生成副本。仅维护明确的 Forge 专有文件。 |
| `buildSrc/` | 共享构建辅助类，如版本、映射与发布产物复制规则 | 改版本解析或发布规则前先理解所有子模块调用点。 |
| `libs/` | 本地 MTR server JAR 依赖缓存，按 loader/MC 版本命名 | 视为二进制依赖；不随意替换、重命名或提交生成物。 |
| `deploy.gradle` | 跨版本构建、下载、部署与启动验证自动化 | 会访问网络、读取/写入外部 Minecraft 目录并可启动客户端；执行前确认参数与目标路径。 |
| `gradle.properties` | 默认 MC/MTR/项目版本及部署矩阵 | 使用 `-PminecraftVersion=...` 等属性临时覆盖，避免无意修改默认矩阵。 |
| `README*.md` | 面向用户的中英文说明与字体许可说明 | 新增用户可见功能时同步更新；许可证表不可随意删除。 |

根目录的 `build/`、`.gradle/`、IDE 配置、`fabric/run/`、Forge 从 Fabric 同步的目录、`fabric.mod.json` 与 `Keys.java` 均为忽略的构建/运行产物，通常不提交。

## 3. 构建模型（关键约束）

1. 根 `settings.gradle` 只包含 `fabric` 与 `forge` 两个子项目；根 `build.gradle` 为两者配置 Java、仓库、注解依赖与 UTF-8。
2. `fabric/build.gradle` 使用 Fabric Loom。`setupFiles` 从以下模板生成运行时文件：
   - `fabric/src/main/fabric.mod.template.json` → `fabric/src/main/resources/fabric.mod.json`
   - `fabric/src/main/KeysTemplate.java` → `fabric/src/main/java/top/xfunny/mod/Keys.java`
   这两个输出受 `.gitignore` 保护，绝不能作为源文件编辑。
3. `forge/build.gradle` 使用 ForgeGradle + Mixin。它的 `setupFiles` 生成 `META-INF/mods.toml`，再将 Fabric 的共享源和资源复制到 Forge；`processResources` 与 `compileJava` 都依赖此任务。Forge 目录中看到的共享源码可能是过时副本，重新构建即可被覆盖。
4. 各 loader 的 `build` 完成后由 `BuildTools.copyBuildFile()` 汇总发行 JAR。不要手动把开发 JAR 复制到 `build/release` 冒充正式构建。
5. `fabric` 依赖 Fabric Loader、Fabric API、Mod Menu、MTR Fabric；`forge` 依赖 Forge、MTR Forge。MTR 版本范围由元数据限制为 `[4.0.0, 4.1)` / `>=4.0.0 <4.1`。

### 常用命令

在 Windows PowerShell 的仓库根目录执行：

```powershell
# 首次导入、切换 Minecraft 版本，或怀疑模板/Forge 镜像过期时
.\gradlew.bat setupFiles

# 构建当前默认版本（先做对应 setupFiles）
.\gradlew.bat :fabric:build :forge:build

# 构建一个指定 Minecraft 版本
.\gradlew.bat :fabric:build :forge:build -PminecraftVersion=1.20.4

# 查看可用任务
.\gradlew.bat tasks --all
```

部署自动化定义了 `deployForTesting`、`testLaunch`、`buildAllVersions`、`fullTest`。例如外部测试游戏目录只能通过显式参数指定：

```powershell
.\gradlew.bat deployForTesting -PminecraftDir=D:/Minecraft/test/.minecraft
```

`deployForTesting` 可能从 Modrinth 下载 MTR，`testLaunch` 会执行外部 PowerShell 启动脚本，`fullTest` 会部署后再启动验证。它们不是纯单元测试，不能在未确认用户的外部目录、网络下载和客户端启动意图时盲目运行。`buildAllVersions` 会为每一个版本启动子 Gradle 进程，耗时和资源开销均较高。

本仓库未发现独立的 Java 单元测试源集；质量验证以可编译性、资源/注册一致性和真实客户端/服务器冒烟验证为主。

### 终端与长运行进程约束

- 会正常退出的文件查看、Git、依赖安装和 Gradle 构建命令可前台执行，以便读取错误输出。
- 开发服务器、watch/监听进程、游戏客户端或其他不会自行退出的命令不得占用当前终端；在适用的 shell 中以后台方式启动并重定向输出，例如 Bash 使用 `nohup <command> > /dev/null 2>&1 &`。启动后应明确说明进程、日志位置和访问/连接方式。
- 不要用 `tail -f`、交互式编辑器或交互式 shell 阻塞自动化会话；需要查看日志时读取有限范围。
- 启动、部署或停止任何外部 Minecraft 实例前，先确认目标游戏目录和影响范围；不要为了验证而修改用户的默认游戏目录。

## 4. 启动路径与运行时架构

### Loader 入口

- Fabric 元数据模板在 `fabric/src/main/fabric.mod.template.json`，入口为：
  - `top.xfunny.entrypoint.YunzhuTransitExtension`：调用 `top.xfunny.mod.Init.init()`；
  - `top.xfunny.entrypoint.YunzhuTransitExtensionClient`：调用 `top.xfunny.mod.client.InitClient.init()`；
  - `top.xfunny.entrypoint.ModMenu`：Mod Menu 集成。
- Forge 元数据模板在 `forge/src/main/mods.template.toml`；其 loader 专有入口应保持 Forge API 语义，不要套用 Fabric entrypoint 写法。

### 服务端

`top.xfunny.mod.Init` 是服务端注册中枢，负责：

- 注册创造模式页、声音、方块、方块实体、物品与 MTR packet；
- `serverStarted` 时枚举所有维度，在世界存档根目录下创建 `yte/` 数据根；
- 每个 server tick 调用 `YteMain.manualTick()`；
- 约每 30 秒自动保存，玩家断开时保存，服务停止时先停止并持久化再清空内存状态；
- 持有 `Registry`、服务端实例与所有维度的 world ID。不要绕过这些生命周期直接初始化依赖服务器的状态。

`top.xfunny.core.YteMain` 为每个维度创建一个 `YteSimulator`，当前通过主线程的 `manualTick()` 运行，`threadedSimulation` 被固定为 `false`。不要擅自启用后台模拟：MTR/Minecraft 数据访问需要仔细评估线程安全。

`YteSimulator` 使用 MTR 的 `MessageQueue` 排队处理服务端操作，并在 `yte/<namespace>/<path>/lift_configs` 保存按维度的 `YteLiftConfig`。电梯运行状态由 `YteMain` 在全局 `yte/lift_states` 保存。运行模式和持久化逻辑要保持配套修改。

### 客户端

`top.xfunny.mod.client.InitClient` 负责客户端配置、临时状态清理、渲染层、方块实体渲染器、client packet 和加入服务器后的初始化。所有 `net.minecraft` UI/渲染访问、动态纹理、客户端缓存、屏幕和 renderer 都必须留在 client-only 路径，避免被专用服务器类加载。

### 网络与数据

- `top.xfunny.mod.packet` 是客户端/服务端同步边界。新 packet 必须在 `Init` 中注册，并清晰限定 C2S/S2C 方向、权限/实体有效性、目标玩家范围与线程切换。
- `YtePacketRequestResponseBase` 将请求送入 `YteMain`/`YteSimulator` 的队列；`YteOperationProcessor` 目前路由 `GET_DATA` 与 `UPDATE_DATA`。不要在网络接收线程直接改共享游戏状态。
- `top.xfunny.core.data`、`core.operation`、`core.generated.data` 组成配置序列化模型。改变字段时必须审计：默认值、序列化/反序列化、旧世界兼容、客户端同步、配置存储与 UI。
- `YteLiftConfigStore` 是给 Mixin/渲染快速查询的静态缓存。服务端与客户端各自填充；新增、更新、清理一种配置值时必须同时检查 `put`、getter、`remove`、`clear`、同步和默认/边界值。

## 5. 电梯领域约束

电梯 ID 使用 MTR `long liftId`；不要将展示编号或楼层字符串当作其替代。`LiftModeState` 是电梯运行状态的唯一入口，`LiftDoorState` 管理门控队列。修改消防、检修、急停、自动救援、专梯或司机模式时，至少检查以下不变量：

1. 锁定必须阻止运动和不允许的派梯；解除锁定不应隐式恢复危险动作。
2. 消防返回、消防员模式和自动救援在重启后有明确的恢复或静止策略；`lift_states` 的导出/恢复不能丢失安全态。
3. 模式切换需要同时考虑内呼、外呼、指令队列、门状态、到站行为及所有客户端广播。
4. 有世界/维度归属的配置必须在正确 `YteSimulator` 中修改；清理孤儿电梯时同步清理 `LiftDoorState`、`LiftModeState` 与 `YteLiftConfigStore`。
5. 为 MTR 类添加访问器/注入时，先检查现有 `MixinLiftSchema`、`MixinLiftFields` 等接口。Mixin 的目标方法/字段与注入点极易随 MTR/MC 版本变化，不能只靠静态编译判断正确性。

## 6. 注册、方块、渲染和资源

新增一个可见方块/方块实体通常是跨层改动，不是只加一个 Java 类。按需要同步检查：

1. `top.xfunny.mod.block`（优先复用 `block.base` 中的通用行为）；
2. `Blocks.java` 注册方块、标识符和方块属性；
3. `BlockEntityTypes.java`（有动态状态/自定义渲染时）和 `Items.java`/创造模式页；
4. `InitClient.java` 注册 renderer、render layer 或 client event；
5. `assets/yte/blockstates`、`models/block`、`models/item`、`textures/block|item`；
6. `assets/yte/lang/en_us.json`、`zh_cn.json`、`zh_hk.json`、`ja_jp.json` 的本地化键；
7. 有声音时 `SoundEvents.java`、`assets/yte/sounds.json` 与声音资源；
8. 若影响 MTR 行为，所需 packet、Mixin、持久化和客户端同步。

资源命名与注册 ID 保持小写 snake_case，路径应与命名空间 `yte` 精确匹配。已有大量成对的 odd/even、vertical/horizontal、带/不带屏幕的型号；扩展一个系列时遵循该系列已有命名、状态、模型和 renderer 对应关系，避免只补其中一半。

字体、纹理、图标和声音是发布资产。保留已有许可证与署名；除非已明确确认再分发权，不要替换、压缩、批量转换或从网上抓取新素材。`assets/mtr` 是对 MTR 资源的扩展/覆盖，改动时需验证与上游资源包的兼容性。

## 7. Mixin 规则

Mixin 配置为 `fabric/src/main/resources/yte.mixins.json`，并会复制到 Forge。它含 common 与 client 两组，默认 `defaultRequire` 为 1：一个目标注入未匹配会使启动失败，这属于有意的兼容性保护。

- 把无客户端依赖的注入放入 `mixins`，渲染/UI 注入放入 `client`；不要让专用服务器加载客户端 Mixin。
- 新增或重命名 Mixin 时同时更新 JSON、package、目标类、方法描述符、`@Shadow`/`@Accessor`/`@Invoker` 和 Forge 同步结果。
- **禁止在本项目的 Mixin 中使用 `@Redirect`。** 优先使用边界更清晰、兼容性更易验证的 `@Inject`、`@ModifyArg`、`@ModifyVariable`、`@ModifyExpressionValue`、Accessor 或 Invoker；若现有机制均无法满足需求，必须先取得明确授权，再讨论替代设计。
- `remap = false` 的 MTR 目标依赖其 API 名称；升级 MTR 或 MC 后，优先检查目标签名、局部变量捕获和 ordinal，而非盲目调整 `require` 为 0。
- 不要以降低 `defaultRequire`、吞掉异常或反射兜底来掩盖失效注入，除非变更需求明确且有多版本验证策略。

## 8. 代码风格与变更纪律

- 保持 Java 文件 UTF-8；沿用现有 4 空格缩进、`final` 局部变量、同包的命名和注释语言，并注意兼容 Java 8 语法。不要为了风格而大范围格式化 700+ 个 Java 文件。
- 先复用现有领域类、基类和工具；不要为单个按钮/屏幕复制一套不兼容的状态机。
- 在复杂状态转换、Mixin 注入原因、线程边界、持久化兼容处写注释；不要写陈述性噪音注释。
- 新日志使用 `Init.LOGGER` 或 `YteCoreLogger`；不要新增 `System.out.println`。现有调试输出和 TODO 是历史债务，不应被无关改动扩散。
- **非必要不得更改非项目业务代码或基础设施文件**，包括根/子项目的 `build.gradle`、`settings.gradle`、`gradle.properties`、`deploy.gradle`、Gradle wrapper、`buildSrc/`、依赖 JAR 与构建/发布脚本。只有当用户需求确实要求构建、依赖、版本、发布或跨加载器生成流程变更时，才以最小范围修改，并说明影响与验证方式。
- 若本次更新改变了项目结构、模块所有权、构建/运行方式、数据流、领域不变量、Mixin 约束、验证流程或其他本文件描述的事实，必须在同一变更中同步更新或补充 `AGENTS.md` 的相关部分；不要让代理说明滞后于代码。
- 不要提交生成的 `Keys.java`、`fabric.mod.json`、运行存档、构建输出、IDE 元数据或 Forge 的从 Fabric 复制内容。
- 工作树当前可能包含用户未提交改动或本地脚本/资源；修改前先用 `git status --short` 与 `git diff` 确认范围，绝不重置、覆盖或清理无关改动。

## 9. 验证清单

按改动风险选择验证，并在交付说明中写明实际运行的命令和未验证部分。

- **仅文档/资源描述**：检查 JSON/路径/本地化键的准确性与 `git diff`。
- **共享 Java 或构建改动**：至少运行 `:fabric:build`；若涉及共享代码、资源或 Mixin，再运行 `:forge:build`，以发现同步与 loader 差异。
- **方块/renderer/UI 改动**：进入 Fabric 与 Forge 客户端，确认注册、创造栏、模型、纹理、屏幕缩放和无客户端类加载错误。
- **网络/持久化/电梯状态改动**：用专用服务器与客户端交互测试；重连和重启后验证配置/安全态；至少覆盖正常、异常/锁定、模式退出与清理孤儿电梯场景。
- **多版本兼容改动**：用 `-PminecraftVersion=<目标版本>` 单独构建受影响版本。只有在资源和时间允许时才调用完整矩阵任务。

## 10. 已知注意点

- README 说明首次运行或切换 MC 版本要执行根任务 `setupLibrary`；若该任务在当前分支不可见，先执行 `gradlew tasks --all` 并检查分支/构建脚本，不要臆造任务。模块内 `setupFiles` 是已定义的生成步骤。
- `gradle.properties` 中的 `deploy.enableDownload` 与 `deploy.targetMtrVersion` 带 `deploy.` 前缀，而 `deploy.gradle` 也读取无前缀的项目属性作为回退/覆盖；执行部署前确认真实生效的参数。
- `build.ps1` 会并发地对多个 MC 版本执行 Gradle，可能争用缓存、消耗大量内存；它不是日常单版本开发首选。
- 现有 TODO 涉及更新检查、列表裁剪/鼠标事件、渲染声音及发布前测试代码等。除非需求相关，不要把这些注释当作本次任务范围。
- 项目采用 MIT，但部分内置字体有 CC、SIL、FontStruct、GPL 或非商业限制；发布新资产时必须保留并核实其独立许可证。

## 11. 代理工作流程

1. 先阅读本文件、相关 `build.gradle`、目标类及相邻实现；对 loader 边界和生成关系形成结论后再改。
2. 以 `fabric` 为共享实现源，最小化修改；需要 Forge 同步时通过 Gradle 生成，而不是人工双写。
3. 对跨层特性按“注册 → 数据/状态 → packet/Mixin → client renderer/UI → 资源/翻译 → 持久化/测试”逐项检查。
4. 只执行与改动成比例的验证；任何会下载、部署到真实游戏目录或启动外部客户端的步骤都要显式说明影响。
5. 判断本次更新是否改变了本文件记录的项目事实或约束；如有，先同步更新 `AGENTS.md`，再完成交付。
6. 交付时简短列出改动文件、行为变化、验证结果及已知未覆盖的 loader/版本/游戏内场景。
