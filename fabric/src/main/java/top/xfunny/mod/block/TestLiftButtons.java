package top.xfunny.mod.block;

import org.mtr.mapping.holder.*;
import org.mtr.mapping.mapper.BlockEntityExtension;
import org.mtr.mapping.mapper.TextHelper;
import org.mtr.mapping.tool.HolderBase;
import org.mtr.mod.block.IBlock;
import top.xfunny.mod.Init;
import top.xfunny.mod.block.base.LiftButtonsBase;
import top.xfunny.mod.client.InitClient;
import top.xfunny.mod.config.YteLiftConfigStore;
import top.xfunny.mod.lift.LiftModeState;
import top.xfunny.mod.packet.PacketLiftFireMode;
import top.xfunny.mod.packet.PacketYTEOpenBlockEntityScreen;

import javax.annotation.Nonnull;
import java.util.List;


public class TestLiftButtons extends LiftButtonsBase {

    public TestLiftButtons() {
        super(true, true);
    }


    @Nonnull
    @Override
    public VoxelShape getOutlineShape2(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return IBlock.getVoxelShapeByDirection(4, 0, 0, 12, 16, 1, IBlock.getStatePropertySafe(state, FACING));
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

    @Nonnull
    @Override
    public ActionResult onUse2(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
                               BlockHitResult hit) {
        // 消防模式临时触发：潜行 + 空手右键
        if (world.isClient() && player.isSneaking() && player.getMainHandStack().isEmpty()) {
            yte$toggleFireMode(player, world, pos);
            return ActionResult.SUCCESS;
        }
        return IBlock.checkHoldingBrush(world, player, () -> {
            Init.REGISTRY.sendPacketToClient(ServerPlayerEntity.cast(player), new PacketYTEOpenBlockEntityScreen(pos));
        });
    }

    /**
     * 消防模式临时触发（两态）：开 = 进入消防模式，关 = 退出。
     * 点击者不本地镜像状态，统一等 {@link PacketLiftFireMode} 的 S→C 广播镜像。
     */
    private void yte$toggleFireMode(PlayerEntity player, World world, BlockPos blockPos) {
        final org.mtr.mapping.holder.BlockEntity holder = world.getBlockEntity(blockPos);
        if (holder == null || !(holder.data instanceof TestLiftButtons.BlockEntity)) {
            return;
        }
        final TestLiftButtons.BlockEntity buttons = (TestLiftButtons.BlockEntity) holder.data;
        // ponytail: 不能用 -1 作「未找到」哨兵——MTR 的 liftId 可为负数
        final boolean[] found = {false};
        final long[] fireLiftId = {0};
        buttons.forEachTrackPosition(trackPos -> {
            final org.mtr.core.data.Position position = Init.blockPosToPosition(trackPos);
            org.mtr.mod.client.MinecraftClientData.getInstance().lifts.forEach(lift -> {
                if (!found[0] && lift.getFloorIndex(position) >= 0) {
                    found[0] = true;
                    fireLiftId[0] = lift.getId();
                }
            });
        });
        if (!found[0]) {
            return;
        }
        final boolean fireActive = !LiftModeState.isFireMode(fireLiftId[0]);
        player.sendMessage(Text.cast(TextHelper.translatable(
                fireActive ? "hint.yte.fire_mode_on" : "hint.yte.fire_mode_off",
                fireActive ? YteLiftConfigStore.getFireRecallFloor(fireLiftId[0]) : "")), true);
        InitClient.REGISTRY_CLIENT.sendPacketToServer(
                new PacketLiftFireMode(fireLiftId[0], fireActive, 0, false));
    }

    /**
     * 表示一个可追踪位置的方块实体，扩展自BlockEntityExtension
     * 主要功能是通过CompoundTag来读取和写入特定位置集合
     */
    public static class BlockEntity extends BlockEntityBase {
        public BlockEntity(BlockPos pos, BlockState state) {
            super(top.xfunny.mod.BlockEntityTypes.TEST_LIFT_BUTTONS.get(), pos, state);
        }
    }
}







