package top.xfunny.mod.lift;

import org.mtr.core.tool.Utilities;

/**
 * Per-lift acceleration and levelling strategies. Door opening remains outside
 * this class so ADO can be enabled or disabled independently of the motion
 * profile.
 */
public enum LiftMotionProfile {
    STANDARD("gui.yte.lift_motion_profile_standard") {
        @Override
        public MotionResult calculate(MotionContext context) {
            final boolean useLevelling = context.levellingDistance > 0 && context.levellingSpeed > 0;
            final double distanceToBrakingTarget = useLevelling
                    ? Math.max(context.distanceToTarget - context.levellingDistance, 0)
                    : context.distanceToTarget;
            final double brakingTargetSpeed = useLevelling ? context.levellingSpeed : 0;
            double speed = calculateApproachSpeed(context, distanceToBrakingTarget, brakingTargetSpeed,
                    useLevelling && context.absoluteSpeed > context.levellingSpeed, context.acceleration);

            if (speed != 0 && useLevelling && context.distanceToTarget <= context.levellingDistance) {
                final double levellingDeceleration = context.levellingSpeed * context.levellingSpeed
                        / (2 * context.levellingDistance);
                final double levellingTargetSpeed = Math.sqrt(2 * levellingDeceleration * context.distanceToTarget);
                final double speedDirection = speed == 0 ? context.direction : Math.signum(speed);
                speed = Math.min(Math.abs(speed), levellingTargetSpeed) * speedDirection;
            }
            return new MotionResult(speed, false);
        }
    },
    TWO_STAGE("gui.yte.lift_motion_profile_two_stage") {
        @Override
        public MotionResult calculate(MotionContext context) {
            final boolean useLevelling = context.levellingDistance > 0 && context.levellingSpeed > 0;
            if (!useLevelling) {
                return STANDARD.calculate(context);
            }

            if (!context.fineLevelling) {
                final double distanceToCoarseStop = Math.max(context.distanceToTarget - context.levellingDistance, 0);
                final double speedChange = context.acceleration * context.millisElapsed;
                // Calculate the maximum speed for this tick that can still reach
                // 0.01 m/s at the coarse-stop point under the configured
                // deceleration. This follows the braking curve continuously and
                // avoids both a one-tick hard cap and a long 0.01 m/s crawl.
                final double brakingCurveSpeed = Math.max(TWO_STAGE_COARSE_STOP_SPEED,
                        -speedChange + Math.sqrt(speedChange * speedChange
                                + TWO_STAGE_COARSE_STOP_SPEED * TWO_STAGE_COARSE_STOP_SPEED
                                + 2 * context.acceleration * distanceToCoarseStop));
                final double absoluteSpeed = Math.min(
                        Math.min(context.absoluteSpeed + speedChange, context.maximumSpeed), brakingCurveSpeed);
                final double speed = absoluteSpeed * context.direction;
                final boolean atCoarseStopSpeed = Math.abs(speed) <= TWO_STAGE_COARSE_STOP_SPEED + 1E-12;
                final boolean atCoarseStopPoint = distanceToCoarseStop <= Math.abs(speed) * context.millisElapsed;
                final boolean enterFineLevelling = atCoarseStopSpeed && atCoarseStopPoint;
                return new MotionResult(speed, enterFineLevelling);
            }

            final double levellingDeceleration = context.levellingSpeed * context.levellingSpeed
                    / (2 * context.levellingDistance);
            final double distanceLimitedSpeed = Math.sqrt(2 * levellingDeceleration * context.distanceToTarget);
            final double targetSpeed = Math.min(context.levellingSpeed, distanceLimitedSpeed);
            // Normal lift acceleration can cover the small 0.01 -> levelling-speed
            // difference in only a few ticks. Limit only the fine-levelling ramp so
            // the second stage is visible, while retaining the existing final
            // deceleration and its anti-snap behaviour.
            final double fineAcceleration = Math.min(context.acceleration,
                    Math.max(context.levellingSpeed - TWO_STAGE_COARSE_STOP_SPEED, 0)
                            / TWO_STAGE_FINE_ACCELERATION_TIME);
            final double accelerationSpeedChange = fineAcceleration * context.millisElapsed;
            final double decelerationSpeedChange = context.acceleration * context.millisElapsed;
            final double adjustedSpeed = context.absoluteSpeed < targetSpeed
                    ? Math.min(context.absoluteSpeed + accelerationSpeedChange, targetSpeed)
                    : Math.max(context.absoluteSpeed - decelerationSpeedChange, targetSpeed);
            return new MotionResult(adjustedSpeed * context.direction, false);
        }
    };

