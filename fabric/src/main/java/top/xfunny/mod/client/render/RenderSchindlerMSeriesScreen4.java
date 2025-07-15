package top.xfunny.mod.client.render;


import org.mtr.core.data.Lift;
import org.mtr.core.data.LiftDirection;
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
import top.xfunny.mod.block.SchindlerMSeriesScreen4Even;
import top.xfunny.mod.block.base.LiftButtonsBase;
import top.xfunny.mod.client.resource.FontList;
import top.xfunny.mod.client.view.*;
import top.xfunny.mod.client.view.view_group.FrameLayout;
import top.xfunny.mod.client.view.view_group.LinearLayout;
import top.xfunny.mod.item.YteGroupLiftButtonsLinker;
import top.xfunny.mod.item.YteLiftButtonsLinker;
import top.xfunny.mod.util.ClientGetLiftDetails;

import java.util.Comparator;

public class RenderSchindlerMSeriesScreen4<T extends LiftButtonsBase.BlockEntityBase> extends BlockEntityRenderer<T> implements DirectionHelper, IGui, IBlock {
    private static final Identifier BUTTON_TEXTURE = new Identifier(Init.MOD_ID, "textures/block/schindler_m_series_screen_4_arrow.png");
    private static final int PRESSED_COLOR = 0xFFFF0000;
    private static final int DEFAULT_COLOR = 0xFF4D0000;
    private final boolean isOdd;

