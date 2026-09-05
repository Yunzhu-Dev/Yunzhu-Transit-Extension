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
import top.xfunny.mod.block.TonicDMScreen1GreenEven;
import top.xfunny.mod.block.TonicDMScreen1RedEven;
import top.xfunny.mod.block.TonicDMScreen1YellowEven;
import top.xfunny.mod.block.base.LiftPanelBase;
import top.xfunny.mod.client.resource.FontList;
import top.xfunny.mod.client.view.*;
import top.xfunny.mod.client.view.view_group.LinearLayout;
import top.xfunny.mod.item.YteGroupLiftButtonsLinker;
import top.xfunny.mod.item.YteLiftButtonsLinker;
import top.xfunny.mod.util.ClientGetLiftDetails;

import java.util.Comparator;


public class RenderTonicDMScreen1<T extends LiftPanelBase.BlockEntityBase> extends BlockEntityRenderer<T> implements DirectionHelper, IGui, IBlock {
    private final boolean isOdd;
    private final renderTonicDMScreen1Color color;
    private int fontColor;
    private String textureId;
    private Identifier arrowTexture;


    public RenderTonicDMScreen1(Argument dispatcher, Boolean isOdd, renderTonicDMScreen1Color color) {
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
                this.textureId = "tonic_dm_screen_1_green_display";
                this.fontColor = 0xFF69B82D;
                this.arrowTexture = new Identifier(Init.MOD_ID, "textures/block/tonic_dm_screen_1_arrow_green.png");
                break;

            case YELLOW:
                this.textureId = "tonic_dm_screen_1_yellow_display";
                this.fontColor = 0xFFFFF000;
                this.arrowTexture = new Identifier(Init.MOD_ID, "textures/block/tonic_dm_screen_1_arrow_yellow.png");
                break;

            case RED:
                this.textureId = "tonic_dm_screen_1_red_display";
                this.fontColor = 0xFFE72518;
                this.arrowTexture = new Identifier(Init.MOD_ID, "textures/block/tonic_dm_screen_1_arrow_red.png");
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
        parentLayout.setPosition(isOdd ? -0.234375F : -0.734375F, 0.5025F);
        parentLayout.setWidth(LayoutSize.MATCH_PARENT);
        parentLayout.setHeight(LayoutSize.MATCH_PARENT);


        final LineComponent line = new LineComponent();
        line.setBasicsAttributes(world, blockPos);

        final ObjectArrayList<ObjectObjectImmutablePair<BlockPos, Lift>> sortedPositionsAndLifts = new ObjectArrayList<>();

        blockEntity.forEachTrackPosition(trackPosition -> {
            line.RenderLine(holdingLinker, trackPosition);

            switch (color) {
                case GREEN:
                    TonicDMScreen1GreenEven.LiftCheck(trackPosition, (floorIndex, lift) -> {
                        sortedPositionsAndLifts.add(new ObjectObjectImmutablePair<>(trackPosition, lift));
                    });
                    break;

                case YELLOW:
                    TonicDMScreen1YellowEven.LiftCheck(trackPosition, (floorIndex, lift) -> {
                        sortedPositionsAndLifts.add(new ObjectObjectImmutablePair<>(trackPosition, lift));
                    });
                    break;

                case RED:
                    TonicDMScreen1RedEven.LiftCheck(trackPosition, (floorIndex, lift) -> {
                        sortedPositionsAndLifts.add(new ObjectObjectImmutablePair<>(trackPosition, lift));
                    });
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
                        FontList.instance.getFont("tonic-led"),
                        4,
                        fontColor);
                liftFloorDisplayView.setTextureId(textureId);
                liftFloorDisplayView.setWidth(1.12F / 16);
                liftFloorDisplayView.setHeight(2.8F / 16);
                liftFloorDisplayView.setGravity(Gravity.CENTER_VERTICAL);
                liftFloorDisplayView.setTextAlign(TextView.HorizontalTextAlign.CENTER);
                liftFloorDisplayView.setLetterSpacing(0);
                liftFloorDisplayView.setMargin(liftDirection != LiftDirection.NONE ? (float) 0.25 / 16 : (float) 3.195 / 16, (float) 0.5 / 16, 0, 0);
                liftFloorDisplayView.addStoredMatrixTransformations(graphicsHolder -> graphicsHolder.translate(0, 0, -SMALL_OFFSET));
                if (liftFloorDisplayView.getTextLength() >= 3) {
                    liftFloorDisplayView.setBasicsAttributes(world,
                            blockPos,
                            sortedPositionsAndLifts.get(i).right(),
                            FontList.instance.getFont("tonic-led-thin"),
                            4,
                            fontColor);
                    liftFloorDisplayView.setAdaptMode(LiftFloorDisplayView.AdaptMode.FORCE_FIT_WIDTH);
                } else {
                    liftFloorDisplayView.setAdaptMode(LiftFloorDisplayView.AdaptMode.ASPECT_FILL);
                }


                final LiftArrowView liftArrowView = new LiftArrowView();
                liftArrowView.setBasicsAttributes(world, blockPos, sortedPositionsAndLifts.get(i).right(), LiftArrowView.ArrowType.AUTO);
                liftArrowView.setTexture(arrowTexture);
                liftArrowView.setAnimationScrolling(true, 0.05F);
                liftArrowView.setDimension(liftDirection != LiftDirection.NONE ? (0.8F / 16) : 0);
                liftArrowView.setMargin(liftDirection != LiftDirection.NONE ? 2.5F / 16 : 0, 3F / 16, 0, 0);
                liftArrowView.setGravity(Gravity.CENTER_VERTICAL);
                liftArrowView.setColor(0xFFFFFFFF);
                liftArrowView.setQueuedRenderLayer(QueuedRenderLayer.LIGHT_TRANSLUCENT);

                parentLayout.addChild(liftArrowView);
                parentLayout.addChild(liftFloorDisplayView);
            }
        }
        parentLayout.render();
    }

    public enum renderTonicDMScreen1Color {
        GREEN,
        RED,
        YELLOW
    }
}