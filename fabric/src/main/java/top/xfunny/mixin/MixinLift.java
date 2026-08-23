package top.xfunny.mixin;

import org.mtr.core.data.*;
import org.mtr.core.data.Lift;
import org.mtr.core.operation.PressLift;
import org.mtr.core.simulation.Simulator;
import org.mtr.core.tool.Utilities;
import org.mtr.mapping.holder.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.xfunny.mod.config.YteLiftConfigStore;
import top.xfunny.mod.Init;
import top.xfunny.mod.lift.LiftDisplayDirection;
import top.xfunny.mod.lift.LiftDisplayDirectionState;
import top.xfunny.mod.lift.LiftDoorControlState;
import top.xfunny.mod.lift.LiftFloorCancelState;
import top.xfunny.mod.lift.DisplayDirectionMode;
import top.xfunny.mod.lift.LiftDisplayState;
import top.xfunny.mod.lift.LiftMotionProfile;
import top.xfunny.mod.lift.LiftStateManager;

@Mixin(value = Lift.class, remap = false)
public abstract class MixinLift implements MixinLiftSchema, MixinLiftFields, MixinNameColorDataBaseSchema, LiftDisplayDirection {

    @Unique
    private static final long YTE_BRAKE_HOLD_TIME = 200;

    @Unique
    private static final long YTE_ARRIVAL_DIRECTION_DELAY = 100;

    @Unique
    private int yte$motionTargetFloor = Integer.MIN_VALUE;

    @Unique
    private boolean yte$twoStageFineLevelling;

    @Unique
    private long yte$twoStageCoarseHoldRemaining;

    @Unique
    private static final long YTE_TWO_STAGE_COARSE_HOLD_TIME = 1000;

    /**
     * 故障隔离（D2）：隔离期（联锁/待救援/救援中）对外呼派梯返回
     * {@link Double#MAX_VALUE} —— MTR 调度器取最小成本，故障梯永不出局；
     * 全场皆故障时 bestLift==null，呼叫静默丢弃。覆盖 MTR 原生/YTE/面板所有按钮路径。
     */
    @Inject(method = "pressButton", at = @At("HEAD"), cancellable = true)
    private void yte$rejectDispatchWhenIsolated(LiftInstruction instruction, boolean actuallyRegister,
                                                CallbackInfoReturnable<Double> cir) {
        if (LiftDoorControlState.isIsolated(((Lift) (Object) this).getId())) {
            cir.setReturnValue(Double.MAX_VALUE);
        }
    }

    /**
     * 清空前重派外呼：① 条目必须用各候选梯自己的同层高轨道坐标——本梯井道坐标
     * 对兄弟梯不可见；② 调度器一次 pressLift 只成交一笔，故逐条呼叫独立发起。
     * 隔离注入（pressButton 返回 MAX_VALUE）+ 显式过滤，双重保证故障梯绝不回流。
     */
    @Unique
    private void yte$redispatchHallCalls() {
        if (!(getData() instanceof Simulator)) {
            return;
        }
        final Simulator simulator = (Simulator) getData();
        final long selfId = ((Lift) (Object) this).getId();
        for (LiftInstruction instruction : getInstructions()) {
            final int floor = instruction.getFloor();
            if (instruction.getDirection() == LiftDirection.NONE || floor < 0 || floor >= getFloors().size()) {
                continue;
            }
            final long storeyY = getFloors().get(floor).getPosition().getY();
            final PressLift pressLift = new PressLift();
            for (final Lift other : simulator.lifts) {
                if (other.getId() == selfId || LiftDoorControlState.isIsolated(other.getId())) {
                    continue;
                }
                for (int i = 0; i < other.getFloorCount(); i++) {
                    final Position candidatePosition = ((MixinLiftSchema) other).getFloors().get(i).getPosition();
                    if (candidatePosition.getY() == storeyY) {
                        pressLift.add(candidatePosition, instruction.getDirection());
                        break;
                    }
                }
            }
            pressLift.pressLift(simulator);
        }
    }

