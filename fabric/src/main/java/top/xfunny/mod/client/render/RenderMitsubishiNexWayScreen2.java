package top.xfunny.mod.client.render;


import org.mtr.core.data.Lift;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectObjectImmutablePair;
import org.mtr.mapping.holder.*;
import org.mtr.mapping.mapper.*;
import org.mtr.mod.block.IBlock;
import org.mtr.mod.data.IGui;
import org.mtr.mod.render.QueuedRenderLayer;
import org.mtr.mod.render.StoredMatrixTransformations;
import top.xfunny.mod.block.MitsubishiNexWayScreen2Even;
import top.xfunny.mod.block.base.LiftPanelBase;
import top.xfunny.mod.client.resource.FontList;
import top.xfunny.mod.client.view.*;
import top.xfunny.mod.client.view.view_group.LinearLayout;
import top.xfunny.mod.item.YteGroupLiftButtonsLinker;
import top.xfunny.mod.item.YteLiftButtonsLinker;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

public class RenderMitsubishiNexWayScreen2<T extends LiftPanelBase.BlockEntityBase> extends BlockEntityRenderer<T> implements DirectionHelper, IGui, IBlock {
    private final boolean isOdd;

    public RenderMitsubishiNexWayScreen2(Argument dispatcher, Boolean isOdd) {
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
        parentLayout.setParentDimensions(3.75F / 16, 2.75F / 16);
        parentLayout.setPosition(isOdd ? -1.875F / 16 : -9.875F / 16, 9.75F / 16);
        parentLayout.setWidth(LayoutSize.MATCH_PARENT);
        parentLayout.setHeight(LayoutSize.MATCH_PARENT);

        final LinearLayout numberLayout = new LinearLayout(false);
        numberLayout.setBasicsAttributes(world, blockPos);
        numberLayout.setWidth(LayoutSize.WRAP_CONTENT);
        numberLayout.setHeight(LayoutSize.WRAP_CONTENT);
        numberLayout.setMargin(0.16F / 16, 0.35F / 16, 0, 0);


        final LineComponent line = new LineComponent();
        line.setBasicsAttributes(world, blockPos);

        final ObjectArrayList<ObjectObjectImmutablePair<BlockPos, Lift>> sortedPositionsAndLifts = new ObjectArrayList<>();

        blockEntity.forEachTrackPosition(trackPosition -> {
            line.RenderLine(holdingLinker, trackPosition);
            MitsubishiNexWayScreen2Even.LiftCheck(trackPosition, (floorIndex, lift) -> sortedPositionsAndLifts.add(new ObjectObjectImmutablePair<>(trackPosition, lift)));
        });

        sortedPositionsAndLifts.sort(Comparator.comparingInt(sortedPositionAndLift -> blockPos.getManhattanDistance(new Vector3i(sortedPositionAndLift.left().data))));

        if (!sortedPositionsAndLifts.isEmpty()) {
            final int count = 1;

            for (int i = 0; i < count; i++) {
                final LiftFloorDisplayView liftFloorDisplayView = new LiftFloorDisplayView();
                liftFloorDisplayView.setBasicsAttributes(world,
                        blockPos,
                        sortedPositionsAndLifts.get(i).right(),
                        FontList.instance.getFont("gill_sans_mt"),
                        16,
                        0xFFFFFFFF);
                liftFloorDisplayView.setTextureId(String.format("mitsubishi_nexway_screen_2_display_%d", i))
                ;
                liftFloorDisplayView.setWidth(1.9F / 16);
                liftFloorDisplayView.setHeight(1.5F / 16);
                liftFloorDisplayView.setGravity(Gravity.CENTER_VERTICAL);
                liftFloorDisplayView.setTextAlign(TextView.HorizontalTextAlign.CENTER);
                liftFloorDisplayView.setLetterSpacing(0);
                liftFloorDisplayView.setMargin(0.02F / 16, 0, 0, 0);
                liftFloorDisplayView.addStoredMatrixTransformations(graphicsHolder -> graphicsHolder.translate(0, 0, -SMALL_OFFSET));
                if (liftFloorDisplayView.getTextLength() >= 3) {
                    liftFloorDisplayView.setMargin(0F / 16, 0, 0, 0);
                    liftFloorDisplayView.setAdaptMode(LiftFloorDisplayView.AdaptMode.FORCE_FIT_WIDTH);
                } else {
                    liftFloorDisplayView.setAdaptMode(LiftFloorDisplayView.AdaptMode.ASPECT_FILL);
                }

                final LiftArrowView liftArrowView = new LiftArrowView();
                liftArrowView.setBasicsAttributes(world, blockPos, sortedPositionsAndLifts.get(i).right(), LiftArrowView.ArrowType.AUTO);
                liftArrowView.setAnimatedTexture("yte", "textures/block/mitsubishi_nexway_2_lcd_arrow_1/mitsubishi_nexway_2_lcd_arrow_1_", 6, 3.3f);
                liftArrowView.setDimension(1.5F / 16);
                liftArrowView.setGravity(Gravity.CENTER_VERTICAL);
                liftArrowView.setQueuedRenderLayer(QueuedRenderLayer.LIGHT_TRANSLUCENT);
                liftArrowView.setColor(0xFFFFFFFE);


                //时间显示
                final long time = WorldHelper.getTimeOfDay(world);
                Date day = new Date();

                //游戏时间处理
                long ticksInDay = time % 24000;
                int totalSeconds = (int) (ticksInDay * 3.6);
                int hours24 = (totalSeconds / 3600 + 6) % 24; // 从06:00开始
                int minutes = (totalSeconds % 3600) / 60;

                int hours12 = hours24 % 12;

                SimpleDateFormat dateFormat = new SimpleDateFormat("M月 d日");
                String timeStr = dateFormat.format(day);
                String timePeriod = hours24 < 12 ? "AM" : "PM";
                // 移除"%02d"中的'0'，讓小時數不帶前導零
                String formattedTime = String.format("%d:%02d", hours12, minutes);
                String text = timeStr + " " + formattedTime + timePeriod;


                final TextView textView = new TextView();
                textView.setId("textView");
                textView.setBasicsAttributes(world, blockPos, FontList.instance.getFont("wqy-microhei"), 5, 0xFFFFFFFF);
                textView.setTextureId(String.format("mitsubishi_nexway_screen_2_date_display_%d", i))
                ;
                textView.setText(text);
                textView.setWidth(4F / 16);
                textView.setHeight(1F / 16);
                textView.setDisplayLength(20, 0);
                textView.setTextAlign(TextView.HorizontalTextAlign.CENTER);
                textView.setGravity(Gravity.CENTER_HORIZONTAL);

                numberLayout.addChild(liftArrowView);
                numberLayout.addChild(liftFloorDisplayView);
                parentLayout.addChild(numberLayout);
                parentLayout.addChild(textView);
            }
        }
        parentLayout.render();
    }
}