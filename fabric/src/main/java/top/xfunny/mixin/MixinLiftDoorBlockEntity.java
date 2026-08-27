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
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.xfunny.mod.block.HitachiB85Door1;
import top.xfunny.mod.block.KoneMDoor1;
import top.xfunny.mod.block.MitsubishiNexWayDoor1;
import top.xfunny.mod.block.OtisE411USDoor1;
import top.xfunny.mod.block.SchindlerQKS9Door1;

@Mixin(value = BlockPSDAPGDoorBase.BlockEntityBase.class, remap = false)
public abstract class MixinLiftDoorBlockEntity extends BlockEntityExtension {

    protected MixinLiftDoorBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    @Inject(method = "setDoorValue", at = @At("HEAD"), cancellable = true)
    private void yte$onlyOpenAtTargetLiftFloor(double doorValue, CallbackInfo ci) {
        if (doorValue <= 0 || !yte$isLiftDoor()) {
            return;
        }

        BlockPos doorPos = getPos2();
        if (IBlock.getStatePropertySafe(getCachedState2(), IBlock.HALF) == IBlock.DoubleBlockHalf.UPPER) {
            doorPos = doorPos.down(1);
        }

        boolean foundNearbyOpeningLift = false;
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
                    && Math.abs(doorPos.getZ() - targetZ) <= horizontalRange) {
                foundNearbyOpeningLift = true;
                if (doorPos.getY() + 1 >= alignY - 2 && doorPos.getY() <= alignY + 2) {
                    return;
                }
            }
        }

        if (foundNearbyOpeningLift) {
            ci.cancel();
        }
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
