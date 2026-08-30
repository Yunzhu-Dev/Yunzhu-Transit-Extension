package top.xfunny.mod.lift;

public enum LiftArrivalLanternTriggerMode {
    DECELERATION("gui.yte.lift_arrival_lantern_trigger_deceleration"),
    DOOR_OPEN("gui.yte.lift_arrival_lantern_trigger_door_open");

    private final String translationKey;

    LiftArrivalLanternTriggerMode(String translationKey) {
        this.translationKey = translationKey;
    }

    public String getTranslationKey() {
        return translationKey;
    }

    public LiftArrivalLanternTriggerMode next() {
        final LiftArrivalLanternTriggerMode[] values = values();
        return values[(ordinal() + 1) % values.length];
    }

    public static LiftArrivalLanternTriggerMode fromSerializedName(String value) {
        try {
            return value == null ? DECELERATION : valueOf(value);
        } catch (IllegalArgumentException ignored) {
            return DECELERATION;
        }
    }
}
