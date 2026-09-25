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
import top.xfunny.mod.block.FujitecMIC400Button1Old;
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

public class RenderFujitecMIC400Button1Old extends BlockEntityRenderer<FujitecMIC400Button1Old.BlockEntity> implements DirectionHelper, IGui, IBlock {

    private static final int HOVER_COLOR = 0xFFFFCC66;
    private static final int PRESSED_COLOR = 0xFFFF8800;
    private static final Identifier BUTTON_TEXTURE = new Identifier(top.xfunny.mod.Init.MOD_ID, "textures/block/fujitec_mic400_button_1.png");
    private static final Identifier BUTTON_LIGHT_TEXTURE = new Identifier(top.xfunny.mod.Init.MOD_ID, "textures/block/fujitec_mic400_button_1_old_light.png");
    private static final Identifier ARROW_TEXTURE_UP = new Identifier(top.xfunny.mod.Init.MOD_ID, "textures/block/fujitec_mic400_button_1_old_arrow_up.png");
    private static final Identifier ARROW_TEXTURE_DOWN = new Identifier(top.xfunny.mod.Init.MOD_ID, "textures/block/fujitec_mic400_button_1_old_arrow_down.png");

    public RenderFujitecMIC400Button1Old(Argument dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(FujitecMIC400Button1Old.BlockEntity blockEntity, float tickDelta, GraphicsHolder graphicsHolder1, int light, int overlay) {
        final World world = blockEntity.getWorld2();
        if (world == null) {
            return;
        }

        final ClientPlayerEntity clientPlayerEntity = MinecraftClient.getInstance().getPlayerMapped();
        if (clientPlayerEntity == null) {
            return;
        }

        final DefaultButtonsKeyMapping keyMapping = blockEntity.getKeyMapping();

        final boolean holdingLinker = PlayerHelper.isHolding(PlayerEntity.cast(clientPlayerEntity), item -> item.data instanceof YteLiftButtonsLinker || item.data instanceof YteGroupLiftButtonsLinker);
        final BlockPos blockPos = blockEntity.getPos2();
        final BlockState blockState = world.getBlockState(blockPos);
        final Direction facing = IBlock.getStatePropertySafe(blockState, FACING);
        LiftButtonsBase.LiftButtonDescriptor buttonDescriptor = new LiftButtonsBase.LiftButtonDescriptor(false, false);

        final StoredMatrixTransformations storedMatrixTransformations = new StoredMatrixTransformations(0.5, 0, 0.5);
        StoredMatrixTransformations storedMatrixTransformations1 = storedMatrixTransformations.copy();
        storedMatrixTransformations1.add(graphicsHolder -> {
            graphicsHolder.rotateYDegrees(-facing.asRotation());
            graphicsHolder.translate(0, 0, 7.95F / 16 - SMALL_OFFSET);
        });


        final LinearLayout parentLayout = new LinearLayout(true);
        parentLayout.setBasicsAttributes(world, blockPos);
        parentLayout.setStoredMatrixTransformations(storedMatrixTransformations1);
        parentLayout.setParentDimensions(4F / 16, 12F / 16);
        parentLayout.setPosition(-0.125F, 0.0625F);
        parentLayout.setWidth(LayoutSize.MATCH_PARENT);
        parentLayout.setHeight(LayoutSize.MATCH_PARENT);


        final LinearLayout screenLayout = new LinearLayout(false);
        screenLayout.setBasicsAttributes(world, blockPos);
        screenLayout.setWidth(LayoutSize.WRAP_CONTENT);
        screenLayout.setHeight(LayoutSize.WRAP_CONTENT);
        screenLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        screenLayout.setMargin(0, 0.7F / 16, 0, 0);


        final FrameLayout buttonLayout = new FrameLayout();
        buttonLayout.setBasicsAttributes(world, blockPos);
        buttonLayout.setWidth(LayoutSize.MATCH_PARENT);
        buttonLayout.setHeight(LayoutSize.MATCH_PARENT);
        buttonLayout.setMargin(0, 1.4F / 16, 0, 0);

        final LinearLayout buttonContainer = new LinearLayout(true);
        buttonContainer.setBasicsAttributes(world, blockPos);
        buttonContainer.setWidth(LayoutSize.WRAP_CONTENT);
        buttonContainer.setHeight(LayoutSize.WRAP_CONTENT);
        buttonContainer.setGravity(Gravity.CENTER);

        final FrameLayout upButtonGroup = new FrameLayout();
        upButtonGroup.setBasicsAttributes(world, blockPos);
        upButtonGroup.setStoredMatrixTransformations(storedMatrixTransformations1);
        upButtonGroup.setWidth(LayoutSize.WRAP_CONTENT);
        upButtonGroup.setHeight(LayoutSize.WRAP_CONTENT);
        upButtonGroup.setGravity(Gravity.CENTER_HORIZONTAL);

        final FrameLayout downButtonGroup = new FrameLayout();
        downButtonGroup.setBasicsAttributes(world, blockPos);
        downButtonGroup.setStoredMatrixTransformations(storedMatrixTransformations1);
        downButtonGroup.setWidth(LayoutSize.WRAP_CONTENT);
        downButtonGroup.setHeight(LayoutSize.WRAP_CONTENT);
        downButtonGroup.setGravity(Gravity.CENTER_HORIZONTAL);

        ImageView buttonUp = new ImageView();
        buttonUp.setBasicsAttributes(world, blockPos);
        buttonUp.setTexture(BUTTON_TEXTURE);
        buttonUp.setDimension(0.7F / 16);
        buttonUp.setGravity(Gravity.CENTER);
        buttonUp.setLight(light);

        ImageView arrowUp = new ImageView();
        arrowUp.setBasicsAttributes(world, blockPos);
        arrowUp.setTexture(ARROW_TEXTURE_UP);
        arrowUp.setDimension(0.7F / 16);
        arrowUp.setGravity(Gravity.CENTER);
        arrowUp.setLight(light);

        ButtonView buttonUpLight = new ButtonView();
        buttonUpLight.setId("up");
        buttonUpLight.setBasicsAttributes(world, blockPos, keyMapping);
        buttonUpLight.setTexture(BUTTON_LIGHT_TEXTURE);
        buttonUpLight.setDimension(0.7F / 16);
        buttonUpLight.setGravity(Gravity.CENTER);
        buttonUpLight.setLight(light);
        buttonUpLight.setDefaultColor(0xFF444444);
        buttonUpLight.setHoverColor(HOVER_COLOR);
        buttonUpLight.setPressedColor(PRESSED_COLOR);

        ImageView buttonDown = new ImageView();
        buttonDown.setBasicsAttributes(world, blockPos);
        buttonDown.setTexture(BUTTON_TEXTURE);
        buttonDown.setDimension(0.7F / 16);
        buttonDown.setGravity(Gravity.CENTER);
        buttonDown.setLight(light);

        ImageView arrowDown = new ImageView();
        arrowDown.setBasicsAttributes(world, blockPos);
        arrowDown.setTexture(ARROW_TEXTURE_DOWN);
        arrowDown.setDimension(0.7F / 16);
        arrowDown.setGravity(Gravity.CENTER);
        arrowDown.setLight(light);

        ButtonView buttonDownLight = new ButtonView();
        buttonDownLight.setId("down");
        buttonDownLight.setBasicsAttributes(world, blockPos, keyMapping);
        buttonDownLight.setTexture(BUTTON_LIGHT_TEXTURE);
        buttonDownLight.setDimension(0.7F / 16);
        buttonDownLight.setGravity(Gravity.CENTER);
        buttonDownLight.setLight(light);
        buttonDownLight.setDefaultColor(0xFF444444);
        buttonDownLight.setHoverColor(HOVER_COLOR);
        buttonDownLight.setPressedColor(PRESSED_COLOR);


        final LineComponent line = new LineComponent();
        line.setBasicsAttributes(world, blockPos);


        final ObjectArrayList<ObjectObjectImmutablePair<BlockPos, Lift>> sortedPositionsAndLifts = new ObjectArrayList<>();


        blockEntity.forEachTrackPosition(trackPosition -> {

            line.RenderLine(holdingLinker, trackPosition);


            FujitecMIC400Button1Old.hasButtonsClient(trackPosition, buttonDescriptor, (floorIndex, lift) -> {
                sortedPositionsAndLifts.add(new ObjectObjectImmutablePair<>(trackPosition, lift));
                final ObjectArraySet<LiftDirection> instructionDirections = lift.hasInstruction(floorIndex);
                instructionDirections.forEach(liftDirection -> {
                    switch (liftDirection) {
                        case DOWN:
                            buttonDownLight.activate();
                            break;
                        case UP:
                            buttonUpLight.activate();
                            break;
                    }
                });
            });
        });


        sortedPositionsAndLifts.sort(Comparator.comparingInt(sortedPositionAndLift -> blockPos.getManhattanDistance(new Vector3i(sortedPositionAndLift.left().data))));

        if (!sortedPositionsAndLifts.isEmpty()) {

            final int count = Math.min(2, sortedPositionsAndLifts.size());
            final boolean reverseRendering = count > 1 && ReverseRendering.reverseRendering(facing.rotateYCounterclockwise(), sortedPositionsAndLifts.get(0).left(), sortedPositionsAndLifts.get(1).left());


            for (int i = 0; i < count; i++) {

                final LiftFloorDisplayView liftFloorDisplayView = new LiftFloorDisplayView();
                liftFloorDisplayView.setBasicsAttributes(world,
                        blockPos,
                        sortedPositionsAndLifts.get(i).right(),
                        FontList.instance.getFont("fujitec_computer_control"),
                        4.1F,
                        0xFFE71F1B);
                liftFloorDisplayView.setDisplayLength(2, 0);
                liftFloorDisplayView.setTextureId(String.format("fujitec_mic400_button_1_old_display_%d", i));
                liftFloorDisplayView.setWidth(1.5F / 16);
                liftFloorDisplayView.setHeight(1.7F / 16);
                liftFloorDisplayView.setMargin(-0.065F / 16, 0, 0.3F / 16, 0);
                liftFloorDisplayView.setTextAlign(TextView.HorizontalTextAlign.RIGHT);


                final LiftArrowView liftArrowView = new LiftArrowView();
                liftArrowView.setBasicsAttributes(world, blockPos, sortedPositionsAndLifts.get(i).right(), LiftArrowView.ArrowType.AUTO);
                liftArrowView.setAnimatedTexture("yte", "textures/block/fujitec_mic400_round_arrow/fujitec_mic400_round_arrow_", 7, 2.85f);
                liftArrowView.setQueuedRenderLayer(QueuedRenderLayer.LIGHT_TRANSLUCENT);
                liftArrowView.setDimension(0.4F / 16);
                liftArrowView.setMargin(0, 2.27F / 16, 0, 0);
                liftArrowView.setGravity(Gravity.CENTER_HORIZONTAL);
                liftArrowView.setColor(0xFFFFFFFF);


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

        upButtonGroup.addChild(buttonUp);
        upButtonGroup.addChild(arrowUp);
        upButtonGroup.addChild(buttonUpLight);
        downButtonGroup.addChild(buttonDown);
        downButtonGroup.addChild(arrowDown);
        downButtonGroup.addChild(buttonDownLight);

        if (buttonDescriptor.hasUpButton()) {
            buttonContainer.addChild(upButtonGroup);
        }

        if (buttonDescriptor.hasDownButton()) {
            if (buttonDescriptor.hasUpButton()) {
                downButtonGroup.setMargin(0, 0.55F / 16, 0, 0);
            }
            buttonContainer.addChild(downButtonGroup);
        }

        buttonLayout.addChild(buttonContainer);
        parentLayout.addChild(screenLayout);
        parentLayout.addChild(buttonLayout);

        parentLayout.render();
    }
}