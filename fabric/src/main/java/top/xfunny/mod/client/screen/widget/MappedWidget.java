package top.xfunny.mod.client.screen.widget;

import org.mtr.mapping.mapper.ButtonWidgetExtension;
import org.mtr.mapping.mapper.CheckboxWidgetExtension;
import org.mtr.mapping.mapper.ClickableWidgetExtension;
import org.mtr.mapping.mapper.GraphicsHolder;
import org.mtr.mapping.mapper.SliderWidgetExtension;
import org.mtr.mapping.mapper.TextFieldWidgetExtension;

public class MappedWidget {
    private final Object widget;
    private final Access access;

    public MappedWidget(Object widget) {
        while (widget instanceof MappedWidget) {
            widget = ((MappedWidget) widget).widget;
        }
        if (widget instanceof ButtonWidgetExtension) {
            final ButtonWidgetExtension cast = (ButtonWidgetExtension) widget;
            this.access = new Access() {
                @Override public int getX2() { return cast.getX2(); }
                @Override public int getY2() { return cast.getY2(); }
                @Override public void setX2(int x) { cast.setX2(x); }
                @Override public void setY2(int y) { cast.setY2(y); }
                @Override public int getWidth2() { return cast.getWidth2(); }
                @Override public int getHeight2() { return cast.getHeight2(); }
                @Override public void setWidth2(int width) { cast.setWidth2(width); }
                @Override public boolean getActiveMapped() { return cast.getActiveMapped(); }
                @Override public void setActiveMapped(boolean active) { cast.setActiveMapped(active); }
                @Override public void setVisibleMapped(boolean visible) { cast.setVisibleMapped(visible); }
                @Override public void render(GraphicsHolder graphicsHolder, int mouseX, int mouseY, float tickDelta) { cast.render(graphicsHolder, mouseX, mouseY, tickDelta); }
                @Override public boolean mouseClicked2(double mouseX, double mouseY, int button) { return cast.mouseClicked2(mouseX, mouseY, button); }
            };
        } else if (widget instanceof TextFieldWidgetExtension) {
            final TextFieldWidgetExtension cast = (TextFieldWidgetExtension) widget;
            this.access = new Access() {
                @Override public int getX2() { return cast.getX2(); }
                @Override public int getY2() { return cast.getY2(); }
                @Override public void setX2(int x) { cast.setX2(x); }
                @Override public void setY2(int y) { cast.setY2(y); }
                @Override public int getWidth2() { return cast.getWidth2(); }
                @Override public int getHeight2() { return cast.getHeight2(); }
                @Override public void setWidth2(int width) { cast.setWidth2(width); }
                @Override public boolean getActiveMapped() { return cast.getActiveMapped(); }
                @Override public void setActiveMapped(boolean active) { cast.setActiveMapped(active); }
                @Override public void setVisibleMapped(boolean visible) { cast.setVisibleMapped(visible); }
                @Override public void render(GraphicsHolder graphicsHolder, int mouseX, int mouseY, float tickDelta) { cast.render(graphicsHolder, mouseX, mouseY, tickDelta); }
                @Override public boolean mouseClicked2(double mouseX, double mouseY, int button) { return cast.mouseClicked2(mouseX, mouseY, button); }
            };
        } else if (widget instanceof CheckboxWidgetExtension) {
            final CheckboxWidgetExtension cast = (CheckboxWidgetExtension) widget;
            this.access = new Access() {
                @Override public int getX2() { return cast.getX2(); }
                @Override public int getY2() { return cast.getY2(); }
                @Override public void setX2(int x) { cast.setX2(x); }
                @Override public void setY2(int y) { cast.setY2(y); }
                @Override public int getWidth2() { return cast.getWidth2(); }
                @Override public int getHeight2() { return cast.getHeight2(); }
                @Override public void setWidth2(int width) { cast.setWidth2(width); }
                @Override public boolean getActiveMapped() { return cast.getActiveMapped(); }
                @Override public void setActiveMapped(boolean active) { cast.setActiveMapped(active); }
                @Override public void setVisibleMapped(boolean visible) { cast.setVisibleMapped(visible); }
                @Override public void render(GraphicsHolder graphicsHolder, int mouseX, int mouseY, float tickDelta) { cast.render(graphicsHolder, mouseX, mouseY, tickDelta); }
                @Override public boolean mouseClicked2(double mouseX, double mouseY, int button) { return cast.mouseClicked2(mouseX, mouseY, button); }
            };
        } else if (widget instanceof SliderWidgetExtension) {
            final SliderWidgetExtension cast = (SliderWidgetExtension) widget;
            this.access = new Access() {
                @Override public int getX2() { return cast.getX2(); }
                @Override public int getY2() { return cast.getY2(); }
                @Override public void setX2(int x) { cast.setX2(x); }
                @Override public void setY2(int y) { cast.setY2(y); }
                @Override public int getWidth2() { return cast.getWidth2(); }
                @Override public int getHeight2() { return cast.getHeight2(); }
                @Override public void setWidth2(int width) { cast.setWidth2(width); }
                @Override public boolean getActiveMapped() { return cast.getActiveMapped(); }
                @Override public void setActiveMapped(boolean active) { cast.setActiveMapped(active); }
                @Override public void setVisibleMapped(boolean visible) { cast.setVisibleMapped(visible); }
                @Override public void render(GraphicsHolder graphicsHolder, int mouseX, int mouseY, float tickDelta) { cast.render(graphicsHolder, mouseX, mouseY, tickDelta); }
                @Override public boolean mouseClicked2(double mouseX, double mouseY, int button) { return cast.mouseClicked2(mouseX, mouseY, button); }
            };
        } else if (widget instanceof ClickableWidgetExtension) {
            final ClickableWidgetExtension cast = (ClickableWidgetExtension) widget;
            this.access = new Access() {
                @Override public int getX2() { return cast.getX2(); }
                @Override public int getY2() { return cast.getY2(); }
                @Override public void setX2(int x) { cast.setX2(x); }
                @Override public void setY2(int y) { cast.setY2(y); }
                @Override public int getWidth2() { return cast.getWidth2(); }
                @Override public int getHeight2() { return cast.getHeight2(); }
                @Override public void setWidth2(int width) { cast.setWidth2(width); }
                @Override public boolean getActiveMapped() { return cast.getActiveMapped(); }
                @Override public void setActiveMapped(boolean active) { cast.setActiveMapped(active); }
                @Override public void setVisibleMapped(boolean visible) { cast.setVisibleMapped(visible); }
                @Override public void render(GraphicsHolder graphicsHolder, int mouseX, int mouseY, float tickDelta) { cast.render(graphicsHolder, mouseX, mouseY, tickDelta); }
                @Override public boolean mouseClicked2(double mouseX, double mouseY, int button) { return cast.mouseClicked2(mouseX, mouseY, button); }
            };
        } else {
            throw new IllegalArgumentException("Unsupported widget type: " + (widget == null ? "null" : widget.getClass().getName()));
        }
        this.widget = widget;
    }

