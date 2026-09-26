package top.xfunny.mod.block.base;

import org.mtr.core.data.Lift;
import org.mtr.core.data.LiftDirection;
import org.mtr.core.operation.PressLift;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.mtr.mod.client.MinecraftClientData;
import top.xfunny.mod.Init;
import org.mtr.mapping.holder.*;
import org.mtr.mapping.holder.Blocks;
import org.mtr.mapping.mapper.*;
import org.mtr.mod.InitClient;
import org.mtr.mod.block.IBlock;
import org.mtr.mod.packet.PacketPressLiftButton;
import top.xfunny.mod.*;
import top.xfunny.mod.Items;
import top.xfunny.mod.keymapping.DefaultButtonsKeyMapping;
import top.xfunny.mod.config.YteLiftConfigStore;
import top.xfunny.mod.lift.LiftArrivalLanternContext;
import top.xfunny.mod.lift.LiftArrivalLanternDecision;
import top.xfunny.mod.lift.LiftArrivalLanternDisplayPhase;
import top.xfunny.mod.lift.LiftArrivalLanternAssignmentProvider;
import top.xfunny.mod.lift.LiftArrivalLanternFlashPattern;
import top.xfunny.mod.lift.LiftArrivalLanternPolicy;
import top.xfunny.mod.lift.LiftArrivalLanternState;
import top.xfunny.mod.lift.LiftDisplayDirectionState;
import top.xfunny.mod.lift.LiftDisplayState;
import top.xfunny.mod.lift.LiftFloorRegistry;
import top.xfunny.mod.lift.LiftLanternController;
import top.xfunny.mod.lift.LiftModeState;
import top.xfunny.mod.util.TransformPositionX;
import top.xfunny.mixin.MixinLiftSchema;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import static org.mtr.core.data.LiftDirection.NONE;

public abstract class LiftButtonsBase extends BlockExtension implements DirectionHelper, BlockWithEntity, IBlock {
    public static final BooleanProperty UNLOCKED = BooleanProperty.of("unlocked");
    public static final BooleanProperty SINGLE = BooleanProperty.of("single");

    // [修复] 去掉 static，否则所有实例将共享同一个值，导致无法区分 Lantern 和 Button
    public final boolean allowPress;

    private final boolean isOdd;
    private double median = 0.25;//判定按下上、下按钮的分界线

    public LiftButtonsBase(boolean allowPress, boolean isOdd) {
        super(BlockHelper.createBlockSettings(true, true));
        this.isOdd = isOdd;
        this.allowPress = allowPress; // [修复] 赋值给实例变量
    }

    public LiftButtonsBase(boolean allowPress, boolean isOdd, double median) {//todo:即将弃用
        super(BlockHelper.createBlockSettings(true, true));
        this.isOdd = isOdd;
        this.allowPress = allowPress; // [修复] 赋值给实例变量
        this.median = median;
    }

