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
import top.xfunny.mod.block.CESScreen1Even;
import top.xfunny.mod.block.base.LiftPanelBase;
import top.xfunny.mod.client.resource.FontList;
import top.xfunny.mod.client.view.*;
import top.xfunny.mod.client.view.view_group.LinearLayout;
import top.xfunny.mod.item.YteGroupLiftButtonsLinker;
import top.xfunny.mod.item.YteLiftButtonsLinker;

import java.util.Comparator;

public class RenderCESScreen1<T extends LiftPanelBase.BlockEntityBase> extends BlockEntityRenderer<T> implements DirectionHelper, IGui, IBlock {

    private final boolean isOdd;

    public RenderCESScreen1(Argument dispatcher, Boolean isOdd) {
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

        final boolean holdingLinker = PlayerHelper.isHolding(
                PlayerEntity.cast(clientPlayerEntity),
                item -> item.data instanceof YteLiftButtonsLinker || item.data instanceof YteGroupLiftButtonsLinker
        );

        final BlockPos blockPos = blockEntity.getPos2();
        final BlockState blockState = world.getBlockState(blockPos);
        final Direction facing = IBlock.getStatePropertySafe(blockState, FACING);

        final StoredMatrixTransformations storedMatrixTransformations =
                new StoredMatrixTransformations(0.5, 0, 0.5);
        StoredMatrixTransformations storedMatrixTransformations1 = storedMatrixTransformations.copy();
        storedMatrixTransformations1.add(graphicsHolder -> {
            graphicsHolder.rotateYDegrees(-facing.asRotation());
            graphicsHolder.translate(0, 0, 7.91F / 16 - SMALL_OFFSET);
        });

        final LinearLayout parentLayout = new LinearLayout(false);
        parentLayout.setBasicsAttributes(world, blockPos);
        parentLayout.setStoredMatrixTransformations(storedMatrixTransformations1);
        parentLayout.setParentDimensions(7.5F / 16, 5F / 16);
        parentLayout.setPosition(isOdd ? -0.284375F : -0.784375F, 0.5725F);
        parentLayout.setWidth(LayoutSize.MATCH_PARENT);
        parentLayout.setHeight(LayoutSize.MATCH_PARENT);

        final LineComponent line = new LineComponent();
        line.setBasicsAttributes(world, blockPos);

        final ObjectArrayList<ObjectObjectImmutablePair<BlockPos, Lift>> sortedPositionsAndLifts = new ObjectArrayList<>();

        blockEntity.forEachTrackPosition(trackPosition -> {
            line.RenderLine(holdingLinker, trackPosition);
            CESScreen1Even.LiftCheck(trackPosition, (floorIndex, lift) -> {
                sortedPositionsAndLifts.add(new ObjectObjectImmutablePair<>(trackPosition, lift));
            });
        });

        sortedPositionsAndLifts.sort(
                Comparator.comparingInt(sorted ->
                        blockPos.getManhattanDistance(new Vector3i(sorted.left().data)))
        );

        if (!sortedPositionsAndLifts.isEmpty()) {
            final int count = 1;

            for (int i = 0; i < count; i++) {
                final Lift lift = sortedPositionsAndLifts.get(i).right();
                final LiftDirection direction = lift.getDirection();

                final int upColor = 0xFF00FF00;     // 上行绿色
                final int downColor = 0xFFFF0000;   // 下行红色
                final int idleColor = 0xFFAAAAAA;   // 空闲灰色（可改）

                final LiftFloorDisplayView liftFloorDisplayView = new LiftFloorDisplayView();
                liftFloorDisplayView.setBasicsAttributes(
                        world,
                        blockPos,
                        lift,
                        FontList.instance.getFont("ces-14x7"),
                        8,
                        0xFFFF8800
                );
                liftFloorDisplayView.setTextureId(String.format(
                        "ces_screen_1_display_%d",
                        i
                ));
                liftFloorDisplayView.setWidth(2.6F / 16);
                liftFloorDisplayView.setHeight(2.8F / 16);
                liftFloorDisplayView.setGravity(Gravity.CENTER_VERTICAL);
                liftFloorDisplayView.setTextAlign(TextView.HorizontalTextAlign.CENTER);
                liftFloorDisplayView.setLetterSpacing(15);
                liftFloorDisplayView.setDisplayLength(5, 0.01F);
                liftFloorDisplayView.setMargin(1.7F / 16, 0, 0, 0);
                liftFloorDisplayView.addStoredMatrixTransformations(
                        graphicsHolder -> graphicsHolder.translate(0, 0, -SMALL_OFFSET)
                );

                // ======================
                // 右箭头
                // ======================
                final LiftArrowView liftArrowView_right = new LiftArrowView();
                liftArrowView_right.setBasicsAttributes(world, blockPos, lift, LiftArrowView.ArrowType.AUTO);
                liftArrowView_right.setTexture(new Identifier(Init.MOD_ID, "textures/block/ces_arrow_1.png"));
                liftArrowView_right.setAnimationScrolling(true, 0.05F);
                liftArrowView_right.setDimension(0.97F / 16, 434, 999);
                liftArrowView_right.setMargin(1.55F / 16, 3F / 16, 0, 0);
                liftArrowView_right.setGravity(Gravity.CENTER_VERTICAL);
                liftArrowView_right.setQueuedRenderLayer(QueuedRenderLayer.LIGHT_TRANSLUCENT);

                if (direction == LiftDirection.UP) {
                    liftArrowView_right.setColor(upColor);
                } else if (direction == LiftDirection.DOWN) {
                    liftArrowView_right.setColor(downColor);
                } else {
                    liftArrowView_right.setColor(idleColor);
                }

                // ======================
                // 左箭头
                // ======================
                final LiftArrowView liftArrowView_left = new LiftArrowView();
                liftArrowView_left.setBasicsAttributes(world, blockPos, lift, LiftArrowView.ArrowType.AUTO);
                liftArrowView_left.setTexture(new Identifier(Init.MOD_ID, "textures/block/ces_arrow_1.png"));
                liftArrowView_left.setAnimationScrolling(true, 0.05F);
                liftArrowView_left.setDimension(0.97F / 16, 434, 999);
                liftArrowView_left.setMargin(-7.75F / 16, 3F / 16, 0, 0);
                liftArrowView_left.setGravity(Gravity.CENTER_VERTICAL);
                liftArrowView_left.setQueuedRenderLayer(QueuedRenderLayer.LIGHT_TRANSLUCENT);

                if (direction == LiftDirection.UP) {
                    liftArrowView_left.setColor(upColor);
                } else if (direction == LiftDirection.DOWN) {
                    liftArrowView_left.setColor(downColor);
                } else {
                    liftArrowView_left.setColor(idleColor);
                }

                parentLayout.addChild(liftFloorDisplayView);
                parentLayout.addChild(liftArrowView_right);
                parentLayout.addChild(liftArrowView_left);
            }
        }

        parentLayout.render();
    }
}
