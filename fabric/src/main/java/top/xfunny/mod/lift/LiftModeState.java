package top.xfunny.mod.lift;

import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArrayList;
import top.xfunny.core.data.YteLiftState;
import top.xfunny.mod.config.YteLiftConfigStore;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 电梯运行时模式状态（每梯独立）：锁定、通用模式、消防迫降/消防员模式、内外呼隔离。
 * 门控状态见 {@link LiftDoorState}；本类是「电梯进入某一运行状态」的唯一入口。
 * 状态由 YteMain 的 lift_states 文件持久化（服务器重启不丢）。
 */
public final class LiftModeState {

    public enum LiftMode {
        /** 服务模式 */
        NORMAL,             // 正常服务
        VIP,                // 专梯直驶
        PARKING,            // 驻停
        INDEPENDENT,        // 专用模式（不响应外呼）
        ATTENDANT,          // 司机模式（不响应外呼）

        /** 保护模式 */
        FIRE_MODE,          // 消防模式
        MAINTENANCE,        // 检修模式
        EMERGENCY_STOP,     // 急停

        /** 特殊模式 */
        AUTO_RECOVERY;      // 自动救援

        /** 是否隔离外呼派梯。 */
        public boolean isolates() {
            return this == AUTO_RECOVERY || this == FIRE_MODE || this == EMERGENCY_STOP || this == MAINTENANCE;
        }

        /** 是否接受外呼派梯：隔离模式与专用/司机模式拒绝，内呼不受限。 */
        public boolean acceptsHallCalls() {
            return !isolates() && this != INDEPENDENT && this != ATTENDANT;
        }
        /** 完全开门后是否自动退出该模式。 */
        public boolean shouldAutoExit() {
            return this == AUTO_RECOVERY;
        }
    }

    public enum FireMode {
        FIRE_RECALL,        // 消防迫降/返回
        FIREMAN_MODE,       // 消防员模式
    }

    public static final class State {
        /** 锁定（安全回路断开）：轿厢禁止运行，两端同步冻结速度。 */
        public boolean maintenanceLocked;
        /** 上锁/进入模式后待执行的指令清空标记（下一服务端 tick 消费）。 */
        public boolean instructionPurgePending;

        // 通用模式
        public LiftMode mode = LiftMode.NORMAL;             // 当前运行模式
        public boolean modePending;                         // 待进入模式
        public long modeDelayMs;                            // 进入模式倒计时
        public boolean modeActive;                          // 模式运行中

        // 消防
        public FireMode fireMode;
        /** 消防返回层编号 */
        public String fireFloorNumber;
        /** 取消迫降待定：保持 FIRE_MODE 隔离，到达返回层后才恢复 */
        public boolean fireCancelPending;
        /** 通用模式回退：进入消防模式前的模式，退出后恢复（浏览器式回退，非消防专用） */
        public LiftMode returnMode = LiftMode.NORMAL;
    }

    private static final Map<Long, State> STATES = new ConcurrentHashMap<>();

    /** 状态变更待持久化标记，由 YteMain.manualTick 消费后写盘。 */
    private static volatile boolean stateDirty;

    private LiftModeState() {
    }

    public static State getOrCreate(long liftId) {
        return STATES.computeIfAbsent(liftId, ignored -> new State());
    }

    public static void remove(long liftId) {
        STATES.remove(liftId);
        markStateDirty();
    }

    public static void clearQueues() {
        STATES.clear();
        markStateDirty();
    }

    /** 标记状态已变更，等待服务端 tick 刷盘。 */
    public static void markStateDirty() {
        stateDirty = true;
    }

    /** 取出并清除待持久化标记。 */
    public static boolean consumeStateDirty() {
        final boolean dirty = stateDirty;
        stateDirty = false;
        return dirty;
    }

    /** 导出需要持久化的状态（非锁定且非隔离/保护模式不落盘——服务模式由配置文件持久化）。 */
    public static ObjectArrayList<YteLiftState> exportStates() {
        final ObjectArrayList<YteLiftState> exported = new ObjectArrayList<>();
        STATES.forEach((liftId, state) -> {
            if (!state.maintenanceLocked && !state.mode.isolates()) {
                return;
            }
            exported.add(new YteLiftState(liftId, state.mode.name(), state.maintenanceLocked,
                    state.fireFloorNumber == null ? "" : state.fireFloorNumber,
                    state.fireCancelPending, state.fireMode == FireMode.FIREMAN_MODE,
                    state.returnMode.name()));
        });
        return exported;
    }

