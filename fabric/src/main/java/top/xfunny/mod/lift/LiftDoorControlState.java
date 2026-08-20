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
    }

    private static final Map<Long, DoorQueue> QUEUES = new ConcurrentHashMap<>();

    private LiftDoorControlState() {
    }

    public static DoorQueue getOrCreate(long liftId) {
        return QUEUES.computeIfAbsent(liftId, ignored -> new DoorQueue());
    }

    public static void remove(long liftId) {
        QUEUES.remove(liftId);
    }

    public static void clearQueues() {
        QUEUES.clear();
    }

    public static void request(long liftId, Command command) {
        getOrCreate(liftId).pendingCommand = command;
    }
}
