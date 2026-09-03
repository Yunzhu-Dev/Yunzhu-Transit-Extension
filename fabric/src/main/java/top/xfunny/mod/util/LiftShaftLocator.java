package top.xfunny.mod.util;

import org.mtr.core.data.Lift;
import org.mtr.core.data.Position;
import org.mtr.core.tool.Vector;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.Vector3d;
import org.mtr.mod.client.MinecraftClientData;
import org.mtr.mod.render.PositionAndRotation;
import top.xfunny.mixin.MixinLiftSchema;

/**
 * 层门坐标 → 井道电梯匹配：门块必须落在轿厢正面（双面时含背面）的门洞内，
 * 左右侧面与隔壁井道的门天然不命中；Y 容差保留预平层。
 */
public final class LiftShaftLocator {

    private LiftShaftLocator() {
    }

    public static Long findForDoor(BlockPos doorPos) {
        for (final Lift lift : MinecraftClientData.getInstance().lifts) {
            final MixinLiftSchema schema = (MixinLiftSchema) lift;
            for (int i = 0; i < schema.getFloors().size(); i++) {
                if (isDoorAtLiftDoorway(doorPos, lift, schema.getFloors().get(i).getPosition())) {
                    return lift.getId();
                }
            }
        }
        return null;
    }

    /**
     * 门块中心转轿厢本地坐标判定是否在门洞内。容差：
     * X 半宽+0.5（覆盖轿厢偏移与门块厚度）、Z 1.0（墙厚）、Y 2.0（预平层）。
     */
    public static boolean isDoorAtLiftDoorway(BlockPos doorPos, Lift lift, Position floorPosition) {
        final Vector center = new Vector(
                floorPosition.getX() + lift.getOffsetX(),
                floorPosition.getY() + lift.getOffsetY(),
                floorPosition.getZ() + lift.getOffsetZ());
        final PositionAndRotation positionAndRotation = new PositionAndRotation(
                center, -Math.PI / 2 - lift.getAngle().angleRadians, 0);
        final Vector3d local = positionAndRotation.transformBackwards(
                new Vector3d(doorPos.getX() + 0.5, doorPos.getY() + 0.5, doorPos.getZ() + 0.5),
                Vector3d::rotateX, Vector3d::rotateY, Vector3d::add);

        final double halfW = lift.getWidth() / 2.0;
        final double halfD = lift.getDepth() / 2.0;
        final boolean withinWidth = Math.abs(local.getXMapped()) <= halfW + 0.5;
        final boolean frontSide = withinWidth && Math.abs(local.getZMapped() + halfD) <= 1.0;
        final boolean backSide = lift.getIsDoubleSided()
                && withinWidth && Math.abs(local.getZMapped() - halfD) <= 1.0;

        return (frontSide || backSide) && Math.abs(local.getYMapped()) <= 2.0;
    }
}
