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
import top.xfunny.mod.block.MitsubishiNexWayScreen1Even;
import top.xfunny.mod.block.base.LiftButtonsBase;
import top.xfunny.mod.client.view.*;
import top.xfunny.mod.client.view.view_group.FrameLayout;
import top.xfunny.mod.client.view.view_group.LinearLayout;
import top.xfunny.mod.item.YteGroupLiftButtonsLinker;
import top.xfunny.mod.item.YteLiftButtonsLinker;
import top.xfunny.mod.util.ClientGetLiftDetails;

import java.util.Comparator;

import static org.mtr.core.data.LiftDirection.NONE;

public class RenderMitsubishiNexWayLantern1Horizontal<T extends LiftButtonsBase.BlockEntityBase> extends BlockEntityRenderer<T> implements DirectionHelper, IGui, IBlock {
    private static final int PRESSED_COLOR = 0xFFFEE1A9;
    private static final int DEFAULT_COLOR = 0xFFFFFFFF;
    private static final Identifier BUTTON_TEXTURE = new Identifier(Init.MOD_ID, "textures/block/mitsubishi_lantern_light_1.png");
    private static final Identifier ARROW_TEXTURE = new Identifier(Init.MOD_ID, "textures/block/mitsubishi_lantern_light_1_arrow.png");
    private final boolean isOdd;

    public RenderMitsubishiNexWayLantern1Horizontal(Argument dispatcher, Boolean isOdd) {
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
        parentLayout.setParentDimensions(15.5F / 16, 4.5F / 16);
        parentLayout.setPosition(isOdd ? -7.75F / 16 : -15.75F / 16, 9F / 16);
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
        lanternArrowUpLeft.setDimension(2.75F / 16);
        lanternArrowUpLeft.setGravity(Gravity.CENTER);
        lanternArrowUpLeft.setLight(light);

        final ImageView lanternArrowUpRight = new ImageView();
        lanternArrowUpRight.setBasicsAttributes(world, blockPos);
        lanternArrowUpRight.setTexture(ARROW_TEXTURE);
        lanternArrowUpRight.setDimension(2.75F / 16);
        lanternArrowUpRight.setGravity(Gravity.CENTER);
        lanternArrowUpRight.setLight(light);

        final ImageView lanternArrowDownLeft = new ImageView();
        lanternArrowDownLeft.setBasicsAttributes(world, blockPos);
        lanternArrowDownLeft.setTexture(ARROW_TEXTURE);
        lanternArrowDownLeft.setDimension(2.75F / 16);
        lanternArrowDownLeft.setGravity(Gravity.CENTER);
        lanternArrowDownLeft.setLight(light);
        lanternArrowDownLeft.setFlip(false, true);

        final ImageView lanternArrowDownRight = new ImageView();
        lanternArrowDownRight.setBasicsAttributes(world, blockPos);
        lanternArrowDownRight.setTexture(ARROW_TEXTURE);
        lanternArrowDownRight.setDimension(2.75F / 16);
        lanternArrowDownRight.setGravity(Gravity.CENTER);
        lanternArrowDownRight.setLight(light);
        lanternArrowDownRight.setFlip(false, true);

        final ButtonView downLanternLeft = new ButtonView();
        downLanternLeft.setBasicsAttributes(world, blockPos);
        downLanternLeft.setTexture(BUTTON_TEXTURE);
        downLanternLeft.setDimension(2.75F / 16);
        downLanternLeft.setGravity(Gravity.CENTER);
        downLanternLeft.setLight(light);
        downLanternLeft.setDefaultColor(DEFAULT_COLOR);
        downLanternLeft.setPressedColor(PRESSED_COLOR);

        final ButtonView downLanternRight = new ButtonView();
        downLanternRight.setBasicsAttributes(world, blockPos);
        downLanternRight.setTexture(BUTTON_TEXTURE);
        downLanternRight.setDimension(2.75F / 16);
        downLanternRight.setGravity(Gravity.CENTER);
        downLanternRight.setLight(light);
        downLanternRight.setDefaultColor(DEFAULT_COLOR);
        downLanternRight.setPressedColor(PRESSED_COLOR);

        final ButtonView upLanternLeft = new ButtonView();
        upLanternLeft.setBasicsAttributes(world, blockPos);
        upLanternLeft.setTexture(BUTTON_TEXTURE);
        upLanternLeft.setDimension(2.75F / 16);
        upLanternLeft.setGravity(Gravity.CENTER);
        upLanternLeft.setLight(light);
        upLanternLeft.setDefaultColor(DEFAULT_COLOR);
        upLanternLeft.setPressedColor(PRESSED_COLOR);

        final ButtonView upLanternRight = new ButtonView();
        upLanternRight.setBasicsAttributes(world, blockPos);
        upLanternRight.setTexture(BUTTON_TEXTURE);
        upLanternRight.setDimension(2.75F / 16);
        upLanternRight.setGravity(Gravity.CENTER);
        upLanternRight.setLight(light);
        upLanternRight.setDefaultColor(DEFAULT_COLOR);
        upLanternRight.setPressedColor(PRESSED_COLOR);

        final LineComponent line = new LineComponent();
        line.setBasicsAttributes(world, blockPos);

        final LineComponent buttonLine = new LineComponent();
        buttonLine.setBasicsAttributes(world, blockPos);

        final ObjectArrayList<ObjectObjectImmutablePair<BlockPos, Lift>> sortedPositionsAndLifts = new ObjectArrayList<>();

        blockEntity.forEachTrackPosition(trackPosition -> {
            line.RenderLine(holdingLinker, trackPosition);

            MitsubishiNexWayScreen1Even.hasButtonsClient(trackPosition, buttonDescriptor, (floorIndex, lift) -> {
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

        blockEntity.forEachLiftButtonPosition(buttonPosition -> {
            buttonLine.RenderLine(holdingLinker, buttonPosition, true);
        });

        if (buttonDescriptor.hasDownButton() && buttonDescriptor.hasUpButton()) {
            lanternGroupLeft.setMargin(0, 0, 0.5F / 16, 0);
            lanternGroupLeft.addChild(downLanternLeft);
            lanternGroupLeft.addChild(lanternArrowDownLeft);
            lanternGroupRight.addChild(upLanternRight);
            lanternGroupRight.addChild(lanternArrowUpRight);
            linearLayout.addChild(lanternGroupLeft);
            linearLayout.addChild(lanternGroupRight);

        } else if (buttonDescriptor.hasDownButton()) {
            lanternGroupLeft.addChild(downLanternLeft);
            lanternGroupLeft.addChild(lanternArrowDownLeft);
            linearLayout.addChild(lanternGroupLeft);

        } else if (buttonDescriptor.hasUpButton()) {
            lanternGroupRight.addChild(upLanternRight);
            lanternGroupRight.addChild(lanternArrowUpRight);
            linearLayout.addChild(lanternGroupRight);
        }

        parentLayout.addChild(linearLayout);
        parentLayout.render();
    }
}
