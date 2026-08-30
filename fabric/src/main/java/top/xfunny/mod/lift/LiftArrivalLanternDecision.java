package top.xfunny.mod.lift;

import org.mtr.core.data.LiftDirection;

/** Policy output consumed by the common lantern renderer adapter. */
public final class LiftArrivalLanternDecision {

    private static final LiftArrivalLanternDecision INACTIVE = new LiftArrivalLanternDecision(
            false, LiftDirection.NONE, LiftArrivalLanternDisplayPhase.IDLE,
            LiftArrivalLanternFlashPattern.OFF, null, 0, 0);

    private final boolean active;
    private final LiftDirection direction;
    private final LiftArrivalLanternDisplayPhase phase;
    private final LiftArrivalLanternFlashPattern flashPattern;
    private final String soundCue;
    private final long eventSequence;
    private final long phaseStartMillis;

    private LiftArrivalLanternDecision(boolean active, LiftDirection direction,
            LiftArrivalLanternDisplayPhase phase, LiftArrivalLanternFlashPattern flashPattern,
            String soundCue, long eventSequence, long phaseStartMillis) {
        this.active = active;
        this.direction = direction;
        this.phase = phase;
        this.flashPattern = flashPattern;
        this.soundCue = soundCue;
        this.eventSequence = eventSequence;
        this.phaseStartMillis = phaseStartMillis;
    }

    public static LiftArrivalLanternDecision inactive() {
        return INACTIVE;
    }

    public static LiftArrivalLanternDecision active(LiftDirection direction,
            LiftArrivalLanternDisplayPhase phase, LiftArrivalLanternFlashPattern flashPattern,
            String soundCue, long eventSequence, long phaseStartMillis) {
        return direction == null || direction == LiftDirection.NONE ? INACTIVE
                : new LiftArrivalLanternDecision(true, direction, phase,
                flashPattern == null ? LiftArrivalLanternFlashPattern.STEADY : flashPattern,
                soundCue, eventSequence, phaseStartMillis);
    }

    public boolean isActive() { return active; }
    public LiftDirection getDirection() { return direction; }
    public LiftArrivalLanternDisplayPhase getPhase() { return phase; }
    public LiftArrivalLanternFlashPattern getFlashPattern() { return flashPattern; }
    public String getSoundCue() { return soundCue; }
    public long getEventSequence() { return eventSequence; }
    public long getPhaseStartMillis() { return phaseStartMillis; }
}
