package top.xfunny.mod.lift;

public enum LiftDoorButtonLightMode {
    MOMENTARY("gui.yte.lift_door_button_light_momentary", true),
    TIMED("gui.yte.lift_door_button_light_timed", true),
    AUTOMATIC("gui.yte.lift_door_button_light_automatic", false);

    private final String translationKey;
    private final boolean selectable;

    LiftDoorButtonLightMode(String translationKey, boolean selectable) {
        this.translationKey = translationKey;
        this.selectable = selectable;
    }

    public String getTranslationKey() {
        return translationKey;
    }

    public LiftDoorButtonLightMode nextSelectable() {
        LiftDoorButtonLightMode nextMode = this;
        do {
            nextMode = values()[(nextMode.ordinal() + 1) % values().length];
        } while (!nextMode.selectable);
        return nextMode;
    }

    public boolean isLit(boolean pressed, boolean timedLightActive, boolean automaticLightActive) {
        switch (this) {
            case MOMENTARY:
                return pressed;
            case TIMED:
                return pressed || timedLightActive;
            case AUTOMATIC:
                return automaticLightActive;
            default:
                return false;
        }
    }

    public static LiftDoorButtonLightMode fromSerializedName(String value) {
        try {
            return value == null ? MOMENTARY : valueOf(value);
        } catch (IllegalArgumentException ignored) {
            return MOMENTARY;
        }
    }
}
