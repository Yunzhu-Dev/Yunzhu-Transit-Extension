package top.xfunny.mod.lift;

import org.mtr.core.data.LiftDirection;

public interface LiftDisplayDirection {

    void yte$resetArrivalDirectionDelay();

    LiftDisplayState yte$getDisplayState();

    default LiftDirection yte$getDisplayDirection(DisplayDirectionPolicy policy) {
        return policy.getDirection(yte$getDisplayState());
    }

    default LiftDirection yte$getDisplayDirection(DisplayDirectionMode mode) {
        return yte$getDisplayDirection((DisplayDirectionPolicy) mode);
    }
}
