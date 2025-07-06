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
import org.mtr.mod.render.StoredMatrixTransformations;
import top.xfunny.mod.Init;
import top.xfunny.mod.block.OtisSeries3Button1;
import top.xfunny.mod.block.base.LiftButtonsBase;
import top.xfunny.mod.client.view.*;
import top.xfunny.mod.client.view.view_group.FrameLayout;
import top.xfunny.mod.client.view.view_group.LinearLayout;
import top.xfunny.mod.item.YteGroupLiftButtonsLinker;
import top.xfunny.mod.item.YteLiftButtonsLinker;
import top.xfunny.mod.keymapping.DefaultButtonsKeyMapping;

public class RenderOtisSeries3Button1 extends BlockEntityRenderer<OtisSeries3Button1.BlockEntity> implements DirectionHelper, IGui, IBlock {

    private final Identifier BUTTON_TEXTURE = new Identifier(Init.MOD_ID, "textures/block/otis_s3_button_1.png");
    private final Identifier ARROW_TEXTURE = new Identifier(Init.MOD_ID, "textures/block/otis_s3_button_1_arrow.png");
    private final Identifier BUTTON_LIGHT_TEXTURE = new Identifier(Init.MOD_ID, "textures/block/otis_s3_button_1_light.png");

    public RenderOtisSeries3Button1(Argument dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(OtisSeries3Button1.BlockEntity blockEntity, float tickDelta, GraphicsHolder graphicsHolder1, int light, int overlay) {
        final World world = blockEntity.getWorld2();
        if (world == null) {
            return;
        }

        final ClientPlayerEntity clientPlayerEntity = MinecraftClient.getInstance().getPlayerMapped();
        if (clientPlayerEntity == null) {
            return;
        }

        final DefaultButtonsKeyMapping keyMapping = blockEntity.getKeyMapping();

        final BlockPos blockPos = blockEntity.getPos2();
        final BlockState blockState = world.getBlockState(blockPos);
        final Direction facing = IBlock.getStatePropertySafe(blockState, FACING);
        final boolean holdingLinker = PlayerHelper.isHolding(PlayerEntity.cast(clientPlayerEntity), item -> item.data instanceof YteLiftButtonsLinker || item.data instanceof YteGroupLiftButtonsLinker);
        LiftButtonsBase.LiftButtonDescriptor buttonDescriptor = new LiftButtonsBase.LiftButtonDescriptor(false, false);


        final StoredMatrixTransformations storedMatrixTransformations1 = new StoredMatrixTransformations(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5);
        storedMatrixTransformations1.add(graphicsHolder -> {
            graphicsHolder.rotateYDegrees(-facing.asRotation());
            graphicsHolder.translate(0, 0, 7.65F / 16 - SMALL_OFFSET);
        });

        final LinearLayout parentLayout = new LinearLayout(true);
        parentLayout.setBasicsAttributes(world, blockPos);
        parentLayout.setStoredMatrixTransformations(storedMatrixTransformations1);
        parentLayout.setParentDimensions(1.35F / 16, 3.725F / 16);
        parentLayout.setPosition(-0.0405F, 0.9F / 16);
        parentLayout.setWidth(LayoutSize.MATCH_PARENT);
        parentLayout.setHeight(LayoutSize.MATCH_PARENT);


        final LinearLayout buttonContainer = new LinearLayout(true);
        buttonContainer.setBasicsAttributes(world, blockPos);
        buttonContainer.setWidth(LayoutSize.WRAP_CONTENT);
        buttonContainer.setHeight(LayoutSize.WRAP_CONTENT);
        buttonContainer.setMargin(0.1F / 16, 0.45F / 16, 0, 0);

        final LinearLayout upButtonGroup = new LinearLayout(false);
        upButtonGroup.setBasicsAttributes(world, blockPos);
        upButtonGroup.setStoredMatrixTransformations(storedMatrixTransformations1);
        upButtonGroup.setWidth(LayoutSize.WRAP_CONTENT);
        upButtonGroup.setHeight(LayoutSize.WRAP_CONTENT);
        upButtonGroup.setGravity(Gravity.CENTER_HORIZONTAL);

        final LinearLayout downButtonGroup = new LinearLayout(false);
        downButtonGroup.setBasicsAttributes(world, blockPos);
        downButtonGroup.setStoredMatrixTransformations(storedMatrixTransformations1);
        downButtonGroup.setWidth(LayoutSize.WRAP_CONTENT);
        downButtonGroup.setHeight(LayoutSize.WRAP_CONTENT);
        downButtonGroup.setGravity(Gravity.CENTER_HORIZONTAL);

        final FrameLayout upButtonLightGroup = new FrameLayout();
        upButtonLightGroup.setBasicsAttributes(world, blockPos);
        upButtonLightGroup.setWidth(LayoutSize.WRAP_CONTENT);
        upButtonLightGroup.setHeight(LayoutSize.WRAP_CONTENT);
        upButtonLightGroup.setGravity(Gravity.END);

        final FrameLayout downButtonLightGroup = new FrameLayout();
        downButtonLightGroup.setBasicsAttributes(world, blockPos);
        downButtonLightGroup.setWidth(LayoutSize.WRAP_CONTENT);
        downButtonLightGroup.setHeight(LayoutSize.WRAP_CONTENT);
        downButtonLightGroup.setGravity(Gravity.END);

        ImageView buttonUpArrow = new ImageView();
        buttonUpArrow.setBasicsAttributes(world, blockPos);
        buttonUpArrow.setTexture(ARROW_TEXTURE);
        buttonUpArrow.setDimension(0.6F / 16);
        buttonUpArrow.setGravity(Gravity.START);
        buttonUpArrow.setLight(light);

        ImageView buttonDownArrow = new ImageView();
        buttonDownArrow.setBasicsAttributes(world, blockPos);
        buttonDownArrow.setTexture(ARROW_TEXTURE);
        buttonDownArrow.setDimension(0.6F / 16);
        buttonDownArrow.setGravity(Gravity.START);
        buttonDownArrow.setLight(light);
        buttonDownArrow.setFlip(false, true);

        ImageView buttonUpBackground = new ImageView();
        buttonUpBackground.setBasicsAttributes(world, blockPos);
        buttonUpBackground.setTexture(BUTTON_TEXTURE);
        buttonUpBackground.setDimension(0.6F / 16);
        buttonUpBackground.setGravity(Gravity.CENTER);
        buttonUpBackground.setLight(light);

        ImageView buttonDownBackground = new ImageView();
        buttonDownBackground.setBasicsAttributes(world, blockPos);
        buttonDownBackground.setTexture(BUTTON_TEXTURE);
        buttonDownBackground.setDimension(0.6F / 16);
        buttonDownBackground.setGravity(Gravity.CENTER);
        buttonDownBackground.setLight(light);

        ButtonView buttonUpLight = new ButtonView();
        buttonUpLight.setId("up");
        buttonUpLight.setBasicsAttributes(world, blockPos, keyMapping);
        buttonUpLight.setTexture(BUTTON_LIGHT_TEXTURE);
        buttonUpLight.setDimension(0.6F / 16);
        buttonUpLight.setGravity(Gravity.CENTER);
        buttonUpLight.setLight(light);
        buttonUpLight.setDefaultColor(0xFF111111);
        buttonUpLight.setHoverColor(0xFFCCFFCC);
        buttonUpLight.setPressedColor(0xFF66FF66);

        ButtonView buttonDownLight = new ButtonView();
        buttonDownLight.setId("down");
        buttonDownLight.setBasicsAttributes(world, blockPos, keyMapping);
        buttonDownLight.setTexture(BUTTON_LIGHT_TEXTURE);
        buttonDownLight.setDimension(0.6F / 16);
        buttonDownLight.setGravity(Gravity.CENTER);
        buttonDownLight.setLight(light);
        buttonDownLight.setDefaultColor(0xFF111111);
        buttonDownLight.setHoverColor(0xFFFF9999);
        buttonDownLight.setPressedColor(0xFFFF0000);

        final LineComponent line = new LineComponent();
        line.setBasicsAttributes(world, blockPos);

        final ObjectArrayList<ObjectObjectImmutablePair<BlockPos, Lift>> sortedPositionsAndLifts = new ObjectArrayList<>();

        blockEntity.forEachTrackPosition(trackPosition -> {
            line.RenderLine(holdingLinker, trackPosition);

            OtisSeries3Button1.hasButtonsClient(trackPosition, buttonDescriptor, (floorIndex, lift) -> {
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

        upButtonLightGroup.addChild(buttonUpBackground);
        upButtonLightGroup.addChild(buttonUpLight);

        downButtonLightGroup.addChild(buttonDownBackground);
        downButtonLightGroup.addChild(buttonDownLight);

        if (buttonDescriptor.hasUpButton()) {
            upButtonGroup.addChild(buttonUpArrow);
            upButtonGroup.addChild(upButtonLightGroup);
            buttonContainer.addChild(upButtonGroup);
        }

        if (buttonDescriptor.hasDownButton()) {
            if (buttonDescriptor.hasUpButton()) {
                downButtonGroup.setMargin(0, 0.35F / 16, 0, 0);
            }
            downButtonGroup.addChild(buttonDownArrow);
            downButtonGroup.addChild(downButtonLightGroup);
            buttonContainer.addChild(downButtonGroup);
        }

        parentLayout.addChild(buttonContainer);

        parentLayout.render();
    }
}
