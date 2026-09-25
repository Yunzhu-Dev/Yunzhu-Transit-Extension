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
import top.xfunny.mod.block.KoneKDS220Screen1Even;
import top.xfunny.mod.block.base.LiftPanelBase;
import top.xfunny.mod.client.resource.FontList;
import top.xfunny.mod.client.view.*;
import top.xfunny.mod.client.view.view_group.LinearLayout;
import top.xfunny.mod.item.YteGroupLiftButtonsLinker;
import top.xfunny.mod.item.YteLiftButtonsLinker;
import top.xfunny.mod.util.ClientGetLiftDetails;

import java.util.Comparator;

public class RenderKoneKDS220Screen1<T extends LiftPanelBase.BlockEntityBase> extends BlockEntityRenderer<T> implements DirectionHelper, IGui, IBlock {
    private final boolean isOdd;

    public RenderKoneKDS220Screen1(Argument dispatcher, Boolean isOdd) {
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

        final StoredMatrixTransformations storedMatrixTransformations = new StoredMatrixTransformations(0.5, 0, 0.5);
        StoredMatrixTransformations storedMatrixTransformations1 = storedMatrixTransformations.copy();
        storedMatrixTransformations1.add(graphicsHolder -> {
            graphicsHolder.rotateYDegrees(-facing.asRotation());
            graphicsHolder.translate(0, 0, 7.5F / 16 - SMALL_OFFSET);
        });

        final LinearLayout parentLayout = new LinearLayout(false);
        parentLayout.setBasicsAttributes(world, blockPos);
        parentLayout.setStoredMatrixTransformations(storedMatrixTransformations1);
        parentLayout.setParentDimensions(7.5F / 16, 5F / 16);
        parentLayout.setPosition(isOdd ? -0.284375F : -0.784375F, 0.635F);
        parentLayout.setWidth(LayoutSize.MATCH_PARENT);
        parentLayout.setHeight(LayoutSize.MATCH_PARENT);


        final LineComponent line = new LineComponent();
        line.setBasicsAttributes(world, blockPos);

        final ObjectArrayList<ObjectObjectImmutablePair<BlockPos, Lift>> sortedPositionsAndLifts = new ObjectArrayList<>();

        blockEntity.forEachTrackPosition(trackPosition -> {
            line.RenderLine(holdingLinker, trackPosition);
            KoneKDS220Screen1Even.LiftCheck(trackPosition, (floorIndex, lift) -> sortedPositionsAndLifts.add(new ObjectObjectImmutablePair<>(trackPosition, lift)));
        });

        sortedPositionsAndLifts.sort(Comparator.comparingInt(sortedPositionAndLift -> blockPos.getManhattanDistance(new Vector3i(sortedPositionAndLift.left().data))));

        if (!sortedPositionsAndLifts.isEmpty()) {
            final int count = 1;

            for (int i = 0; i < count; i++) {
                final Lift lift = sortedPositionsAndLifts.get(i).right();
                ObjectObjectImmutablePair<LiftDirection, ObjectObjectImmutablePair<String, String>> liftDetails = ClientGetLiftDetails.getLiftDetails(world, lift, org.mtr.mod.Init.positionToBlockPos(lift.getCurrentFloor().getPosition()));
                String floorNumber = liftDetails.right().left();
                final LiftDirection liftDirection = liftDetails.left();
                final LiftFloorDisplayView liftFloorDisplayView = new LiftFloorDisplayView();
                liftFloorDisplayView.setBasicsAttributes(world,
                        blockPos,
                        sortedPositionsAndLifts.get(i).right(),
                        FontList.instance.getFont("kone-kds220-segment"),
                        5.1F,
                        0xFFFFFFFF);
                liftFloorDisplayView.setTextureId(String.format("kone_kds_220_screen_lcd_segment_display_%d_%s", i, blockEntity.getPos2().asLong()))
                ;
                liftFloorDisplayView.setWidth(2.6F / 16);
                liftFloorDisplayView.setHeight(2.8F / 16);
                liftFloorDisplayView.setTextAlign(TextView.HorizontalTextAlign.RIGHT);
                liftFloorDisplayView.setLetterSpacing(0);
                liftFloorDisplayView.setDisplayLength(2, 0);
                liftFloorDisplayView.setMargin(0.3F / 16, 6F / 16, 0, 0);
                liftFloorDisplayView.addStoredMatrixTransformations(graphicsHolder -> graphicsHolder.translate(0, 0, -SMALL_OFFSET));

                final LiftArrowView liftArrowView_left = new LiftArrowView();
                liftArrowView_left.setBasicsAttributes(world, blockPos, sortedPositionsAndLifts.get(i).right(), LiftArrowView.ArrowType.UP);
                liftArrowView_left.setTexture(new Identifier(Init.MOD_ID, "textures/block/kone_kds330_arrow.png"));
                liftArrowView_left.setAnimationScrolling(false, 0.05F);
                liftArrowView_left.setDimension(1F / 16);
                liftArrowView_left.setMargin(0.05F / 16, 6.75F / 16, 0, 0);
                liftArrowView_left.setQueuedRenderLayer(QueuedRenderLayer.LIGHT_TRANSLUCENT);
                liftArrowView_left.setFlip(false, true);
                liftArrowView_left.setColor(0xFFFFFFFF);

                final LiftArrowView liftArrowView_right = new LiftArrowView();
                liftArrowView_right.setBasicsAttributes(world, blockPos, sortedPositionsAndLifts.get(i).right(), LiftArrowView.ArrowType.DOWN);
                liftArrowView_right.setTexture(new Identifier(Init.MOD_ID, "textures/block/kone_kds330_arrow.png"));
                liftArrowView_right.setAnimationScrolling(false, 0.05F);
                liftArrowView_right.setDimension(1F / 16);
                liftArrowView_right.setMargin(-0.625F / 16, 6.8F / 16, 0, 0);
                liftArrowView_right.setQueuedRenderLayer(QueuedRenderLayer.LIGHT_TRANSLUCENT);
                liftArrowView_right.setFlip(false, true);
                liftArrowView_right.setColor(0xFFFFFFFF);


                parentLayout.addChild(liftFloorDisplayView);
                parentLayout.addChild(liftArrowView_left);
                parentLayout.addChild(liftArrowView_right);

            }
        }
        parentLayout.render();
    }
}