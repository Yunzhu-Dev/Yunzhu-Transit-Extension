package top.xfunny.mixin;

import org.mtr.core.data.Lift;
import org.mtr.core.data.LiftFloor;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.BlockState;
import org.mtr.mapping.holder.BlockEntityType;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.xfunny.mod.block.HitachiB85Door1;
import top.xfunny.mod.block.KoneMDoor1;
import top.xfunny.mod.block.MitsubishiNexWayDoor1;
import top.xfunny.mod.block.OtisE411USDoor1;
import top.xfunny.mod.block.SchindlerQKS9Door1;
import top.xfunny.mod.lift.LiftDoorMaintenance;

@Mixin(value = BlockPSDAPGDoorBase.BlockEntityBase.class, remap = false)
public abstract class MixinLiftDoorBlockEntity extends BlockEntityExtension implements LiftDoorMaintenance {

    @Unique
    private boolean yte$maintenanceOpen;

    protected MixinLiftDoorBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    @Override
    public void yte$setMaintenanceOpen(boolean open) {
        // ponytail: MTR's door block entity has no NBT hooks, so this flag is
        // in-memory only; the C→S packet re-applies it after chunk reloads.
        yte$maintenanceOpen = open;
        markDirty2();
        // 门共四格实体（左右扇 × 上下格）：全部同步，否则只开半扇
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
                    ((MixinLiftDoorBlockEntity) entity.data).yte$maintenanceOpen = open;
                    ((BlockEntityExtension) entity.data).markDirty2();
                }
            }
        }
    }

    @Override
    public boolean yte$isMaintenanceOpen() {
        final MixinLiftDoorBlockEntity bottom = yte$getBottomEntity();
        return bottom != null ? bottom.yte$maintenanceOpen : yte$maintenanceOpen;
    }

    /**
     * 层门实时读取轿厢门值：渲染与碰撞（tick 阶段）读取同一份新鲜值，消除
     * canOpenDoors 渲染帧写入造成的碰撞 1 帧延迟；同时把「仅本层开门」门控
     * 移到读取侧，写入侧无需再拦截。
     */
    @Inject(method = "getDoorValue", at = @At("RETURN"), cancellable = true)
    private void yte$liftDoorLiveValue(CallbackInfoReturnable<Double> cir) {
        if (!yte$isLiftDoor()) {
            return;
        }
        // 检修开门标志统一读本扇底部实体（四格一致）
        final MixinLiftDoorBlockEntity bottom = yte$getBottomEntity();
        cir.setReturnValue(bottom != null && bottom.yte$maintenanceOpen ? 1.0 : yte$nearbyLiftDoorValue());
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
            final double horizontalRange = Math.max(lift.getWidth(), lift.getDepth()) / 2 + 3;
            if (Math.abs(doorPos.getX() - targetX) <= horizontalRange
                    && Math.abs(doorPos.getZ() - targetZ) <= horizontalRange
                    && doorPos.getY() + 1 >= alignY - 2 && doorPos.getY() <= alignY + 2) {
                // 完整曲线映射到 0~0.75：缓入缓出的减速尾段不再被 min() 封顶截掉，
                // 层门全程跟随轿厢门、最终开度同为 0.75
                doorValue = Math.max(doorValue, lift.getDoorValue() * 0.75);
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
