package top.xfunny.mod.lift;

import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.World;

public interface LiftFloorRegistry {
    void registerFloor(BlockPos selfPos, World world, BlockPos blockPos, boolean isAdd);

    ObjectOpenHashSet<BlockPos> getTrackPositions();
}
