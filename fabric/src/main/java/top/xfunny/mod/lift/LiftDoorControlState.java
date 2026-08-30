package top.xfunny.mod.lift;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class LiftDoorControlState {

    private static final long CLIENT_OPEN_PREDICTION_TIMEOUT = 3000;
    private static final long HOLD_TIMEOUT = 60_000;
    private static final long MANUAL_CLOSE_SIGNAL_TIMEOUT = 750;

    public enum Command {
        OPEN,
        CLOSE,
        HOLD_OPEN,
        RELEASE_CLOSE
    }

    private static final Map<Long, Command> PENDING_COMMANDS = new ConcurrentHashMap<>();
    private static final Map<Long, ClientOpenPrediction> CLIENT_OPEN_PREDICTIONS = new ConcurrentHashMap<>();
    private static final Set<Long> ACTIVE_HOLDS = ConcurrentHashMap.newKeySet();
    private static final Map<Long, Long> HOLD_START_TIMES = new ConcurrentHashMap<>();
    private static final Map<Long, Long> CLIENT_HOLD_EXPIRATION_TIMES = new ConcurrentHashMap<>();
    private static final Map<Long, Long> MANUAL_CLOSE_SIGNAL_TIMES = new ConcurrentHashMap<>();

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

    public static void signalManualClose(long liftId) {
        MANUAL_CLOSE_SIGNAL_TIMES.put(liftId, System.currentTimeMillis());
    }

    public static void endManualClose(long liftId) {
        MANUAL_CLOSE_SIGNAL_TIMES.remove(liftId);
    }

    public static boolean isManualCloseActive(long liftId) {
        final Long lastSignalTime = MANUAL_CLOSE_SIGNAL_TIMES.get(liftId);
        if (lastSignalTime == null) {
            return false;
        }
        if (System.currentTimeMillis() - lastSignalTime > MANUAL_CLOSE_SIGNAL_TIMEOUT) {
            MANUAL_CLOSE_SIGNAL_TIMES.remove(liftId, lastSignalTime);
            return false;
        }
        return true;
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

    public static void beginClientOpenPrediction(long liftId, float doorValue, long doorMoveTime) {
        final long currentTime = System.currentTimeMillis();
        CLIENT_OPEN_PREDICTIONS.put(liftId, new ClientOpenPrediction(doorValue, currentTime,
                Math.max(doorMoveTime, 1), currentTime + CLIENT_OPEN_PREDICTION_TIMEOUT));
    }

    public static float preserveClientOpenDoorValue(long liftId, float doorValue) {
        final ClientOpenPrediction prediction = CLIENT_OPEN_PREDICTIONS.get(liftId);
        if (prediction == null) {
            return doorValue;
        }
        final long currentTime = System.currentTimeMillis();
        if (currentTime > prediction.expiresAt) {
            CLIENT_OPEN_PREDICTIONS.remove(liftId, prediction);
            return doorValue;
        }

        final float predictedDoorValue = Math.min(1F, prediction.initialDoorValue
                + (float) (currentTime - prediction.startedAt) / prediction.doorMoveTime);
        prediction.doorValue = Math.max(prediction.doorValue, Math.max(doorValue, predictedDoorValue));

        // Do not hand control back merely because the command acknowledgement
        // arrived; a regular lift update carrying the pre-command cooldown can
        // still arrive afterwards. Release only after both timelines are open.
        if (prediction.confirmed && doorValue >= 0.999F && predictedDoorValue >= 0.999F) {
            CLIENT_OPEN_PREDICTIONS.remove(liftId, prediction);
            return doorValue;
        }
        return prediction.doorValue;
    }

    public static long reconcileClientOpenCoolDown(long liftId, long serverCoolDown) {
        final ClientOpenPrediction prediction = CLIENT_OPEN_PREDICTIONS.get(liftId);
        if (prediction == null) {
            return serverCoolDown;
        }
        if (System.currentTimeMillis() > prediction.expiresAt) {
            CLIENT_OPEN_PREDICTIONS.remove(liftId, prediction);
            return serverCoolDown;
        }

        prediction.confirmed = true;
        prediction.expiresAt = System.currentTimeMillis() + CLIENT_OPEN_PREDICTION_TIMEOUT;
        return serverCoolDown;
    }

    private static final class ClientOpenPrediction {
        private final float initialDoorValue;
        private final long startedAt;
        private final long doorMoveTime;
        private float doorValue;
        private boolean confirmed;
        private long expiresAt;

        private ClientOpenPrediction(float doorValue, long startedAt, long doorMoveTime, long expiresAt) {
            initialDoorValue = doorValue;
            this.startedAt = startedAt;
            this.doorMoveTime = doorMoveTime;
            this.doorValue = doorValue;
            this.expiresAt = expiresAt;
        }
    }
}
