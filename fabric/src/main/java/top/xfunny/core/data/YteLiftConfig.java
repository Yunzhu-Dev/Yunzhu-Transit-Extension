package top.xfunny.core.data;

import org.mtr.core.serializer.ReaderBase;
import top.xfunny.core.generated.data.YteLiftConfigSchema;
import top.xfunny.mod.lift.LiftDoorButtonLightMode;
import top.xfunny.mod.lift.LiftFloorCancelMode;
import top.xfunny.mod.lift.LiftMotionProfile;

public class YteLiftConfig extends YteLiftConfigSchema {

    public YteLiftConfig(long liftId) {
        super(liftId, DEFAULT_SPEED, DEFAULT_ACCELERATION, DEFAULT_ADO_DISTANCE, DEFAULT_LEVELLING_DISTANCE, DEFAULT_LEVELLING_SPEED);
    }

    public YteLiftConfig(long liftId, double speed, double acceleration, double adoDistance, double levellingDistance, double levellingSpeed) {
        super(liftId, speed, acceleration, adoDistance, levellingDistance, levellingSpeed);
    }

    public YteLiftConfig(long liftId, double upSpeed, double downSpeed, double upAcceleration, double downAcceleration,
            boolean directionParametersLinked, double adoDistance, double levellingDistance, double levellingSpeed) {
        super(liftId, upSpeed, downSpeed, upAcceleration, downAcceleration, directionParametersLinked,
                adoDistance, levellingDistance, levellingSpeed);
    }

    public YteLiftConfig(long liftId, double upSpeed, double downSpeed, double upAcceleration, double downAcceleration,
            boolean directionParametersLinked, double adoDistance, double levellingDistance, double levellingSpeed,
            LiftMotionProfile motionProfile) {
        this(liftId, upSpeed, downSpeed, upAcceleration, downAcceleration, directionParametersLinked,
                adoDistance, levellingDistance, levellingSpeed, motionProfile, false);
    }

    public YteLiftConfig(long liftId, double upSpeed, double downSpeed, double upAcceleration, double downAcceleration,
            boolean directionParametersLinked, double adoDistance, double levellingDistance, double levellingSpeed,
            LiftMotionProfile motionProfile, boolean doorHoldEnabled) {
        this(liftId, upSpeed, downSpeed, upAcceleration, downAcceleration, directionParametersLinked,
                adoDistance, levellingDistance, levellingSpeed, motionProfile, doorHoldEnabled,
                LiftDoorButtonLightMode.MOMENTARY, LiftFloorCancelMode.DOUBLE_CLICK, false);
    }

    public YteLiftConfig(long liftId, double upSpeed, double downSpeed, double upAcceleration, double downAcceleration,
            boolean directionParametersLinked, double adoDistance, double levellingDistance, double levellingSpeed,
            LiftMotionProfile motionProfile, boolean doorHoldEnabled, LiftDoorButtonLightMode doorButtonLightMode,
            LiftFloorCancelMode floorCancelMode, boolean floorCancelWhileMoving) {
        super(liftId, upSpeed, downSpeed, upAcceleration, downAcceleration, directionParametersLinked,
                adoDistance, levellingDistance, levellingSpeed, motionProfile.name(), doorHoldEnabled,
                doorButtonLightMode.name(), floorCancelMode.name(), floorCancelWhileMoving);
    }

    public YteLiftConfig(ReaderBase readerBase) {
        super(readerBase);
    }

    public double getSpeed() {
        return speed;
    }

    public double getAcceleration() {
        return acceleration;
    }

    public double getUpSpeed() { return speed; }

    public double getDownSpeed() { return directionParametersLinked ? speed : downSpeed; }

    public double getUpAcceleration() { return acceleration; }

    public double getDownAcceleration() { return directionParametersLinked ? acceleration : downAcceleration; }

    public boolean areDirectionParametersLinked() { return directionParametersLinked; }

    public LiftMotionProfile getMotionProfile() { return LiftMotionProfile.fromSerializedName(motionProfile); }

    public double getAdoDistance() { return adoDistance; }

    public double getLevellingDistance() { return levellingDistance; }

    public double getLevellingSpeed() { return levellingSpeed; }

    public boolean isDoorHoldEnabled() { return doorHoldEnabled; }

    public LiftDoorButtonLightMode getDoorButtonLightMode() {
        return LiftDoorButtonLightMode.fromSerializedName(doorButtonLightMode);
    }

    public LiftFloorCancelMode getFloorCancelMode() {
        return LiftFloorCancelMode.fromSerializedName(floorCancelMode);
    }

    public boolean isFloorCancelWhileMovingAllowed() { return floorCancelWhileMoving; }

    public void setSpeed(double speed) {
        this.speed = clamp(speed, MIN_SPEED, MAX_SPEED);
    }

    public void setAcceleration(double acceleration) {
        this.acceleration = clamp(acceleration, MIN_ACCELERATION, MAX_ACCELERATION);
    }

    public void setAdoDistance(double adoDistance) { this.adoDistance = clamp(adoDistance, MIN_ADO_DISTANCE, MAX_ADO_DISTANCE); }

    public void setLevellingDistance(double levellingDistance) { this.levellingDistance = clamp(levellingDistance, MIN_LEVELLING_DISTANCE, MAX_LEVELLING_DISTANCE); }

    public void setLevellingSpeed(double levellingSpeed) { this.levellingSpeed = clamp(levellingSpeed, MIN_LEVELLING_SPEED, MAX_LEVELLING_SPEED); }

    public void setDoorHoldEnabled(boolean doorHoldEnabled) { this.doorHoldEnabled = doorHoldEnabled; }

    public void setDoorButtonLightMode(LiftDoorButtonLightMode doorButtonLightMode) {
        this.doorButtonLightMode = doorButtonLightMode.name();
    }

    public void setFloorCancelMode(LiftFloorCancelMode floorCancelMode) {
        this.floorCancelMode = floorCancelMode.name();
    }

    public void setFloorCancelWhileMovingAllowed(boolean floorCancelWhileMoving) {
        this.floorCancelWhileMoving = floorCancelWhileMoving;
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
