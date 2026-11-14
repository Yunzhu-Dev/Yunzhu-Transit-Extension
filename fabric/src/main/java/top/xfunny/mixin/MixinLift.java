package top.xfunny.mixin;

import org.mtr.core.data.*;
import org.mtr.core.data.Lift;
// import org.mtr.core.generated.data.LiftSchema;
import org.mtr.core.serializer.ReaderBase;
// import org.mtr.core.serializer.WriterBase;
import org.mtr.core.simulation.Simulator;
import org.mtr.core.tool.Utilities;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
// import top.xfunny.core.data.YteLiftConfig;
import top.xfunny.mod.config.YteLiftConfigStore;
import top.xfunny.mod.Init;
import top.xfunny.mod.lift.LiftArrivalLanternState;
import top.xfunny.mod.lift.LiftDisplayDirection;
import top.xfunny.mod.lift.LiftDisplayDirectionState;
import top.xfunny.mod.lift.LiftDoorControlState;
import top.xfunny.mod.lift.LiftFloorCancelState;
import top.xfunny.mod.lift.DisplayDirectionMode;
import top.xfunny.mod.lift.LiftDisplayState;
import top.xfunny.mod.lift.LiftMotionProfile;
import top.xfunny.mod.lift.LiftServiceMode;

@Mixin(value = Lift.class, remap = false)
public abstract class MixinLift /* extends LiftSchema */ implements MixinLiftSchema, MixinLiftFields, MixinNameColorDataBaseSchema, LiftDisplayDirection {

    @Unique
    private static final long YTE_LIFT_STOPPING_TIME = Vehicle.DOOR_MOVE_TIME + 2500;

    @Unique
    private static final long YTE_ARRIVAL_DIRECTION_DELAY = 100;

    @Unique
    private static final double YTE_ARRIVAL_DIRECTION_DISTANCE = 5.0;

    @Unique
    private static final long YTE_DOOR_CLOSED_DELAY = 500;

    @Unique
    private static final long YTE_SINGLE_DOOR_MOVE_TIME = Vehicle.DOOR_MOVE_TIME / 2;

    /** 门完全打开时对应的 stoppingCoolDown（= 停站时间 - 单次开门时间） */
    @Unique
    private static final long YTE_DOOR_FULL_OPEN_COOL_DOWN = YTE_LIFT_STOPPING_TIME - YTE_SINGLE_DOOR_MOVE_TIME;

    @Unique
    private static final long YTE_DOOR_CLOSE_PROTECTION_TIME = 300;

    @Unique
    private int yte$motionTargetFloor = Integer.MIN_VALUE;

    @Unique
    private boolean yte$twoStageFineLevelling;

    @Unique
    private long yte$twoStageCoarseHoldRemaining;

    /* Reverse levelling temporarily disabled.
    @Unique
    private int yte$reverseApproachDirection;

    @Unique
    private double yte$reverseTurnProgress;

    @Unique
    private boolean yte$reverseReturning;
    */
    @Unique
    private long yte$doorCoolDownBeforeUpdate;

    @Inject(method = "updateData", at = @At("HEAD"))
    private void yte$rememberDoorAnimation(ReaderBase reader, CallbackInfo ci) {
        yte$doorCoolDownBeforeUpdate = getStoppingCoolDown();
    }

    @Unique
    private static final long YTE_TWO_STAGE_COARSE_HOLD_TIME = 1000;

    @Unique
    private LiftServiceMode yte$lastServiceMode;

