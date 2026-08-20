package top.xfunny.mod.lift;

import org.mtr.core.data.LiftDirection;

@FunctionalInterface
public interface DisplayDirectionPolicy {
    LiftDirection getDirection(LiftDisplayState state);
}
