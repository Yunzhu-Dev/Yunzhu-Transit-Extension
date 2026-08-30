package top.xfunny.mod.lift.policy;

import org.mtr.core.data.LiftDirection;
import top.xfunny.mod.lift.LiftArrivalLanternContext;
import top.xfunny.mod.lift.LiftArrivalLanternDecision;
import top.xfunny.mod.lift.LiftArrivalLanternDisplayPhase;
import top.xfunny.mod.lift.LiftArrivalLanternFlashPattern;
import top.xfunny.mod.lift.LiftArrivalLanternPolicy;
import top.xfunny.mod.lift.LiftArrivalLanternState;
import top.xfunny.mod.lift.LiftDisplayState;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** Mitsubishi NexWay/Ryoden non-AIL arrival-lantern behavior. */
public final class MitsubishiNexWayLanternPolicy implements LiftArrivalLanternPolicy {

    public static final MitsubishiNexWayLanternPolicy INSTANCE = new MitsubishiNexWayLanternPolicy();

    private static final LiftArrivalLanternFlashPattern SLOW_FLASH =
            LiftArrivalLanternFlashPattern.flashing(700, 400);
    private static final LiftArrivalLanternFlashPattern FAST_FLASH =
            LiftArrivalLanternFlashPattern.flashing(400, 300);

    private final Map<Long, Integer> approachFloors = new ConcurrentHashMap<>();
    private final Map<Long, Long> approachStartMillis = new ConcurrentHashMap<>();
    private final Map<Long, Long> doorStartMillis = new ConcurrentHashMap<>();

    private MitsubishiNexWayLanternPolicy() {
    }

    public void clear() {
        approachFloors.clear();
        approachStartMillis.clear();
        doorStartMillis.clear();
    }

    @Override
    public LiftArrivalLanternDecision evaluate(LiftArrivalLanternContext context) {
        final LiftDisplayState facts = context.getFacts();
        final LiftArrivalLanternState arrivalState = context.getArrivalState();
        final long liftId = facts.getLiftId();
        final int lanternFloor = context.getLanternFloor();

        // AIL is intentionally not implemented yet. The assigned lift and its
        // pre-announcement data are available through context.getGroupAssignment().

        final boolean displayedTargetAndDecelerating = facts.getTargetFloor() == lanternFloor
                && facts.getDisplayedFloor() == lanternFloor && facts.isDecelerating();
        if (displayedTargetAndDecelerating) {
            final Integer previousApproachFloor = approachFloors.put(liftId, lanternFloor);
            if (previousApproachFloor == null || previousApproachFloor != lanternFloor) {
                approachStartMillis.put(liftId, context.getCurrentMillis());
            }
            approachStartMillis.putIfAbsent(liftId, context.getCurrentMillis());
        }
        final boolean approachLatched = approachFloors.getOrDefault(liftId, -1) == lanternFloor
                && (facts.getTargetFloor() == lanternFloor || arrivalState.isActiveForFloor(lanternFloor));
        final boolean activeDoorCycleAtLantern = facts.getDoorValue() > 0
                && arrivalState.isActiveForFloor(lanternFloor);
        if (!approachLatched && !activeDoorCycleAtLantern) {
            clearFinishedCycle(facts, arrivalState, liftId);
            return LiftArrivalLanternDecision.inactive();
        }

        LiftDirection direction = activeDoorCycleAtLantern
                ? arrivalState.getDirection() : facts.getPlannedArrivalDirection();
        if (direction == LiftDirection.NONE) {
            direction = arrivalState.getDirection();
        }
        if (direction == LiftDirection.NONE) {
            direction = facts.getPlannedArrivalDirection();
        }
        if (direction == LiftDirection.NONE) {
            return LiftArrivalLanternDecision.inactive();
        }

        final String soundCue = direction == LiftDirection.UP
                ? "mitsubishi_nexway_lantern_1_up"
                : "mitsubishi_nexway_lantern_1_down";

        // Keep flashing through opening, fully open and closing; extinguish only
        // after the door has reached the fully closed value.
        if (arrivalState.isArrived() && facts.getDoorValue() <= 0) {
            return LiftArrivalLanternDecision.inactive();
        }

        if (facts.getDoorValue() > 0) {
            // isDoorOpening() remains true throughout door movement, so resetting
            // this timestamp every frame would leave the flash permanently on.
            doorStartMillis.putIfAbsent(liftId, context.getCurrentMillis());
            final long eventSequence = approachStartMillis.getOrDefault(
                    liftId, arrivalState.getTriggerSequence());
            return LiftArrivalLanternDecision.active(direction,
                    LiftArrivalLanternDisplayPhase.ARRIVED, FAST_FLASH, soundCue,
                    eventSequence,
                    doorStartMillis.getOrDefault(liftId, context.getCurrentMillis()));
        }

        if (approachLatched) {
            return LiftArrivalLanternDecision.active(direction,
                    LiftArrivalLanternDisplayPhase.APPROACHING, SLOW_FLASH, soundCue,
                    approachStartMillis.getOrDefault(liftId, context.getCurrentMillis()),
                    approachStartMillis.getOrDefault(liftId, context.getCurrentMillis()));
        }

        return LiftArrivalLanternDecision.inactive();
    }

    private void clearFinishedCycle(LiftDisplayState facts, LiftArrivalLanternState arrivalState, long liftId) {
        final Integer approachFloor = approachFloors.get(liftId);
        if (!facts.isDoorCycle() && (approachFloor == null
                || !arrivalState.isActiveForFloor(approachFloor) && facts.getTargetFloor() != approachFloor)) {
            approachFloors.remove(liftId);
            approachStartMillis.remove(liftId);
            doorStartMillis.remove(liftId);
        }
    }
}
