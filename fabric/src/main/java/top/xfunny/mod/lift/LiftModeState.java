package top.xfunny.mod.lift;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 电梯运行时模式状态（每梯独立）：锁定、通用模式、消防迫降/消防员模式、内外呼隔离。
 * 门控状态见 {@link LiftDoorState}；本类是「电梯进入某一运行状态」的唯一入口。
 */
public final class LiftModeState {

    public enum LiftMode {
        NORMAL,
        /** 手动开门救援：低速就近平层，完全开门后自动退出。 */
        MANUAL_DOOR_RECOVERY,
        /** 消防迫降：迫降目标楼层后常驻隔离，不自动退出。 */
        FIRE_RECALL,
        /** 急停：锁定电梯，隔离内外呼，不自动退出。 */
        EMERGENCY_STOP,
        /** 占位，行为待实现。 */
        VIP,
        /** 占位，行为待实现。 */
        PARKING,
        /** 占位，行为待实现。 */
        ATTENDANT;

        /** 是否隔离外呼派梯。 */
        public boolean isolates() {
            return this == MANUAL_DOOR_RECOVERY || this == FIRE_RECALL || this == EMERGENCY_STOP;
        }

        /** 完全开门后是否自动退出该模式。 */
        public boolean shouldAutoExit() {
            return this == MANUAL_DOOR_RECOVERY;
        }
    }

    public static final class State {
        // 锁定（安全回路断开）：轿厢禁止运行。
        public boolean maintenanceLocked;
        // 上锁/进入模式后待执行的指令清空标记（下一服务端 tick 消费）。
        public boolean instructionPurgePending;

        // 通用模式
        public LiftMode mode = LiftMode.NORMAL;
        public boolean modePending;
        public long modeDelayMs;
        public boolean modeActive;
        /** -1 = 就近平层；TODO: 消防迫降按 fireFloorNumber 解析目标楼层后启用。 */
        public int modeTargetFloor = -1;

        // 消防
        public String fireFloorNumber;
        /** 消防员模式：与 FIRE_RECALL 叠加时只响应内呼；单独开启时也只响应内呼。 */
        public boolean firefighterMode;
    }

    private static final Map<Long, State> STATES = new ConcurrentHashMap<>();

    private LiftModeState() {
    }

    public static State getOrCreate(long liftId) {
        return STATES.computeIfAbsent(liftId, ignored -> new State());
    }

    public static void remove(long liftId) {
        STATES.remove(liftId);
    }

    public static void clearQueues() {
        STATES.clear();
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
    }

    public static void unlock(long liftId) {
        getOrCreate(liftId).maintenanceLocked = false;
    }

    // ------------------------------------------------------------------
    // 通用模式
    // ------------------------------------------------------------------

    public static void requestMode(long liftId, LiftMode mode, long delayMs) {
        requestMode(liftId, mode, delayMs, -1);
    }

    /**
     * 请求进入某模式：{@code delayMs} 倒计时结束后由 MixinLift 启动模式移动。
     * 已锁定或正在执行模式移动时忽略。
     */
    public static void requestMode(long liftId, LiftMode mode, long delayMs, int targetFloor) {
        final State state = getOrCreate(liftId);
        if (state.maintenanceLocked || state.modeActive) {
            return;
        }
        state.modePending = true;
        state.modeDelayMs = delayMs;
        state.modeTargetFloor = targetFloor;
        state.mode = mode;
    }

    /**
     * 结束模式执行阶段：清 pending/active 标记；仅 {@code shouldAutoExit()}
     * 的模式（救援）回到 NORMAL，其余保持（如 FIRE_RECALL 常驻隔离）。
     * 同时清理门控强关残留，保证「完全开门即恢复服务」。
     */
    public static void exitMode(long liftId) {
        final State state = getOrCreate(liftId);
        state.modePending = false;
        state.modeActive = false;
        if (state.mode.shouldAutoExit()) {
            state.mode = LiftMode.NORMAL;
            state.modeTargetFloor = -1;
        }
        final LiftDoorState.DoorQueue doorQueue = LiftDoorState.getOrCreate(liftId);
        doorQueue.forcedClosing = false;
        doorQueue.curtainSuppressed = false;
    }

    public static LiftMode getMode(long liftId) {
        return getOrCreate(liftId).mode;
    }

    // ------------------------------------------------------------------
    // 内外呼隔离
    // ------------------------------------------------------------------

    /** 外呼是否可派梯至本梯：锁定 / 模式过渡与执行 / 隔离模式 / 消防员模式 / 强关均拒绝。 */
    public static boolean canAcceptHallCall(long liftId) {
        final State state = STATES.get(liftId);
        if (state == null) {
            return true;
        }
        if (state.maintenanceLocked || state.modePending || state.modeActive) {
            return false;
        }
        if (state.firefighterMode) {
            return false;
        }
        if (state.mode != LiftMode.NORMAL && state.mode.isolates()) {
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
        if (state.firefighterMode) {
            return true;
        }
        if (state.mode != LiftMode.NORMAL && state.mode.isolates()) {
            return false;
        }
        return !LiftDoorState.getOrCreate(liftId).forcedClosing;
    }

    // ------------------------------------------------------------------
    // 消防迫降 / 消防员模式
    // ------------------------------------------------------------------

    /** 开启消防迫降并保存返回层编号；到达后保持 FIRE_RECALL 常驻隔离直至显式关闭。 */
    public static void enableFireRecall(long liftId, String fireFloorNumber) {
        final State state = getOrCreate(liftId);
        state.fireFloorNumber = fireFloorNumber;
        // ponytail: targetFloor 由 fireFloorNumber 解析的逻辑待定，先一律就近平层
        requestMode(liftId, LiftMode.FIRE_RECALL, 0, -1);
    }

    public static void disableFireRecall(long liftId) {
        final State state = getOrCreate(liftId);
        state.fireFloorNumber = null;
        if (state.mode == LiftMode.FIRE_RECALL) {
            state.mode = LiftMode.NORMAL;
            state.modeTargetFloor = -1;
        }
    }

    public static void enableFirefighterMode(long liftId) {
        getOrCreate(liftId).firefighterMode = true;
    }

    public static void disableFirefighterMode(long liftId) {
        getOrCreate(liftId).firefighterMode = false;
    }

    /** 是否处于消防迫降模式（用于门无限常开）。 */
    public static boolean isFireRecall(long liftId) {
        return getOrCreate(liftId).mode == LiftMode.FIRE_RECALL;
    }

    public static String getFireFloor(long liftId) {
        final State state = STATES.get(liftId);
        return state == null ? null : state.fireFloorNumber;
    }

    // ------------------------------------------------------------------
    // 急停
    // ------------------------------------------------------------------

    /** 急停：锁定电梯、清空指令、EMERGENCY_STOP 模式隔离内外呼。 */
    public static void enterEmergencyStop(long liftId) {
        lock(liftId);
        getOrCreate(liftId).mode = LiftMode.EMERGENCY_STOP;
    }

    public static void releaseEmergencyStop(long liftId) {
        unlock(liftId);
        getOrCreate(liftId).mode = LiftMode.NORMAL;
    }
}
