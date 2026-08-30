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
import top.xfunny.mod.block.MitsubishiNexWayScreen1Even;
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
import top.xfunny.mod.lift.policy.MitsubishiNexWayLanternPolicy;
import top.xfunny.mod.packet.PacketLanternSoundInstruction;

import java.util.Comparator;

public class RenderMitsubishiNexWayLantern2Vertical<T extends LiftButtonsBase.BlockEntityBase> extends BlockEntityRenderer<T> implements DirectionHelper, IGui, IBlock {
    private static final int PRESSED_COLOR = 0xFFFEE1A9;
    private static final int DEFAULT_COLOR = 0xFFFFFFFF;
    private static final Identifier BUTTON_TEXTURE = new Identifier(Init.MOD_ID, "textures/block/mitsubishi_lantern_light_2.png");
    private final boolean isOdd;

    public RenderMitsubishiNexWayLantern2Vertical(Argument dispatcher, Boolean isOdd) {
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

        final StoredMatrixTransformations storedMatrixTransformations = new StoredMatrixTransformations(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5);
        StoredMatrixTransformations storedMatrixTransformations1 = storedMatrixTransformations.copy();
        storedMatrixTransformations1.add(graphicsHolder -> {
            graphicsHolder.rotateYDegrees(-facing.asRotation());
            graphicsHolder.translate(0, 0, 7.9F / 16 - SMALL_OFFSET);
        });

        final FrameLayout parentLayout = new FrameLayout();
        parentLayout.setBasicsAttributes(world, blockPos);
        parentLayout.setStoredMatrixTransformations(storedMatrixTransformations1);
        parentLayout.setParentDimensions(15.5F / 16, 4.5F / 16);
        parentLayout.setPosition(isOdd ? -9.125F / 16 : -17.125F / 16, 9F / 16);
        parentLayout.setWidth(LayoutSize.MATCH_PARENT);
        parentLayout.setHeight(LayoutSize.MATCH_PARENT);

        final LinearLayout linearLayout = new LinearLayout(true);
        linearLayout.setBasicsAttributes(world, blockPos);
        linearLayout.setHeight(LayoutSize.WRAP_CONTENT);
        linearLayout.setWidth(LayoutSize.WRAP_CONTENT);
        linearLayout.setGravity(Gravity.CENTER);

        final FrameLayout lanternGroupLeft = new FrameLayout();
        lanternGroupLeft.setBasicsAttributes(world, blockPos);
        lanternGroupLeft.setWidth(LayoutSize.WRAP_CONTENT);
        lanternGroupLeft.setHeight(LayoutSize.WRAP_CONTENT);
        lanternGroupLeft.setGravity(Gravity.CENTER_VERTICAL);


        final FrameLayout lanternGroupRight = new FrameLayout();
        lanternGroupRight.setBasicsAttributes(world, blockPos);
        lanternGroupRight.setWidth(LayoutSize.WRAP_CONTENT);
        lanternGroupRight.setHeight(LayoutSize.WRAP_CONTENT);
        lanternGroupRight.setGravity(Gravity.CENTER_VERTICAL);

        final ButtonView downLanternLeft = new ButtonView();
        downLanternLeft.setBasicsAttributes(world, blockPos);
        downLanternLeft.setTexture(BUTTON_TEXTURE);
        downLanternLeft.setDimension(2.75F / 16);
        downLanternLeft.setGravity(Gravity.CENTER);
        downLanternLeft.setLight(light);
        downLanternLeft.setDefaultColor(DEFAULT_COLOR);
        downLanternLeft.setPressedColor(PRESSED_COLOR);
        downLanternLeft.setFlip(false, true);

        final ButtonView downLanternRight = new ButtonView();
        downLanternRight.setBasicsAttributes(world, blockPos);
        downLanternRight.setTexture(BUTTON_TEXTURE);
        downLanternRight.setDimension(2.75F / 16);
        downLanternRight.setGravity(Gravity.CENTER);
        downLanternRight.setLight(light);
        downLanternRight.setDefaultColor(DEFAULT_COLOR);
        downLanternRight.setPressedColor(PRESSED_COLOR);
        downLanternRight.setFlip(false, true);

        final ButtonView upLanternLeft = new ButtonView();
        upLanternLeft.setBasicsAttributes(world, blockPos);
        upLanternLeft.setTexture(BUTTON_TEXTURE);
        upLanternLeft.setDimension(2.75F / 16);
        upLanternLeft.setGravity(Gravity.CENTER);
        upLanternLeft.setLight(light);
        upLanternLeft.setDefaultColor(DEFAULT_COLOR);
        upLanternLeft.setPressedColor(PRESSED_COLOR);

        final ButtonView upLanternRight = new ButtonView();
        upLanternRight.setBasicsAttributes(world, blockPos);
        upLanternRight.setTexture(BUTTON_TEXTURE);
        upLanternRight.setDimension(2.75F / 16);
        upLanternRight.setGravity(Gravity.CENTER);
        upLanternRight.setLight(light);
        upLanternRight.setDefaultColor(DEFAULT_COLOR);
        upLanternRight.setPressedColor(PRESSED_COLOR);

        final LineComponent line = new LineComponent();
        line.setBasicsAttributes(world, blockPos);

        final LineComponent buttonLine = new LineComponent();
        buttonLine.setBasicsAttributes(world, blockPos);

        final ObjectArrayList<ObjectObjectImmutablePair<BlockPos, Lift>> sortedPositionsAndLifts = new ObjectArrayList<>();

        blockEntity.forEachTrackPosition(trackPosition -> {
            line.RenderLine(holdingLinker, trackPosition);

            MitsubishiNexWayScreen1Even.hasButtonsClient(trackPosition, buttonDescriptor, (floorIndex, lift) -> {
                sortedPositionsAndLifts.add(new ObjectObjectImmutablePair<>(trackPosition, lift));
            });

            LiftButtonsBase.LanternState state = blockEntity.getLanternState(
                    trackPosition, MitsubishiNexWayLanternPolicy.INSTANCE);

            if (state.downActive) {
                downLanternLeft.activate();
                downLanternRight.activate();
            }
            if (state.upActive) {
                upLanternLeft.activate();
                upLanternRight.activate();
            }
            if (state.justTriggered && state.soundCue != null) {
                InitClient.REGISTRY_CLIENT.sendPacketToServer(
                        new PacketLanternSoundInstruction(blockPos, state.soundCue));
            }
        });

        sortedPositionsAndLifts.sort(Comparator.comparingInt(sortedPositionAndLift -> blockPos.getManhattanDistance(new Vector3i(sortedPositionAndLift.left().data))));

        blockEntity.forEachLiftButtonPosition(buttonPosition -> {
            buttonLine.RenderLine(holdingLinker, buttonPosition, true);
        });

        if (buttonDescriptor.hasDownButton() && buttonDescriptor.hasUpButton()) {
            lanternGroupLeft.setMargin(0, 0.75F / 16, 0, 0);
            lanternGroupRight.addChild(upLanternRight);
            lanternGroupLeft.addChild(downLanternLeft);
            linearLayout.addChild(lanternGroupRight);
            linearLayout.addChild(lanternGroupLeft);

        } else if (buttonDescriptor.hasDownButton()) {
            lanternGroupLeft.addChild(downLanternLeft);
            linearLayout.addChild(lanternGroupLeft);
        } else if (buttonDescriptor.hasUpButton()) {
            lanternGroupRight.addChild(upLanternRight);
            linearLayout.addChild(lanternGroupRight);
        }

        parentLayout.addChild(linearLayout);
        parentLayout.render();
    }
}
