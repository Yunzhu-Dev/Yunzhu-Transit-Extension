package top.xfunny.mod.client.view;

import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.BlockState;
import org.mtr.mapping.holder.Direction;
import org.mtr.mapping.holder.World;
import org.mtr.mapping.mapper.GraphicsHolder;
import org.mtr.mod.block.IBlock;
import org.mtr.mod.client.IDrawing;
import org.mtr.mod.render.MainRenderer;
import org.mtr.mod.render.QueuedRenderLayer;
import org.mtr.mod.render.StoredMatrixTransformations;
import top.xfunny.mod.client.client_data.DynamicResource;

import top.xfunny.mod.client.resource.TextureList;
import top.xfunny.mod.client.font.FontFeatures;
import top.xfunny.mod.client.font.FontFeature;
import top.xfunny.mod.client.font.FontAnimation;

import java.awt.*;
import java.util.function.Consumer;

import static org.mtr.mapping.mapper.DirectionHelper.FACING;
import static org.mtr.mod.data.IGui.ARGB_WHITE;

public class TextView implements RenderView {
    protected String id;
    protected StoredMatrixTransformations storedMatrixTransformations, storedMatrixTransformations1;
    protected Font font;
    protected int color;
    protected float letterSpacing = 0;
    private FontFeature[] fontFeatures = new FontFeature[0];
    private FontAnimation fontAnimation;
    private int renderedTextLength;
    protected World world;
    protected BlockPos blockPos;
    protected float height;
    protected float width;
    protected int displayTextLength = 2;
    protected float scrollSpeed;
    protected float x, y;
    protected float textX, textY;
    protected String textureId;
    protected float fontSize;
    protected float gameTick;
    protected String text = "";
    protected DynamicResource texture;
    protected float fixedWidth;
    protected float textWidth;
    protected float textHeight;
    protected float marginLeft, marginTop, marginRight, marginBottom;
    protected HorizontalTextAlign horizontalTextAlign = HorizontalTextAlign.RIGHT;//文本默认水平右对齐
    protected VerticalTextAlign verticalTextAlign = VerticalTextAlign.CENTER;//默认垂直居中
    protected AdaptMode adaptMode = AdaptMode.ASPECT_FILL;
    protected Gravity gravity;
    protected Consumer<GraphicsHolder> transformation;
    protected Direction facing;
    private QueuedRenderLayer queuedRenderLayer = QueuedRenderLayer.EXTERIOR_TRANSLUCENT;

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void render() {
        calculateSize();
        calculateTextPositionX();
        calculateTextPositionY();

        BlockState blockState = world.getBlockState(blockPos);
        this.facing = IBlock.getStatePropertySafe(blockState, FACING);
        storedMatrixTransformations1 = storedMatrixTransformations.copy();
        storedMatrixTransformations1.add(graphicsHolder -> {
        });

        if (transformation != null) {
            storedMatrixTransformations1.add(transformation);
        }

        float offset1;
        if (!isFontAnimationRunning() && renderedTextLength > displayTextLength && adaptMode == AdaptMode.ASPECT_FILL) {
            offset1 = (gameTick * scrollSpeed) % 1;
            MainRenderer.scheduleRender(
                    texture.identifier,
                    false,
                    QueuedRenderLayer.LIGHT_TRANSLUCENT,
                    (graphicsHolder, offset) -> {
                        storedMatrixTransformations1.transform(graphicsHolder, offset);
                        IDrawing.drawTexture(graphicsHolder, textX, textY, fixedWidth, textHeight, offset1 + fixedWidth / textWidth, 1, offset1, 0, Direction.UP, ARGB_WHITE, GraphicsHolder.getDefaultLight());
                        graphicsHolder.pop();
                    });
        } else {
            MainRenderer.scheduleRender(
                    texture.identifier,
                    false,
                    QueuedRenderLayer.LIGHT_TRANSLUCENT,
                    (graphicsHolder, offset) -> {
                        storedMatrixTransformations1.transform(graphicsHolder, offset);
                        IDrawing.drawTexture(graphicsHolder, textX, textY, textWidth, textHeight, 1, 1, 0, 0, Direction.UP, ARGB_WHITE, GraphicsHolder.getDefaultLight());
                        graphicsHolder.pop();
                    }
            );
        }
    }

    protected void calculateSize() {
        this.gameTick = org.mtr.mod.InitClient.getGameTick();
        final String frame = fontAnimation == null ? null : fontAnimation.getCurrentFrame(Math.max(0, gameTick) / 20D);
        final String renderedText = frame == null ? text : frame;
        this.renderedTextLength = frame == null ? renderedText.length() : 1;
        this.texture = TextureList.instance.renderFont(textureId, blockPos.asLong(), renderedText, color, font, fontSize, letterSpacing, fontFeatures);
        int rawTextWidth = texture.width;
        int rawTextHeight = texture.height;
        float scale = (float) rawTextHeight / height;

        switch (adaptMode) {
            case FORCE_FIT_WIDTH:
                this.fixedWidth = width;
                this.textWidth = width;
                this.textHeight = rawTextHeight / scale;
                break;

            case ASPECT_FILL:
                this.textWidth = rawTextWidth / scale;//缩放处理后的文本宽度
                this.textHeight = rawTextHeight / scale;//缩放处理后的文本高度
                this.fixedWidth = frame == null ? textWidth / Math.max(1, renderedTextLength) * displayTextLength : textWidth;
                break;

            case FIT_WIDTH:
                final float scaleByWidth = (float) rawTextWidth / width;
                final float scaleByMaxHeight = (float) rawTextHeight / height;

                final float finalScale = Math.max(scaleByWidth, scaleByMaxHeight);

                this.textWidth = rawTextWidth / finalScale;
                this.fixedWidth = textWidth;
                this.textHeight = rawTextHeight / finalScale;
                break;
        }

    }

