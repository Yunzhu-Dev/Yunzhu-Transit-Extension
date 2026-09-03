package top.xfunny.mod.client.screen;

import org.mtr.mapping.mapper.GraphicsHolder;
import org.mtr.mapping.mapper.GuiDrawing;
import org.mtr.mapping.mapper.ScreenExtension;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public final class GuiHelper {
    public static final int MAX_CONTENT_WIDTH = 400;
    public static final int lineHeight = 9;

    private static Field drawContextField;
    private static Method enableScissorMethod;
    private static Method disableScissorMethod;
    private static boolean scissorUnavailable;
    private static Method clearChildrenMethod;
    private static boolean clearChildrenUnavailable;

    private GuiHelper() {
    }

    public static void clearScreenChildren(ScreenExtension screen) {
        if (screen == null) {
            return;
        }
        if (clearChildrenMethod == null && !clearChildrenUnavailable) {
            Class<?> clazz = screen.getClass();
            while (clazz != null) {
                try {
                    clearChildrenMethod = clazz.getDeclaredMethod("clearChildren");
                    break;
                } catch (NoSuchMethodException e) {
                    try {
                        clearChildrenMethod = clazz.getDeclaredMethod("clearWidgets");
                        break;
                    } catch (NoSuchMethodException e2) {
                        clazz = clazz.getSuperclass();
                    }
                }
            }
            if (clearChildrenMethod == null) {
                clearChildrenUnavailable = true;
            } else {
                clearChildrenMethod.setAccessible(true);
            }
        }
        if (clearChildrenMethod != null) {
            try {
                clearChildrenMethod.invoke(screen);
            } catch (ReflectiveOperationException ignored) {
            }
        }
    }

    public static void drawRectangle(GuiDrawing guiDrawing, double x, double y, double width, double height, int color) {
        guiDrawing.beginDrawingRectangle();
        guiDrawing.drawRectangle(x, y, x + width, y + height, color);
        guiDrawing.finishDrawingRectangle();
    }

    // 自 RenderHelper 迁入（原接口已删除）
    public static void scaleToFit(GraphicsHolder graphicsHolder, int targetW, double maxW, boolean keepAspectRatio) {
        scaleToFit(graphicsHolder, targetW, maxW, keepAspectRatio, 0);
    }

    public static void scaleToFit(GraphicsHolder graphicsHolder, double targetW, double maxW, boolean keepAspectRatio, double height) {
        height = height / 2;
        double scaleX = Math.min(1, maxW / targetW);
        if (scaleX < 1) {
            if (keepAspectRatio) {
                graphicsHolder.translate(0, height / 2.0, 0);
                graphicsHolder.scale((float) scaleX, (float) scaleX, (float) scaleX);
                graphicsHolder.translate(0, -height / 2.0, 0);
            } else {
                graphicsHolder.scale((float) scaleX, 1, 1);
            }
        }
    }

    // 反射访问 MTR 未暴露的 drawContext：fabric 为 Yarn 名（DrawContext）、forge 为 Mojmap 名（GuiGraphics），
    // 且 1.19.2 及更早版本无 scissor API。按运行时类型与方法名查找，失败即永久降级为无裁剪。
    public static void enableScissor(GraphicsHolder graphicsHolder, int x1, int y1, int x2, int y2) {
        final Object drawContext = getDrawContext(graphicsHolder);
        if (enableScissorMethod == null && !scissorUnavailable && drawContextField != null) {
            try {
                enableScissorMethod = drawContextField.getType().getMethod("enableScissor", int.class, int.class, int.class, int.class);
            } catch (NoSuchMethodException e) {
                scissorUnavailable = true;
            }
        }
        if (drawContext == null || enableScissorMethod == null) {
            return;
        }
        try {
            enableScissorMethod.invoke(drawContext, x1, y1, x2, y2);
        } catch (ReflectiveOperationException ignored) {
        }
    }

    public static void disableScissor(GraphicsHolder graphicsHolder) {
        final Object drawContext = getDrawContext(graphicsHolder);
        if (disableScissorMethod == null && !scissorUnavailable && drawContextField != null) {
            try {
                disableScissorMethod = drawContextField.getType().getMethod("disableScissor");
            } catch (NoSuchMethodException e) {
                scissorUnavailable = true;
            }
        }
        if (drawContext == null || disableScissorMethod == null) {
            return;
        }
        try {
            disableScissorMethod.invoke(drawContext);
        } catch (ReflectiveOperationException ignored) {
        }
    }

    private static Object getDrawContext(GraphicsHolder graphicsHolder) {
        if (scissorUnavailable) {
            return null;
        }
        if (drawContextField == null) {
            try {
                drawContextField = GraphicsHolder.class.getDeclaredField("drawContext");
                drawContextField.setAccessible(true);
            } catch (NoSuchFieldException | SecurityException e) {
                scissorUnavailable = true;
                return null;
            }
        }
        try {
            return drawContextField.get(graphicsHolder);
        } catch (IllegalAccessException e) {
            scissorUnavailable = true;
            return null;
        }
    }
}