    /** 救援模式：全部内外呼已在上锁时清空，此处仅就近平层，低速由运动分支钳制。 */
    @Unique
    private void yte$beginMaintenanceRecovery(LiftDoorControlState.DoorQueue queue) {
        // 以轿厢中心为判定基准（方案 A）：底部锚点会让“顶部已贴近上层”的情形误选下层
        final double carCenterProgress = getRailProgress() + ((Lift) (Object) this).getHeight() / 2.0;
        int nearestFloor = -1;
        double nearestDistance = Double.POSITIVE_INFINITY;
        for (int i = 0; i < getFloors().size(); i++) {
            final double distance = Math.abs(invokeGetProgress(i) - carCenterProgress);
            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearestFloor = i;
            }
        }
        if (nearestFloor < 0) {
            return;
        }
        getInstructions().add(0, new LiftInstruction(nearestFloor, LiftDirection.NONE));
        queue.recovering = true;
        setNeedsUpdate(true);
    }

    /**
     * Per-lift door curve: opening / fully open / closing / closed + RUN
     * window, parameterized by the lift's door config instead of MTR's fixed
     * 500/2100/4100/5700 hardcoded curve. Defaults reproduce MTR's values.
     */
    @Inject(method = "getDoorValue", at = @At("HEAD"), cancellable = true)
    private void yte$doorCurve(CallbackInfoReturnable<Float> cir) {
        final Lift lift = (Lift) (Object) this;
        final YteLiftConfigStore.DoorParams p = YteLiftConfigStore.getDoorParams(lift.getId());
        final long coolDown = getStoppingCoolDown();
        final double doorValue;
        switch (LiftDoorControlState.getDoorState(coolDown, p)) {
            case OPENING:
                doorValue = p.curve.apply((double) (p.total() - coolDown) / p.openMs);
                break;
            case FULLY_OPEN:
                doorValue = 1;
                break;
            case CLOSING:
                doorValue = p.curve.apply((double) (coolDown - p.runDelay) / p.closeMs);
                break;
            default: // CLOSED
                doorValue = 0;
        }
        // 客户端渲染值做一阶平滑，关门↔开门反转时呈减速→加速过渡，同步跳变也被吸收；
        // 服务端保持原始曲线值（门控判定依赖精确相位）
        cir.setReturnValue(isClientside()
                ? LiftDoorControlState.smoothDoorValue(lift.getId(), (float) doorValue)
                : (float) doorValue);
    }

    /**
     * Keep dispatching on MTR's original server-side direction while exposing a
     * persistent, elevator-style travel direction to every client display.
     */
    @Inject(method = "getDirection", at = @At("HEAD"), cancellable = true)
    private void yte$getDisplayDirection(CallbackInfoReturnable<LiftDirection> cir) {
        if (isClientside()) {
            cir.setReturnValue(yte$getDisplayDirection(DisplayDirectionMode.LATCH_UNTIL_DOOR_CLOSE));
        }
    }

    /**
     * @author YTE
     * @reason Replace MAX_SPEED and ACCELERATION_DEFAULT with per-lift custom values
     */
    @Overwrite
    public void tick(long millisElapsed) {
        if (isClientside() && MinecraftClient.getInstance().isPaused()) {
            return;
        }

        final long id = ((Lift) (Object) this).getId();
        final LiftDoorControlState.DoorQueue doorQueue = LiftDoorControlState.getOrCreate(id);
        final boolean movingDown = getSpeed() < 0 || getSpeed() == 0 && !getInstructions().isEmpty()
                && invokeGetProgress(getInstructions().get(0).getFloor()) < getRailProgress();
        double customMaxSpeed = YteLiftConfigStore.getSpeed(id, movingDown) / 1000.0;
        if (doorQueue.recovering) {
            // 救援限速：recoverySpeed（读端已 clamp [0.1,1.0] m/s）
            customMaxSpeed = Math.min(customMaxSpeed, YteLiftConfigStore.getRecoverySpeed(id) / 1000.0);
        }
        final double customAccel = YteLiftConfigStore.getAcceleration(id, movingDown) / 1_000_000.0;
        final double adoDistance = YteLiftConfigStore.getAdoDistance(id);
        final double levellingDistance = YteLiftConfigStore.getLevellingDistance(id);
        final double levellingSpeed = YteLiftConfigStore.getLevellingSpeed(id) / 1000.0;
        final LiftMotionProfile motionProfile = YteLiftConfigStore.getMotionProfile(id);

        if (!isClientside()) {
            if (doorQueue.pendingCommand != null) {
                yte$processDoorCommand(doorQueue, doorQueue.pendingCommand);
                doorQueue.pendingCommand = null;
            }
            yte$updateDoorState(doorQueue);
            yte$tickCloseTimer(doorQueue, millisElapsed);

            // 上锁/解锁后的指令清空（下一 tick 生效）：按钮灯随指令清除熄灭，
            // 并防止锁定期间旧的同层指令触发重开门；
            // 外呼（dir≠NONE）清空前按楼层坐标重派——隔离注入使故障梯绝不回流，
            // 自动落到同组健康梯，全场皆故障则静默丢弃；内呼直接抛弃
            if (doorQueue.instructionPurgePending) {
                doorQueue.instructionPurgePending = false;
                if (!getInstructions().isEmpty()) {
                    yte$redispatchHallCalls();
                    getInstructions().clear();
                    setNeedsUpdate(true);
                }
            }

            // 解锁后等待层门关门动画播完（closeMs，与动画共用配置时长）；
            // 空闲且已平层停靠：跳过救援保持原样待命（隔离随 pending 清除即刻解除）
            if (doorQueue.maintenanceRecoveryPending && !doorQueue.maintenanceLocked) {
                doorQueue.recoveryCloseDelayMs -= millisElapsed;
                if (doorQueue.recoveryCloseDelayMs <= 0) {
                    doorQueue.maintenanceRecoveryPending = false;
                    if (!getInstructions().isEmpty() || !yte$isExactlyAtFloor()) {
                        yte$beginMaintenanceRecovery(doorQueue);
                    }
                }
            }

            // 救援收尾：就近平层 + 开关门循环全部结束后恢复正常调度
            if (doorQueue.recovering && getInstructions().isEmpty()
                    && getStoppingCoolDown() == 0 && getSpeed() == 0) {
                doorQueue.recovering = false;
                setNeedsUpdate(true);
            }

            final Integer cancelledFloor = LiftFloorCancelState.consume(id);
            if (cancelledFloor != null && getSpeed() == 0 && yte$isExactlyAtFloor()
                    && cancelledFloor >= 0 && cancelledFloor < getFloors().size()) {
                final boolean instructionRemoved = getInstructions().removeIf(instruction ->
                        instruction.getFloor() == cancelledFloor && instruction.getDirection() == LiftDirection.NONE);
                if (instructionRemoved) {
                    setNeedsUpdate(true);
                }
            }

            // 电梯运行中：清空待执行门命令，避免到站后误执行
            if (getSpeed() != 0) {
                doorQueue.pendingCommand = null;
            }
        }

        final boolean adoLevelling = getStoppingCoolDown() > 0 && getSpeed() != 0 && !getInstructions().isEmpty();

        if (getStoppingCoolDown() > 0 && !adoLevelling) {
            setStoppingCoolDown(Math.max(getStoppingCoolDown() - millisElapsed, 0));
            if (getStoppingCoolDown() == 0) {
                if (isClientside()) {
                    setStoppingCoolDown(1);
                } else {
                    setNeedsUpdate(true);
                }
            }

            // 同层外呼在门循环中到达：立即消费指令并（反向）重开，不等门关死
            if (getSpeed() == 0 && !getInstructions().isEmpty()
                    && Math.abs(invokeGetProgress(getInstructions().get(0).getFloor()) - getRailProgress()) < 0.000001) {
                getInstructions().remove(0);
                if (!isClientside()) {
                    final YteLiftConfigStore.DoorParams p = YteLiftConfigStore.getDoorParams(id);
                    final float doorValue = Utilities.clamp(((Lift) (Object) this).getDoorValue(), 0, 1);
                    final long coolDown = getStoppingCoolDown();
                    if (coolDown <= p.runDelay) {
                        // 门关：开门
                        setStoppingCoolDown(p.total());
                    } else if (coolDown <= p.closeStartCoolDown() && doorValue < 1) {
                        // 关门中：反向续开
                        setStoppingCoolDown(p.total() - Math.round(p.curve.invert(doorValue) * p.openMs));
                    }
                    // 开门中/全开：保持开门即可
                    doorQueue.closeRemainingMs = yte$closeTimerValue(id);
                    setNeedsUpdate(true);
                    Init.sendLiftDoorOpen(id, getStoppingCoolDown(), true);
                }
            }
        } else if (LiftDoorControlState.getOrCreate(id).maintenanceLocked) {
            // 联锁：层门被钥匙手动打开 → 两端同步冻结速度；
            // 客户端不冻结会继续模拟、被服务端逐帧同步拽回（“闪回”）
            setSpeed(0);
            if (!isClientside()) {
                setNeedsUpdate(true);
            }
        } else {
            if (adoLevelling) {
                // 平层/预开门阶段：门开到全开后保持，避免徐行过慢时 cooldown 耗尽、停稳后二次开门
                setStoppingCoolDown(Math.max(getStoppingCoolDown() - millisElapsed,
                        YteLiftConfigStore.getDoorParams(id).fullOpenCoolDown()));
            }

            if (getInstructions().isEmpty()) {
                yte$motionTargetFloor = Integer.MIN_VALUE;
                yte$twoStageFineLevelling = false;
                yte$twoStageCoarseHoldRemaining = 0;
                setSpeed(Math.max(Math.abs(getSpeed()) - customAccel * millisElapsed, 0) * Math.signum(getSpeed()));
            } else {
                final int nextInstructionFloor = getInstructions().get(0).getFloor();
                final long nextInstructionProgress = invokeGetProgress(nextInstructionFloor);
                if (nextInstructionFloor != yte$motionTargetFloor) {
                    yte$motionTargetFloor = nextInstructionFloor;
                    yte$twoStageFineLevelling = false;
                    yte$twoStageCoarseHoldRemaining = 0;
                }
                final double distanceToTarget = Math.abs(nextInstructionProgress - getRailProgress());
                final double direction = Math.signum(nextInstructionProgress - getRailProgress());
                if (motionProfile == LiftMotionProfile.TWO_STAGE && yte$twoStageCoarseHoldRemaining > 0) {
                    setSpeed(LiftMotionProfile.TWO_STAGE_COARSE_STOP_SPEED * direction);
                    yte$twoStageCoarseHoldRemaining = Math.max(
                            yte$twoStageCoarseHoldRemaining - millisElapsed, 0);
                    if (yte$twoStageCoarseHoldRemaining == 0) {
                        yte$twoStageFineLevelling = true;
                    }
                } else {
                    final LiftMotionProfile.MotionResult motionResult = motionProfile.calculate(
                            new LiftMotionProfile.MotionContext(getSpeed(), customMaxSpeed, customAccel, distanceToTarget,
                                    levellingDistance, levellingSpeed, direction, millisElapsed,
                                    yte$twoStageFineLevelling));
                    setSpeed(motionResult.speed);
                    if (motionProfile == LiftMotionProfile.TWO_STAGE && motionResult.enterFineLevelling) {
                        yte$twoStageCoarseHoldRemaining = YTE_TWO_STAGE_COARSE_HOLD_TIME;
                    }
                }
                if (motionProfile != LiftMotionProfile.TWO_STAGE) {
                    yte$twoStageFineLevelling = false;
                    yte$twoStageCoarseHoldRemaining = 0;
                }

                final double updatedMovementThisTick = Math.abs(getSpeed() * millisElapsed);
                if (adoDistance > 0 && !isClientside() && !adoLevelling && getSpeed() != 0 && distanceToTarget <= adoDistance + updatedMovementThisTick) {
                    setStoppingCoolDown(YteLiftConfigStore.getDoorParams(id).total());
                    Init.sendLiftAdoStart(id, YteLiftConfigStore.getDoorParams(id).total());
                }

                if (Math.abs(getRailProgress() - nextInstructionProgress) <= Math.abs(getSpeed() * millisElapsed)) {
                    setRailProgress(nextInstructionProgress);
                    setSpeed(0);
                    // 客户端镜像同步移除指令，否则镜像会用自己的 coolDown==0 分支反复重开门
                    getInstructions().remove(0);
                    yte$motionTargetFloor = Integer.MIN_VALUE;
                    yte$twoStageFineLevelling = false;
                    yte$twoStageCoarseHoldRemaining = 0;
                    if (!isClientside()) {
                        if (getStoppingCoolDown() == 0) {
                            setStoppingCoolDown(YteLiftConfigStore.getDoorParams(id).total()
                                    + (adoDistance <= 0 ? YTE_BRAKE_HOLD_TIME : 0));
                        }
                        setNeedsUpdate(true);
                    }
                }
            }

            setRailProgress(Utilities.clamp(getRailProgress() + getSpeed() * millisElapsed, 0, invokeGetProgress(Integer.MAX_VALUE)));
        }

        if (isClientside()) {
            yte$updateDisplayFacts(levellingDistance);
            yte$updateDisplayDirection(millisElapsed);
        }

        if (getData() instanceof Simulator) {
            ((Simulator) getData()).clients.forEach(client -> {
                if (Utilities.isBetween(client.getPosition(), getMinPosition(), getMaxPosition(), client.getUpdateRadius())) {
                    client.update((Lift) (Object) this, getNeedsUpdate());
                }
            });

            setNeedsUpdate(false);
        }
    }

    @Unique
    private void yte$processDoorCommand(LiftDoorControlState.DoorQueue queue, LiftDoorControlState.Command command) {
        final Lift lift = (Lift) (Object) this;
        final long id = lift.getId();

        if (getSpeed() != 0 || !yte$isExactlyAtFloor()) {
            return;
        }

        final YteLiftConfigStore.DoorParams p = YteLiftConfigStore.getDoorParams(id);
        final long coolDown = getStoppingCoolDown();
        final float doorValue = Utilities.clamp(lift.getDoorValue(), 0, 1);
        final LiftDoorControlState.DoorState phase = LiftDoorControlState.getDoorState(coolDown, p);

        if (command == LiftDoorControlState.Command.CLOSE) {
            // 全开相位按下：立即进入关门段，跳过保持时长；
            // 开门中/关门中/门关：忽略（现实电梯开门过程中按关门键无效，门完成开门循环后由保持计时器自动关）
            if (phase == LiftDoorControlState.DoorState.FULLY_OPEN) {
                setStoppingCoolDown(p.closeStartCoolDown());
                setNeedsUpdate(true);
            }
            return;
        }

        if (command == LiftDoorControlState.Command.HOLD_OPEN
                && (phase == LiftDoorControlState.DoorState.CLOSED || !YteLiftConfigStore.isDoorHoldEnabled(id))) {
            return;
        }

        final boolean startingIdleDoorCycle = command == LiftDoorControlState.Command.OPEN
                && getInstructions().isEmpty()
                && coolDown < p.runDelay;
        if (coolDown <= p.runDelay) {
            setStoppingCoolDown(p.total());
            queue.closeRemainingMs = yte$closeTimerValue(id);
            Init.sendLiftDoorOpen(id, getStoppingCoolDown(), startingIdleDoorCycle);
            setNeedsUpdate(true);
        } else if (phase == LiftDoorControlState.DoorState.FULLY_OPEN) {
            setStoppingCoolDown(p.fullOpenCoolDown());
            queue.closeRemainingMs = yte$closeTimerValue(id);
            Init.sendLiftDoorOpen(id, getStoppingCoolDown(), false);
            setNeedsUpdate(true);
        } else if (phase == LiftDoorControlState.DoorState.CLOSING) {
            // 关门中：反向续开
            setStoppingCoolDown(p.total() - Math.round(p.curve.invert(doorValue) * p.openMs));
            queue.closeRemainingMs = yte$closeTimerValue(id);
            Init.sendLiftDoorOpen(id, getStoppingCoolDown(), false);
            setNeedsUpdate(true);
        } else {
            // 开门中：保持开门即可
            queue.closeRemainingMs = yte$closeTimerValue(id);
        }
    }

    /** -1（消防/专用模式）表示无限开门；否则为配置的开门保持时长。 */
    @Unique
    private static long yte$closeTimerValue(long id) {
        return LiftStateManager.isFireMode(id)
                ? LiftDoorControlState.INFINITE_OPEN
                : YteLiftConfigStore.getDoorDwellMs(id);
    }

    /** 由 coolDown 推导门状态；转入 FULLY_OPEN / CLOSED 即「门已完全打开/关闭」事件。 */
    @Unique
    private void yte$updateDoorState(LiftDoorControlState.DoorQueue queue) {
        final Lift lift = (Lift) (Object) this;
        final YteLiftConfigStore.DoorParams p = YteLiftConfigStore.getDoorParams(lift.getId());
        final long coolDown = getStoppingCoolDown();
        final LiftDoorControlState.DoorState state = LiftDoorControlState.getDoorState(coolDown, p);

        if (state != queue.doorState) {
            queue.doorState = state;
            if (state == LiftDoorControlState.DoorState.FULLY_OPEN) {
                // 「门已完全打开」事件：启动关门计时器
                queue.closeRemainingMs = yte$closeTimerValue(lift.getId());
            }
        }
    }

    @Unique
    private void yte$tickCloseTimer(LiftDoorControlState.DoorQueue queue, long millisElapsed) {
        if (queue.doorState != LiftDoorControlState.DoorState.FULLY_OPEN) {
            return;
        }
        final YteLiftConfigStore.DoorParams p = YteLiftConfigStore.getDoorParams(((Lift) (Object) this).getId());
        if (queue.closeRemainingMs == LiftDoorControlState.INFINITE_OPEN) {
            // 无限开门：把冷却钉在全开位置
            if (getStoppingCoolDown() < p.fullOpenCoolDown()) {
                setStoppingCoolDown(p.fullOpenCoolDown());
                setNeedsUpdate(true);
            }
            return;
        }
        queue.closeRemainingMs -= millisElapsed;
        if (queue.closeRemainingMs <= 0) {
            queue.closeRemainingMs = LiftDoorControlState.INFINITE_OPEN;
            setStoppingCoolDown(p.closeStartCoolDown());
            setNeedsUpdate(true);
        }
    }

    @Unique
    private boolean yte$isExactlyAtFloor() {
        for (int i = 0; i < getFloors().size(); i++) {
            if (Math.abs(getRailProgress() - invokeGetProgress(i)) < 0.000001) {
                return true;
            }
        }
        return false;
    }

    @Unique
    private void yte$updateDisplayFacts(double levellingDistance) {
        final Lift lift = (Lift) (Object) this;
        final boolean moving = getSpeed() != 0;
        final LiftDirection movementDirection = getSpeed() > 0
                ? LiftDirection.UP
                : getSpeed() < 0 ? LiftDirection.DOWN : LiftDirection.NONE;
        int targetFloor = -1;
        double distanceToTarget = Double.POSITIVE_INFINITY;
        LiftDirection targetDirection = LiftDirection.NONE;

        if (!getInstructions().isEmpty()) {
            final LiftInstruction instruction = getInstructions().get(0);
            targetFloor = instruction.getFloor();
            final double difference = invokeGetProgress(targetFloor) - getRailProgress();
            distanceToTarget = Math.abs(difference);
            targetDirection = difference > 0
                    ? LiftDirection.UP
                    : difference < 0
                    ? LiftDirection.DOWN
                    : instruction.getDirection();
        }

        final boolean doorCycle = getStoppingCoolDown() > 1 || lift.getDoorValue() != 0;
        final boolean levelling = moving && levellingDistance > 0 && distanceToTarget <= levellingDistance;
        final boolean idle = !moving && getInstructions().isEmpty() && !doorCycle;
        final int displayedFloor = lift.getFloorIndex(lift.getCurrentFloor().getPosition());

        LiftDisplayState.get(lift.getId()).update(
                movementDirection, targetDirection, moving, levelling, doorCycle, idle,
                displayedFloor, targetFloor, getSpeed(), distanceToTarget, getStoppingCoolDown());
    }

    @Unique
    private void yte$updateDisplayDirection(long millisElapsed) {
        final Lift lift = (Lift) (Object) this;
        final LiftDisplayDirectionState displayState = LiftDisplayDirectionState.get(lift.getId());
        final int instructionCount = getInstructions().size();
        final boolean instructionAdded = instructionCount > displayState.previousInstructionCount;
        displayState.previousInstructionCount = instructionCount;
        final int floorCount = getFloors().size();
        final int displayedFloorIndex = lift.getFloorIndex(lift.getCurrentFloor().getPosition());

        final boolean activeDirectionCycle = getSpeed() != 0
                || getStoppingCoolDown() > 1
                || !getInstructions().isEmpty();
        final boolean doorCycleActive = getStoppingCoolDown() > 1 || lift.getDoorValue() != 0;

        if (doorCycleActive && displayState.sameFloorCallDirection == LiftDirection.NONE) {
            final LiftDirection claimedSameFloorCallDirection =
                    LiftDisplayDirectionState.claimPendingSameFloorCall(lift.getId());
            if (claimedSameFloorCallDirection != LiftDirection.NONE) {
                displayState.setSameFloorCallDirection(claimedSameFloorCallDirection);
            }
        }

        if (getSpeed() != 0) {
            displayState.movedSinceIdle = true;
        } else if (!activeDirectionCycle) {
            displayState.movedSinceIdle = false;
        }

        if (!doorCycleActive) {
            displayState.deferredSameFloorCallDirection = LiftDirection.NONE;
        }

        if (displayState.sameFloorCallDirection != LiftDirection.NONE) {
            displayState.sameFloorCallWaitMillis += millisElapsed;
            if (doorCycleActive) {
                displayState.sameFloorCallDoorCycleStarted = true;
            }

            if (doorCycleActive) {
                displayState.direction = displayState.sameFloorCallDirection;
                return;
            }

            if (!displayState.sameFloorCallDoorCycleStarted && displayState.sameFloorCallWaitMillis < 10000) {
                displayState.direction = displayState.sameFloorCallDirection;
                return;
            }

            displayState.sameFloorCallDirection = LiftDirection.NONE;
            displayState.sameFloorCallDoorCycleStarted = false;
            displayState.sameFloorCallWaitMillis = 0;
        }

        if (!getInstructions().isEmpty()) {
            final LiftInstruction instruction = getInstructions().get(0);
            if (displayedFloorIndex == instruction.getFloor()) {
                final boolean terminalTurnaround = displayState.movedSinceIdle
                        || instruction.getDirection() != LiftDirection.NONE;
                final LiftDirection arrivalDirection = terminalTurnaround && displayedFloorIndex == floorCount - 1
                        ? LiftDirection.DOWN
                        : terminalTurnaround && displayedFloorIndex == 0
                        ? LiftDirection.UP
                        : instruction.getDirection();
                final boolean deferDirectionChange = doorCycleActive
                        && displayState.deferredSameFloorCallDirection != LiftDirection.NONE;
                if (!deferDirectionChange && (displayState.arrivalFloor != displayedFloorIndex
                        || displayState.arrivalDirection != arrivalDirection)) {
                    displayState.arrivalFloor = displayedFloorIndex;
                    displayState.arrivalDirection = arrivalDirection;
                    displayState.arrivalMillis = 0;
                }
            }
        }

        if (displayState.arrivalFloor >= 0 && displayedFloorIndex != displayState.arrivalFloor) {
            yte$resetArrivalDirectionDelay();
        } else if (activeDirectionCycle && displayState.arrivalFloor >= 0) {
            displayState.arrivalMillis += millisElapsed;
            final boolean currentInstructionIsArrival = !getInstructions().isEmpty()
                    && getInstructions().get(0).getFloor() == displayState.arrivalFloor;
            final boolean retainArrivalDirection = getStoppingCoolDown() > 1 || currentInstructionIsArrival;
            if (retainArrivalDirection
                    && displayState.arrivalMillis >= YTE_ARRIVAL_DIRECTION_DELAY
                    && displayState.arrivalDirection != LiftDirection.NONE) {
                displayState.direction = displayState.arrivalDirection;
                return;
            }
        }

        // During ADO and the complete door cycle, retain the arrival direction.
        // Client lifts keep a value of 1 as the completed sync sentinel.
        // Only larger values represent an active ADO/door cycle.
        if (getStoppingCoolDown() > 1) {
            if (instructionAdded && displayState.direction == LiftDirection.NONE) {
                yte$setDisplayDirectionFromNextInstruction(displayState);
            }
            return;
        }

        if (getInstructions().isEmpty()) {
            displayState.direction = LiftDirection.NONE;
            return;
        }

        yte$setDisplayDirectionFromNextInstruction(displayState);
    }

    @Unique
    private void yte$setDisplayDirectionFromNextInstruction(LiftDisplayDirectionState displayState) {
        final LiftInstruction instruction = getInstructions().get(0);
        final double difference = invokeGetProgress(instruction.getFloor()) - getRailProgress();
        displayState.direction = difference > 0 ? LiftDirection.UP
                : difference < 0 ? LiftDirection.DOWN
                : instruction.getDirection() != LiftDirection.NONE
                ? instruction.getDirection()
                : displayState.direction;
    }

    @Override
    public void yte$resetArrivalDirectionDelay() {
        final LiftDisplayDirectionState displayState = LiftDisplayDirectionState.get(((Lift) (Object) this).getId());
        displayState.arrivalFloor = -1;
        displayState.arrivalDirection = LiftDirection.NONE;
        displayState.arrivalMillis = 0;
    }

    @Override
    public LiftDisplayState yte$getDisplayState() {
        return LiftDisplayState.get(((Lift) (Object) this).getId());
    }

}
