package top.xfunny.mod.client.render;

import org.mtr.core.data.Lift;
import org.mtr.core.data.LiftDirection;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArraySet;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectObjectImmutablePair;
import org.mtr.mapping.holder.*;
import org.mtr.mapping.mapper.BlockEntityRenderer;
import org.mtr.mapping.mapper.DirectionHelper;
import org.mtr.mapping.mapper.GraphicsHolder;
import org.mtr.mapping.mapper.PlayerHelper;
import org.mtr.mod.block.IBlock;
import org.mtr.mod.data.IGui;
import org.mtr.mod.render.QueuedRenderLayer;
import org.mtr.mod.render.StoredMatrixTransformations;
import top.xfunny.mod.Init;
import top.xfunny.mod.block.KoneKDS360Button1DisplayTop;
import top.xfunny.mod.block.base.LiftButtonsBase;
import top.xfunny.mod.client.resource.FontList;
import top.xfunny.mod.client.view.*;
import top.xfunny.mod.client.view.view_group.FrameLayout;
import top.xfunny.mod.client.view.view_group.LinearLayout;
import top.xfunny.mod.item.YteGroupLiftButtonsLinker;
import top.xfunny.mod.item.YteLiftButtonsLinker;
import top.xfunny.mod.keymapping.DefaultButtonsKeyMapping;
import top.xfunny.mod.util.ReverseRendering;

import java.util.Comparator;

public class RenderKoneKDS360Button1DisplayTop extends BlockEntityRenderer<KoneKDS360Button1DisplayTop.BlockEntity> implements DirectionHelper, IGui, IBlock {

    private static final int HOVER_COLOR = 0xFFCCCCCC;
    private static final int PRESSED_COLOR = 0xFFFFFFFF;
    private static final int DEFAULT_COLOR = 0xFFFFFFFF;
    private static final Identifier ARROW_TEXTURE = new Identifier(Init.MOD_ID, "textures/block/kone_kds330_arrow.png");
    private static final BooleanProperty UNLOCKED = BooleanProperty.of("unlocked");
    private final Identifier BUTTON_TEXTURE = new Identifier(Init.MOD_ID, "textures/block/kone_kds220_button_1_surface.png");
    private final Identifier BUTTON_LIGHT_TEXTURE = new Identifier(Init.MOD_ID, "textures/block/kone_kds220_button_1_light.png");
    private final Identifier LOGO = new Identifier(Init.MOD_ID, "textures/block/kone_logo_2_black.png");

