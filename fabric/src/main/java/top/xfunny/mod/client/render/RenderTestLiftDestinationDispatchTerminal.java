package top.xfunny.mod.client.render;

import org.mtr.mapping.holder.*;
import org.mtr.mapping.mapper.BlockEntityRenderer;
import org.mtr.mapping.mapper.DirectionHelper;
import org.mtr.mapping.mapper.GraphicsHolder;
import org.mtr.mapping.mapper.PlayerHelper;
import org.mtr.mod.block.IBlock;
import org.mtr.mod.data.IGui;
import org.mtr.mod.render.StoredMatrixTransformations;
import top.xfunny.mod.Init;
import top.xfunny.mod.block.TestLiftDestinationDispatchTerminal;
import top.xfunny.mod.client.resource.FontList;
import top.xfunny.mod.client.view.ButtonView;
import top.xfunny.mod.client.view.LayoutSize;
import top.xfunny.mod.client.view.LineComponent;
import top.xfunny.mod.client.view.TextView;
import top.xfunny.mod.client.view.view_group.LinearLayout;
import top.xfunny.mod.item.YteGroupLiftButtonsLinker;
import top.xfunny.mod.item.YteLiftButtonsLinker;
import top.xfunny.mod.keymapping.DefaultButtonsKeyMapping;
import top.xfunny.mod.util.ArrayListToString;

import java.util.ArrayList;

public class RenderTestLiftDestinationDispatchTerminal extends BlockEntityRenderer<TestLiftDestinationDispatchTerminal.BlockEntity> implements DirectionHelper, IGui, IBlock {
    private static final int HOVER_COLOR = 0xFFFFCC66;
    private static final int DEFAULT_COLOR = 0xFFFFFFF;
    private final boolean isOdd;

    public RenderTestLiftDestinationDispatchTerminal(Argument dispatcher, boolean isOdd) {
        super(dispatcher);
        this.isOdd = isOdd;
    }

