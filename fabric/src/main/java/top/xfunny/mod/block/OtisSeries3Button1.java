package top.xfunny.mod.block;


import org.mtr.mapping.holder.*;
import org.mtr.mapping.mapper.BlockEntityExtension;
import org.mtr.mapping.tool.HolderBase;
import org.mtr.mod.block.IBlock;
import top.xfunny.mod.BlockEntityTypes;
import top.xfunny.mod.block.base.LiftButtonsBase;

import javax.annotation.Nonnull;
import java.util.List;

public class OtisSeries3Button1 extends LiftButtonsBase {

    public OtisSeries3Button1() {
        super(true, true);
    }


    @Nonnull
    @Override
    public VoxelShape getOutlineShape2(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return IBlock.getVoxelShapeByDirection(5.45, 0.75, 0, 10.55, 7.25, 1, IBlock.getStatePropertySafe(state, FACING));
    }


    @Nonnull
    @Override
    public BlockEntityExtension createBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new OtisSeries3Button1.BlockEntity(blockPos, blockState);
    }

    @Override
    public void addBlockProperties(List<HolderBase<?>> properties) {
        // 添加块的方向属性
        properties.add(FACING);
        // 添加块的解锁状态属性
        properties.add(UNLOCKED);
        properties.add(SINGLE);
    }


    public static class BlockEntity extends BlockEntityBase {
        public BlockEntity(BlockPos pos, BlockState state) {
            super(BlockEntityTypes.OTIS_SERIES_3_BUTTON_1.get(), pos, state);
        }
    }
}







