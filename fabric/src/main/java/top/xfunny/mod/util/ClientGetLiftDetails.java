package top.xfunny.mod.util;

import org.mtr.core.data.Lift;
import org.mtr.core.data.LiftDirection;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectObjectImmutablePair;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.World;
import org.mtr.mod.render.RenderLifts;
import top.xfunny.mod.block.LiftTrackMagneticVane;
import top.xfunny.mod.lift.LiftDisplayDirection;

public class ClientGetLiftDetails {
    public static ObjectObjectImmutablePair<LiftDirection, ObjectObjectImmutablePair<String, String>> getLiftDetails(World world, Lift lift, BlockPos blockPos) {
        final LiftTrackMagneticVane.BlockEntity magneticVane = LiftTrackMagneticVaneDisplayHelper.getDisplayedMagneticVane(world, lift);
        if (magneticVane != null) {
            ((LiftDisplayDirection) lift).yte$resetArrivalDirectionDelay();
            return new ObjectObjectImmutablePair<>(lift.getDirection(),
                    new ObjectObjectImmutablePair<>(magneticVane.getFloorNumber(), magneticVane.getFloorDescription()));
        }
        return RenderLifts.getLiftDetails(world, lift, blockPos);
    }
}
