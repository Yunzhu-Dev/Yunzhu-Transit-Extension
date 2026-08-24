package top.xfunny.mod.util;

import org.mtr.core.data.Lift;
import org.mtr.core.data.Position;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mod.client.MinecraftClientData;
import top.xfunny.mixin.MixinLiftSchema;

/**
 * 层门坐标 → 井道电梯匹配（逐层最近者胜）：层门必然位于某层高度，
 * 邻井重叠时取水平最近的一部，杜绝跨井误配。
 */
public final class LiftShaftLocator {

    private LiftShaftLocator() {
    }

    public static Long findNearest(BlockPos doorPos) {
        Long bestLiftId = null;
        double bestDistanceSq = Double.POSITIVE_INFINITY;
        for (final Lift lift : MinecraftClientData.getInstance().lifts) {
            final MixinLiftSchema schema = (MixinLiftSchema) lift;
            final double horizontalRange = Math.max(lift.getWidth(), lift.getDepth()) / 2 + 1;
            for (int i = 0; i < schema.getFloors().size(); i++) {
                final Position floorPosition = schema.getFloors().get(i).getPosition();
                final double dx = Math.abs(doorPos.getX() - floorPosition.getX());
                final double dz = Math.abs(doorPos.getZ() - floorPosition.getZ());
                final double dy = Math.abs(doorPos.getY() - (floorPosition.getY() + lift.getOffsetY()));
                if (dx <= horizontalRange && dz <= horizontalRange && dy <= 2) {
                    final double distanceSq = dx * dx + dz * dz;
                    if (distanceSq < bestDistanceSq) {
                        bestDistanceSq = distanceSq;
                        bestLiftId = lift.getId();
                    }
                }
            }
        }
        return bestLiftId;
    }
}
