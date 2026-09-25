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
import top.xfunny.mod.block.HitachiGHL668Lantern1Even;
import top.xfunny.mod.block.base.LiftButtonsBase;
import top.xfunny.mod.client.InitClient;
import top.xfunny.mod.client.view.ButtonView;
import top.xfunny.mod.client.view.Gravity;
import top.xfunny.mod.client.view.LayoutSize;
import top.xfunny.mod.client.view.LineComponent;
import top.xfunny.mod.client.view.view_group.LinearLayout;
import top.xfunny.mod.item.YteGroupLiftButtonsLinker;
import top.xfunny.mod.item.YteLiftButtonsLinker;
import top.xfunny.mod.lift.policy.HitachiGHLLanternPolicy;
import top.xfunny.mod.packet.PacketLanternSoundInstruction;

public class RenderHitachiGHL668Lantern1<T extends LiftButtonsBase.BlockEntityBase> extends BlockEntityRenderer<T> implements DirectionHelper, IGui, IBlock {
    private static final int PRESSED_COLOR = 0xFFFFFFFF;
    private static final int DEFAULT_COLOR = 0xFFEFEFEF;
    private static final Identifier BUTTON_TEXTURE = new Identifier(Init.MOD_ID, "textures/block/hitachi_ghl_668_lantern_1.png");
    private final boolean isOdd;

    public RenderHitachiGHL668Lantern1(Argument dispatcher, Boolean isOdd) {
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
        LiftButtonsBase.LiftButtonDescriptor buttonDescriptor = new LiftButtonsBase.LiftButtonDescriptor(false, false);

        final StoredMatrixTransformations storedMatrixTransformations = new StoredMatrixTransformations(0.5, 0, 0.5);
        StoredMatrixTransformations storedMatrixTransformations1 = storedMatrixTransformations.copy();
        storedMatrixTransformations1.add(graphicsHolder -> {
            graphicsHolder.rotateYDegrees(-facing.asRotation());
            graphicsHolder.translate(0, 0, 7.55F / 16 - SMALL_OFFSET);
        });

        final LinearLayout parentLayout = new LinearLayout(true);
        parentLayout.setBasicsAttributes(world, blockPos);
        parentLayout.setStoredMatrixTransformations(storedMatrixTransformations1);
        parentLayout.setParentDimensions(7.5F / 16, 7.5F / 16);
        parentLayout.setPosition(isOdd ? -3.75F / 16 : -11.75F / 16, 2.025F / 16);
        parentLayout.setWidth(LayoutSize.MATCH_PARENT);
        parentLayout.setHeight(LayoutSize.MATCH_PARENT);

        ButtonView lanternUp = new ButtonView();
        lanternUp.setBasicsAttributes(world, blockPos);
        lanternUp.setTexture(BUTTON_TEXTURE);
        lanternUp.setDimension(3F / 16, 1.5F / 3F);
        lanternUp.setGravity(Gravity.CENTER_HORIZONTAL);
        lanternUp.setDefaultColor(DEFAULT_COLOR);
        lanternUp.setPressedColor(PRESSED_COLOR);
        lanternUp.setMargin(0, 0, 0, 0.01F / 16);
        lanternUp.setLight(light);

        ButtonView lanternDown = new ButtonView();
        lanternDown.setBasicsAttributes(world, blockPos);
        lanternDown.setTexture(BUTTON_TEXTURE);
        lanternDown.setDimension(3F / 16, 1.5F / 3F);
        lanternDown.setGravity(Gravity.CENTER_HORIZONTAL);
        lanternDown.setDefaultColor(DEFAULT_COLOR);
        lanternDown.setPressedColor(PRESSED_COLOR);
        lanternDown.setLight(light);
        lanternDown.setFlip(false, true);

        final LineComponent line = new LineComponent();
        line.setBasicsAttributes(world, blockEntity.getPos2());

        final LineComponent buttonLine = new LineComponent();
        buttonLine.setBasicsAttributes(world, blockEntity.getPos2());

        final ObjectArrayList<ObjectObjectImmutablePair<BlockPos, Lift>> sortedPositionsAndLifts = new ObjectArrayList<>();

        blockEntity.forEachTrackPosition(trackPosition -> {
            line.RenderLine(holdingLinker, trackPosition);

            HitachiGHL668Lantern1Even.hasButtonsClient(trackPosition, buttonDescriptor, (floorIndex, lift) -> {
                sortedPositionsAndLifts.add(new ObjectObjectImmutablePair<>(trackPosition, lift));
            });

            LiftButtonsBase.LanternState state = blockEntity.getLanternState(
                    trackPosition, HitachiGHLLanternPolicy.GHL668);

            if (state.downActive) {
                lanternDown.activate();
            }
            if (state.upActive) {
                lanternUp.activate();
            }
            if (state.justTriggered && state.soundCue != null) {
                InitClient.REGISTRY_CLIENT.sendPacketToServer(new PacketLanternSoundInstruction(blockPos, state.soundCue));
            }
        });

        blockEntity.forEachLiftButtonPosition(buttonPosition -> {
            buttonLine.RenderLine(holdingLinker, buttonPosition, true);
        });


        if (buttonDescriptor.hasDownButton() || buttonDescriptor.hasUpButton()) {
            parentLayout.addChild(lanternUp);
            parentLayout.addChild(lanternDown);
        }

        parentLayout.render();
    }
}
