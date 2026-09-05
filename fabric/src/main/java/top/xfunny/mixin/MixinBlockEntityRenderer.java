package top.xfunny.mixin;

import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.ClientPlayerEntity;
import org.mtr.mapping.holder.HitResult;
import org.mtr.mapping.holder.MinecraftClient;
import org.mtr.mapping.holder.Vector3d;
import org.mtr.mapping.holder.World;
import org.mtr.mapping.mapper.BlockEntityRenderer;
import org.mtr.mapping.mapper.GraphicsHolder;
import org.mtr.mod.render.MainRenderer;
import org.mtr.mod.render.QueuedRenderLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import top.xfunny.mod.client.hint.ConnectionHintRenderer;
import top.xfunny.mod.client.view.DirectRenderer;

import java.util.function.Consumer;

/**
 * 通过 @ModifyArg 包装 GraphicsHolder.createInstanceSafe 的 Consumer 参数，
 * 在每次方块实体渲染后检查是否需要显示提示，通过 MainRenderer.scheduleRender
 * 以独立的 GraphicsHolder 在目标方块上方渲染标签。
 * <p>
 * 零 MC 原生类型引用，双平台源码兼容。
 */
@Mixin(value = BlockEntityRenderer.class, remap = false)
public class MixinBlockEntityRenderer {

    private static final String HINT_RENDER_KEY = "yte_connection_hint";

    /** 距离裁剪：32 格以外的方块跳过渲染 */
    private static final float MAX_RENDER_DISTANCE_SQ = 32 * 32;

    /** ThreadLocal 不好跨 @ModifyVariable → @ModifyArg 传递，直接用实例字段（渲染单线程） */
    private org.mtr.mapping.mapper.BlockEntityExtension currentEntity;

    @ModifyVariable(method = "render",
            at = @At(value = "INVOKE", target = "Lorg/mtr/mapping/mapper/GraphicsHolder;createInstanceSafe", remap = false),
            index = 1, remap = false)
    private org.mtr.mapping.mapper.BlockEntityExtension captureEntity(org.mtr.mapping.mapper.BlockEntityExtension entity) {
        this.currentEntity = entity;
        return entity;
    }

    @ModifyArg(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/mtr/mapping/mapper/GraphicsHolder;createInstanceSafe",
                    remap = false
            ),
            index = 2,
            remap = false
    )
    private Consumer<GraphicsHolder> wrapConsumer(Consumer<GraphicsHolder> original) {
        // 距离裁剪：32 格外不渲染
        final org.mtr.mapping.mapper.BlockEntityExtension entity = this.currentEntity;
        if (entity != null) {
            final BlockPos pos = entity.getPos2();
            if (pos != null) {
                final ClientPlayerEntity player = MinecraftClient.getInstance().getPlayerMapped();
                if (player != null) {
                    final double dx = pos.getX() + 0.5 - player.getPos().getXMapped();
                    final double dy = pos.getY() + 0.5 - player.getPos().getYMapped();
                    final double dz = pos.getZ() + 0.5 - player.getPos().getZMapped();
                    if (dx * dx + dy * dy + dz * dz > MAX_RENDER_DISTANCE_SQ) {
                        return gh -> {}; // no-op
                    }
                }
            }
        }

        final boolean isYteRenderer = this.getClass().getName().startsWith("top.xfunny");

        return gh -> {
            // 阴影通道跳过 yte 方块实体的重渲染（按钮/面板/厅灯/屏幕是自发光件，不应投阴影）
            if (!(isYteRenderer && DirectRenderer.shadowPass)) {
                DirectRenderer.setGraphicsHolder(gh);
                try {
                    original.accept(gh);
                } finally {
                    DirectRenderer.setGraphicsHolder(null);
                }
            }

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
            MainRenderer.cancelRender(new org.mtr.mapping.holder.Identifier(
                    top.xfunny.mod.Init.MOD_ID, HINT_RENDER_KEY));
            MainRenderer.scheduleRender(
                    new org.mtr.mapping.holder.Identifier(
                            top.xfunny.mod.Init.MOD_ID, HINT_RENDER_KEY),
                    false,
                    QueuedRenderLayer.EXTERIOR,
                    (graphicsHolder, cameraOffset) ->
                            ConnectionHintRenderer.renderLabel(graphicsHolder, cameraOffset,
                                    displayPos, info)
            );
        };
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
