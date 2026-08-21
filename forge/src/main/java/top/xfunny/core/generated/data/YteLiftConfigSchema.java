package top.xfunny.core.generated.data;

import org.mtr.core.serializer.ReaderBase;
import org.mtr.core.serializer.SerializedDataBaseWithId;
import org.mtr.core.serializer.WriterBase;

public abstract class YteLiftConfigSchema implements SerializedDataBaseWithId {

    protected long liftId;
    protected double speed;
    protected double acceleration;
    protected double downSpeed;
    protected double downAcceleration;
    protected boolean directionParametersLinked;
    protected String motionProfile;
    protected double adoDistance;
    protected double levellingDistance;
    protected double levellingSpeed;
    protected boolean doorHoldEnabled;
    protected String doorButtonLightMode;
    protected String floorCancelMode;
    protected boolean floorCancelWhileMoving;
    protected long doorOpenMs;
    protected long doorCloseMs;
    protected long doorDwellMs;
    protected long doorRunDelayMs;
    protected String doorCurve;

    private static final String KEY_LIFT_ID = "lift_id";
    private static final String KEY_SPEED = "speed";
    private static final String KEY_ACCELERATION = "acceleration";
    private static final String KEY_DOWN_SPEED = "down_speed";
    private static final String KEY_DOWN_ACCELERATION = "down_acceleration";
    private static final String KEY_DIRECTION_PARAMETERS_LINKED = "direction_parameters_linked";
    private static final String KEY_MOTION_PROFILE = "motion_profile";
    private static final String KEY_ADO_DISTANCE = "ado_distance";
    private static final String KEY_LEVELLING_DISTANCE = "levelling_distance";
    private static final String KEY_LEVELLING_SPEED = "levelling_speed";
    private static final String KEY_DOOR_HOLD_ENABLED = "door_hold_enabled";
    private static final String KEY_DOOR_BUTTON_LIGHT_MODE = "door_button_light_mode";
    private static final String KEY_FLOOR_CANCEL_MODE = "floor_cancel_mode";
    private static final String KEY_FLOOR_CANCEL_WHILE_MOVING = "floor_cancel_while_moving";
    private static final String KEY_DOOR_OPEN_MS = "door_open_ms";
    private static final String KEY_DOOR_CLOSE_MS = "door_close_ms";
    private static final String KEY_DOOR_DWELL_MS = "door_dwell_ms";
    private static final String KEY_DOOR_RUN_DELAY_MS = "door_run_delay_ms";
    private static final String KEY_DOOR_CURVE = "door_curve";

    public static final double DEFAULT_SPEED = 10.0;
    public static final double DEFAULT_ACCELERATION = 4.0;
    public static final double DEFAULT_ADO_DISTANCE = 0;
    public static final double DEFAULT_LEVELLING_DISTANCE = 0.3;
    public static final double DEFAULT_LEVELLING_SPEED = 0.2;
    public static final double MIN_SPEED = 0.1;
    public static final double MAX_SPEED = 20.0;
    public static final double MIN_ACCELERATION = 0.1;
    public static final double MAX_ACCELERATION = 10.0;
    public static final double MIN_ADO_DISTANCE = 0;
    public static final double MAX_ADO_DISTANCE = 2;
    public static final double MIN_LEVELLING_DISTANCE = 0;
    public static final double MAX_LEVELLING_DISTANCE = 5;
    public static final double MIN_LEVELLING_SPEED = 0;
    public static final double MAX_LEVELLING_SPEED = 5;
    public static final double STEP = 0.5;
    public static final String DEFAULT_MOTION_PROFILE = "STANDARD";
    public static final String DEFAULT_DOOR_BUTTON_LIGHT_MODE = "MOMENTARY";
    public static final String DEFAULT_FLOOR_CANCEL_MODE = "DOUBLE_CLICK";
    public static final long DEFAULT_DOOR_OPEN_MS = 1600;
    public static final long DEFAULT_DOOR_CLOSE_MS = 1600;
    public static final long DEFAULT_DOOR_DWELL_MS = 2000;
    public static final long DEFAULT_DOOR_RUN_DELAY_MS = 500;
    public static final String DEFAULT_DOOR_CURVE = "LINEAR";
    public static final long MIN_DOOR_OPEN_MS = 1000;
    public static final long MAX_DOOR_OPEN_MS = 10000;
    public static final long MIN_DOOR_CLOSE_MS = 1000;
    public static final long MAX_DOOR_CLOSE_MS = 10000;
    /** -1 表示无限开门（消防/专用模式） */
    public static final long MIN_DOOR_DWELL_MS = -1;
    public static final long MAX_DOOR_DWELL_MS = 60000;
    public static final long MIN_DOOR_RUN_DELAY_MS = 0;
    public static final long MAX_DOOR_RUN_DELAY_MS = 5000;

