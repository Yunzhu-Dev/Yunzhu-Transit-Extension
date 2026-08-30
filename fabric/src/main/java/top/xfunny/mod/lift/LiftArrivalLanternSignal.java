package top.xfunny.mod.lift;

/** Extensible event vocabulary for brand-specific arrival-lantern policies. */
public enum LiftArrivalLanternSignal {
    NONE,
    CALL_REGISTERED,
    APPROACH_STARTED,
    DECELERATION_STARTED,
    DOOR_OPENING_STARTED,
    DOOR_CLOSING_STARTED,
    DOOR_CLOSED
}
