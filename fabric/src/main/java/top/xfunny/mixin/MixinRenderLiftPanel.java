package top.xfunny.mixin;

import org.mtr.mapping.holder.PlayerEntity;
import org.mtr.core.data.Lift;
import org.mtr.core.data.LiftDirection;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectObjectImmutablePair;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.World;
import org.mtr.mapping.mapper.PlayerHelper;
import org.mtr.mod.render.RenderLiftPanel;
import org.mtr.mod.render.RenderLifts;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.xfunny.mod.block.LiftTrackMagneticVane;
import top.xfunny.mod.config.YteLiftConfigStore;
import top.xfunny.mod.item.YteGroupLiftButtonsLinker;
import top.xfunny.mod.item.YteLiftButtonsLinker;
import top.xfunny.mod.lift.LiftDisplayDirection;
import top.xfunny.mod.util.LiftTrackMagneticVaneDisplayHelper;

@Mixin(value = RenderLiftPanel.class, remap = false)
public class MixinRenderLiftPanel {

    // 第一处调用：大数字（lift panel 所在楼层），只做 service mode 处理，不做磁隔板替换。
    @Redirect(
            method = "render",
            at = @At(value = "INVOKE", target = "Lorg/mtr/mod/render/RenderLifts;getLiftDetails(Lorg/mtr/mapping/holder/World;Lorg/mtr/core/data/Lift;Lorg/mtr/mapping/holder/BlockPos;)Lorg/mtr/libraries/it/unimi/dsi/fastutil/objects/ObjectObjectImmutablePair;", ordinal = 0)
    )
    private ObjectObjectImmutablePair<LiftDirection, ObjectObjectImmutablePair<String, String>> yte$renderPanelFloorNumber(
            World world, Lift lift, BlockPos blockPos) {
        return YteLiftConfigStore.getServiceMode(lift.getId()).hidesHallDisplay()
                ? new ObjectObjectImmutablePair<>(LiftDirection.NONE, new ObjectObjectImmutablePair<>("", ""))
                : RenderLifts.getLiftDetails(world, lift, blockPos);
    }

    // 第二处调用：滚动显示（电梯当前位置），做 service mode 处理 + 磁隔板替换。
    @Redirect(
            method = "render",
            at = @At(value = "INVOKE", target = "Lorg/mtr/mod/render/RenderLifts;getLiftDetails(Lorg/mtr/mapping/holder/World;Lorg/mtr/core/data/Lift;Lorg/mtr/mapping/holder/BlockPos;)Lorg/mtr/libraries/it/unimi/dsi/fastutil/objects/ObjectObjectImmutablePair;", ordinal = 1)
    )
    private ObjectObjectImmutablePair<LiftDirection, ObjectObjectImmutablePair<String, String>> yte$renderPanelLiveFloor(
            World world, Lift lift, BlockPos blockPos) {
        if (YteLiftConfigStore.getServiceMode(lift.getId()).hidesHallDisplay()) {
            return new ObjectObjectImmutablePair<>(LiftDirection.NONE, new ObjectObjectImmutablePair<>("", ""));
        }
        final LiftTrackMagneticVane.BlockEntity magneticVane = LiftTrackMagneticVaneDisplayHelper.getDisplayedMagneticVane(world, lift);
        if (magneticVane != null) {
            ((LiftDisplayDirection) lift).yte$resetArrivalDirectionDelay();
            return new ObjectObjectImmutablePair<>(lift.getDirection(),
                    new ObjectObjectImmutablePair<>(magneticVane.getFloorNumber(), magneticVane.getFloorDescription()));
        }
        return RenderLifts.getLiftDetails(world, lift, blockPos);
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
