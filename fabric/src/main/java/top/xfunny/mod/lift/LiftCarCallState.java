package top.xfunny.mod.lift;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** 类型2消防员延迟登记：客户端门完全关闭后发起的内呼登记请求，由服务端 Lift tick 消费。 */
public final class LiftCarCallState {

    private static final Map<Long, Integer> PENDING_REGISTRATIONS = new ConcurrentHashMap<>();

    private LiftCarCallState() {
    }

    public static void request(long liftId, int floorIndex) {
        PENDING_REGISTRATIONS.put(liftId, floorIndex);
    }

    public static Integer consume(long liftId) {
        return PENDING_REGISTRATIONS.remove(liftId);
    }
}
