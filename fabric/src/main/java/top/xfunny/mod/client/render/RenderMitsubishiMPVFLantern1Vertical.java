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
import top.xfunny.mod.block.MitsubishiMPVFLantern1VerticalEven;
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
import top.xfunny.mod.lift.policy.MitsubishiMPVFLanternPolicy;
import top.xfunny.mod.packet.PacketLanternSoundInstruction;

import java.util.Comparator;

public class RenderMitsubishiMPVFLantern1Vertical<T extends LiftButtonsBase.BlockEntityBase> extends BlockEntityRenderer<T> implements DirectionHelper, IGui, IBlock {
    private static final int PRESSED_COLOR = 0xFFEFC2A3;
    private static final int DEFAULT_COLOR = 0xFFCCCCCC;
    private static final Identifier BUTTON_TEXTURE = new Identifier(Init.MOD_ID, "textures/block/mitsubishi_mp_lantern_1.png");
    private final boolean isOdd;

    public RenderMitsubishiMPVFLantern1Vertical(Argument dispatcher, Boolean isOdd) {
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
            graphicsHolder.translate(0, 0, 7.95F / 16 - SMALL_OFFSET);
        });

        final FrameLayout parentLayout = new FrameLayout();
        parentLayout.setBasicsAttributes(world, blockEntity.getPos2());
        parentLayout.setStoredMatrixTransformations(storedMatrixTransformations1);
        parentLayout.setParentDimensions(1.825F / 16, 3.225F / 16);
        parentLayout.setPosition(isOdd ? -0.9125F / 16 : -8.9125F / 16, 2.5F / 16);
        parentLayout.setWidth(LayoutSize.MATCH_PARENT);
        parentLayout.setHeight(LayoutSize.MATCH_PARENT);

        final LinearLayout linearLayout = new LinearLayout(true);
        linearLayout.setBasicsAttributes(world, blockEntity.getPos2());
        linearLayout.setHeight(LayoutSize.WRAP_CONTENT);
        linearLayout.setWidth(LayoutSize.WRAP_CONTENT);
        linearLayout.setGravity(Gravity.CENTER);

        ButtonView upLantern = new ButtonView();
        upLantern.setBasicsAttributes(world, blockEntity.getPos2());
        upLantern.setTexture(BUTTON_TEXTURE);
        upLantern.setDimension(1.375F / 16, 236, 256);
        upLantern.setGravity(Gravity.CENTER_HORIZONTAL);
        upLantern.setLight(light);
        upLantern.setDefaultColor(DEFAULT_COLOR);
        upLantern.setPressedColor(PRESSED_COLOR);

        ButtonView downLantern = new ButtonView();
        downLantern.setBasicsAttributes(world, blockEntity.getPos2());
        downLantern.setTexture(BUTTON_TEXTURE);
        downLantern.setDimension(1.375F / 16, 236, 256);
        downLantern.setGravity(Gravity.CENTER_HORIZONTAL);
        downLantern.setLight(light);
        downLantern.setDefaultColor(DEFAULT_COLOR);
        downLantern.setPressedColor(PRESSED_COLOR);
        downLantern.setFlip(false, true);


        final LineComponent line = new LineComponent();
        line.setBasicsAttributes(world, blockEntity.getPos2());

        final LineComponent buttonLine = new LineComponent();
        buttonLine.setBasicsAttributes(world, blockEntity.getPos2());

        final ObjectArrayList<ObjectObjectImmutablePair<BlockPos, Lift>> sortedPositionsAndLifts = new ObjectArrayList<>();

        blockEntity.forEachTrackPosition(trackPosition -> {
            line.RenderLine(holdingLinker, trackPosition);

            MitsubishiMPVFLantern1VerticalEven.hasButtonsClient(trackPosition, buttonDescriptor, (floorIndex, lift) -> {
                sortedPositionsAndLifts.add(new ObjectObjectImmutablePair<>(trackPosition, lift));
            });

            LiftButtonsBase.LanternState state = blockEntity.getLanternState(
                    trackPosition, MitsubishiMPVFLanternPolicy.INSTANCE);

            if (state.downActive) {
                downLantern.activate();
            }
            if (state.upActive) {
                upLantern.activate();
            }
            if (state.justTriggered && state.soundCue != null) {
                InitClient.REGISTRY_CLIENT.sendPacketToServer(new PacketLanternSoundInstruction(blockPos, state.soundCue));
            }
        });

        sortedPositionsAndLifts.sort(Comparator.comparingInt(sortedPositionAndLift -> blockEntity.getPos2().getManhattanDistance(new Vector3i(sortedPositionAndLift.left().data))));

        if (buttonDescriptor.hasDownButton() && buttonDescriptor.hasUpButton()) {
            downLantern.setMargin(0, 0.625F / 16, 0, 0);
            linearLayout.addChild(upLantern);
            linearLayout.addChild(downLantern);
        } else if (buttonDescriptor.hasDownButton()) {
            linearLayout.addChild(downLantern);
        } else if (buttonDescriptor.hasUpButton()) {
            linearLayout.addChild(upLantern);
        }

        parentLayout.addChild(linearLayout);
        parentLayout.render();
    }
}
