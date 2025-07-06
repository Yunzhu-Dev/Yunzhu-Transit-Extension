package top.xfunny.mod.block;

import org.mtr.mapping.holder.*;
import org.mtr.mapping.mapper.BlockEntityExtension;
import org.mtr.mapping.tool.HolderBase;
import org.mtr.mod.block.IBlock;
import top.xfunny.mod.BlockEntityTypes;
import top.xfunny.mod.block.base.LiftPanelBase;

import javax.annotation.Nonnull;
import java.util.List;

public class KoneKSS280Screen1Even extends LiftPanelBase {
    public KoneKSS280Screen1Even() {
        super(false);
    }

    @Nonnull
    @Override
    public VoxelShape getOutlineShape2(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        switch (IBlock.getStatePropertySafe(state, SIDE)) {
            case LEFT:
                return IBlock.getVoxelShapeByDirection(14.05, 11.125, 0, 16, 12.8225, 0.5, IBlock.getStatePropertySafe(state, FACING));
            case RIGHT:
                return IBlock.getVoxelShapeByDirection(0, 11.125, 0, 1.95, 12.8225, 0.4, IBlock.getStatePropertySafe(state, FACING));
            default:
                return VoxelShapes.empty();
        }
    }


    @Nonnull
    @Override
    public BlockEntityExtension createBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new KoneKSS280Screen1Even.BlockEntity(blockPos, blockState);
    }

    @Override
    public void addBlockProperties(List<HolderBase<?>> properties) {
        // 添加块的方向属性
        properties.add(FACING);
        properties.add(SIDE);
    }

    public static class BlockEntity extends BlockEntityBase {

        public BlockEntity(BlockPos pos, BlockState state) {
            super(BlockEntityTypes.KONE_KSS280_SCREEN_1_EVEN.get(), pos, state);
        }
    }

}