    /** Internal units are blocks per millisecond. */
    public static final double TWO_STAGE_COARSE_STOP_SPEED = 0.01 / 1000.0;
    private static final double TWO_STAGE_FINE_ACCELERATION_TIME = 500;

    private final String translationKey;

    LiftMotionProfile(String translationKey) {
        this.translationKey = translationKey;
    }

    public String getTranslationKey() {
        return translationKey;
    }

    public LiftMotionProfile next() {
        final LiftMotionProfile[] values = values();
        return values[(ordinal() + 1) % values.length];
    }

    public static LiftMotionProfile fromSerializedName(String name) {
        try {
            return valueOf(name);
        } catch (Exception ignored) {
            return STANDARD;
        }
    }

    public abstract MotionResult calculate(MotionContext context);

    private static double calculateApproachSpeed(MotionContext context, double distanceToBrakingTarget,
            double brakingTargetSpeed, boolean forceBoundarySpeed, double minimumBrakingSpeed) {
        final double requiredBrakingDistance = Math.max(
                (context.absoluteSpeed * context.absoluteSpeed - brakingTargetSpeed * brakingTargetSpeed)
                        / (2 * context.acceleration), 0);

        if (forceBoundarySpeed && context.movementThisTick >= distanceToBrakingTarget) {
            return brakingTargetSpeed * context.motionDirection;
        } else if (context.absoluteSpeed > brakingTargetSpeed
                && requiredBrakingDistance + context.movementThisTick > distanceToBrakingTarget) {
            return Math.max(context.absoluteSpeed - context.acceleration * context.millisElapsed,
                    minimumBrakingSpeed) * context.motionDirection;
        } else {
            return Utilities.clamp(context.signedSpeed + context.acceleration * context.millisElapsed * context.direction,
                    -context.maximumSpeed, context.maximumSpeed);
        }
    }

    public static final class MotionContext {
        private final double signedSpeed;
        private final double absoluteSpeed;
        private final double maximumSpeed;
        private final double acceleration;
        private final double distanceToTarget;
        private final double levellingDistance;
        private final double levellingSpeed;
        private final double movementThisTick;
        private final double direction;
        private final double motionDirection;
        private final long millisElapsed;
        private final boolean fineLevelling;

        public MotionContext(double signedSpeed, double maximumSpeed, double acceleration, double distanceToTarget,
                double levellingDistance, double levellingSpeed, double direction, long millisElapsed,
                boolean fineLevelling) {
            this.signedSpeed = signedSpeed;
            this.absoluteSpeed = Math.abs(signedSpeed);
            this.maximumSpeed = maximumSpeed;
            this.acceleration = acceleration;
            this.distanceToTarget = distanceToTarget;
            this.levellingDistance = levellingDistance;
            this.levellingSpeed = levellingSpeed;
            this.movementThisTick = absoluteSpeed * millisElapsed;
            this.direction = direction;
            this.motionDirection = signedSpeed == 0 ? direction : Math.signum(signedSpeed);
            this.millisElapsed = millisElapsed;
            this.fineLevelling = fineLevelling;
        }
    }

    public static final class MotionResult {
        public final double speed;
        public final boolean enterFineLevelling;

        private MotionResult(double speed, boolean enterFineLevelling) {
            this.speed = speed;
            this.enterFineLevelling = enterFineLevelling;
        }
    }
}
