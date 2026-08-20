package top.xfunny.mod.lift;

import org.mtr.core.data.LiftDirection;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.mtr.mapping.holder.BlockPos;

import java.util.function.Consumer;

public interface LiftLanternController {
    void forEachLiftButtonPosition(Consumer<BlockPos> consumer);

    ObjectOpenHashSet<BlockPos> getLiftButtonPositions();

    LiftDirection getPressedButtonDirection();
}