    protected YteLiftConfigSchema(long liftId, double speed, double acceleration, double adoDistance, double levellingDistance, double levellingSpeed) {
        this(liftId, speed, speed, acceleration, acceleration, true, adoDistance, levellingDistance, levellingSpeed,
                DEFAULT_MOTION_PROFILE, false);
    }

    protected YteLiftConfigSchema(long liftId, double speed, double downSpeed, double acceleration, double downAcceleration,
            boolean directionParametersLinked, double adoDistance, double levellingDistance, double levellingSpeed) {
        this(liftId, speed, downSpeed, acceleration, downAcceleration, directionParametersLinked,
                adoDistance, levellingDistance, levellingSpeed, DEFAULT_MOTION_PROFILE, false);
    }

    protected YteLiftConfigSchema(long liftId, double speed, double downSpeed, double acceleration, double downAcceleration,
            boolean directionParametersLinked, double adoDistance, double levellingDistance, double levellingSpeed,
            String motionProfile) {
        this(liftId, speed, downSpeed, acceleration, downAcceleration, directionParametersLinked,
                adoDistance, levellingDistance, levellingSpeed, motionProfile, false);
    }

    protected YteLiftConfigSchema(long liftId, double speed, double downSpeed, double acceleration, double downAcceleration,
            boolean directionParametersLinked, double adoDistance, double levellingDistance, double levellingSpeed,
            String motionProfile, boolean doorHoldEnabled) {
        this(liftId, speed, downSpeed, acceleration, downAcceleration, directionParametersLinked,
                adoDistance, levellingDistance, levellingSpeed, motionProfile, doorHoldEnabled,
                DEFAULT_DOOR_BUTTON_LIGHT_MODE, DEFAULT_FLOOR_CANCEL_MODE, false);
    }

    protected YteLiftConfigSchema(long liftId, double speed, double downSpeed, double acceleration, double downAcceleration,
            boolean directionParametersLinked, double adoDistance, double levellingDistance, double levellingSpeed,
            String motionProfile, boolean doorHoldEnabled, String doorButtonLightMode, String floorCancelMode,
            boolean floorCancelWhileMoving) {
        this(liftId, speed, downSpeed, acceleration, downAcceleration, directionParametersLinked,
                adoDistance, levellingDistance, levellingSpeed, motionProfile, doorHoldEnabled,
                doorButtonLightMode, floorCancelMode, floorCancelWhileMoving,
                DEFAULT_DOOR_OPEN_MS, DEFAULT_DOOR_CLOSE_MS, DEFAULT_DOOR_DWELL_MS,
                DEFAULT_DOOR_RUN_DELAY_MS, DEFAULT_DOOR_CURVE);
    }

