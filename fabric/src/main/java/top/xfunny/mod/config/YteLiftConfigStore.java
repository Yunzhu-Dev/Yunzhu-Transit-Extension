package top.xfunny.mod.config;

import top.xfunny.core.data.YteLiftConfig;
import top.xfunny.mod.lift.DoorMotionCurve;
import top.xfunny.mod.lift.FiremanOperationType;
import top.xfunny.mod.lift.LiftArrivalLanternTriggerMode;
import top.xfunny.mod.lift.LiftDoorButtonLightMode;
import top.xfunny.mod.lift.LiftFloorCancelMode;
import top.xfunny.mod.lift.LiftMotionProfile;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 全局电梯配置存储，供 Mixin 快速访问
 * 由 YteSimulator (服务端) 和 YteData (客户端) 各自填充
 */
public final class YteLiftConfigStore {

    private static final Map<Long, Double> speedMap = new ConcurrentHashMap<>();
    private static final Map<Long, Double> accelerationMap = new ConcurrentHashMap<>();
    private static final Map<Long, Double> downSpeedMap = new ConcurrentHashMap<>();
    private static final Map<Long, Double> downAccelerationMap = new ConcurrentHashMap<>();
    private static final Map<Long, Double> adoDistanceMap = new ConcurrentHashMap<>();
    private static final Map<Long, Double> levellingDistanceMap = new ConcurrentHashMap<>();
    private static final Map<Long, Double> levellingSpeedMap = new ConcurrentHashMap<>();
    private static final Map<Long, LiftMotionProfile> motionProfileMap = new ConcurrentHashMap<>();
    private static final Map<Long, Boolean> doorHoldEnabledMap = new ConcurrentHashMap<>();
    private static final Map<Long, LiftDoorButtonLightMode> doorButtonLightModeMap = new ConcurrentHashMap<>();
    private static final Map<Long, LiftFloorCancelMode> floorCancelModeMap = new ConcurrentHashMap<>();
    private static final Map<Long, Boolean> floorCancelWhileMovingMap = new ConcurrentHashMap<>();
    private static final Map<Long, Long> doorOpenMsMap = new ConcurrentHashMap<>();
    private static final Map<Long, Long> doorCloseMsMap = new ConcurrentHashMap<>();
    private static final Map<Long, Long> doorDwellMsMap = new ConcurrentHashMap<>();
    private static final Map<Long, Long> doorRunDelayMsMap = new ConcurrentHashMap<>();
    private static final Map<Long, DoorMotionCurve> doorCurveMap = new ConcurrentHashMap<>();
    private static final Map<Long, Double> recoverySpeedMap = new ConcurrentHashMap<>();
    private static final Map<Long, Long> maxDoorOpenMsMap = new ConcurrentHashMap<>();
    private static final Map<Long, LiftArrivalLanternTriggerMode> arrivalLanternTriggerModeMap = new ConcurrentHashMap<>();
    private static final Map<Long, String> liftNumberMap = new ConcurrentHashMap<>();
    private static final Map<Long, Boolean> firemanLiftMap = new ConcurrentHashMap<>();
    private static final Map<Long, FiremanOperationType> firemanOperationMap = new ConcurrentHashMap<>();
    private static final Map<Long, String> fireRecallFloorMap = new ConcurrentHashMap<>();

    private static final double DEFAULT_SPEED = 10.0;
    private static final double DEFAULT_ACCELERATION = 4.0;
    private static final double DEFAULT_ADO_DISTANCE = 0;
    private static final double DEFAULT_LEVELLING_DISTANCE = 0.3;
    private static final double DEFAULT_LEVELLING_SPEED = 0.2;
    private static final long DEFAULT_DOOR_OPEN_MS = 1600;
    private static final long DEFAULT_DOOR_CLOSE_MS = 1600;
    private static final long DEFAULT_DOOR_DWELL_MS = 2000;
    private static final long DEFAULT_DOOR_RUN_DELAY_MS = 500;

    private YteLiftConfigStore() {}

    public static void put(long liftId, double upSpeed, double downSpeed, double upAcceleration, double downAcceleration,
            double adoDistance, double levellingDistance, double levellingSpeed, LiftMotionProfile motionProfile,
            boolean doorHoldEnabled, LiftDoorButtonLightMode doorButtonLightMode, LiftFloorCancelMode floorCancelMode,
            boolean floorCancelWhileMoving, LiftArrivalLanternTriggerMode arrivalLanternTriggerMode,
            boolean firemanLift, FiremanOperationType firemanOperation, String fireRecallFloor) {
        speedMap.put(liftId, upSpeed);
        downSpeedMap.put(liftId, downSpeed);
        accelerationMap.put(liftId, upAcceleration);
        downAccelerationMap.put(liftId, downAcceleration);
        adoDistanceMap.put(liftId, adoDistance);
        levellingDistanceMap.put(liftId, levellingDistance);
        levellingSpeedMap.put(liftId, levellingSpeed);
        motionProfileMap.put(liftId, motionProfile);
        doorHoldEnabledMap.put(liftId, doorHoldEnabled);
        doorButtonLightModeMap.put(liftId, doorButtonLightMode);
        floorCancelModeMap.put(liftId, floorCancelMode);
        floorCancelWhileMovingMap.put(liftId, floorCancelWhileMoving);
        firemanLiftMap.put(liftId, firemanLift);
        firemanOperationMap.put(liftId, firemanOperation);
        fireRecallFloorMap.put(liftId, fireRecallFloor);
        arrivalLanternTriggerModeMap.put(liftId, arrivalLanternTriggerMode);
    }

