package top.xfunny.mod.client.view;

import org.mtr.mapping.holder.*;
import org.mtr.mapping.mapper.GraphicsHolder;
import org.mtr.mod.InitClient;
import org.mtr.mod.block.IBlock;
import org.mtr.mod.client.IDrawing;
import org.mtr.mod.render.QueuedRenderLayer;
import org.mtr.mod.render.StoredMatrixTransformations;

import java.util.ArrayList;
import java.util.List;

import static org.mtr.mapping.mapper.DirectionHelper.FACING;
import static org.mtr.mod.data.IGui.ARGB_WHITE;

public class ImageView implements RenderView {
    // --- 动画相关属性 Start ---
    private final List<Identifier> animationFrames = new ArrayList<>(); // 存储动画序列帧
    private final float[] yawRotationResult = new float[2];
    public float y;
    // 基本属性
    protected String id;
    protected float width, height;
    protected float x;
    protected World world;
    protected BlockPos blockPos;
    protected int color = ARGB_WHITE;
    private StoredMatrixTransformations storedMatrixTransformations;
    private float marginLeft, marginTop, marginRight, marginBottom;
    private Gravity gravity;
    // 默认静态贴图
    private Identifier texture;
    private boolean isAnimated = false; // 是否启用动画
    // --- 动画相关属性 End ---
    private float animationInterval = 10.0f; // 动画帧间隔 (tick)，越小越快。10 tick = 0.5秒
    private float scale;
    private int light = GraphicsHolder.getDefaultLight();
    private float[] uv;
    private QueuedRenderLayer queuedRenderLayer = QueuedRenderLayer.EXTERIOR;
    private boolean needBlink;
    private boolean needYawRotate;
    private float blinkInterval = 0.5f;
    private float yawRotationSpeed = 0.5f;

    public ImageView() {
        this.uv = new float[]{1, 1, 0, 0};
    }

    // Getters and Setters
    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTexture(Identifier texture) {
        this.texture = texture;
        // 设置静态贴图时，关闭动画模式
        this.isAnimated = false;
        this.animationFrames.clear();
    }

    /**
     * 设置动画贴图序列 (核心修改)
     *
     * @param frames   包含所有帧的 Identifier 列表
     * @param interval 每一帧持续的时间 (Ticks)，20 ticks = 1秒
     */
    public void setAnimatedTexture(List<Identifier> frames, float interval) {
        if (frames == null || frames.isEmpty()) {
            this.isAnimated = false;
            return;
        }
        this.animationFrames.clear();
        this.animationFrames.addAll(frames);
        this.animationInterval = interval;
        this.isAnimated = true;
        // 默认将第一帧设为 fallback 贴图
        this.texture = frames.get(0);
    }

    /**
     * 快捷设置动画贴图序列
     * 假设你的贴图命名规则为: namespace:textures/block/my_gif_0.png, my_gif_1.png ...
     *
     * @param namespace  命名空间 (如 "minecraft" 或 你的mod id)
     * @param basePath   贴图路径前缀 (不包含序号和后缀)，如 "textures/block/my_gif_"
     * @param frameCount 总帧数
     * @param interval   每一帧持续的时间 (Ticks)
     */
    public void setAnimatedTexture(String namespace, String basePath, int frameCount, float interval) {
        List<Identifier> frames = new ArrayList<>();
        for (int i = 0; i < frameCount; i++) {
            // 拼接 Identifier, 例如: new Identifier("mymod", "textures/custom/anim_0.png")
            // 注意：MTR Mapping 的 Identifier 构造函数可能略有不同，这里假设为 standard 格式
            frames.add(new Identifier(namespace, basePath + i + ".png"));
        }
        setAnimatedTexture(frames, interval);
    }

    /**
     * 设置尺寸
     *
     * @param width     宽度
     * @param rawWidth  贴图原始宽度
     * @param rawHeight 贴图原始高度
     */
    public void setDimension(float width, float rawWidth, float rawHeight) {
        float scale = rawHeight / rawWidth;
        setWidth(width);
        this.scale = scale;
        calculateDimensions();
    }

    /**
     * 设置尺寸
     *
     * @param width 宽度
     * @param scale 缩放比：贴图原始高度/贴图原始宽度
     */
    public void setDimension(float width, float scale) {
        setWidth(width);
        this.scale = scale;
        calculateDimensions();
    }

    /**
     * 设置尺寸
     * 宽高比为1:1
     *
     * @param width 宽度
     */
    public void setDimension(float width) {
        setWidth(width);
        this.scale = 1;
        calculateDimensions();
    }

    public void setLight(int light) {
        this.light = light;
    }

    public void setQueuedRenderLayer(QueuedRenderLayer queuedRenderLayer) {
        this.queuedRenderLayer = queuedRenderLayer;
    }

    public void setBasicsAttributes(World world, BlockPos blockPos) {
        this.world = world;
        this.blockPos = blockPos;
    }

