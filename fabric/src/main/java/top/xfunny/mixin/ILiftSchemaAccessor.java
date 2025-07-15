package top.xfunny.mixin;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.mtr.core.data.LiftInstruction;

public interface ILiftSchemaAccessor {
    double getSpeed();

    ObjectArrayList<LiftInstruction> getInstructions();
}