    public RenderKoneKDS360Button1DisplayTop(Argument dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(KoneKDS360Button1DisplayTop.BlockEntity blockEntity, float tickDelta, GraphicsHolder graphicsHolder1, int light, int overlay) {
        final World world = blockEntity.getWorld2();
        if (world == null) return;

        final ClientPlayerEntity clientPlayerEntity = MinecraftClient.getInstance().getPlayerMapped();
        if (clientPlayerEntity == null) return;

        final boolean holdingLinker = PlayerHelper.isHolding(PlayerEntity.cast(clientPlayerEntity), item -> item.data instanceof YteLiftButtonsLinker || item.data instanceof YteGroupLiftButtonsLinker);
        final BlockPos blockPos = blockEntity.getPos2();
        final BlockState blockState = world.getBlockState(blockPos);
        final Direction facing = IBlock.getStatePropertySafe(blockState, FACING);
        final boolean unlocked = IBlock.getStatePropertySafe(blockState, UNLOCKED);

        // 按钮描述符，用于存储是否有上下按钮
        LiftButtonsBase.LiftButtonDescriptor buttonDescriptor = new LiftButtonsBase.LiftButtonDescriptor(false, false);

        final StoredMatrixTransformations storedMatrixTransformations = new StoredMatrixTransformations(0.5, 0, 0.5);
        StoredMatrixTransformations storedMatrixTransformations1 = storedMatrixTransformations.copy();
        storedMatrixTransformations1.add(graphicsHolder -> {
            graphicsHolder.rotateYDegrees(-facing.asRotation());
            graphicsHolder.translate(0, 0, 7.5F / 16 - SMALL_OFFSET);
        });

        final DefaultButtonsKeyMapping keyMapping = blockEntity.getKeyMapping();

        // --- 按钮逻辑处理部分 ---

        // 预定义按钮视图，稍后根据逻辑激活
        ButtonView buttonUpLight = new ButtonView();
        buttonUpLight.setId("up");
        buttonUpLight.setBasicsAttributes(world, blockPos, keyMapping);
        buttonUpLight.setTexture(BUTTON_LIGHT_TEXTURE);
        buttonUpLight.setDimension(0.9F / 16);
        buttonUpLight.setGravity(Gravity.CENTER);
        buttonUpLight.setLight(light);
        buttonUpLight.setDefaultColor(DEFAULT_COLOR);
        buttonUpLight.setHoverColor(HOVER_COLOR);
        buttonUpLight.setPressedColor(PRESSED_COLOR);

        ButtonView buttonDownLight = new ButtonView();
        buttonDownLight.setId("down");
        buttonDownLight.setBasicsAttributes(world, blockPos, keyMapping);
        buttonDownLight.setTexture(BUTTON_LIGHT_TEXTURE);
        buttonDownLight.setDimension(0.9F / 16);
        buttonDownLight.setGravity(Gravity.CENTER);
        buttonDownLight.setLight(light);
        buttonDownLight.setDefaultColor(DEFAULT_COLOR);
        buttonDownLight.setHoverColor(HOVER_COLOR);
        buttonDownLight.setPressedColor(PRESSED_COLOR);
        buttonDownLight.setFlip(false, true);

        final LinearLayout screenLayout = new LinearLayout(false);
        screenLayout.setBasicsAttributes(world, blockPos);
        screenLayout.setWidth(LayoutSize.WRAP_CONTENT);
        screenLayout.setHeight(LayoutSize.WRAP_CONTENT);
        screenLayout.setGravity(Gravity.CENTER);

        final LineComponent line = new LineComponent();
        line.setBasicsAttributes(world, blockPos);

        final ObjectArrayList<ObjectObjectImmutablePair<BlockPos, Lift>> sortedPositionsAndLifts = new ObjectArrayList<>();

        // 核心循环：获取电梯状态并填充 buttonDescriptor
        blockEntity.forEachTrackPosition(trackPosition -> {
            line.RenderLine(holdingLinker, trackPosition);

            KoneKDS360Button1DisplayTop.hasButtonsClient(trackPosition, buttonDescriptor, (floorIndex, lift) -> {
                sortedPositionsAndLifts.add(new ObjectObjectImmutablePair<>(trackPosition, lift));
                final ObjectArraySet<LiftDirection> instructionDirections = lift.hasInstruction(floorIndex);
                instructionDirections.forEach(liftDirection -> {
                    if (liftDirection == LiftDirection.DOWN) buttonDownLight.activate();
                    if (liftDirection == LiftDirection.UP) buttonUpLight.activate();
                });
            });
        });

        // --- 布局位置计算 ---

        float upButtonY = 2.775F / 16;
        float downButtonY = 1.325F / 16;
        float centerButtonY = 2.05F / 16; // (2.775 + 1.325) / 2

        // 如果只有一个按钮，则使用中间坐标
        if (buttonDescriptor.hasUpButton() && !buttonDescriptor.hasDownButton()) {
            upButtonY = centerButtonY;
        } else if (!buttonDescriptor.hasUpButton() && buttonDescriptor.hasDownButton()) {
            downButtonY = centerButtonY;
        }

        // 1. 屏幕布局
        final FrameLayout screenContainer = new FrameLayout();
        screenContainer.setBasicsAttributes(world, blockPos);
        screenContainer.setStoredMatrixTransformations(storedMatrixTransformations1);
        screenContainer.setParentDimensions(4.5F / 16, 3F / 16);
        screenContainer.setPosition(-2.25F / 16, 5.825F / 16);
        screenContainer.setWidth(LayoutSize.MATCH_PARENT);
        screenContainer.setHeight(LayoutSize.MATCH_PARENT);

        // 2. Logo 布局
        final FrameLayout logoLayout = new FrameLayout();
        logoLayout.setBasicsAttributes(world, blockPos);
        logoLayout.setStoredMatrixTransformations(storedMatrixTransformations1);
        logoLayout.setParentDimensions(4.5F / 16, 1F / 16);
        logoLayout.setPosition(-2.25F / 16, 5.325F / 16);
        logoLayout.setWidth(LayoutSize.MATCH_PARENT);
        logoLayout.setHeight(LayoutSize.MATCH_PARENT);

        // 3. 上按钮布局
        final FrameLayout buttonUpLayout = new FrameLayout();
        buttonUpLayout.setBasicsAttributes(world, blockPos);
        buttonUpLayout.setStoredMatrixTransformations(storedMatrixTransformations1);
        buttonUpLayout.setParentDimensions(4.5F / 16, 3.05F / 16);
        buttonUpLayout.setPosition(-2.25F / 16, upButtonY);
        buttonUpLayout.setWidth(LayoutSize.MATCH_PARENT);
        buttonUpLayout.setHeight(LayoutSize.MATCH_PARENT);

        // 4. 下按钮布局
        final FrameLayout buttonDownLayout = new FrameLayout();
        buttonDownLayout.setBasicsAttributes(world, blockPos);
        buttonDownLayout.setStoredMatrixTransformations(storedMatrixTransformations1);
        buttonDownLayout.setParentDimensions(4.5F / 16, 3.05F / 16);
        buttonDownLayout.setPosition(-2.25F / 16, downButtonY);
        buttonDownLayout.setWidth(LayoutSize.MATCH_PARENT);
        buttonDownLayout.setHeight(LayoutSize.MATCH_PARENT);

        // --- 视图组装 ---

        ImageView koneLogo = new ImageView();
        koneLogo.setBasicsAttributes(world, blockPos);
        koneLogo.setTexture(LOGO);
        koneLogo.setDimension(0.5F / 16, 854, 372);
        koneLogo.setLight(light);
        koneLogo.setGravity(Gravity.CENTER);

        sortedPositionsAndLifts.sort(Comparator.comparingInt(sortedPositionAndLift -> blockPos.getManhattanDistance(new Vector3i(sortedPositionAndLift.left().data))));

        if (!sortedPositionsAndLifts.isEmpty()) {
            final int count = Math.min(2, sortedPositionsAndLifts.size());
            final boolean reverseRendering = count > 1 && ReverseRendering.reverseRendering(facing.rotateYCounterclockwise(), sortedPositionsAndLifts.get(0).left(), sortedPositionsAndLifts.get(1).left());

            for (int i = 0; i < count; i++) {
                final LiftFloorDisplayView liftFloorDisplayView = new LiftFloorDisplayView();
                liftFloorDisplayView.setBasicsAttributes(world, blockPos, sortedPositionsAndLifts.get(i).right(), FontList.instance.getFont("kone-kds220-segment"), 7, 0xFFFFFFFF);
                liftFloorDisplayView.setTextureId(String.format("kone_kds_360_lcd_segment_display_%d", i));
                liftFloorDisplayView.setWidth(1.8F / 16);
                liftFloorDisplayView.setHeight(1.7F / 16);
                liftFloorDisplayView.setMargin(-0.25F / 16, 0, 0.4F / 16, 0);
                liftFloorDisplayView.setTextAlign(TextView.HorizontalTextAlign.RIGHT);

                final LiftArrowView liftArrowView = new LiftArrowView();
                liftArrowView.setBasicsAttributes(world, blockPos, sortedPositionsAndLifts.get(i).right(), LiftArrowView.ArrowType.AUTO);
                liftArrowView.setTexture(ARROW_TEXTURE);
                liftArrowView.setDimension(0.8F / 16);
                liftArrowView.setGravity(Gravity.CENTER_HORIZONTAL);
                liftArrowView.setQueuedRenderLayer(QueuedRenderLayer.LIGHT_TRANSLUCENT);
                liftArrowView.setFlip(false, true);
                liftArrowView.setColor(unlocked ? 0xFFFFFFFF : 0xFF000000);

                final LinearLayout numberLayout = new LinearLayout(true);
                numberLayout.setBasicsAttributes(world, blockPos);
                numberLayout.setWidth(LayoutSize.WRAP_CONTENT);
                numberLayout.setHeight(LayoutSize.WRAP_CONTENT);
                numberLayout.addChild(liftArrowView);
                numberLayout.addChild(liftFloorDisplayView);

                if (reverseRendering) {
                    screenLayout.addChild(numberLayout);
                    screenLayout.reverseChildren();
                } else {
                    screenLayout.addChild(numberLayout);
                }
            }
        }

        screenContainer.addChild(screenLayout);
        logoLayout.addChild(koneLogo);

        if (buttonDescriptor.hasUpButton()) {
            ImageView buttonUp = new ImageView();
            buttonUp.setBasicsAttributes(world, blockPos);
            buttonUp.setTexture(BUTTON_TEXTURE);
            buttonUp.setDimension(0.9F / 16);
            buttonUp.setGravity(Gravity.CENTER);
            buttonUp.setLight(light);

            final FrameLayout buttonUpGroup = new FrameLayout();
            buttonUpGroup.setBasicsAttributes(world, blockPos);
            buttonUpGroup.setWidth(LayoutSize.WRAP_CONTENT);
            buttonUpGroup.setHeight(LayoutSize.WRAP_CONTENT);
            buttonUpGroup.setGravity(Gravity.CENTER);
            buttonUpGroup.addChild(buttonUp);
            buttonUpGroup.addChild(buttonUpLight);
            buttonUpLayout.addChild(buttonUpGroup);
            buttonUpLayout.render();
        }

        if (buttonDescriptor.hasDownButton()) {
            ImageView buttonDown = new ImageView();
            buttonDown.setBasicsAttributes(world, blockPos);
            buttonDown.setTexture(BUTTON_TEXTURE);
            buttonDown.setDimension(0.9F / 16);
            buttonDown.setGravity(Gravity.CENTER);
            buttonDown.setLight(light);
            buttonDown.setFlip(false, true);

            final FrameLayout buttonDownGroup = new FrameLayout();
            buttonDownGroup.setBasicsAttributes(world, blockPos);
            buttonDownGroup.setWidth(LayoutSize.WRAP_CONTENT);
            buttonDownGroup.setHeight(LayoutSize.WRAP_CONTENT);
            buttonDownGroup.setGravity(Gravity.CENTER);
            buttonDownGroup.addChild(buttonDown);
            buttonDownGroup.addChild(buttonDownLight);
            buttonDownLayout.addChild(buttonDownGroup);
            buttonDownLayout.render();
        }

        logoLayout.render();
        screenContainer.render();
    }
}