    public RenderSchindlerMSeriesScreen4(Argument dispatcher, Boolean isOdd) {
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

        final LinearLayout parentLayout = new LinearLayout(false);
        parentLayout.setBasicsAttributes(world, blockPos);
        parentLayout.setStoredMatrixTransformations(storedMatrixTransformations1);
        parentLayout.setParentDimensions(9F / 16, 2F / 16);
        parentLayout.setPosition(isOdd ? -4.5F / 16 : -12.5F / 16F, 9.5F / 16);
        parentLayout.setWidth(LayoutSize.MATCH_PARENT);
        parentLayout.setHeight(LayoutSize.MATCH_PARENT);

        final FrameLayout screenLayout = new FrameLayout();
        screenLayout.setBasicsAttributes(world, blockEntity.getPos2());
        screenLayout.setWidth(LayoutSize.WRAP_CONTENT);
        screenLayout.setHeight(LayoutSize.WRAP_CONTENT);
        screenLayout.setGravity(Gravity.CENTER_VERTICAL);
        screenLayout.setMargin(0.9F / 16, 0, 0, 0);

        ButtonView upLanternLeft = new ButtonView();
        upLanternLeft.setBasicsAttributes(world, blockEntity.getPos2());
        upLanternLeft.setTexture(BUTTON_TEXTURE);
        upLanternLeft.setDimension(0.625F / 16);
        upLanternLeft.setGravity(Gravity.CENTER_VERTICAL);
        upLanternLeft.setLight(light);
        upLanternLeft.setDefaultColor(DEFAULT_COLOR);
        upLanternLeft.setPressedColor(PRESSED_COLOR);
        upLanternLeft.setMargin(1.25F / 16, 0, 0, 0);

        ButtonView upLanternRight = new ButtonView();
        upLanternRight.setBasicsAttributes(world, blockEntity.getPos2());
        upLanternRight.setTexture(BUTTON_TEXTURE);
        upLanternRight.setDimension(0.625F / 16);
        upLanternRight.setGravity(Gravity.CENTER_VERTICAL);
        upLanternRight.setLight(light);
        upLanternRight.setDefaultColor(DEFAULT_COLOR);
        upLanternRight.setPressedColor(PRESSED_COLOR);
        upLanternRight.setMargin(1.75F / 16, 0, 0, 0);

        ButtonView downLanternLeft = new ButtonView();
        downLanternLeft.setBasicsAttributes(world, blockEntity.getPos2());
        downLanternLeft.setTexture(BUTTON_TEXTURE);
        downLanternLeft.setDimension(0.625F / 16);
        downLanternLeft.setGravity(Gravity.CENTER_VERTICAL);
        downLanternLeft.setLight(light);
        downLanternLeft.setDefaultColor(DEFAULT_COLOR);
        downLanternLeft.setPressedColor(PRESSED_COLOR);
        downLanternLeft.setFlip(false, true);
        downLanternLeft.setMargin(1.25F / 16, 0, 0, 0);

        ButtonView downLanternRight = new ButtonView();
        downLanternRight.setBasicsAttributes(world, blockEntity.getPos2());
        downLanternRight.setTexture(BUTTON_TEXTURE);
        downLanternRight.setDimension(0.625F / 16);
        downLanternRight.setGravity(Gravity.CENTER_VERTICAL);
        downLanternRight.setLight(light);
        downLanternRight.setDefaultColor(DEFAULT_COLOR);
        downLanternRight.setPressedColor(PRESSED_COLOR);
        downLanternRight.setFlip(false, true);
        downLanternRight.setMargin(1.75F / 16, 0, 0, 0);


        final LineComponent line = new LineComponent();
        line.setBasicsAttributes(world, blockPos);

        final ObjectArrayList<ObjectObjectImmutablePair<BlockPos, Lift>> sortedPositionsAndLifts = new ObjectArrayList<>();

        blockEntity.forEachTrackPosition(trackPosition -> {
            line.RenderLine(holdingLinker, trackPosition);
            SchindlerMSeriesScreen4Even.hasButtonsClient(trackPosition, buttonDescriptor, (floorIndex, lift) -> {
                sortedPositionsAndLifts.add(new ObjectObjectImmutablePair<>(trackPosition, lift));
                ObjectObjectImmutablePair<LiftDirection, ObjectObjectImmutablePair<String, String>> liftDetails = ClientGetLiftDetails.getLiftDetails(world, lift, org.mtr.mod.Init.positionToBlockPos(lift.getCurrentFloor().getPosition()));
                final LiftDirection liftDirection = liftDetails.left();
                switch (liftDirection) {
                    case UP:
                        upLanternRight.activate();
                        upLanternLeft.activate();
                        break;
                    case DOWN:
                        downLanternRight.activate();
                        downLanternLeft.activate();
                        break;
                }
            });
        });

        sortedPositionsAndLifts.sort(Comparator.comparingInt(sortedPositionAndLift -> blockPos.getManhattanDistance(new Vector3i(sortedPositionAndLift.left().data))));

        if (!sortedPositionsAndLifts.isEmpty()) {
            final int count = 1;

            for (int i = 0; i < count; i++) {
                final LiftFloorDisplayView liftFloorDisplayView = new LiftFloorDisplayView();
                liftFloorDisplayView.setBasicsAttributes(world,
                        blockPos,
                        sortedPositionsAndLifts.get(i).right(),
                        FontList.instance.getFont("schindler_m_series_segment"),
                        3,
                        0xFFFF0000);
                liftFloorDisplayView.setTextureId("schindler_m_series_screen_4");
                liftFloorDisplayView.setWidth(2.6F / 16);
                liftFloorDisplayView.setHeight(2.8F / 16);
                liftFloorDisplayView.setGravity(Gravity.CENTER_VERTICAL);
                liftFloorDisplayView.setTextAlign(TextView.HorizontalTextAlign.RIGHT);
                liftFloorDisplayView.setLetterSpacing(0);
                liftFloorDisplayView.setDisplayLength(2, 0);
                liftFloorDisplayView.addStoredMatrixTransformations(graphicsHolder -> graphicsHolder.translate(0, 0, -SMALL_OFFSET));
                liftFloorDisplayView.setGravity(Gravity.CENTER);

                screenLayout.addChild(liftFloorDisplayView);
            }
        }

        if (buttonDescriptor.hasDownButton() && buttonDescriptor.hasUpButton()) {
            parentLayout.addChild(downLanternLeft);
            parentLayout.addChild(screenLayout);
            parentLayout.addChild(upLanternRight);
        } else if (buttonDescriptor.hasDownButton()) {
            parentLayout.addChild(downLanternLeft);
            parentLayout.addChild(screenLayout);
            parentLayout.addChild(upLanternRight);
        } else if (buttonDescriptor.hasUpButton()) {
            parentLayout.addChild(downLanternLeft);
            parentLayout.addChild(screenLayout);
            parentLayout.addChild(upLanternRight);
        }

        parentLayout.render();
    }
}