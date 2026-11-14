package top.xfunny.mixin;

import org.mtr.core.data.Lift;
import org.mtr.core.data.Vehicle;
import org.mtr.mod.client.MinecraftClientData;
import org.mtr.mod.packet.PacketUpdateVehiclesLifts;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import top.xfunny.mod.config.YteLiftConfigStore;

import java.util.function.Consumer;

@Mixin(value = PacketUpdateVehiclesLifts.class, remap = false)
public abstract class MixinPacketUpdateVehiclesLifts {

    @ModifyArg(method = "runClientInbound", index = 2, at = @At(value = "INVOKE", ordinal = 1,
            target = "Lorg/mtr/mod/packet/PacketUpdateVehiclesLifts;updateVehiclesOrLifts(Lorg/mtr/libraries/it/unimi/dsi/fastutil/objects/ObjectArraySet;Ljava/util/function/Consumer;Ljava/util/function/Consumer;Ljava/util/function/Consumer;Ljava/util/function/ToLongFunction;Ljava/util/function/Function;)Z"))
    private Consumer<Consumer<Lift>> yte$reconcileLiftDoorAnimation(Consumer<Consumer<Lift>> incomingUpdates) {
        return acceptUpdate -> incomingUpdates.accept(incoming -> {
            yte$reconcileArrivalOpening(incoming);
            acceptUpdate.accept(incoming);
        });
    }

    @Unique
    private static void yte$reconcileArrivalOpening(Lift incoming) {
        final long liftId = incoming.getId();
        final long openingStart = Vehicle.DOOR_MOVE_TIME + 2500;
        final long fullyOpen = openingStart - Vehicle.DOOR_MOVE_TIME / 2;
        final MixinLiftSchema incomingSchema = (MixinLiftSchema) incoming;
        final long serverCoolDown = incomingSchema.getStoppingCoolDown();
        if (incomingSchema.getSpeed() != 0 || serverCoolDown <= fullyOpen || serverCoolDown > openingStart
                || YteLiftConfigStore.getAdoDistance(liftId) != 0
                || YteLiftConfigStore.getDoorOpenDelayMillis(liftId) != 0) {
            return;
        }

        final Lift previous = MinecraftClientData.getLift(liftId);
        if (previous == null) {
            return;
        }

        final MixinLiftSchema previousSchema = (MixinLiftSchema) previous;
        final long localCoolDown = previousSchema.getStoppingCoolDown();
        if (localCoolDown <= 1 && previousSchema.getInstructions().size() > incomingSchema.getInstructions().size()) {
            // A completed stop starts opening from fully closed on the client.
            incomingSchema.setStoppingCoolDown(openingStart);
        } else if (localCoolDown >= fullyOpen && localCoolDown <= openingStart
                && previousSchema.getRailProgress() == incomingSchema.getRailProgress()) {
            // Further snapshots of this opening keep the locally animated position.
            incomingSchema.setStoppingCoolDown(localCoolDown);
        }
    }
}
