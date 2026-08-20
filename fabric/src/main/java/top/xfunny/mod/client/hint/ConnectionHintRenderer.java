package top.xfunny.mod.client.hint;

import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.mtr.mapping.holder.*;
import org.mtr.mapping.mapper.BlockEntityExtension;
import org.mtr.mapping.mapper.DirectionHelper;
import org.mtr.mapping.mapper.GraphicsHolder;
import org.mtr.mapping.mapper.PlayerHelper;
import org.mtr.mod.block.IBlock;
import top.xfunny.mod.ButtonRegistry;
import top.xfunny.mod.lift.LiftFloorRegistry;
import org.mtr.mapping.mapper.TextHelper;
import top.xfunny.mod.item.YteGroupLiftButtonsLinker;
import top.xfunny.mod.item.YteLiftButtonsLinker;

import java.util.ArrayList;
import java.util.List;

/**
 * 准心悬浮标签渲染器。
 * <p>
 * 显示已连接楼层轨道数 / 已连接按钮数，并在异常时列出缺失项。
 */
public final class ConnectionHintRenderer {

    private static final int COLOR_NORMAL  = 0xFFFFFFFF; // 白色（正常）
    private static final int COLOR_WARN    = 0xFFFFAA00; // 橙黄（异常）
    private static final int COLOR_HEADER  = 0xFFFF5555; // 红色（异常标题）
    private static final int SHADOW_COLOR  = 0xFF000000;

    private ConnectionHintRenderer() {}

    // ======================== 数据容器 ========================

    public static class HintInfo {
        public final int trackCount;
        public final int buttonCount;    // -1 不显示此行；>=0 显示
        public final int lanternCount;   // -1 不显示；>=0 显示（仅按钮类型）
        public final boolean hasTrackWarning;
        public final boolean hasButtonWarning;

        HintInfo(int trackCount, int buttonCount, int lanternCount,
                 boolean hasTrackWarning, boolean hasButtonWarning) {
            this.trackCount = trackCount;
            this.buttonCount = buttonCount;
            this.lanternCount = lanternCount;
            this.hasTrackWarning = hasTrackWarning;
            this.hasButtonWarning = hasButtonWarning;
        }

        boolean hasAnyWarning() { return hasTrackWarning || hasButtonWarning; }
    }

    // ======================== 公共接口 ========================

    public static HintInfo getHintsAt(BlockPos pos) {
        final MinecraftClient client = MinecraftClient.getInstance();
        final ClientPlayerEntity player = client.getPlayerMapped();
        if (player == null) return null;

        if (!PlayerHelper.isHolding(PlayerEntity.cast(player),
                item -> item.data instanceof YteLiftButtonsLinker
                     || item.data instanceof YteGroupLiftButtonsLinker)) {
            return null;
        }

        final World world = player.getEntityWorld();
        if (world == null) return null;

        final BlockPos checkPos = getDisplayPos(world, pos);
        final BlockEntity blockEntity = world.getBlockEntity(checkPos);
        if (blockEntity == null || !(blockEntity.data instanceof BlockEntityExtension))
            return null;

        final BlockEntityExtension ext = (BlockEntityExtension) blockEntity.data;
        if (!(ext instanceof LiftFloorRegistry)) return null;

        return buildHintInfo(ext);
    }

    public static BlockPos getDisplayPos(World world, BlockPos pos) {
        final BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity == null || !(blockEntity.data instanceof BlockEntityExtension))
            return pos;

        final BlockEntityExtension ext = (BlockEntityExtension) blockEntity.data;
        if (!isEvenSide(ext)) return pos;

