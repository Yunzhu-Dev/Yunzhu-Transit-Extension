package top.xfunny.mod.block;




import org.mtr.mapping.holder.*;
import org.mtr.mapping.mapper.*;
import org.mtr.mapping.tool.HolderBase;
import org.mtr.mod.block.BlockWaterloggable;
import org.mtr.mod.block.IBlock;
import top.xfunny.mod.BlockEntityTypes;
import top.xfunny.mod.Init;
import top.xfunny.mod.Items;
import top.xfunny.mod.packet.PacketYTEOpenBlockEntityScreen;

import javax.annotation.Nonnull;
import java.util.List;

public class HallCallBox extends BlockWaterloggable implements DirectionHelper, BlockWithEntity {
    public static final BooleanProperty UNLOCKED = BooleanProperty.of("unlocked");


    public HallCallBox() {
        super(BlockHelper.createBlockSettings(true, true));
    }


    @Nonnull
    @Override
    public BlockState getPlacementState2(ItemPlacementContext ctx) {
        final Direction facing = ctx.getPlayerFacing();
        return super.getPlacementState2(ctx).with(new Property<>(FACING.data), facing.data);
    }


    @Nonnull
    @Override
    // 碰撞箱
    public VoxelShape getOutlineShape2(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return IBlock.getVoxelShapeByDirection(4, 0, 0, 12, 16, 1, IBlock.getStatePropertySafe(state, FACING));
    }


    @Override
    // 注册方块自定义属性
    public void addBlockProperties(List<HolderBase<?>> properties) {
        super.addBlockProperties(properties);
        properties.add(FACING);
        properties.add(UNLOCKED);
    }


    @Nonnull
    @Override
    public ActionResult onUse2(BlockState state, World world, BlockPos pos, PlayerEntity player,
                               Hand hand, BlockHitResult hit) {
        // 刷子右击行为，打开config screen
        final ActionResult brushResult = IBlock.checkHoldingBrush(world, player, () -> {
            final boolean unlocked = !IBlock.getStatePropertySafe(state, UNLOCKED);

            world.setBlockState(pos, state.with(new Property<>(UNLOCKED.data), unlocked));
            Init.REGISTRY.sendPacketToClient(ServerPlayerEntity.cast(player), new PacketYTEOpenBlockEntityScreen(pos));
        });
        if (brushResult == ActionResult.SUCCESS) {
            return ActionResult.SUCCESS;
        }

        // 手持 linker 时放行，交给连线系统
        if (player.isHolding(Items.YTE_LIFT_BUTTONS_LINK_CONNECTOR.get())
                || player.isHolding(Items.YTE_LIFT_BUTTONS_LINK_REMOVER.get())
                || player.isHolding(Items.YTE_GROUP_LIFT_BUTTONS_LINK_CONNECTOR.get())
                || player.isHolding(Items.YTE_GROUP_LIFT_BUTTONS_LINK_REMOVER.get())) {
            return ActionResult.PASS;
        }

        // 3) TODO: 呼梯逻辑 → 下一步接 HallCallService
        return ActionResult.FAIL;
    }










    @Nonnull
    @Override
    public BlockEntityExtension createBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new BlockEntity(blockPos, blockState);
    }

    public static class BlockEntity extends BlockEntityExtension{
        public BlockEntity(BlockPos blockPos, BlockState blockState) {
            super(BlockEntityTypes.HALL_CALL_BOX.get(),blockPos, blockState);
        }

    }

}
