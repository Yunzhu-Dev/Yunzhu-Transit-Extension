package top.xfunny.mod.client.render;

import org.mtr.core.data.Lift;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectObjectImmutablePair;
import org.mtr.mapping.holder.*;
import org.mtr.mapping.mapper.BlockEntityRenderer;
import org.mtr.mapping.mapper.DirectionHelper;
import org.mtr.mapping.mapper.GraphicsHolder;
import org.mtr.mapping.mapper.PlayerHelper;
import org.mtr.mod.block.IBlock;
import org.mtr.mod.data.IGui;
import org.mtr.mod.render.StoredMatrixTransformations;
import top.xfunny.mod.Init;
import top.xfunny.mod.block.MitsubishiNexWayScreen1Even;
import top.xfunny.mod.block.base.LiftButtonsBase;
import top.xfunny.mod.client.view.*;
import top.xfunny.mod.client.view.view_group.FrameLayout;
import top.xfunny.mod.client.view.view_group.LinearLayout;
import top.xfunny.mod.item.YteGroupLiftButtonsLinker;
import top.xfunny.mod.item.YteLiftButtonsLinker;

import java.util.Comparator;

public class RenderDoverImpulseLantern1Horizontal<T extends LiftButtonsBase.BlockEntityBase> extends BlockEntityRenderer<T> implements DirectionHelper, IGui, IBlock {
    private static final int PRESSED_COLOR = 0xFFFDCC23;
    private static final int DEFAULT_COLOR = 0xFFFFFFFF;
    private static final Identifier BUTTON_TEXTURE = new Identifier(Init.MOD_ID, "textures/block/dover_impulse_lantern_1.png");
    private static final Identifier ARROW_TEXTURE = new Identifier(Init.MOD_ID, "textures/block/dover_impulse_lantern_1_arrow.png");
    private final boolean isOdd;

    public RenderDoverImpulseLantern1Horizontal(Argument dispatcher, Boolean isOdd) {
        super(dispatcher);
        this.isOdd = isOdd;
    }

