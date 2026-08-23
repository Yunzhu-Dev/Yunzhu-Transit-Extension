package top.xfunny.mod.lift;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LiftStateManager {
    // 存储处于消防模式的电梯ID，以及对应的消防返回层(基站)的标识或索引
    private static final Map<Long, String> FIRE_MODE_LIFTS = new ConcurrentHashMap<>();

    public static void enableFireMode(long liftId, String fireFloorNumber) {
        FIRE_MODE_LIFTS.put(liftId, fireFloorNumber);
    }

    public static void disableFireMode(long liftId) {
        FIRE_MODE_LIFTS.remove(liftId);
    }

    public static boolean isFireMode(long liftId) {
        return FIRE_MODE_LIFTS.containsKey(liftId);
    }

    public static String getFireFloor(long liftId) {
        return FIRE_MODE_LIFTS.get(liftId);
    }
}