    /* Reverse levelling temporarily disabled.
    protected MixinLift(TransportMode transportMode, Data data) {
        super(transportMode, data);
    }

    @Inject(method = "getCurrentFloor", at = @At("HEAD"), cancellable = true)
    private void yte$terminalFloorOutsideRail(CallbackInfoReturnable<LiftFloor> cir) {
        if (!getFloors().isEmpty()) {
            if (getRailProgress() < 0) {
                cir.setReturnValue(getFloors().get(0));
            } else if (getRailProgress() > invokeGetProgress(Integer.MAX_VALUE)) {
                cir.setReturnValue(getFloors().get(getFloors().size() - 1));
            }
        }
    }

    @Inject(method = "getPosition", at = @At("HEAD"), cancellable = true)
    private void yte$terminalOvershootPosition(
            java.util.function.BiFunction<Position, Position,
                    org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArrayList<org.mtr.core.tool.Vector>> path,
            CallbackInfoReturnable<org.mtr.core.tool.Vector> cir) {
        final double progress = getRailProgress();
        final double boundary = Utilities.clamp(progress, 0, invokeGetProgress(Integer.MAX_VALUE));
        if (progress != boundary && getFloors().size() >= 2) {
            // Sample the existing rendered path at its endpoint and just inside it.
            // Extending that tangent also supports terminals on non-vertical paths.
            final double sample = Math.min(0.01, invokeGetProgress(Integer.MAX_VALUE));
            final double inward = progress < 0 ? sample : -sample;
            final Lift lift = (Lift) (Object) this;
            try {
                setRailProgress(boundary);
                final org.mtr.core.tool.Vector endpoint = lift.getPosition(path);
                setRailProgress(boundary + inward);
                final org.mtr.core.tool.Vector inside = lift.getPosition(path);
                final double scale = (progress - boundary) / inward;
                cir.setReturnValue(endpoint.add((inside.x - endpoint.x) * scale,
                        (inside.y - endpoint.y) * scale, (inside.z - endpoint.z) * scale));
            } finally {
                setRailProgress(progress);
            }
        }
    }

    @Override
    public void serializeData(WriterBase writer) {
        super.serializeData(writer);
        if (yte$reverseApproachDirection != 0) {
            writer.writeInt("yteReverseTargetFloor", yte$motionTargetFloor);
            writer.writeInt("yteReverseApproachDirection", yte$reverseApproachDirection);
            writer.writeDouble("yteReverseTurnProgress", yte$reverseTurnProgress);
            writer.writeBoolean("yteReverseReturning", yte$reverseReturning);
        }
    }
    */
    @Inject(method = "updateData", at = @At("TAIL"))
    private void yte$restoreDoorAnimation(ReaderBase reader, CallbackInfo ci) {
        // ADO events and regular snapshots travel independently. Once opening
        // starts, preserve its local timeline across opening/old closed snapshots.
        // A closing snapshot still takes effect normally.
        if (isClientside() && yte$doorCoolDownBeforeUpdate >= YTE_DOOR_FULL_OPEN_COOL_DOWN
                && (getStoppingCoolDown() >= YTE_DOOR_FULL_OPEN_COOL_DOWN || getStoppingCoolDown() <= 1)) {
            setStoppingCoolDown(yte$doorCoolDownBeforeUpdate);
        }
        /* Reverse levelling temporarily disabled.
        yte$reverseApproachDirection = reader.getInt("yteReverseApproachDirection", 0);
        yte$reverseReturning = reader.getBoolean("yteReverseReturning", false);
        if (yte$reverseApproachDirection != 0) {
            yte$motionTargetFloor = reader.getInt("yteReverseTargetFloor", Integer.MIN_VALUE);
            yte$reverseTurnProgress = reader.getDouble("yteReverseTurnProgress", 0);
        }
        */
    }

    @Inject(method = "pressButton", at = @At("HEAD"), cancellable = true)
    private void yte$rejectHallCallsInServiceMode(
            LiftInstruction instruction, boolean addInstruction, CallbackInfoReturnable<Double> cir) {
        if (!isClientside() && instruction.getDirection() != LiftDirection.NONE
                && !YteLiftConfigStore.getServiceMode(((Lift) (Object) this).getId()).acceptsHallCalls()) {
            cir.setReturnValue(Double.MAX_VALUE);
        }
    }

