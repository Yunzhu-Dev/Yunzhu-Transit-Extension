package top.xfunny.mod.lift;

public enum LiftServiceMode {
    NORMAL("gui.yte.lift_service_mode_normal"),
    INDEPENDENT("gui.yte.lift_service_mode_independent"),
    DRIVER("gui.yte.lift_service_mode_driver");

    private final String translationKey;

    LiftServiceMode(String translationKey) {
        this.translationKey = translationKey;
    }

    public String getTranslationKey() {
        return translationKey;
    }

    public boolean acceptsHallCalls() {
        return this == NORMAL;
    }

    public boolean hidesHallDisplay() {
        return this == DRIVER;
    }

    public LiftServiceMode next() {
        final LiftServiceMode[] values = values();
        return values[(ordinal() + 1) % values.length];
    }

    public static LiftServiceMode fromSerializedName(String value) {
        try {
            return value == null ? NORMAL : valueOf(value);
        } catch (IllegalArgumentException ignored) {
            return NORMAL;
        }
    }
}
