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
import org.mtr.mod.Init;
import org.mtr.mod.block.IBlock;
import org.mtr.mod.data.IGui;
import org.mtr.mod.render.StoredMatrixTransformations;
import top.xfunny.mod.block.ShanghaiMitsubishiLehy3Button4;
import top.xfunny.mod.block.base.LiftButtonsBase;
import top.xfunny.mod.client.client_data.LiftSpeed;
import top.xfunny.mod.client.resource.FontList;
import top.xfunny.mod.client.view.*;
import top.xfunny.mod.client.view.view_group.FrameLayout;
import top.xfunny.mod.client.view.view_group.LinearLayout;
import top.xfunny.mod.item.YteGroupLiftButtonsLinker;
import top.xfunny.mod.item.YteLiftButtonsLinker;
import top.xfunny.mod.keymapping.DefaultButtonsKeyMapping;
import top.xfunny.mod.util.ClientGetLiftDetails;
import top.xfunny.mod.util.ReverseRendering;

import java.util.Comparator;

public class RenderShanghaiMitsubishiLehy3Button4 extends BlockEntityRenderer<ShanghaiMitsubishiLehy3Button4.BlockEntity> implements DirectionHelper, IGui, IBlock {

    private static final int HOVER_COLOR = 0xFFBCA27C;
    private static final int PRESSED_COLOR = 0xFFFAFAFF;
    private static final Identifier BUTTON_TEXTURE = new Identifier(top.xfunny.mod.Init.MOD_ID, "textures/block/shanghai_mitsubishi_a11_button_1.png");
    private static final Identifier BUTTON_LIGHT_TEXTURE = new Identifier(top.xfunny.mod.Init.MOD_ID, "textures/block/shanghai_mitsubishi_a11_button_1_light.png");
    private static final String DIRECTION_FONT_ID = "shanghai_mitsubishi_727arrow"; // 方向箭头字体
    private static final float TEXT_SCALE_X = 0.9F; //横向压缩

    private final LiftSpeed liftSpeed = new LiftSpeed();

