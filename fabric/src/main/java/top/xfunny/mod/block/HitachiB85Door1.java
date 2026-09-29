package top.xfunny.mod.block;

import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.BlockRenderType;
import org.mtr.mapping.holder.BlockState;
import org.mtr.mapping.holder.BlockView;
import org.mtr.mapping.holder.Item;
import org.mtr.mapping.holder.ShapeContext;
import org.mtr.mapping.holder.VoxelShape;
import org.mtr.mapping.mapper.BlockEntityExtension;
import org.mtr.mod.block.BlockPSDAPGDoorBase;
import top.xfunny.mod.BlockEntityTypes;
import top.xfunny.mod.Items;
import top.xfunny.mod.util.LiftDoorCollisionHelper;

import javax.annotation.Nonnull;

public class HitachiB85Door1 extends BlockPSDAPGDoorBase {

    @Nonnull
    @Override
    public BlockEntityExtension createBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new BlockEntity(blockPos, blockState);
    }

    @Nonnull
    @Override
    public BlockRenderType getRenderType2(BlockState state) {
        return BlockRenderType.getModelMapped();
    }

    @Nonnull
    @Override
    public Item asItem2() {
        return Items.HITACHI_B85_DOOR_1.get();
    }

    @Override
    public VoxelShape getCollisionShape2(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return LiftDoorCollisionHelper.getShape(state, world, pos,
                super.getCollisionShape2(state, world, pos, context));
    }

    public static class BlockEntity extends BlockEntityBase {

        public BlockEntity(BlockPos pos, BlockState state) {
            super(BlockEntityTypes.HITACHI_B85_DOOR_1.get(), pos, state);
        }
    }
}