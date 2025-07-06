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
import org.mtr.mod.render.QueuedRenderLayer;
import org.mtr.mod.render.StoredMatrixTransformations;
import top.xfunny.mod.Init;
import top.xfunny.mod.block.TestLiftButtons;
import top.xfunny.mod.block.TestLiftButtonsWithoutScreen;
import top.xfunny.mod.block.base.LiftButtonsBase;
import top.xfunny.mod.client.client_data.LiftSpeed;
import top.xfunny.mod.client.resource.FontList;
import top.xfunny.mod.client.view.*;
import top.xfunny.mod.client.view.view_group.FrameLayout;
import top.xfunny.mod.client.view.view_group.LinearLayout;
import top.xfunny.mod.item.YteGroupLiftButtonsLinker;
import top.xfunny.mod.item.YteLiftButtonsLinker;
import top.xfunny.mod.keymapping.DefaultButtonsKeyMapping;
import top.xfunny.mod.util.ReverseRendering;

import java.util.Comparator;

public class RenderTestLiftButtons4 extends BlockEntityRenderer<TestLiftButtons.BlockEntity> implements DirectionHelper, IGui, IBlock {
    private DefaultButtonsKeyMapping keyMapping;
    private LiftSpeed liftSpeed = new LiftSpeed();

    public RenderTestLiftButtons4(Argument argument) {
        super(argument);
    }

