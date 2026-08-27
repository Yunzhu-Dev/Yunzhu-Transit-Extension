package top.xfunny.mod.lift;

@FunctionalInterface
public interface LiftArrivalLanternAssignmentProvider {

    LiftArrivalLanternAssignmentProvider NONE = (liftId, floor) -> LiftArrivalLanternGroupAssignment.NONE;

    LiftArrivalLanternGroupAssignment getAssignment(long liftId, int floor);
}
