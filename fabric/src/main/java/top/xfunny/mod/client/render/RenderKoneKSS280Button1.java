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
import top.xfunny.mod.block.KoneKSS280Button1;
import top.xfunny.mod.block.base.LiftButtonsBase;
import top.xfunny.mod.client.resource.FontList;
import top.xfunny.mod.client.view.*;
import top.xfunny.mod.client.view.view_group.FrameLayout;
import top.xfunny.mod.client.view.view_group.LinearLayout;
import top.xfunny.mod.item.YteGroupLiftButtonsLinker;
import top.xfunny.mod.item.YteLiftButtonsLinker;
import top.xfunny.mod.keymapping.DefaultButtonsKeyMapping;
import top.xfunny.mod.util.ReverseRendering;

import java.util.Comparator;

public class RenderKoneKSS280Button1 extends BlockEntityRenderer<KoneKSS280Button1.BlockEntity> implements DirectionHelper, IGui, IBlock {

    private static final int HOVER_COLOR = 0xFFCCCCCC;
    private static final int PRESSED_COLOR = 0xFFFFFFFF;
    private static final int DEFAULT_COLOR = 0xFF000000;
    private static final Identifier ARROW_TEXTURE = new Identifier(Init.MOD_ID, "textures/block/kone_arrow_light.png");
    private static final BooleanProperty UNLOCKED = BooleanProperty.of("unlocked");
    private final Identifier BUTTON_TEXTURE = new Identifier(Init.MOD_ID, "textures/block/kone_kss280_button_1.png");//todo

    public RenderKoneKSS280Button1(Argument dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(KoneKSS280Button1.BlockEntity blockEntity, float tickDelta, GraphicsHolder graphicsHolder1, int light, int overlay) {
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
        final boolean unlocked = IBlock.getStatePropertySafe(blockState, UNLOCKED);
        LiftButtonsBase.LiftButtonDescriptor buttonDescriptor = new LiftButtonsBase.LiftButtonDescriptor(false, false);

        final StoredMatrixTransformations storedMatrixTransformations = new StoredMatrixTransformations(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5);
        StoredMatrixTransformations storedMatrixTransformations1 = storedMatrixTransformations.copy();
        storedMatrixTransformations1.add(graphicsHolder -> {
            graphicsHolder.rotateYDegrees(-facing.asRotation());
            graphicsHolder.translate(0, 0, 7.5F / 16 - SMALL_OFFSET);
        });

        final DefaultButtonsKeyMapping keyMapping = blockEntity.getKeyMapping();

        final FrameLayout screenContainer = new FrameLayout();
        screenContainer.setBasicsAttributes(world, blockPos);
        screenContainer.setStoredMatrixTransformations(storedMatrixTransformations1);
        screenContainer.setParentDimensions(2.5F / 16, 3F / 16);
        screenContainer.setPosition(-1.25F / 16, 3.375F / 16);
        screenContainer.setWidth(LayoutSize.MATCH_PARENT);
        screenContainer.setHeight(LayoutSize.MATCH_PARENT);

        final FrameLayout buttonUpLayout = new FrameLayout();
        buttonUpLayout.setBasicsAttributes(world, blockPos);
        buttonUpLayout.setStoredMatrixTransformations(storedMatrixTransformations1);
        buttonUpLayout.setParentDimensions(2.5F / 16, 3.05F / 16);
        buttonUpLayout.setPosition(-1.25F / 16, 6.575F / 16);
        buttonUpLayout.setWidth(LayoutSize.MATCH_PARENT);
        buttonUpLayout.setHeight(LayoutSize.MATCH_PARENT);

        final FrameLayout buttonDownLayout = new FrameLayout();
        buttonDownLayout.setBasicsAttributes(world, blockPos);
        buttonDownLayout.setStoredMatrixTransformations(storedMatrixTransformations1);
        buttonDownLayout.setParentDimensions(2.5F / 16, 3.05F / 16);
        buttonDownLayout.setPosition(-1.25F / 16, 0.925F / 16);
        buttonDownLayout.setWidth(LayoutSize.MATCH_PARENT);
        buttonDownLayout.setHeight(LayoutSize.MATCH_PARENT);

        final LinearLayout screenLayout = new LinearLayout(false);
        screenLayout.setBasicsAttributes(world, blockPos);
        screenLayout.setWidth(LayoutSize.WRAP_CONTENT);
        screenLayout.setHeight(LayoutSize.WRAP_CONTENT);
        screenLayout.setGravity(Gravity.CENTER);
        screenLayout.setMargin(0.2F / 16, 0, 0, 0);

        final FrameLayout buttonUpGroup = new FrameLayout();
        buttonUpGroup.setBasicsAttributes(world, blockPos);
        buttonUpGroup.setWidth(LayoutSize.WRAP_CONTENT);
        buttonUpGroup.setHeight(LayoutSize.WRAP_CONTENT);
        buttonUpGroup.setGravity(Gravity.CENTER);

        final FrameLayout buttonDownGroup = new FrameLayout();
        buttonDownGroup.setBasicsAttributes(world, blockPos);
        buttonDownGroup.setWidth(LayoutSize.WRAP_CONTENT);
        buttonDownGroup.setHeight(LayoutSize.WRAP_CONTENT);
        buttonDownGroup.setGravity(Gravity.CENTER);

        ButtonView buttonUpLight = new ButtonView();
        buttonUpLight.setId("up");
        buttonUpLight.setBasicsAttributes(world, blockPos, keyMapping);
        buttonUpLight.setTexture(BUTTON_TEXTURE);
        buttonUpLight.setDimension(0.7F / 16, 569, 395);
        buttonUpLight.setGravity(Gravity.CENTER);
        buttonUpLight.setLight(light);
        buttonUpLight.setDefaultColor(DEFAULT_COLOR);
        buttonUpLight.setHoverColor(HOVER_COLOR);
        buttonUpLight.setPressedColor(PRESSED_COLOR);

        ButtonView buttonDownLight = new ButtonView();
        buttonDownLight.setId("down");
        buttonDownLight.setBasicsAttributes(world, blockPos, keyMapping);
        buttonDownLight.setTexture(BUTTON_TEXTURE);
        buttonDownLight.setDimension(0.7F / 16, 569, 395);
        buttonDownLight.setGravity(Gravity.CENTER);
        buttonDownLight.setLight(light);
        buttonDownLight.setDefaultColor(DEFAULT_COLOR);
        buttonDownLight.setHoverColor(HOVER_COLOR);
        buttonDownLight.setPressedColor(PRESSED_COLOR);
        buttonDownLight.setFlip(false, true);

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
            KoneKSS280Button1.hasButtonsClient(trackPosition, buttonDescriptor, (floorIndex, lift) -> {
                sortedPositionsAndLifts.add(new ObjectObjectImmutablePair<>(trackPosition, lift));
                final ObjectArraySet<LiftDirection> instructionDirections = lift.hasInstruction(floorIndex);
                instructionDirections.forEach(liftDirection -> {
                    switch (liftDirection) {
                        case DOWN:
                            //向下的按钮亮灯
                            buttonDownLight.activate();
                            break;
                        case UP:
                            //向上的按钮亮灯
                            buttonUpLight.activate();
                            break;
                    }
                });
            });
        });
        sortedPositionsAndLifts.sort(Comparator.comparingInt(sortedPositionAndLift -> blockPos.getManhattanDistance(new Vector3i(sortedPositionAndLift.left().data))));