    @Override
    public void setStoredMatrixTransformations(StoredMatrixTransformations storedMatrixTransformations) {
        this.storedMatrixTransformations = storedMatrixTransformations;
    }

    @Override
    public float getWidth() {
        return width;
    }

    public void setWidth(float width) { //设置初始宽度
        this.width = width;
    }

    @Override
    public float getHeight() {
        return height;
    }

    public void setHeight(float height) { //设置初始高度
        this.height = height;
    }

    public void setTextAlign(HorizontalTextAlign horizontalTextAlign) {
        this.horizontalTextAlign = horizontalTextAlign;
    }

    public void setTextAlign(HorizontalTextAlign horizontalTextAlign, VerticalTextAlign verticalTextAlign) {
        this.horizontalTextAlign = horizontalTextAlign;
        this.verticalTextAlign = verticalTextAlign;
    }

    public void setLetterSpacing(float letterSpacing) {
        this.letterSpacing = letterSpacing;
    }

    /** Static imports: setFontFeatures(ss01, cv02(2)). Empty arguments disable features. */
    public void setFontFeatures(FontFeature... features) {
        this.fontFeatures = FontFeatures.normalize(features);
    }

    /** Configure a stopped animation. Call startFontAnimation() to play. */
    public void setFontAnimation(String firstCodePoint, String lastCodePoint, double framesPerSecond) {
        this.fontAnimation = new FontAnimation(firstCodePoint, lastCodePoint, framesPerSecond);
    }

    /** Preserve playback across rebuilt views, identified by owner and this view's stable texture ID. */
    public void setFontAnimation(Object owner, String firstCodePoint, String lastCodePoint, double framesPerSecond) {
        this.fontAnimation = FontAnimation.forScreen(owner, textureId, firstCodePoint, lastCodePoint, framesPerSecond);
    }

    public void startFontAnimation() {
        if (fontAnimation == null) throw new IllegalStateException("Configure a font animation before starting it");
        fontAnimation.start(Math.max(0, org.mtr.mod.InitClient.getGameTick()) / 20D);
    }

    /** Restore ordinary text, keeping the range available for another start from frame one. */
    public void stopFontAnimation() {
        if (fontAnimation != null) fontAnimation.stop();
    }

    public boolean isFontAnimationRunning() {
        return fontAnimation != null && fontAnimation.isRunning();
    }

    public void clearFontAnimation() {
        stopFontAnimation();
        this.fontAnimation = null;
    }

    public void setAdaptMode(AdaptMode adaptMode) {
        this.adaptMode = adaptMode;
    }

    public void setQueuedRenderLayer(QueuedRenderLayer queuedRenderLayer) {
        this.queuedRenderLayer = queuedRenderLayer;
    }

    protected void calculateTextPositionX() {
        switch (horizontalTextAlign) {
            case LEFT:
                textX = x + width - (renderedTextLength > displayTextLength ? (displayTextLength != 0 ? fixedWidth : textWidth) : textWidth);
                break;
            case CENTER:
                textX = x + width / 2 - (renderedTextLength > displayTextLength ? (displayTextLength != 0 ? fixedWidth : textWidth) : textWidth) / 2;
                break;
            case RIGHT:
                textX = x;
                break;
        }
    }

    protected void calculateTextPositionY() {
        switch (verticalTextAlign) {
            case TOP:
                textY = y + height - textHeight;
                break;
            case CENTER:
                textY = y + height / 2 - textHeight / 2;
                break;
            case BOTTOM:
                textY = y;
                break;
        }
    }

    @Override
    public void setParentDimensions(float parentWidth, float parentHeight) {//用于设置子控件约束空间
    }

    @Override
    public void calculateLayoutWidth() {

    }

    @Override
    public void calculateLayoutHeight() {

    }

    @Override
    public float[] calculateChildGravityOffset(float childWidth, float childHeight, float[] childMargin, Gravity childGravity) {
        return new float[0];
    }

    @Override
    public Object getParentType() {
        return null;
    }

    @Override
    public void setParentType(Object thisObject) {

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

    public int getTextLength() {
        return isFontAnimationRunning() ? 1 : text.length();
    }

    @Override
    public void setMargin(float left, float top, float right, float bottom) {
        this.marginLeft = left;
        this.marginTop = top;
        this.marginRight = right;
        this.marginBottom = bottom;
    }

    @Override
    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }


    public void setDisplayLength(int textSize, float scrollSpeed) {
        this.displayTextLength = textSize;
        this.scrollSpeed = scrollSpeed;
    }


    public void setBasicsAttributes(World world, BlockPos blockPos, Font font, float fontSize, int color) {
        this.world = world;
        this.blockPos = blockPos;
        this.font = font;
        this.fontSize = fontSize;
        this.color = color;
    }

    public void setTextureId(String textureId) {
        this.textureId = textureId;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void addStoredMatrixTransformations(Consumer<GraphicsHolder> transformation) {
        this.transformation = transformation;
    }

    public enum HorizontalTextAlign {
        LEFT,
        CENTER,
        RIGHT
    }

    public enum VerticalTextAlign {
        TOP,
        CENTER,
        BOTTOM
    }

    public enum AdaptMode {
        FORCE_FIT_WIDTH,
        ASPECT_FILL,
        FIT_WIDTH
    }
}
