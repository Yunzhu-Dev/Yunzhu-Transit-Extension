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
import top.xfunny.mod.Init;
import top.xfunny.mod.block.MitsubishiMaxiezScreen2Even;
import top.xfunny.mod.block.base.LiftPanelBase;
import top.xfunny.mod.client.resource.FontList;
import top.xfunny.mod.client.view.*;
import top.xfunny.mod.client.view.view_group.LinearLayout;
import top.xfunny.mod.item.YteGroupLiftButtonsLinker;
import top.xfunny.mod.item.YteLiftButtonsLinker;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

// 确保导入 java.util.Locale 以便设置 SimpleDateFormat 的本地化

public class RenderMitsubishiMaxiezScreen2<T extends LiftPanelBase.BlockEntityBase> extends BlockEntityRenderer<T> implements DirectionHelper, IGui, IBlock {
    private final boolean isOdd;

    public RenderMitsubishiMaxiezScreen2(Argument dispatcher, Boolean isOdd) {
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
        parentLayout.setPosition(isOdd ? -1.875F / 16 : -9.875F / 16, 10.125F / 16);
        parentLayout.setWidth(LayoutSize.MATCH_PARENT);
        parentLayout.setHeight(LayoutSize.MATCH_PARENT);

        final LinearLayout numberLayout = new LinearLayout(false);
        numberLayout.setBasicsAttributes(world, blockPos);
        numberLayout.setWidth(LayoutSize.WRAP_CONTENT);
        numberLayout.setHeight(LayoutSize.WRAP_CONTENT);
        numberLayout.setMargin(0.34F / 16, 0.6F / 16, 0, -.2F / 16);


        final LineComponent line = new LineComponent();
        line.setBasicsAttributes(world, blockPos);

        final ObjectArrayList<ObjectObjectImmutablePair<BlockPos, Lift>> sortedPositionsAndLifts = new ObjectArrayList<>();

        blockEntity.forEachTrackPosition(trackPosition -> {
            line.RenderLine(holdingLinker, trackPosition);
            MitsubishiMaxiezScreen2Even.LiftCheck(trackPosition, (floorIndex, lift) -> sortedPositionsAndLifts.add(new ObjectObjectImmutablePair<>(trackPosition, lift)));
        });

        sortedPositionsAndLifts.sort(Comparator.comparingInt(sortedPositionAndLift -> blockPos.getManhattanDistance(new Vector3i(sortedPositionAndLift.left().data))));

        if (!sortedPositionsAndLifts.isEmpty()) {
            final int count = 1;

            for (int i = 0; i < count; i++) {
                final LiftFloorDisplayView liftFloorDisplayView = new LiftFloorDisplayView();
                liftFloorDisplayView.setBasicsAttributes(world,
                        blockPos,
                        sortedPositionsAndLifts.get(i).right(),
                        FontList.instance.getFont("gill_sans_mt_light"),
                        12,
                        0xFFBBBBBB);
                liftFloorDisplayView.setTextureId(String.format("mitsubishi_maxiez_screen_2_display_%d_%s", i, blockEntity.getPos2().asLong()));
                liftFloorDisplayView.setWidth(1.9F / 16);
                liftFloorDisplayView.setHeight(1.5F / 16);
                liftFloorDisplayView.setGravity(Gravity.CENTER_VERTICAL);
                liftFloorDisplayView.setTextAlign(TextView.HorizontalTextAlign.CENTER);
                liftFloorDisplayView.setLetterSpacing(0);
                liftFloorDisplayView.setMargin(0.2F / 16, 0.2F / 16, 0, 0);
                liftFloorDisplayView.addStoredMatrixTransformations(graphicsHolder -> graphicsHolder.translate(0, 0, -SMALL_OFFSET));
                if (liftFloorDisplayView.getTextLength() >= 3) {
                    liftFloorDisplayView.setMargin(0.15F / 16, 0.2F / 16, 0, 0);
                    liftFloorDisplayView.setAdaptMode(LiftFloorDisplayView.AdaptMode.FORCE_FIT_WIDTH);
                } else {
                    liftFloorDisplayView.setAdaptMode(LiftFloorDisplayView.AdaptMode.ASPECT_FILL);
                }

                final LiftArrowView liftArrowView = new LiftArrowView();
                liftArrowView.setBasicsAttributes(world, blockPos, sortedPositionsAndLifts.get(i).right(), LiftArrowView.ArrowType.AUTO);
                liftArrowView.setTexture(new Identifier(Init.MOD_ID, "textures/block/mitsubishi_maxiez_2_lcd_arrow_1.png"));
                liftArrowView.setDimension(0.875F / 16);
                liftArrowView.setMargin(0, 0.1F / 16, 0, 0);
                liftArrowView.setGravity(Gravity.CENTER_VERTICAL);
                liftArrowView.setQueuedRenderLayer(QueuedRenderLayer.LIGHT_TRANSLUCENT);
                liftArrowView.setColor(0xFFFFFFFF);
                liftArrowView.setAnimationYawRotation(true, 0.075F);

                final long time = WorldHelper.getTimeOfDay(world);
                Date day = new Date();

                //游戏时间处理
                long ticksInDay = time % 24000;
                int totalSeconds = (int) (ticksInDay * 3.6);
                int hours24 = (totalSeconds / 3600 + 6) % 24; // 从06:00开始
                int minutes = (totalSeconds % 3600) / 60;

                SimpleDateFormat dateFormat = new SimpleDateFormat("M月d日", Locale.CHINESE);
                String dateStr = dateFormat.format(day);

                // 2. 格式化星期部分： 使用 EEEE 获取完整的 "星期日" 或 "星期一"
                SimpleDateFormat dayOfWeekFormat = new SimpleDateFormat("EEEE", Locale.CHINESE);
                String fullDayOfWeek = dayOfWeekFormat.format(day);

                // 3. 手动替换为单字缩写
                String shortDayOfWeek;
                switch (fullDayOfWeek) {
                    case "星期日":
                    case "周日":
                        shortDayOfWeek = "日";
                        break;
                    case "星期一":
                    case "周一":
                        shortDayOfWeek = "一";
                        break;
                    case "星期二":
                    case "周二":
                        shortDayOfWeek = "二";
                        break;
                    case "星期三":
                    case "周三":
                        shortDayOfWeek = "三";
                        break;
                    case "星期四":
                    case "周四":
                        shortDayOfWeek = "四";
                        break;
                    case "星期五":
                    case "周五":
                        shortDayOfWeek = "五";
                        break;
                    case "星期六":
                    case "周六":
                        shortDayOfWeek = "六";
                        break;
                    default:
                        shortDayOfWeek = "?"; // 无法识别时使用问号或保持原样
                        break;
                }

                // 4. 格式化游戏时间为 24 小时制 HH:mm
                String formattedTime = String.format("%02d:%02d", hours24, minutes);

                // 5. 组合最终文本： 10月12日(日) 12:00
                String text = String.format("%s(%s)  %s", dateStr, shortDayOfWeek, formattedTime);

                final TextView textView = new TextView();
                textView.setId("textView");
                textView.setBasicsAttributes(world, blockPos, FontList.instance.getFont("wqy-microhei"), 4, 0xFFBBBBBB);
                textView.setTextureId(String.format("mitsubishi_maxiez_screen_1_date_display_%d", i))
                ;
                textView.setText(text);
                textView.setWidth(4F / 16);
                textView.setHeight(0.7F / 16);
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