        try {
            final BlockState state = world.getBlockState(ext.getPos2());
            if (state == null) return pos;
            final Direction facing = IBlock.getStatePropertySafe(state, DirectionHelper.FACING);
            final BlockPos oddPos = ext.getPos2().offset(facing.rotateYCounterclockwise());
            final BlockEntity oddEntity = world.getBlockEntity(oddPos);
            if (oddEntity != null && oddEntity.data instanceof BlockEntityExtension)
                return oddPos;
        } catch (Exception ignored) {}
        return pos;
    }

    public static void renderLabel(GraphicsHolder g, Vector3d cameraOffset,
                                   BlockPos targetPos, HintInfo info) {
        final LineWithColor[] lines = buildLines(info);
        if (lines.length == 0) return;

        // 以碰撞箱中心为锚点
        final World world = MinecraftClient.getInstance().getPlayerMapped() != null
                ? MinecraftClient.getInstance().getPlayerMapped().getEntityWorld() : null;
        final Vector3d anchor = getCollisionBoxCenter(world, targetPos);

        g.push();

        g.translate(
                anchor.getXMapped() - cameraOffset.getXMapped(),
                anchor.getYMapped() - cameraOffset.getYMapped(),
                anchor.getZMapped() - cameraOffset.getZMapped()
        );

        final Entity camera = MinecraftClient.getInstance().getCameraEntityMapped();
        if (camera != null) {
            g.rotateYDegrees(-camera.getYaw(0));
            g.rotateXDegrees(camera.getPitch(0));
        }

        // 微调：右偏 + 略上移 + 稍突出方块表面
        g.translate(0.1, 0.05, -0.3);

        final float s = 0.008F;
        g.scale(-s, -s, s);

        final int defaultLight = GraphicsHolder.getDefaultLight();
        final int lineHeight = 12;
        final int padding = 4;

        int maxWidth = 0;
        for (LineWithColor line : lines) {
            final int w = GraphicsHolder.getTextWidth(line.text);
            if (w > maxWidth) maxWidth = w;
        }
        final int bgW = maxWidth + padding * 2;
        final int bgH = lineHeight * lines.length + padding * 2;
        final int bgX = -bgW / 2;
        final int bgY = -bgH / 2;

        // 纯文字 + 阴影，不绘制背景避免 drawLineInWorld 兼容性问题

        // 文字
        for (int i = 0; i < lines.length; i++) {
            final int textX = bgX + padding;
            final int textY = bgY + padding + i * lineHeight + lineHeight - 2;
            final LineWithColor line = lines[i];

            g.drawText(line.text, textX + 1, textY + 1, SHADOW_COLOR, false, defaultLight);
            g.drawText(line.text, textX, textY, line.color, false, defaultLight);
        }

        g.pop();
    }

    // ======================== 行数据 ========================

    private static class LineWithColor {
        final String text;
        final int color;
        LineWithColor(String text, int color) {
            this.text = text;
            this.color = color;
        }
    }

    // ======================== 构建文案 ========================

    private static LineWithColor[] buildLines(HintInfo info) {
        final List<LineWithColor> list = new ArrayList<>();

        // 状态行（始终显示）
        list.add(new LineWithColor(
                TextHelper.translatable("hint.yte.connected_track_count", info.trackCount).getString(),
                COLOR_NORMAL));
        if (info.buttonCount >= 0) {
            list.add(new LineWithColor(
                    TextHelper.translatable("hint.yte.connected_button_count", info.buttonCount).getString(),
                    COLOR_NORMAL));
        }
        if (info.lanternCount >= 0) {
            list.add(new LineWithColor(
                    TextHelper.translatable("hint.yte.connected_lantern_count", info.lanternCount).getString(),
                    COLOR_NORMAL));
        }

        // 异常行
        if (info.hasAnyWarning()) {
            list.add(new LineWithColor(
                    TextHelper.translatable("hint.yte.warning_header").getString(),
                    COLOR_HEADER));
            int idx = 1;
            if (info.hasTrackWarning) {
                list.add(new LineWithColor(
                        TextHelper.translatable("hint.yte.unconnected_track", idx).getString(),
                        COLOR_WARN));
                idx++;
            }
            if (info.hasButtonWarning) {
                list.add(new LineWithColor(
                        TextHelper.translatable("hint.yte.unconnected_button", idx).getString(),
                        COLOR_WARN));
            }
        }

        return list.toArray(new LineWithColor[0]);
    }

    // ======================== 碰撞箱工具 ========================

    private static Vector3d getCollisionBoxCenter(World world, BlockPos pos) {
        if (world == null) return fallbackCenter(pos);
        try {
            final BlockState state = world.getBlockState(pos);
            final VoxelShape shape = state.getOutlineShape(new BlockView(world.data), pos);
            if (shape == null || shape.isEmpty()) return fallbackCenter(pos);
            final Box box = shape.getBoundingBox();
            final Vector3d local = box.getCenter();
            return new Vector3d(
                    pos.getX() + local.getXMapped(),
                    pos.getY() + local.getYMapped(),
                    pos.getZ() + local.getZMapped()
            );
        } catch (Exception ignored) {
            return fallbackCenter(pos);
        }
    }

    private static Vector3d fallbackCenter(BlockPos pos) {
        return new Vector3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
    }

    // ======================== 数据采集 ========================

    private static HintInfo buildHintInfo(BlockEntityExtension blockEntity) {
        final LiftFloorRegistry floor = (LiftFloorRegistry) blockEntity;
        final ObjectOpenHashSet<BlockPos> tracks = floor.getTrackPositions();
        final int trackCount = tracks != null ? tracks.size() : 0;
        final boolean hasTrackWarning = trackCount == 0;

        final boolean isButton = isButtonBlock(blockEntity);
        final boolean isTerminal = isTerminalBlock(blockEntity);

        if (isButton) {
            // 按钮：显示到站灯数（不检测警告）
            int lanternCount = 0;
            if (blockEntity instanceof ButtonRegistry) {
                final ObjectOpenHashSet<BlockPos> bp =
                        ((ButtonRegistry) blockEntity).getLiftButtonPositions();
                lanternCount = bp != null ? bp.size() : 0;
            }
            return new HintInfo(trackCount, -1, lanternCount, hasTrackWarning, false);
        }

        if (isTerminal) {
            // 智能分配终端：显示轨道数 + 到站灯数
            int lanternCount = 0;
            if (blockEntity instanceof ButtonRegistry) {
                final ObjectOpenHashSet<BlockPos> bp =
                        ((ButtonRegistry) blockEntity).getLiftButtonPositions();
                lanternCount = bp != null ? bp.size() : 0;
            }
            return new HintInfo(trackCount, -1, lanternCount, hasTrackWarning, false);
        }

        // 到站灯 / 其他：显示按钮数 + 警告
        final boolean needsButton = blockEntity instanceof ButtonRegistry;
        int buttonCount = -1;
        boolean hasButtonWarning = false;
        if (needsButton) {
            final ObjectOpenHashSet<BlockPos> bp =
                    ((ButtonRegistry) blockEntity).getLiftButtonPositions();
            buttonCount = bp != null ? bp.size() : 0;
            hasButtonWarning = buttonCount == 0;
        }
        return new HintInfo(trackCount, buttonCount, -1, hasTrackWarning, hasButtonWarning);
    }

    private static boolean isButtonBlock(BlockEntityExtension blockEntity) {
        final World world = blockEntity.getWorld2();
        if (world == null) return false;
        final BlockState state = world.getBlockState(blockEntity.getPos2());
        if (state == null) return false;
        final Object blockData = state.getBlock().data;
        return blockData instanceof top.xfunny.mod.block.base.LiftButtonsBase
                && ((top.xfunny.mod.block.base.LiftButtonsBase) blockData).allowPress;
    }

    private static boolean isTerminalBlock(BlockEntityExtension blockEntity) {
        final World world = blockEntity.getWorld2();
        if (world == null) return false;
        final BlockState state = world.getBlockState(blockEntity.getPos2());
        if (state == null) return false;
        return state.getBlock().data instanceof top.xfunny.mod.block.base.LiftDestinationDispatchTerminalBase;
    }

    private static boolean isEvenSide(BlockEntityExtension blockEntity) {
        final World world = blockEntity.getWorld2();
        if (world == null) return false;
        final BlockState state = world.getBlockState(blockEntity.getPos2());
        if (state == null) return false;
        try {
            return IBlock.getStatePropertySafe(state, IBlock.SIDE) == IBlock.EnumSide.RIGHT;
        } catch (Exception ignored) {
            return false;
        }
    }
}