    /**
     * MTR's door curve becomes negative when the cooldown is extended beyond its
     * native stopping time. Keep the doors fully closed during the opening delay.
     */
    @Inject(method = "getDoorValue", at = @At("RETURN"), cancellable = true)
    private void yte$clampDelayedDoorValue(CallbackInfoReturnable<Float> cir) {
        final Lift lift = (Lift) (Object) this;
        final float doorValue = Math.max(cir.getReturnValue(), 0F);
        cir.setReturnValue(isClientside()
                ? LiftDoorControlState.preserveClientOpenDoorValue(lift.getId(), doorValue)
                : doorValue);
    }

    /**
     * Keep dispatching on MTR's original server-side direction while exposing a
     * persistent, elevator-style travel direction to every client display.
     */
    @Inject(method = "getDirection", at = @At("HEAD"), cancellable = true)
    private void yte$getDisplayDirection(CallbackInfoReturnable<LiftDirection> cir) {
        if (isClientside()) {
            final LiftServiceMode serviceMode = YteLiftConfigStore.getServiceMode(
                    ((Lift) (Object) this).getId());
            cir.setReturnValue(yte$getDisplayDirection(serviceMode.acceptsHallCalls()
                    ? DisplayDirectionMode.LATCH_UNTIL_DOOR_CLOSE
                    : DisplayDirectionMode.INDEPENDENT));
        }
    }

