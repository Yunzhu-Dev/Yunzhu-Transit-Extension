package top.xfunny.mixin;

import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.ClientPlayerEntity;
import org.mtr.mapping.holder.HitResult;
import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.holder.MinecraftClient;
import org.mtr.mapping.holder.Vector3d;
import org.mtr.mapping.holder.World;
import org.mtr.mapping.mapper.BlockEntityExtension;
import org.mtr.mapping.mapper.BlockEntityRenderer;
import org.mtr.mapping.mapper.GraphicsHolder;
import org.mtr.mod.render.MainRenderer;
import org.mtr.mod.render.QueuedRenderLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.xfunny.mod.client.hint.ConnectionHintRenderer;

/**
 * 方块实体渲染前后处理：HEAD 距离裁剪（32 格外跳过渲染）、TAIL 连接提示调度。
 * <p>
 * ponytail: 原 @ModifyVariable/@ModifyArg 方案的目标 createInstanceSafe 缺方法描述符
 * （yarn 原生参数类型，双平台无法书写），改为混入 lambda$render$0——参数全为映射层类型，
 * 零 MC 原生类型引用，双平台源码兼容。
 */
@Mixin(value = BlockEntityRenderer.class, remap = false)
public class MixinBlockEntityRenderer {

    private static final String HINT_RENDER_KEY = "yte_connection_hint";

    /** 距离裁剪：32 格以外的方块跳过渲染 */
    private static final float MAX_RENDER_DISTANCE_SQ = 32 * 32;

    @Inject(method = "lambda$render$0", at = @At("HEAD"), cancellable = true)
    private void yte$cullFarEntities(BlockEntityExtension entity, float tickDelta, int light, int overlay,
            GraphicsHolder graphicsHolder, CallbackInfo ci) {
        final BlockPos pos = entity.getPos2();
        if (pos == null) {
            return;
        }
        final ClientPlayerEntity player = MinecraftClient.getInstance().getPlayerMapped();
        if (player == null) {
            return;
        }
        final double dx = pos.getX() + 0.5 - player.getPos().getXMapped();
        final double dy = pos.getY() + 0.5 - player.getPos().getYMapped();
        final double dz = pos.getZ() + 0.5 - player.getPos().getZMapped();
        if (dx * dx + dy * dy + dz * dz > MAX_RENDER_DISTANCE_SQ) {
            ci.cancel();
        }
    }

    @Inject(method = "lambda$render$0", at = @At("TAIL"))
    private void yte$afterRender(BlockEntityExtension entity, float tickDelta, int light, int overlay,
            GraphicsHolder graphicsHolder, CallbackInfo ci) {
        // 检查是否需要渲染提示，只调度一次
        final MinecraftClient client = MinecraftClient.getInstance();
        if (client.getPlayerMapped() == null) return;

        final HitResult hit = client.getCrosshairTargetMapped();
        if (hit == null) return;

        final BlockPos hitPos = getTargetBlockPos(hit);
        if (hitPos == null) return;

        final ConnectionHintRenderer.HintInfo info =
                ConnectionHintRenderer.getHintsAt(hitPos);
        if (info == null) return;

        // even → odd：标签显示在 odd 方块上方
        final ClientPlayerEntity player = client.getPlayerMapped();
        final World world = player.getEntityWorld();
        final BlockPos displayPos = ConnectionHintRenderer.getDisplayPos(world, hitPos);

        // 通过 MainRenderer 独立调度渲染，使用唯一 key 防止重复
        MainRenderer.cancelRender(new Identifier(
                top.xfunny.mod.Init.MOD_ID, HINT_RENDER_KEY));
        MainRenderer.scheduleRender(
                new Identifier(
                        top.xfunny.mod.Init.MOD_ID, HINT_RENDER_KEY),
                false,
                QueuedRenderLayer.EXTERIOR,
                (gh, cameraOffset) ->
                        ConnectionHintRenderer.renderLabel(gh, cameraOffset,
                                displayPos, info)
        );
    }

    private static BlockPos getTargetBlockPos(HitResult hit) {
        final Vector3d pos = hit.getPos();
        return new BlockPos(
                (int) Math.floor(pos.getXMapped()),
                (int) Math.floor(pos.getYMapped()),
                (int) Math.floor(pos.getZMapped())
        );
    }
}