    public static double getSpeed(long liftId) {
        return speedMap.getOrDefault(liftId, DEFAULT_SPEED);
    }

    public static double getAcceleration(long liftId) {
        return accelerationMap.getOrDefault(liftId, DEFAULT_ACCELERATION);
    }

    public static double getSpeed(long liftId, boolean down) {
        return down ? downSpeedMap.getOrDefault(liftId, getSpeed(liftId)) : getSpeed(liftId);
    }

    public static double getAcceleration(long liftId, boolean down) {
        return down ? downAccelerationMap.getOrDefault(liftId, getAcceleration(liftId)) : getAcceleration(liftId);
    }

    public static double getAdoDistance(long liftId) { return adoDistanceMap.getOrDefault(liftId, DEFAULT_ADO_DISTANCE); }

    public static double getLevellingDistance(long liftId) { return levellingDistanceMap.getOrDefault(liftId, DEFAULT_LEVELLING_DISTANCE); }

    public static double getLevellingSpeed(long liftId) { return levellingSpeedMap.getOrDefault(liftId, DEFAULT_LEVELLING_SPEED); }

    public static LiftMotionProfile getMotionProfile(long liftId) {
        return motionProfileMap.getOrDefault(liftId, LiftMotionProfile.STANDARD);
    }

    public static boolean isDoorHoldEnabled(long liftId) {
        return doorHoldEnabledMap.getOrDefault(liftId, false);
    }

    public static LiftDoorButtonLightMode getDoorButtonLightMode(long liftId) {
        return doorButtonLightModeMap.getOrDefault(liftId, LiftDoorButtonLightMode.MOMENTARY);
    }

    public static LiftFloorCancelMode getFloorCancelMode(long liftId) {
        return floorCancelModeMap.getOrDefault(liftId, LiftFloorCancelMode.DOUBLE_CLICK);
    }

    public static boolean isFloorCancelWhileMovingAllowed(long liftId) {
        return floorCancelWhileMovingMap.getOrDefault(liftId, false);
    }

    public static boolean isFiremanLift(long liftId){
        return firemanLiftMap.getOrDefault(liftId, false);
    }

    public static FiremanOperationType getFiremanOperation(long liftId) {
        return firemanOperationMap.getOrDefault(liftId, FiremanOperationType.HOLD_DOOR_BUTTON);
    }

    public static String getFireRecallFloor(long liftId) {
        return fireRecallFloorMap.getOrDefault(liftId, YteLiftConfig.DEFAULT_FIRE_RECALL_FLOOR);
    }

    /**
     * Door timing per lift, bundled for the door curve and the message queue.
     * A raw dwell of -1 (infinite open) keeps the default dwell length for the
     * curve math so the FULLY_OPEN band stays non-empty; the infinite-open
     * semantics are handled by the queue's INFINITE_OPEN close timer pinning.
     */
    public static final class DoorParams {
        public final long openMs;
        public final long closeMs;
        public final long dwellMs;
        public final long runDelay;
        public final DoorMotionCurve curve;

        private DoorParams(long openMs, long closeMs, long dwellMs, long runDelay, DoorMotionCurve curve) {
            this.openMs = openMs;
            this.closeMs = closeMs;
            this.dwellMs = dwellMs == -1 ? DEFAULT_DOOR_DWELL_MS : Math.max(dwellMs, 0);
            this.runDelay = runDelay;
            this.curve = curve;
        }

        public long total() {
            return openMs + dwellMs + closeMs + runDelay;
        }

        public long fullOpenCoolDown() {
            return dwellMs + closeMs + runDelay;
        }

        public long closeStartCoolDown() {
            return closeMs + runDelay;
        }
    }

    public static DoorParams getDoorParams(long liftId) {
        return new DoorParams(
                doorOpenMsMap.getOrDefault(liftId, DEFAULT_DOOR_OPEN_MS),
                doorCloseMsMap.getOrDefault(liftId, DEFAULT_DOOR_CLOSE_MS),
                doorDwellMsMap.getOrDefault(liftId, DEFAULT_DOOR_DWELL_MS),
                doorRunDelayMsMap.getOrDefault(liftId, DEFAULT_DOOR_RUN_DELAY_MS),
                doorCurveMap.getOrDefault(liftId, DoorMotionCurve.LINEAR));
    }