    /**
     * @author YTE
     * @reason Replace MAX_SPEED and ACCELERATION_DEFAULT with per-lift custom values
     */
    @Overwrite
    public void tick(long millisElapsed) {
        // Bound integration steps so a delayed frame cannot snap across the
        // entire levelling distance or consume half of the door animation.
        if (millisElapsed > 20) {
            long remaining = millisElapsed;
            while (remaining > 0) {
                final long step = Math.min(remaining, 20);
                tick(step);
                remaining -= step;
            }
            return;
        }
        final long id = ((Lift) (Object) this).getId();
        final boolean movingDown = getSpeed() < 0 || getSpeed() == 0 && !getInstructions().isEmpty()
                && invokeGetProgress(getInstructions().get(0).getFloor()) < getRailProgress();
        final double customMaxSpeed = YteLiftConfigStore.getSpeed(id, movingDown) / 1000.0;
        final double customAccel = YteLiftConfigStore.getAcceleration(id, movingDown) / 1_000_000.0;
        final double adoDistance = YteLiftConfigStore.getAdoDistance(id);
        final double levellingDistance = YteLiftConfigStore.getLevellingDistance(id);
        final double levellingSpeed = YteLiftConfigStore.getLevellingSpeed(id) / 1000.0;
        final LiftMotionProfile motionProfile = YteLiftConfigStore.getMotionProfile(id);
        final LiftServiceMode serviceMode = YteLiftConfigStore.getServiceMode(id);

        if (!isClientside()) {
            if (serviceMode != yte$lastServiceMode) {
                yte$lastServiceMode = serviceMode;
                LiftDoorControlState.endManualClose(id);
                if (!serviceMode.acceptsHallCalls()) {
                    final boolean removedHallCalls = getInstructions().removeIf(
                            instruction -> instruction.getDirection() != LiftDirection.NONE);
                    if (removedHallCalls) {
                        setNeedsUpdate(true);
                    }
                    if (getSpeed() == 0 && getInstructions().isEmpty() && yte$isExactlyAtFloor()) {
                        yte$applyDoorCommand(LiftDoorControlState.Command.OPEN);
                    }
                }
            }

            final LiftDoorControlState.Command doorCommand = LiftDoorControlState.consume(id);
            if (doorCommand != null) {
                yte$applyDoorCommand(doorCommand);
            }

            final Integer cancelledFloor = LiftFloorCancelState.peek(id);
            if (cancelledFloor != null) {
                final boolean validFloor = cancelledFloor >= 0 && cancelledFloor < getFloors().size();
                final boolean cancellationAllowed = YteLiftConfigStore.isFloorCancelWhileMovingAllowed(id)
                        || getSpeed() == 0 && yte$isExactlyAtFloor();
                if (!validFloor || !cancellationAllowed) {
                    LiftFloorCancelState.complete(id, cancelledFloor);
                } else {
                    final boolean instructionRemoved = getInstructions().removeIf(instruction ->
                            instruction.getFloor() == cancelledFloor && instruction.getDirection() == LiftDirection.NONE);
                    if (instructionRemoved) {
                        LiftFloorCancelState.complete(id, cancelledFloor);
                        setNeedsUpdate(true);
                    }
                }
            }

            if (LiftDoorControlState.isHoldActive(id)) {
                final boolean stoppedAtFloor = getSpeed() == 0 && yte$isExactlyAtFloor();
                if (!YteLiftConfigStore.isDoorHoldEnabled(id) || LiftDoorControlState.isHoldExpired(id)) {
                    LiftDoorControlState.endHold(id);
                    Init.sendLiftHoldState(id, false);
                    final float doorValue = ((Lift) (Object) this).getDoorValue();
                    if (stoppedAtFloor && doorValue >= 0.999F) {
                        setStoppingCoolDown(YTE_DOOR_CLOSED_DELAY + YTE_SINGLE_DOOR_MOVE_TIME);
                        setNeedsUpdate(true);
                    }
                } else if (stoppedAtFloor) {
                    final float doorValue = ((Lift) (Object) this).getDoorValue();
                    final long fullOpenCoolDown = YTE_LIFT_STOPPING_TIME - YTE_SINGLE_DOOR_MOVE_TIME;
                    if (doorValue >= 0.999F && getStoppingCoolDown() < fullOpenCoolDown) {
                        setStoppingCoolDown(fullOpenCoolDown);
                        setNeedsUpdate(true);
                    }
                }
            }

            if (!serviceMode.acceptsHallCalls()) {
                if (!LiftDoorControlState.isManualCloseActive(id)) {
                    yte$keepIndependentDoorOpen(millisElapsed);
                }
            } else {
                LiftDoorControlState.endManualClose(id);
            }
        }

        /* Reverse levelling temporarily disabled.
        final int nextFloor = getInstructions().isEmpty() ? Integer.MIN_VALUE : getInstructions().get(0).getFloor();
        if (yte$reverseApproachDirection != 0 && nextFloor != yte$motionTargetFloor) {
            yte$reverseApproachDirection = 0;
            yte$reverseReturning = false;
            if (getStoppingCoolDown() > 0) {
                setStoppingCoolDown(0);
            }
        }
        final boolean adoLevelling = getStoppingCoolDown() > 0 && !getInstructions().isEmpty()
                && (getSpeed() != 0 || yte$reverseApproachDirection != 0
                && getRailProgress() != invokeGetProgress(nextFloor));
        */
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
        } else {
            if (adoLevelling) {
                // 平层/预开门阶段：门开到全开后保持，避免徐行过慢时 cooldown 耗尽、停稳后二次开门
                setStoppingCoolDown(Math.max(getStoppingCoolDown() - millisElapsed, YTE_DOOR_FULL_OPEN_COOL_DOWN));
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
                /* Reverse levelling temporarily disabled.
                if (motionProfile == LiftMotionProfile.REVERSE_LEVELLING && yte$reverseApproachDirection == 0
                        && getStoppingCoolDown() == 0 && levellingDistance > 0 && levellingSpeed > 0 && direction != 0) {
                    // Keep the turnaround on the target floor's side of the midpoint to the next floor.
                    final long adjacentProgress = invokeGetProgress(nextInstructionFloor + (int) direction);
                    final double clearance = adjacentProgress == nextInstructionProgress ? levellingDistance
                            : Math.abs(adjacentProgress - nextInstructionProgress) / 2.0 - 0.000001;
                    if (clearance > 0) {
                        yte$reverseApproachDirection = (int) direction;
                        yte$reverseTurnProgress = nextInstructionProgress + direction * Math.min(levellingDistance, clearance);
                        setNeedsUpdate(true);
                    }
                }

                final boolean approachingTurn = yte$reverseApproachDirection != 0 && !yte$reverseReturning;
                final double motionTarget = approachingTurn ? yte$reverseTurnProgress : nextInstructionProgress;
                final double motionDifference = motionTarget - getRailProgress();
                final LiftMotionProfile activeProfile = yte$reverseApproachDirection != 0
                        ? LiftMotionProfile.REVERSE_LEVELLING
                        : motionProfile == LiftMotionProfile.REVERSE_LEVELLING ? LiftMotionProfile.STANDARD : motionProfile;
                if (activeProfile == LiftMotionProfile.TWO_STAGE && yte$twoStageCoarseHoldRemaining > 0) {
                    setSpeed(LiftMotionProfile.TWO_STAGE_COARSE_STOP_SPEED * direction);
                    yte$twoStageCoarseHoldRemaining = Math.max(
                            yte$twoStageCoarseHoldRemaining - millisElapsed, 0);
                    if (yte$twoStageCoarseHoldRemaining == 0) {
                        yte$twoStageFineLevelling = true;
                    }
                } else {
                    final double returnSpeed = levellingSpeed > 0 ? levellingSpeed : YteLiftConfig.DEFAULT_LEVELLING_SPEED / 1000.0;
                    final double maximumSpeed = yte$reverseReturning ? Math.min(returnSpeed, customMaxSpeed) : customMaxSpeed;
                    final LiftMotionProfile.MotionResult motionResult = activeProfile.calculate(
                            new LiftMotionProfile.MotionContext(getSpeed(), maximumSpeed, customAccel,
                                    Math.abs(motionDifference), levellingDistance, levellingSpeed,
                                    Math.signum(motionDifference), millisElapsed,
                                    activeProfile == LiftMotionProfile.REVERSE_LEVELLING
                                            ? yte$reverseReturning : yte$twoStageFineLevelling));
                    setSpeed(motionResult.speed);
                    if (activeProfile == LiftMotionProfile.TWO_STAGE && motionResult.enterFineLevelling) {
                        yte$twoStageCoarseHoldRemaining = YTE_TWO_STAGE_COARSE_HOLD_TIME;
                    }
                }
                if (activeProfile != LiftMotionProfile.TWO_STAGE) {
                    yte$twoStageFineLevelling = false;
                    yte$twoStageCoarseHoldRemaining = 0;
                }

                final double updatedMovementThisTick = Math.abs(getSpeed() * millisElapsed);
                if (!approachingTurn && adoDistance > 0 && !isClientside() && !adoLevelling && getSpeed() != 0
                        && distanceToTarget <= adoDistance + updatedMovementThisTick) {
                    setStoppingCoolDown(YTE_LIFT_STOPPING_TIME);
                    Init.sendLiftAdoStart(id, YTE_LIFT_STOPPING_TIME);
                }

                final boolean movingTowardsTarget = activeProfile != LiftMotionProfile.REVERSE_LEVELLING
                        || Math.signum(getSpeed()) == Math.signum(motionDifference);
                if (movingTowardsTarget && Math.abs(motionDifference) <= updatedMovementThisTick) {
                    setRailProgress(motionTarget);
                    setSpeed(0);
                    if (approachingTurn) {
                        yte$reverseReturning = true;
                        setNeedsUpdate(true);
                    } else if (!isClientside()) {
                */
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
                    setStoppingCoolDown(YTE_LIFT_STOPPING_TIME);
                    Init.sendLiftAdoStart(id, YTE_LIFT_STOPPING_TIME);
                }

                if (Math.abs(getRailProgress() - nextInstructionProgress) <= Math.abs(getSpeed() * millisElapsed)) {
                    setRailProgress(nextInstructionProgress);
                    setSpeed(0);
                    if (!isClientside()) {
                        getInstructions().remove(0);
                        yte$motionTargetFloor = Integer.MIN_VALUE;
                        yte$twoStageFineLevelling = false;
                        yte$twoStageCoarseHoldRemaining = 0;
                        /* Reverse levelling temporarily disabled.
                        yte$reverseApproachDirection = 0;
                        yte$reverseReturning = false;
                        */
                        if (getStoppingCoolDown() == 0) {
                            final long doorOpenDelay = adoDistance == 0 ? YteLiftConfigStore.getDoorOpenDelayMillis(id) : 0;
                            setStoppingCoolDown(YTE_LIFT_STOPPING_TIME + Math.min(doorOpenDelay, Long.MAX_VALUE - YTE_LIFT_STOPPING_TIME));
                        }
                        setNeedsUpdate(true);
                    }
                }
            }

