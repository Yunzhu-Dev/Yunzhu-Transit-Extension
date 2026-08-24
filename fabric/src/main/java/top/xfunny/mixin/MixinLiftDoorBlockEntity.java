package top.xfunny.mixin;

import org.mtr.core.data.Lift;
import org.mtr.core.data.LiftFloor;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.BlockState;
import org.mtr.mapping.holder.BlockEntityType;
import org.mtr.mapping.holder.BlockHitResult;
import org.mtr.mapping.holder.ClientPlayerEntity;
import org.mtr.mapping.holder.MinecraftClient;
import org.mtr.mapping.mapper.BlockEntityExtension;
import org.mtr.mod.block.BlockLiftDoor;
import org.mtr.mod.block.BlockLiftDoorOdd;
import org.mtr.mod.block.BlockPSDAPGDoorBase;
import org.mtr.mod.block.IBlock;
import org.mtr.mod.client.MinecraftClientData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.xfunny.mod.client.InitClient;
import top.xfunny.mod.block.HitachiB85Door1;
import top.xfunny.mod.block.KoneMDoor1;
import top.xfunny.mod.block.MitsubishiNexWayDoor1;
import top.xfunny.mod.block.OtisE411USDoor1;
import top.xfunny.mod.block.SchindlerQKS9Door1;
import top.xfunny.mod.config.YteLiftConfigStore;
import top.xfunny.mod.lift.DoorMotionCurve;
import top.xfunny.mod.lift.LiftDoorState;
import top.xfunny.mod.lift.LiftModeState;
import top.xfunny.mod.lift.LiftDoorMaintenance;
import top.xfunny.mod.packet.PacketLiftDoorCurtain;
import top.xfunny.mod.util.LiftShaftLocator;

@Mixin(value = BlockPSDAPGDoorBase.BlockEntityBase.class, remap = false)
public abstract class MixinLiftDoorBlockEntity extends BlockEntityExtension implements LiftDoorMaintenance {

    @Unique
    private boolean yte$maintenanceOpen;
    /** 检修过渡动画：起始时刻 / 起始门值 / 时长 / 曲线。开门线性，关门用配置曲线。 */
    @Unique
    private float yte$animFromValue;
    @Unique
    private long yte$animStartNanos;
    @Unique
    private long yte$animDurationMs = 1;
    @Unique
    private DoorMotionCurve yte$animCurve = DoorMotionCurve.LINEAR;
    /** 光幕客户端检测状态（每格独立，服务端去重）。 */
    @Unique
    private boolean yte$curtainLastBlocked;
    @Unique
    private long yte$curtainNextSendNanos;
    @Unique
    private Long yte$curtainLiftId;
    @Unique
    private long yte$nextAttackSendNanos;

    /**
     * 光幕客户端检测：本格碰撞盒微扩后与本地玩家相交即视为「玩家在门区域」，
     * 状态变化立即上报、持续遮挡每秒心跳一次（C→S）。
     */
    @Inject(method = "tick", at = @At("HEAD"))
    private void yte$curtainDetect(float tickDelta, CallbackInfo ci) {
        final org.mtr.mapping.holder.World world = getWorld2();
        if (world == null || !world.isClient()) {
            return;
        }
        final ClientPlayerEntity player = MinecraftClient.getInstance().getPlayerMapped();
        if (player == null) {
            return;
        }
        final BlockPos pos = getPos2();
        final double px = player.getPos().getXMapped();
        final double py = player.getPos().getYMapped();
        final double pz = player.getPos().getZMapped();
        final boolean blocked = px + 0.3 > pos.getX() && px - 0.3 < pos.getX() + 1
                && py + 1.8 > pos.getY() && py < pos.getY() + 1
                && pz + 0.3 > pos.getZ() && pz - 0.3 < pos.getZ() + 1;

        final long now = System.nanoTime();

        // 攻击门：左键按住且 1 格视线首命中本门 → 「手伸进门缝」脉冲（独立 250ms 节流，立即上报）
        if (MinecraftClient.getInstance().getOptionsMapped().getKeyAttackMapped().isPressed()
                && now >= yte$nextAttackSendNanos) {
            final org.mtr.mapping.holder.HitResult hitResult = player.raycast(1.0, 0, false);
            if (BlockHitResult.isInstance(hitResult)
                    && BlockHitResult.cast(hitResult).getBlockPos().equals(getPos2())) {
                yte$nextAttackSendNanos = now + 250_000_000L;
                if (yte$curtainLiftId == null) {
                    yte$curtainLiftId = LiftShaftLocator.findNearest(pos);
                }
                if (yte$curtainAllowed(yte$curtainLiftId)) {
                    InitClient.REGISTRY_CLIENT.sendPacketToServer(
                            new PacketLiftDoorCurtain(yte$curtainLiftId, true));
                }
            }
        }

        final boolean shouldSend = blocked != yte$curtainLastBlocked
                || (blocked && now >= yte$curtainNextSendNanos);
        if (!shouldSend) {
            return;
        }
        if (blocked) {
            yte$curtainNextSendNanos = now + 300_000_000L;
        }
        yte$curtainLastBlocked = blocked;
        if (yte$curtainLiftId == null) {
            yte$curtainLiftId = LiftShaftLocator.findNearest(pos);
        }
        if (yte$curtainAllowed(yte$curtainLiftId)) {
            InitClient.REGISTRY_CLIENT.sendPacketToServer(
                    new PacketLiftDoorCurtain(yte$curtainLiftId, blocked));
        }
    }

