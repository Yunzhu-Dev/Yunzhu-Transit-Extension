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
import top.xfunny.mod.block.KoneKDS330Button1WithoutScreen;
import top.xfunny.mod.block.SchindlerMSeriesButton;
import top.xfunny.mod.block.base.LiftButtonsBase;
import top.xfunny.mod.client.view.*;
import top.xfunny.mod.client.view.view_group.FrameLayout;
import top.xfunny.mod.item.YteGroupLiftButtonsLinker;
import top.xfunny.mod.item.YteLiftButtonsLinker;
import top.xfunny.mod.keymapping.DefaultButtonsKeyMapping;

public class RenderKoneKDS330Button1WithoutScreen extends BlockEntityRenderer<KoneKDS330Button1WithoutScreen.BlockEntity> implements DirectionHelper, IGui, IBlock {

    private static final int HOVER_COLOR = 0xFFCCCCCC;
    private static final int PRESSED_COLOR = 0xFFFFFFFF;
    private static final int DEFAULT_COLOR = 0xFFFFFFFF;
    private final Identifier BUTTON_TEXTURE_UP = new Identifier(Init.MOD_ID, "textures/block/kone_kds330_up_button.png");
    private final Identifier BUTTON_TEXTURE_DOWN = new Identifier(Init.MOD_ID, "textures/block/kone_kds330_up_button.png");//todo
    private final Identifier BUTTON_LIGHT_TEXTURE_UP = new Identifier(Init.MOD_ID, "textures/block/kone_kds330_up_button_light.png");
    private final Identifier BUTTON_LIGHT_TEXTURE_DOWN = new Identifier(Init.MOD_ID, "textures/block/kone_kds330_up_button_light.png");//todo

    public RenderKoneKDS330Button1WithoutScreen(Argument dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(KoneKDS330Button1WithoutScreen.BlockEntity blockEntity, float tickDelta, GraphicsHolder graphicsHolder1, int light, int overlay) {
        final World world = blockEntity.getWorld2();
        if (world == null) {
            return;
        }

        final ClientPlayerEntity clientPlayerEntity = MinecraftClient.getInstance().getPlayerMapped();
        if (clientPlayerEntity == null) {
            return;
        }

        final DefaultButtonsKeyMapping keyMapping = blockEntity.getKeyMapping();

        final BlockPos blockPos = blockEntity.getPos2();
        final BlockState blockState = world.getBlockState(blockPos);
        final Direction facing = IBlock.getStatePropertySafe(blockState, FACING);
        final boolean holdingLinker = PlayerHelper.isHolding(PlayerEntity.cast(clientPlayerEntity), item -> item.data instanceof YteLiftButtonsLinker || item.data instanceof YteGroupLiftButtonsLinker);
        LiftButtonsBase.LiftButtonDescriptor buttonDescriptor = new LiftButtonsBase.LiftButtonDescriptor(false, false);

        // 创建一个存储矩阵转换的实例，用于后续的渲染操作
        // 参数为方块的中心位置坐标 (x, y, z)
        final StoredMatrixTransformations storedMatrixTransformations1 = new StoredMatrixTransformations(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5);
        storedMatrixTransformations1.add(graphicsHolder -> {
            graphicsHolder.rotateYDegrees(-facing.asRotation());
            graphicsHolder.translate(0, 0, 7.5F / 16 - SMALL_OFFSET);
        });

        final FrameLayout buttonUpLayout = new FrameLayout();
        buttonUpLayout.setBasicsAttributes(world, blockPos);
        buttonUpLayout.setStoredMatrixTransformations(storedMatrixTransformations1);
        buttonUpLayout.setParentDimensions(4.5F / 16, 3.05F / 16);
        buttonUpLayout.setPosition(-2.25F / 16, 5.275F / 16);
        buttonUpLayout.setWidth(LayoutSize.MATCH_PARENT);
        buttonUpLayout.setHeight(LayoutSize.MATCH_PARENT);

        final FrameLayout buttonDownLayout = new FrameLayout();
        buttonDownLayout.setBasicsAttributes(world, blockPos);
        buttonDownLayout.setStoredMatrixTransformations(storedMatrixTransformations1);
        buttonDownLayout.setParentDimensions(4.5F / 16, 3.05F / 16);
        buttonDownLayout.setPosition(-2.25F / 16, 0.725F / 16);
        buttonDownLayout.setWidth(LayoutSize.MATCH_PARENT);
        buttonDownLayout.setHeight(LayoutSize.MATCH_PARENT);

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

        ImageView buttonUp = new ImageView();
        buttonUp.setBasicsAttributes(world, blockPos);
        buttonUp.setTexture(BUTTON_TEXTURE_UP);
        buttonUp.setDimension(0.9F / 16);
        buttonUp.setGravity(Gravity.CENTER);
        buttonUp.setLight(light);

        ButtonView buttonUpLight = new ButtonView();
        buttonUpLight.setId("up");
        buttonUpLight.setBasicsAttributes(world, blockPos, keyMapping);
        buttonUpLight.setTexture(BUTTON_LIGHT_TEXTURE_UP);
        buttonUpLight.setDimension(0.9F / 16);
        buttonUpLight.setGravity(Gravity.CENTER);
        buttonUpLight.setLight(light);
        buttonUpLight.setDefaultColor(DEFAULT_COLOR);
        buttonUpLight.setHoverColor(HOVER_COLOR);
        buttonUpLight.setPressedColor(PRESSED_COLOR);

        ImageView buttonDown = new ImageView();
        buttonDown.setBasicsAttributes(world, blockPos);
        buttonDown.setTexture(BUTTON_TEXTURE_DOWN);
        buttonDown.setDimension(0.9F / 16);
        buttonDown.setGravity(Gravity.CENTER);
        buttonDown.setLight(light);
        buttonDown.setFlip(false, true);

        ButtonView buttonDownLight = new ButtonView();
        buttonDownLight.setId("down");
        buttonDownLight.setBasicsAttributes(world, blockPos, keyMapping);
        buttonDownLight.setTexture(BUTTON_LIGHT_TEXTURE_DOWN);
        buttonDownLight.setDimension(0.9F / 16);
        buttonDownLight.setGravity(Gravity.CENTER);
        buttonDownLight.setLight(light);
        buttonDownLight.setDefaultColor(DEFAULT_COLOR);
        buttonDownLight.setHoverColor(HOVER_COLOR);
        buttonDownLight.setPressedColor(PRESSED_COLOR);
        buttonDownLight.setFlip(false, true);


        final LineComponent line = new LineComponent();
        line.setBasicsAttributes(world, blockPos);

        final ObjectArrayList<ObjectObjectImmutablePair<BlockPos, Lift>> sortedPositionsAndLifts = new ObjectArrayList<>();

        blockEntity.forEachTrackPosition(trackPosition -> {
            line.RenderLine(holdingLinker, trackPosition);

            SchindlerMSeriesButton.hasButtonsClient(trackPosition, buttonDescriptor, (floorIndex, lift) -> {
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
            buttonUpGroup.addChild(buttonUp);
            buttonUpGroup.addChild(buttonUpLight);
            buttonUpLayout.addChild(buttonUpGroup);
        }

        if (buttonDescriptor.hasDownButton()) {
            buttonDownGroup.addChild(buttonDown);
            buttonDownGroup.addChild(buttonDownLight);
            buttonDownLayout.addChild(buttonDownGroup);
        }

        buttonDownLayout.render();
        buttonUpLayout.render();
    }
}
