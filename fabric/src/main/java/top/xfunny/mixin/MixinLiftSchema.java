package top.xfunny.mixin;

import org.mtr.core.data.LiftInstruction;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = org.mtr.core.generated.data.LiftSchema.class, remap = false)
public interface MixinLiftSchema {
    @Accessor("speed")
    double getSpeed();

    @Accessor("instructions")
    ObjectArrayList<LiftInstruction> getInstructions();
}