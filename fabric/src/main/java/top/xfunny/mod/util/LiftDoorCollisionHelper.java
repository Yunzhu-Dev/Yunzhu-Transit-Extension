package top.xfunny.mod.util;

import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.BlockState;
import org.mtr.mapping.holder.BlockView;
import org.mtr.mapping.holder.Direction;
import org.mtr.mapping.holder.VoxelShape;
import org.mtr.mapping.holder.VoxelShapes;
import org.mtr.mod.block.BlockPSDAPGDoorBase;
import org.mtr.mod.block.IBlock;
import top.xfunny.mod.lift.LiftDoorState;

/**
 * 层门动态碰撞箱 + 光幕区域几何。
 * 授权坐标系：像素单位、随 FACING 旋转前（N 恒等；厚度 z∈[0,4] 贴门面）。
 */
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

    /** 关闭位扫掠盒（授权像素）：整列，供光幕预检判定关门路径是否有人。 */
    public static double[] sweepBoxPx() {
        return new double[]{0, 0, 0, 16, 16, 4};
    }

    /**
     * 玩家世界坐标 → 本格授权坐标（含 FACING 反变换）。
     * 返回 {ax, ay, az}，ay 即玩家脚部高度。
     */
    public static double[] toAuthoredLocal(BlockPos pos, Direction facing,
                                           double wx, double wy, double wz) {
        final double lx = wx - pos.getX();
        final double ly = wy - pos.getY();
        final double lz = wz - pos.getZ();
        double[] result;
        switch (facing) {
            case NORTH:
                result = new double[]{lx, ly, lz};
                break;
            case EAST:
                result = new double[]{lz, ly, 16 - lx};
                break;
            case SOUTH:
                result = new double[]{16 - lx, ly, 16 - lz};
                break;
            case WEST:
                result = new double[]{16 - lz, ly, lx};
                break;
            default:
                result = new double[]{lx, ly, lz};
                break;
        }
        return result;
    }

    /** 玩家 AABB（宽 0.6 高 1.8）与授权盒相交测试。 */
    public static boolean boxIntersectsPlayer(double[] box, double ax, double ay, double az) {
        return box[0] < ax + 0.3 && box[3] > ax - 0.3
                && box[1] < ay + 1.8 && box[4] > ay
                && box[2] < az + 0.3 && box[5] > az - 0.3;
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
