package top.xfunny.mod.lift;

import org.mtr.core.data.LiftDirection;

/** Built-in policies. Brand renderers may also provide their own policy. */
public enum DisplayDirectionMode implements DisplayDirectionPolicy {
    /** Current YTE behaviour: retain the arrival direction through the door cycle. */
    LATCH_UNTIL_DOOR_CLOSE {
        @Override
        public LiftDirection getDirection(LiftDisplayState state) {
            return state.getLatchedDirection();
        }
    },
    /** Simple MTR-style indication: show physical movement only. */
    MTR_DEFAULT {
        @Override
        public LiftDirection getDirection(LiftDisplayState state) {
            return state.isMoving() ? state.getMovementDirection() : LiftDirection.NONE;
        }
    },
    /** Extinguish as soon as the car starts decelerating. */
    OFF_WHEN_BRAKING {
        @Override
        public LiftDirection getDirection(LiftDisplayState state) {
            return state.isMoving() && !state.isDecelerating()
                    ? state.getMovementDirection()
                    : LiftDirection.NONE;
        }
    }
}