    protected YteLiftConfigSchema(long liftId, double speed, double downSpeed, double acceleration, double downAcceleration,
            boolean directionParametersLinked, double adoDistance, double levellingDistance, double levellingSpeed,
            String motionProfile, boolean doorHoldEnabled, String doorButtonLightMode, String floorCancelMode,
            boolean floorCancelWhileMoving, long doorOpenMs, long doorCloseMs, long doorDwellMs, long doorRunDelayMs,
            String doorCurve) {
        this.liftId = liftId;
        this.speed = speed;
        this.acceleration = acceleration;
        this.downSpeed = downSpeed;
        this.downAcceleration = downAcceleration;
        this.directionParametersLinked = directionParametersLinked;
        this.motionProfile = motionProfile;
        this.adoDistance = adoDistance;
        this.levellingDistance = levellingDistance;
        this.levellingSpeed = levellingSpeed;
        this.doorHoldEnabled = doorHoldEnabled;
        this.doorButtonLightMode = doorButtonLightMode;
        this.floorCancelMode = floorCancelMode;
        this.floorCancelWhileMoving = floorCancelWhileMoving;
        this.doorOpenMs = doorOpenMs;
        this.doorCloseMs = doorCloseMs;
        this.doorDwellMs = doorDwellMs;
        this.doorRunDelayMs = doorRunDelayMs;
        this.doorCurve = doorCurve;
    }

    protected YteLiftConfigSchema(ReaderBase readerBase) {
        speed = DEFAULT_SPEED;
        acceleration = DEFAULT_ACCELERATION;
        downSpeed = DEFAULT_SPEED;
        downAcceleration = DEFAULT_ACCELERATION;
        directionParametersLinked = true;
        motionProfile = DEFAULT_MOTION_PROFILE;
        adoDistance = DEFAULT_ADO_DISTANCE;
        levellingDistance = DEFAULT_LEVELLING_DISTANCE;
        levellingSpeed = DEFAULT_LEVELLING_SPEED;
        doorHoldEnabled = false;
        doorButtonLightMode = DEFAULT_DOOR_BUTTON_LIGHT_MODE;
        floorCancelMode = DEFAULT_FLOOR_CANCEL_MODE;
        floorCancelWhileMoving = false;
        doorOpenMs = DEFAULT_DOOR_OPEN_MS;
        doorCloseMs = DEFAULT_DOOR_CLOSE_MS;
        doorDwellMs = DEFAULT_DOOR_DWELL_MS;
        doorRunDelayMs = DEFAULT_DOOR_RUN_DELAY_MS;
        doorCurve = DEFAULT_DOOR_CURVE;
        updateData(readerBase);
    }

    @Override
    public void updateData(ReaderBase readerBase) {
        readerBase.unpackLong(KEY_LIFT_ID, value -> liftId = value);
        readerBase.unpackDouble(KEY_SPEED, value -> speed = value);
        readerBase.unpackDouble(KEY_ACCELERATION, value -> acceleration = value);
        downSpeed = readerBase.getDouble(KEY_DOWN_SPEED, speed);
        downAcceleration = readerBase.getDouble(KEY_DOWN_ACCELERATION, acceleration);
        directionParametersLinked = readerBase.getBoolean(KEY_DIRECTION_PARAMETERS_LINKED, true);
        motionProfile = readerBase.getString(KEY_MOTION_PROFILE, DEFAULT_MOTION_PROFILE);
        readerBase.unpackDouble(KEY_ADO_DISTANCE, value -> adoDistance = value);
        readerBase.unpackDouble(KEY_LEVELLING_DISTANCE, value -> levellingDistance = value);
        readerBase.unpackDouble(KEY_LEVELLING_SPEED, value -> levellingSpeed = value);
        doorHoldEnabled = readerBase.getBoolean(KEY_DOOR_HOLD_ENABLED, false);
        doorButtonLightMode = readerBase.getString(KEY_DOOR_BUTTON_LIGHT_MODE, DEFAULT_DOOR_BUTTON_LIGHT_MODE);
        floorCancelMode = readerBase.getString(KEY_FLOOR_CANCEL_MODE, DEFAULT_FLOOR_CANCEL_MODE);
        floorCancelWhileMoving = readerBase.getBoolean(KEY_FLOOR_CANCEL_WHILE_MOVING, false);
        doorOpenMs = readerBase.getLong(KEY_DOOR_OPEN_MS, DEFAULT_DOOR_OPEN_MS);
        doorCloseMs = readerBase.getLong(KEY_DOOR_CLOSE_MS, DEFAULT_DOOR_CLOSE_MS);
        doorDwellMs = readerBase.getLong(KEY_DOOR_DWELL_MS, DEFAULT_DOOR_DWELL_MS);
        doorRunDelayMs = readerBase.getLong(KEY_DOOR_RUN_DELAY_MS, DEFAULT_DOOR_RUN_DELAY_MS);
        doorCurve = readerBase.getString(KEY_DOOR_CURVE, DEFAULT_DOOR_CURVE);
    }

