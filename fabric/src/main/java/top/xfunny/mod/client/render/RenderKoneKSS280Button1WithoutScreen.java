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
import org.mtr.mod.render.StoredMatrixTransformations;
import top.xfunny.mod.Init;
import top.xfunny.mod.block.KoneKSS280Button1WithoutScreen;
import top.xfunny.mod.block.base.LiftButtonsBase;
import top.xfunny.mod.client.view.ButtonView;
import top.xfunny.mod.client.view.Gravity;
import top.xfunny.mod.client.view.LayoutSize;
import top.xfunny.mod.client.view.LineComponent;
import top.xfunny.mod.client.view.view_group.FrameLayout;
import top.xfunny.mod.client.view.view_group.LinearLayout;
import top.xfunny.mod.item.YteGroupLiftButtonsLinker;
import top.xfunny.mod.item.YteLiftButtonsLinker;
import top.xfunny.mod.keymapping.DefaultButtonsKeyMapping;

public class RenderKoneKSS280Button1WithoutScreen extends BlockEntityRenderer<KoneKSS280Button1WithoutScreen.BlockEntity> implements DirectionHelper, IGui, IBlock {

    private static final int HOVER_COLOR = 0xFFCCCCCC;
    private static final int PRESSED_COLOR = 0xFFFFFFFF;
    private static final int DEFAULT_COLOR = 0xFF000000;
    private static final BooleanProperty UNLOCKED = BooleanProperty.of("unlocked");
    private final Identifier BUTTON_TEXTURE = new Identifier(Init.MOD_ID, "textures/block/kone_kss280_button_1.png");//todo

    public RenderKoneKSS280Button1WithoutScreen(Argument dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(KoneKSS280Button1WithoutScreen.BlockEntity blockEntity, float tickDelta, GraphicsHolder graphicsHolder1, int light, int overlay) {
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
        screenContainer.setPosition(-1.25F / 16, 0.675F / 16);
        screenContainer.setWidth(LayoutSize.MATCH_PARENT);
        screenContainer.setHeight(LayoutSize.MATCH_PARENT);

        final FrameLayout buttonUpLayout = new FrameLayout();
        buttonUpLayout.setBasicsAttributes(world, blockPos);
        buttonUpLayout.setStoredMatrixTransformations(storedMatrixTransformations1);
        buttonUpLayout.setParentDimensions(2.5F / 16, 3.05F / 16);
        buttonUpLayout.setPosition(-1.25F / 16, 3.875F / 16);
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
            KoneKSS280Button1WithoutScreen.hasButtonsClient(trackPosition, buttonDescriptor, (floorIndex, lift) -> {
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