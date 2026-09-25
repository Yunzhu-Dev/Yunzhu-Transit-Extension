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
import top.xfunny.mod.Init;
import top.xfunny.mod.block.OtisSeries1Lantern1Even;
import top.xfunny.mod.block.base.LiftButtonsBase;
import top.xfunny.mod.client.InitClient;
import top.xfunny.mod.client.view.ButtonView;
import top.xfunny.mod.client.view.Gravity;
import top.xfunny.mod.client.view.LayoutSize;
import top.xfunny.mod.client.view.LineComponent;
import top.xfunny.mod.client.view.view_group.FrameLayout;
import top.xfunny.mod.client.view.view_group.LinearLayout;
import top.xfunny.mod.item.YteGroupLiftButtonsLinker;
import top.xfunny.mod.item.YteLiftButtonsLinker;
import top.xfunny.mod.packet.PacketLanternSoundInstruction;

public class RenderOtisSeries1Lantern1<T extends LiftButtonsBase.BlockEntityBase> extends BlockEntityRenderer<T> implements DirectionHelper, IGui, IBlock {

    private static final int PRESSED_COLOR = 0xFF1D953F;
    private static final int DEFAULT_COLOR = 0xFF0D441D;
    private static final Identifier ARROW_TEXTURE_END = new Identifier(Init.MOD_ID, "textures/block/otis_series_1_lantern_arrow_end.png");
    private static final Identifier ARROW_TEXTURE_MIDDLE = new Identifier(Init.MOD_ID, "textures/block/otis_series_1_lantern_arrow_middle.png");
    private final boolean isOdd;

    public RenderOtisSeries1Lantern1(Argument dispatcher, Boolean isOdd) {
        super(dispatcher);
        this.isOdd = isOdd;
    }