    @Override
    public void render(T blockEntity, float tickDelta, GraphicsHolder graphicsHolder1, int light, int overlay) {
        final World world = blockEntity.getWorld2();
        if (world == null) {
            return;
        }

        final ClientPlayerEntity clientPlayerEntity = MinecraftClient.getInstance().getPlayerMapped();
        if (clientPlayerEntity == null) {
            return;
        }

        final boolean holdingLinker = PlayerHelper.isHolding(PlayerEntity.cast(clientPlayerEntity), item -> item.data instanceof YteLiftButtonsLinker || item.data instanceof YteGroupLiftButtonsLinker);
        final BlockPos blockPos = blockEntity.getPos2();
        final BlockState blockState = world.getBlockState(blockPos);
        final Direction facing = IBlock.getStatePropertySafe(blockState, FACING);
        LiftButtonsBase.LiftButtonDescriptor buttonDescriptor = new LiftButtonsBase.LiftButtonDescriptor(false, false);

        final StoredMatrixTransformations storedMatrixTransformations = new StoredMatrixTransformations(0.5, 0, 0.5);
        StoredMatrixTransformations storedMatrixTransformations1 = storedMatrixTransformations.copy();
        storedMatrixTransformations1.add(graphicsHolder -> {
            graphicsHolder.rotateYDegrees(-facing.asRotation());
            graphicsHolder.translate(0, 0, 7.9F / 16 - SMALL_OFFSET);
        });

        final FrameLayout parentLayout = new FrameLayout();
        parentLayout.setBasicsAttributes(world, blockPos);
        parentLayout.setStoredMatrixTransformations(storedMatrixTransformations1);
        parentLayout.setParentDimensions(15.5F / 16, 4F / 16);
        parentLayout.setPosition(isOdd ? -7.75F / 16 : -15.75F / 16, 7.3F / 16);
        parentLayout.setWidth(LayoutSize.MATCH_PARENT);
        parentLayout.setHeight(LayoutSize.MATCH_PARENT);

        final LinearLayout linearLayout = new LinearLayout(false);
        linearLayout.setBasicsAttributes(world, blockPos);
        linearLayout.setHeight(LayoutSize.WRAP_CONTENT);
        linearLayout.setWidth(LayoutSize.WRAP_CONTENT);
        linearLayout.setGravity(Gravity.CENTER);

        final FrameLayout lanternGroupLeft = new FrameLayout();
        lanternGroupLeft.setBasicsAttributes(world, blockPos);
        lanternGroupLeft.setWidth(LayoutSize.WRAP_CONTENT);
        lanternGroupLeft.setHeight(LayoutSize.WRAP_CONTENT);
        lanternGroupLeft.setGravity(Gravity.CENTER_VERTICAL);

        final FrameLayout lanternGroupRight = new FrameLayout();
        lanternGroupRight.setBasicsAttributes(world, blockPos);
        lanternGroupRight.setWidth(LayoutSize.WRAP_CONTENT);
        lanternGroupRight.setHeight(LayoutSize.WRAP_CONTENT);
        lanternGroupRight.setGravity(Gravity.CENTER_VERTICAL);

        final ImageView lanternArrowUpLeft = new ImageView();
        lanternArrowUpLeft.setBasicsAttributes(world, blockPos);
        lanternArrowUpLeft.setTexture(ARROW_TEXTURE);
        lanternArrowUpLeft.setDimension(1.5F / 16);
        lanternArrowUpLeft.setGravity(Gravity.CENTER);
        lanternArrowUpLeft.setLight(light);

        final ImageView lanternArrowUpRight = new ImageView();
        lanternArrowUpRight.setBasicsAttributes(world, blockPos);
        lanternArrowUpRight.setTexture(ARROW_TEXTURE);
        lanternArrowUpRight.setDimension(1.5F / 16);
        lanternArrowUpRight.setGravity(Gravity.CENTER);
        lanternArrowUpRight.setLight(light);

        final ImageView lanternArrowDownLeft = new ImageView();
        lanternArrowDownLeft.setBasicsAttributes(world, blockPos);
        lanternArrowDownLeft.setTexture(ARROW_TEXTURE);
        lanternArrowDownLeft.setDimension(1.5F / 16);
        lanternArrowDownLeft.setGravity(Gravity.CENTER);
        lanternArrowDownLeft.setLight(light);
        lanternArrowDownLeft.setFlip(false, true);

        final ImageView lanternArrowDownRight = new ImageView();
        lanternArrowDownRight.setBasicsAttributes(world, blockPos);
        lanternArrowDownRight.setTexture(ARROW_TEXTURE);
        lanternArrowDownRight.setDimension(1.5F / 16);
        lanternArrowDownRight.setGravity(Gravity.CENTER);
        lanternArrowDownRight.setLight(light);
        lanternArrowDownRight.setFlip(false, true);

        final ButtonView downLanternLeft = new ButtonView();
        downLanternLeft.setBasicsAttributes(world, blockPos);
        downLanternLeft.setTexture(BUTTON_TEXTURE);
        downLanternLeft.setDimension(1.5F / 16);
        downLanternLeft.setGravity(Gravity.CENTER);
        downLanternLeft.setLight(light);
        downLanternLeft.setDefaultColor(DEFAULT_COLOR);
        downLanternLeft.setPressedColor(PRESSED_COLOR);

        final ButtonView downLanternRight = new ButtonView();
        downLanternRight.setBasicsAttributes(world, blockPos);
        downLanternRight.setTexture(BUTTON_TEXTURE);
        downLanternRight.setDimension(1.5F / 16);
        downLanternRight.setGravity(Gravity.CENTER);
        downLanternRight.setLight(light);
        downLanternRight.setDefaultColor(DEFAULT_COLOR);
        downLanternRight.setPressedColor(PRESSED_COLOR);

        final ButtonView upLanternLeft = new ButtonView();
        upLanternLeft.setBasicsAttributes(world, blockPos);
        upLanternLeft.setTexture(BUTTON_TEXTURE);
        upLanternLeft.setDimension(1.5F / 16);
        upLanternLeft.setGravity(Gravity.CENTER);
        upLanternLeft.setLight(light);
        upLanternLeft.setDefaultColor(DEFAULT_COLOR);
        upLanternLeft.setPressedColor(PRESSED_COLOR);

        final ButtonView upLanternRight = new ButtonView();
        upLanternRight.setBasicsAttributes(world, blockPos);
        upLanternRight.setTexture(BUTTON_TEXTURE);
        upLanternRight.setDimension(1.5F / 16);
        upLanternRight.setGravity(Gravity.CENTER);
        upLanternRight.setLight(light);
        upLanternRight.setDefaultColor(DEFAULT_COLOR);
        upLanternRight.setPressedColor(PRESSED_COLOR);

        final LineComponent line = new LineComponent();
        line.setBasicsAttributes(world, blockPos);

        final LineComponent buttonLine = new LineComponent();
        buttonLine.setBasicsAttributes(world, blockPos);

        final ObjectArrayList<ObjectObjectImmutablePair<BlockPos, Lift>> sortedPositionsAndLifts = new ObjectArrayList<>();

        // ===== 到站灯闪烁策略（本型号可配置）=====
        final boolean enableCallFlash = true;      // 呼叫登记阶段 → 慢闪
        final boolean enableApproachFlash = false;  // 接近阶段 → 常亮
        final boolean flash = (System.currentTimeMillis() % 1000) < 500;

        blockEntity.forEachTrackPosition(trackPosition -> {
            line.RenderLine(holdingLinker, trackPosition);

            // 收集电梯信息用于后续排版
            MitsubishiNexWayScreen1Even.hasButtonsClient(trackPosition, buttonDescriptor, (floorIndex, lift) -> {
                sortedPositionsAndLifts.add(new ObjectObjectImmutablePair<>(trackPosition, lift));
            });

            // ---- 使用统一到站灯状态 API ----
            LiftButtonsBase.LanternState state = blockEntity.getLanternState(trackPosition);

            final boolean showDown = state.downActive;
            final boolean showUp = state.upActive;
            final boolean useFlash = (state.phase == LiftButtonsBase.LanternPhase.CALL_REGISTERED && enableCallFlash)
                    || (state.phase == LiftButtonsBase.LanternPhase.APPROACHING && enableApproachFlash);

            if (showDown && (!useFlash || flash)) {
                downLanternLeft.activate();
                downLanternRight.activate();
            }
            if (showUp && (!useFlash || flash)) {
                upLanternLeft.activate();
                upLanternRight.activate();
            }

            // 声音触发（边沿触发，仅一帧）
            if (state.justTriggered) {
                // TODO: 根据需要发声音包
                // InitClient.REGISTRY_CLIENT.sendPacketToServer(
                //     new PacketLanternSoundInstruction(blockPos, "dover_impulse_lantern_1"));
            }
        });

        sortedPositionsAndLifts.sort(Comparator.comparingInt(sortedPositionAndLift -> blockPos.getManhattanDistance(new Vector3i(sortedPositionAndLift.left().data))));

        blockEntity.forEachLiftButtonPosition(buttonPosition -> {
            buttonLine.RenderLine(holdingLinker, buttonPosition, true);
        });

        if (buttonDescriptor.hasDownButton() && buttonDescriptor.hasUpButton()) {
            lanternGroupLeft.setMargin(0, 0, 0.5F / 16, 0);
            lanternGroupLeft.addChild(upLanternLeft);
            lanternGroupLeft.addChild(lanternArrowUpLeft);
            lanternGroupRight.addChild(downLanternRight);
            lanternGroupRight.addChild(lanternArrowDownRight);
            linearLayout.addChild(lanternGroupLeft);
            linearLayout.addChild(lanternGroupRight);

        } else if (buttonDescriptor.hasDownButton()) {
            lanternGroupLeft.setMargin(0, 0, 0.5F / 16, 0);
            lanternGroupLeft.addChild(upLanternLeft);
            lanternGroupLeft.addChild(lanternArrowUpLeft);
            lanternGroupRight.addChild(downLanternRight);
            lanternGroupRight.addChild(lanternArrowDownRight);
            linearLayout.addChild(lanternGroupLeft);
            linearLayout.addChild(lanternGroupRight);

        } else if (buttonDescriptor.hasUpButton()) {
            lanternGroupLeft.setMargin(0, 0, 0.5F / 16, 0);
            lanternGroupLeft.addChild(upLanternLeft);
            lanternGroupLeft.addChild(lanternArrowUpLeft);
            lanternGroupRight.addChild(downLanternRight);
            lanternGroupRight.addChild(lanternArrowDownRight);
            linearLayout.addChild(lanternGroupLeft);
            linearLayout.addChild(lanternGroupRight);
        }

        parentLayout.addChild(linearLayout);
        parentLayout.render();
    }
}
