package top.xfunny.mod.lift;

import org.mtr.core.data.LiftDirection;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Client-side, per-lift arrival-lantern latch. It separates the short trigger
 * edge (deceleration or door opening) from the full approach/door cycle so
 * renderers do not depend on lift instructions that are removed at arrival.
 */
public final class LiftArrivalLanternState {

    private static final double DOOR_OPEN_EPSILON = 0.000001;
    private static final Map<Long, LiftArrivalLanternState> STATES = new ConcurrentHashMap<>();

    private final long liftId;
    private int armedFloor = -1;
    private int triggeredFloor = -1;
    private LiftDirection armedDirection = LiftDirection.NONE;
    private LiftDirection direction = LiftDirection.NONE;
    private boolean active;
    private boolean arrived;
    private boolean doorOpened;
    private long triggerSequence;
    private long triggerStartedMillis;
    private LiftArrivalLanternSignal triggerSignal = LiftArrivalLanternSignal.NONE;

    private LiftArrivalLanternState(long liftId) {
        this.liftId = liftId;
    }

    public static LiftArrivalLanternState get(long liftId) {
        return STATES.computeIfAbsent(liftId, LiftArrivalLanternState::new);
    }

    public static void clear() {
        STATES.clear();
    }

    public void update(LiftDisplayState facts, LiftArrivalLanternTriggerMode triggerMode) {
        final boolean doorOpen = facts.getDoorValue() > DOOR_OPEN_EPSILON;
        final LiftDisplayDirectionState directionState = LiftDisplayDirectionState.get(liftId);

        if (active) {
            if (doorOpen) {
                arrived = true;
                doorOpened = true;
            }
            // The announced direction is allowed to settle during approach, but
            // once door movement starts it must remain latched for the complete
            // open/close cycle. Later calls belong to the following journey.
            if (!doorOpened) {
                final LiftDirection resolvedDirection = resolveDirection(facts, directionState, triggeredFloor);
                if (resolvedDirection != LiftDirection.NONE) {
                    direction = resolvedDirection;
                }
            }

            final boolean doorCycleFinished = doorOpened && !doorOpen && !facts.isDoorCycle();
            final boolean targetCancelledBeforeArrival = !doorOpened && !facts.isDoorCycle()
                    && facts.getTargetFloor() != triggeredFloor;
            if (doorCycleFinished || targetCancelledBeforeArrival) {
                resetLatch();
            }
        }

        if (!active) {
            updateArmedTarget(facts, directionState, doorOpen);

            final LiftDirection sameFloorDirection = resolveSameFloorDirection(directionState);
            final boolean sameFloorDoorTrigger = doorOpen && facts.getExactFloor() >= 0
                    && sameFloorDirection != LiftDirection.NONE;
            final boolean decelerationTrigger = triggerMode == LiftArrivalLanternTriggerMode.DECELERATION
                    && armedFloor >= 0 && facts.getTargetFloor() == armedFloor
                    && facts.isMoving() && facts.isDecelerating();
            final boolean retainedArrivalAtExactFloor = facts.getExactFloor() >= 0
                    && directionState.arrivalFloor == facts.getExactFloor();
            final boolean doorTrigger = triggerMode == LiftArrivalLanternTriggerMode.DOOR_OPEN
                    && doorOpen && (armedFloor >= 0 || retainedArrivalAtExactFloor);

            if (decelerationTrigger || sameFloorDoorTrigger || doorTrigger) {
                final int triggerFloor = sameFloorDoorTrigger || retainedArrivalAtExactFloor
                        ? facts.getExactFloor() : armedFloor;
                final LiftDirection triggerDirection = resolveDirection(facts, directionState, triggerFloor);
                if (triggerFloor >= 0 && triggerDirection != LiftDirection.NONE) {
                    active = true;
                    arrived = doorOpen;
                    doorOpened = doorOpen;
                    triggeredFloor = triggerFloor;
                    direction = triggerDirection;
                    triggerSequence++;
                    triggerStartedMillis = System.currentTimeMillis();
                    triggerSignal = decelerationTrigger
                            ? LiftArrivalLanternSignal.DECELERATION_STARTED
                            : LiftArrivalLanternSignal.DOOR_OPENING_STARTED;
                }
            }
        }

    }

    private void updateArmedTarget(LiftDisplayState facts, LiftDisplayDirectionState directionState, boolean doorOpen) {
        final int targetFloor = facts.getTargetFloor();
        if (targetFloor >= 0) {
            if (armedFloor < 0 || targetFloor == armedFloor || !facts.isDoorCycle()) {
                armedFloor = targetFloor;
                final LiftDirection resolvedDirection = resolveDirection(facts, directionState, armedFloor);
                if (resolvedDirection != LiftDirection.NONE) {
                    armedDirection = resolvedDirection;
                }
            }
        } else if (!facts.isDoorCycle() && !doorOpen) {
            armedFloor = -1;
            armedDirection = LiftDirection.NONE;
        }
    }

    private LiftDirection resolveDirection(LiftDisplayState facts,
            LiftDisplayDirectionState directionState, int floor) {
        if (floor >= 0 && facts.getExactFloor() == floor) {
            final LiftDirection sameFloorDirection = resolveSameFloorDirection(directionState);
            if (sameFloorDirection != LiftDirection.NONE) {
                return sameFloorDirection;
            }
        }
        if (facts.getPlannedArrivalDirection() != LiftDirection.NONE) {
            return facts.getPlannedArrivalDirection();
        }
        if (directionState.arrivalFloor == floor && directionState.arrivalDirection != LiftDirection.NONE) {
            return directionState.arrivalDirection;
        }
        if (facts.getLatchedDirection() != LiftDirection.NONE) {
            return facts.getLatchedDirection();
        }
        if (facts.getMovementDirection() != LiftDirection.NONE) {
            return facts.getMovementDirection();
        }
        if (facts.getTargetDirection() != LiftDirection.NONE) {
            return facts.getTargetDirection();
        }
        return armedDirection;
    }

    private static LiftDirection resolveSameFloorDirection(LiftDisplayDirectionState directionState) {
        if (directionState.sameFloorCallDirection != LiftDirection.NONE) {
            return directionState.sameFloorCallDirection;
        }
        return directionState.deferredSameFloorCallDirection;
    }

    private void resetLatch() {
        active = false;
        arrived = false;
        doorOpened = false;
        triggeredFloor = -1;
        direction = LiftDirection.NONE;
        armedFloor = -1;
        armedDirection = LiftDirection.NONE;
        triggerSignal = LiftArrivalLanternSignal.NONE;
    }

    public boolean isActiveForFloor(int floor) {
        return active && triggeredFloor == floor;
    }

    public boolean isArrived() {
        return arrived;
    }

    public LiftDirection getDirection() {
        return direction;
    }

    public long getTriggerSequence() {
        return triggerSequence;
    }

    public long getTriggerStartedMillis() {
        return triggerStartedMillis;
    }

    public LiftArrivalLanternSignal getTriggerSignal() {
        return triggerSignal;
    }
}
