package top.xfunny.mod.block;

import org.mtr.mapping.holder.*;
import org.mtr.mapping.mapper.BlockEntityExtension;
import org.mtr.mapping.tool.HolderBase;
import org.mtr.mod.block.IBlock;
import top.xfunny.mod.BlockEntityTypes;
import top.xfunny.mod.block.base.LiftButtonsBase;

import javax.annotation.Nonnull;
import java.util.List;

public class MitsubishiNexWayScreen1SegmentedOdd extends LiftButtonsBase {
    public MitsubishiNexWayScreen1SegmentedOdd() {
        super(false, true);
    }

    @Nonnull
    @Override
    public VoxelShape getOutlineShape2(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return IBlock.getVoxelShapeByDirection(0.25, 9, 0, 15.75, 13.5, 0.1, IBlock.getStatePropertySafe(state, FACING));

    }

    @Nonnull
    @Override
    public BlockEntityExtension createBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new MitsubishiNexWayScreen1SegmentedOdd.BlockEntity(blockPos, blockState);
    }

    @Override
    public void addBlockProperties(List<HolderBase<?>> properties) {
        properties.add(FACING);
    }

    public static class BlockEntity extends BlockEntityBase {
        public BlockEntity(BlockPos pos, BlockState state) {
            super(BlockEntityTypes.MITSUBISHI_NEXWAY_SCREEN_1_SEGMENTED_ODD.get(), pos, state);
        }
    }
}
