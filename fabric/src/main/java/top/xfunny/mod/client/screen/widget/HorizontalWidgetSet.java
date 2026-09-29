package top.xfunny.mod.client.screen.widget;

import java.util.ArrayList;
import java.util.List;

/**
 * 单行控件布局容器（参考 JCM HorizontalWidgetSet）。
 * 只负责把加入的控件按可用宽度均匀摆放；渲染与点击由各控件作为 Screen 子控件完成，
 * 因此本类自身不是 widget、不参与事件分发。
 */
public class HorizontalWidgetSet {
    public static final int WIDGET_X_MARGIN = 2;

    private final List<MappedWidget> widgets = new ArrayList<>();
    private int x, y, width, height;

    public void setXYSize(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void addWidget(MappedWidget widget) {
        widgets.add(widget);
    }

    public void positionWidgets() {
        if (widgets.isEmpty()) {
            return;
        }
        final int slot = width / widgets.size();
        for (int i = 0; i < widgets.size(); i++) {
            final MappedWidget widget = widgets.get(i);
            final int slotX = x + i * slot;
            widget.setX(slotX + (slot - widget.getWidth()) / 2 + WIDGET_X_MARGIN);
            widget.setY(y + (height - widget.getHeight()) / 2);
        }
    }
}
