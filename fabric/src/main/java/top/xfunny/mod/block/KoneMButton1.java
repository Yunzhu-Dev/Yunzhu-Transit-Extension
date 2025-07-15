package top.xfunny.mod.block;

import org.mtr.mapping.holder.*;
import org.mtr.mapping.mapper.BlockEntityExtension;
import org.mtr.mapping.tool.HolderBase;
import org.mtr.mod.block.IBlock;
import top.xfunny.mod.BlockEntityTypes;
import top.xfunny.mod.block.base.LiftButtonsBase;

import javax.annotation.Nonnull;
import java.util.List;


public class KoneMButton1 extends LiftButtonsBase {

    public KoneMButton1() {
        super(true, true);
    }


    @Nonnull
    @Override
    public VoxelShape getOutlineShape2(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        final boolean single = !IBlock.getStatePropertySafe(world.getBlockState(pos), SINGLE);
        return IBlock.getVoxelShapeByDirection(single ? 6 : 7, 0, 0, single ? 10 : 9, 7.875, 0.1, IBlock.getStatePropertySafe(state, FACING));
    }

    /**
     * 创建方块实体扩展
     * 此方法用于实例化与电梯按钮相关的方块实体
     *
     * @param blockPos   方块的位置
     * @param blockState 方块的状态
     * @return 返回一个新的 {@code BlockEntityExtension} 实例，代表电梯按钮的方块实体
     */
    @Nonnull
    @Override
    public BlockEntityExtension createBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new BlockEntity(blockPos, blockState);
    }

    /**
     * 添加块属性
     * 此方法用于向块的属性列表中添加方向和解锁状态属性
     *
     * @param properties 块的属性列表，包含所有与块相关的属性
     */
    @Override
    public void addBlockProperties(List<HolderBase<?>> properties) {
        // 添加块的方向属性
        properties.add(FACING);
        // 添加块的解锁状态属性
        properties.add(UNLOCKED);
        properties.add(SINGLE);
    }

    /**
     * 表示一个可追踪位置的方块实体，扩展自BlockEntityExtension
     * 主要功能是通过CompoundTag来读取和写入特定位置集合
     */
    public static class BlockEntity extends BlockEntityBase {
        public BlockEntity(BlockPos pos, BlockState state) {
            super(BlockEntityTypes.KONE_M_BUTTON_1.get(), pos, state);
        }
    }

    /**@Override
     *public ActionResult onUse2(BlockState state, World world, BlockPos blockPos, PlayerEntity player, Hand hand, BlockHitResult hit) {
     *if (!world.isClient()) {
     *world.playSound(null, blockPos, SoundEvents.getMusicDiscBlocksMapped(), SoundCategory.BLOCKS, 1, 1);
     *}
     *return ActionResult.SUCCESS;
    }*/


}