    // 渲染逻辑
    @Override
    public void render() {
        float gameTick = InitClient.getGameTick();
        BlockState blockState = world.getBlockState(blockPos);
        Direction facing = IBlock.getStatePropertySafe(blockState, FACING);

        StoredMatrixTransformations storedMatrixTransformations1 = storedMatrixTransformations.copy();

        // 闪烁逻辑
        boolean shouldRender = true;
        if (needBlink && blinkInterval > 0) {
            int framesPerCycle = (int) (blinkInterval * 20);
            int currentFrame = (int) (gameTick % framesPerCycle);
            shouldRender = currentFrame < (framesPerCycle / 2); // 半周期亮、半周期灭
        }

        // --- 动画计算逻辑 Start ---
        // 决定当前使用哪张贴图
        Identifier renderTexture = this.texture;

        if (isAnimated && !animationFrames.isEmpty() && animationInterval > 0) {
            int totalFrames = animationFrames.size();
            // 计算当前帧索引： (总时间 / 单帧间隔) % 总帧数
            int currentFrameIndex = (int) ((gameTick / animationInterval) % totalFrames);

            // 安全检查防止越界
            if (currentFrameIndex >= 0 && currentFrameIndex < totalFrames) {
                renderTexture = animationFrames.get(currentFrameIndex);
            }
        }
        // --- 动画计算逻辑 End ---

        if (shouldRender && renderTexture != null) {
            final float[] yawRotationResult = new float[2];

            if (needYawRotate) {
                final float[] scaled = scale(gameTick);
                yawRotationResult[0] = scaled[0];
                yawRotationResult[1] = scaled[1];
            }

            final float width2 = yawRotationResult[0] == 0 ? width : yawRotationResult[0];
            final float x2 = yawRotationResult[1] == 0 ? x : yawRotationResult[1];

            final GraphicsHolder graphicsHolder = DirectRenderer.prepare(queuedRenderLayer, renderTexture, storedMatrixTransformations1);
            if (graphicsHolder != null) {
                IDrawing.drawTexture(
                        graphicsHolder,
                        x2,
                        y,
                        width2,
                        height,
                        uv[0],
                        uv[1],
                        uv[2],
                        uv[3],
                        facing,
                        color,
                        light
                );
                graphicsHolder.pop();
            }
        }

    }

    private float[] scale(float gameTick) {
        float multiplier = (float) Math.sin(gameTick * yawRotationSpeed * 3) * 0.5f + 0.5f;
        float width2 = width * multiplier;
        float x2 = x + (width - width2) * 0.5f;
        yawRotationResult[0] = width2;
        yawRotationResult[1] = x2;

        return yawRotationResult;
    }

    // 计算尺寸
    public void calculateDimensions() {
        this.height = width * scale;
    }

    @Override
    public void setStoredMatrixTransformations(StoredMatrixTransformations storedMatrixTransformations) {
        this.storedMatrixTransformations = storedMatrixTransformations;
    }

    @Override
    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    @Override
    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    @Override
    public float[] getMargin() {
        return new float[]{marginLeft, marginTop, marginRight, marginBottom};
    }

    @Override
    public Gravity getGravity() {
        return gravity;
    }

    @Override
    public void setGravity(Gravity gravity) {
        this.gravity = gravity;
    }

    @Override
    public void setMargin(float left, float top, float right, float bottom) {
        this.marginLeft = left;
        this.marginTop = top;
        this.marginRight = right;
        this.marginBottom = bottom;
    }

    protected void setUv(float[] uv) {
        this.uv = uv;
    }

    public void setFlip(boolean flipVertical, boolean flipHorizontal) {
        if (flipVertical) {
            // 垂直翻转
            final float tempV = uv[0];
            uv[0] = uv[2];
            uv[2] = tempV;
        }
        if (flipHorizontal) {
            // 水平翻转
            final float tempU = uv[1];
            uv[1] = uv[3];
            uv[3] = tempU;
        }
    }


    public void setAnimationBliking(boolean needBlink, float blinkInterval) {
        this.needBlink = needBlink;
        this.blinkInterval = blinkInterval;
    }

    public void setAnimationYawRotation(boolean needYawRotate, float yawRotationSpeed) {
        this.needYawRotate = needYawRotate;
        this.yawRotationSpeed = yawRotationSpeed;
    }

    @Override
    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    // 不适用的方法移到最后
    @Override
    public void setParentDimensions(float parentWidth, float parentHeight) {
        // 不适用
    }

    @Override
    public void calculateLayoutWidth() {
        // 不适用
    }

    @Override
    public void calculateLayoutHeight() {
        // 不适用
    }

    @Override
    public float[] calculateChildGravityOffset(float childWidth, float childHeight, float[] childMargin, Gravity childGravity) {
        return new float[0]; // 不适用
    }

    @Override
    public Object getParentType() {
        return null; // 不适用
    }

    @Override
    public void setParentType(Object thisObject) {
        // 不适用
    }
}