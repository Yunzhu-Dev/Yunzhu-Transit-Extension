package top.xfunny.mod.lift;

import org.mtr.core.data.LiftDirection;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Brand-neutral facts about a lift which display policies can interpret.
 * This class deliberately contains no brand-specific arrow rules.
 */
public final class LiftDisplayState {

    private static final Map<Long, LiftDisplayState> STATES = new ConcurrentHashMap<>();

    private final long liftId;
    private LiftDirection movementDirection = LiftDirection.NONE;
    private LiftDirection targetDirection = LiftDirection.NONE;
    private boolean moving;
    private boolean decelerating;
    private boolean levelling;
    private boolean doorCycle;
    private boolean idle = true;
    private int displayedFloor = -1;
    private int targetFloor = -1;
    private double speed;
    private double distanceToTarget = Double.POSITIVE_INFINITY;
    private long stoppingCoolDown;
    private double previousAbsoluteSpeed;

    private LiftDisplayState(long liftId) {
        this.liftId = liftId;
    }

    public static LiftDisplayState get(long liftId) {
        return STATES.computeIfAbsent(liftId, LiftDisplayState::new);
    }

    public void update(LiftDirection movementDirection, LiftDirection targetDirection,
            boolean moving, boolean levelling, boolean doorCycle, boolean idle,
            int displayedFloor, int targetFloor, double speed,
            double distanceToTarget, long stoppingCoolDown) {
        final double absoluteSpeed = Math.abs(speed);
        decelerating = moving && previousAbsoluteSpeed > 0 && absoluteSpeed + 0.000000001 < previousAbsoluteSpeed;
        previousAbsoluteSpeed = absoluteSpeed;
        this.movementDirection = movementDirection;
        this.targetDirection = targetDirection;
        this.moving = moving;
        this.levelling = levelling;
        this.doorCycle = doorCycle;
        this.idle = idle;
        this.displayedFloor = displayedFloor;
        this.targetFloor = targetFloor;
        this.speed = speed;
        this.distanceToTarget = distanceToTarget;
        this.stoppingCoolDown = stoppingCoolDown;
    }

    public long getLiftId() { return liftId; }
    public LiftDirection getMovementDirection() { return movementDirection; }
    public LiftDirection getTargetDirection() { return targetDirection; }
    public boolean isMoving() { return moving; }
    public boolean isDecelerating() { return decelerating; }
    public boolean isLevelling() { return levelling; }
    public boolean isDoorCycle() { return doorCycle; }
    public boolean isIdle() { return idle; }
    public int getDisplayedFloor() { return displayedFloor; }
    public int getTargetFloor() { return targetFloor; }
    public double getSpeed() { return speed; }
    public double getDistanceToTarget() { return distanceToTarget; }
    public long getStoppingCoolDown() { return stoppingCoolDown; }

    /** The existing arrival/door-cycle latch, retained as the default policy output. */
    public LiftDirection getLatchedDirection() {
        return LiftDisplayDirectionState.get(liftId).direction;
    }

    /** Direction waiting to take over after arrival or the current door cycle. */
    public LiftDirection getPendingDirection() {
        final LiftDisplayDirectionState state = LiftDisplayDirectionState.get(liftId);
        if (state.deferredSameFloorCallDirection != LiftDirection.NONE) {
            return state.deferredSameFloorCallDirection;
        }
        return state.arrivalDirection;
    }
}
