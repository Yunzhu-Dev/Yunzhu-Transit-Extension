package top.xfunny.mod.client.view;

import org.mtr.mapping.holder.*;
import org.mtr.mod.block.BlockLiftTrackFloor;
import org.mtr.mod.block.IBlock;
import org.mtr.mod.render.MainRenderer;
import org.mtr.mod.render.QueuedRenderLayer;
import org.mtr.mod.render.StoredMatrixTransformations;
import top.xfunny.mod.block.base.LiftButtonsBase;
import top.xfunny.mod.block.base.LiftDestinationDispatchTerminalBase;

import static org.mtr.mapping.mapper.DirectionHelper.FACING;

public class LineComponent {
    private World world;
    private BlockPos blockPos;
    private BlockState blockState;
    private Direction facing;


    public void RenderLine(Boolean holdingLinker, BlockPos trackPosition) {
        this.blockState = world.getBlockState(blockPos);
        this.facing = IBlock.getStatePropertySafe(blockState, FACING);

        StoredMatrixTransformations storedMatrixTransformations = new StoredMatrixTransformations(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5);

        if (world.getBlockState(trackPosition).getBlock().data instanceof BlockLiftTrackFloor) {
            final Direction trackFacing = IBlock.getStatePropertySafe(world, trackPosition, FACING);
            RenderLiftObjectLink(
                    storedMatrixTransformations,
                    new Vector3d(facing.getOffsetX() / 2F, 0.5, facing.getOffsetZ() / 2F),
                    new Vector3d(trackPosition.getX() - blockPos.getX() + trackFacing.getOffsetX() / 2F, trackPosition.getY() - blockPos.getY() + 0.5, trackPosition.getZ() - blockPos.getZ() + trackFacing.getOffsetZ() / 2F),
                    holdingLinker
            );
        }
    }

    public void RenderLine(Boolean holdingLinker, BlockPos buttonPosition, Boolean isLantern) {
        this.blockState = world.getBlockState(blockPos);
        this.facing = IBlock.getStatePropertySafe(blockState, FACING);

        StoredMatrixTransformations storedMatrixTransformations = new StoredMatrixTransformations(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5);

        if (world.getBlockState(buttonPosition).getBlock().data instanceof LiftButtonsBase || world.getBlockState(buttonPosition).getBlock().data instanceof LiftDestinationDispatchTerminalBase) {
            final Direction trackFacing = IBlock.getStatePropertySafe(world, buttonPosition, FACING);
            RenderButtonObjectLink(
                    storedMatrixTransformations,
                    new Vector3d(facing.getOffsetX() / 2F, 0.5, facing.getOffsetZ() / 2F),
                    new Vector3d(buttonPosition.getX() - blockPos.getX() + trackFacing.getOffsetX() / 2F, buttonPosition.getY() - blockPos.getY() + 0.5, buttonPosition.getZ() - blockPos.getZ() + trackFacing.getOffsetZ() / 2F),
                    holdingLinker
            );
        }
    }

    public void setBasicsAttributes(World world, BlockPos blockPos) {
        this.world = world;
        this.blockPos = blockPos;
    }

    public void RenderLiftObjectLink(StoredMatrixTransformations storedMatrixTransformations, Vector3d position1, Vector3d position2, boolean holdingLinker) {
        if (holdingLinker) {
            MainRenderer.scheduleRender(QueuedRenderLayer.LINES, (graphicsHolder, offset) -> {
                storedMatrixTransformations.transform(graphicsHolder, offset);
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
            });
        }
    }

    public void RenderButtonObjectLink(StoredMatrixTransformations storedMatrixTransformations, Vector3d position1, Vector3d position2, boolean holdingLinker) {
        if (holdingLinker) {
            MainRenderer.scheduleRender(QueuedRenderLayer.LINES, (graphicsHolder, offset) -> {
                storedMatrixTransformations.transform(graphicsHolder, offset);
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
            });
        }
    }

}
