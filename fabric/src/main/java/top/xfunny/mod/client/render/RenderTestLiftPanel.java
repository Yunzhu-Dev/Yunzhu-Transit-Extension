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
import org.mtr.mod.Init;
import org.mtr.mod.InitClient;
import org.mtr.mod.block.BlockLiftTrackFloor;
import org.mtr.mod.block.IBlock;
import org.mtr.mod.client.IDrawing;
import org.mtr.mod.data.IGui;
import org.mtr.mod.render.QueuedRenderLayer;
import org.mtr.mod.render.StoredMatrixTransformations;
import top.xfunny.mod.block.TestLiftPanel;
import top.xfunny.mod.client.resource.TextureList;
import top.xfunny.mod.client.view.DirectRenderer;
import top.xfunny.mod.item.YteGroupLiftButtonsLinker;
import top.xfunny.mod.item.YteLiftButtonsLinker;
import top.xfunny.mod.util.ClientGetLiftDetails;
import top.xfunny.mod.util.ReverseRendering;

import java.util.Comparator;

public class RenderTestLiftPanel extends BlockEntityRenderer<TestLiftPanel.BlockEntity> implements DirectionHelper, IGui, IBlock {
    //已弃用
    private static final float ARROW_SPEED = 0.04F;
    private static final Identifier ARROW_TEXTURE = new Identifier(Init.MOD_ID, "textures/block/lift_arrow.png");
    private final boolean isOdd;
    private final Lift lift = null;

    public RenderTestLiftPanel(Argument dispatcher, Boolean isOdd) {
        super(dispatcher);
        this.isOdd = isOdd;
    }