    /** Raw dwell in ms; -1 means the door stays open indefinitely. */
    public static long getDoorDwellMs(long liftId) {
        return doorDwellMsMap.getOrDefault(liftId, DEFAULT_DOOR_DWELL_MS);
    }

    public static void putDoorParams(long liftId, long openMs, long closeMs, long dwellMs, long runDelay,
            DoorMotionCurve curve) {
        doorOpenMsMap.put(liftId, openMs);
        doorCloseMsMap.put(liftId, closeMs);
        doorDwellMsMap.put(liftId, dwellMs);
        doorRunDelayMsMap.put(liftId, runDelay);
        doorCurveMap.put(liftId, curve);
    }

    public static void putRecoverySpeed(long liftId, double recoverySpeed) {
        recoverySpeedMap.put(liftId, recoverySpeed);
    }

    public static void putMaxDoorOpenMs(long liftId, long maxDoorOpenMs) {
        maxDoorOpenMsMap.put(liftId, Math.max(YteLiftConfig.MIN_MAX_DOOR_OPEN_MS,
                Math.min(YteLiftConfig.MAX_MAX_DOOR_OPEN_MS, maxDoorOpenMs)));
    }

    /** 光幕最大开门时长（ms），读端 clamp [30s, 60s]。 */
    public static long getMaxDoorOpenMs(long liftId) {
        return Math.max(YteLiftConfig.MIN_MAX_DOOR_OPEN_MS,
                Math.min(YteLiftConfig.MAX_MAX_DOOR_OPEN_MS,
                        maxDoorOpenMsMap.getOrDefault(liftId, YteLiftConfig.DEFAULT_MAX_DOOR_OPEN_MS)));
    }

    /** 自动救援就近平层速度（m/s），读端 clamp [0.1, 1.0]。 */
    public static double getRecoverySpeed(long liftId) {
        return Math.max(YteLiftConfig.MIN_RECOVERY_SPEED,
                Math.min(YteLiftConfig.MAX_RECOVERY_SPEED,
                        recoverySpeedMap.getOrDefault(liftId, YteLiftConfig.DEFAULT_RECOVERY_SPEED)));
    }

    public static LiftArrivalLanternTriggerMode getArrivalLanternTriggerMode(long liftId) {
        return arrivalLanternTriggerModeMap.getOrDefault(liftId, LiftArrivalLanternTriggerMode.DECELERATION);
    }

    /**
     * 电梯编号（备注别名，非 MTR 的 liftId）。
     * 未设置时返回空字符串。
     */
    public static String getLiftNumber(long liftId) {
        return liftNumberMap.getOrDefault(liftId, "");
    }

    public static void setLiftNumber(long liftId, String liftNumber) {
        if (liftNumber == null || liftNumber.isEmpty()) {
            liftNumberMap.remove(liftId);
        } else {
            liftNumberMap.put(liftId, liftNumber);
        }
    }

    public static void remove(long liftId) {
        speedMap.remove(liftId);
        accelerationMap.remove(liftId);
        downSpeedMap.remove(liftId);
        downAccelerationMap.remove(liftId);
        adoDistanceMap.remove(liftId);
        levellingDistanceMap.remove(liftId);
        levellingSpeedMap.remove(liftId);
        motionProfileMap.remove(liftId);
        doorHoldEnabledMap.remove(liftId);
        doorButtonLightModeMap.remove(liftId);
        floorCancelModeMap.remove(liftId);
        floorCancelWhileMovingMap.remove(liftId);
        arrivalLanternTriggerModeMap.remove(liftId);
        liftNumberMap.remove(liftId);
        doorOpenMsMap.remove(liftId);
        doorCloseMsMap.remove(liftId);
        doorDwellMsMap.remove(liftId);
        doorRunDelayMsMap.remove(liftId);
        doorCurveMap.remove(liftId);
        recoverySpeedMap.remove(liftId);
        maxDoorOpenMsMap.remove(liftId);
        firemanLiftMap.remove(liftId);
        firemanOperationMap.remove(liftId);
        fireRecallFloorMap.remove(liftId);
    }

    public static void clear() {
        speedMap.clear();
        accelerationMap.clear();
        downSpeedMap.clear();
        downAccelerationMap.clear();
        adoDistanceMap.clear();
        levellingDistanceMap.clear();
        levellingSpeedMap.clear();
        motionProfileMap.clear();
        doorHoldEnabledMap.clear();
        doorButtonLightModeMap.clear();
        floorCancelModeMap.clear();
        floorCancelWhileMovingMap.clear();
        arrivalLanternTriggerModeMap.clear();
        liftNumberMap.clear();
        doorOpenMsMap.clear();
        doorCloseMsMap.clear();
        doorDwellMsMap.clear();
        doorRunDelayMsMap.clear();
        doorCurveMap.clear();
        recoverySpeedMap.clear();
        maxDoorOpenMsMap.clear();
        firemanLiftMap.clear();
        firemanOperationMap.clear();
        fireRecallFloorMap.clear();
    }
}
