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
import org.mtr.mod.render.RenderLifts;
import org.mtr.mod.render.StoredMatrixTransformations;
import top.xfunny.mod.Init;
import top.xfunny.mod.block.SchindlerMSeriesScreen3Even;
import top.xfunny.mod.block.base.LiftButtonsBase;
import top.xfunny.mod.client.resource.FontList;
import top.xfunny.mod.client.view.*;
import top.xfunny.mod.client.view.view_group.FrameLayout;
import top.xfunny.mod.client.view.view_group.LinearLayout;
import top.xfunny.mod.item.YteGroupLiftButtonsLinker;
import top.xfunny.mod.item.YteLiftButtonsLinker;
import top.xfunny.mod.util.ClientGetLiftDetails;

import java.util.Comparator;

import static org.mtr.core.data.LiftDirection.NONE;

public class RenderSchindlerMSeriesScreen3<T extends LiftButtonsBase.BlockEntityBase> extends BlockEntityRenderer<T> implements DirectionHelper, IGui, IBlock {
    private static final int PRESSED_COLOR = 0xFFFFCC00;
    private static final int DEFAULT_COLOR = 0xFFFFFFFF;
    private static final Identifier ARROW_TEXTURE = new Identifier(Init.MOD_ID, "textures/block/schindler_m_series_panel_arrow_2.png");
    private static final Identifier BUTTON_TEXTURE = new Identifier(Init.MOD_ID, "textures/block/schindler_m_series_lantern_3.png");
    private static final Identifier SCREEN_TEXTURE = new Identifier(Init.MOD_ID, "textures/block/schindler_m_series_screen_1_white.png");
    private final boolean isOdd;

