package top.xfunny.mod.client.view;

import org.mtr.mapping.holder.*;
import org.mtr.mod.Init;
import org.mtr.mod.block.IBlock;
import org.mtr.mod.render.QueuedRenderLayer;
import top.xfunny.mod.client.sound.SoundPlaybackManager;
import top.xfunny.mod.keymapping.DefaultButtonsKeyMapping;
import top.xfunny.mod.util.TransformPositionX;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import static org.mtr.mapping.mapper.DirectionHelper.FACING;

public class ButtonView extends ImageView {


    private static final Map<ButtonKey, Long> CLICK_TIME_MAP = new ConcurrentHashMap<>();
    private static final long CLICK_COOLDOWN = 200; // 0.2 秒
    private int hoverColor;
    private int pressedColor;
    private int defaultColor;
    private boolean isFocused;
    private boolean isPressed; // 仅由 activate/reset 控制
    private boolean isAlwaysOn;
    private DefaultButtonsKeyMapping keyMapping;

    private final float[] location;
    private final float[] dimension;
    private final float[] uv;
    private boolean wasUseKeyDown = false; // 边缘检测（实例级）

    public ButtonView() {
        location = new float[2];
        dimension = new float[2];
        uv = new float[]{1, 1, 0, 0};
    }

    public void setBasicsAttributes(World world, BlockPos blockPos, DefaultButtonsKeyMapping keyMapping) {
        this.keyMapping = keyMapping;
        super.setBasicsAttributes(world, blockPos);
    }

    public void setBasicsAttributes(World world, BlockPos blockPos) {
        super.setBasicsAttributes(world, blockPos);
    }

    private ButtonKey currentButtonKey = null;

    @Override
    public void render() {

        location[0] = x;
        location[1] = y;
        dimension[0] = width;
        dimension[1] = height;

        final MinecraftClient client = MinecraftClient.getInstance();
        final HitResult hitResult = client.getCrosshairTargetMapped();

        if (hitResult != null && keyMapping != null && world != null && blockPos != null) {

            keyMapping.registerButton(id, location, dimension);

            BlockState blockState = world.getBlockState(blockPos);
            Direction facing = IBlock.getStatePropertySafe(blockState, FACING);

            final Vector3d hitLocation = hitResult.getPos();

            final String inButton = keyMapping.mapping(
                    TransformPositionX.transform(
                            MathHelper.fractionalPart(hitLocation.getXMapped()),
                            MathHelper.fractionalPart(hitLocation.getZMapped()),
                            facing
                    ),
                    MathHelper.fractionalPart(hitLocation.getYMapped())
            );

            final boolean inBlock = Init.newBlockPos(
                    hitLocation.getXMapped(),
                    hitLocation.getYMapped(),
                    hitLocation.getZMapped()
            ).equals(blockPos);

            isFocused = inBlock && Objects.equals(inButton, id);



            boolean isUseKeyDown = client.getOptionsMapped().getKeyUseMapped().isPressed();

            if (isFocused && isUseKeyDown && !wasUseKeyDown) {

                // 只有在真正需要比对或写入冷却时，才延迟初始化这个 Key（或者在 setId 时初始化）
                if (currentButtonKey == null) {
                    currentButtonKey = new ButtonKey(blockPos, id);
                }

                long now = System.currentTimeMillis();
                Long lastTime = CLICK_TIME_MAP.get(currentButtonKey);

                if (lastTime == null || now - lastTime > CLICK_COOLDOWN) {
                    if (onClickListener != null) {
                        onClickListener.onClick(blockPos);
                    }
                    CLICK_TIME_MAP.put(currentButtonKey, now);
                }
            }
            wasUseKeyDown = isUseKeyDown;
        }



        if (isFocused || isPressed || isAlwaysOn) {
            setQueuedRenderLayer(QueuedRenderLayer.LIGHT_TRANSLUCENT);
        }

        setUv(uv);

        color = isPressed
                ? pressedColor
                : isFocused
                  ? hoverColor
                  : defaultColor;

        super.render();
    }



    public void activate() {
        isPressed = true;
        if (onStateChangeListener != null) {
            onStateChangeListener.onActivate(blockPos);
        }
    }

    public void resetLanternSound() {
        isPressed = false;
        if (onStateChangeListener != null) {
            onStateChangeListener.onReset(blockPos);
        }
    }


    @Deprecated
    public void setButtonSound(String sound) {
        // 临时保留，或者直接重定向到新的声音管理器
        SoundPlaybackManager.registerButtonSound(this, sound);
    }

    @Deprecated
    public void setLanternSound(String soundInstruction) {
        SoundPlaybackManager.registerLanternSound(this, soundInstruction);
    }

    public void setOnClickListener(OnClickListener listener) {
        this.onClickListener = listener;
    }

    public void setOnStateChangeListener(OnStateChangeListener listener) {
        this.onStateChangeListener = listener;
    }

    public void setDefaultColor(int defaultColor, boolean isAlwaysOn) {
        this.defaultColor = defaultColor;
        this.isAlwaysOn = isAlwaysOn;
    }

    public void setDefaultColor(int defaultColor) {
        this.defaultColor = defaultColor;
    }

    public void setHoverColor(int hoverColor) {
        this.hoverColor = hoverColor;
    }

    public void setPressedColor(int pressedColor) {
        this.pressedColor = pressedColor;
    }

    public void setFlip(boolean flipVertical, boolean flipHorizontal) {
        if (flipVertical) {// 垂直翻转
            final float tempV = uv[0];
            uv[0] = uv[2];
            uv[2] = tempV;
        }
        if (flipHorizontal) {// 水平反转
            final float tempU = uv[1];
            uv[1] = uv[3];
            uv[3] = tempU;
        }
    }

    public interface OnClickListener {
        void onClick(BlockPos pos);
    }
    public interface OnStateChangeListener {
        void onActivate(BlockPos pos);
        void onReset(BlockPos pos);
    }

    private OnClickListener onClickListener;
    private OnStateChangeListener onStateChangeListener;

    private static class ButtonKey {
        private final int x, y, z;
        private final String buttonId; // 区分同一个方块上的不同按钮（如 "up", "down"）

        public ButtonKey(BlockPos pos, String buttonId) {
            this.x = pos.getX();
            this.y = pos.getY();
            this.z = pos.getZ();
            this.buttonId = buttonId != null ? buttonId : "";
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ButtonKey buttonKey = (ButtonKey) o;
            return x == buttonKey.x && y == buttonKey.y && z == buttonKey.z && buttonId.equals(buttonKey.buttonId);
        }

        @Override
        public int hashCode() {
            // 工业级高效 Hash 算法，避免任何装箱和字符串开销
            int result = x;
            result = 31 * result + y;
            result = 31 * result + z;
            result = 31 * result + buttonId.hashCode();
            return result;
        }
    }
}