    @Override
    public void serializeData(WriterBase writerBase) {
        writerBase.writeLong(KEY_LIFT_ID, liftId);
        writerBase.writeDouble(KEY_SPEED, speed);
        writerBase.writeDouble(KEY_ACCELERATION, acceleration);
        writerBase.writeDouble(KEY_DOWN_SPEED, downSpeed);
        writerBase.writeDouble(KEY_DOWN_ACCELERATION, downAcceleration);
        writerBase.writeBoolean(KEY_DIRECTION_PARAMETERS_LINKED, directionParametersLinked);
        writerBase.writeString(KEY_MOTION_PROFILE, motionProfile);
        writerBase.writeDouble(KEY_ADO_DISTANCE, adoDistance);
        writerBase.writeDouble(KEY_LEVELLING_DISTANCE, levellingDistance);
        writerBase.writeDouble(KEY_LEVELLING_SPEED, levellingSpeed);
        writerBase.writeBoolean(KEY_DOOR_HOLD_ENABLED, doorHoldEnabled);
        writerBase.writeString(KEY_DOOR_BUTTON_LIGHT_MODE, doorButtonLightMode);
        writerBase.writeString(KEY_FLOOR_CANCEL_MODE, floorCancelMode);
        writerBase.writeBoolean(KEY_FLOOR_CANCEL_WHILE_MOVING, floorCancelWhileMoving);
        writerBase.writeLong(KEY_DOOR_OPEN_MS, doorOpenMs);
        writerBase.writeLong(KEY_DOOR_CLOSE_MS, doorCloseMs);
        writerBase.writeLong(KEY_DOOR_DWELL_MS, doorDwellMs);
        writerBase.writeLong(KEY_DOOR_RUN_DELAY_MS, doorRunDelayMs);
        writerBase.writeString(KEY_DOOR_CURVE, doorCurve);
    }

    @Override
    public String getHexId() {
        return Long.toHexString(liftId);
    }

    @Override
    public boolean isValid() {
        return liftId != 0
                && speed >= MIN_SPEED && speed <= MAX_SPEED
                && acceleration >= MIN_ACCELERATION && acceleration <= MAX_ACCELERATION
                && downSpeed >= MIN_SPEED && downSpeed <= MAX_SPEED
                && downAcceleration >= MIN_ACCELERATION && downAcceleration <= MAX_ACCELERATION
                && adoDistance >= MIN_ADO_DISTANCE && adoDistance <= MAX_ADO_DISTANCE
                && levellingDistance >= MIN_LEVELLING_DISTANCE && levellingDistance <= MAX_LEVELLING_DISTANCE
                && levellingSpeed >= MIN_LEVELLING_SPEED && levellingSpeed <= MAX_LEVELLING_SPEED
                && doorOpenMs >= MIN_DOOR_OPEN_MS && doorOpenMs <= MAX_DOOR_OPEN_MS
                && doorCloseMs >= MIN_DOOR_CLOSE_MS && doorCloseMs <= MAX_DOOR_CLOSE_MS
                && doorDwellMs >= MIN_DOOR_DWELL_MS && doorDwellMs <= MAX_DOOR_DWELL_MS
                && doorRunDelayMs >= MIN_DOOR_RUN_DELAY_MS && doorRunDelayMs <= MAX_DOOR_RUN_DELAY_MS;
    }

    public long getId() {
        return liftId;
    }
}
