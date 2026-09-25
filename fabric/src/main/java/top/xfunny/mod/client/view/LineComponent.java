package top.xfunny.mod.client.view;

import org.mtr.mapping.holder.*;
import org.mtr.mapping.mapper.GraphicsHolder;
import org.mtr.mod.block.BlockLiftTrackFloor;
import org.mtr.mod.block.IBlock;
import org.mtr.mod.render.QueuedRenderLayer;
import org.mtr.mod.render.StoredMatrixTransformations;
import top.xfunny.mod.block.base.LiftButtonsBase;
import top.xfunny.mod.block.base.LiftDestinationDispatchTerminalBase;

import static org.mtr.mapping.mapper.DirectionHelper.FACING;

public class LineComponent {
    private World world;
    private BlockPos blockPos;

    public void RenderLine(Boolean holdingLinker, BlockPos trackPosition) {
        final StoredMatrixTransformations storedMatrixTransformations =
                new StoredMatrixTransformations(0.5, 0, 0.5);

        if (world.getBlockState(trackPosition).getBlock().data instanceof BlockLiftTrackFloor) {
            final Vector3d srcCenter = getCollisionBoxCenter(blockPos);
            final Vector3d tgtCenter = getCollisionBoxCenter(trackPosition);

            RenderLiftObjectLink(
                    storedMatrixTransformations,
                    toRelative(srcCenter),
                    toRelative(tgtCenter),
                    holdingLinker
            );
        }
    }

    public void RenderLine(Boolean holdingLinker, BlockPos buttonPosition, Boolean isLantern) {
        final StoredMatrixTransformations storedMatrixTransformations =
                new StoredMatrixTransformations(0.5, 0, 0.5);

        final Block targetBlock = world.getBlockState(buttonPosition).getBlock();
        if (targetBlock.data instanceof LiftButtonsBase
                || targetBlock.data instanceof LiftDestinationDispatchTerminalBase) {
            final Vector3d srcCenter = getCollisionBoxCenter(blockPos);
            final Vector3d tgtCenter = getCollisionBoxCenter(buttonPosition);

            RenderButtonObjectLink(
                    storedMatrixTransformations,
                    toRelative(srcCenter),
                    toRelative(tgtCenter),
                    holdingLinker
            );
        }
    }

    public void setBasicsAttributes(World world, BlockPos blockPos) {
        this.world = world;
        this.blockPos = blockPos;
    }

    // ======================== helpers ========================

    /** 获取方块碰撞箱的世界坐标中心 */
    private Vector3d getCollisionBoxCenter(BlockPos pos) {
        try {
            final BlockState state = world.getBlockState(pos);
            final BlockView blockView = new BlockView(world.data);
            final VoxelShape shape = state.getOutlineShape(blockView, pos);
            if (shape == null || shape.isEmpty()) {
                return fallbackCenter(pos);
            }
            final Box box = shape.getBoundingBox();
            final Vector3d local = box.getCenter();
            return new Vector3d(
                    pos.getX() + local.getXMapped(),
                    pos.getY() + local.getYMapped(),
                    pos.getZ() + local.getZMapped()
            );
        } catch (Exception ignored) {
            return fallbackCenter(pos);
        }
    }

    /** 方块中心兜底 */
    private Vector3d fallbackCenter(BlockPos pos) {
        return new Vector3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
    }

    /** 世界坐标 → 相对于 StoredMatrixTransformations 原点的坐标 */
    private Vector3d toRelative(Vector3d worldPos) {
        return new Vector3d(
                worldPos.getXMapped() - (blockPos.getX() + 0.5),
                worldPos.getYMapped() - blockPos.getY(),
                worldPos.getZMapped() - (blockPos.getZ() + 0.5)
        );
    }

    // ======================== rendering ========================

    public void RenderLiftObjectLink(StoredMatrixTransformations storedMatrixTransformations,
                                     Vector3d position1, Vector3d position2, boolean holdingLinker) {
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

    public void RenderButtonObjectLink(StoredMatrixTransformations storedMatrixTransformations,
                                        Vector3d position1, Vector3d position2, boolean holdingLinker) {
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
