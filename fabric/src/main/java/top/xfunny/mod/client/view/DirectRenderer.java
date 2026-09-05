package top.xfunny.mod.client.view;

import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.holder.RenderLayer;
import org.mtr.mapping.holder.Vector3d;
import org.mtr.mapping.mapper.GraphicsHolder;
import org.mtr.mod.render.MoreRenderLayers;
import org.mtr.mod.render.QueuedRenderLayer;
import org.mtr.mod.render.StoredMatrixTransformations;

/**
 * 直接绘制上下文：替代 MainRenderer.scheduleRender 的延迟渲染。
 * <p>
 * 方块实体渲染器已经通过 {@link top.xfunny.mixin.MixinBlockEntityRenderer} 设置当前 GraphicsHolder，
 * view 框架直接写入该 GraphicsHolder 对应的 VertexConsumer，避免阴影通道下的重复绘制。
 */
public final class DirectRenderer {

    /** 当前是否处于 Iris/OptiFine 阴影通道，由 MixinMainRenderer 每 pass 更新一次 */
    public static boolean shadowPass;

    private static final Vector3d ZERO_OFFSET = new Vector3d(0, 0, 0);
    private static GraphicsHolder graphicsHolder;

    private DirectRenderer() {
    }

    public static void setGraphicsHolder(GraphicsHolder graphicsHolder) {
        DirectRenderer.graphicsHolder = graphicsHolder;
    }

    public static GraphicsHolder getGraphicsHolder() {
        return graphicsHolder;
    }

    /**
     * 准备绘制：设置当前 RenderLayer 对应的 VertexConsumer，并应用矩阵变换（push）。
     *
     * @return 可用的 GraphicsHolder；如果当前没有上下文或 RenderLayer 无效则返回 null
     */
    public static GraphicsHolder prepare(QueuedRenderLayer queuedRenderLayer, Identifier identifier, StoredMatrixTransformations storedMatrixTransformations) {
        return prepare(graphicsHolder, queuedRenderLayer, identifier, storedMatrixTransformations);
    }

    public static GraphicsHolder prepare(QueuedRenderLayer queuedRenderLayer, StoredMatrixTransformations storedMatrixTransformations) {
        return prepare(graphicsHolder, queuedRenderLayer, null, storedMatrixTransformations);
    }

    public static GraphicsHolder prepare(GraphicsHolder graphicsHolder, QueuedRenderLayer queuedRenderLayer, Identifier identifier, StoredMatrixTransformations storedMatrixTransformations) {
        if (graphicsHolder == null || storedMatrixTransformations == null) {
            return null;
        }
        final RenderLayer renderLayer = getRenderLayer(queuedRenderLayer, identifier);
        if (renderLayer == null) {
            return null;
        }
        graphicsHolder.createVertexConsumer(renderLayer);
        storedMatrixTransformations.transform(graphicsHolder, ZERO_OFFSET);
        return graphicsHolder;
    }

    private static RenderLayer getRenderLayer(QueuedRenderLayer queuedRenderLayer, Identifier identifier) {
        if (queuedRenderLayer == QueuedRenderLayer.LINES) {
            return RenderLayer.getLines();
        }
        if (identifier == null) {
            return null;
        }
        switch (queuedRenderLayer) {
            case LIGHT:
                return MoreRenderLayers.getLight(identifier, false);
            case LIGHT_TRANSLUCENT:
                return MoreRenderLayers.getLight(identifier, true);
            case LIGHT_2:
                return MoreRenderLayers.getLight2(identifier);
            case INTERIOR:
                return MoreRenderLayers.getInterior(identifier);
            case INTERIOR_TRANSLUCENT:
                return MoreRenderLayers.getInteriorTranslucent(identifier);
            case EXTERIOR:
                return MoreRenderLayers.getExterior(identifier);
            case EXTERIOR_TRANSLUCENT:
                return MoreRenderLayers.getExteriorTranslucent(identifier);
            default:
                return null;
        }
    }
}