    public static void hasButtonsClient(BlockPos trackPosition, LiftButtonDescriptor descriptor, FloorLiftCallback callback) {
        MinecraftClientData.getInstance().lifts.forEach(lift -> {
            final int floorIndex = lift.getFloorIndex(Init.blockPosToPosition(trackPosition));
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
    public ActionResult onUse2(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        final ActionResult result = IBlock.checkHoldingBrush(world, player, () -> {
            final boolean unlocked = !IBlock.getStatePropertySafe(state, UNLOCKED);
            world.setBlockState(pos, state.with(new Property<>(UNLOCKED.data), unlocked));
            player.sendMessage(Text.cast(TextHelper.translatable(unlocked ? "hint.yte.unlocked" : "hint.yte.locked")), true);
        });

        if (result == ActionResult.SUCCESS) {
            return ActionResult.SUCCESS;
        } else {
            if (player.isHolding(Items.YTE_LIFT_BUTTONS_LINK_CONNECTOR.get()) || player.isHolding(Items.YTE_LIFT_BUTTONS_LINK_REMOVER.get()) || player.isHolding(Items.YTE_GROUP_LIFT_BUTTONS_LINK_CONNECTOR.get()) || player.isHolding(Items.YTE_GROUP_LIFT_BUTTONS_LINK_REMOVER.get())) {
                return ActionResult.PASS;
            } else {
                final boolean unlocked = IBlock.getStatePropertySafe(state, UNLOCKED);
                final double hitY = MathHelper.fractionalPart(hit.getPos().getYMapped());
                final BlockEntity blockEntity = world.getBlockEntity(pos);
                final BlockEntityBase data = (BlockEntityBase) blockEntity.data;
                final DefaultButtonsKeyMapping keyMapping = data.getKeyMapping();
                final String focusButton = keyMapping.mapping(TransformPositionX.transform(MathHelper.fractionalPart(hit.getPos().getXMapped()), MathHelper.fractionalPart(hit.getPos().getZMapped()), IBlock.getStatePropertySafe(state, FACING)), hitY);


                if (unlocked) {
                    if (world.isClient() && !focusButton.equals("null")) {
                        final boolean[] availableHallLift = {false};
                        data.trackPositions.forEach(trackPosition -> MinecraftClientData.getInstance().lifts.forEach(lift -> {
                            if (lift.getFloorIndex(Init.blockPosToPosition(trackPosition)) >= 0
                                    && LiftModeState.canAcceptHallCall(lift.getId())) {
                                availableHallLift[0] = true;
                            }
                        }));
                        if (!availableHallLift[0]) {
                            return ActionResult.FAIL;
                        }

                        ObjectOpenHashSet<BlockPos> connectedLanternPositions = data.getLiftButtonPositions();
                        LiftButtonDescriptor descriptor = new LiftButtonDescriptor(false, false);
                        data.trackPositions.forEach(trackPosition -> LiftButtonsBase.hasButtonsClient(trackPosition, descriptor, (floor, lift) -> {
                        }));

                        connectedLanternPositions.forEach(lanternPos -> {
                            BlockEntity lanternBlockEntity = world.getBlockEntity(lanternPos);
                            if (lanternBlockEntity != null && lanternBlockEntity.data instanceof BlockEntityBase) {
                                BlockEntityBase lanternData = (BlockEntityBase) lanternBlockEntity.data;
                                if (descriptor.hasDownButton() && descriptor.hasUpButton()) {
                                    if (focusButton.equals("down")) {
                                        lanternData.setPressedButtonDirection(LiftDirection.DOWN);
                                    } else if (focusButton.equals("up")) {
                                        lanternData.setPressedButtonDirection(LiftDirection.UP);
                                    }
                                } else {
                                    lanternData.setPressedButtonDirection(descriptor.hasDownButton() ? LiftDirection.DOWN : LiftDirection.UP);
                                }
                            }
                        });

                        if (descriptor.hasDownButton() && descriptor.hasUpButton()) {
                            data.liftDirection = focusButton.equals("up") ? LiftDirection.UP : focusButton.equals("down") ? LiftDirection.DOWN : NONE;
                        } else {
                            data.liftDirection = descriptor.hasDownButton() ? LiftDirection.DOWN : LiftDirection.UP;
                        }

                        // A same-floor hall call can be accepted and removed by the server
                        // before the client ever observes a lift instruction. Latch the
                        // direction directly from the button so the car and hall displays
                        // still show the requested direction for the complete door cycle.
                        final Set<Long> sameFloorCandidateLiftIds = new HashSet<>();
                        data.trackPositions.forEach(trackPosition -> MinecraftClientData.getInstance().lifts.forEach(lift -> {
                            final int calledFloor = lift.getFloorIndex(Init.blockPosToPosition(trackPosition));
                            final int displayedFloor = lift.getFloorIndex(lift.getCurrentFloor().getPosition());
                            final MixinLiftSchema liftSchema = (MixinLiftSchema) lift;
                            final boolean doorCycleActive = liftSchema.getStoppingCoolDown() > 1 || lift.getDoorValue() != 0;
                            if (calledFloor >= 0 && calledFloor == displayedFloor && liftSchema.getSpeed() == 0) {
                                final LiftDisplayDirectionState directionState = LiftDisplayDirectionState.get(lift.getId());
                                if (doorCycleActive) {
                                    directionState.deferSameFloorCallDirection(data.liftDirection);
                                } else {
                                    sameFloorCandidateLiftIds.add(lift.getId());
                                }
                            }
                        }));
                        LiftDisplayDirectionState.registerPendingSameFloorCall(
                                sameFloorCandidateLiftIds, data.liftDirection);

                        final PressLift pressLift = new PressLift();
                        data.trackPositions.forEach(trackPosition -> pressLift.add(Init.blockPosToPosition(trackPosition), data.liftDirection));

                        InitClient.REGISTRY_CLIENT.sendPacketToServer(new PacketPressLiftButton(pressLift));
                        return ActionResult.SUCCESS;
                    }
                    return ActionResult.SUCCESS;
                } else {
                    return ActionResult.FAIL;
                }
            }
        }
    }

    @Override
    public BlockState getPlacementState2(ItemPlacementContext ctx) {
        final Direction facing = ctx.getPlayerFacing();
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
        public boolean hasUpButton() { return hasUpButton; }
        public boolean hasDownButton() { return hasDownButton; }
        public void setHasUpButton(boolean hasUpButton) { this.hasUpButton = hasUpButton; }
        public void setHasDownButton(boolean hasDownButton) { this.hasDownButton = hasDownButton; }
    }

    // ======================== 到站灯状态类型 ========================

    /** 到站灯阶段 */
    public enum LanternPhase {
        /** 空闲：无电梯将至 */
        IDLE,
        /** 呼叫已登记：按钮已按，但电梯尚未接近本层 */
        CALL_REGISTERED,
        /** 电梯接近中：已将本层加入指令列表，尚未到站 */
        APPROACHING,
        /** 电梯到站：在本层开门 */
        ARRIVED
    }

    /** 到站灯状态，由 {@link BlockEntityBase#getLanternState} 计算，渲染类直接读取。 */
    public static class LanternState {
        public final boolean upActive;
        public final boolean downActive;
        public final LanternPhase phase;
        /** 刚触发（仅一帧为 true），用于播放到站提示音 */
        public final boolean justTriggered;
        /** 最近一部已登记本层指令的电梯距离本层的楼层数，无相关电梯时为 -1 */
        public final int distanceToNearestLift;
        /** Policy-selected flash pattern; legacy renderers may ignore it. */
        public final LiftArrivalLanternFlashPattern flashPattern;
        /** Optional policy-selected sound id/cue for future brand strategies. */
        @Nullable public final String soundCue;
        public final long phaseStartMillis;

        LanternState(boolean upActive, boolean downActive, LanternPhase phase, boolean justTriggered, int distanceToNearestLift) {
            this(upActive, downActive, phase, justTriggered, distanceToNearestLift,
                    LiftArrivalLanternFlashPattern.STEADY, null, 0);
        }

        LanternState(boolean upActive, boolean downActive, LanternPhase phase, boolean justTriggered,
                int distanceToNearestLift, LiftArrivalLanternFlashPattern flashPattern,
                @Nullable String soundCue, long phaseStartMillis) {
            this.upActive = upActive;
            this.downActive = downActive;
            this.phase = phase;
            this.justTriggered = justTriggered;
            this.distanceToNearestLift = distanceToNearestLift;
            this.flashPattern = flashPattern;
            this.soundCue = soundCue;
            this.phaseStartMillis = phaseStartMillis;
        }
    }

    public static class BlockEntityBase extends BlockEntityExtension implements LiftFloorRegistry, ButtonRegistry, LiftLanternController {
        private static final String KEY_TRACK_FLOOR_POS = "track_floor_pos";
        private static final String KEY_LIFT_BUTTON_POSITIONS = "lift_button_position";
        public final ObjectOpenHashSet<BlockPos> liftButtonPositions = new ObjectOpenHashSet<>();
        private final ObjectOpenHashSet<BlockPos> trackPositions = new ObjectOpenHashSet<>();
        public LiftDirection liftDirection = NONE;

        public BlockPos selfPos;
        /** @deprecated 请使用 {@link #getLanternState} 的 justTriggered 替代。保留以兼容未迁移的 Screen 渲染类。 */
        @Deprecated
        public boolean lastUpActive = false;
        /** @deprecated 请使用 {@link #getLanternState} 的 justTriggered 替代。保留以兼容未迁移的 Screen 渲染类。 */
        @Deprecated
        public boolean lastDownActive = false;
        /** 每个轨道楼层、每部电梯最后观察到的触发序号，用于保证到站音只播放一次。 */
        private final java.util.Map<BlockPos, java.util.Map<Long, Long>> lanternTriggerSequences = new java.util.HashMap<>();
        private LiftDirection pressedButtonDirection;
        private long pressedButtonAtMillis;
        private DefaultButtonsKeyMapping keyMapping = new DefaultButtonsKeyMapping();

        public BlockEntityBase(BlockEntityType<?> type, BlockPos blockPos, BlockState blockState) {
            super(type, blockPos, blockState);
        }

        @Override
        public void readCompoundTag(CompoundTag compoundTag) {
            trackPositions.clear();
            liftButtonPositions.clear();
            for (final long position : compoundTag.getLongArray(KEY_TRACK_FLOOR_POS)) {
                trackPositions.add(BlockPos.fromLong(position));
            }
            for (final long position : compoundTag.getLongArray(KEY_LIFT_BUTTON_POSITIONS)) {
                liftButtonPositions.add(BlockPos.fromLong(position));
            }
        }

        @Override
        public void writeCompoundTag(CompoundTag compoundTag) {
            final List<Long> trackPositionsList = new ArrayList<>();
            trackPositions.forEach(position -> trackPositionsList.add(position.asLong()));
            compoundTag.putLongArray(KEY_TRACK_FLOOR_POS, trackPositionsList);

            final List<Long> liftButtonPositionsList = new ArrayList<>();
            liftButtonPositions.forEach(position -> liftButtonPositionsList.add(position.asLong()));
            compoundTag.putLongArray(KEY_LIFT_BUTTON_POSITIONS, liftButtonPositionsList);
        }

        public DefaultButtonsKeyMapping getKeyMapping() { return keyMapping; }
        public void setKeyMapping(DefaultButtonsKeyMapping keyMapping) { this.keyMapping = keyMapping; }

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
                    trackPositions.add(pos);
                    if (trackPositions.size() != 1 && single) {
                        final boolean single1 = !IBlock.getStatePropertySafe(world.getBlockState(selfPos), SINGLE);
                        world.setBlockState(selfPos, world.getBlockState(selfPos).with(new Property<>(SINGLE.data), single1));
                    }
                } else {
                    trackPositions.remove(pos);
                    if (trackPositions.size() == 1 && !single) {
                        final boolean single1 = !IBlock.getStatePropertySafe(world.getBlockState(selfPos), SINGLE);
                        world.setBlockState(selfPos, world.getBlockState(selfPos).with(new Property<>(SINGLE.data), single1));
                    }
                }
            }
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
                    liftButtonPositions.add(blockPos);
                } else {
                    liftButtonPositions.remove(blockPos);
                }
            }
            markDirty2();
        }

        /**
         * 计算指定轨道位置的到站灯状态。渲染每帧调用一次。
         * <p>
         * 内部更新 {@link #lastUpActive}/{@link #lastDownActive} 用于边沿检测。
         * <p>
         * 渲染类根据返回的 {@link LanternPhase} 自行决定闪烁策略：
         * <ul>
         *   <li>{@link LanternPhase#CALL_REGISTERED} → 可选慢闪（enableCallFlash）</li>
         *   <li>{@link LanternPhase#APPROACHING}     → 可选快闪/常亮（enableApproachFlash）</li>
         *   <li>{@link LanternPhase#ARRIVED}         → 常亮</li>
         * </ul>
         *
         * @param trackPosition 关联的电梯轨道楼层位置
         * @return 到站灯状态，绝不会为 null
         */
        public LanternState getLanternState(BlockPos trackPosition) {
            return getLanternState(trackPosition, LiftArrivalLanternPolicy.DEFAULT);
        }

        /**
         * Policy extension point for brand-specific advance notice, flash and
         * sound strategies. The no-policy overload preserves current behavior.
         */
        public LanternState getLanternState(BlockPos trackPosition, LiftArrivalLanternPolicy policy) {
            return getLanternState(trackPosition, policy, LiftArrivalLanternAssignmentProvider.NONE);
        }

        public LanternState getLanternState(BlockPos trackPosition, LiftArrivalLanternPolicy policy,
                LiftArrivalLanternAssignmentProvider assignmentProvider) {
            boolean upActive = false;
            boolean downActive = false;
            LanternPhase phase = LanternPhase.IDLE;
            int minDistance = Integer.MAX_VALUE;
            boolean justTriggered = false;
            LiftArrivalLanternFlashPattern selectedFlashPattern = LiftArrivalLanternFlashPattern.STEADY;
            String selectedSoundCue = null;
            long selectedPhaseStartMillis = 0;
            final long currentMillis = System.currentTimeMillis();
            final java.util.Map<Long, Long> seenTriggerSequences = lanternTriggerSequences.computeIfAbsent(
                    trackPosition, ignored -> new java.util.HashMap<>());

            for (Lift lift : MinecraftClientData.getInstance().lifts) {
                if (LiftModeState.getMode(lift.getId()) == LiftModeState.LiftMode.ATTENDANT) {
                    continue;
                }
                final int floorIndex = lift.getFloorIndex(Init.blockPosToPosition(trackPosition));
                if (floorIndex < 0) continue;

                final LiftArrivalLanternState arrivalState = LiftArrivalLanternState.get(lift.getId());
                final int currentLiftFloorIdx = lift.getFloorIndex(lift.getCurrentFloor().getPosition());
                final int distance = Math.abs(currentLiftFloorIdx - floorIndex);
                final LiftArrivalLanternContext context = new LiftArrivalLanternContext(
                        LiftDisplayState.get(lift.getId()), arrivalState,
                        YteLiftConfigStore.getArrivalLanternTriggerMode(lift.getId()), floorIndex, distance,
                        pressedButtonDirection == null ? LiftDirection.NONE : pressedButtonDirection,
                        pressedButtonAtMillis, currentMillis,
                        assignmentProvider == null ? null : assignmentProvider.getAssignment(lift.getId(), floorIndex));
                final LiftArrivalLanternDecision decision = policy == null
                        ? LiftArrivalLanternDecision.inactive() : policy.evaluate(context);
                if (decision == null || !decision.isActive()) {
                    continue;
                }

                final LiftDirection direction = decision.getDirection();
                final boolean lightVisible = decision.getFlashPattern().isLit(
                        currentMillis, decision.getPhaseStartMillis());

                if (lightVisible && direction == LiftDirection.UP) {
                    upActive = true;
                } else if (lightVisible && direction == LiftDirection.DOWN) {
                    downActive = true;
                }
                final LanternPhase decisionPhase = yte$toLegacyLanternPhase(decision.getPhase());
                if (decisionPhase.ordinal() > phase.ordinal()) {
                    phase = decisionPhase;
                    selectedFlashPattern = decision.getFlashPattern();
                    selectedSoundCue = decision.getSoundCue();
                    selectedPhaseStartMillis = decision.getPhaseStartMillis();
                } else if (selectedSoundCue == null && decision.getSoundCue() != null) {
                    selectedSoundCue = decision.getSoundCue();
                }
                minDistance = Math.min(minDistance, distance);

                final long eventSequence = decision.getEventSequence();
                if (eventSequence > 0) {
                    final Long previousSequence = seenTriggerSequences.put(lift.getId(), eventSequence);
                    if (previousSequence == null || previousSequence != eventSequence) {
                        justTriggered = true;
                    }
                }
            }

            return new LanternState(upActive, downActive, phase, justTriggered,
                    minDistance == Integer.MAX_VALUE ? -1 : minDistance,
                    selectedFlashPattern, selectedSoundCue, selectedPhaseStartMillis);
        }

        private static LanternPhase yte$toLegacyLanternPhase(LiftArrivalLanternDisplayPhase phase) {
            switch (phase) {
                case CALL_REGISTERED:
                    return LanternPhase.CALL_REGISTERED;
                case APPROACHING:
                    return LanternPhase.APPROACHING;
                case ARRIVED:
                case CLOSING:
                    return LanternPhase.ARRIVED;
                case IDLE:
                default:
                    return LanternPhase.IDLE;
            }
        }

        @Override
        public ObjectOpenHashSet<BlockPos> getTrackPositions() { return trackPositions; }
        public void forEachTrackPosition(Consumer<BlockPos> consumer) { trackPositions.forEach(consumer); }
        public void forEachLiftButtonPosition(Consumer<BlockPos> consumer) { liftButtonPositions.forEach(consumer); }
        @Override
        public ObjectOpenHashSet<BlockPos> getLiftButtonPositions() { return liftButtonPositions; }
        public LiftDirection getPressedButtonDirection() { return pressedButtonDirection; }
        public void setPressedButtonDirection(LiftDirection direction) {
            pressedButtonDirection = direction;
            pressedButtonAtMillis = System.currentTimeMillis();
        }
    }
}
