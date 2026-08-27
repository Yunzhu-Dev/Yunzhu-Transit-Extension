package top.xfunny.mod.lift;

/** A reusable light pattern; durations are milliseconds. */
public final class LiftArrivalLanternFlashPattern {

    public static final LiftArrivalLanternFlashPattern OFF = new LiftArrivalLanternFlashPattern(0, Long.MAX_VALUE);
    public static final LiftArrivalLanternFlashPattern STEADY = new LiftArrivalLanternFlashPattern(Long.MAX_VALUE, 0);

    private final long onMillis;
    private final long offMillis;

    private LiftArrivalLanternFlashPattern(long onMillis, long offMillis) {
        this.onMillis = Math.max(onMillis, 0);
        this.offMillis = Math.max(offMillis, 0);
    }

    public static LiftArrivalLanternFlashPattern flashing(long onMillis, long offMillis) {
        return onMillis <= 0 ? OFF : offMillis <= 0 ? STEADY
                : new LiftArrivalLanternFlashPattern(onMillis, offMillis);
    }

    public boolean isLit(long currentMillis, long phaseStartMillis) {
        if (this == OFF || onMillis == 0) {
            return false;
        }
        if (this == STEADY || offMillis == 0 || onMillis == Long.MAX_VALUE) {
            return true;
        }
        final long cycleMillis = onMillis + offMillis;
        final long elapsedMillis = Math.max(currentMillis - phaseStartMillis, 0);
        return elapsedMillis % cycleMillis < onMillis;
    }

    public long getOnMillis() {
        return onMillis;
    }

    public long getOffMillis() {
        return offMillis;
    }
}
