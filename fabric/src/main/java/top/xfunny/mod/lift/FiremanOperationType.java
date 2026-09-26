package top.xfunny.mod.lift;

public enum FiremanOperationType {
    HOLD_DOOR_BUTTON("gui.yte.lift_fireman_operation_hold_door"),
    HOLD_FLOOR_BUTTON("gui.yte.lift_fireman_operation_hold_floor"),
    REGISTER_TO_CLOSE("gui.yte.lift_fireman_operation_register");

    private final String translationKey;

    FiremanOperationType(String translationKey) {
        this.translationKey = translationKey;
    }

    public String getTranslationKey() {
        return translationKey;
    }

    public FiremanOperationType next() {
        return values()[(ordinal() + 1) % values().length];
    }

    public static FiremanOperationType fromSerializedName(String value) {
        try {
            return value == null ? HOLD_DOOR_BUTTON : valueOf(value);
        } catch (IllegalArgumentException ignored) {
            return HOLD_DOOR_BUTTON;
        }
    }
}
