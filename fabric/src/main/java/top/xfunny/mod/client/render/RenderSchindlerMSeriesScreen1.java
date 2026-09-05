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
import org.mtr.mod.render.StoredMatrixTransformations;
import top.xfunny.mod.block.SchindlerMSeriesScreen1Even;
import top.xfunny.mod.block.base.LiftButtonsBase;
import top.xfunny.mod.client.resource.FontList;
import top.xfunny.mod.client.view.*;
import top.xfunny.mod.client.view.view_group.FrameLayout;
import top.xfunny.mod.item.YteGroupLiftButtonsLinker;
import top.xfunny.mod.item.YteLiftButtonsLinker;

import java.util.Comparator;

public class RenderSchindlerMSeriesScreen1<T extends LiftButtonsBase.BlockEntityBase> extends BlockEntityRenderer<T> implements DirectionHelper, IGui, IBlock {
    private final boolean isOdd;

    public RenderSchindlerMSeriesScreen1(Argument dispatcher, Boolean isOdd) {
        super(dispatcher);
        this.isOdd = isOdd;
    }

    @Override
    public void render(T blockEntity, float tickDelta, GraphicsHolder graphicsHolder, int light, int overlay) {
        final World world = blockEntity.getWorld2();
        if (world == null) {
            return;
        }

        final ClientPlayerEntity clientPlayerEntity = MinecraftClient.getInstance().getPlayerMapped();
        if (clientPlayerEntity == null) {
            return;
        }

        // 检查玩家是否持有链接器
        final boolean holdingLinker = PlayerHelper.isHolding(PlayerEntity.cast(clientPlayerEntity), item -> item.data instanceof YteLiftButtonsLinker || item.data instanceof YteGroupLiftButtonsLinker);
        final BlockPos blockPos = blockEntity.getPos2();
        final BlockState blockState = world.getBlockState(blockPos);
        final Direction facing = IBlock.getStatePropertySafe(blockState, FACING);
        LiftButtonsBase.LiftButtonDescriptor buttonDescriptor = new LiftButtonsBase.LiftButtonDescriptor(false, false);
        final EnumSide side = IBlock.getStatePropertySafe(blockState, SIDE);
        if (side == EnumSide.RIGHT) {
            return;
        }

        // 基础矩阵变换：旋转和贴平表面
        final StoredMatrixTransformations storedMatrixTransformations = new StoredMatrixTransformations(0.5, 0, 0.5);
        StoredMatrixTransformations storedMatrixTransformations1 = storedMatrixTransformations.copy();
        storedMatrixTransformations1.add(graphicsHolder1 -> {
            graphicsHolder1.rotateYDegrees(-facing.asRotation());
            graphicsHolder1.translate(0, 0, 7.9F / 16 - SMALL_OFFSET);
        });

        // 主布局容器
        final FrameLayout parentLayout = new FrameLayout();
        parentLayout.setBasicsAttributes(world, blockEntity.getPos2());
        parentLayout.setStoredMatrixTransformations(storedMatrixTransformations1);
        parentLayout.setParentDimensions((float) 7 / 16, (float) 7 / 16);
        // 根据 isOdd 区分坐标偏移
        parentLayout.setPosition(isOdd ? -.21875F : -.71875F, 0.34375F);
        parentLayout.setWidth(LayoutSize.MATCH_PARENT);
        parentLayout.setHeight(LayoutSize.MATCH_PARENT);

        // 屏幕显示内容的容器
        final FrameLayout screenLayout = new FrameLayout();
        screenLayout.setBasicsAttributes(world, blockEntity.getPos2());
        screenLayout.setWidth(LayoutSize.MATCH_PARENT);
        screenLayout.setHeight(LayoutSize.MATCH_PARENT);

        // 背景图层
        final ImageView background = new ImageView();
        background.setBasicsAttributes(world, blockEntity.getPos2());
        background.setTexture(new Identifier(top.xfunny.mod.Init.MOD_ID, "textures/block/schindler_m_series_screen_1.png"));
        background.setDimension(3.25F / 16);
        background.setGravity(Gravity.CENTER);

        // 链接线渲染
        final LineComponent line = new LineComponent();
        line.setBasicsAttributes(world, blockEntity.getPos2());

        // 按钮链接线渲染 (同步自 Screen2 的新结构)
        final LineComponent buttonLine = new LineComponent();
        buttonLine.setBasicsAttributes(world, blockEntity.getPos2());

        final ObjectArrayList<ObjectObjectImmutablePair<BlockPos, Lift>> sortedPositionsAndLifts = new ObjectArrayList<>();

        // 获取并筛选电梯实例
        blockEntity.forEachTrackPosition(trackPosition -> {
            line.RenderLine(holdingLinker, trackPosition);

            SchindlerMSeriesScreen1Even.hasButtonsClient(trackPosition, buttonDescriptor, (floorIndex, lift) -> {
                sortedPositionsAndLifts.add(new ObjectObjectImmutablePair<>(trackPosition, lift));
            });
        });

        // 按距离排序，确保渲染最近的电梯
        sortedPositionsAndLifts.sort(Comparator.comparingInt(sortedPositionAndLift -> blockEntity.getPos2().getManhattanDistance(new Vector3i(sortedPositionAndLift.left().data))));

        if (!sortedPositionsAndLifts.isEmpty()) {
            final int count = 1; // 只显示一个最近的电梯数据

            for (int i = 0; i < count; i++) {
                final LiftFloorDisplayView liftFloorDisplayView = new LiftFloorDisplayView();
                liftFloorDisplayView.setBasicsAttributes(world,
                        blockEntity.getPos2(),
                        sortedPositionsAndLifts.get(i).right(),
                        FontList.instance.getFont("schindler_m_series"),//字体
                        5,//字号
                        0xFFFF0000); //字体颜色 (经典的 Schindler 红色)

                liftFloorDisplayView.setDisplayLength(2, 0);//true开启滚动，开启滚动时的字数条件(>)，滚动速度
                liftFloorDisplayView.setTextureId(String.format("schindler_m_series_screen_1_display_%d", i));//字体贴图id，不能与其他显示屏的重复
                liftFloorDisplayView.setWidth((float) 2 / 16);//显示屏宽度
                liftFloorDisplayView.setHeight((float) 3 / 16);//显示屏高度
                liftFloorDisplayView.setGravity(Gravity.CENTER);
                liftFloorDisplayView.setTextAlign(TextView.HorizontalTextAlign.RIGHT);//文字对齐方式
                liftFloorDisplayView.addStoredMatrixTransformations(graphics -> graphics.translate(0, 0, -SMALL_OFFSET));

                screenLayout.addChild(liftFloorDisplayView);
            }
        }

        // 遍历渲染按钮的链接线 (同步自 Screen2 的新逻辑)
        blockEntity.forEachLiftButtonPosition(buttonPosition -> {
            buttonLine.RenderLine(holdingLinker, buttonPosition, true);
        });

        // 组合并执行渲染
        parentLayout.addChild(background);
        parentLayout.addChild(screenLayout);
        parentLayout.render();
    }
}