    /** 从持久化记录恢复状态；需要续跑的模式（消防迫降、自动救援）置 modePending 以便首 tick 续跑。 */
    public static void restoreStates(Collection<YteLiftState> storedStates) {
        if (storedStates == null) {
            return;
        }
        storedStates.forEach(stored -> {
            final State state = getOrCreate(stored.getId());
            state.mode = parseMode(stored.getMode());
            state.maintenanceLocked = stored.isMaintenanceLocked();
            state.fireCancelPending = stored.isFireCancelPending();
            state.fireFloorNumber = stored.getFireFloorNumber() == null ? "" : stored.getFireFloorNumber();
            state.fireMode = state.mode == LiftMode.FIRE_MODE
                    ? (stored.isFireman() ? FireMode.FIREMAN_MODE : FireMode.FIRE_RECALL)
                    : null;
            state.returnMode = parseMode(stored.getReturnMode());
            // 静止安全态（急停/检修/联锁）不续跑；消防迫降与自动救援续跑
            state.modePending = state.mode == LiftMode.FIRE_MODE || state.mode == LiftMode.AUTO_RECOVERY;
            state.modeDelayMs = 0;
        });
        markStateDirty();
    }

    /** 模式名解析：未知/空按普通服务处理。供状态恢复与配置路由共用。 */
    public static LiftMode parseMode(String modeName) {
        if (modeName != null && !modeName.isEmpty()) {
            try {
                return LiftMode.valueOf(modeName);
            } catch (IllegalArgumentException ignored) {
                // 未知模式按普通服务处理
            }
        }
        return LiftMode.NORMAL;
    }

    // ------------------------------------------------------------------
    // 锁定
    // ------------------------------------------------------------------

    /**
     * 上锁（安全回路断开）：急停 + 弃全部内外呼 + 进入隔离；
     * 解除由 {@link #unlock} 完成，救援等后续动作由调用方 {@link #requestMode} 发起。
     */
    public static void lock(long liftId) {
        final State state = getOrCreate(liftId);
        state.maintenanceLocked = true;
        state.modePending = false;
        state.modeActive = false;
        state.instructionPurgePending = true;
        // 安全回路断开：消防整体一并失效，避免残留（如 FIREMAN_MODE 泄漏到派梯判定）
        state.fireMode = null;
        state.fireFloorNumber = null;
        state.fireCancelPending = false;
        markStateDirty();
    }

    public static void unlock(long liftId) {
        getOrCreate(liftId).maintenanceLocked = false;
        markStateDirty();
    }

    public static boolean isMaintenanceLocked(long liftId) {
        final State state = STATES.get(liftId);
        return state != null && state.maintenanceLocked;
    }

    // ------------------------------------------------------------------
    // 通用模式
    // ------------------------------------------------------------------

    public static void requestMode(long liftId, LiftMode mode, long delayMs) {
        final State state = getOrCreate(liftId);
        if (state.maintenanceLocked || (state.modeActive && mode != LiftMode.FIRE_MODE)) {
            return;
        }
        state.modePending = true;
        state.modeDelayMs = delayMs;
        state.mode = mode;
        markStateDirty();
    }

    /**
     * 结束模式执行阶段：清 pending/active 标记；仅 {@code shouldAutoExit()}
     * 的模式（救援）回到 NORMAL，其余保持（如 FIRE_MODE 常驻隔离）。
     * 同时清理门控强关残留，保证「完全开门即恢复服务」。
     */
    public static void exitMode(long liftId) {
        final State state = getOrCreate(liftId);
        state.modePending = false;
        state.modeActive = false;
        if (state.mode.shouldAutoExit()) {
            state.mode = LiftMode.NORMAL;
        }
        final LiftDoorState.DoorQueue doorQueue = LiftDoorState.getOrCreate(liftId);
        doorQueue.forcedClosing = false;
        doorQueue.curtainSuppressed = false;
        markStateDirty();
    }

    public static LiftMode getMode(long liftId) {
        return getOrCreate(liftId).mode;
    }

    /**
     * 服务模式即时切换（专用/司机 ↔ 正常）：无倒计时、无移动阶段，直接生效。
     * 保护态（隔离/消防/模式执行中/待启动）不覆盖；配置同步反复调用时相等即短路。
     */
    public static void setServiceMode(long liftId, LiftMode mode) {
        final State state = getOrCreate(liftId);
        if (state.mode.isolates() || state.modeActive || state.modePending || state.mode == mode) {
            return;
        }
        state.mode = mode;
        markStateDirty();
    }

    // ------------------------------------------------------------------
    // 内外呼隔离
    // ------------------------------------------------------------------

    /** 外呼是否可派梯至本梯：锁定 / 模式过渡与执行 / 隔离或专用司机模式 / 消防员模式 / 强关均拒绝。 */
    public static boolean canAcceptHallCall(long liftId) {
        final State state = STATES.get(liftId);
        if (state == null) {
            return true;
        }
        if (state.maintenanceLocked || state.modePending || state.modeActive) {
            return false;
        }
        if (!state.mode.acceptsHallCalls()) {
            return false;
        }
        return !LiftDoorState.getOrCreate(liftId).forcedClosing;
    }

