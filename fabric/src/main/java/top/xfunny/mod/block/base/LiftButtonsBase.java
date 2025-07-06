package top.xfunny.mod.block.base;

import org.mtr.core.data.Lift;
import org.mtr.core.data.LiftDirection;
import org.mtr.core.operation.PressLift;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.mtr.mapping.holder.*;
import org.mtr.mapping.holder.Blocks;
import org.mtr.mapping.mapper.*;
import org.mtr.mod.InitClient;
import org.mtr.mod.block.IBlock;
import org.mtr.mod.client.MinecraftClientData;
import org.mtr.mod.packet.PacketPressLiftButton;
import top.xfunny.mod.*;
import top.xfunny.mod.Items;
import top.xfunny.mod.keymapping.DefaultButtonsKeyMapping;
import top.xfunny.mod.util.TransformPositionX;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.mtr.core.data.LiftDirection.NONE;

;

public abstract class LiftButtonsBase extends BlockExtension implements DirectionHelper, BlockWithEntity, IBlock {
    public static final BooleanProperty UNLOCKED = BooleanProperty.of("unlocked");
    public static final BooleanProperty SINGLE = BooleanProperty.of("single");
    public static boolean allowPress;
    private final boolean isOdd;
    private double median = 0.25;//判定按下上、下按钮的分界线


    public LiftButtonsBase(boolean allowPress, boolean isOdd) {
        super(BlockHelper.createBlockSettings(true, true));
        this.isOdd = isOdd;
        this.allowPress = allowPress;
    }

    public LiftButtonsBase(boolean allowPress, boolean isOdd, double median) {
        super(BlockHelper.createBlockSettings(true, true));
        this.isOdd = isOdd;
        this.allowPress = allowPress;
        this.median = median;
    }

    /**
     * 检查电梯轨道位置是否存在上下按钮的客户端方法
     * 本方法主要用于确定在给定的电梯轨道位置是否有上下按钮，以及通知相关的回调函数
     *
     * @param trackPosition the position of the lift floor track 电梯轨道的位置
     * @param callback      a callback for the lift and floor index, only run if the lift floor track exists in the lift
     *                      关于电梯和楼层索引的回调函数，只有当电梯地板轨道存在于电梯中时才运行
     */
    public static void hasButtonsClient(BlockPos trackPosition, LiftButtonDescriptor descriptor, FloorLiftCallback callback) {
        // 获取实例中的所有电梯数据
        MinecraftClientData.getInstance().lifts.forEach(lift -> {
            // 获取电梯轨道位置对应的楼层索引
            final int floorIndex = lift.getFloorIndex(Init.blockPosToPosition(trackPosition));
            // 如果楼层索引大于0，则表示存在向下按钮
            if (floorIndex > 0) {
                descriptor.setHasDownButton(true);
            }

            if (floorIndex >= 0 && floorIndex < lift.getFloorCount() - 1) {
                descriptor.setHasUpButton(true);
            }

            if (floorIndex >= 0) {
                callback.accept(floorIndex, lift);
            }
        });
    }