    /**
     * 锁定 / 模式过渡与执行期间整梯不接入光幕：客户端不上报遮挡（服务端 guard 为最终权威）。
     */
    @Unique
    private static boolean yte$curtainAllowed(Long liftId) {
        if (liftId == null) {
            return false;
        }
        final LiftModeState.State modeState = LiftModeState.getOrCreate(liftId);
        return !modeState.maintenanceLocked && !modeState.modePending && !modeState.modeActive;
    }

    protected MixinLiftDoorBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    @Override
    public void yte$setMaintenanceOpen(boolean open, long liftId, long durationMs) {
        // ponytail: MTR's door block entity has no NBT hooks, so this flag is
        // in-memory only; the C→S/S→C packets re-apply it after chunk reloads.
        // 以自身当前动画值为过渡起点；关门时长与曲线取该梯配置
        final YteLiftConfigStore.DoorParams params = YteLiftConfigStore.getDoorParams(liftId);
        final float from = (float) yte$maintenanceAnimValue();
        final long startNanos = System.nanoTime();
        final long resolvedDurationMs = open ? durationMs : params.closeMs;
        final DoorMotionCurve curve = params.curve;
        markDirty2();
        // 门共四格实体（左右扇 × 上下格）：全部写入同一组状态，否则只开半扇/动画错相
        final org.mtr.mapping.holder.World world = getWorld2();
        if (world != null) {
            BlockPos bottomPos = getPos2();
            if (IBlock.getStatePropertySafe(getCachedState2(), IBlock.HALF) == IBlock.DoubleBlockHalf.UPPER) {
                bottomPos = bottomPos.down(1);
            }
            final org.mtr.mapping.holder.Direction facing = IBlock.getStatePropertySafe(getCachedState2(), BlockPSDAPGDoorBase.FACING);
            final boolean sideRight = IBlock.getStatePropertySafe(getCachedState2(), IBlock.SIDE) == IBlock.EnumSide.RIGHT;
            final org.mtr.mapping.holder.Direction otherDirection = sideRight
                    ? facing.rotateYCounterclockwise() : facing.rotateYClockwise();
            final BlockPos[] targets = {
                    bottomPos, bottomPos.up(1),
                    bottomPos.offset(otherDirection), bottomPos.offset(otherDirection).up(1)
            };
            for (final BlockPos target : targets) {
                final org.mtr.mapping.holder.BlockEntity entity = world.getBlockEntity(target);
                if (entity != null && entity.data instanceof MixinLiftDoorBlockEntity) {
                    ((MixinLiftDoorBlockEntity) entity.data).yte$applyMaintenanceState(
                            open, from, startNanos, resolvedDurationMs, curve);
                }
            }
        } else {
            yte$applyMaintenanceState(open, from, startNanos, resolvedDurationMs, curve);
        }
    }

    @Unique
    private void yte$applyMaintenanceState(
            boolean open, float fromValue, long startNanos, long durationMs, DoorMotionCurve curve) {
        yte$maintenanceOpen = open;
        yte$animFromValue = fromValue;
        yte$animStartNanos = startNanos;
        yte$animDurationMs = Math.max(durationMs, 1);
        yte$animCurve = curve == null ? DoorMotionCurve.LINEAR : curve;
        markDirty2();
    }

    /** 检修过渡的实时门值：开门线性、关门套配置曲线，播完后稳定在目标值。 */
    @Unique
    private double yte$maintenanceAnimValue() {
        if (yte$animStartNanos == 0) {
            return yte$maintenanceOpen ? 1.0 : 0.0;
        }
        final double progress = Math.min(
                (System.nanoTime() - yte$animStartNanos) / 1_000_000.0 / yte$animDurationMs, 1.0);
        if (progress >= 1.0) {
            return yte$maintenanceOpen ? 1.0 : 0.0;
        }
        if (yte$maintenanceOpen) {
            // 开门：线性
            return yte$animFromValue + (1.0 - yte$animFromValue) * progress;
        }
        // 关门：电梯配置曲线
        return yte$animFromValue * (1.0 - yte$animCurve.apply(progress));
    }

    @Override
    public boolean yte$isMaintenanceOpen() {
        final MixinLiftDoorBlockEntity bottom = yte$getBottomEntity();
        return bottom != null ? bottom.yte$maintenanceOpen : yte$maintenanceOpen;
    }