    @Override
    public void render(T blockEntity, float tickDelta, GraphicsHolder graphicsHolder1, int light, int overlay) {
        final World world = blockEntity.getWorld2();
        if (world == null) return;

        final ClientPlayerEntity clientPlayerEntity = MinecraftClient.getInstance().getPlayerMapped();
        if (clientPlayerEntity == null) return;

        final boolean holdingLinker = PlayerHelper.isHolding(
                PlayerEntity.cast(clientPlayerEntity),
                item -> item.data instanceof YteLiftButtonsLinker || item.data instanceof YteGroupLiftButtonsLinker
        );
        final BlockPos blockPos = blockEntity.getPos2();
        final BlockState blockState = world.getBlockState(blockPos);
        final Direction facing = IBlock.getStatePropertySafe(blockState, FACING);

        LiftButtonsBase.LiftButtonDescriptor buttonDescriptor = new LiftButtonsBase.LiftButtonDescriptor(false, false);

        final StoredMatrixTransformations storedMatrixTransformations = new StoredMatrixTransformations(0.5, 0, 0.5);
        StoredMatrixTransformations storedMatrixTransformations1 = storedMatrixTransformations.copy();
        storedMatrixTransformations1.add(graphicsHolder -> {
            graphicsHolder.rotateYDegrees(-facing.asRotation());
            graphicsHolder.translate(0, 0, 7.8F / 16 - SMALL_OFFSET);
        });

        FrameLayout parentLayout = new FrameLayout();
        parentLayout.setBasicsAttributes(world, blockPos);
        parentLayout.setStoredMatrixTransformations(storedMatrixTransformations1);
        parentLayout.setParentDimensions(2.5F / 16, 3.75F / 16);
        parentLayout.setPosition(isOdd ? -1.25F / 16 : -9.25F / 16, 0.9F / 16);
        parentLayout.setWidth(LayoutSize.MATCH_PARENT);
        parentLayout.setHeight(LayoutSize.MATCH_PARENT);

        LinearLayout backgroundLayout = new LinearLayout(true);
        backgroundLayout.setBasicsAttributes(world, blockPos);
        backgroundLayout.setWidth(LayoutSize.WRAP_CONTENT);
        backgroundLayout.setHeight(LayoutSize.WRAP_CONTENT);
        backgroundLayout.setGravity(Gravity.CENTER);
        backgroundLayout.setBackgroundColor(0xFF000000);

        ButtonView upLantern = new ButtonView();
        upLantern.setBasicsAttributes(world, blockPos);
        upLantern.setTexture(ARROW_TEXTURE_END);
        upLantern.setDimension(1.8F / 16);
        upLantern.setLight(light);
        upLantern.setDefaultColor(DEFAULT_COLOR);
        upLantern.setPressedColor(PRESSED_COLOR);
        upLantern.setMargin(0, 0.1F / 16, 0, -1.8F / 16);

        ButtonView downLantern = new ButtonView();
        downLantern.setBasicsAttributes(world, blockPos);
        downLantern.setTexture(ARROW_TEXTURE_END);
        downLantern.setDimension(1.8F / 16);
        downLantern.setLight(light);
        downLantern.setDefaultColor(DEFAULT_COLOR);
        downLantern.setPressedColor(PRESSED_COLOR);
        downLantern.setFlip(false, true);
        downLantern.setMargin(0, -1.8F / 16, 0, 0.1F / 16);

        ButtonView middleLantern = new ButtonView();
        middleLantern.setBasicsAttributes(world, blockPos);
        middleLantern.setTexture(ARROW_TEXTURE_MIDDLE);
        middleLantern.setDimension(1.8F / 16);
        middleLantern.setLight(light);
        middleLantern.setDefaultColor(DEFAULT_COLOR);
        middleLantern.setPressedColor(PRESSED_COLOR);

        final LineComponent line = new LineComponent();
        line.setBasicsAttributes(world, blockPos);

        final LineComponent buttonLine = new LineComponent();
        buttonLine.setBasicsAttributes(world, blockPos);

        final ObjectArrayList<ObjectObjectImmutablePair<BlockPos, Lift>> sortedPositionsAndLifts = new ObjectArrayList<>();

        blockEntity.forEachTrackPosition(trackPosition -> {
            line.RenderLine(holdingLinker, trackPosition);

            OtisSeries1Lantern1Even.hasButtonsClient(
                    trackPosition, buttonDescriptor, (floorIndex, lift) -> { }
            );

            // Otis Series 1: 到站/呼叫登记亮灯+发声 + 距离≤3层预亮灯（不闪烁、不发声）
            LiftButtonsBase.LanternState state = blockEntity.getLanternState(trackPosition);
            final boolean shouldShow = state.phase == LiftButtonsBase.LanternPhase.ARRIVED
                    || state.phase == LiftButtonsBase.LanternPhase.CALL_REGISTERED;
            final boolean preLight = state.phase == LiftButtonsBase.LanternPhase.APPROACHING
                    && state.distanceToNearestLift > 0 && state.distanceToNearestLift <= 3;

            final boolean downOn = state.downActive && (shouldShow || preLight);
            final boolean upOn = state.upActive && (shouldShow || preLight);

            if (downOn) {
                downLantern.activate();
                middleLantern.activate();
            } else {
                downLantern.resetLanternSound();
            }
            if (upOn) {
                upLantern.activate();
                middleLantern.activate();
            } else {
                upLantern.resetLanternSound();
            }
            if (!downOn && !upOn) {
                middleLantern.resetLanternSound();
            }

            if (state.justTriggered && shouldShow) {
                if (state.downActive) {
                    InitClient.REGISTRY_CLIENT.sendPacketToServer(new PacketLanternSoundInstruction(blockPos, "otis_series_1_lantern_down"));
                }
                if (state.upActive) {
                    InitClient.REGISTRY_CLIENT.sendPacketToServer(new PacketLanternSoundInstruction(blockPos, "otis_series_1_lantern_up"));
                }
            }
        });

        blockEntity.forEachLiftButtonPosition(buttonPosition -> {
            buttonLine.RenderLine(holdingLinker, buttonPosition, true);
        });

        if (buttonDescriptor.hasUpButton() || buttonDescriptor.hasDownButton()) {
            backgroundLayout.addChild(upLantern);
            backgroundLayout.addChild(middleLantern);
            backgroundLayout.addChild(downLantern);
        }

        parentLayout.addChild(backgroundLayout);
        parentLayout.render();
    }
}
