package top.xfunny.mod.lift;

import top.xfunny.mod.config.YteLiftConfigStore;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class LiftDoorControlState {

    public enum Command {
        OPEN,
        CLOSE,
        HOLD_OPEN
    }

    /** Close timer sentinel: the door stays open indefinitely (fire/special mode). */
    public static final long INFINITE_OPEN = -1;

    /** 层门/轿厢门渲染最大开度 = 满程的 75%（模型几何限制）。 */
    public static final double DOOR_MAX_OPEN_SCALE = 0.75;

    /**
     * Door position state, updated by the door control once per tick. The
     * transitions into {@link #FULLY_OPEN} and {@link #CLOSED} are the
     * "door fully opened" and "door fully closed" events: the former starts
     * the close timer, the latter gates the RUN decision.
     */
    public enum DoorState {
        CLOSED,
        OPENING,
        FULLY_OPEN,
        CLOSING
    }

    /** 由 coolDown 推导门相位，供门值计算、状态机、命令处理三处共用，避免区间判定漂移。 */
    public static DoorState getDoorState(long coolDown, YteLiftConfigStore.DoorParams p) {
        if (coolDown >= p.total() || coolDown <= p.runDelay) {
            return DoorState.CLOSED;
        } else if (coolDown > p.fullOpenCoolDown()) {
            return DoorState.OPENING;
        } else if (coolDown > p.closeStartCoolDown()) {
            return DoorState.FULLY_OPEN;
        }
        return DoorState.CLOSING;
    }

    /**
     * 故障隔离：联锁 / 待救援 / 救援中 任一为真即视为故障，从外呼派梯中分离
     * （{@code MixinLift} 的 pressButton 注入据此拒绝调度）。未来新增故障源时并入此链。
     */
    public static boolean isIsolated(long liftId) {
        final DoorQueue queue = QUEUES.get(liftId);
        return queue != null && (queue.maintenanceLocked
                || queue.maintenanceRecoveryPending || queue.recovering);
    }

    /**
     * Per-lift door message queue. Server-authoritative, processed by
     * {@code MixinLift.tick}; each lift has its own queue so lifts never
     * interfere with one another.
     */
    public static final class DoorQueue {
        /** Latest incoming command, consumed once per tick (last one wins). */
        public Command pendingCommand;
        /** Milliseconds until the automatic close fires. {@link #INFINITE_OPEN} disables it. */
        public long closeRemainingMs;
        /** Current door state; transitions into FULLY_OPEN / CLOSED are the two door events. */
        public DoorState doorState = DoorState.CLOSED;
        /** 联锁：某层门被三角钥匙手动打开 → 轿厢禁止运行，直到层门关闭。 */
        public boolean maintenanceLocked;
        /** 上锁/解锁后待执行的指令清空（下一服务端 tick 生效，按钮灯随之熄灭）。 */
        public boolean instructionPurgePending;
        /** 解锁后等待层门关闭动画播完（closeMs），再进入就近平层救援。 */
        public boolean maintenanceRecoveryPending;
        public long recoveryCloseDelayMs;
        /** 救援模式：低速就近平层，门循环完成后解除。 */
        public boolean recovering;
    }

    private static final Map<Long, DoorQueue> QUEUES = new ConcurrentHashMap<>();
    private static final Map<Long, DoorSmoother> SMOOTHERS = new ConcurrentHashMap<>();
    /** 临界阻尼二阶平滑的自然频率（rad/s）：反转过渡约 5/ω，30→167ms，45→111ms。 */
    private static final double SMOOTH_OMEGA = 45;
    /** 换向期低ω：门先缓缓停住再反向，模拟真实电梯电机换向；速度过零后恢复正常跟踪。 */
    private static final double REVERSAL_OMEGA = 14;
    /** 换向软化最短保持时长：保证「顿一下」可被感知。 */
    private static final long REVERSAL_MIN_HOLD_NANOS = 150_000_000L;
    /** ω 过渡时间常数（秒）：软化↔跟踪之间一阶惯性趋近，避免回复力阶跃造成抽动。 */
    private static final double OMEGA_TAU = 0.08;

    private LiftDoorControlState() {
    }

    public static DoorQueue getOrCreate(long liftId) {
        return QUEUES.computeIfAbsent(liftId, ignored -> new DoorQueue());
    }

    public static void remove(long liftId) {
        QUEUES.remove(liftId);
        SMOOTHERS.remove(liftId);
    }

    public static void clearQueues() {
        QUEUES.clear();
        SMOOTHERS.clear();
    }

    public static void request(long liftId, Command command) {
        getOrCreate(liftId).pendingCommand = command;
    }

    /**
     * 客户端门值临界阻尼二阶平滑：目标突变（关门↔开门反转、同步跳变）时
     * 速度连续过零；检测到换向时切换低 ω，让门先缓停再反向（真实电梯电机
     * 换向质感），平时以高 ω 跟踪保证起步跟手。
     * 目标为 0 且足够近时直接归零，保证碰撞箱及时出现。
     */
    public static float smoothDoorValue(long liftId, float target) {
        final long now = System.nanoTime();
        DoorSmoother smoother = SMOOTHERS.get(liftId);
        if (smoother == null) {
            smoother = new DoorSmoother(target, now);
            SMOOTHERS.put(liftId, smoother);
            return target;
        }
        final double dt = Math.min(Math.max((now - smoother.lastNanos) / 1E9, 0), 0.1);
        smoother.lastNanos = now;

        // 换向检测：平滑器速度方向与目标运动方向相反 → 进入软化期（最少保持
        // REVERSAL_MIN_HOLD_NANOS），门先缓停再反向加速
        if ((smoother.velocity < 0 && target > smoother.lastTarget)
                || (smoother.velocity > 0 && target < smoother.lastTarget)) {
            smoother.reversalUntilNanos = now + REVERSAL_MIN_HOLD_NANOS;
        }
        smoother.lastTarget = target;

        final double goalOmega = now < smoother.reversalUntilNanos ? REVERSAL_OMEGA : SMOOTH_OMEGA;
        // ω 一阶惯性趋近：软化进入/退出均连续过渡，回复力无阶跃，续开中段不再被“拽”一把
        smoother.currentOmega += (goalOmega - smoother.currentOmega) * Math.min(dt / OMEGA_TAU, 1);
        final double omega = smoother.currentOmega;

        if (target <= 0 && smoother.value < 0.02) {
            smoother.value = 0;
            smoother.velocity = 0;
        } else {
            smoother.value += smoother.velocity * dt;
            smoother.velocity += (omega * omega * (target - smoother.value)
                    - 2 * omega * smoother.velocity) * dt;
            if (smoother.value < 0) {
                smoother.value = 0;
                smoother.velocity = Math.max(0, smoother.velocity);
            } else if (smoother.value > 1) {
                smoother.value = 1;
                smoother.velocity = Math.min(0, smoother.velocity);
            }
        }
        return (float) smoother.value;
    }

    private static final class DoorSmoother {
        double value;
        double velocity;
        long lastNanos;
        float lastTarget;
        long reversalUntilNanos;
        double currentOmega = SMOOTH_OMEGA;

        private DoorSmoother(double value, long lastNanos) {
            this.value = value;
            this.lastNanos = lastNanos;
            this.lastTarget = (float) value;
        }
    }
}
