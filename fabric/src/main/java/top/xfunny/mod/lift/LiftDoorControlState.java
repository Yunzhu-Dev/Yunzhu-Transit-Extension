package top.xfunny.mod.lift;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class LiftDoorControlState {

    private static final long CLIENT_OPEN_PREDICTION_TIMEOUT = 3000;
    private static final long HOLD_TIMEOUT = 60_000;

    public enum Command {
        OPEN,
        CLOSE,
        HOLD_OPEN
    }

    private static final Map<Long, Command> PENDING_COMMANDS = new ConcurrentHashMap<>();
    private static final Map<Long, ClientOpenPrediction> CLIENT_OPEN_PREDICTIONS = new ConcurrentHashMap<>();
    private static final Set<Long> ACTIVE_HOLDS = ConcurrentHashMap.newKeySet();
    private static final Map<Long, Long> HOLD_START_TIMES = new ConcurrentHashMap<>();
    private static final Map<Long, Long> CLIENT_HOLD_EXPIRATION_TIMES = new ConcurrentHashMap<>();

    private LiftDoorControlState() {
    }

    public static void request(long liftId, Command command) {
        PENDING_COMMANDS.put(liftId, command);
    }

    public static Command consume(long liftId) {
        return PENDING_COMMANDS.remove(liftId);
    }

    public static void beginHold(long liftId) {
        ACTIVE_HOLDS.add(liftId);
        HOLD_START_TIMES.put(liftId, System.currentTimeMillis());
    }

    public static void endHold(long liftId) {
        ACTIVE_HOLDS.remove(liftId);
        HOLD_START_TIMES.remove(liftId);
    }

    public static boolean isHoldActive(long liftId) {
        return ACTIVE_HOLDS.contains(liftId);
    }

    public static boolean isHoldExpired(long liftId) {
        final Long startTime = HOLD_START_TIMES.get(liftId);
        return startTime != null && System.currentTimeMillis() - startTime >= HOLD_TIMEOUT;
    }

    public static long getHoldRemainingMillis(long liftId) {
        final Long startTime = HOLD_START_TIMES.get(liftId);
        return startTime == null ? 0 : Math.max(HOLD_TIMEOUT - (System.currentTimeMillis() - startTime), 0);
    }

    public static void updateClientHold(long liftId, boolean active, long remainingMillis) {
        if (active && remainingMillis > 0) {
            CLIENT_HOLD_EXPIRATION_TIMES.put(liftId,
                    System.currentTimeMillis() + Math.min(remainingMillis, HOLD_TIMEOUT));
        } else {
            CLIENT_HOLD_EXPIRATION_TIMES.remove(liftId);
        }
    }

    public static boolean isClientHoldActive(long liftId) {
        final Long expirationTime = CLIENT_HOLD_EXPIRATION_TIMES.get(liftId);
        if (expirationTime == null) {
            return false;
        }
        if (System.currentTimeMillis() >= expirationTime) {
            CLIENT_HOLD_EXPIRATION_TIMES.remove(liftId, expirationTime);
            return false;
        }
        return true;
    }

    public static void clearClientState() {
        CLIENT_OPEN_PREDICTIONS.clear();
        CLIENT_HOLD_EXPIRATION_TIMES.clear();
    }

    public static void beginClientOpenPrediction(long liftId, float doorValue) {
        CLIENT_OPEN_PREDICTIONS.put(liftId, new ClientOpenPrediction(doorValue,
                System.currentTimeMillis() + CLIENT_OPEN_PREDICTION_TIMEOUT));
    }

    public static float preserveClientOpenDoorValue(long liftId, float doorValue) {
        final ClientOpenPrediction prediction = CLIENT_OPEN_PREDICTIONS.get(liftId);
        if (prediction == null) {
            return doorValue;
        }
        if (System.currentTimeMillis() > prediction.expiresAt) {
            CLIENT_OPEN_PREDICTIONS.remove(liftId, prediction);
            return doorValue;
        }
        prediction.doorValue = Math.max(prediction.doorValue, doorValue);
        return prediction.doorValue;
    }

    public static long reconcileClientOpenCoolDown(long liftId, long serverCoolDown,
            long stoppingTime, long singleDoorMoveTime) {
        final ClientOpenPrediction prediction = CLIENT_OPEN_PREDICTIONS.remove(liftId);
        if (prediction == null || System.currentTimeMillis() > prediction.expiresAt) {
            return serverCoolDown;
        }

        // The client starts opening immediately, while the server starts only
        // after receiving the button packet. Never let that later authoritative
        // start rewind the already visible opening progress.
        final long predictedCoolDown = stoppingTime
                - Math.round(Math.max(0, Math.min(prediction.doorValue, 1)) * singleDoorMoveTime);
        return Math.min(serverCoolDown, predictedCoolDown);
    }

    private static final class ClientOpenPrediction {
        private float doorValue;
        private final long expiresAt;

        private ClientOpenPrediction(float doorValue, long expiresAt) {
            this.doorValue = doorValue;
            this.expiresAt = expiresAt;
        }
    }
}
