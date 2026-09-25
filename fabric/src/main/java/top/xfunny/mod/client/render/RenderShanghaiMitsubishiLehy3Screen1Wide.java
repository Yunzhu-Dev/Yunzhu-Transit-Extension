package top.xfunny.mod.client.render;


import org.mtr.core.data.Lift;
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
import top.xfunny.mod.block.ShanghaiMitsubishiLehy3Screen1WideEven;
import top.xfunny.mod.block.base.LiftPanelBase;
import top.xfunny.mod.client.resource.FontList;
import top.xfunny.mod.client.view.*;
import top.xfunny.mod.client.view.view_group.LinearLayout;
import top.xfunny.mod.item.YteGroupLiftButtonsLinker;
import top.xfunny.mod.item.YteLiftButtonsLinker;

import java.util.Comparator;

public class RenderShanghaiMitsubishiLehy3Screen1Wide<T extends LiftPanelBase.BlockEntityBase> extends BlockEntityRenderer<T> implements DirectionHelper, IGui, IBlock {
    private final boolean isOdd;

    public RenderShanghaiMitsubishiLehy3Screen1Wide(Argument dispatcher, Boolean isOdd) {
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
            graphicsHolder.translate(0, 0, 7.9F / 16 - SMALL_OFFSET);
        });

        final LinearLayout parentLayout = new LinearLayout(true);
        parentLayout.setBasicsAttributes(world, blockPos);
        parentLayout.setStoredMatrixTransformations(storedMatrixTransformations1);
        parentLayout.setParentDimensions(5.25F / 16, 2.75F / 16);
        parentLayout.setPosition(isOdd ? -1.875F / 16 : -9.875F / 16, 10.5F / 16);
        parentLayout.setWidth(LayoutSize.MATCH_PARENT);
        parentLayout.setHeight(LayoutSize.MATCH_PARENT);

        final ImageView media = new ImageView();
        media.setBasicsAttributes(world, blockEntity.getPos2());
        media.setTexture(new Identifier(Init.MOD_ID, "textures/block/media_1.png"));
        media.setDimension(3.23F / 16, 400, 300);
        media.setMargin(0, 0, 0.2F / 16, 0);
        media.setGravity(Gravity.START);

        final LinearLayout numberLayout = new LinearLayout(false);
        numberLayout.setBasicsAttributes(world, blockPos);
        numberLayout.setWidth(LayoutSize.WRAP_CONTENT);
        numberLayout.setHeight(LayoutSize.WRAP_CONTENT);
        numberLayout.setMargin(-1.32F / 16, 0.8F / 16, 0, 0);


        final LineComponent line = new LineComponent();
        line.setBasicsAttributes(world, blockPos);

        final ObjectArrayList<ObjectObjectImmutablePair<BlockPos, Lift>> sortedPositionsAndLifts = new ObjectArrayList<>();

        blockEntity.forEachTrackPosition(trackPosition -> {
            line.RenderLine(holdingLinker, trackPosition);
            ShanghaiMitsubishiLehy3Screen1WideEven.LiftCheck(trackPosition, (floorIndex, lift) -> sortedPositionsAndLifts.add(new ObjectObjectImmutablePair<>(trackPosition, lift)));
        });

        sortedPositionsAndLifts.sort(Comparator.comparingInt(sortedPositionAndLift -> blockPos.getManhattanDistance(new Vector3i(sortedPositionAndLift.left().data))));

        if (!sortedPositionsAndLifts.isEmpty()) {
            final int count = 1;

            for (int i = 0; i < count; i++) {
                final LiftFloorDisplayView liftFloorDisplayView = new LiftFloorDisplayView();
                liftFloorDisplayView.setBasicsAttributes(world,
                        blockPos,
                        sortedPositionsAndLifts.get(i).right(),
                        FontList.instance.getFont("shanghai_mitsubishi_lcd_1"),
                        12,
                        0xFFCEB993);
                liftFloorDisplayView.setTextureId("shanghai_mitsubishi_lehy_3_screen_1_wide_display");
                liftFloorDisplayView.setWidth(1.9F / 16);
                liftFloorDisplayView.setHeight(1.5F / 16);
                liftFloorDisplayView.setGravity(Gravity.CENTER_VERTICAL);
                liftFloorDisplayView.setTextAlign(TextView.HorizontalTextAlign.CENTER);
                liftFloorDisplayView.setLetterSpacing(0);
                liftFloorDisplayView.setMargin(0.57F / 16, 0, 0, 0);
                liftFloorDisplayView.addStoredMatrixTransformations(graphicsHolder -> graphicsHolder.translate(0, 0, -SMALL_OFFSET));

                if (liftFloorDisplayView.getTextLength() >= 3) {
                    liftFloorDisplayView.setMargin(0.32F / 16, 0.3F / 16, 0, 0);
                    liftFloorDisplayView.setAdaptMode(LiftFloorDisplayView.AdaptMode.FORCE_FIT_WIDTH);
                } else {
                    liftFloorDisplayView.setAdaptMode(LiftFloorDisplayView.AdaptMode.ASPECT_FILL);
                }

                final LiftArrowView liftArrowView = new LiftArrowView();
                liftArrowView.setBasicsAttributes(world, blockPos, sortedPositionsAndLifts.get(i).right(), LiftArrowView.ArrowType.AUTO);
                liftArrowView.setTexture(new Identifier(Init.MOD_ID, "textures/block/shanghai_mitsubishi_lehy_3_arrow_2.png"));
                liftArrowView.setDimension(0.875F / 16);
                liftArrowView.setGravity(Gravity.CENTER_VERTICAL);
                liftArrowView.setMargin(1.2F / 16, 0.3F / 16, 0, 0);
                liftArrowView.setQueuedRenderLayer(QueuedRenderLayer.LIGHT_TRANSLUCENT);
                liftArrowView.setColor(0xFFCEB993);
                liftArrowView.setAnimationYawRotation(true, 0.075F);

                numberLayout.addChild(media);
                numberLayout.addChild(liftArrowView);
                numberLayout.addChild(liftFloorDisplayView);
                parentLayout.addChild(numberLayout);
            }
        }
        parentLayout.render();
    }
}