            /* Reverse levelling temporarily disabled.
            final double minimumProgress = Math.min(0, yte$reverseApproachDirection != 0 ? yte$reverseTurnProgress : getRailProgress());
            final double maximumProgress = Math.max(invokeGetProgress(Integer.MAX_VALUE),
                    yte$reverseApproachDirection != 0 ? yte$reverseTurnProgress : getRailProgress());
            setRailProgress(Utilities.clamp(getRailProgress() + getSpeed() * millisElapsed, minimumProgress, maximumProgress));
            */
            setRailProgress(Utilities.clamp(getRailProgress() + getSpeed() * millisElapsed, 0, invokeGetProgress(Integer.MAX_VALUE)));
        }

        if (isClientside()) {
            yte$updateDisplayFacts(levellingDistance);
            yte$updateDisplayDirection(millisElapsed);
            LiftArrivalLanternState.get(id).update(
                    LiftDisplayState.get(id), YteLiftConfigStore.getArrivalLanternTriggerMode(id));
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
    private void yte$applyDoorCommand(LiftDoorControlState.Command command) {
        if (getSpeed() != 0 || !yte$isExactlyAtFloor()) {
            return;
        }

        final Lift lift = (Lift) (Object) this;
        final long id = lift.getId();
        final boolean independentService = !YteLiftConfigStore.getServiceMode(id).acceptsHallCalls();
        final long coolDown = getStoppingCoolDown();
        final float doorValue = Utilities.clamp(lift.getDoorValue(), 0, 1);
        if (command == LiftDoorControlState.Command.RELEASE_CLOSE) {
            LiftDoorControlState.endManualClose(id);
            if (!independentService) {
                return;
            }
            // Reopen only while the doors are physically still closing. Once
            // they have reached the closed position, releasing the close
            // button must leave them closed and allow the lift to depart.
            if (doorValue <= 0) {
                if (coolDown <= YTE_DOOR_CLOSED_DELAY) {
                    setStoppingCoolDown(Math.min(coolDown, 1));
                    setNeedsUpdate(true);
                }
                return;
            }
            command = LiftDoorControlState.Command.OPEN;
        } else if (command == LiftDoorControlState.Command.CLOSE && independentService) {
            LiftDoorControlState.signalManualClose(id);
        } else if (command == LiftDoorControlState.Command.OPEN
                || command == LiftDoorControlState.Command.HOLD_OPEN) {
            LiftDoorControlState.endManualClose(id);
        }
        final long fullOpenCoolDown = YTE_LIFT_STOPPING_TIME - YTE_SINGLE_DOOR_MOVE_TIME;
        final long closeStartCoolDown = YTE_DOOR_CLOSED_DELAY + YTE_SINGLE_DOOR_MOVE_TIME;
        final boolean startingIdleDoorCycle = command == LiftDoorControlState.Command.OPEN
                && getInstructions().isEmpty()
                && coolDown < YTE_DOOR_CLOSED_DELAY
                && lift.getDoorValue() <= 0;
        boolean openCommandApplied = false;

        if (command == LiftDoorControlState.Command.HOLD_OPEN
                && YteLiftConfigStore.isDoorHoldEnabled(id)) {
            LiftDoorControlState.beginHold(id);
            Init.sendLiftHoldState(id, true);
            if (doorValue >= 1) {
                setStoppingCoolDown(fullOpenCoolDown);
                openCommandApplied = true;
            } else if (doorValue > 0 && coolDown <= closeStartCoolDown) {
                setStoppingCoolDown(YTE_LIFT_STOPPING_TIME - Math.round(doorValue * YTE_SINGLE_DOOR_MOVE_TIME));
                openCommandApplied = true;
            } else if (doorValue <= 0 && coolDown <= YTE_DOOR_CLOSED_DELAY) {
                setStoppingCoolDown(YTE_LIFT_STOPPING_TIME);
                openCommandApplied = true;
            }
        } else if (command == LiftDoorControlState.Command.OPEN) {
            if (doorValue >= 1) {
                setStoppingCoolDown(fullOpenCoolDown);
                openCommandApplied = true;
            } else if (doorValue > 0 && coolDown <= closeStartCoolDown) {
                setStoppingCoolDown(YTE_LIFT_STOPPING_TIME - Math.round(doorValue * YTE_SINGLE_DOOR_MOVE_TIME));
                openCommandApplied = true;
            } else if (doorValue <= 0 && coolDown <= YTE_DOOR_CLOSED_DELAY) {
                setStoppingCoolDown(YTE_LIFT_STOPPING_TIME);
                openCommandApplied = true;
            }
        } else if (command == LiftDoorControlState.Command.CLOSE) {
            LiftDoorControlState.endHold(id);
            Init.sendLiftHoldState(id, false);
            if (lift.getDoorValue() >= 0.999F) {
                setStoppingCoolDown(closeStartCoolDown);
            }
        }

        if (openCommandApplied) {
            Init.sendLiftDoorOpen(lift.getId(), getStoppingCoolDown(), startingIdleDoorCycle);
        }

        setNeedsUpdate(true);
    }

    @Unique
    private void yte$keepIndependentDoorOpen(long millisElapsed) {
        if (getSpeed() != 0 || !yte$isExactlyAtFloor()) {
            return;
        }

        final Lift lift = (Lift) (Object) this;
        final long coolDown = getStoppingCoolDown();
        final float doorValue = Utilities.clamp(lift.getDoorValue(), 0, 1);
        if (coolDown <= 1 && doorValue <= 0) {
            return;
        }

        final long adjustedTick = Math.max(millisElapsed, 0);
        if (doorValue >= 0.999F && coolDown <= YTE_DOOR_FULL_OPEN_COOL_DOWN) {
            setStoppingCoolDown(YTE_DOOR_FULL_OPEN_COOL_DOWN + adjustedTick);
            setNeedsUpdate(true);
        } else if (doorValue > 0 && coolDown <= YTE_DOOR_CLOSED_DELAY + YTE_SINGLE_DOOR_MOVE_TIME) {
            setStoppingCoolDown(YTE_LIFT_STOPPING_TIME
                    - Math.round(doorValue * YTE_SINGLE_DOOR_MOVE_TIME) + adjustedTick);
            setNeedsUpdate(true);
        } else if (doorValue <= 0 && coolDown <= YTE_DOOR_CLOSED_DELAY) {
            setStoppingCoolDown(YTE_LIFT_STOPPING_TIME + adjustedTick);
            setNeedsUpdate(true);
        }
    }

    @Unique
    private boolean yte$isExactlyAtFloor() {
        return yte$getExactFloorIndex() >= 0;
    }

    @Unique
    private int yte$getExactFloorIndex() {
        for (int i = 0; i < getFloors().size(); i++) {
            if (Math.abs(getRailProgress() - invokeGetProgress(i)) < 0.000001) {
                return i;
            }
        }
        return -1;
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
        LiftDirection plannedArrivalDirection = LiftDirection.NONE;
        LiftDirection nextQueuedDirection = LiftDirection.NONE;

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
            /* Reverse levelling temporarily disabled.
            final LiftDirection approachDirection = yte$reverseApproachDirection == 0
                    ? movementDirection : LiftDirection.fromDifference(yte$reverseApproachDirection);
            plannedArrivalDirection = yte$getPlannedArrivalDirection(targetFloor, instruction, approachDirection);
            */
            plannedArrivalDirection = yte$getPlannedArrivalDirection(targetFloor, instruction, movementDirection);
        }

        for (LiftInstruction instruction : getInstructions()) {
            final double difference = invokeGetProgress(instruction.getFloor()) - getRailProgress();
            if (difference > 0.000001) {
                nextQueuedDirection = LiftDirection.UP;
                break;
            }
            if (difference < -0.000001) {
                nextQueuedDirection = LiftDirection.DOWN;
                break;
            }
        }

        final boolean doorCycle = getStoppingCoolDown() > 1 || lift.getDoorValue() != 0;
        final boolean levelling = moving && levellingDistance > 0 && distanceToTarget <= levellingDistance;
        final boolean idle = !moving && getInstructions().isEmpty() && !doorCycle;
        final int displayedFloor = lift.getFloorIndex(lift.getCurrentFloor().getPosition());
        final int exactFloor = yte$getExactFloorIndex();

        LiftDisplayState.get(lift.getId()).update(
                movementDirection, targetDirection, plannedArrivalDirection, nextQueuedDirection,
                moving, levelling, doorCycle, idle,
                displayedFloor, exactFloor, targetFloor, getSpeed(), lift.getDoorValue(),
                distanceToTarget, getStoppingCoolDown());
    }

    @Unique
    private LiftDirection yte$getPlannedArrivalDirection(int targetFloor, LiftInstruction instruction,
            LiftDirection movementDirection) {
        final int floorCount = getFloors().size();
        final boolean arrivingFromTravel = getSpeed() != 0
                || LiftDisplayDirectionState.get(((Lift) (Object) this).getId()).movedSinceIdle;
        if (arrivingFromTravel && targetFloor == floorCount - 1) {
            return LiftDirection.DOWN;
        }
        if (arrivingFromTravel && targetFloor == 0) {
            return LiftDirection.UP;
        }
        if (instruction.getDirection() != LiftDirection.NONE) {
            return instruction.getDirection();
        }
        if (getInstructions().size() > 1) {
            final int followingFloor = getInstructions().get(1).getFloor();
            if (followingFloor > targetFloor) {
                return LiftDirection.UP;
            }
            if (followingFloor < targetFloor) {
                return LiftDirection.DOWN;
            }
        }
        return movementDirection;
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
            // The nearest displayed floor may change far ahead of arrival when
            // floors are widely spaced. Start the delay only within five metres.
            if (displayedFloorIndex == instruction.getFloor()
                    && Math.abs(invokeGetProgress(instruction.getFloor()) - getRailProgress()) <= YTE_ARRIVAL_DIRECTION_DISTANCE) {
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

        if (displayState.arrivalFloor >= 0 && (displayedFloorIndex != displayState.arrivalFloor
                || Math.abs(invokeGetProgress(displayState.arrivalFloor) - getRailProgress()) > YTE_ARRIVAL_DIRECTION_DISTANCE)) {
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
        /* Reverse levelling temporarily disabled.
        final double difference = yte$reverseApproachDirection != 0
                ? yte$reverseApproachDirection : invokeGetProgress(instruction.getFloor()) - getRailProgress();
        */
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
