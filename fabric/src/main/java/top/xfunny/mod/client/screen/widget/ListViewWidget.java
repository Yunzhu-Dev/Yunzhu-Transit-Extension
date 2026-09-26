package top.xfunny.mod.client.screen.widget;

import org.jetbrains.annotations.NotNull;
import org.mtr.mapping.holder.MathHelper;
import org.mtr.mapping.holder.MutableText;
import org.mtr.mapping.mapper.ClickableWidgetExtension;
import org.mtr.mapping.mapper.GraphicsHolder;
import org.mtr.mapping.mapper.GuiDrawing;
import top.xfunny.mod.client.screen.ClipStack;
import top.xfunny.mod.client.screen.GuiHelper;

import java.util.ArrayList;
import java.util.List;

public class ListViewWidget extends ClickableWidgetExtension {
    public static final int ENTRY_PADDING = 5;
    public static final int SCROLLBAR_WIDTH = 5;
    private final List<BaseListItem> entryList = new ArrayList<>();
    protected double currentScroll = 0;
    private boolean scrollbarDragging = false;
    private double scrollbarDragOffset = 0;
    private int totalEntryHeight = 0;

    public ListViewWidget() {
        super(0, 0, 0, 0);
    }

    public ListViewWidget(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    public void setXYSize(int x, int y, int width, int height) {
        setX2(x);
        setY2(y);
        setWidth2(width);
        setHeightMapped(height);
        setScroll(currentScroll);
    }

    public void add(MutableText text, MappedWidget widget) {
        add(new ContentItem(text, widget));
    }

    /**
     * 纯文字条目（无内嵌控件）。
     */
    public ContentItem addText(MutableText text) {
        final ContentItem item = new ContentItem(text, null);
        add(item);
        return item;
    }

    /**
     * 纯文字条目，指定对齐方式。
     */
    public ContentItem addText(MutableText text, ContentItem.Alignment alignment) {
        return addText(text).setAlignment(alignment);
    }


    public ContentItem addText(MutableText title, MutableText value) {
        return addText(title).setAlignment(ContentItem.Alignment.JUSTIFIED).setValue(value);
    }

    public void add(BaseListItem listItem) {
        entryList.add(listItem);
        totalEntryHeight += listItem.height;
        setScroll(currentScroll);
    }

    public void addCategory(MutableText text) {
        add(new CategoryItem(text));
    }

    public void clear() {
        entryList.clear();
        totalEntryHeight = 0;
        setScroll(0);
    }

    @Override
    public void render(@NotNull GraphicsHolder graphicsHolder, int mouseX, int mouseY, float tickDelta) {
        GuiDrawing guiDrawing = new GuiDrawing(graphicsHolder);
        GuiHelper.drawRectangle(guiDrawing, getX2(), getY2(), width, height, 0x4C4C4C4C);
        positionWidgets();

        final int contentWidth = width - scrollbarWidth();
        // 内嵌控件由 vanilla 在本列表之后渲染（children 正序），不经过此处裁剪；
        // 视口外的控件靠 positionWidgets 的 hidden() 消除残影与误点击。
        ClipStack.push(getX2(), getY2(), contentWidth, height);
        try {
            int incY = 0;
            for (BaseListItem listItem : entryList) {
                final int entryY = getY2() + incY - (int) currentScroll;
                if (entryY + listItem.height > getY2() && entryY < getY2() + height) {
                    listItem.draw(graphicsHolder, getX2(), entryY, contentWidth, mouseX, mouseY, tickDelta);
                }
                incY += listItem.height;
            }
        } finally {
            ClipStack.pop();
        }
        renderScrollBar(graphicsHolder, mouseX, mouseY, tickDelta);
    }

    /**
     * 每帧统一布局：条目定位含滚动偏移，视口外条目 hidden()（隐藏内嵌控件）。
     */
    private void positionWidgets() {
        final int contentWidth = width - scrollbarWidth();
        int incY = 0;
        for (BaseListItem listItem : entryList) {
            final int entryY = getY2() + incY - (int) currentScroll;
            final boolean intersectsViewport = entryY + listItem.height > getY2() && entryY < getY2() + height;
            if (intersectsViewport) {
                listItem.shown();
                listItem.positionChanged(getX2(), entryY, contentWidth);
            } else {
                listItem.hidden();
            }
            incY += listItem.height;
        }
    }

    // Screen 会向所有 child 广播滚轮事件，必须以指针在列表内为前提，避免多列表互抢或越界滚动。
    @Override
    public boolean mouseScrolled2(double mouseX, double mouseY, double amount) {
        if (!isMouseOver2(mouseX, mouseY)) {
            return false;
        }
        double oldScroll = currentScroll;
        if (contentOverflowed()) {
            amount *= 26;
            setScroll(oldScroll - amount);
        }
        return oldScroll != currentScroll;
    }

    @Override
    public boolean mouseClicked2(double mouseX, double mouseY, int button) {
        // 内嵌控件是 Screen 真子控件，由 vanilla 按 bounds 命中分发（后加入者先命中）；这里只处理滚动条拖拽。
        if (button == 0 && contentOverflowed() && isScrollbarThumbHover(mouseX, mouseY)) {
            scrollbarDragging = true;
            scrollbarDragOffset = mouseY - getScrollbarThumbY();
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseDragged2(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (scrollbarDragging) {
            setScrollFromThumbPosition(mouseY - scrollbarDragOffset);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased2(double mouseX, double mouseY, int button) {
        scrollbarDragging = false;
        return false;
    }

    private void setScrollFromThumbPosition(double thumbTop) {
        int visibleHeight = getHeight2();
        double scrollbarHeight = getScrollbarThumbHeight();
        double trackHeight = visibleHeight - scrollbarHeight;
        if (trackHeight <= 0) {
            return;
        }
        setScroll((thumbTop - getY2()) / trackHeight * (totalEntryHeight - visibleHeight));
    }

    public void renderScrollBar(GraphicsHolder graphicsHolder, int mouseX, int mouseY, float tickDelta) {
        if (!contentOverflowed()) return;

        GuiDrawing guiDrawing = new GuiDrawing(graphicsHolder);
        double thumbY = getScrollbarThumbY();
        double thumbHeight = getScrollbarThumbHeight();

        GuiHelper.drawRectangle(guiDrawing, getX2() + getWidth2() - SCROLLBAR_WIDTH, thumbY, SCROLLBAR_WIDTH, thumbHeight, isScrollbarThumbHover(mouseX, mouseY) ? 0xFFD1D1D1 : 0xFF9F9F9F);
    }

    protected boolean contentOverflowed() {
        return totalEntryHeight > getHeight2();
    }

    private int scrollbarWidth() {
        return contentOverflowed() ? SCROLLBAR_WIDTH : 0;
    }

    private double getScrollbarThumbHeight() {
        int visibleHeight = getHeight2();
        return visibleHeight * ((double) visibleHeight / totalEntryHeight);
    }

    private double getScrollbarThumbY() {
        int visibleHeight = getHeight2();
        double thumbHeight = getScrollbarThumbHeight();
        double bottomOffset = currentScroll / (totalEntryHeight - visibleHeight);
        return getY2() + bottomOffset * (visibleHeight - thumbHeight);
    }

    private boolean isScrollbarThumbHover(double mouseX, double mouseY) {
        if (!contentOverflowed()) {
            return false;
        }
        double thumbY = getScrollbarThumbY();
        double thumbHeight = getScrollbarThumbHeight();
        return mouseX >= getX2() + getWidth2() - SCROLLBAR_WIDTH
                && mouseY >= thumbY
                && mouseX < getX2() + getWidth2()
                && mouseY < thumbY + thumbHeight;
    }

    public void setScroll(double scroll) {
        int maxScroll = Math.max(0, totalEntryHeight - getHeight2());
        currentScroll = MathHelper.clamp(scroll, 0, maxScroll);
    }
}
