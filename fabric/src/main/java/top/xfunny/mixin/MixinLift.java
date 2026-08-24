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
import top.xfunny.mod.lift.LiftDoorState;
import top.xfunny.mod.lift.LiftModeState;
import top.xfunny.mod.lift.LiftFloorCancelState;
import top.xfunny.mod.lift.DisplayDirectionMode;
import top.xfunny.mod.lift.LiftDisplayState;
import top.xfunny.mod.lift.LiftMotionProfile;

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
     * 故障隔离（D2）：隔离期（联锁/待救援/救援中/消防迫降/急停）对呼叫派梯返回
     * {@link Double#MAX_VALUE} —— MTR 调度器取最小成本，故障梯永不出局；
     * 全场皆故障时 bestLift==null，呼叫静默丢弃。覆盖 MTR 原生/YTE/面板所有按钮路径。
     * 外呼与内呼分开判定：消防员模式下放行内呼、拒绝外呼，其余隔离态全部拒绝。
     */
    @Inject(method = "pressButton", at = @At("HEAD"), cancellable = true)
    private void yte$rejectDispatchWhenIsolated(LiftInstruction instruction, boolean actuallyRegister,
                                                CallbackInfoReturnable<Double> cir) {
        final long liftId = ((Lift) (Object) this).getId();
        final boolean rejected = instruction.getDirection() != LiftDirection.NONE
                ? !LiftModeState.canAcceptHallCall(liftId)
                : !LiftModeState.canAcceptCarCall(liftId);
        if (rejected) {
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
                if (other.getId() == selfId || !LiftModeState.canAcceptHallCall(other.getId())) {
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

    /** 原始门值（服务端四段公式，无客户端平滑），供门值渲染与光幕门控共用。 */
    @Unique
    private static double yte$rawDoorValue(long coolDown, YteLiftConfigStore.DoorParams p) {
        switch (LiftDoorState.getDoorState(coolDown, p)) {
            case OPENING:
                return p.curve.apply((double) (p.total() - coolDown) / p.openMs);
            case FULLY_OPEN:
                return 1;
            case CLOSING:
                return p.curve.apply((double) (coolDown - p.runDelay) / p.closeMs);
            default: // CLOSED
                return 0;
        }
    }

    /** 模式移动（救援等）：全部内外呼已在进入模式时清空，此处仅就近平层，低速由运动分支钳制。 */
    @Unique
    private void yte$beginModeMovement(LiftModeState.State modeState) {
        // 以轿厢中心为判定基准（方案 A）：底部锚点会让“顶部已贴近上层”的情形误选下层
        final double carCenterProgress = getRailProgress() + ((Lift) (Object) this).getHeight() / 2.0;
        int nearestFloor = -1;
        double nearestDistance = Double.POSITIVE_INFINITY;
        // TODO: modeTargetFloor 已预留（消防迫降等指定楼层模式）；当前所有模式一律就近平层
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
        modeState.modeActive = true;
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
        switch (LiftDoorState.getDoorState(coolDown, p)) {
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
                ? LiftDoorState.smoothDoorValue(lift.getId(), (float) doorValue)
                : (float) doorValue);
    }

    /**
     * Keep dispatching on MTR's original server-side direction while exposing a
     * persistent, elevator-style travel direction to every client display.
     */
    @Inject(method = "getDirection", at = @At("HEAD"), cancellable = true)
    private void yte$getDisplayDirection(CallbackInfoReturnable<LiftDirection> cir) {
        if (isClientside()) {
            // 模式运动期旁路：不拦截，MTR 原版 getDirection() 直通
            // （有目标=行进方向；到站/无目标=NONE），箭头与常规运行一致
            if (!LiftModeState.getOrCreate(((Lift) (Object) this).getId()).modeActive) {
                cir.setReturnValue(yte$getDisplayDirection(DisplayDirectionMode.LATCH_UNTIL_DOOR_CLOSE));
            }
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
        final LiftDoorState.DoorQueue doorQueue = LiftDoorState.getOrCreate(id);
        final LiftModeState.State modeState = LiftModeState.getOrCreate(id);
        final boolean movingDown = getSpeed() < 0 || getSpeed() == 0 && !getInstructions().isEmpty()
                && invokeGetProgress(getInstructions().get(0).getFloor()) < getRailProgress();
        double customMaxSpeed = YteLiftConfigStore.getSpeed(id, movingDown) / 1000.0;
        if (modeState.modeActive && modeState.mode == LiftModeState.LiftMode.MANUAL_DOOR_RECOVERY) {
            // 救援限速：recoverySpeed（读端已 clamp [0.1,1.0] m/s）；其余模式正常速度
            customMaxSpeed = Math.min(customMaxSpeed, YteLiftConfigStore.getRecoverySpeed(id) / 1000.0);
        }
        final double customAccel = YteLiftConfigStore.getAcceleration(id, movingDown) / 1_000_000.0;
        final double adoDistance = YteLiftConfigStore.getAdoDistance(id);
        final double levellingDistance = YteLiftConfigStore.getLevellingDistance(id);
        final double levellingSpeed = YteLiftConfigStore.getLevellingSpeed(id) / 1000.0;
        final LiftMotionProfile motionProfile = YteLiftConfigStore.getMotionProfile(id);

        if (!isClientside()) {
            // 锁定 / 待进入模式 / 模式执行中：整梯不接入光幕，清空残留遮挡状态
            if (modeState.maintenanceLocked || modeState.modePending || modeState.modeActive) {
                doorQueue.curtainFlags = 0;
                doorQueue.obstructionUntilMillis = 0;
            } else {
                // 光幕安全网：CLOSING 全程持续门控（300ms 心跳维持）——
                // 接触面板（从方块边缘向内推进的前沿）立即反向续开；盲区内保持待命、自然关死
                final boolean curtainTouch = (doorQueue.curtainFlags & LiftDoorState.CURTAIN_TOUCH) != 0;
                final boolean curtainActive =
                        System.currentTimeMillis() < doorQueue.obstructionUntilMillis || curtainTouch;
                if (curtainActive && !doorQueue.forcedClosing) {
                    final YteLiftConfigStore.DoorParams cp = YteLiftConfigStore.getDoorParams(id);
                    final LiftDoorState.DoorState curtainPhase =
                            LiftDoorState.getDoorState(getStoppingCoolDown(), cp);
                    if (curtainPhase == LiftDoorState.DoorState.CLOSING) {
                        if (curtainTouch && yte$rawDoorValue(getStoppingCoolDown(), cp)
                                >= LiftDoorState.CURTAIN_MIN_DOOR_VALUE) {
                            yte$processDoorCommand(doorQueue, LiftDoorState.Command.OPEN);
                        }
                    } else if (curtainPhase != LiftDoorState.DoorState.CLOSING) {
                        // 离开接触区：清除 TOUCH 位
                        doorQueue.curtainFlags &= ~LiftDoorState.CURTAIN_TOUCH;
                    }
                }
            }
            if (doorQueue.pendingCommand != null) {
                yte$processDoorCommand(doorQueue, doorQueue.pendingCommand);
                doorQueue.pendingCommand = null;
            }
            yte$updateDoorState(doorQueue);
            yte$tickCloseTimer(doorQueue, millisElapsed);

            // 上锁/进入模式后的指令清空（下一 tick 生效）：按钮灯随指令清除熄灭，
            // 并防止隔离期间旧的同层指令触发重开门；
            // 外呼（dir≠NONE）清空前按楼层坐标重派——隔离注入使故障梯绝不回流，
            // 自动落到同组健康梯，全场皆故障则静默丢弃；内呼直接抛弃
            if (modeState.instructionPurgePending) {
                modeState.instructionPurgePending = false;
                if (!getInstructions().isEmpty()) {
                    yte$redispatchHallCalls();
                    getInstructions().clear();
                    setNeedsUpdate(true);
                }
            }

            // 模式倒计时：解锁/触发后等待前置条件（如层门关门动画播完），再启动模式移动；
            // 空闲且已平层停靠：无需移动——需自动退出的模式（救援）即刻退出待命
            if (modeState.modePending && !modeState.maintenanceLocked) {
                modeState.modeDelayMs -= millisElapsed;
                if (modeState.modeDelayMs <= 0) {
                    modeState.modePending = false;
                    if (!getInstructions().isEmpty() || !yte$isExactlyAtFloor()) {
                        yte$beginModeMovement(modeState);
                    } else if (modeState.mode.shouldAutoExit()) {
                        LiftModeState.exitMode(id);
                    }
                }
            }

            // 模式收尾：就近平层 + 开关门循环全部结束后退出执行态
            // （exitMode 内部按 shouldAutoExit 决定是否回到 NORMAL）
            if (modeState.modeActive && getInstructions().isEmpty()
                    && getStoppingCoolDown() == 0 && getSpeed() == 0) {
                LiftModeState.exitMode(id);
                setNeedsUpdate(true);
            }

            // 超时强关自愈：门已关死（CLOSED 相位）即解除强关隔离
            if (doorQueue.forcedClosing
                    && LiftDoorState.getDoorState(getStoppingCoolDown(),
                            YteLiftConfigStore.getDoorParams(id)) == LiftDoorState.DoorState.CLOSED) {
                doorQueue.forcedClosing = false;
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
        } else if (modeState.maintenanceLocked) {
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
                        // 新停靠周期：重置光幕最大开门时长并解除超时抑制
                        doorQueue.maxOpenRemainingMs = YteLiftConfigStore.getMaxDoorOpenMs(id);
                        doorQueue.curtainSuppressed = false;
                        doorQueue.obstructionUntilMillis = 0;
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
    private void yte$processDoorCommand(LiftDoorState.DoorQueue queue, LiftDoorState.Command command) {
        final Lift lift = (Lift) (Object) this;
        final long id = lift.getId();

        // 超时强关阶段：开门/保持类指令全部失效，仅允许 CLOSE
        if (queue.forcedClosing && command != LiftDoorState.Command.CLOSE) {
            return;
        }

        // 锁定 / 模式过渡与执行期间不接受门命令；
        // 隔离模式常驻期（如消防迫降未开消防员）同样拒绝，防止门被意外关闭
        final LiftModeState.State modeState = LiftModeState.getOrCreate(id);
        if (modeState.maintenanceLocked || modeState.modePending || modeState.modeActive) {
            return;
        }
        if (modeState.mode.isolates() && !modeState.firefighterMode) {
            return;
        }

        if (getSpeed() != 0 || !yte$isExactlyAtFloor()) {
            return;
        }

        final YteLiftConfigStore.DoorParams p = YteLiftConfigStore.getDoorParams(id);
        final long coolDown = getStoppingCoolDown();
        final float doorValue = Utilities.clamp(lift.getDoorValue(), 0, 1);
        final LiftDoorState.DoorState phase = LiftDoorState.getDoorState(coolDown, p);

        if (command == LiftDoorState.Command.CLOSE) {
            // 光幕（Q2甲）：遮挡生效期内手动关门同样被拦截
            if (System.currentTimeMillis() < queue.obstructionUntilMillis && !queue.curtainSuppressed) {
                return;
            }
            // 全开相位按下：立即进入关门段，跳过保持时长；
            if (phase == LiftDoorState.DoorState.FULLY_OPEN) {
                setStoppingCoolDown(p.closeStartCoolDown());
                setNeedsUpdate(true);
            }
            return;
        }

        if (command == LiftDoorState.Command.HOLD_OPEN
                && (phase == LiftDoorState.DoorState.CLOSED || !YteLiftConfigStore.isDoorHoldEnabled(id))) {
            return;
        }

        final boolean startingIdleDoorCycle = command == LiftDoorState.Command.OPEN
                && getInstructions().isEmpty()
                && coolDown < p.runDelay;
        if (coolDown <= p.runDelay) {
            setStoppingCoolDown(p.total());
            queue.closeRemainingMs = yte$closeTimerValue(id);
            queue.maxOpenRemainingMs = YteLiftConfigStore.getMaxDoorOpenMs(id);
            queue.curtainSuppressed = false;
            Init.sendLiftDoorOpen(id, getStoppingCoolDown(), startingIdleDoorCycle);
            setNeedsUpdate(true);
        } else if (phase == LiftDoorState.DoorState.FULLY_OPEN) {
            setStoppingCoolDown(p.fullOpenCoolDown());
            queue.closeRemainingMs = yte$closeTimerValue(id);
            Init.sendLiftDoorOpen(id, getStoppingCoolDown(), false);
            setNeedsUpdate(true);
        } else if (phase == LiftDoorState.DoorState.CLOSING) {
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

    /** -1（消防迫降）表示无限开门；否则为配置的开门保持时长。 */
    @Unique
    private static long yte$closeTimerValue(long id) {
        return LiftModeState.isFireRecall(id)
                ? LiftDoorState.INFINITE_OPEN
                : YteLiftConfigStore.getDoorDwellMs(id);
    }

    /** 由 coolDown 推导门状态；转入 FULLY_OPEN / CLOSED 即「门已完全打开/关闭」事件。 */
    @Unique
    private void yte$updateDoorState(LiftDoorState.DoorQueue queue) {
        final Lift lift = (Lift) (Object) this;
        final YteLiftConfigStore.DoorParams p = YteLiftConfigStore.getDoorParams(lift.getId());
        final long coolDown = getStoppingCoolDown();
        final LiftDoorState.DoorState state = LiftDoorState.getDoorState(coolDown, p);

        if (state != queue.doorState) {
            queue.doorState = state;
            if (state == LiftDoorState.DoorState.FULLY_OPEN) {
                // 「门已完全打开」事件：启动关门计时器
                queue.closeRemainingMs = yte$closeTimerValue(lift.getId());
                if (LiftModeState.getOrCreate(lift.getId()).modeActive) {
                    // 完全开门即恢复服务（不等关门）：退出模式执行态 + 清理强关残留
                    LiftModeState.exitMode(lift.getId());
                    yte$resetArrivalDirectionDelay();
                    setNeedsUpdate(true);
                }
            }
        }
    }

    @Unique
    private void yte$tickCloseTimer(LiftDoorState.DoorQueue queue, long millisElapsed) {
        if (queue.doorState != LiftDoorState.DoorState.FULLY_OPEN) {
            return;
        }
        final YteLiftConfigStore.DoorParams p = YteLiftConfigStore.getDoorParams(((Lift) (Object) this).getId());
        if (queue.closeRemainingMs == LiftDoorState.INFINITE_OPEN) {
            // 无限开门：把冷却钉在全开位置
            if (getStoppingCoolDown() < p.fullOpenCoolDown()) {
                setStoppingCoolDown(p.fullOpenCoolDown());
                setNeedsUpdate(true);
            }
            return;
        }

        // 最大开门时长：保持期无条件递减（光幕不能无限续命），归零强制关门
        if (queue.maxOpenRemainingMs > 0) {
            queue.maxOpenRemainingMs = Math.max(queue.maxOpenRemainingMs - millisElapsed, 0);
        }
        if (queue.maxOpenRemainingMs == 0 && !queue.forcedClosing) {
            // 超时强关：本周期抑制光幕与开门请求，门全闭后解除
            queue.forcedClosing = true;
            queue.curtainSuppressed = true;
            setStoppingCoolDown(p.closeStartCoolDown());
            setNeedsUpdate(true);
            return;
        }

        queue.closeRemainingMs -= millisElapsed;
        if (queue.closeRemainingMs <= 0) {
            // 光幕预检：发出关门指令前先查门区是否有人；
            // 有人则暂缓发指令（置 1 下 tick 重查），门保持全开、零关门动画
            if (System.currentTimeMillis() < queue.obstructionUntilMillis && !queue.curtainSuppressed) {
                queue.closeRemainingMs = 1;
                return;
            }
            queue.closeRemainingMs = LiftDoorState.INFINITE_OPEN;
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
        // 模式运动期跳过自定义闩锁更新：箭头由 MTR 原版 getDirection() 驱动（见 getDisplayDirection 旁路）
        if (LiftModeState.getOrCreate(lift.getId()).modeActive) {
            return;
        }
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