    public RenderShanghaiMitsubishiLehy3Button4(Argument dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(ShanghaiMitsubishiLehy3Button4.BlockEntity blockEntity, float tickDelta, GraphicsHolder graphicsHolder1, int light, int overlay) {
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
            graphicsHolder.translate(0, 0, 7.75F / 16 - SMALL_OFFSET);
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
        screenLayout.setMargin(0, 2.8F / 16, 0, 0);


        final FrameLayout buttonLayout = new FrameLayout();
        buttonLayout.setBasicsAttributes(world, blockPos);
        buttonLayout.setWidth(LayoutSize.MATCH_PARENT);
        buttonLayout.setHeight(LayoutSize.MATCH_PARENT);
        buttonLayout.setMargin(0, -0.4F / 16, 0, 0);

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
        buttonUp.setDimension(0.85F / 16);
        buttonUp.setGravity(Gravity.CENTER);
        buttonUp.setLight(light);

        ButtonView buttonUpLight = new ButtonView();
        buttonUpLight.setId("up");
        buttonUpLight.setBasicsAttributes(world, blockPos, keyMapping);
        buttonUpLight.setTexture(BUTTON_LIGHT_TEXTURE);
        buttonUpLight.setDimension(0.85F / 16);
        buttonUpLight.setGravity(Gravity.CENTER);
        buttonUpLight.setLight(light);
        buttonUpLight.setDefaultColor(ARGB_WHITE);
        buttonUpLight.setHoverColor(HOVER_COLOR);
        buttonUpLight.setPressedColor(PRESSED_COLOR);

        ImageView buttonDown = new ImageView();
        buttonDown.setBasicsAttributes(world, blockPos);
        buttonDown.setTexture(BUTTON_TEXTURE);
        buttonDown.setDimension(0.85F / 16);
        buttonDown.setGravity(Gravity.CENTER);
        buttonDown.setLight(light);
        buttonDown.setFlip(false, true);

        ButtonView buttonDownLight = new ButtonView();
        buttonDownLight.setId("down");
        buttonDownLight.setBasicsAttributes(world, blockPos, keyMapping);
        buttonDownLight.setTexture(BUTTON_LIGHT_TEXTURE);
        buttonDownLight.setDimension(0.85F / 16);
        buttonDownLight.setGravity(Gravity.CENTER);
        buttonDownLight.setLight(light);
        buttonDownLight.setDefaultColor(ARGB_WHITE);
        buttonDownLight.setHoverColor(HOVER_COLOR);
        buttonDownLight.setPressedColor(PRESSED_COLOR);
        buttonDownLight.setFlip(false, true);


        final LineComponent line = new LineComponent();
        line.setBasicsAttributes(world, blockPos);


        final ObjectArrayList<ObjectObjectImmutablePair<BlockPos, Lift>> sortedPositionsAndLifts = new ObjectArrayList<>();


        blockEntity.forEachTrackPosition(trackPosition -> {

            line.RenderLine(holdingLinker, trackPosition);


            ShanghaiMitsubishiLehy3Button4.hasButtonsClient(trackPosition, buttonDescriptor, (floorIndex, lift) -> {
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
                final Lift lift = sortedPositionsAndLifts.get(i).right();
                ObjectObjectImmutablePair<LiftDirection, ObjectObjectImmutablePair<String, String>> liftDetails = ClientGetLiftDetails.getLiftDetails(world, lift, Init.positionToBlockPos(lift.getCurrentFloor().getPosition()));
                final String floorNumber = liftDetails.right().left();
                final LiftDirection direction = liftDetails.left();

                final double speed = liftSpeed.getSpeed(lift);
                final boolean hasDirection = direction == LiftDirection.UP || direction == LiftDirection.DOWN;
                final boolean showDirection = hasDirection && Math.abs(speed) < 1 // 交替闪烁的最大速度
                        && ((int) (org.mtr.mod.InitClient.getGameTick() / 30)) % 2 == 1; //闪烁频率，20 tick/s

                final java.awt.Font font = FontList.instance.getFont(
                        floorNumber.equals("1") ? "mitsubishi_modern_1" :
                                (floorNumber.matches("^1.$") ? "mitsubishi_modern_10" : "mitsubishi_modern"));

                final LiftFloorDisplayView liftFloorDisplayView = new SquishedFloorDisplayView(TEXT_SCALE_X);
                liftFloorDisplayView.setBasicsAttributes(world, blockPos, lift, font, 5.8F, 0xFFFA7A24);
                liftFloorDisplayView.setDisplayLength(2, 0);
                liftFloorDisplayView.setTextureId(String.format("shanghai_mitsubishi_lehy_3_button_4_display_%d", i));
                liftFloorDisplayView.setWidth(1.4F / 16);
                liftFloorDisplayView.setHeight(1.7F / 16);
                liftFloorDisplayView.setMargin(0.18F / 16, 0, 0.12F / 16, 0);
                liftFloorDisplayView.setTextAlign(TextView.HorizontalTextAlign.CENTER);

                // 方向字符：向上为"<"，向下为">"，单字符与楼层数字占用同一显示区域
                final java.awt.Font directionFont = FontList.instance.getFont(DIRECTION_FONT_ID);
                final TextView directionView = new SquishedTextView(TEXT_SCALE_X);
                directionView.setBasicsAttributes(world, blockPos, directionFont, 5.8F, 0xFFFA7A24);
                directionView.setDisplayLength(2, 0);
                directionView.setTextureId(String.format("shanghai_mitsubishi_lehy_3_button_4_direction_%d", i));
                directionView.setWidth(1.4F / 16);
                directionView.setHeight(1.7F / 16);
                directionView.setMargin(0.18F / 16, 0, 0.12F / 16, 0);
                directionView.setTextAlign(TextView.HorizontalTextAlign.CENTER);
                directionView.setText(direction == LiftDirection.UP ? "<" : ">");

                final LinearLayout numberLayout = new LinearLayout(true);
                numberLayout.setBasicsAttributes(world, blockPos);
                numberLayout.setWidth(LayoutSize.WRAP_CONTENT);
                numberLayout.setHeight(LayoutSize.WRAP_CONTENT);

                numberLayout.addChild(showDirection ? directionView : liftFloorDisplayView);

                if (reverseRendering) {
                    screenLayout.addChild(numberLayout);
                    screenLayout.reverseChildren();
                } else {
                    screenLayout.addChild(numberLayout);
                }
            }
        }

        upButtonGroup.addChild(buttonUp);
        upButtonGroup.addChild(buttonUpLight);
        downButtonGroup.addChild(buttonDown);
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

    /** 仅本型号使用的横向压扁文本：重写 calculateSize 压缩绘制宽度，不改动公共 TextView。 */
    private static class SquishedTextView extends TextView {
        private final float scaleX;

        private SquishedTextView(float scaleX) {
            this.scaleX = scaleX;
        }

        @Override
        protected void calculateSize() {
            super.calculateSize();
            this.textWidth *= scaleX;
            this.fixedWidth *= scaleX;
        }
    }

    /** 仅本型号使用的横向压扁楼层文本。 */
    private static class SquishedFloorDisplayView extends LiftFloorDisplayView {
        private final float scaleX;

        private SquishedFloorDisplayView(float scaleX) {
            this.scaleX = scaleX;
        }

        @Override
        protected void calculateSize() {
            super.calculateSize();
            this.textWidth *= scaleX;
            this.fixedWidth *= scaleX;
        }
    }
}
