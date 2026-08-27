package top.xfunny.mod.lift;

import org.mtr.core.data.LiftDirection;

/** Immutable input supplied to a brand-specific arrival-lantern policy. */
public final class LiftArrivalLanternContext {

    private static final double SPEED_EPSILON = 0.000000001;

    private final LiftDisplayState facts;
    private final LiftArrivalLanternState arrivalState;
    private final LiftArrivalLanternTriggerMode configuredTriggerMode;
    private final int lanternFloor;
    private final int floorDistance;
    private final LiftDirection registeredCallDirection;
    private final long callRegisteredMillis;
    private final long currentMillis;
    private final LiftArrivalLanternGroupAssignment groupAssignment;

    public LiftArrivalLanternContext(LiftDisplayState facts, LiftArrivalLanternState arrivalState,
            LiftArrivalLanternTriggerMode configuredTriggerMode, int lanternFloor, int floorDistance,
            LiftDirection registeredCallDirection, long callRegisteredMillis, long currentMillis,
            LiftArrivalLanternGroupAssignment groupAssignment) {
        this.facts = facts;
        this.arrivalState = arrivalState;
        this.configuredTriggerMode = configuredTriggerMode;
        this.lanternFloor = lanternFloor;
        this.floorDistance = floorDistance;
        this.registeredCallDirection = registeredCallDirection;
        this.callRegisteredMillis = callRegisteredMillis;
        this.currentMillis = currentMillis;
        this.groupAssignment = groupAssignment == null
                ? LiftArrivalLanternGroupAssignment.NONE : groupAssignment;
    }

    public LiftDisplayState getFacts() { return facts; }
    public LiftArrivalLanternState getArrivalState() { return arrivalState; }
    public LiftArrivalLanternTriggerMode getConfiguredTriggerMode() { return configuredTriggerMode; }
    public int getLanternFloor() { return lanternFloor; }
    public int getFloorDistance() { return floorDistance; }
    public LiftDirection getRegisteredCallDirection() { return registeredCallDirection; }
    public long getCallRegisteredMillis() { return callRegisteredMillis; }
    public long getCurrentMillis() { return currentMillis; }
    public LiftArrivalLanternGroupAssignment getGroupAssignment() { return groupAssignment; }

    public long getMillisSinceCallRegistration() {
        return callRegisteredMillis <= 0 ? Long.MAX_VALUE : Math.max(currentMillis - callRegisteredMillis, 0);
    }

    /** Estimate based on remaining rail distance and current absolute speed. */
    public long estimateMillisToTarget() {
        final double absoluteSpeed = Math.abs(facts.getSpeed());
        return absoluteSpeed <= SPEED_EPSILON || !Double.isFinite(facts.getDistanceToTarget())
                ? Long.MAX_VALUE : Math.max(Math.round(facts.getDistanceToTarget() / absoluteSpeed), 0);
    }
}
