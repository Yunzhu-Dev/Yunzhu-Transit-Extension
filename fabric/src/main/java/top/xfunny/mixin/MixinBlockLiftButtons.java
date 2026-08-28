package top.xfunny.mixin;

import org.mtr.mapping.holder.ActionResult;
import org.mtr.mapping.holder.BlockHitResult;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.BlockState;
import org.mtr.mapping.holder.Hand;
import org.mtr.mapping.holder.PlayerEntity;
import org.mtr.mapping.holder.World;
import org.mtr.mod.block.BlockLiftButtons;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.xfunny.mod.Items;

@Mixin(value = BlockLiftButtons.class, remap = false)
public abstract class MixinBlockLiftButtons {

    @Inject(
            method = "onUse2",
            at = @At("HEAD")
    )
    private void yte$linkerItemsPass(BlockState state, World world, BlockPos pos, PlayerEntity player,
            Hand hand, BlockHitResult hit, CallbackInfoReturnable cir) {
        if (player.isHolding(Items.YTE_LIFT_BUTTONS_LINK_CONNECTOR.get()) ||
                player.isHolding(Items.YTE_LIFT_BUTTONS_LINK_REMOVER.get()) ||
                player.isHolding(Items.YTE_GROUP_LIFT_BUTTONS_LINK_CONNECTOR.get()) ||
                player.isHolding(Items.YTE_GROUP_LIFT_BUTTONS_LINK_REMOVER.get())) {
            cir.setReturnValue(ActionResult.PASS);
        }
    }
}