    @Nonnull
    @Override
    // 处理按钮的使用交互
    public ActionResult onUse2(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {


        // 检查玩家是否拿着刷子，并尝试更新按钮状态
        final ActionResult result = IBlock.checkHoldingBrush(world, player, () -> {
            final boolean unlocked = !IBlock.getStatePropertySafe(state, UNLOCKED);
            world.setBlockState(pos, state.with(new Property<>(UNLOCKED.data), unlocked));
            player.sendMessage(Text.of((unlocked ? "已解锁" : "已锁定")), true);
            //player.sendMessage((unlocked ? TranslationProvider.GUI_MTR_LIFT_BUTTONS_UNLOCKED : TranslationProvider.GUI_MTR_LIFT_BUTTONS_LOCKED).getText(), true);
        });

        if (result == ActionResult.SUCCESS) {
            return ActionResult.SUCCESS;
        } else {
            // 如果玩家手持电梯连接器或移除器，允许进行其他操作
            if (player.isHolding(Items.YTE_LIFT_BUTTONS_LINK_CONNECTOR.get()) || player.isHolding(Items.YTE_LIFT_BUTTONS_LINK_REMOVER.get()) || player.isHolding(Items.YTE_GROUP_LIFT_BUTTONS_LINK_CONNECTOR.get()) || player.isHolding(Items.YTE_GROUP_LIFT_BUTTONS_LINK_REMOVER.get())) {
                return ActionResult.PASS;
            } else {
                // 检查按钮是否解锁，并处理客户端按钮按下逻辑
                final boolean unlocked = IBlock.getStatePropertySafe(state, UNLOCKED);
                final double hitY = MathHelper.fractionalPart(hit.getPos().getYMapped());
                final BlockEntity blockEntity = world.getBlockEntity(pos);
                final BlockEntityBase data = (BlockEntityBase) blockEntity.data;
                final DefaultButtonsKeyMapping keyMapping = data.getKeyMapping();
                final String focusButton = keyMapping.mapping(TransformPositionX.transform(MathHelper.fractionalPart(hit.getPos().getXMapped()), MathHelper.fractionalPart(hit.getPos().getZMapped()), IBlock.getStatePropertySafe(state, FACING)), hitY);

                Init.LOGGER.info(focusButton);

                // 当按钮已解锁，且玩家点击到按钮区域时执行
                if (unlocked) {
                    // 客户端按钮按下特殊处理
                    if (world.isClient() && !focusButton.equals("null")) {
                        ObjectOpenHashSet<BlockPos> connectedLanternPositions = data.getLiftButtonPositions();

                        // 检查并记录上下按钮状态
                        LiftButtonDescriptor descriptor = new LiftButtonDescriptor(false, false);
                        data.trackPositions.forEach(trackPosition -> LiftButtonsBase.hasButtonsClient(trackPosition, descriptor, (floor, lift) -> {
                        }));

                        connectedLanternPositions.forEach(lanternPos -> {
                            BlockEntity lanternBlockEntity = world.getBlockEntity(lanternPos);
                            if (lanternBlockEntity != null && lanternBlockEntity.data instanceof LiftButtonsBase.BlockEntityBase) {
                                LiftButtonsBase.BlockEntityBase lanternData = (LiftButtonsBase.BlockEntityBase) lanternBlockEntity.data;

                                if (descriptor.hasDownButton() && descriptor.hasUpButton()) {
                                    // 双向按钮情况：根据点击位置设置方向
                                    if (focusButton.equals("down")) {
                                        lanternData.setPressedButtonDirection(LiftDirection.DOWN);
                                    } else if (focusButton.equals("up")) {
                                        lanternData.setPressedButtonDirection(LiftDirection.UP);
                                    }
                                } else {
                                    // 单向按钮情况：统一设置为存在的方向
                                    lanternData.setPressedButtonDirection(descriptor.hasDownButton() ? LiftDirection.DOWN : LiftDirection.UP);
                                }
                            }
                        });

                        // 确认呼梯方向
                        if (descriptor.hasDownButton() && descriptor.hasUpButton()) {
                            data.liftDirection = focusButton.equals("up") ? LiftDirection.UP : focusButton.equals("down") ? LiftDirection.DOWN : NONE;
                        } else {
                            data.liftDirection = descriptor.hasDownButton() ? LiftDirection.DOWN : LiftDirection.UP;
                        }

                        // 创建并发送按电梯按钮事件
                        final PressLift pressLift = new PressLift();
                        data.trackPositions.forEach(trackPosition -> pressLift.add(Init.blockPosToPosition(trackPosition), data.liftDirection));

                        InitClient.REGISTRY_CLIENT.sendPacketToServer(new PacketPressLiftButton(pressLift));
                        return ActionResult.SUCCESS;
                    }

                    return ActionResult.SUCCESS;
                } else {
                    System.out.println(allowPress);
                    return ActionResult.FAIL;
                }
            }
        }
    }

    @Override
    public BlockState getPlacementState2(ItemPlacementContext ctx) {
        // 获取玩家面对的方向
        final Direction facing = ctx.getPlayerFacing();
        // 根据默认状态和玩家面对的方向来设置方块状态，并返回
        if (!isOdd) {
            return IBlock.isReplaceable(ctx, facing.rotateYClockwise(), 2) ? getDefaultState2().with(new Property<>(FACING.data), facing.data).with(new Property<>(SIDE.data), EnumSide.LEFT) : null;
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

    @FunctionalInterface
    public interface FloorLiftCallback {
        void accept(int floor, Lift lift);
    }

    public static class LiftButtonDescriptor {
        private boolean hasUpButton;
        private boolean hasDownButton;

        public LiftButtonDescriptor(boolean hasUpButton, boolean hasDownButton) {
            this.hasDownButton = hasDownButton;
            this.hasUpButton = hasUpButton;
        }

        public boolean hasUpButton() {
            return hasUpButton;
        }

        public boolean hasDownButton() {
            return hasDownButton;
        }

        public void setHasUpButton(boolean hasUpButton) {
            this.hasUpButton = hasUpButton;
        }

        public void setHasDownButton(boolean hasDownButton) {
            this.hasDownButton = hasDownButton;
        }
    }

    public static class BlockEntityBase extends BlockEntityExtension implements LiftFloorRegistry, ButtonRegistry, LiftLanternController {
        // 用于在CompoundTag中标识地板位置数组的键
        private static final String KEY_TRACK_FLOOR_POS = "track_floor_pos";

        private static final String KEY_LIFT_BUTTON_POSITIONS = "lift_button_position";
        public final ObjectOpenHashSet<BlockPos> liftButtonPositions = new ObjectOpenHashSet<>();
        // 存储需要追踪的位置的集合
        private final ObjectOpenHashSet<BlockPos> trackPositions = new ObjectOpenHashSet<>();
        public LiftDirection liftDirection = NONE;

        public BlockPos selfPos;

        private LiftDirection pressedButtonDirection;

        private DefaultButtonsKeyMapping keyMapping = new DefaultButtonsKeyMapping();

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
            liftButtonPositions.clear();

            // 从CompoundTag中读取名为KEY_TRACK_FLOOR_POS的长整型数组
            // 每个长整型代表一个BlockPos位置，将其转换并添加到trackPositions集合中
            for (final long position : compoundTag.getLongArray(KEY_TRACK_FLOOR_POS)) {
                trackPositions.add(BlockPos.fromLong(position));
            }

            for (final long position : compoundTag.getLongArray(KEY_LIFT_BUTTON_POSITIONS)) {
                liftButtonPositions.add(BlockPos.fromLong(position));
            }
        }

        /**
         * 重写writeCompoundTag方法，将当前类中的数据保存到CompoundTag中
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
            trackPositions.forEach(position -> {
                trackPositionsList.add(position.asLong());
            });
            // 将收集到的trackPositions长整型列表以数组的形式存储到compoundTag中
            // 使用的键是KEY_TRACK_FLOOR_POS，值是trackPositionsList数组
            compoundTag.putLongArray(KEY_TRACK_FLOOR_POS, trackPositionsList);

            final List<Long> liftButtonPositionsList = new ArrayList<>();
            liftButtonPositions.forEach(position -> {
                liftButtonPositionsList.add(position.asLong());
            });
            compoundTag.putLongArray(KEY_LIFT_BUTTON_POSITIONS, liftButtonPositionsList);

        }

        public DefaultButtonsKeyMapping getKeyMapping() {
            return keyMapping;
        }

        public void setKeyMapping(DefaultButtonsKeyMapping keyMapping) {
            this.keyMapping = keyMapping;
        }

        /**
         * 注册或取消注册一个楼层位置
         * 该方法用于控制哪些楼层位置被跟踪以及在更改时标记数据为脏
         *
         * @param pos   需要注册或取消注册的楼层位置，使用BlockPos表示
         * @param isAdd 指示是注册还是取消注册的操作类型；true表示注册，false表示取消注册
         */
        public void registerFloor(BlockPos selfPos, World world, BlockPos pos, boolean isAdd) {
            this.selfPos = selfPos;
            final boolean single = IBlock.getStatePropertySafe(world.getBlockState(selfPos), SINGLE);
            if (IBlock.getStatePropertySafe(world, getPos2(), SIDE) == EnumSide.RIGHT) {
                final BlockEntity blockEntity = world.getBlockEntity(getPos2().offset(IBlock.getStatePropertySafe(world, getPos2(), FACING).rotateYCounterclockwise()));
                if (blockEntity != null && blockEntity.data instanceof BlockEntityBase) {
                    ((BlockEntityBase) blockEntity.data).registerFloor(selfPos, world, pos, isAdd);
                }
            } else {
                if (isAdd) {
                    // 如果是添加操作，则将位置添加到跟踪列表中
                    trackPositions.add(pos);
                    if (trackPositions.size() != 1 && single) {
                        final boolean single1 = !IBlock.getStatePropertySafe(world.getBlockState(selfPos), SINGLE);
                        world.setBlockState(selfPos, world.getBlockState(selfPos).with(new Property<>(SINGLE.data), single1));
                    }
                } else {
                    // 如果是非添加操作，则从跟踪列表中移除该位置
                    trackPositions.remove(pos);
                    if (trackPositions.size() == 1 && !single) {
                        final boolean single1 = !IBlock.getStatePropertySafe(world.getBlockState(selfPos), SINGLE);
                        world.setBlockState(selfPos, world.getBlockState(selfPos).with(new Property<>(SINGLE.data), single1));
                    }
                }
            }
            // 更新数据状态，标记数据为“脏”，表示需要保存或同步
            markDirty2();
        }

        @Override
        public void registerButton(World world, BlockPos blockPos, boolean isAdd) {

            if (IBlock.getStatePropertySafe(world, getPos2(), SIDE) == EnumSide.RIGHT) {
                final BlockEntity blockEntity = world.getBlockEntity(getPos2().offset(IBlock.getStatePropertySafe(world, getPos2(), FACING).rotateYCounterclockwise()));
                if (blockEntity != null && blockEntity.data instanceof BlockEntityBase) {
                    ((BlockEntityBase) blockEntity.data).registerButton(world, blockPos, isAdd);
                }
            } else {
                if (isAdd) {
                    // 如果是添加操作，则将位置添加到跟踪列表中
                    liftButtonPositions.add(blockPos);
                } else {
                    // 如果是非添加操作，则从跟踪列表中移除该位置
                    liftButtonPositions.remove(blockPos);
                }
            }
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

        public void forEachLiftButtonPosition(Consumer<BlockPos> consumer) {
            liftButtonPositions.forEach(consumer);
        }

        public ObjectOpenHashSet<BlockPos> getLiftButtonPositions() {
            return liftButtonPositions;
        }

        public LiftDirection getPressedButtonDirection() {
            return pressedButtonDirection;
        }

        public void setPressedButtonDirection(LiftDirection direction) {
            this.pressedButtonDirection = direction;
        }
    }
}