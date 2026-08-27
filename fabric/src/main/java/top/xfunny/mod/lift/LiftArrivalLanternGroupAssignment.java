package top.xfunny.mod.lift;

import org.mtr.core.data.LiftDirection;

/**
 * Reserved AIL/group-control assignment input. A future dispatcher can publish
 * the selected lift, direction, assignment time and pre-announcement cue.
 */
public final class LiftArrivalLanternGroupAssignment {

    public static final LiftArrivalLanternGroupAssignment NONE = new LiftArrivalLanternGroupAssignment(
            false, 0, -1, LiftDirection.NONE, 0, null);

    private final boolean assigned;
    private final long assignedLiftId;
    private final int floor;
    private final LiftDirection direction;
    private final long assignedMillis;
    private final String preAnnouncementCue;

    public LiftArrivalLanternGroupAssignment(boolean assigned, long assignedLiftId, int floor,
            LiftDirection direction, long assignedMillis, String preAnnouncementCue) {
        this.assigned = assigned;
        this.assignedLiftId = assignedLiftId;
        this.floor = floor;
        this.direction = direction == null ? LiftDirection.NONE : direction;
        this.assignedMillis = assignedMillis;
        this.preAnnouncementCue = preAnnouncementCue;
    }

    public boolean isAssigned() { return assigned; }
    public long getAssignedLiftId() { return assignedLiftId; }
    public int getFloor() { return floor; }
    public LiftDirection getDirection() { return direction; }
    public long getAssignedMillis() { return assignedMillis; }
    public String getPreAnnouncementCue() { return preAnnouncementCue; }

    public boolean isAssignedTo(long liftId, int lanternFloor) {
        return assigned && assignedLiftId == liftId && floor == lanternFloor;
    }
}
