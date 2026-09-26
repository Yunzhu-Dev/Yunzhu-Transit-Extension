package top.xfunny.mod.client.screen.widget;

import org.mtr.mapping.mapper.GraphicsHolder;

public abstract class BaseListItem {
    public final int height;

    public BaseListItem(int height) {
        super();
        this.height = height;
    }

    public BaseListItem() {
        this(26);
    }

    /**
     * 绘制条目自身（背景/悬停/文本/图标）。
     * 内嵌控件是 Screen 真子控件，由 vanilla 在列表之后渲染并分发事件，不在此处理。
     */
    public abstract void draw(GraphicsHolder graphicsHolder, int entryX, int entryY, int entryWidth, int mouseX, int mouseY, float tickDelta);

    /**
     * 定位内嵌控件。entryX/entryY 为含滚动偏移后的条目左上角绝对坐标，entryWidth 为条目可用宽度。
     */
    public abstract void positionChanged(int entryX, int entryY, int entryWidth);

    /**
     * 条目离开列表视口时回调（隐藏内嵌控件，防止视口外残影与误点击）。
     */
    public void hidden() {
    }

    /**
     * 条目回到列表视口时回调。
     */
    public void shown() {
    }
}