    @Override
    public void render(TestLiftButtons.BlockEntity blockEntity, float v, GraphicsHolder graphicsHolder1, int light, int overlay) {
        final World world = blockEntity.getWorld2();

        keyMapping = blockEntity.getKeyMapping();

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
            graphicsHolder.translate(0, 0, 7F / 16 - SMALL_OFFSET);
        });

        //创建一个纵向的linear layout作为最底层的父容器
        final LinearLayout parentLayout = new LinearLayout(true);
        parentLayout.setBasicsAttributes(world, blockPos);//传入必要的参数
        parentLayout.setStoredMatrixTransformations(storedMatrixTransformations1);
        parentLayout.setParentDimensions(8F / 16, 16F / 16);//宽度为8，高度为16，宽高取决于外呼模型像素大小，一个立方体其中一个面的像素宽高为16x16
        parentLayout.setPosition(-0.25F, 0);//通过设置坐标的方式设置底层layout的位置
        parentLayout.setWidth(LayoutSize.MATCH_PARENT);//宽度为match_parent，即占满父容器，最底层父容器大小已通过setParentDimensions设置
        parentLayout.setHeight(LayoutSize.MATCH_PARENT);//高度为match_parent，即占满父容器，最底层父容器大小已通过setParentDimensions设置

        //创建一个横向的linear layout用于放置显示屏
        final LinearLayout screenLayout = new LinearLayout(false);
        screenLayout.setBasicsAttributes(world, blockPos);//传入必要的参数
        screenLayout.setWidth(LayoutSize.WRAP_CONTENT);
        screenLayout.setHeight(LayoutSize.WRAP_CONTENT);
        screenLayout.setGravity(Gravity.CENTER_HORIZONTAL);//居中
        screenLayout.setMargin(0, 2F / 16, 0, 0);//设置外边距，可选
        screenLayout.setBackgroundColor(0xFF000000);

        //创建一个FrameLayout用于在剩余的空间中放置按钮
        final FrameLayout buttonLayout = new FrameLayout();
        buttonLayout.setBasicsAttributes(world, blockPos);
        buttonLayout.setWidth(LayoutSize.MATCH_PARENT);
        buttonLayout.setHeight(LayoutSize.MATCH_PARENT);

        final LinearLayout buttonGroup = new LinearLayout(true);
        buttonGroup.setBasicsAttributes(world, blockPos);
        buttonGroup.setWidth(LayoutSize.WRAP_CONTENT);
        buttonGroup.setHeight(LayoutSize.WRAP_CONTENT);
        buttonGroup.setGravity(Gravity.CENTER);

        ButtonView buttonUp = new ButtonView();
        buttonUp.setId("up");//必须设置id
        buttonUp.setBasicsAttributes(world, blockPos, keyMapping);
        buttonUp.setTexture(new Identifier(Init.MOD_ID, "textures/block/thyssenkrupp_button.png"));
        buttonUp.setDimension(3F / 16);
        buttonUp.setDefaultColor(0xFFFFFFFF);
        buttonUp.setPressedColor(0xFFFFCB3B);
        buttonUp.setHoverColor(0xFFFF9933);
        buttonUp.setGravity(Gravity.CENTER_HORIZONTAL);
        buttonUp.setLight(light);

        ButtonView buttonDown = new ButtonView();
        buttonDown.setId("down");
        buttonDown.setBasicsAttributes(world, blockPos, keyMapping);
        buttonDown.setTexture(new Identifier(Init.MOD_ID, "textures/block/thyssenkrupp_button.png"));
        buttonDown.setDimension(3F / 16);
        buttonDown.setDefaultColor(0xFFFFFFFF);
        buttonDown.setPressedColor(0xFF00FF00);
        buttonDown.setHoverColor(0xFFCCFF66);
        buttonDown.setGravity(Gravity.CENTER_HORIZONTAL);
        buttonDown.setLight(light);
        buttonDown.setFlip(false, true);

        //添加外呼与楼层轨道的连线
        final LineComponent line = new LineComponent();
        line.setBasicsAttributes(world, blockPos);

        // 创建一个对象列表，用于存储排序后的位置和升降机的配对信息
        final ObjectArrayList<ObjectObjectImmutablePair<BlockPos, Lift>> sortedPositionsAndLifts = new ObjectArrayList<>();

        // 遍历每个轨道位置，进行后续处理
        blockEntity.forEachTrackPosition(trackPosition -> {
            //开始渲染外呼与轨道的连线
            line.RenderLine(holdingLinker, trackPosition);

            //判断是否渲染上下按钮
            TestLiftButtonsWithoutScreen.hasButtonsClient(trackPosition, buttonDescriptor, (floorIndex, lift) -> {
                sortedPositionsAndLifts.add(new ObjectObjectImmutablePair<>(trackPosition, lift));
                final ObjectArraySet<LiftDirection> instructionDirections = lift.hasInstruction(floorIndex);

                instructionDirections.forEach(liftDirection -> {
                    switch (liftDirection) {
                        case DOWN:
                            //向下的按钮亮灯
                            buttonDown.activate();
                            break;
                        case UP:
                            //向上的按钮亮灯
                            buttonUp.activate();
                            break;
                    }
                });
            });
        });

        if (buttonDescriptor.hasUpButton()) {
            buttonGroup.addChild(buttonUp);
        }

        if (buttonDescriptor.hasDownButton()) {
            if (buttonDescriptor.hasUpButton()) {
                buttonDown.setMargin(0, 1F / 16, 0, 0);
            }
            buttonGroup.addChild(buttonDown);
        }

        //按距离对数组元素进行排序，使其只渲染最近的两部电梯的信息
        sortedPositionsAndLifts.sort(Comparator.comparingInt(sortedPositionAndLift -> blockPos.getManhattanDistance(new Vector3i(sortedPositionAndLift.left().data))));

        if (!sortedPositionsAndLifts.isEmpty()) {
            // 确定要渲染的电梯数量，这里设置为2个
            final int count = Math.min(2, sortedPositionsAndLifts.size());
            final boolean reverseRendering = count > 1 && ReverseRendering.reverseRendering(facing.rotateYCounterclockwise(), sortedPositionsAndLifts.get(0).left(), sortedPositionsAndLifts.get(1).left());

            for (int i = 0; i < count; i++) {
                final Lift lift = sortedPositionsAndLifts.get(i).right();

                //速度显示
                TextView textView = new TextView();
                textView.setBasicsAttributes(world,
                        blockPos, FontList.instance.getFont("wqy-microhei"),
                        7,
                        0xFFFF0000);
                textView.setWidth(2F / 16);
                textView.setHeight(1F / 16);
                textView.setDisplayLength(10, 0.2F);
                textView.setTextAlign(TextView.HorizontalTextAlign.CENTER);
                textView.setGravity(Gravity.CENTER_HORIZONTAL);
                textView.setText(String.format("%.2f m/s", liftSpeed.getSpeed(lift)));


                //添加外呼显示屏
                final LiftFloorDisplayView liftFloorDisplayView = new LiftFloorDisplayView();
                liftFloorDisplayView.setBasicsAttributes(world,
                        blockPos,
                        lift,
                        FontList.instance.getFont("acmeled"),//字体
                        6,//字号
                        0xFFFF0000);//字体颜色
                liftFloorDisplayView.setDisplayLength(3, 0.05F);//true开启滚动，开启滚动时的字数条件(>)，滚动速度
                liftFloorDisplayView.setTextureId("testliftbuttonsdisplay");//字体贴图id，不能与其他显示屏的重复
                liftFloorDisplayView.setWidth(3F / 16);//显示屏宽度
                liftFloorDisplayView.setHeight(3F / 16);//显示屏高度
                liftFloorDisplayView.setGravity(Gravity.CENTER_HORIZONTAL);
                liftFloorDisplayView.setTextAlign(TextView.HorizontalTextAlign.CENTER);//文字对齐方式，center为居中对齐，left为左对齐，right为右对齐

                //添加箭头
                final LiftArrowView liftArrowView = new LiftArrowView();
                liftArrowView.setBasicsAttributes(world, blockPos, lift, LiftArrowView.ArrowType.AUTO);
                liftArrowView.setTexture(new Identifier(Init.MOD_ID, "textures/block/mitsubishi_nexway_1_arrow.png"));
                //liftArrowView.setAnimationScrolling(true, 0.05F);
                liftArrowView.setAnimationBliking(true, 0.5F);
                liftArrowView.setDimension(2F / 16);//不填高度默认宽高比为1:1
                liftArrowView.setMargin(0, 0.5F / 16, 0, 0);
                liftArrowView.setGravity(Gravity.CENTER_HORIZONTAL);
                liftArrowView.setColor(0xFFFF0000);
                liftArrowView.setQueuedRenderLayer(QueuedRenderLayer.LIGHT_TRANSLUCENT);

                //创建一个linear layout用于组合数字和箭头
                final LinearLayout numberLayout = new LinearLayout(true);
                numberLayout.setBasicsAttributes(world, blockPos);
                numberLayout.setWidth(LayoutSize.WRAP_CONTENT);
                numberLayout.setHeight(LayoutSize.WRAP_CONTENT);
                numberLayout.addChild(liftArrowView);
                numberLayout.addChild(liftFloorDisplayView);
                numberLayout.addChild(textView);
                //将外呼显示屏添加到刚才设定的screenLayout线性布局中
                if (reverseRendering) {
                    screenLayout.addChild(numberLayout);
                    screenLayout.reverseChildren();
                } else {
                    screenLayout.addChild(numberLayout);
                }
            }
        }

        buttonLayout.addChild(buttonGroup);//将按钮添加到线性布局中进行渲染
        parentLayout.addChild(screenLayout);//将screenLayout添加到父容器中
        parentLayout.addChild(buttonLayout);//将buttonLayout添加到父容器中

        parentLayout.render();//渲染父容器
    }
}
