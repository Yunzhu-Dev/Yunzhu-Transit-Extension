package top.xfunny.mod.client.render;

import org.mtr.mapping.holder.Vector3d;
import org.mtr.mapping.mapper.GraphicsHolder;
import org.mtr.mod.render.QueuedRenderLayer;
import org.mtr.mod.render.StoredMatrixTransformations;
import top.xfunny.mod.client.view.DirectRenderer;

public class RenderLiftObjectLink {
    public static void RenderLiftObjectLink(StoredMatrixTransformations storedMatrixTransformations, Vector3d position1, Vector3d position2, boolean holdingLinker) {
        if (holdingLinker) {
            final GraphicsHolder graphicsHolder = DirectRenderer.prepare(QueuedRenderLayer.LINES, storedMatrixTransformations);
            if (graphicsHolder != null) {
                graphicsHolder.drawLineInWorld(
                        (float) position1.getXMapped(),
                        (float) position1.getYMapped(),
                        (float) position1.getZMapped(),
                        (float) position2.getXMapped(),
                        (float) position2.getYMapped(),
                        (float) position2.getZMapped(),
                        0xFF00FF00
                );
                graphicsHolder.pop();
            }
        }
    }

    public static void RenderButtonObjectLink(StoredMatrixTransformations storedMatrixTransformations, Vector3d position1, Vector3d position2, boolean holdingLinker) {
        if (holdingLinker) {
            final GraphicsHolder graphicsHolder = DirectRenderer.prepare(QueuedRenderLayer.LINES, storedMatrixTransformations);
            if (graphicsHolder != null) {
                graphicsHolder.drawLineInWorld(
                        (float) position1.getXMapped(),
                        (float) position1.getYMapped(),
                        (float) position1.getZMapped(),
                        (float) position2.getXMapped(),
                        (float) position2.getYMapped(),
                        (float) position2.getZMapped(),
                        0xFFFFFF17
                );
                graphicsHolder.pop();
            }
        }
    }

}