    @Override
    public void render(TestLiftDestinationDispatchTerminal.BlockEntity blockEntity, float tickDelta, GraphicsHolder graphicsHolder1, int light, int overlay) {
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
        final ArrayList<Object> inputNumber = blockEntity.getInputNumber();

        final StoredMatrixTransformations storedMatrixTransformations = new StoredMatrixTransformations(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5);
        StoredMatrixTransformations storedMatrixTransformations1 = storedMatrixTransformations.copy();
        storedMatrixTransformations1.add(graphicsHolder -> {
            graphicsHolder.rotateYDegrees(-facing.asRotation());
            graphicsHolder.translate(0, 0, 7.9F / 16 - SMALL_OFFSET);
        });

        final LineComponent line = new LineComponent();
        line.setBasicsAttributes(world, blockPos);
        blockEntity.forEachTrackPosition(trackPosition -> {
            line.RenderLine(holdingLinker, trackPosition);
        });

        final LinearLayout parentLayout = new LinearLayout(true);
        parentLayout.setBasicsAttributes(world, blockPos);
        parentLayout.setStoredMatrixTransformations(storedMatrixTransformations1);
        parentLayout.setParentDimensions(16F / 16, 11F / 16);
        parentLayout.setPosition(-8F / 16, 0);
        parentLayout.setWidth(LayoutSize.MATCH_PARENT);//宽度为match_parent，即占满父容器，最底层父容器大小已通过setParentDimensions设置
        parentLayout.setHeight(LayoutSize.MATCH_PARENT);//高度为match_parent，即占满父容器，最底层父容器大小已通过setParentDimensions设置

        if (screenId.equals("test_lift_destination_dispatch_terminal_key_mapping_home")) {
            final LinearLayout group1 = new LinearLayout(false);
            group1.setBasicsAttributes(world, blockPos);
            group1.setWidth(LayoutSize.MATCH_PARENT);
            group1.setHeight(LayoutSize.WRAP_CONTENT);
            group1.setMargin(0, 1F / 16, 0, 0);

            final LinearLayout group2 = new LinearLayout(false);
            group2.setBasicsAttributes(world, blockPos);
            group2.setWidth(LayoutSize.MATCH_PARENT);
            group2.setHeight(LayoutSize.WRAP_CONTENT);
            group2.setMargin(0, 1F / 16, 0, 0);

            final LinearLayout group3 = new LinearLayout(false);
            group3.setBasicsAttributes(world, blockPos);
            group3.setWidth(LayoutSize.MATCH_PARENT);
            group3.setHeight(LayoutSize.WRAP_CONTENT);
            group3.setMargin(0, 1F / 16, 0, 0);

            final TextView textView = new TextView();
            textView.setId("textView");
            textView.setBasicsAttributes(world, blockPos, FontList.instance.getFont("mitsubishi_modern"), 6, HOVER_COLOR);
            textView.setDisplayLength(19, 0.005F);
            textView.setTextureId("test_lift_destination_dispatch_terminal_display");
            textView.setText(ArrayListToString.arrayListToString(inputNumber));
            textView.setWidth(11F / 16);
            textView.setHeight(2F / 16);
            textView.setMargin(1F / 16, 1F / 16, 0, 0);
            textView.setTextAlign(TextView.HorizontalTextAlign.LEFT);

            final ButtonView number1 = new ButtonView();
            number1.setId("number1");
            number1.setBasicsAttributes(world, blockPos, keyMapping);
            number1.setTexture(new Identifier(Init.MOD_ID, "textures/block/test_lift_destination_dispatch_terminal/number1.png"));
            number1.setDimension(1F / 16);
            number1.setLight(light);
            number1.setDefaultColor(DEFAULT_COLOR);
            number1.setHoverColor(HOVER_COLOR);
            number1.setMargin(1F / 16, 0, 0, 0);

            final ButtonView number2 = new ButtonView();
            number2.setId("number2");
            number2.setBasicsAttributes(world, blockPos, keyMapping);
            number2.setTexture(new Identifier(Init.MOD_ID, "textures/block/test_lift_destination_dispatch_terminal/number2.png"));
            number2.setDimension(1F / 16);
            number2.setLight(light);
            number2.setDefaultColor(DEFAULT_COLOR);
            number2.setHoverColor(HOVER_COLOR);
            number2.setMargin(1F / 16, 0, 0, 0);

            final ButtonView number3 = new ButtonView();
            number3.setId("number3");
            number3.setBasicsAttributes(world, blockPos, keyMapping);
            number3.setTexture(new Identifier(Init.MOD_ID, "textures/block/test_lift_destination_dispatch_terminal/number3.png"));
            number3.setDimension(1F / 16);
            number3.setLight(light);
            number3.setDefaultColor(DEFAULT_COLOR);
            number3.setHoverColor(HOVER_COLOR);
            number3.setMargin(1F / 16, 0, 0, 0);

            final ButtonView number4 = new ButtonView();
            number4.setId("number4");
            number4.setBasicsAttributes(world, blockPos, keyMapping);
            number4.setTexture(new Identifier(Init.MOD_ID, "textures/block/test_lift_destination_dispatch_terminal/number4.png"));
            number4.setDimension(1F / 16);
            number4.setLight(light);
            number4.setDefaultColor(DEFAULT_COLOR);
            number4.setHoverColor(HOVER_COLOR);
            number4.setMargin(1F / 16, 0, 0, 0);

            final ButtonView number5 = new ButtonView();
            number5.setId("number5");
            number5.setBasicsAttributes(world, blockPos, keyMapping);
            number5.setTexture(new Identifier(Init.MOD_ID, "textures/block/test_lift_destination_dispatch_terminal/number5.png"));
            number5.setDimension(1F / 16);
            number5.setLight(light);
            number5.setDefaultColor(DEFAULT_COLOR);
            number5.setHoverColor(HOVER_COLOR);
            number5.setMargin(1F / 16, 0, 0, 0);

            final ButtonView number6 = new ButtonView();
            number6.setId("number6");
            number6.setBasicsAttributes(world, blockPos, keyMapping);
            number6.setTexture(new Identifier(Init.MOD_ID, "textures/block/test_lift_destination_dispatch_terminal/number6.png"));
            number6.setDimension(1F / 16);
            number6.setLight(light);
            number6.setDefaultColor(DEFAULT_COLOR);
            number6.setHoverColor(HOVER_COLOR);
            number6.setMargin(1F / 16, 0, 0, 0);

            final ButtonView number7 = new ButtonView();
            number7.setId("number7");
            number7.setBasicsAttributes(world, blockPos, keyMapping);
            number7.setTexture(new Identifier(Init.MOD_ID, "textures/block/test_lift_destination_dispatch_terminal/number7.png"));
            number7.setDimension(1F / 16);
            number7.setLight(light);
            number7.setDefaultColor(DEFAULT_COLOR);
            number7.setHoverColor(HOVER_COLOR);
            number7.setMargin(1F / 16, 0, 0, 0);

            final ButtonView number8 = new ButtonView();
            number8.setId("number8");
            number8.setBasicsAttributes(world, blockPos, keyMapping);
            number8.setTexture(new Identifier(Init.MOD_ID, "textures/block/test_lift_destination_dispatch_terminal/number8.png"));
            number8.setDimension(1F / 16);
            number8.setLight(light);
            number8.setDefaultColor(DEFAULT_COLOR);
            number8.setHoverColor(HOVER_COLOR);
            number8.setMargin(1F / 16, 0, 0, 0);

            final ButtonView number9 = new ButtonView();
            number9.setId("number9");
            number9.setBasicsAttributes(world, blockPos, keyMapping);
            number9.setTexture(new Identifier(Init.MOD_ID, "textures/block/test_lift_destination_dispatch_terminal/number9.png"));
            number9.setDimension(1F / 16);
            number9.setLight(light);
            number9.setDefaultColor(DEFAULT_COLOR);
            number9.setHoverColor(HOVER_COLOR);
            number9.setMargin(1F / 16, 0, 0, 0);

            final ButtonView number0 = new ButtonView();
            number0.setId("number0");
            number0.setBasicsAttributes(world, blockPos, keyMapping);
            number0.setTexture(new Identifier(Init.MOD_ID, "textures/block/test_lift_destination_dispatch_terminal/number0.png"));
            number0.setDimension(1F / 16);
            number0.setLight(light);
            number0.setDefaultColor(DEFAULT_COLOR);
            number0.setHoverColor(HOVER_COLOR);
            number0.setMargin(1F / 16, 0, 0, 0);

            final ButtonView clearNumber = new ButtonView();
            clearNumber.setId("clearNumber");
            clearNumber.setBasicsAttributes(world, blockPos, keyMapping);
            clearNumber.setTexture(new Identifier(Init.MOD_ID, "textures/block/cross.png"));
            clearNumber.setDimension(1F / 16);
            clearNumber.setLight(light);
            clearNumber.setDefaultColor(DEFAULT_COLOR);
            clearNumber.setHoverColor(HOVER_COLOR);
            clearNumber.setMargin(1F / 16, 0, 0, 0);

            final ButtonView callLift = new ButtonView();
            callLift.setId("callLift");
            callLift.setBasicsAttributes(world, blockPos, keyMapping);
            callLift.setTexture(new Identifier(Init.MOD_ID, "textures/block/test_lift_destination_dispatch_terminal/call_lift.png"));
            callLift.setDimension(1F / 16);
            callLift.setLight(light);
            callLift.setDefaultColor(DEFAULT_COLOR);
            callLift.setHoverColor(HOVER_COLOR);
            callLift.setMargin(1F / 16, 0, 0, 0);

            group1.addChild(number1);
            group1.addChild(number2);
            group1.addChild(number3);
            group1.addChild(clearNumber);

            group2.addChild(number4);
            group2.addChild(number5);
            group2.addChild(number6);
            group2.addChild(number0);

            group3.addChild(number7);
            group3.addChild(number8);
            group3.addChild(number9);
            group3.addChild(callLift);

            parentLayout.addChild(textView);
            parentLayout.addChild(group1);
            parentLayout.addChild(group2);
            parentLayout.addChild(group3);
        }


        parentLayout.render();

    }
}
