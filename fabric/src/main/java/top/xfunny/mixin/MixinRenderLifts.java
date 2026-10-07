package top.xfunny.mixin;

import org.mtr.mapping.holder.ClientPlayerEntity;
import org.mtr.mapping.holder.ClientWorld;
import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.holder.MinecraftClient;
import org.mtr.mapping.holder.Vector3d;
import org.mtr.mapping.holder.World;
import org.mtr.mod.Items;
import org.mtr.mod.client.MinecraftClientData;
import org.mtr.mod.model.ModelSmallCube;
import org.mtr.mod.render.RenderLifts;
import org.mtr.mod.render.StoredMatrixTransformations;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.xfunny.mod.block.LiftTrackMagneticVane;

@Mixin(value = RenderLifts.class, remap = false)
public abstract class MixinRenderLifts {

    @Unique
    private static final ModelSmallCube YTE_MAGNETIC_VANE_MARKER = new ModelSmallCube(
            new Identifier("textures/block/lapis_block.png"));

    @Inject(method = "render", at = @At("TAIL"))
    private static void yte$renderMagneticVaneMarkers(long millisElapsed, Vector3d cameraPosition, CallbackInfo ci) {
        final MinecraftClient minecraftClient = MinecraftClient.getInstance();
        final ClientWorld clientWorld = minecraftClient.getWorldMapped();
        final ClientPlayerEntity player = minecraftClient.getPlayerMapped();
        if (clientWorld == null || player == null || !player.isHolding(Items.LIFT_REFRESHER.get())) {
            return;
        }

        final World world = new World(clientWorld.data);
        MinecraftClientData.getInstance().lifts.forEach(lift -> {
            final MixinLiftSchema schema = (MixinLiftSchema) lift;
            for (int index = 1; index < schema.getFloors().size(); index++) {
                final org.mtr.core.data.Position first = schema.getFloors().get(index - 1).getPosition();
                final org.mtr.core.data.Position second = schema.getFloors().get(index).getPosition();
                final long minY = Math.min(first.getY(), second.getY());
                final long maxY = Math.max(first.getY(), second.getY());
                for (long y = minY + 1; y < maxY; y++) {
                    final org.mtr.core.data.Position position = new org.mtr.core.data.Position(first.getX(), y, first.getZ());
                    final org.mtr.mapping.holder.BlockEntity blockEntity = world.getBlockEntity(top.xfunny.mod.Init.positionToBlockPos(position));
                    if (blockEntity != null && blockEntity.data instanceof LiftTrackMagneticVane.BlockEntity) {
                        YTE_MAGNETIC_VANE_MARKER.render(new StoredMatrixTransformations(
                                position.getX(), position.getY(), position.getZ()),
                                org.mtr.mapping.mapper.GraphicsHolder.getDefaultLight());
                    }
                }
            }
        });
    }
}