    /** 内呼是否可登记：消防员模式下放行（含叠加迫降）；急停/迫降未开消防员/锁定/救援均拒绝。 */
    public static boolean canAcceptCarCall(long liftId) {
        final State state = STATES.get(liftId);
        if (state == null) {
            return true;
        }
        if (state.maintenanceLocked || state.modePending || state.modeActive) {
            return false;
        }
        if (state.mode == LiftMode.EMERGENCY_STOP) {
            return false;
        }
        if (state.fireMode == FireMode.FIREMAN_MODE) {
            return true;
        }
        if (state.mode != LiftMode.NORMAL && state.mode.isolates()) {
            return false;
        }
        return !LiftDoorState.getOrCreate(liftId).forcedClosing;
    }

    // ------------------------------------------------------------------
    // 消防模式： 消防迫降 / 消防员模式
    // ------------------------------------------------------------------

    /** 开启消防迫降；返回层取电梯配置的首层。联锁（安全回路断开）时直接急停。 */
    public static void activateFireMode(long liftId) {
        final State state = getOrCreate(liftId);
        if (state.maintenanceLocked) {
            // 安全回路断开：不迫降，直接急停（lock 内已清空消防字段）
            triggerEmergencyStop(liftId);
            return;
        }
        if (state.mode != LiftMode.FIRE_MODE) {
            state.returnMode = state.mode;
        }
        state.fireCancelPending = false;
        state.fireFloorNumber = YteLiftConfigStore.getFireRecallFloor(liftId);
        state.fireMode = FireMode.FIRE_RECALL;
        requestMode(liftId, LiftMode.FIRE_MODE, 0);
        markStateDirty();
    }

    public static void exitFireMode(long liftId) {
        final State state = getOrCreate(liftId);
        if (state.mode != LiftMode.FIRE_MODE) {
            return;
        }
        state.fireCancelPending = true;
        markStateDirty();
    }

    /** 是否处于消防模式 */
    public static boolean isFireMode(long liftId) {
        return getOrCreate(liftId).mode == LiftMode.FIRE_MODE;
    }

    /** 获取消防模式具体状态 */
    public static FireMode getFireMode(long liftId) {
        final State state = STATES.get(liftId);
        return state != null && state.mode == LiftMode.FIRE_MODE ? state.fireMode : null;
    }

    public static String getFireFloor(long liftId) {
        final State state = STATES.get(liftId);
        return state == null ? null : state.fireFloorNumber;
    }

    /**
     * 消防迫降到达返回层（消防指令完成）：具备消防员资质 → 内部转移 FIRE_RECALL → FIREMAN_MODE。
     * 返回 true 表示发生了转换（调用方需广播）。由 MixinLift 到站分支调用。
     */
    public static boolean onFireModeArrival(long liftId) {
        final State state = STATES.get(liftId);
        if (state == null || state.mode != LiftMode.FIRE_MODE) return false;
        if (state.fireMode == FireMode.FIRE_RECALL && YteLiftConfigStore.isFiremanLift(liftId)) {
            state.fireMode = FireMode.FIREMAN_MODE;
            markStateDirty();
            return true;
        }
        return false;
    }

    /**
     * 消防取消待定：电梯停稳于楼层（迫降完成）后恢复进入前的模式。
     * 途中（modeActive）与待启动（modePending）时不恢复——保持隔离不响应内外呼。
     * 返回 true 通知调用方广播 + 恢复正常门周期。
     */
    public static boolean tickFireCancelExit(long liftId, boolean atFloor) {
        final State state = STATES.get(liftId);
        if (state == null || state.mode != LiftMode.FIRE_MODE || !state.fireCancelPending) return false;
        if (state.modePending || state.modeActive || !atFloor) return false;
        state.fireCancelPending = false;
        state.mode = state.returnMode == null ? LiftMode.NORMAL : state.returnMode;
        state.returnMode = LiftMode.NORMAL;
        state.fireMode = null;
        state.fireFloorNumber = null;
        if (state.mode == LiftMode.AUTO_RECOVERY) {
            state.modePending = true;
            state.modeDelayMs = 0;
        }
        markStateDirty();
        return true;
    }


    // ------------------------------------------------------------------
    // 急停
    // ------------------------------------------------------------------

    /** 急停：锁定电梯、清空指令、EMERGENCY_STOP 模式隔离内外呼。 */
    public static void triggerEmergencyStop(long liftId) {
        lock(liftId);
        getOrCreate(liftId).mode = LiftMode.EMERGENCY_STOP;
        markStateDirty();
    }

    public static void resetEmergencyStop(long liftId) {
        unlock(liftId);
        getOrCreate(liftId).mode = LiftMode.NORMAL;
        markStateDirty();
    }
}
