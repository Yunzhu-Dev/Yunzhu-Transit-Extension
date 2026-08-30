package top.xfunny.mixin;

import org.mtr.core.data.Lift;
import org.mtr.mapping.holder.World;
import org.mtr.mapping.holder.PlayerEntity;
import org.mtr.mapping.mapper.PlayerHelper;
import org.mtr.mod.render.RenderLiftButtons;
import org.mtr.mod.render.RenderLifts;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import top.xfunny.mod.item.YteGroupLiftButtonsLinker;
import top.xfunny.mod.item.YteLiftButtonsLinker;
import top.xfunny.mod.config.YteLiftConfigStore;

@Mixin(value = RenderLiftButtons.class, remap = false)
public class MixinRenderLiftButtons {
    @org.spongepowered.asm.mixin.injection.Redirect(
            method = "render",
            at = @At(value = "INVOKE", target = "Lorg/mtr/mod/render/RenderLifts;renderLiftDisplay(Lorg/mtr/mod/render/StoredMatrixTransformations;Lorg/mtr/mapping/holder/World;Lorg/mtr/core/data/Lift;FF)V")
    )
    private void yte$renderBlankDriverModeDisplay(org.mtr.mod.render.StoredMatrixTransformations transformations,
            World world, Lift lift, float width, float height) {
        if (!YteLiftConfigStore.getServiceMode(lift.getId()).hidesHallDisplay()) {
            RenderLifts.renderLiftDisplay(transformations, world, lift, width, height);
        }
    }

    @ModifyVariable(
            method = "render*",
            at = @At(value = "STORE", ordinal = 0),
            name = "holdingLinker"
    )
    private boolean modifyHoldingLinker(boolean original) {
        if (original) {
            return true;
        }
        final org.mtr.mapping.holder.ClientPlayerEntity clientPlayerEntity = org.mtr.mapping.holder.MinecraftClient.getInstance().getPlayerMapped();
        if (clientPlayerEntity == null) {
            return false;
        }
        return PlayerHelper.isHolding(PlayerEntity.cast(clientPlayerEntity), item ->
                item.data instanceof YteLiftButtonsLinker || item.data instanceof YteGroupLiftButtonsLinker
        );
    }
}
