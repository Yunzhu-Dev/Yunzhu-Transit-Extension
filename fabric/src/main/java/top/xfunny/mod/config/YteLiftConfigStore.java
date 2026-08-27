package top.xfunny.mod.config;

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
    private static final Map<Long, LiftArrivalLanternTriggerMode> arrivalLanternTriggerModeMap = new ConcurrentHashMap<>();
    private static final Map<Long, String> liftNumberMap = new ConcurrentHashMap<>();

    private static final double DEFAULT_SPEED = 10.0;
    private static final double DEFAULT_ACCELERATION = 4.0;
    private static final double DEFAULT_ADO_DISTANCE = 0;
    private static final double DEFAULT_LEVELLING_DISTANCE = 0.3;
    private static final double DEFAULT_LEVELLING_SPEED = 0.2;

    private YteLiftConfigStore() {}

    public static void put(long liftId, double speed, double acceleration, double adoDistance, double levellingDistance, double levellingSpeed) {
        put(liftId, speed, speed, acceleration, acceleration, adoDistance, levellingDistance, levellingSpeed);
    }

    public static void put(long liftId, double upSpeed, double downSpeed, double upAcceleration, double downAcceleration,
            double adoDistance, double levellingDistance, double levellingSpeed) {
        put(liftId, upSpeed, downSpeed, upAcceleration, downAcceleration,
                adoDistance, levellingDistance, levellingSpeed, LiftMotionProfile.STANDARD);
    }

    public static void put(long liftId, double upSpeed, double downSpeed, double upAcceleration, double downAcceleration,
            double adoDistance, double levellingDistance, double levellingSpeed, LiftMotionProfile motionProfile) {
        put(liftId, upSpeed, downSpeed, upAcceleration, downAcceleration,
                adoDistance, levellingDistance, levellingSpeed, motionProfile, false);
    }

    public static void put(long liftId, double upSpeed, double downSpeed, double upAcceleration, double downAcceleration,
            double adoDistance, double levellingDistance, double levellingSpeed, LiftMotionProfile motionProfile,
            boolean doorHoldEnabled) {
        put(liftId, upSpeed, downSpeed, upAcceleration, downAcceleration, adoDistance, levellingDistance,
                levellingSpeed, motionProfile, doorHoldEnabled, LiftDoorButtonLightMode.MOMENTARY,
                LiftFloorCancelMode.DOUBLE_CLICK, false);
    }

    public static void put(long liftId, double upSpeed, double downSpeed, double upAcceleration, double downAcceleration,
            double adoDistance, double levellingDistance, double levellingSpeed, LiftMotionProfile motionProfile,
            boolean doorHoldEnabled, LiftDoorButtonLightMode doorButtonLightMode, LiftFloorCancelMode floorCancelMode,
            boolean floorCancelWhileMoving) {
        put(liftId, upSpeed, downSpeed, upAcceleration, downAcceleration, adoDistance, levellingDistance,
                levellingSpeed, motionProfile, doorHoldEnabled, doorButtonLightMode, floorCancelMode,
                floorCancelWhileMoving, LiftArrivalLanternTriggerMode.DECELERATION);
    }

    public static void put(long liftId, double upSpeed, double downSpeed, double upAcceleration, double downAcceleration,
            double adoDistance, double levellingDistance, double levellingSpeed, LiftMotionProfile motionProfile,
            boolean doorHoldEnabled, LiftDoorButtonLightMode doorButtonLightMode, LiftFloorCancelMode floorCancelMode,
            boolean floorCancelWhileMoving, LiftArrivalLanternTriggerMode arrivalLanternTriggerMode) {
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
    }
}
