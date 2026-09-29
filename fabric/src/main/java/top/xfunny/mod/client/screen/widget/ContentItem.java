package top.xfunny.mod.client.screen.widget;

import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.holder.MutableText;
import org.mtr.mapping.mapper.GraphicsHolder;
import org.mtr.mapping.mapper.GuiDrawing;
import top.xfunny.mod.client.screen.GuiHelper;

import static top.xfunny.mod.client.screen.widget.ListViewWidget.ENTRY_PADDING;

public class ContentItem extends BaseListItem {

    public enum Alignment {
        LEFT, CENTER, JUSTIFIED
    }

    private static final int TEXT_COLOR_ENABLED = 0xFFFFFFFF;
    private static final int TEXT_COLOR_DISABLED = 0xFF808080;

    public final MutableText title;
    public final MappedWidget widget;
    private Identifier textureResource;
    private boolean hasIcon;
    private double hoverOpacity = 0;
    private Alignment alignment = Alignment.LEFT;
    private MutableText valueText;
    private boolean enabled = true;

    public ContentItem(MutableText title, MappedWidget widget, int height) {
        super(height);
        this.title = title;
        this.widget = widget;
    }

    public ContentItem(MutableText title, MappedWidget widget) {
        this(title, widget, 26);
    }

    public ContentItem setIcon(Identifier textureResource) {
        if (textureResource != null) {
            this.textureResource = textureResource;
            hasIcon = true;
        } else {
            hasIcon = false;
        }
        return this;
    }

    public boolean hasIcon() {
        return hasIcon;
    }

    public ContentItem setAlignment(Alignment alignment) {
        this.alignment = alignment;
        return this;
    }

    public ContentItem setValue(MutableText valueText) {
        this.valueText = valueText;
        return this;
    }

    public ContentItem setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (widget != null) {
            widget.setActive(enabled);
        }
        return this;
    }

    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void positionChanged(int entryX, int entryY, int entryWidth) {
        if (widget != null) {
            widget.setX(entryX + entryWidth - ENTRY_PADDING - widget.getWidth());
            widget.setY(entryY + (height - widget.getHeight()) / 2);
        }
    }

    @Override
    public void hidden() {
        if (widget != null) {
            widget.setVisible(false);
        }
    }

    @Override
    public void shown() {
        if (widget != null) {
            widget.setVisible(true);
        }
    }

    @Override
    public void draw(GraphicsHolder graphicsHolder, int entryX, int entryY, int entryWidth, int mouseX, int mouseY, float tickDelta) {
        drawBackground(graphicsHolder, entryX, entryY, entryWidth, mouseX, mouseY, tickDelta);
        drawListEntryDescription(graphicsHolder, entryX, entryY, entryWidth);
    }

    private void drawListEntryDescription(GraphicsHolder graphicsHolder, int entryX, int entryY, int entryWidth) {
        int textHeight = 9;
        int iconSize = hasIcon() ? height - ENTRY_PADDING : 0;
        int textY = (height / 2) - (textHeight / 2) - (ENTRY_PADDING / 2);
        int reservedRight = widget != null ? widget.getWidth() + ENTRY_PADDING : 0;
        int available = Math.max(1, entryWidth - ENTRY_PADDING * 2 - iconSize - reservedRight);
        final int textColor = enabled ? TEXT_COLOR_ENABLED : TEXT_COLOR_DISABLED;

        graphicsHolder.push();
        graphicsHolder.translate(entryX, entryY, 0);
        graphicsHolder.translate(ENTRY_PADDING, ENTRY_PADDING / 2.0, 0);

        if (hasIcon()) {
            GuiDrawing guiDrawing = new GuiDrawing(graphicsHolder);
            guiDrawing.beginDrawingTexture(textureResource);
            guiDrawing.drawTexture(0F, 0F, iconSize, iconSize, 0F, 0F, 1F, 1F);
            guiDrawing.finishDrawingTexture();
            graphicsHolder.translate(iconSize + ENTRY_PADDING, 0, 0);
        }

        if (alignment == Alignment.CENTER) {
            final int titleWidth = GraphicsHolder.getTextWidth(title);
            final int drawnWidth = Math.min(titleWidth, available);
            graphicsHolder.push();
            graphicsHolder.translate((available - drawnWidth) / 2.0, 0, 0);
            drawScaledText(graphicsHolder, title, textY, available, textColor);
            graphicsHolder.pop();
        } else if (alignment == Alignment.JUSTIFIED) {
            if (valueText != null) {
                final int valueWidth = GraphicsHolder.getTextWidth(valueText);
                final int titleMaxWidth = Math.max(1, available - Math.min(valueWidth, available));
                drawScaledText(graphicsHolder, title, textY, titleMaxWidth, textColor);
                graphicsHolder.push();
                graphicsHolder.translate(available - Math.min(valueWidth, available), 0, 0);
                drawScaledText(graphicsHolder, valueText, textY, available, textColor);
                graphicsHolder.pop();
            } else {
                drawScaledText(graphicsHolder, title, textY, available, textColor);
            }
        } else {
            drawScaledText(graphicsHolder, title, textY, available, textColor);
        }

        graphicsHolder.pop();
    }

    private void drawScaledText(GraphicsHolder graphicsHolder, MutableText text, int textY, int maxWidth, int color) {
        GuiHelper.scaleToFit(graphicsHolder, GraphicsHolder.getTextWidth(text), maxWidth, true);
        graphicsHolder.drawText(text, 0, textY, color, true, GraphicsHolder.getDefaultLight());
    }

    private void drawBackground(GraphicsHolder graphicsHolder, int entryX, int entryY, int entryWidth, int mouseX, int mouseY, float tickDelta) {
        if (!enabled) {
            hoverOpacity = 0;
            return;
        }
        double highlightFadeSpeed = (tickDelta / 4);
        boolean entryHovered = mouseX >= entryX && mouseY >= entryY && mouseX < entryX + entryWidth && mouseY < entryY + this.height;
        hoverOpacity = entryHovered ? Math.min(1, hoverOpacity + highlightFadeSpeed) : Math.max(0, hoverOpacity - highlightFadeSpeed);

        if (hoverOpacity > 0) {
            GuiDrawing guiDrawing = new GuiDrawing(graphicsHolder);
            drawListEntryHighlight(guiDrawing, entryX, entryY, entryWidth, height);
        }
    }

    private void drawListEntryHighlight(GuiDrawing guiDrawing, int x, int y, int width, int height) {
        int highlightAlpha = (int) (100 * hoverOpacity);
        int highlightColor = (highlightAlpha << 24) | (150 << 16) | (150 << 8) | 150;

        GuiHelper.drawRectangle(guiDrawing, x, y, width, height, highlightColor);
    }
}