package top.xfunny.mod.lift;

public enum LiftFloorCancelMode {
    DOUBLE_CLICK("gui.yte.lift_floor_cancel_double_click"),
    LONG_PRESS("gui.yte.lift_floor_cancel_long_press");

    private final String translationKey;

    LiftFloorCancelMode(String translationKey) {
        this.translationKey = translationKey;
    }

    public String getTranslationKey() {
        return translationKey;
    }

    public LiftFloorCancelMode next() {
        final LiftFloorCancelMode[] values = values();
        return values[(ordinal() + 1) % values.length];
    }

    public static LiftFloorCancelMode fromSerializedName(String value) {
        try {
            return value == null ? DOUBLE_CLICK : valueOf(value);
        } catch (IllegalArgumentException ignored) {
            return DOUBLE_CLICK;
        }
    }
}
