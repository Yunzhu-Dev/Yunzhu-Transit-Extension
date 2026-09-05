package top.xfunny.mixin;

import org.mtr.mapping.holder.Vector3d;
import org.mtr.mapping.mapper.GraphicsHolder;
import org.mtr.mapping.mapper.OptimizedRenderer;
import org.mtr.mod.render.MainRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.xfunny.mod.client.view.DirectRenderer;

/**
 * 每个渲染 pass（阴影/主）开始时缓存一次阴影通道标志，
 * 供 yte 方块实体渲染器判断是否跳过阴影通道的重渲染。
 */
@Mixin(value = MainRenderer.class, remap = false)
public abstract class MixinMainRenderer {

    @Inject(method = "render(Lorg/mtr/mapping/mapper/GraphicsHolder;Lorg/mtr/mapping/holder/Vector3d;)V", at = @At("HEAD"), remap = false)
    private static void yte$updateShadowPass(GraphicsHolder graphicsHolder, Vector3d offset, CallbackInfo ci) {
        DirectRenderer.shadowPass = OptimizedRenderer.renderingShadows();
    }
}
