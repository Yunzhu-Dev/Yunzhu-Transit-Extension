package top.xfunny.mod.lift;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class LiftDoorControlState {

    public enum Command {
        OPEN,
        CLOSE,
        HOLD_OPEN,
        /** 内部消息：电梯出发（开始移动）时由门控发出，置 runIssued。不进网络包。 */
        RUN
    }

    /** Close timer sentinel: the door stays open indefinitely (fire/special mode). */
    public static final long INFINITE_OPEN = -1;

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
        /** RUN issued (door fully closed event + a target) → all further commands are ignored until the next stop. */
        public boolean runIssued;
        /** 联锁：某层门被三角钥匙手动打开 → 轿厢禁止运行，直到层门关闭。 */
        public boolean maintenanceLocked;
        /** 开门动画期间按下 CLOSE：门全开瞬间立即关门，跳过保持时长。 */
        public boolean pendingClose;
    }

    private static final Map<Long, DoorQueue> QUEUES = new ConcurrentHashMap<>();
    private static final Map<Long, DoorSmoother> SMOOTHERS = new ConcurrentHashMap<>();
    /** 临界阻尼二阶平滑的自然频率（rad/s）：反转过渡约 5/ω ≈ 170ms。 */
    private static final double SMOOTH_OMEGA = 30;

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
     * 速度连续过零，反转呈 S 形减速→加速过渡；对匀速段目标的跟踪滞后约 2/ω。
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
        if (target <= 0 && smoother.value < 0.02) {
            smoother.value = 0;
            smoother.velocity = 0;
        } else {
            smoother.value += smoother.velocity * dt;
            smoother.velocity += (SMOOTH_OMEGA * SMOOTH_OMEGA * (target - smoother.value)
                    - 2 * SMOOTH_OMEGA * smoother.velocity) * dt;
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

        private DoorSmoother(double value, long lastNanos) {
            this.value = value;
            this.lastNanos = lastNanos;
        }
    }
}
