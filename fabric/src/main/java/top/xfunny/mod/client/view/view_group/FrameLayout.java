package top.xfunny.mod.client.view.view_group;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.Direction;
import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.holder.World;
import org.mtr.mapping.mapper.GraphicsHolder;
import org.mtr.mod.Init;
import org.mtr.mod.client.IDrawing;
import org.mtr.mod.render.MainRenderer;
import org.mtr.mod.render.QueuedRenderLayer;
import org.mtr.mod.render.StoredMatrixTransformations;
import top.xfunny.mod.client.view.Gravity;
import top.xfunny.mod.client.view.LayoutSize;
import top.xfunny.mod.client.view.RenderView;

import java.util.function.Consumer;

import static org.mtr.mod.data.IGui.SMALL_OFFSET;

public class FrameLayout implements RenderView {

    private final ObjectArrayList<RenderView> children = new ObjectArrayList<>();
    private String id;
    private StoredMatrixTransformations storedMatrixTransformations;
    private float marginLeft, marginTop, marginRight, marginBottom;
    private float x, y;
    private float parentWidth, parentHeight;
    private float width, height;
    private Gravity gravity;
    private LayoutSize widthType, heightType;
    private Object parentType;
    private float coordinateOriginX, coordinateOriginY;
    private int backgroundColor;
    private World world;
    private BlockPos blockPos;
    private Consumer<GraphicsHolder> transformation;

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public void render() {
        calculateLayoutWidth();
        calculateLayoutHeight();
        calculateSelfCoordinateOrigin();


        if (transformation != null) {
            storedMatrixTransformations.add(transformation);
        }

        if (backgroundColor != 0) {
            StoredMatrixTransformations storedMatrixTransformations1 = storedMatrixTransformations.copy();
            storedMatrixTransformations1.add(graphicsHolder -> {
                graphicsHolder.translate(0, 0, -SMALL_OFFSET);
            });
            MainRenderer.scheduleRender(
                    new Identifier(Init.MOD_ID, "textures/block/white.png"),
                    false,
                    QueuedRenderLayer.LIGHT_TRANSLUCENT,
                    (graphicsHolder, offset) -> {
                        storedMatrixTransformations1.transform(graphicsHolder, offset);
                        IDrawing.drawTexture(graphicsHolder, x, y, width, height, 0, 0, 1, 1, Direction.UP, backgroundColor, 15);
                        graphicsHolder.pop();
                    });
        }

        StoredMatrixTransformations storedMatrixTransformations2 = storedMatrixTransformations.copy();
        storedMatrixTransformations2.add(graphicsHolder -> {
            graphicsHolder.translate(0, 0, (backgroundColor != 0 ? 2 : 0) * -SMALL_OFFSET);
        });


        for (RenderView child : children) {
            float[] margin = child.getMargin();
            Gravity childGravity = child.getGravity();
            child.setParentType(this);
            child.setStoredMatrixTransformations(storedMatrixTransformations2);
            child.setParentDimensions(width, height);
            child.calculateLayoutWidth();
            child.calculateLayoutHeight();
            float[] childGravityPositionOffset = calculateChildGravityOffset(child.getWidth(), child.getHeight(), margin, childGravity);
            child.setPosition(coordinateOriginX + childGravityPositionOffset[0], coordinateOriginY + height - margin[1] - child.getHeight() + childGravityPositionOffset[1]);
            child.render();

            storedMatrixTransformations2.add(graphicsHolder -> {
                graphicsHolder.translate(0, 0, -SMALL_OFFSET / 4);
            });
        }
    }

    public void addChild(RenderView child) {
        children.add(child);
    }

    @Override
    public void setStoredMatrixTransformations(StoredMatrixTransformations storedMatrixTransformations) {
        this.storedMatrixTransformations = storedMatrixTransformations;
    }

    @Override
    public float getWidth() {
        return width;
    }

    public void setWidth(LayoutSize widthType) {
        this.widthType = widthType;
    }

    @Override
    public float getHeight() {
        return height;
    }

    public void setHeight(LayoutSize heightType) {
        this.heightType = heightType;
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

    @Override
    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public void setParentDimensions(float parentWidth, float parentHeight) {
        this.parentWidth = parentWidth;
        this.parentHeight = parentHeight;
    }

    @Override
    public void calculateLayoutWidth() {
        switch (widthType) {
            case WRAP_CONTENT:
                float maxWidth = 0;
                for (RenderView child : children) {
                    float[] margin = child.getMargin();
                    child.calculateLayoutWidth();
                    maxWidth = Math.max(maxWidth, child.getWidth() + margin[0] + margin[2]);
                }
                width = maxWidth;
                break;
            case MATCH_PARENT:
                width = parentWidth;
                break;
            default:
                width = widthType.ordinal();
                break;
        }
    }

    @Override
    public void calculateLayoutHeight() {
        switch (heightType) {
            case WRAP_CONTENT:
                float maxHeight = 0;
                for (RenderView child : children) {
                    float[] margin = child.getMargin();
                    child.calculateLayoutHeight();
                    maxHeight = Math.max(maxHeight, child.getHeight() + margin[1] + margin[3]);
                }
                height = maxHeight;
                break;
            case MATCH_PARENT:
                height = parentHeight;
                break;
            default:
                height = heightType.ordinal();
                break;
        }
    }


    @Override
    public float[] calculateChildGravityOffset(float childWidth, float childHeight, float[] childMargin, Gravity childGravity) {
        float[] offset = new float[2];
        if (childGravity == null) {
            offset = new float[]{width / 2 - childWidth - childMargin[0], height - childHeight - childMargin[2]};
        } else {
            switch (childGravity) {
                case START:
                    offset = new float[]{width / 2 - childWidth - childMargin[0], 0};
                    break;
                case CENTER_VERTICAL:
                    offset = new float[]{0, -(height - childHeight) / 2};
                    break;
                case CENTER_HORIZONTAL:
                    offset = new float[]{-childWidth / 2, 0};
                    break;
                case CENTER:
                    offset = new float[]{-childWidth / 2, -(height - childHeight) / 2};
                    break;
                case END:
                    offset = new float[]{-width / 2 + childMargin[2], 0};
                    break;
                case TOP:
                    offset = new float[]{0, -height / 2 + childMargin[1]};
                    break;
                case BOTTOM:
                    offset = new float[]{0, height};
                    break;
            }
        }
        return offset;
    }


    @Override
    public Object getParentType() {
        return parentType;
    }

    @Override
    public void setParentType(Object thisObject) {
        this.parentType = thisObject;
    }

    public ObjectArrayList<RenderView> getChildren() {
        return children;
    }

    private void calculateSelfCoordinateOrigin() {
        this.coordinateOriginX = x + width / 2;
        this.coordinateOriginY = y;
    }

    public void addStoredMatrixTransformations(Consumer<GraphicsHolder> transformation) {
        this.transformation = transformation;
    }

    public void setBackgroundColor(int color) {
        this.backgroundColor = color;
    }

    public void setBasicsAttributes(World world, BlockPos blockPos) {
        this.world = world;
        this.blockPos = blockPos;
    }
}

