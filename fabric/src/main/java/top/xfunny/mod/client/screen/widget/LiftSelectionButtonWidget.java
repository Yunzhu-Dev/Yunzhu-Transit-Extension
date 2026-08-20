package top.xfunny.mod.client.screen.widget;

import org.mtr.mapping.holder.MutableText;
import org.mtr.mapping.mapper.ButtonWidgetExtension;
import org.mtr.mapping.mapper.GraphicsHolder;
import org.mtr.mapping.mapper.GuiDrawing;
import org.mtr.mod.data.IGui;

public final class LiftSelectionButtonWidget extends ButtonWidgetExtension {

    private boolean lit;

    public LiftSelectionButtonWidget(int x, int y, int width, int height, MutableText message,
            org.mtr.mapping.holder.PressAction pressAction) {
        super(x, y, width, height, message, pressAction);
    }

    public void setLit(boolean lit) {
        this.lit = lit;
    }

    @Override
    public void render(GraphicsHolder graphicsHolder, int mouseX, int mouseY, float delta) {
        if (!getVisibleMapped()) {
            return;
        }

        final boolean active = getActiveMapped();
        final int x = getX2();
        final int y = getY2();
        final GuiDrawing guiDrawing = new GuiDrawing(graphicsHolder);
        guiDrawing.beginDrawingRectangle();
        guiDrawing.drawRectangle(x + 6, y + 6, x + 14, y + 14,
                lit ? 0xFFFF0000 : active ? 0xFF000000 : 0xFF444444);
        guiDrawing.finishDrawingRectangle();

        final String text = getMessage2().getString();
        final int textWidth = GraphicsHolder.getTextWidth(text);
        final int availableWidth = Math.max(getWidth2() - 26, 1);
        graphicsHolder.push();
        graphicsHolder.translate(x + 20, 0, 0);
        if (textWidth > availableWidth) {
            graphicsHolder.scale((float) availableWidth / textWidth, 1, 1);
        }
        graphicsHolder.drawText(text, 0, y + IGui.TEXT_PADDING,
                active ? IGui.ARGB_WHITE : 0xFF777777, false, GraphicsHolder.getDefaultLight());
        graphicsHolder.pop();
    }
}
