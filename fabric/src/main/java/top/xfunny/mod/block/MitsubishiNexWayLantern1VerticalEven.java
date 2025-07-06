package top.xfunny.mod.block;

import org.mtr.mapping.holder.*;
import org.mtr.mapping.mapper.BlockEntityExtension;
import org.mtr.mapping.tool.HolderBase;
import org.mtr.mod.block.IBlock;
import top.xfunny.mod.BlockEntityTypes;
import top.xfunny.mod.block.base.LiftButtonsBase;

import javax.annotation.Nonnull;
import java.util.List;

public class MitsubishiNexWayLantern1VerticalEven extends LiftButtonsBase {
    public MitsubishiNexWayLantern1VerticalEven() {
        super(false, false);
    }

    @Nonnull
    @Override
    public VoxelShape getOutlineShape2(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        switch (IBlock.getStatePropertySafe(state, SIDE)) {
            case LEFT:
                return IBlock.getVoxelShapeByDirection(13.75, 7, 0, 16, 15.5, 0.1, IBlock.getStatePropertySafe(state, FACING));

            case RIGHT:
                return IBlock.getVoxelShapeByDirection(0, 7, 0, 2.25, 15.5, 0.1, IBlock.getStatePropertySafe(state, FACING));

        }
        return VoxelShapes.empty();
    }

    @Nonnull
    @Override
    public BlockEntityExtension createBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new MitsubishiNexWayLantern1VerticalEven.BlockEntity(blockPos, blockState);
    }

    @Override
    public void addBlockProperties(List<HolderBase<?>> properties) {
        properties.add(FACING);
        properties.add(SIDE);
    }

    public static class BlockEntity extends BlockEntityBase {
        public BlockEntity(BlockPos pos, BlockState state) {
            super(BlockEntityTypes.MITSUBISHI_NEXWAY_LANTERN_1_VERTICAL_EVEN.get(), pos, state);
        }
    }
}
