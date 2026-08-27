package top.xfunny.mod.lift;

@FunctionalInterface
public interface LiftArrivalLanternPolicy {

    LiftArrivalLanternPolicy DEFAULT = context -> {
        final LiftArrivalLanternState state = context.getArrivalState();
        if (!state.isActiveForFloor(context.getLanternFloor())) {
            return LiftArrivalLanternDecision.inactive();
        }
        return LiftArrivalLanternDecision.active(state.getDirection(),
                state.isArrived() ? LiftArrivalLanternDisplayPhase.ARRIVED
                        : LiftArrivalLanternDisplayPhase.APPROACHING,
                LiftArrivalLanternFlashPattern.STEADY, null,
                state.getTriggerSequence(), state.getTriggerStartedMillis());
    };

    LiftArrivalLanternDecision evaluate(LiftArrivalLanternContext context);
}
