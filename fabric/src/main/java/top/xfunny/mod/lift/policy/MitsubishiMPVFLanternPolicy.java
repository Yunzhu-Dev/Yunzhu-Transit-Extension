package top.xfunny.mod.lift.policy;

import org.mtr.core.data.LiftDirection;
import top.xfunny.mod.lift.LiftArrivalLanternContext;
import top.xfunny.mod.lift.LiftArrivalLanternDecision;
import top.xfunny.mod.lift.LiftArrivalLanternDisplayPhase;
import top.xfunny.mod.lift.LiftArrivalLanternFlashPattern;
import top.xfunny.mod.lift.LiftArrivalLanternPolicy;
import top.xfunny.mod.lift.LiftArrivalLanternState;
import top.xfunny.mod.lift.LiftDisplayState;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 三菱 MP-VF 到站灯策略：电梯开门前约 5 秒开始慢闪（0.7 亮 / 0.4 熄），
 * 开门期间保持同频闪烁，关门后熄灭。
 * <p>
 * 锁存状态按 (liftId, lanternFloor) 二维 key 存储，避免不同楼层的
 * 到站灯共享同一单例时互相覆盖触发时间戳、导致声音每帧重复播放。
 */
public final class MitsubishiMPVFLanternPolicy implements LiftArrivalLanternPolicy {

    public static final MitsubishiMPVFLanternPolicy INSTANCE = new MitsubishiMPVFLanternPolicy();

    private static final LiftArrivalLanternFlashPattern SLOW_FLASH = LiftArrivalLanternFlashPattern.flashing(700, 400);
    private static final long PRE_DOOR_MILLIS = 5000;
    private static final String SOUND_CUE = "mitsubishi_mp_lantern_1";

    private final Map<Long, Map<Integer, Long>> approachStartMillis = new ConcurrentHashMap<>();

    private MitsubishiMPVFLanternPolicy() {
    }

    public void clear() {
        approachStartMillis.clear();
    }

    @Override
    public LiftArrivalLanternDecision evaluate(LiftArrivalLanternContext context) {
        final LiftDisplayState facts = context.getFacts();
        final LiftArrivalLanternState arrivalState = context.getArrivalState();
        final long liftId = facts.getLiftId();
        final int lanternFloor = context.getLanternFloor();

        // 开门前约 5 秒：朝本层运行且预估剩余时间不超过 5 秒。触发即锁存，
        // 避免速度接近 0 时 estimateMillisToTarget() 归为无穷导致熄灯。
        if (facts.getTargetFloor() == lanternFloor && facts.isMoving()
                && context.estimateMillisToTarget() <= PRE_DOOR_MILLIS) {
            approachStartMillis.computeIfAbsent(liftId, ignored -> new ConcurrentHashMap<>())
                    .putIfAbsent(lanternFloor, context.getCurrentMillis());
        }

        final boolean activeDoorCycleAtLantern = facts.getDoorValue() > 0
                && arrivalState.isActiveForFloor(lanternFloor);
        final boolean approachLatched = getStartMillis(liftId, lanternFloor) > 0
                && (facts.getTargetFloor() == lanternFloor || arrivalState.isActiveForFloor(lanternFloor));

        if (!approachLatched && !activeDoorCycleAtLantern) {
            clearFinishedCycle(facts, arrivalState, liftId, lanternFloor);
            return LiftArrivalLanternDecision.inactive();
        }

        final LiftDirection direction = resolveDirection(activeDoorCycleAtLantern, facts, arrivalState);
        if (direction == LiftDirection.NONE) {
            return LiftArrivalLanternDecision.inactive();
        }

        // 关门结束后熄灭
        if (arrivalState.isArrived() && facts.getDoorValue() <= 0) {
            return LiftArrivalLanternDecision.inactive();
        }

        final long startMillis = getStartMillis(liftId, lanternFloor);
        if (facts.getDoorValue() > 0) {
            final long eventSequence = startMillis > 0 ? startMillis : arrivalState.getTriggerSequence();
            final long phaseStartMillis = startMillis > 0 ? startMillis : arrivalState.getTriggerStartedMillis();
            return LiftArrivalLanternDecision.active(direction, LiftArrivalLanternDisplayPhase.ARRIVED,
                    SLOW_FLASH, SOUND_CUE, eventSequence, phaseStartMillis);
        }

        if (approachLatched) {
            final long effectiveStart = startMillis > 0 ? startMillis : context.getCurrentMillis();
            return LiftArrivalLanternDecision.active(direction, LiftArrivalLanternDisplayPhase.APPROACHING,
                    SLOW_FLASH, SOUND_CUE, effectiveStart, effectiveStart);
        }

        return LiftArrivalLanternDecision.inactive();
    }

    private long getStartMillis(long liftId, int lanternFloor) {
        final Map<Integer, Long> perFloor = approachStartMillis.get(liftId);
        return perFloor == null ? 0 : perFloor.getOrDefault(lanternFloor, 0L);
    }

    private LiftDirection resolveDirection(boolean activeDoorCycleAtLantern,
            LiftDisplayState facts, LiftArrivalLanternState arrivalState) {
        LiftDirection direction = activeDoorCycleAtLantern
                ? arrivalState.getDirection() : facts.getPlannedArrivalDirection();
        if (direction == LiftDirection.NONE) {
            direction = arrivalState.getDirection();
        }
        if (direction == LiftDirection.NONE) {
            direction = facts.getPlannedArrivalDirection();
        }
        return direction;
    }

    private void clearFinishedCycle(LiftDisplayState facts, LiftArrivalLanternState arrivalState,
            long liftId, int lanternFloor) {
        if (!facts.isDoorCycle() && !arrivalState.isActiveForFloor(lanternFloor)
                && facts.getTargetFloor() != lanternFloor) {
            final Map<Integer, Long> perFloor = approachStartMillis.get(liftId);
            if (perFloor != null) {
                perFloor.remove(lanternFloor);
                if (perFloor.isEmpty()) {
                    approachStartMillis.remove(liftId);
                }
            }
        }
    }
}