        if (!sortedPositionsAndLifts.isEmpty()) {
            // 确定要渲染的电梯数量，这里设置为2个
            final int count = Math.min(2, sortedPositionsAndLifts.size());
            final boolean reverseRendering = count > 1 && ReverseRendering.reverseRendering(facing.rotateYCounterclockwise(), sortedPositionsAndLifts.get(0).left(), sortedPositionsAndLifts.get(1).left());


            for (int i = 0; i < count; i++) {
                //添加外呼显示屏
                final LiftFloorDisplayView liftFloorDisplayView = new LiftFloorDisplayView();
                liftFloorDisplayView.setBasicsAttributes(world,
                        blockPos,
                        sortedPositionsAndLifts.get(i).right(),
                        FontList.instance.getFont("kone-lcd-segment"),//字体
                        3,//字号
                        0xFFFFFFFF);//字体颜色
                liftFloorDisplayView.setTextureId("kone-kss-280-lcd-segment");//字体贴图id，不能与其他显示屏的重复
                liftFloorDisplayView.setWidth(1.3F / 16);//显示屏宽度
                liftFloorDisplayView.setHeight(1.7F / 16);//显示屏高度
                liftFloorDisplayView.setMargin(-0.625F / 16, -0.6F / 16, 0.1F / 16, 0);
                liftFloorDisplayView.setTextAlign(TextView.HorizontalTextAlign.RIGHT);//文字对齐方式，center为居中对齐，left为左对齐，right为右对齐

                //添加箭头
                final LiftArrowView liftArrowView = new LiftArrowView();
                liftArrowView.setBasicsAttributes(world, blockPos, sortedPositionsAndLifts.get(i).right(), LiftArrowView.ArrowType.AUTO);
                liftArrowView.setTexture(ARROW_TEXTURE);
                liftArrowView.setDimension(0.6F / 16);
                liftArrowView.setMargin(0, -1F / 16, 0, 0.5F / 16);
                liftArrowView.setGravity(Gravity.CENTER_HORIZONTAL);
                liftArrowView.setQueuedRenderLayer(QueuedRenderLayer.LIGHT_TRANSLUCENT);
                // If unlocked, display the arrow.
                if (unlocked) {
                    liftArrowView.setColor(0xFFFFFFFF);
                } else {
                    liftArrowView.setColor(0xFF000000);
                }


                //创建一个linear layout用于组合数字和箭头
                final LinearLayout numberLayout = new LinearLayout(true);
                numberLayout.setBasicsAttributes(world, blockPos);
                numberLayout.setWidth(LayoutSize.WRAP_CONTENT);
                numberLayout.setHeight(LayoutSize.WRAP_CONTENT);
                numberLayout.addChild(liftArrowView);
                numberLayout.addChild(liftFloorDisplayView);
                //将外呼显示屏添加到刚才设定的screenLayout线性布局中
                if (reverseRendering) {
                    screenLayout.addChild(numberLayout);
                    screenLayout.reverseChildren();
                } else {
                    screenLayout.addChild(numberLayout);
                }
            }
        }

        screenContainer.addChild(screenLayout);

        if (buttonDescriptor.hasUpButton()) {
            buttonUpGroup.addChild(buttonUpLight);
            buttonUpLayout.addChild(buttonUpGroup);
        }

        if (buttonDescriptor.hasDownButton()) {
            buttonDownGroup.addChild(buttonDownLight);
            buttonDownLayout.addChild(buttonDownGroup);
        }

        buttonDownLayout.render();
        buttonUpLayout.render();
        screenContainer.render();
    }
}