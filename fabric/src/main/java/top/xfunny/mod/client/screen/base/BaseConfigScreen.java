package top.xfunny.mod.client.screen.base;

import org.jetbrains.annotations.NotNull;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.ClickableWidget;
import org.mtr.mapping.holder.MutableText;
import org.mtr.mapping.mapper.ButtonWidgetExtension;
import org.mtr.mapping.mapper.GraphicsHolder;
import org.mtr.mapping.mapper.TextHelper;
import top.xfunny.mod.client.screen.GuiHelper;
import top.xfunny.mod.client.screen.widget.HorizontalWidgetSet;
import top.xfunny.mod.client.screen.widget.ListViewWidget;
import top.xfunny.mod.client.screen.widget.MappedWidget;

import static top.xfunny.mod.client.screen.GuiHelper.MAX_CONTENT_WIDTH;

public abstract class BaseConfigScreen extends TitledScreen {
    protected final ListViewWidget listViewWidget;
    final BlockPos blockPos;
    private final boolean saveMode;
    private final ButtonWidgetExtension saveButton;
    private final ButtonWidgetExtension discardButton;
    private static final int BOTTOM_ROW_HEIGHT = GuiHelper.lineHeight * 2;
    private static final int BOTTOM_ROW_SPACING = 8;
    private static final int BUTTON_WIDTH = 60;

    public BaseConfigScreen(BlockPos blockPos) {
        // 即时生效模式：行内控件点击直接回调，无底部提交栏
        this(blockPos, false);
    }

    public BaseConfigScreen(BlockPos blockPos, boolean saveMode) {
        this.blockPos = blockPos;
        this.saveMode = saveMode;
        this.listViewWidget = new ListViewWidget();
        if (saveMode) {
            this.saveButton = new ButtonWidgetExtension(0, 0, BUTTON_WIDTH, BOTTOM_ROW_HEIGHT, TextHelper.translatable("gui.yte.save"), button -> {
                onSave();
                finish();
            });
            this.discardButton = new ButtonWidgetExtension(0, 0, BUTTON_WIDTH, BOTTOM_ROW_HEIGHT, TextHelper.translatable("gui.yte.discard"), button -> finish());
        } else {
            this.saveButton = null;
            this.discardButton = null;
        }
    }

    @Override
    protected void init2() {
        super.init2();
        int contentWidth = (int) Math.min((width * 0.75), MAX_CONTENT_WIDTH);
        int startX = (width - contentWidth) / 2;
        int startY = TEXT_PADDING * 5;
        int bottomSpace = saveMode ? BOTTOM_ROW_HEIGHT + BOTTOM_ROW_SPACING : 0;
        // JCM 式列表高度，并夹紧到屏幕内（JCM 的 max(160, ...) 在小窗口会溢出）
        int listViewHeight = Math.min((int) Math.max(160, (height - 60) * 0.75), height - startY - bottomSpace - TEXT_PADDING);

        listViewWidget.clear();
        listViewWidget.setXYSize(startX, startY, contentWidth, listViewHeight);
        addChild(new ClickableWidget(listViewWidget));

        if (saveMode) {
            addChild(new ClickableWidget(saveButton));
            addChild(new ClickableWidget(discardButton));
            HorizontalWidgetSet bottomRow = new HorizontalWidgetSet();
            bottomRow.addWidget(new MappedWidget(saveButton));
            bottomRow.addWidget(new MappedWidget(discardButton));
            bottomRow.setXYSize(startX, startY + listViewHeight + BOTTOM_ROW_SPACING, contentWidth, BOTTOM_ROW_HEIGHT);
            bottomRow.positionWidgets();
        }
        addItemConfig();
    }

    @Override
    public void render(@NotNull GraphicsHolder graphicsHolder, int mouseX, int mouseY, float tickDelta) {
        renderBackground(graphicsHolder);
        super.render(graphicsHolder, mouseX, mouseY, tickDelta);
    }

    public MutableText getScreenSubtitle() {
        if (blockPos == null) {
            return TextHelper.literal("");
        }
        return TextHelper.translatable("gui.yte.subtitle", blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    public abstract void addItemConfig();

    /**
     * saveMode 下点击 Save 时回调；即时模式下不会被调用。
     */
    protected void onSave() {
    }
}
