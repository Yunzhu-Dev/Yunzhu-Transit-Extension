package top.xfunny.core.data;

import org.mtr.core.serializer.ReaderBase;
import top.xfunny.core.generated.data.YteLiftConfigSchema;
import top.xfunny.mod.lift.DoorMotionCurve;
import top.xfunny.mod.lift.LiftArrivalLanternTriggerMode;
import top.xfunny.mod.lift.LiftDoorButtonLightMode;
import top.xfunny.mod.lift.LiftFloorCancelMode;
import top.xfunny.mod.lift.LiftMotionProfile;
import top.xfunny.mod.lift.LiftServiceMode;

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
                LiftDoorButtonLightMode.MOMENTARY, LiftFloorCancelMode.DOUBLE_CLICK, false,
                LiftArrivalLanternTriggerMode.DECELERATION, "");
    }

    public YteLiftConfig(long liftId, double upSpeed, double downSpeed, double upAcceleration, double downAcceleration,
            boolean directionParametersLinked, double adoDistance, double levellingDistance, double levellingSpeed,
            LiftMotionProfile motionProfile, boolean doorHoldEnabled, LiftDoorButtonLightMode doorButtonLightMode,
            LiftFloorCancelMode floorCancelMode, boolean floorCancelWhileMoving, String liftNumber) {
        this(liftId, upSpeed, downSpeed, upAcceleration, downAcceleration, directionParametersLinked,
                adoDistance, levellingDistance, levellingSpeed, motionProfile, doorHoldEnabled,
                doorButtonLightMode, floorCancelMode, floorCancelWhileMoving,
                LiftArrivalLanternTriggerMode.DECELERATION, liftNumber);
    }

    public YteLiftConfig(long liftId, double upSpeed, double downSpeed, double upAcceleration, double downAcceleration,
            boolean directionParametersLinked, double adoDistance, double levellingDistance, double levellingSpeed,
            LiftMotionProfile motionProfile, boolean doorHoldEnabled, LiftDoorButtonLightMode doorButtonLightMode,
            LiftFloorCancelMode floorCancelMode, boolean floorCancelWhileMoving,
            LiftArrivalLanternTriggerMode arrivalLanternTriggerMode, String liftNumber) {
        this(liftId, upSpeed, downSpeed, upAcceleration, downAcceleration, directionParametersLinked,
                adoDistance, levellingDistance, levellingSpeed, motionProfile, doorHoldEnabled,
                doorButtonLightMode, floorCancelMode, floorCancelWhileMoving,
                arrivalLanternTriggerMode, LiftServiceMode.NORMAL, liftNumber);
    }

    public YteLiftConfig(long liftId, double upSpeed, double downSpeed, double upAcceleration, double downAcceleration,
            boolean directionParametersLinked, double adoDistance, double levellingDistance, double levellingSpeed,
            LiftMotionProfile motionProfile, boolean doorHoldEnabled, LiftDoorButtonLightMode doorButtonLightMode,
            LiftFloorCancelMode floorCancelMode, boolean floorCancelWhileMoving,
            LiftArrivalLanternTriggerMode arrivalLanternTriggerMode, LiftServiceMode serviceMode, String liftNumber) {
        super(liftId, upSpeed, downSpeed, upAcceleration, downAcceleration, directionParametersLinked,
                adoDistance, levellingDistance, levellingSpeed, motionProfile.name(), doorHoldEnabled,
                doorButtonLightMode.name(), floorCancelMode.name(), floorCancelWhileMoving,
                arrivalLanternTriggerMode.name(), serviceMode.name(), liftNumber);
    }

    public YteLiftConfig(long liftId, double upSpeed, double downSpeed, double upAcceleration, double downAcceleration,
            boolean directionParametersLinked, double adoDistance, double levellingDistance, double levellingSpeed,
            LiftMotionProfile motionProfile, boolean doorHoldEnabled, LiftDoorButtonLightMode doorButtonLightMode,
            LiftFloorCancelMode floorCancelMode, boolean floorCancelWhileMoving, long doorOpenMs, long doorCloseMs,
            long doorDwellMs, long doorRunDelayMs, DoorMotionCurve doorCurve, String liftNumber) {
        super(liftId, upSpeed, downSpeed, upAcceleration, downAcceleration, directionParametersLinked,
                adoDistance, levellingDistance, levellingSpeed, motionProfile.name(), doorHoldEnabled,
                doorButtonLightMode.name(), floorCancelMode.name(), floorCancelWhileMoving,
                doorOpenMs, doorCloseMs, doorDwellMs, doorRunDelayMs, doorCurve.name(), liftNumber);
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

    public long getDoorOpenMs() { return doorOpenMs; }

    public long getDoorCloseMs() { return doorCloseMs; }

    public long getDoorDwellMs() { return doorDwellMs; }

    public long getDoorRunDelayMs() { return doorRunDelayMs; }

    public DoorMotionCurve getDoorCurve() { return DoorMotionCurve.fromSerializedName(doorCurve); }

    public LiftArrivalLanternTriggerMode getArrivalLanternTriggerMode() {
        return LiftArrivalLanternTriggerMode.fromSerializedName(arrivalLanternTriggerMode);
    }

    public LiftServiceMode getServiceMode() {
        return LiftServiceMode.fromSerializedName(serviceMode);
    }

    public String getLiftNumber() { return liftNumber; }

    public double getRecoverySpeed() { return recoverySpeed; }

    public long getMaxDoorOpenMs() { return maxDoorOpenMs; }

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

    public void setArrivalLanternTriggerMode(LiftArrivalLanternTriggerMode arrivalLanternTriggerMode) {
        this.arrivalLanternTriggerMode = arrivalLanternTriggerMode.name();
    }

    public void setServiceMode(LiftServiceMode serviceMode) {
        this.serviceMode = serviceMode.name();
    }

    public void setLiftNumber(String liftNumber) {
        this.liftNumber = liftNumber;
    }

    public void setDoorOpenMs(long doorOpenMs) { this.doorOpenMs = clampLong(doorOpenMs, MIN_DOOR_OPEN_MS, MAX_DOOR_OPEN_MS); }

    public void setDoorCloseMs(long doorCloseMs) { this.doorCloseMs = clampLong(doorCloseMs, MIN_DOOR_CLOSE_MS, MAX_DOOR_CLOSE_MS); }

    public void setDoorDwellMs(long doorDwellMs) { this.doorDwellMs = clampLong(doorDwellMs, MIN_DOOR_DWELL_MS, MAX_DOOR_DWELL_MS); }

    public void setDoorRunDelayMs(long doorRunDelayMs) { this.doorRunDelayMs = clampLong(doorRunDelayMs, MIN_DOOR_RUN_DELAY_MS, MAX_DOOR_RUN_DELAY_MS); }

    public void setDoorCurve(DoorMotionCurve doorCurve) { this.doorCurve = doorCurve.name(); }

    public void setRecoverySpeed(double recoverySpeed) {
        this.recoverySpeed = clamp(recoverySpeed, MIN_RECOVERY_SPEED, MAX_RECOVERY_SPEED);
    }

    public void setMaxDoorOpenMs(long maxDoorOpenMs) {
        this.maxDoorOpenMs = clampLong(maxDoorOpenMs, MIN_MAX_DOOR_OPEN_MS, MAX_MAX_DOOR_OPEN_MS);
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    private static long clampLong(long value, long min, long max) {
        return Math.max(min, Math.min(max, value));
    }
}
