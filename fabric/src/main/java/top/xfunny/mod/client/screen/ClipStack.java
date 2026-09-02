package top.xfunny.mod.client.screen;

import org.mtr.mapping.holder.MinecraftClient;
import org.lwjgl.opengl.GL11;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

/**
 * GUI 坐标系的 scissor 裁剪栈（参考 JCM ClipStack）。
 * push 时与栈顶矩形取交后生效，pop 恢复上一级；
 * 直接走 GL11 而非反射 DrawContext#enableScissor，1.19.2 及更早版本同样可用。
 */
public final class ClipStack {
    private static final List<Rectangle> STACK = new ArrayList<>();

    private ClipStack() {
    }

    public static void push(int x, int y, int width, int height) {
        if (!STACK.isEmpty()) {
            final Rectangle parent = STACK.get(STACK.size() - 1);
            final int newX = Math.max(parent.x, x);
            final int newY = Math.max(parent.y, y);
            final int newRight = Math.min(parent.x + parent.width, x + width);
            final int newBottom = Math.min(parent.y + parent.height, y + height);
            x = newX;
            y = newY;
            width = Math.max(0, newRight - newX);
            height = Math.max(0, newBottom - newY);
        }
        STACK.add(new Rectangle(x, y, width, height));
        enableClip(x, y, width, height);
    }

    public static void pop() {
        if (STACK.isEmpty()) {
            throw new IllegalStateException("No more clip stack to be popped!");
        }
        STACK.remove(STACK.size() - 1);
        if (STACK.isEmpty()) {
            disableClip();
        } else {
            final Rectangle parent = STACK.get(STACK.size() - 1);
            enableClip(parent.x, parent.y, parent.width, parent.height);
        }
    }

    private static void enableClip(int x, int y, int width, int height) {
        final double scale = MinecraftClient.getInstance().getWindow().getScaleFactor();
        final int framebufferHeight = MinecraftClient.getInstance().getWindow().getFramebufferHeight();
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        // GL 视口原点在左下，GUI 坐标原点在左上，需翻转 Y
        GL11.glScissor((int) (x * scale), (int) (framebufferHeight - (y + height) * scale), (int) (width * scale), (int) (height * scale));
    }

    private static void disableClip() {
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }
}
