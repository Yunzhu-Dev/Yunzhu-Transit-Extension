package top.xfunny.mod.client.render;

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
import top.xfunny.mod.block.SchindlerZLine3Keypad1;
import top.xfunny.mod.client.resource.FontList;
import top.xfunny.mod.client.view.*;
import top.xfunny.mod.client.view.view_group.FrameLayout;
import top.xfunny.mod.client.view.view_group.LinearLayout;
import top.xfunny.mod.item.YteGroupLiftButtonsLinker;
import top.xfunny.mod.item.YteLiftButtonsLinker;
import top.xfunny.mod.keymapping.DefaultButtonsKeyMapping;
import top.xfunny.mod.util.ArrayListToString;

import java.util.ArrayList;

public class RenderSchindlerZLine3Keypad1 extends BlockEntityRenderer<SchindlerZLine3Keypad1.BlockEntity> implements DirectionHelper, IGui, IBlock {
    private static final int HOVER_COLOR = 0xFFFFFFFF;
    private static final int DEFAULT_COLOR = 0xFFFFFFFF;
    private final boolean isOdd;
    private final float buttonMarginRight = 0.225F / 16;

    private final Identifier NUMBER1_TEXTURE = new Identifier(Init.MOD_ID, "textures/block/schindler_z_line_keypad/number1.png");
    private final Identifier NUMBER2_TEXTURE = new Identifier(Init.MOD_ID, "textures/block/schindler_z_line_keypad/number2.png");
    private final Identifier NUMBER3_TEXTURE = new Identifier(Init.MOD_ID, "textures/block/schindler_z_line_keypad/number3.png");
    private final Identifier NUMBER4_TEXTURE = new Identifier(Init.MOD_ID, "textures/block/schindler_z_line_keypad/number4.png");
    private final Identifier NUMBER5_TEXTURE = new Identifier(Init.MOD_ID, "textures/block/schindler_z_line_keypad/number5.png");
    private final Identifier NUMBER6_TEXTURE = new Identifier(Init.MOD_ID, "textures/block/schindler_z_line_keypad/number6.png");
    private final Identifier NUMBER7_TEXTURE = new Identifier(Init.MOD_ID, "textures/block/schindler_z_line_keypad/number7.png");
    private final Identifier NUMBER8_TEXTURE = new Identifier(Init.MOD_ID, "textures/block/schindler_z_line_keypad/number8.png");
    private final Identifier NUMBER9_TEXTURE = new Identifier(Init.MOD_ID, "textures/block/schindler_z_line_keypad/number9.png");
    private final Identifier NUMBER0_TEXTURE = new Identifier(Init.MOD_ID, "textures/block/schindler_z_line_keypad/number0.png");
    private final Identifier LOBBY_TEXTURE = new Identifier(Init.MOD_ID, "textures/block/schindler_z_line_keypad/lobby.png");
    private final Identifier BASEMENT_TEXTURE = new Identifier(Init.MOD_ID, "textures/block/schindler_z_line_keypad/basement.png");
    private final Identifier ACCESSBILITY_TEXTURE = new Identifier(Init.MOD_ID, "textures/block/schindler_z_line_keypad/accessibility.png");
    private final Identifier BACKGROUND_TEXTURE = new Identifier(Init.MOD_ID, "textures/block/white.png");
    private final Identifier ACCESSBILITY_ICON_TEXTURE = new Identifier(Init.MOD_ID, "textures/block/schindler_z_line_keypad/accessibility_icon.png");

    public RenderSchindlerZLine3Keypad1(Argument dispatcher, boolean isOdd) {
        super(dispatcher);
        this.isOdd = isOdd;
    }

