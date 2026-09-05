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
import org.mtr.mod.Init;
import org.mtr.mod.block.BlockLiftTrackFloor;
import org.mtr.mod.block.IBlock;
import org.mtr.mod.client.IDrawing;
import org.mtr.mod.data.IGui;
import org.mtr.mod.render.QueuedRenderLayer;
import org.mtr.mod.render.StoredMatrixTransformations;
import top.xfunny.mod.block.TestLiftButtonsWithoutScreen;
import top.xfunny.mod.block.base.LiftButtonsBase;
import top.xfunny.mod.client.view.DirectRenderer;
import top.xfunny.mod.item.YteGroupLiftButtonsLinker;
import top.xfunny.mod.item.YteLiftButtonsLinker;
import top.xfunny.mod.util.ReverseRendering;

import java.util.Comparator;

public class RenderTestLiftButtonsWithoutScreen extends BlockEntityRenderer<TestLiftButtonsWithoutScreen.BlockEntity> implements DirectionHelper, IGui, IBlock {

    private static final int HOVER_COLOR = 0xFFA55000;
    private static final int PRESSED_COLOR = 0xFFD70000;
    private static final Identifier BUTTON_TEXTURE = new Identifier(Init.MOD_ID, "textures/block/lift_button.png");

    public RenderTestLiftButtonsWithoutScreen(Argument dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(TestLiftButtonsWithoutScreen.BlockEntity blockEntity, float tickDelta, GraphicsHolder graphicsHolder1, int light, int overlay) {
        final World world = blockEntity.getWorld2();
        if (world == null) {
            return;
        }

        final ClientPlayerEntity clientPlayerEntity = MinecraftClient.getInstance().getPlayerMapped();
        if (clientPlayerEntity == null) {
            return;
        }

        final BlockPos blockPos = blockEntity.getPos2();
        final BlockState blockState = world.getBlockState(blockPos);
        final Direction facing = IBlock.getStatePropertySafe(blockState, FACING);
        final boolean holdingLinker = PlayerHelper.isHolding(PlayerEntity.cast(clientPlayerEntity), item -> item.data instanceof YteLiftButtonsLinker || item.data instanceof YteGroupLiftButtonsLinker);
        // 创建一个存储矩阵转换的实例，用于后续的渲染操作
        // 参数为方块的中心位置坐标 (x, y, z)
        final StoredMatrixTransformations storedMatrixTransformations1 = new StoredMatrixTransformations(0.5, 0, 0.5);


        // 定义一个布尔数组，用于记录按钮的状态
        // 数组顺序：向下按钮被按下、向上按钮被按下
        final boolean[] buttonStates = {false, false};
        LiftButtonsBase.LiftButtonDescriptor buttonDescriptor = new LiftButtonsBase.LiftButtonDescriptor(false, false);

        // 创建一个对象列表，用于存储排序后的位置和升降机的配对信息
        final ObjectArrayList<ObjectObjectImmutablePair<BlockPos, Lift>> sortedPositionsAndLifts = new ObjectArrayList<>();

        // 遍历每个轨道位置，进行后续处理
        blockEntity.forEachTrackPosition(trackPosition -> {
            // 手持连接器进行连线
            if (world.getBlockState(trackPosition).getBlock().data instanceof BlockLiftTrackFloor) {
                final Direction trackFacing = IBlock.getStatePropertySafe(world, trackPosition, FACING);
                RenderLiftObjectLink.RenderLiftObjectLink(
                        storedMatrixTransformations1,
                        new Vector3d(facing.getOffsetX() / 2F, 0.5, facing.getOffsetZ() / 2F),
                        new Vector3d(trackPosition.getX() - blockPos.getX() + trackFacing.getOffsetX() / 2F, trackPosition.getY() - blockPos.getY() + 0.5, trackPosition.getZ() - blockPos.getZ() + trackFacing.getOffsetZ() / 2F),
                        holdingLinker
                );
            }

            // Figure out whether the up and down buttons should be rendered
            TestLiftButtonsWithoutScreen.hasButtonsClient(trackPosition, buttonDescriptor, (floorIndex, lift) -> {
                // 确定是否渲染上下按钮，基于当前trackPosition和楼层信息
                // 该方法通过floorIndex和lift来决定是否添加trackPosition和lift到已排序的列表中
                // 同时，根据lift的方向（上或下），更新buttonStates数组以指示按钮的渲染状态
                // 这里使用lambda表达式来处理按钮状态的逻辑
                sortedPositionsAndLifts.add(new ObjectObjectImmutablePair<>(trackPosition, lift));
                final ObjectArraySet<LiftDirection> instructionDirections = lift.hasInstruction(floorIndex);
                instructionDirections.forEach(liftDirection -> {
                    switch (liftDirection) {
                        case DOWN:
                            buttonStates[0] = true;
                            break;
                        case UP:
                            buttonStates[1] = true;
                            break;
                    }
                });
            });
        });

        // Sort list and only render the two closest lifts
        sortedPositionsAndLifts.sort(Comparator.comparingInt(sortedPositionAndLift -> blockPos.getManhattanDistance(new Vector3i(sortedPositionAndLift.left().data))));

        // Check whether the player is looking at the top or bottom button
        final HitResult hitResult = MinecraftClient.getInstance().getCrosshairTargetMapped();
        final boolean lookingAtTopHalf;
        final boolean lookingAtBottomHalf;
        if (hitResult == null || !IBlock.getStatePropertySafe(blockState, TestLiftButtonsWithoutScreen.UNLOCKED)) {
            lookingAtTopHalf = false;
            lookingAtBottomHalf = false;
        } else {
            final Vector3d hitLocation = hitResult.getPos();
            final double hitY = MathHelper.fractionalPart(hitLocation.getYMapped());
            final boolean inBlock = hitY < 0.5 && Init.newBlockPos(hitLocation.getXMapped(), hitLocation.getYMapped(), hitLocation.getZMapped()).equals(blockPos);
            lookingAtTopHalf = inBlock && (!buttonDescriptor.hasDownButton() || hitY > 0.25);
            lookingAtBottomHalf = inBlock && (!buttonDescriptor.hasUpButton() || hitY < 0.25);
        }

        final StoredMatrixTransformations storedMatrixTransformations2 = storedMatrixTransformations1.copy();
        storedMatrixTransformations2.add(graphicsHolder -> {
            graphicsHolder.rotateYDegrees(-facing.asRotation());
            graphicsHolder.translate(0, 0, 0.4375 - SMALL_OFFSET);
        });

        // 根据按钮状态渲染按钮
        // 第一个按钮的渲染逻辑
        if (buttonDescriptor.hasDownButton()) {
            // 根据按钮的按下状态和鼠标位置选择不同的渲染层
            {
                final GraphicsHolder graphicsHolder = DirectRenderer.prepare(buttonStates[0] || lookingAtBottomHalf ? QueuedRenderLayer.LIGHT_TRANSLUCENT : QueuedRenderLayer.EXTERIOR, BUTTON_TEXTURE, storedMatrixTransformations2);
                if (graphicsHolder != null) {
                    IDrawing.drawTexture(
                            graphicsHolder,
                            -1.5F / 16,
                            (buttonDescriptor.hasUpButton() ? 0.5F : 2.5F) / 16,
                            3F / 16,
                            3F / 16,
                            0,
                            0,
                            1,
                            1,
                            facing,
                            buttonStates[0] ? PRESSED_COLOR : lookingAtBottomHalf ? HOVER_COLOR : ARGB_GRAY,
                            light
                    );
                    graphicsHolder.pop();
                }
            }
        }
        // 第二个按钮的渲染逻辑
        if (buttonDescriptor.hasUpButton()) {
            // 根据按钮的按下状态和鼠标位置选择不同的渲染层
            {
                final GraphicsHolder graphicsHolder = DirectRenderer.prepare(buttonStates[1] || lookingAtTopHalf ? QueuedRenderLayer.LIGHT_TRANSLUCENT : QueuedRenderLayer.EXTERIOR, BUTTON_TEXTURE, storedMatrixTransformations2);
                if (graphicsHolder != null) {
                    IDrawing.drawTexture(
                            graphicsHolder,
                            -1.5F / 16,
                            (buttonDescriptor.hasDownButton() ? 4.5F : 2.5F) / 16,
                            3F / 16,
                            3F / 16,
                            0,
                            1,
                            1,
                            0,
                            facing,
                            buttonStates[1] ? PRESSED_COLOR : lookingAtTopHalf ? HOVER_COLOR : ARGB_GRAY,
                            light
                    );
                    graphicsHolder.pop();
                }
            }
        }
        // 检查排序后的电梯位置列表是否非空
        if (!sortedPositionsAndLifts.isEmpty()) {
            // 确定要渲染的电梯数量，最多为2个
            final int count = Math.min(2, sortedPositionsAndLifts.size());
            // 设置每个电梯显示的宽度，根据数量不同而变化
            final float width = count == 1 ? 0.25F : 0.375F;

            // 创建当前矩阵变换的副本以供后续修改
            final StoredMatrixTransformations storedMatrixTransformations3 = storedMatrixTransformations2.copy();
            // 添加旋转和平移变换
            storedMatrixTransformations3.add(graphicsHolder -> {
                graphicsHolder.rotateZDegrees(180);
                graphicsHolder.translate(-width / 2, 0, 0);
            });


            // 根据按钮朝向判断两个最近的电梯是否需要反转渲染顺序
            final boolean reverseRendering = count > 1 && ReverseRendering.reverseRendering(facing.rotateYCounterclockwise(), sortedPositionsAndLifts.get(0).left(), sortedPositionsAndLifts.get(1).left());
            // 遍历要渲染的每个电梯
            for (int i = 0; i < count; i++) {
                // 计算当前电梯显示的x位置，考虑反转渲染顺序
                final double x = ((reverseRendering ? count - i - 1 : i) + 0.5) * width / count;
                // 创建另一个矩阵变换的副本用于当前电梯
                final StoredMatrixTransformations storedMatrixTransformations4 = storedMatrixTransformations3.copy();
                // 添加平移变换以定位电梯显示
                storedMatrixTransformations4.add(graphicsHolder -> graphicsHolder.translate(x, -0.875, -SMALL_OFFSET));
            }
        }
    }
}
