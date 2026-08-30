package top.xfunny.mod.lift;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class LiftFloorCancelState {

    private static final long REQUEST_TIMEOUT = 2000;
    private static final Map<Long, PendingCancellation> PENDING_CANCELLATIONS = new ConcurrentHashMap<>();

    private LiftFloorCancelState() {
    }

    public static void request(long liftId, int floorIndex) {
        PENDING_CANCELLATIONS.put(liftId,
                new PendingCancellation(floorIndex, System.currentTimeMillis() + REQUEST_TIMEOUT));
    }

    public static Integer peek(long liftId) {
        final PendingCancellation pendingCancellation = PENDING_CANCELLATIONS.get(liftId);
        if (pendingCancellation == null) {
            return null;
        }
        if (System.currentTimeMillis() > pendingCancellation.expiresAt) {
            PENDING_CANCELLATIONS.remove(liftId, pendingCancellation);
            return null;
        }
        return pendingCancellation.floorIndex;
    }

    public static void complete(long liftId, int floorIndex) {
        PENDING_CANCELLATIONS.computeIfPresent(liftId,
                (ignoredLiftId, pendingCancellation) -> pendingCancellation.floorIndex == floorIndex
                        ? null : pendingCancellation);
    }

    private static final class PendingCancellation {
        private final int floorIndex;
        private final long expiresAt;

        private PendingCancellation(int floorIndex, long expiresAt) {
            this.floorIndex = floorIndex;
            this.expiresAt = expiresAt;
        }
    }
}