    public void render(SchindlerZLine3Keypad1.BlockEntity blockEntity, float tickDelta, GraphicsHolder graphicsHolder1, int light, int overlay) {
        final World world = blockEntity.getWorld2();
        final String screenId = blockEntity.getScreenId();

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
        final ArrayList<Object> inputNumber = blockEntity.getInputString();

        final StoredMatrixTransformations storedMatrixTransformations = new StoredMatrixTransformations(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5);
        StoredMatrixTransformations storedMatrixTransformations1 = storedMatrixTransformations.copy();
        storedMatrixTransformations1.add(graphicsHolder -> {
            graphicsHolder.rotateYDegrees(-facing.asRotation());
            graphicsHolder.translate(0, 0, 7F / 16 - SMALL_OFFSET);
        });

        final LineComponent line = new LineComponent();
        line.setBasicsAttributes(world, blockPos);
        blockEntity.forEachTrackPosition(trackPosition -> {
            line.RenderLine(holdingLinker, trackPosition);
        });

        final LinearLayout parentLayout = new LinearLayout(true);
        parentLayout.setBasicsAttributes(world, blockPos);
        parentLayout.setStoredMatrixTransformations(storedMatrixTransformations1);
        parentLayout.setParentDimensions(6F / 16, 16F / 16);
        parentLayout.setPosition(-3F / 16, 0);
        parentLayout.setWidth(LayoutSize.MATCH_PARENT);//宽度为match_parent，即占满父容器，最底层父容器大小已通过setParentDimensions设置
        parentLayout.setHeight(LayoutSize.MATCH_PARENT);//高度为match_parent，即占满父容器，最底层父容器大小已通过setParentDimensions设置

        final LinearLayout group1 = new LinearLayout(false);
        group1.setBasicsAttributes(world, blockPos);
        group1.setWidth(LayoutSize.MATCH_PARENT);
        group1.setHeight(LayoutSize.WRAP_CONTENT);
        group1.setMargin(1.25F / 16, screenId.equals("schindler_z_line_3_keypad_1_key_mapping_input") || screenId.equals("schindler_z_line_3_keypad_1_key_mapping_accessibility") || screenId.equals("schindler_z_line_3_keypad_1_key_mapping_identifier") ? 1.175F / 16 : 5.6F / 16, 0, 0);
        group1.addStoredMatrixTransformations(graphicsHolder -> graphicsHolder.translate(0, 0, -0.2F / 16 + 0.9 * SMALL_OFFSET));

        final LinearLayout group2 = new LinearLayout(false);
        group2.setBasicsAttributes(world, blockPos);
        group2.setWidth(LayoutSize.MATCH_PARENT);
        group2.setHeight(LayoutSize.WRAP_CONTENT);
        group2.setMargin(1.25F / 16, 0.625F / 16, 0, 0);

        final LinearLayout group3 = new LinearLayout(false);
        group3.setBasicsAttributes(world, blockPos);
        group3.setWidth(LayoutSize.MATCH_PARENT);
        group3.setHeight(LayoutSize.WRAP_CONTENT);
        group3.setMargin(1.25F / 16, 0.625F / 16, 0, 0);

        final LinearLayout group4 = new LinearLayout(false);
        group4.setBasicsAttributes(world, blockPos);
        group4.setWidth(LayoutSize.MATCH_PARENT);
        group4.setHeight(LayoutSize.WRAP_CONTENT);
        group4.setMargin(1.25F / 16, 0.625F / 16, 0, 0);

        if (screenId.equals("schindler_z_line_3_keypad_1_key_mapping_input") || screenId.equals("schindler_z_line_3_keypad_1_key_mapping_identifier")) {
            final FrameLayout textBackgroundLayout = new FrameLayout();
            textBackgroundLayout.setBasicsAttributes(world, blockPos);
            textBackgroundLayout.setStoredMatrixTransformations(storedMatrixTransformations1);
            textBackgroundLayout.setWidth(LayoutSize.WRAP_CONTENT);
            textBackgroundLayout.setHeight(LayoutSize.WRAP_CONTENT);
            textBackgroundLayout.setGravity(Gravity.CENTER_HORIZONTAL);
            textBackgroundLayout.setMargin(0, 1.85F / 16, 0, 0);

            final ImageView imageView = new ImageView();
            imageView.setBasicsAttributes(world, blockPos);
            imageView.setTexture(BACKGROUND_TEXTURE);
            imageView.setDimension(3.5F / 16, 2.575F / 3.5F);
            imageView.setLight(light);
            imageView.setQueuedRenderLayer(QueuedRenderLayer.LIGHT_TRANSLUCENT);
            imageView.setGravity(Gravity.CENTER);

            final TextView textView = new TextView();
            textView.setId("textView");
            textView.setBasicsAttributes(world, blockPos, FontList.instance.getFont("Arial"), 6, 0xFF212121);
            textView.setDisplayLength(6, 0.005F);
            textView.setTextureId("schindler_z_line_3_keypad_1_display");
            textView.setText(ArrayListToString.arrayListToString(inputNumber));
            textView.setWidth(2F / 16);
            textView.setHeight(2F / 16);
            textView.setTextAlign(TextView.HorizontalTextAlign.CENTER);
            textView.setGravity(Gravity.CENTER);

            textBackgroundLayout.addChild(imageView);
            textBackgroundLayout.addChild(textView);
            parentLayout.addChild(textBackgroundLayout);

        } else if (screenId.equals("schindler_z_line_3_keypad_1_key_mapping_accessibility")) {
            final FrameLayout textBackgroundLayout = new FrameLayout();
            textBackgroundLayout.setBasicsAttributes(world, blockPos);
            textBackgroundLayout.setStoredMatrixTransformations(storedMatrixTransformations1);
            textBackgroundLayout.setWidth(LayoutSize.WRAP_CONTENT);
            textBackgroundLayout.setHeight(LayoutSize.WRAP_CONTENT);
            textBackgroundLayout.setGravity(Gravity.CENTER_HORIZONTAL);
            textBackgroundLayout.setMargin(0, 1.85F / 16, 0, 0);

            final ImageView imageView = new ImageView();
            imageView.setBasicsAttributes(world, blockPos);
            imageView.setTexture(BACKGROUND_TEXTURE);
            imageView.setDimension(3.5F / 16, 2.575F / 3.5F);
            imageView.setLight(light);
            imageView.setQueuedRenderLayer(QueuedRenderLayer.LIGHT_TRANSLUCENT);
            imageView.setGravity(Gravity.CENTER);

            final ImageView imageAccessibility = new ImageView();
            imageAccessibility.setBasicsAttributes(world, blockPos);
            imageAccessibility.setTexture(ACCESSBILITY_ICON_TEXTURE);
            imageAccessibility.setDimension(1F / 16);
            imageAccessibility.setLight(light);
            imageAccessibility.setQueuedRenderLayer(QueuedRenderLayer.LIGHT_TRANSLUCENT);
            imageAccessibility.setGravity(Gravity.CENTER);

            textBackgroundLayout.addChild(imageView);
            textBackgroundLayout.addChild(imageAccessibility);
            parentLayout.addChild(textBackgroundLayout);
        }

        final ButtonView number1 = new ButtonView();
        number1.setId("number1");
        number1.setBasicsAttributes(world, blockPos, keyMapping);
        number1.setDimension(1F / 16, 94, 101);
        number1.setLight(light);
        number1.setDefaultColor(DEFAULT_COLOR);
        number1.setHoverColor(HOVER_COLOR);
        number1.setTexture(NUMBER1_TEXTURE);
        number1.setMargin(0, 0, buttonMarginRight, 0);

        final ButtonView number2 = new ButtonView();
        number2.setId("number2");
        number2.setBasicsAttributes(world, blockPos, keyMapping);
        number2.setDimension(1F / 16, 94, 101);
        number2.setLight(light);
        number2.setDefaultColor(DEFAULT_COLOR);
        number2.setHoverColor(HOVER_COLOR);
        number2.setTexture(NUMBER2_TEXTURE);
        number2.setMargin(0, 0, buttonMarginRight, 0);

        final ButtonView number3 = new ButtonView();
        number3.setId("number3");
        number3.setBasicsAttributes(world, blockPos, keyMapping);
        number3.setDimension(1F / 16, 94, 101);
        number3.setLight(light);
        number3.setDefaultColor(DEFAULT_COLOR);
        number3.setHoverColor(HOVER_COLOR);
        number3.setTexture(NUMBER3_TEXTURE);

        final ButtonView number4 = new ButtonView();
        number4.setId("number4");
        number4.setBasicsAttributes(world, blockPos, keyMapping);
        number4.setDimension(1F / 16, 94, 101);
        number4.setLight(light);
        number4.setDefaultColor(DEFAULT_COLOR);
        number4.setHoverColor(HOVER_COLOR);
        number4.setTexture(NUMBER4_TEXTURE);
        number4.setMargin(0, 0, buttonMarginRight, 0);

        final ButtonView number5 = new ButtonView();
        number5.setId("number5");
        number5.setBasicsAttributes(world, blockPos, keyMapping);
        number5.setDimension(1F / 16, 94, 101);
        number5.setLight(light);
        number5.setDefaultColor(DEFAULT_COLOR);
        number5.setHoverColor(HOVER_COLOR);
        number5.setTexture(NUMBER5_TEXTURE);
        number5.setMargin(0, 0, buttonMarginRight, 0);

        final ButtonView number6 = new ButtonView();
        number6.setId("number6");
        number6.setBasicsAttributes(world, blockPos, keyMapping);
        number6.setDimension(1F / 16, 94, 101);
        number6.setLight(light);
        number6.setDefaultColor(DEFAULT_COLOR);
        number6.setHoverColor(HOVER_COLOR);
        number6.setTexture(NUMBER6_TEXTURE);

        final ButtonView number7 = new ButtonView();
        number7.setId("number7");
        number7.setBasicsAttributes(world, blockPos, keyMapping);
        number7.setDimension(1F / 16, 94, 101);
        number7.setLight(light);
        number7.setDefaultColor(DEFAULT_COLOR);
        number7.setHoverColor(HOVER_COLOR);
        number7.setTexture(NUMBER7_TEXTURE);
        number7.setMargin(0, 0, buttonMarginRight, 0);

        final ButtonView number8 = new ButtonView();
        number8.setId("number8");
        number8.setBasicsAttributes(world, blockPos, keyMapping);
        number8.setDimension(1F / 16, 94, 101);
        number8.setLight(light);
        number8.setDefaultColor(DEFAULT_COLOR);
        number8.setHoverColor(HOVER_COLOR);
        number8.setTexture(NUMBER8_TEXTURE);
        number8.setMargin(0, 0, buttonMarginRight, 0);

        final ButtonView number9 = new ButtonView();
        number9.setId("number9");
        number9.setBasicsAttributes(world, blockPos, keyMapping);
        number9.setDimension(1F / 16, 94, 101);
        number9.setLight(light);
        number9.setDefaultColor(DEFAULT_COLOR);
        number9.setHoverColor(HOVER_COLOR);
        number9.setTexture(NUMBER9_TEXTURE);

        final ButtonView number0 = new ButtonView();
        number0.setId("number0");
        number0.setBasicsAttributes(world, blockPos, keyMapping);
        number0.setDimension(1F / 16, 94, 101);
        number0.setLight(light);
        number0.setDefaultColor(DEFAULT_COLOR);
        number0.setHoverColor(HOVER_COLOR);
        number0.setTexture(NUMBER0_TEXTURE);
        number0.setMargin(0, 0, buttonMarginRight, 0);

        final ButtonView lobby = new ButtonView();
        lobby.setId("lobby");
        lobby.setBasicsAttributes(world, blockPos, keyMapping);
        lobby.setDimension(1F / 16, 94, 101);
        lobby.setLight(light);
        lobby.setDefaultColor(DEFAULT_COLOR);
        lobby.setHoverColor(HOVER_COLOR);
        lobby.setTexture(LOBBY_TEXTURE);
        lobby.setMargin(0, 0, buttonMarginRight, 0);

        final ButtonView basement = new ButtonView();
        basement.setId("basement");
        basement.setBasicsAttributes(world, blockPos, keyMapping);
        basement.setDimension(1F / 16, 94, 101);
        basement.setLight(light);
        basement.setDefaultColor(DEFAULT_COLOR);
        basement.setHoverColor(HOVER_COLOR);
        basement.setTexture(BASEMENT_TEXTURE);


        final ButtonView accessibility = new ButtonView();
        accessibility.setId("accessibility");
        accessibility.setBasicsAttributes(world, blockPos, keyMapping);
        accessibility.setDimension(3.5F / 16, 326, 98);
        accessibility.setLight(light);
        accessibility.setDefaultColor(DEFAULT_COLOR);
        accessibility.setHoverColor(HOVER_COLOR);
        accessibility.setTexture(ACCESSBILITY_TEXTURE);
        accessibility.setMargin(1.25F / 16, 0.95F / 16, 0, 0);

        group1.addChild(number1);
        group1.addChild(number2);
        group1.addChild(number3);

        group2.addChild(number4);
        group2.addChild(number5);
        group2.addChild(number6);

        group3.addChild(number7);
        group3.addChild(number8);
        group3.addChild(number9);

        group4.addChild(lobby);
        group4.addChild(number0);
        group4.addChild(basement);

        parentLayout.addChild(group1);
        parentLayout.addChild(group2);
        parentLayout.addChild(group3);
        parentLayout.addChild(group4);
        parentLayout.addChild(accessibility);

        parentLayout.render();
    }
}