    @Override
    public boolean yte$isMaintenanceAnimating() {
        final MixinLiftDoorBlockEntity bottom = yte$getBottomEntity();
        return bottom != null && bottom.yte$animStartNanos != 0
                && System.nanoTime() - bottom.yte$animStartNanos < bottom.yte$animDurationMs * 1_000_000L;
    }

    /**
     * 层门实时读取轿厢门值：渲染与碰撞（tick 阶段）读取同一份新鲜值，消除
     * canOpenDoors 渲染帧写入造成的碰撞 1 帧延迟；同时把「仅本层开门」门控
     * 移到读取侧，写入侧无需再拦截。
     * 检修过渡期间输出取 max(维护动画值, 联动值)，防其他电梯到站夹车。
     */
    @Inject(method = "getDoorValue", at = @At("RETURN"), cancellable = true)
    private void yte$liftDoorLiveValue(CallbackInfoReturnable<Double> cir) {
        if (!yte$isLiftDoor()) {
            return;
        }
        // 检修动画状态统一读本扇底部实体（四格一致）
        final MixinLiftDoorBlockEntity bottom = yte$getBottomEntity();
        double value;
        if (bottom != null && (bottom.yte$maintenanceOpen || bottom.yte$animStartNanos != 0)) {
            value = Math.max(bottom.yte$maintenanceAnimValue(), yte$nearbyLiftDoorValue());
        } else {
            value = yte$nearbyLiftDoorValue();
        }
        cir.setReturnValue(value);
    }

    @Unique
    private MixinLiftDoorBlockEntity yte$getBottomEntity() {
        final org.mtr.mapping.holder.World world = getWorld2();
        if (world == null) {
            return null;
        }
        BlockPos pos = getPos2();
        if (IBlock.getStatePropertySafe(getCachedState2(), IBlock.HALF) == IBlock.DoubleBlockHalf.UPPER) {
            pos = pos.down(1);
        }
        final org.mtr.mapping.holder.BlockEntity entity = world.getBlockEntity(pos);
        return entity != null && entity.data instanceof MixinLiftDoorBlockEntity
                ? (MixinLiftDoorBlockEntity) entity.data : null;
    }

    @Unique
    private double yte$nearbyLiftDoorValue() {
        BlockPos doorPos = getPos2();
        if (IBlock.getStatePropertySafe(getCachedState2(), IBlock.HALF) == IBlock.DoubleBlockHalf.UPPER) {
            doorPos = doorPos.down(1);
        }

        double doorValue = 0;
        for (Lift lift : MinecraftClientData.getInstance().lifts) {
            if (!lift.hasCoolDown() || lift.getDoorValue() <= 0) {
                continue;
            }

            final MixinLiftSchema schema = (MixinLiftSchema) lift;
            final LiftFloor targetFloor;
            // Selecting a destination while the doors are still open must not
            // immediately move the hall-door association to that destination.
            // Use the instruction floor only once the lift is actually moving
            // (the ADO/levelling phase); otherwise keep the current floor.
            if (schema.getSpeed() == 0 || schema.getInstructions().isEmpty()) {
                targetFloor = lift.getCurrentFloor();
            } else {
                final int targetFloorIndex = schema.getInstructions().get(0).getFloor();
                if (targetFloorIndex < 0 || targetFloorIndex >= schema.getFloors().size()) {
                    continue;
                }
                targetFloor = schema.getFloors().get(targetFloorIndex);
            }

            final long targetX = targetFloor.getPosition().getX();
            final long targetY = targetFloor.getPosition().getY();
            final long targetZ = targetFloor.getPosition().getZ();
            final double alignY = targetY + lift.getOffsetY();
            final double rangeX = lift.getWidth();
            final double rangeZ = Math.max(lift.getWidth(), lift.getDepth()) / 2 + 1;
            if (Math.abs(doorPos.getX() - targetX) <= rangeX
                    && Math.abs(doorPos.getZ() - targetZ) <= rangeZ
                    && doorPos.getY() + 1 >= alignY - 2 && doorPos.getY() <= alignY + 2) {
                doorValue = Math.max(doorValue, lift.getDoorValue() * LiftDoorState.DOOR_MAX_OPEN_SCALE);
            }
        }
        return doorValue;
    }

    private boolean yte$isLiftDoor() {
        final Object self = this;
        return self instanceof BlockLiftDoor.BlockEntity
                || self instanceof BlockLiftDoorOdd.BlockEntity
                || self instanceof HitachiB85Door1.BlockEntity
                || self instanceof KoneMDoor1.BlockEntity
                || self instanceof MitsubishiNexWayDoor1.BlockEntity
                || self instanceof OtisE411USDoor1.BlockEntity
                || self instanceof SchindlerQKS9Door1.BlockEntity;
    }
}
