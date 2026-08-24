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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.xfunny.mod.block.HitachiB85Door1;
import top.xfunny.mod.block.KoneMDoor1;
import top.xfunny.mod.block.MitsubishiNexWayDoor1;
import top.xfunny.mod.block.OtisE411USDoor1;
import top.xfunny.mod.block.SchindlerQKS9Door1;

@Mixin(value = BlockPSDAPGDoorBase.BlockEntityBase.class, remap = false)
public abstract class MixinLiftDoorBlockEntity extends BlockEntityExtension {

    /**
     * Lifts whose landing doors stand very close together (for example with
     * only one block between the doors) may still share a door if their
     * ownership score is within this slack of the best-matching lift. The
     * value is small enough that neighbouring shafts (several blocks apart)
     * never fall inside it, but forgiving for imprecise builds.
     */
    @Unique
    private static final double YTE_OWNERSHIP_SLACK = 4;

    /**
     * Squared-distance penalty per block of vertical mismatch when matching a
     * door to the lift floor registered at its landing. Prefers floors on the
     * exact level of the door over floors one block away.
     */
    @Unique
    private static final double YTE_FLOOR_Y_MISMATCH_PENALTY = 100;

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

        // MTR's doorway sweep (RenderVehicleHelper#canOpenDoors) opens every
        // unlocked landing door within roughly two blocks of the car front,
        // without knowing which lift a door belongs to. With lifts standing
        // side by side whose landing doors are only one block apart, one
        // lift's sweep reaches into the neighbour's door blocks and drags
        // that door half open. Resolve which lift actually owns this landing
        // door - among all lifts registered on this level, the one whose
        // floor is horizontally closest to the door - and only let an opening
        // owner (or a lift within the shared-shaft slack band) move it.
        double bestScore = Double.POSITIVE_INFINITY;
        boolean anyFloorOnLevel = false;
        for (Lift lift : MinecraftClientData.getInstance().lifts) {
            final Double score = yte$getOwnershipScore(lift, doorPos);
            if (score != null) {
                anyFloorOnLevel = true;
                if (score < bestScore) {
                    bestScore = score;
                }
            }
        }

        boolean allowed;
        if (!anyFloorOnLevel) {
            // No lift is registered anywhere near this landing; keep the
            // legacy proximity behaviour so unlinked setups keep working.
            allowed = yte$hasNearbyOpeningLift(doorPos);
        } else {
            allowed = false;
            for (Lift lift : MinecraftClientData.getInstance().lifts) {
                final Double score = yte$getOwnershipScore(lift, doorPos);
                if (score != null && score <= bestScore + YTE_OWNERSHIP_SLACK && yte$isOpeningAtDoor(lift, doorPos)) {
                    allowed = true;
                    break;
                }
            }
        }

        if (!allowed) {
            ci.cancel();
        }
    }

    /**
     * Score how well a lift matches this landing door: squared horizontal
     * distance from the door to the lift's nearest floor on this level, plus
     * a penalty per block of vertical mismatch. Returns {@code null} when the
     * lift has no floor within one block of the door's level.
     */
    @Unique
    private Double yte$getOwnershipScore(Lift lift, BlockPos doorPos) {
        double bestScore = Double.POSITIVE_INFINITY;
        for (LiftFloor floor : ((MixinLiftSchema) lift).getFloors()) {
            final long dy = Math.abs(floor.getPosition().getY() - doorPos.getY());
            if (dy > 1) {
                continue;
            }
            final long dx = floor.getPosition().getX() - doorPos.getX();
            final long dz = floor.getPosition().getZ() - doorPos.getZ();
            final double score = dx * dx + dz * dz + dy * dy * YTE_FLOOR_Y_MISMATCH_PENALTY;
            if (score < bestScore) {
                bestScore = score;
            }
        }
        return bestScore == Double.POSITIVE_INFINITY ? null : bestScore;
    }

    /**
     * Legacy proximity rule kept for doors without any registered lift on
     * their level: allow the door movement unless a nearby lift is currently
     * opening at some other level.
     */
    @Unique
    private boolean yte$hasNearbyOpeningLift(BlockPos doorPos) {
        boolean foundHorizontallyNearOpeningLift = false;
        for (Lift lift : MinecraftClientData.getInstance().lifts) {
            if (!lift.hasCoolDown() || lift.getDoorValue() <= 0) {
                continue;
            }
            final LiftFloor targetFloor = yte$getTargetFloor(lift);
            if (targetFloor == null) {
                continue;
            }
            final double horizontalRange = Math.max(lift.getWidth(), lift.getDepth()) / 2 + 3;
            if (Math.abs(doorPos.getX() - targetFloor.getPosition().getX()) <= horizontalRange
                    && Math.abs(doorPos.getZ() - targetFloor.getPosition().getZ()) <= horizontalRange) {
                foundHorizontallyNearOpeningLift = true;
                final double alignY = targetFloor.getPosition().getY() + lift.getOffsetY();
                if (doorPos.getY() + 1 >= alignY - 2 && doorPos.getY() <= alignY + 2) {
                    return true;
                }
            }
        }
        return !foundHorizontallyNearOpeningLift;
    }

    @Unique
    private boolean yte$isOpeningAtDoor(Lift lift, BlockPos doorPos) {
        if (!lift.hasCoolDown() || lift.getDoorValue() <= 0) {
            return false;
        }

        final LiftFloor targetFloor = yte$getTargetFloor(lift);
        if (targetFloor == null) {
            return false;
        }

        final double horizontalRange = Math.max(lift.getWidth(), lift.getDepth()) / 2 + 3;
        if (Math.abs(doorPos.getX() - targetFloor.getPosition().getX()) > horizontalRange
                || Math.abs(doorPos.getZ() - targetFloor.getPosition().getZ()) > horizontalRange) {
            return false;
        }

        final double alignY = targetFloor.getPosition().getY() + lift.getOffsetY();
        return doorPos.getY() + 1 >= alignY - 2 && doorPos.getY() <= alignY + 2;
    }

    @Unique
    private LiftFloor yte$getTargetFloor(Lift lift) {
        final MixinLiftSchema schema = (MixinLiftSchema) lift;
        // Selecting a destination while the doors are still open must not
        // immediately move the hall-door association to that destination.
        // Use the instruction floor only once the lift is actually moving
        // (the ADO/levelling phase); otherwise keep the current floor.
        if (schema.getSpeed() == 0 || schema.getInstructions().isEmpty()) {
            return lift.getCurrentFloor();
        }
        final int targetFloorIndex = schema.getInstructions().get(0).getFloor();
        if (targetFloorIndex < 0 || targetFloorIndex >= schema.getFloors().size()) {
            return null;
        }
        return schema.getFloors().get(targetFloorIndex);
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
