package top.xfunny.mod.client.screen.base;

import org.mtr.mapping.holder.MinecraftClient;
import org.mtr.mapping.holder.Screen;
import org.mtr.mapping.mapper.ScreenExtension;
import org.mtr.mod.data.IGui;
import top.xfunny.mod.client.screen.GuiHelper;

public abstract class BaseScreen extends ScreenExtension implements IGui {
    private Screen previousScreen = null;

    public BaseScreen() {
        super();
    }

    public BaseScreen withPreviousScreen(Screen screen) {
        this.previousScreen = screen;
        return this;
    }

    @Override
    protected void init2() {
        super.init2();
        // MTR mapping 层不会自动调用 Screen.init()，子类每次 init2() 添加的控件会累积
        GuiHelper.clearScreenChildren(this);
    }

    /**
     * 返回上一屏（Android finish() 语义）；无上一屏时等同于关闭自身。
     */
    protected void finish() {
        onClose2();
    }

    @Override
    public void onClose2() {
        super.onClose2(); // vanilla Screen.onClose（setScreen(null)），补全 removed 关闭链
        MinecraftClient.getInstance().openScreen(previousScreen);
    }

    @Override
    public void removed2() {
        super.removed2();
        onDestroy();
    }

    /**
     * 屏幕被移除（关闭或被其他屏幕替换）时回调（Android onDestroy() 语义）。
     */
    protected void onDestroy() {
    }
}