    public RenderSchindlerMSeriesScreen3(Argument dispatcher, Boolean isOdd) {
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

        final StoredMatrixTransformations storedMatrixTransformations = new StoredMatrixTransformations(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5);
        StoredMatrixTransformations storedMatrixTransformations1 = storedMatrixTransformations.copy();
        storedMatrixTransformations1.add(graphicsHolder -> {
            graphicsHolder.rotateYDegrees(-facing.asRotation());
            graphicsHolder.translate(0, 0, 7.9F / 16 - SMALL_OFFSET);
        });

        final FrameLayout parentLayout = new FrameLayout();
        parentLayout.setBasicsAttributes(world, blockPos);
        parentLayout.setStoredMatrixTransformations(storedMatrixTransformations1);
        parentLayout.setParentDimensions(18F / 16, 4.5F / 16);
        parentLayout.setPosition(isOdd ? -0.5575F : -1.055F, 0.61F);
        parentLayout.setWidth(top.xfunny.mod.client.view.LayoutSize.MATCH_PARENT);
        parentLayout.setHeight(top.xfunny.mod.client.view.LayoutSize.MATCH_PARENT);

        final LinearLayout linearLayout = new LinearLayout(false);
        linearLayout.setBasicsAttributes(world, blockPos);
        linearLayout.setHeight(LayoutSize.WRAP_CONTENT);
        linearLayout.setWidth(LayoutSize.WRAP_CONTENT);
        linearLayout.setGravity(Gravity.CENTER);

        final FrameLayout screenLayout = new FrameLayout();
        screenLayout.setBasicsAttributes(world, blockPos);
        screenLayout.setWidth(LayoutSize.WRAP_CONTENT);
        screenLayout.setHeight(LayoutSize.WRAP_CONTENT);
        screenLayout.setGravity(Gravity.CENTER_VERTICAL);
        screenLayout.setMargin(0, 0, 1.5F / 16, 0);

        final FrameLayout LanternGroupLeft = new FrameLayout();
        LanternGroupLeft.setBasicsAttributes(world, blockPos);
        LanternGroupLeft.setWidth(LayoutSize.WRAP_CONTENT);
        LanternGroupLeft.setHeight(LayoutSize.WRAP_CONTENT);
        LanternGroupLeft.setGravity(Gravity.CENTER_VERTICAL);
        LanternGroupLeft.setMargin(0, 0, 1.5F / 16, 0);

        final FrameLayout LanternGroupRight = new FrameLayout();
        LanternGroupRight.setBasicsAttributes(world, blockPos);
        LanternGroupRight.setWidth(LayoutSize.WRAP_CONTENT);
        LanternGroupRight.setHeight(LayoutSize.WRAP_CONTENT);
        LanternGroupRight.setGravity(Gravity.CENTER_VERTICAL);

        final ImageView lanternBackgroundLeft = new ImageView();
        lanternBackgroundLeft.setBasicsAttributes(world, blockPos);
        lanternBackgroundLeft.setTexture(BUTTON_TEXTURE);
        lanternBackgroundLeft.setDimension(3.25F / 16);
        lanternBackgroundLeft.setGravity(Gravity.CENTER);
        lanternBackgroundLeft.setLight(light);

        final ImageView lanternBackgroundRight = new ImageView();
        lanternBackgroundRight.setBasicsAttributes(world, blockPos);
        lanternBackgroundRight.setTexture(BUTTON_TEXTURE);
        lanternBackgroundRight.setDimension(3.25F / 16);
        lanternBackgroundRight.setGravity(Gravity.CENTER);
        lanternBackgroundRight.setLight(light);

        final ImageView background = new ImageView();
        background.setBasicsAttributes(world, blockPos);
        background.setTexture(SCREEN_TEXTURE);
        background.setDimension(3.25F / 16);
        background.setLight(light);
        background.setGravity(Gravity.CENTER);

        ButtonView upLanternLeft = new ButtonView();
        upLanternLeft.setBasicsAttributes(world, blockEntity.getPos2());
        upLanternLeft.setTexture(ARROW_TEXTURE);
        upLanternLeft.setDimension(3.25F / 16);
        upLanternLeft.setGravity(Gravity.CENTER);
        upLanternLeft.setLight(light);
        upLanternLeft.setDefaultColor(DEFAULT_COLOR);
        upLanternLeft.setPressedColor(PRESSED_COLOR);

        ButtonView upLanternRight = new ButtonView();
        upLanternRight.setBasicsAttributes(world, blockEntity.getPos2());
        upLanternRight.setTexture(ARROW_TEXTURE);
        upLanternRight.setDimension(3.25F / 16);
        upLanternRight.setGravity(Gravity.CENTER);
        upLanternRight.setLight(light);
        upLanternRight.setDefaultColor(DEFAULT_COLOR);
        upLanternRight.setPressedColor(PRESSED_COLOR);

        ButtonView downLanternLeft = new ButtonView();
        downLanternLeft.setBasicsAttributes(world, blockEntity.getPos2());
        downLanternLeft.setTexture(ARROW_TEXTURE);
        downLanternLeft.setDimension(3.25F / 16);
        downLanternLeft.setGravity(Gravity.CENTER);
        downLanternLeft.setLight(light);
        downLanternLeft.setDefaultColor(DEFAULT_COLOR);
        downLanternLeft.setPressedColor(PRESSED_COLOR);
        downLanternLeft.setFlip(false, true);

        ButtonView downLanternRight = new ButtonView();
        downLanternRight.setBasicsAttributes(world, blockEntity.getPos2());
        downLanternRight.setTexture(ARROW_TEXTURE);
        downLanternRight.setDimension(3.25F / 16);
        downLanternRight.setGravity(Gravity.CENTER);
        downLanternRight.setLight(light);
        downLanternRight.setDefaultColor(DEFAULT_COLOR);
        downLanternRight.setPressedColor(PRESSED_COLOR);
        downLanternRight.setFlip(false, true);


        final LineComponent line = new LineComponent();
        line.setBasicsAttributes(world, blockPos);

        final LineComponent buttonLine = new LineComponent();
        buttonLine.setBasicsAttributes(world, blockPos);

        final ObjectArrayList<ObjectObjectImmutablePair<BlockPos, Lift>> sortedPositionsAndLifts = new ObjectArrayList<>();

        blockEntity.forEachTrackPosition(trackPosition -> {
            line.RenderLine(holdingLinker, trackPosition);

            SchindlerMSeriesScreen3Even.hasButtonsClient(trackPosition, buttonDescriptor, (floorIndex, lift) -> {
                sortedPositionsAndLifts.add(new ObjectObjectImmutablePair<>(trackPosition, lift));

                LiftDirection pressedButtonDirection = blockEntity.getPressedButtonDirection();

                ObjectObjectImmutablePair<LiftDirection, ObjectObjectImmutablePair<String, String>> liftDetails = ClientGetLiftDetails.getLiftDetails(world, lift, org.mtr.mod.Init.positionToBlockPos(lift.getCurrentFloor().getPosition()));
                String floorNumber = liftDetails.right().left();
                String currentFloorNumber = RenderLifts.getLiftDetails(world, lift, trackPosition).right().left();

                final ObjectArraySet<LiftDirection> instructionDirections = lift.hasInstruction(floorIndex);

                if (instructionDirections.isEmpty() && pressedButtonDirection != null && lift.getDoorValue() != 0 && floorNumber.equals(currentFloorNumber)) {
                    switch (pressedButtonDirection) {
                        case DOWN:
                            downLanternLeft.activate();
                            downLanternRight.activate();
                            break;
                        case UP:
                            upLanternLeft.activate();
                            upLanternRight.activate();
                            break;
                    }
                }

                instructionDirections.forEach(liftDirection -> {
                    if (lift.getDoorValue() != 0 && floorNumber.equals(currentFloorNumber)) {
                        if (liftDirection == NONE) {
                            if (pressedButtonDirection != null) {
                                switch (pressedButtonDirection) {
                                    case DOWN:
                                        downLanternLeft.activate();
                                        downLanternRight.activate();
                                        break;
                                    case UP:
                                        upLanternLeft.activate();
                                        upLanternRight.activate();
                                        break;
                                }
                            }
                        } else {
                            switch (liftDirection) {
                                case DOWN:
                                    downLanternLeft.activate();
                                    downLanternRight.activate();
                                    break;
                                case UP:
                                    upLanternLeft.activate();
                                    upLanternRight.activate();
                                    break;
                            }
                        }
                    }

                });
            });
        });

        sortedPositionsAndLifts.sort(Comparator.comparingInt(sortedPositionAndLift -> blockPos.getManhattanDistance(new Vector3i(sortedPositionAndLift.left().data))));

        if (!sortedPositionsAndLifts.isEmpty()) {

            final int count = 1;

            for (int i = 0; i < count; i++) {
                final top.xfunny.mod.client.view.LiftFloorDisplayView liftFloorDisplayView = new top.xfunny.mod.client.view.LiftFloorDisplayView();
                liftFloorDisplayView.setBasicsAttributes(world,
                        blockPos,
                        sortedPositionsAndLifts.get(i).right(),
                        FontList.instance.getFont("acmeled"),//字体
                        5,//字号
                        0xFFFF0000);//字体颜色
                liftFloorDisplayView.setDisplayLength(2, 0);//true开启滚动，开启滚动时的字数条件(>)，滚动速度
                liftFloorDisplayView.setTextureId("schindler_m_series_screen_3_display");//字体贴图id，不能与其他显示屏的重复
                liftFloorDisplayView.setLetterSpacing(10);
                liftFloorDisplayView.setWidth(2F / 16);//显示屏宽度
                liftFloorDisplayView.setHeight(2F / 16);//显示屏高度
                liftFloorDisplayView.setTextAlign(TextView.HorizontalTextAlign.RIGHT);//文字对齐方式，center为居中对齐，left为左对齐，right为右对齐
                liftFloorDisplayView.setGravity(Gravity.CENTER);

                screenLayout.addChild(background);
                screenLayout.addChild(liftFloorDisplayView);
            }
        }

        blockEntity.forEachLiftButtonPosition(buttonPosition -> {
            buttonLine.RenderLine(holdingLinker, buttonPosition, true);
        });

        if (buttonDescriptor.hasDownButton() && buttonDescriptor.hasUpButton()) {
            LanternGroupLeft.addChild(lanternBackgroundLeft);
            LanternGroupLeft.addChild(downLanternLeft);
            LanternGroupRight.addChild(lanternBackgroundRight);
            LanternGroupRight.addChild(upLanternRight);
            linearLayout.addChild(LanternGroupLeft);
            linearLayout.addChild(screenLayout);
            linearLayout.addChild(LanternGroupRight);
        } else if (buttonDescriptor.hasDownButton()) {
            LanternGroupLeft.addChild(lanternBackgroundLeft);
            LanternGroupLeft.addChild(downLanternLeft);
            LanternGroupRight.addChild(lanternBackgroundRight);
            LanternGroupRight.addChild(downLanternRight);
            linearLayout.addChild(LanternGroupLeft);
            linearLayout.addChild(screenLayout);
            linearLayout.addChild(LanternGroupRight);
        } else if (buttonDescriptor.hasUpButton()) {
            LanternGroupLeft.addChild(lanternBackgroundLeft);
            LanternGroupLeft.addChild(upLanternLeft);
            LanternGroupRight.addChild(lanternBackgroundRight);
            LanternGroupRight.addChild(upLanternRight);
            linearLayout.addChild(LanternGroupLeft);
            linearLayout.addChild(screenLayout);
            linearLayout.addChild(LanternGroupRight);
        }

        parentLayout.addChild(linearLayout);
        parentLayout.render();
    }
}
