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
import org.mtr.mod.render.QueuedRenderLayer;
import org.mtr.mod.render.StoredMatrixTransformations;
import top.xfunny.mod.Init;
import top.xfunny.mod.block.SchindlerDSeriesScreen2BlueEven;
import top.xfunny.mod.block.SchindlerDSeriesScreen2GreenEven;
import top.xfunny.mod.block.SchindlerDSeriesScreen2RedEven;
import top.xfunny.mod.block.base.LiftPanelBase;
import top.xfunny.mod.client.resource.FontList;
import top.xfunny.mod.client.view.*;
import top.xfunny.mod.client.view.view_group.LinearLayout;
import top.xfunny.mod.item.YteGroupLiftButtonsLinker;
import top.xfunny.mod.item.YteLiftButtonsLinker;
import top.xfunny.mod.util.ClientGetLiftDetails;

import java.util.Comparator;


public class RenderSchindlerDSeriesScreen2<T extends LiftPanelBase.BlockEntityBase> extends BlockEntityRenderer<T> implements DirectionHelper, IGui, IBlock {
    private final boolean isOdd;
    private final renderSchindlerDSeriesScreen2Color color;
    private int fontColor;
    private String textureId;
    private Identifier arrowTexture;


    public RenderSchindlerDSeriesScreen2(Argument dispatcher, Boolean isOdd, renderSchindlerDSeriesScreen2Color color) {
        super(dispatcher);
        this.isOdd = isOdd;
        this.color = color;
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

        switch (color) {
            case GREEN:
                this.textureId = "schindler_d_series_screen_2_green_display";
                this.fontColor = 0xFF00FF00;
                this.arrowTexture = new Identifier(Init.MOD_ID, "textures/block/schindler_d_series_screen_2_arrow_green.png");
                break;

            case BLUE:
                this.textureId = "schindler_d_series_screen_2_blue_display";
                this.fontColor = 0xFF0000FF;
                this.arrowTexture = new Identifier(Init.MOD_ID, "textures/block/schindler_d_series_screen_2_arrow_blue.png");
                break;

            case RED:
                this.textureId = "schindler_d_series_screen_2_red_display";
                this.fontColor = 0xFFFF0000;
                this.arrowTexture = new Identifier(Init.MOD_ID, "textures/block/schindler_d_series_screen_2_arrow_red.png");
                break;
        }

        final boolean holdingLinker = PlayerHelper.isHolding(PlayerEntity.cast(clientPlayerEntity), item -> item.data instanceof YteLiftButtonsLinker || item.data instanceof YteGroupLiftButtonsLinker);
        final BlockPos blockPos = blockEntity.getPos2();
        final BlockState blockState = world.getBlockState(blockPos);
        final Direction facing = IBlock.getStatePropertySafe(blockState, FACING);

        final StoredMatrixTransformations storedMatrixTransformations = new StoredMatrixTransformations(0.5, 0, 0.5);
        StoredMatrixTransformations storedMatrixTransformations1 = storedMatrixTransformations.copy();
        storedMatrixTransformations1.add(graphicsHolder -> {
            graphicsHolder.rotateYDegrees(-facing.asRotation());
            graphicsHolder.translate(0, 0, 7.9F / 16 - SMALL_OFFSET);
        });

        final LinearLayout parentLayout = new LinearLayout(false);
        parentLayout.setBasicsAttributes(world, blockPos);
        parentLayout.setStoredMatrixTransformations(storedMatrixTransformations1);
        parentLayout.setParentDimensions(7.5F / 16, 5F / 16);
        parentLayout.setPosition(isOdd ? -0.234375F : -0.734375F, 0.5625F);
        parentLayout.setWidth(LayoutSize.MATCH_PARENT);
        parentLayout.setHeight(LayoutSize.MATCH_PARENT);


        final LineComponent line = new LineComponent();
        line.setBasicsAttributes(world, blockPos);

        final ObjectArrayList<ObjectObjectImmutablePair<BlockPos, Lift>> sortedPositionsAndLifts = new ObjectArrayList<>();

        blockEntity.forEachTrackPosition(trackPosition -> {
            line.RenderLine(holdingLinker, trackPosition);

            switch (color) {
                case GREEN:
                    SchindlerDSeriesScreen2GreenEven.LiftCheck(trackPosition, (floorIndex, lift) -> sortedPositionsAndLifts.add(new ObjectObjectImmutablePair<>(trackPosition, lift)));
                    break;

                case BLUE:
                    SchindlerDSeriesScreen2BlueEven.LiftCheck(trackPosition, (floorIndex, lift) -> sortedPositionsAndLifts.add(new ObjectObjectImmutablePair<>(trackPosition, lift)));
                    break;

                case RED:
                    SchindlerDSeriesScreen2RedEven.LiftCheck(trackPosition, (floorIndex, lift) -> sortedPositionsAndLifts.add(new ObjectObjectImmutablePair<>(trackPosition, lift)));
                    break;
            }


        });

        sortedPositionsAndLifts.sort(Comparator.comparingInt(sortedPositionAndLift -> blockPos.getManhattanDistance(new Vector3i(sortedPositionAndLift.left().data))));

        if (!sortedPositionsAndLifts.isEmpty()) {
            final int count = 1;


            for (int i = 0; i < count; i++) {
                final Lift lift = sortedPositionsAndLifts.get(i).right();
                ObjectObjectImmutablePair<LiftDirection, ObjectObjectImmutablePair<String, String>> liftDetails =
                        ClientGetLiftDetails.getLiftDetails(world, lift, org.mtr.mod.Init.positionToBlockPos(lift.getCurrentFloor().getPosition()));
                final LiftDirection liftDirection = liftDetails.left();

                final LiftFloorDisplayView liftFloorDisplayView = new LiftFloorDisplayView();
                liftFloorDisplayView.setBasicsAttributes(world,
                        blockPos,
                        sortedPositionsAndLifts.get(i).right(),
                        FontList.instance.getFont("schindler_led"),
                        10,
                        fontColor);
                liftFloorDisplayView.setTextureId(textureId);
                liftFloorDisplayView.setWidth(2.6F / 16);
                liftFloorDisplayView.setHeight(2.8F / 16);
                liftFloorDisplayView.setGravity(Gravity.CENTER_VERTICAL);
                liftFloorDisplayView.setTextAlign(TextView.HorizontalTextAlign.CENTER);
                liftFloorDisplayView.setLetterSpacing(-30);
                liftFloorDisplayView.setDisplayLength(2, 0.005F);
                liftFloorDisplayView.setLetterSpacing(10);
                liftFloorDisplayView.setMargin(liftDirection != LiftDirection.NONE ? 0.5F / 16 : 2.5F / 16, 0, 0, 0);
                liftFloorDisplayView.addStoredMatrixTransformations(graphicsHolder -> graphicsHolder.translate(0, 0, -SMALL_OFFSET));

                final LiftArrowView liftArrowView = new LiftArrowView();
                liftArrowView.setBasicsAttributes(world, blockPos, sortedPositionsAndLifts.get(i).right(), LiftArrowView.ArrowType.AUTO);
                liftArrowView.setTexture(arrowTexture);
                liftArrowView.setAnimationScrolling(true, 0.05F);
                liftArrowView.setDimension(liftDirection != LiftDirection.NONE ? (2.2F / 16) : 0);
                liftArrowView.setMargin(liftDirection != LiftDirection.NONE ? 0.8F / 16 : 0, 3F / 16, 0, 0);
                liftArrowView.setGravity(Gravity.CENTER_VERTICAL);
                liftArrowView.setColor(fontColor);
                liftArrowView.setQueuedRenderLayer(QueuedRenderLayer.LIGHT_TRANSLUCENT);

                parentLayout.addChild(liftArrowView);
                parentLayout.addChild(liftFloorDisplayView);
            }
        }
        parentLayout.render();
    }

    public enum renderSchindlerDSeriesScreen2Color {
        GREEN,
        RED,
        BLUE
    }
}