package top.xfunny.mod.util;

import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.BlockState;
import org.mtr.mapping.holder.BlockView;
import org.mtr.mapping.holder.VoxelShape;
import org.mtr.mapping.holder.VoxelShapes;
import org.mtr.mod.block.BlockPSDAPGDoorBase;
import org.mtr.mod.block.IBlock;
import top.xfunny.mod.lift.LiftDoorState;

/** 层门动态碰撞箱（授权像素坐标系，N 恒等）。 */
public final class LiftDoorCollisionHelper {

    /** 面板宽 0.75 块（12px），滑动上限与渲染封顶一致。 */
    public static final double PANEL_WIDTH_PX = 12;

    private LiftDoorCollisionHelper() {
    }

    /** 当前面板盒（授权像素）：随门值从闭合位向外滑动。 */
    public static double[] panelBoxPx(BlockState state, double doorValue) {
        final double slidePx = Math.min(doorValue, LiftDoorState.DOOR_MAX_OPEN_SCALE) * 16
                * (IBlock.getStatePropertySafe(state, IBlock.SIDE) == IBlock.EnumSide.RIGHT ? 1 : -1);
        return new double[]{slidePx, 0, 0, slidePx + PANEL_WIDTH_PX, 16, 4};
    }

    public static VoxelShape getShape(BlockState state, BlockView world, BlockPos pos, VoxelShape closedShape) {
        final org.mtr.mapping.holder.BlockEntity entity = world.getBlockEntity(pos);
        if (entity == null || !(entity.data instanceof BlockPSDAPGDoorBase.BlockEntityBase)) {
            return VoxelShapes.empty();
        }
        // 与原版门控一致：仅客户端计算碰撞，服务端恒空
        if (entity.getWorld() == null || !entity.getWorld().isClient()) {
            return VoxelShapes.empty();
        }
        final double v = ((BlockPSDAPGDoorBase.BlockEntityBase) entity.data).getDoorValue();
        if (v <= 0) {
            return closedShape;
        }
        if (v >= LiftDoorState.DOOR_MAX_OPEN_SCALE) {
            return VoxelShapes.empty();
        }
        final double[] box = panelBoxPx(state, v);
        return IBlock.getVoxelShapeByDirection(box[0], box[1], box[2], box[3], box[4], box[5],
                IBlock.getStatePropertySafe(state, BlockPSDAPGDoorBase.FACING));
    }
}