    public int getX() {
        return access.getX2();
    }

    public int getY() {
        return access.getY2();
    }

    public int getWidth() {
        return access.getWidth2();
    }

    public int getHeight() {
        return access.getHeight2();
    }

    public boolean getActive() {
        return access.getActiveMapped();
    }

    public void setWidth(int width) {
        access.setWidth2(width);
    }

    public void setX(int newX) {
        access.setX2(newX);
    }

    public void setY(int newY) {
        access.setY2(newY);
    }

    public void render(GraphicsHolder graphicsHolder, int mouseX, int mouseY, float tickDelta) {
        access.render(graphicsHolder, mouseX, mouseY, tickDelta);
    }

    public void setVisible(boolean value) {
        access.setVisibleMapped(value);
    }

    public void setActive(boolean active) {
        access.setActiveMapped(active);
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return access.mouseClicked2(mouseX, mouseY, button);
    }

    private interface Access {
        int getX2();
        int getY2();
        void setX2(int x);
        void setY2(int y);
        int getWidth2();
        int getHeight2();
        void setWidth2(int width);
        boolean getActiveMapped();
        void setActiveMapped(boolean active);
        void setVisibleMapped(boolean visible);
        void render(GraphicsHolder graphicsHolder, int mouseX, int mouseY, float tickDelta);
        boolean mouseClicked2(double mouseX, double mouseY, int button);
    }
}
