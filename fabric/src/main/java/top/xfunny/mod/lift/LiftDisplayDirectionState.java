package top.xfunny.mod.lift;

import org.mtr.core.data.LiftDirection;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class LiftDisplayDirectionState {

    private static final Map<Long, LiftDisplayDirectionState> STATES = new ConcurrentHashMap<>();
    private static final Map<Long, PendingSameFloorCall> PENDING_SAME_FLOOR_CALLS = new ConcurrentHashMap<>();
    private static final long PENDING_SAME_FLOOR_CALL_TIMEOUT = 10000;

    public LiftDirection direction = LiftDirection.NONE;
    public int arrivalFloor = -1;
    public LiftDirection arrivalDirection = LiftDirection.NONE;
    public long arrivalMillis;
    public int previousInstructionCount;
    public LiftDirection sameFloorCallDirection = LiftDirection.NONE;
    public LiftDirection deferredSameFloorCallDirection = LiftDirection.NONE;
    public boolean sameFloorCallDoorCycleStarted;
    public long sameFloorCallWaitMillis;
    public boolean movedSinceIdle;

    private LiftDisplayDirectionState() {
    }

    public static LiftDisplayDirectionState get(long liftId) {
        return STATES.computeIfAbsent(liftId, ignored -> new LiftDisplayDirectionState());
    }

    public static synchronized void registerPendingSameFloorCall(Set<Long> candidateLiftIds, LiftDirection direction) {
        if (candidateLiftIds.isEmpty() || direction == LiftDirection.NONE) {
            return;
        }
        final PendingSameFloorCall pendingCall = new PendingSameFloorCall(
                candidateLiftIds, direction, System.currentTimeMillis() + PENDING_SAME_FLOOR_CALL_TIMEOUT);
        candidateLiftIds.forEach(liftId -> PENDING_SAME_FLOOR_CALLS.put(liftId, pendingCall));
    }

    public static synchronized LiftDirection claimPendingSameFloorCall(long liftId) {
        final PendingSameFloorCall pendingCall = PENDING_SAME_FLOOR_CALLS.remove(liftId);
        if (pendingCall == null) {
            return LiftDirection.NONE;
        }
        pendingCall.candidateLiftIds.forEach(candidateLiftId ->
                PENDING_SAME_FLOOR_CALLS.remove(candidateLiftId, pendingCall));
        return System.currentTimeMillis() <= pendingCall.expiresAt
                ? pendingCall.direction
                : LiftDirection.NONE;
    }

    public void setSameFloorCallDirection(LiftDirection direction) {
        this.direction = direction;
        sameFloorCallDirection = direction;
        sameFloorCallDoorCycleStarted = false;
        sameFloorCallWaitMillis = 0;
        arrivalFloor = -1;
        arrivalDirection = LiftDirection.NONE;
        arrivalMillis = 0;
    }

    public void deferSameFloorCallDirection(LiftDirection direction) {
        deferredSameFloorCallDirection = direction;
    }

    public void resetForIdleDoorCycle() {
        // A same-floor hall call has an explicit requested direction and must
        // keep it. Manual door opening and a car call for the current floor do not.
        if (sameFloorCallDirection != LiftDirection.NONE) {
            return;
        }
        direction = LiftDirection.NONE;
        arrivalFloor = -1;
        arrivalDirection = LiftDirection.NONE;
        arrivalMillis = 0;
        deferredSameFloorCallDirection = LiftDirection.NONE;
        movedSinceIdle = false;
    }

    public void resetForCarSameFloorOpen() {
        direction = LiftDirection.NONE;
        arrivalFloor = -1;
        arrivalDirection = LiftDirection.NONE;
        arrivalMillis = 0;
        sameFloorCallDirection = LiftDirection.NONE;
        deferredSameFloorCallDirection = LiftDirection.NONE;
        sameFloorCallDoorCycleStarted = false;
        sameFloorCallWaitMillis = 0;
        movedSinceIdle = false;
    }

    private static final class PendingSameFloorCall {
        private final Set<Long> candidateLiftIds;
        private final LiftDirection direction;
        private final long expiresAt;

        private PendingSameFloorCall(Set<Long> candidateLiftIds, LiftDirection direction, long expiresAt) {
            this.candidateLiftIds = new HashSet<>(candidateLiftIds);
            this.direction = direction;
            this.expiresAt = expiresAt;
        }
    }
}