    @Override
    public void render(TestLiftPanel.BlockEntity blockEntity, float tickDelta, GraphicsHolder graphicsHolder1, int light, int overlay) {
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
            //检测是否安装电梯
            TestLiftPanel.LiftCheck(trackPosition, (floorIndex, lift) -> {
                sortedPositionsAndLifts.add(new ObjectObjectImmutablePair<>(trackPosition, lift));
            });
        });

        // Sort list and only render the two closest lifts
        sortedPositionsAndLifts.sort(Comparator.comparingInt(sortedPositionAndLift -> blockPos.getManhattanDistance(new Vector3i(sortedPositionAndLift.left().data))));
        final StoredMatrixTransformations storedMatrixTransformations2 = storedMatrixTransformations1.copy();
        storedMatrixTransformations2.add(graphicsHolder -> {
            graphicsHolder.rotateYDegrees(-facing.asRotation());
            graphicsHolder.translate(0, 0, 0.4375 - SMALL_OFFSET);
        });

        // 检查排序后的电梯位置列表是否非空
        if (!sortedPositionsAndLifts.isEmpty()) {
            // 确定要渲染的电梯数量，最多为2个
            final int count = Math.min(2, sortedPositionsAndLifts.size());
            // 设置每个电梯显示的宽度，根据数量不同而变化
            final float width = count == 1 ? 0.25F : 0.495F;

            // 创建当前矩阵变换的副本以供后续修改
            final StoredMatrixTransformations storedMatrixTransformations3 = storedMatrixTransformations2.copy();
            // 添加旋转和平移变换
            storedMatrixTransformations3.add(graphicsHolder -> {
                graphicsHolder.rotateZDegrees(180);
                graphicsHolder.translate(-width / 2, 0, 0);
            });

            // 渲染黑色背景
            {
                final GraphicsHolder graphicsHolder = DirectRenderer.prepare(QueuedRenderLayer.EXTERIOR, new Identifier(Init.MOD_ID, "textures/block/black.png"), storedMatrixTransformations3);
                if (graphicsHolder != null) {
                    IDrawing.drawTexture(graphicsHolder, 0, -0.9375F, width, 0.40625F, Direction.UP, light);
                    graphicsHolder.pop();
                }
            }

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

                // 渲染当前电梯的显示
                renderLiftDisplay(storedMatrixTransformations4, world, sortedPositionsAndLifts.get(i).right(), width * 4 / count, 0.2F, 0.2F, 0.2F);

            }
        }
    }

    private void renderLiftDisplay(StoredMatrixTransformations storedMatrixTransformations, World world, Lift lift, float width, float width1, float height1, float height) {
        // 获取电梯的详细信息，包括运行方向和楼层信息
        final ObjectObjectImmutablePair<LiftDirection, ObjectObjectImmutablePair<String, String>> liftDetails = ClientGetLiftDetails.getLiftDetails(world, lift, Init.positionToBlockPos(lift.getCurrentFloor().getPosition()));
        final LiftDirection liftDirection = liftDetails.left();
        final String floorNumber = liftDetails.right().left();
        final String floorDescription = liftDetails.right().right();

        // 判断楼层编号和描述是否为空
        final boolean noFloorNumber = floorNumber.isEmpty();
        final boolean noFloorDisplay = floorDescription.isEmpty();
        final float gameTick = InitClient.getGameTick(); // 获取当前游戏刻
        final boolean goingUp = liftDirection == LiftDirection.UP; // 判断电梯是否向上运行
        final float arrowSize = width / 6; // 设置箭头大小
        final float y = height; // 箭头的Y轴位置

        // 渲染电梯运行方向的箭头
        if (liftDirection != LiftDirection.NONE) {
            final float uv = (gameTick * ARROW_SPEED) % 1;
            final int color = goingUp ? 0xFF00FF00 : 0xFFFF0000; // 根据运行方向设置箭头颜色
            {
                final GraphicsHolder graphicsHolder = DirectRenderer.prepare(QueuedRenderLayer.LIGHT_TRANSLUCENT, ARROW_TEXTURE, storedMatrixTransformations);
                if (graphicsHolder != null) {
                    IDrawing.drawTexture(graphicsHolder, -width / 4 + arrowSize, y - 0.24F, arrowSize, arrowSize, 0, (goingUp ? 0 : 1) + uv, 1, (goingUp ? 1 : 0) + uv, Direction.UP, color, GraphicsHolder.getDefaultLight());
                    graphicsHolder.pop();
                }
            }
        }
        // 渲染楼层信息
        if (!noFloorNumber || !noFloorDisplay) {
            float offset1;
            final String text = String.format("%s%s", floorNumber, noFloorNumber ? " " : ""); // 合并楼层信息文本

            if (text.length() > 2) {
                float scrollSpeed = 24F;
                float scaledSpeed = scrollSpeed * (width1); // 根据显示宽度和纹理总宽度缩放速度
                offset1 = (gameTick * scaledSpeed) % 1;
                // 如果走马灯移动到末端，回到起始位置
                if (offset1 > 1) {
                    offset1 = offset1 - 1;

                }
                float finalOffset = offset1;
                {
                    final GraphicsHolder graphicsHolder = DirectRenderer.prepare(QueuedRenderLayer.LIGHT_TRANSLUCENT, TextureList.instance.getTestLiftPanelDisplay(text, 0xFFAA00).identifier, storedMatrixTransformations);
                    if (graphicsHolder != null) {
                        IDrawing.drawTexture(graphicsHolder, -width + 0.9F, y - 0.07F, width1, height1, finalOffset, 0, finalOffset + (float) 1 / text.length() + 0.0001F * text.length(), 1F, Direction.UP, ARGB_WHITE, GraphicsHolder.getDefaultLight());
                        graphicsHolder.pop();
                    }
                }
            } else {
                // 如果不需要走马灯，保持位置不变
                {
                    final GraphicsHolder graphicsHolder = DirectRenderer.prepare(QueuedRenderLayer.LIGHT_TRANSLUCENT, TextureList.instance.getTestLiftPanelDisplay(text, 0xFFAA00).identifier, storedMatrixTransformations);
                    if (graphicsHolder != null) {
                        IDrawing.drawTexture(graphicsHolder, -width + 0.9F, y - 0.07F, width1, height1, 0, 0, 1, 1F, Direction.UP, ARGB_WHITE, GraphicsHolder.getDefaultLight());
                        graphicsHolder.pop();
                    }
                }
            }
        }
    }
}
