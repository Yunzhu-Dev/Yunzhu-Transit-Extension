package top.xfunny.mod.lift;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class LiftFloorCancelState {

    private static final Map<Long, Integer> PENDING_CANCELLATIONS = new ConcurrentHashMap<>();

    private LiftFloorCancelState() {
    }

    public static void request(long liftId, int floorIndex) {
        PENDING_CANCELLATIONS.put(liftId, floorIndex);
    }

    public static Integer consume(long liftId) {
        return PENDING_CANCELLATIONS.remove(liftId);
    }
}
