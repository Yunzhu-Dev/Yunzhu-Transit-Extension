package top.xfunny.mod.block.base;

import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.mtr.mapping.holder.*;
import org.mtr.mapping.mapper.*;
import org.mtr.mod.block.IBlock;
import org.mtr.mod.client.MinecraftClientData;
import top.xfunny.mod.Init;
import top.xfunny.mod.lift.LiftFloorRegistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class LiftPanelBase extends BlockExtension implements DirectionHelper, BlockWithEntity, IBlock {
    public static final BooleanProperty UNLOCKED = BooleanProperty.of("unlocked");
    private final boolean isOdd;

    public LiftPanelBase(Boolean isOdd) {
        super(BlockHelper.createBlockSettings(true, true));
        this.isOdd = isOdd;
    }

    public static void LiftCheck(BlockPos trackPosition, LiftButtonsBase.FloorLiftCallback callback) {
        // 获取实例中的所有电梯数据
        MinecraftClientData.getInstance().lifts.forEach(lift -> {
            // 获取电梯轨道位置对应的楼层索引
            final int floorIndex = lift.getFloorIndex(Init.blockPosToPosition(trackPosition));
            // 如果楼层索引非负，表示电梯中存在该楼层，执行回调函数
            if (floorIndex >= 0) {
                callback.accept(floorIndex, lift);
            }
        });
    }

    public static void hasButtonsClient(BlockPos trackPosition, boolean[] buttonStates, LiftButtonsBase.FloorLiftCallback callback) {
        // 获取实例中的所有电梯数据
        MinecraftClientData.getInstance().lifts.forEach(lift -> {
            // 获取电梯轨道位置对应的楼层索引
            final int floorIndex = lift.getFloorIndex(Init.blockPosToPosition(trackPosition));
            // 如果楼层索引大于0，则表示存在向下按钮
            if (floorIndex > 0) {
                buttonStates[0] = true;
            }
            // 如果楼层索引在有效范围内（不是顶层也不是底层），则表示存在向上按钮
            if (floorIndex >= 0 && floorIndex < lift.getFloorCount() - 1) {
                buttonStates[1] = true;
            }
            // 如果楼层索引非负，表示电梯中存在该楼层，执行回调函数
            if (floorIndex >= 0) {
                callback.accept(floorIndex, lift);
            }
        });
    }

    @Nonnull
    @Override
    // 处理按钮的使用交互
    public ActionResult onUse2(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        // 手持刷子切换锁定
        final ActionResult brushResult = IBlock.checkHoldingBrush(world, player, () -> {
            final boolean unlocked = !IBlock.getStatePropertySafe(state, UNLOCKED);
            world.setBlockState(pos, state.with(new Property<>(UNLOCKED.data), unlocked));
            player.sendMessage(Text.cast(TextHelper.translatable(unlocked ? "hint.yte.unlocked" : "hint.yte.locked")), true);
        });
        if (brushResult == ActionResult.SUCCESS) {
            return ActionResult.SUCCESS;
        }

        // 如果玩家手持电梯连接器或移除器，允许进行其他操作
        if (player.isHolding(top.xfunny.mod.Items.YTE_LIFT_BUTTONS_LINK_CONNECTOR.get()) || player.isHolding(top.xfunny.mod.Items.YTE_LIFT_BUTTONS_LINK_REMOVER.get())) {
            return ActionResult.PASS;
        }
        return ActionResult.FAIL;
    }

    @Override
    public BlockState getPlacementState2(ItemPlacementContext ctx) {
        // 获取玩家面对的方向
        final Direction facing = ctx.getPlayerFacing();
        // 根据默认状态和玩家面对的方向来设置方块状态，并返回
        if (!isOdd) {
            return IBlock.isReplaceable(ctx, facing.rotateYClockwise(), 2) ? getDefaultState2().with(new Property<>(FACING.data), facing.data).with(new Property<>(SIDE.data), IBlock.EnumSide.LEFT) : null;
        } else {
            return getDefaultState2().with(new Property<>(FACING.data), facing.data);
        }
    }

    @Override
    public void onPlaced2(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if (!world.isClient()) {
            final Direction facing = IBlock.getStatePropertySafe(state, FACING);
            if (!isOdd) {
                world.setBlockState(pos.offset(facing.rotateYClockwise()), getDefaultState2().with(new Property<>(FACING.data), facing.data).with(new Property<>(SIDE.data), EnumSide.RIGHT), 3);
            }
            world.updateNeighbors(pos, Blocks.getAirMapped());
            state.updateNeighbors(new WorldAccess(world.data), pos, 3);
        }
    }

    @Override
    public void onBreak2(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!isOdd) {
            if (IBlock.getStatePropertySafe(state, SIDE) == EnumSide.RIGHT) {
                IBlock.onBreakCreative(world, player, pos.offset(IBlock.getSideDirection(state)));
            } else if (IBlock.getStatePropertySafe(state, SIDE) == EnumSide.LEFT) {
                IBlock.onBreakCreative(world, player, pos.offset(IBlock.getSideDirection(state)));
            }
        }
        super.onBreak2(world, pos, state, player);
    }

    public static class BlockEntityBase extends BlockEntityExtension implements LiftFloorRegistry {

        // 用于在CompoundTag中标识地板位置数组的键
        private static final String KEY_TRACK_FLOOR_POS = "track_floor_pos";
        // 存储需要追踪的位置的集合
        private final ObjectOpenHashSet<BlockPos> trackPositions = new ObjectOpenHashSet<>();


        public BlockEntityBase(BlockEntityType<?> type, BlockPos blockPos, BlockState blockState) {
            super(type, blockPos, blockState);
        }

        /**
         * 从CompoundTag中读取数据，用于加载位置信息到trackPositions集合中
         *
         * @param compoundTag 包含方块实体数据的CompoundTag
         */
        @Override
        public void readCompoundTag(CompoundTag compoundTag) {
            // 清空当前位置集合，准备加载新的数据
            trackPositions.clear();

            // 从CompoundTag中读取名为KEY_TRACK_FLOOR_POS的长整型数组
            // 每个长整型代表一个BlockPos位置，将其转换并添加到trackPositions集合中
            for (final long position : compoundTag.getLongArray(KEY_TRACK_FLOOR_POS)) {
                trackPositions.add(BlockPos.fromLong(position));

            }
        }

        /**
         * 重写writeCompoundTag方法，将当前类中的数据保存到CompoundTag中
         * 主要用于序列化TrackFloor对象的数据，以便存储或传输
         *
         * @param compoundTag 一个CompoundTag对象，用于存储TrackFloor的数据
         */
        @Override
        public void writeCompoundTag(CompoundTag compoundTag) {
            // 创建一个临时的List，用于存储trackPositions的长整型表示
            final List<Long> trackPositionsList = new ArrayList<>();

            // 遍历trackPositions集合，将每个位置转换为长整型并添加到trackPositionsList中
            // 这里的转换是为了以长整型数组的形式存储这些位置信息
            trackPositions.forEach(position -> trackPositionsList.add(position.asLong()));

            // 将收集到的trackPositions长整型列表以数组的形式存储到compoundTag中
            // 使用的键是KEY_TRACK_FLOOR_POS，值是trackPositionsList数组
            compoundTag.putLongArray(KEY_TRACK_FLOOR_POS, trackPositionsList);
        }

        /**
         * 注册或取消注册一个楼层位置
         * 该方法用于控制哪些楼层位置被跟踪以及在更改时标记数据为脏
         *
         * @param pos   需要注册或取消注册的楼层位置，使用BlockPos表示
         * @param isAdd 指示是注册还是取消注册的操作类型；true表示注册，false表示取消注册
         */
        public void registerFloor(BlockPos selfPos, World world, BlockPos pos, boolean isAdd) {


            if (IBlock.getStatePropertySafe(world, getPos2(), SIDE) == EnumSide.RIGHT) {
                final BlockEntity blockEntity = world.getBlockEntity(getPos2().offset(IBlock.getStatePropertySafe(world, getPos2(), FACING).rotateYCounterclockwise()));
                if (blockEntity != null && blockEntity.data instanceof LiftPanelBase.BlockEntityBase) {
                    ((LiftPanelBase.BlockEntityBase) blockEntity.data).registerFloor(selfPos, world, pos, isAdd);
                }
            } else {
                if (isAdd) {
                    // 如果是添加操作，则将位置添加到跟踪列表中
                    trackPositions.add(pos);

                } else {
                    // 如果是非添加操作，则从跟踪列表中移除该位置
                    trackPositions.remove(pos);
                }
            }
            // 更新数据状态，标记数据为“脏”，表示需要保存或同步
            markDirty2();
        }

        /**
         * 对每个轨道位置执行给定的操作
         * <p>
         * 该方法通过遍历所有轨道位置并对其执行给定的操作来提供一种回调机制，这有助于在轨道位置集合上执行统一的操作，
         * 而无需手动实现遍历逻辑
         *
         * @param consumer 执行的操作，接受 {@link BlockPos} 作为参数每个轨道位置都将传递给这个操作
         */
        public void forEachTrackPosition(Consumer<BlockPos> consumer) {
            trackPositions.forEach(consumer);
        }

        @Override
        public ObjectOpenHashSet<BlockPos> getTrackPositions() {
            return trackPositions;
        }
    }


}



