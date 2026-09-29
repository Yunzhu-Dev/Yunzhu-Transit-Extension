package top.xfunny.mixin;

import org.mtr.core.data.Lift;
import org.mtr.core.data.LiftFloor;
import org.mtr.mapping.holder.BlockHitResult;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.BlockState;
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
import top.xfunny.mod.lift.LiftDoorMaintenance;
import top.xfunny.mod.lift.LiftDoorState;
import top.xfunny.mod.lift.LiftModeState;
import top.xfunny.mod.packet.PacketLiftDoorCurtain;
import top.xfunny.mod.util.LiftShaftLocator;

@Mixin(value = BlockPSDAPGDoorBase.BlockEntityBase.class, remap = false)
public abstract class MixinLiftDoorBlockEntity implements LiftDoorMaintenance {

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

    /** 光幕检测：玩家在门贴面 ±0.25 块薄层且落在门宽/门高内即上报；上半格跳过（门高按整扇 2 格覆盖）。 */
    @Inject(method = "tick", at = @At("HEAD"))
    private void yte$curtainDetect(float tickDelta, CallbackInfo ci) {
        final org.mtr.mapping.holder.World world = yte$self().getWorld2();
        if (world == null || !world.isClient()) {
            return;
        }
        final BlockState state = yte$self().getCachedState2();
        if (IBlock.getStatePropertySafe(state, IBlock.HALF) == IBlock.DoubleBlockHalf.UPPER) {
            return;
        }
        final ClientPlayerEntity player = MinecraftClient.getInstance().getPlayerMapped();
        if (player == null) {
            return;
        }
        final BlockPos pos = yte$self().getPos2();
        final double px = player.getPos().getXMapped();
        final double py = player.getPos().getYMapped();
        final double pz = player.getPos().getZMapped();

        // 光幕只看门贴面 ±0.25 块薄层：整格判定会让井道内侧/玻璃后空间误触发
        boolean blocked;
        switch (IBlock.getStatePropertySafe(state, BlockPSDAPGDoorBase.FACING)) {
            case NORTH:
                blocked = Math.abs(pos.getZ() - pz) < 0.25
                        && Math.abs(px - (pos.getX() + 0.5)) < 0.8;
                break;
            case SOUTH:
                blocked = Math.abs(pz - (pos.getZ() + 1)) < 0.25
                        && Math.abs(px - (pos.getX() + 0.5)) < 0.8;
                break;
            case WEST:
                blocked = Math.abs(pos.getX() - px) < 0.25
                        && Math.abs(pz - (pos.getZ() + 0.5)) < 0.8;
                break;
            case EAST:
                blocked = Math.abs(px - (pos.getX() + 1)) < 0.25
                        && Math.abs(pz - (pos.getZ() + 0.5)) < 0.8;
                break;
            default:
                blocked = false;
        }
        // 纵向覆盖整扇门高（本格 + 上格）
        if (!(py < pos.getY() + 2 && py + 1.8 > pos.getY())) {
            blocked = false;
        }

        final long now = System.nanoTime();

        // 攻击门：左键按住且 2 格视线首命中本门（含上格）→ 「手伸进门缝」脉冲（独立 250ms 节流，立即上报）
        if (MinecraftClient.getInstance().getOptionsMapped().getKeyAttackMapped().isPressed()
                && now >= yte$nextAttackSendNanos) {
            final org.mtr.mapping.holder.HitResult hitResult = player.raycast(2.0, 0, false);
            if (BlockHitResult.isInstance(hitResult)) {
                final BlockPos hitPos = BlockHitResult.cast(hitResult).getBlockPos();
                if (hitPos.equals(pos) || hitPos.equals(pos.up(1))) {
                    yte$nextAttackSendNanos = now + 250_000_000L;
                    if (yte$curtainLiftId == null) {
                        yte$curtainLiftId = LiftShaftLocator.findForDoor(pos);
                    }
                    if (yte$curtainAllowed(yte$curtainLiftId)) {
                        InitClient.REGISTRY_CLIENT.sendPacketToServer(
                                new PacketLiftDoorCurtain(yte$curtainLiftId, true));
                    }
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
            yte$curtainLiftId = LiftShaftLocator.findForDoor(pos);
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

    @Override
    public void yte$setMaintenanceOpen(boolean open, long liftId, long durationMs) {
        final YteLiftConfigStore.DoorParams params = YteLiftConfigStore.getDoorParams(liftId);
        final float from = (float) yte$maintenanceAnimValue();
        final long startNanos = System.nanoTime();
        final long resolvedDurationMs = open ? durationMs : params.closeMs;
        final DoorMotionCurve curve = params.curve;
        yte$self().markDirty2();
        final org.mtr.mapping.holder.World world = yte$self().getWorld2();
        if (world != null) {
            BlockPos bottomPos = yte$self().getPos2();
            if (IBlock.getStatePropertySafe(yte$self().getCachedState2(), IBlock.HALF) == IBlock.DoubleBlockHalf.UPPER) {
                bottomPos = bottomPos.down(1);
            }
            final org.mtr.mapping.holder.Direction facing = IBlock.getStatePropertySafe(yte$self().getCachedState2(), BlockPSDAPGDoorBase.FACING);
            final BlockPos otherPos = bottomPos.offset(IBlock.getStatePropertySafe(yte$self().getCachedState2(), IBlock.SIDE) == IBlock.EnumSide.RIGHT
                    ? facing.rotateYCounterclockwise() : facing.rotateYClockwise());
            for (final BlockPos target : new BlockPos[]{bottomPos, bottomPos.up(1), otherPos, otherPos.up(1)}) {
                final org.mtr.mapping.holder.BlockEntity entity = world.getBlockEntity(target);
                if (entity != null && entity.data instanceof LiftDoorMaintenance) {
                    ((LiftDoorMaintenance) entity.data).yte$applyMaintenanceState(
                            open, from, startNanos, resolvedDurationMs, curve);
                }
            }
        } else {
            yte$applyMaintenanceState(open, from, startNanos, resolvedDurationMs, curve);
        }
    }

    /** 四格状态统一写入；四格一致后任意一格读取即为整扇门状态。 */
    @Override
    public void yte$applyMaintenanceState(
            boolean open, float fromValue, long startNanos, long durationMs, DoorMotionCurve curve) {
        yte$maintenanceOpen = open;
        yte$animFromValue = fromValue;
        yte$animStartNanos = startNanos;
        yte$animDurationMs = Math.max(durationMs, 1);
        yte$animCurve = curve == null ? DoorMotionCurve.LINEAR : curve;
        yte$self().markDirty2();
    }

    /** 检修过渡的实时门值：开门线性、关门套配置曲线，播完后稳定在目标值。 */
    @Override
    public double yte$maintenanceAnimValue() {
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
        return yte$maintenanceOpen;
    }

    @Override
    public boolean yte$isMaintenanceAnimating() {
        return yte$animStartNanos != 0
                && System.nanoTime() - yte$animStartNanos < yte$animDurationMs * 1_000_000L;
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
        double value = yte$nearbyLiftDoorValue();
        if (yte$maintenanceOpen || yte$animStartNanos != 0) {
            value = Math.max(yte$maintenanceAnimValue(), value);
        }
        cir.setReturnValue(value);
    }

    @Unique
    private double yte$nearbyLiftDoorValue() {
        BlockPos doorPos = yte$self().getPos2();
        if (IBlock.getStatePropertySafe(yte$self().getCachedState2(), IBlock.HALF) == IBlock.DoubleBlockHalf.UPPER) {
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

            if (LiftShaftLocator.isDoorAtLiftDoorway(doorPos, lift, targetFloor.getPosition())) {
                doorValue = Math.max(doorValue, lift.getDoorValue() * LiftDoorState.DOOR_MAX_OPEN_SCALE);
            }
        }
        return doorValue;
    }

    @Unique
    private BlockEntityExtension yte$self() {
        return (BlockEntityExtension) (Object) this;
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
