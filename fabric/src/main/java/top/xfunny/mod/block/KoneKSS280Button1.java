package top.xfunny.mod.block;

import org.mtr.mapping.holder.*;
import org.mtr.mapping.mapper.BlockEntityExtension;
import org.mtr.mapping.tool.HolderBase;
import org.mtr.mod.block.IBlock;
import top.xfunny.mod.BlockEntityTypes;
import top.xfunny.mod.block.base.LiftButtonsBase;

import javax.annotation.Nonnull;
import java.util.List;

public class KoneKSS280Button1 extends LiftButtonsBase {
    public KoneKSS280Button1() {
        super(true, true, 0.3);
    }

    @Nonnull
    @Override
    public VoxelShape getOutlineShape2(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        final boolean single = !IBlock.getStatePropertySafe(world.getBlockState(pos), SINGLE);
        return IBlock.getVoxelShapeByDirection(single ? 6.8 : 7.3, 1.775, 0, single ? 9.2 : 8.7, 8.7725, 0.5, IBlock.getStatePropertySafe(state, FACING));
    }

    @Nonnull
    @Override
    public BlockEntityExtension createBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new BlockEntity(blockPos, blockState);
    }

    @Override
    public void addBlockProperties(List<HolderBase<?>> properties) {
        properties.add(FACING);
        properties.add(UNLOCKED);
        properties.add(SINGLE);
    }

    public static class BlockEntity extends BlockEntityBase {
        public BlockEntity(BlockPos pos, BlockState state) {
            super(BlockEntityTypes.KONE_KSS280_BUTTON_1.get(), pos, state);
        }
    }
}
