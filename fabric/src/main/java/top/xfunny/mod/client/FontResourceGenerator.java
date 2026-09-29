package top.xfunny.mod.client;

import org.mtr.core.tool.Utilities;
import org.mtr.mapping.holder.NativeImage;
import org.mtr.mapping.holder.NativeImageFormat;
import org.mtr.mod.config.Config;
import org.mtr.mod.data.IGui;
import top.xfunny.mod.client.font.FontFeatureLayout;
import top.xfunny.mod.client.font.FontFeature;
import top.xfunny.mod.client.font.GsubParser;

import java.awt.*;
import java.awt.font.GlyphVector;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.AbstractMap;
import java.util.Locale;
import java.util.Map;

public class FontResourceGenerator implements IGui {

    public static NativeImage generateNativeImage(String text, int textColor, Font font, float fontSize, int padding, float letterSpacing) {
        return generateNativeImage(text, textColor, font, fontSize, padding, letterSpacing, GsubParser.EMPTY, new FontFeature[0]);
    }

    public static NativeImage generateNativeImage(String text, int textColor, Font font, float fontSize, int padding, float letterSpacing,
                                                  GsubParser substitutions, FontFeature... features) {
        // 绝对不改动原有的基础缩放和画布大小，保证所有配件渲染正常
        int baseScale = (int) Math.pow(2, Config.getClient().getDynamicTextureResolution() + 5);
        float sizeScaleFactor = 0.8f;
        int actualScale = Math.round(baseScale * sizeScaleFactor);

        try {
            final int[] dimensions = new int[2];
            float scaledFontSize = (float) actualScale / 8 * fontSize;

            final Map.Entry<int[], byte[]> textInformation = getTextPixels(
                    text.toUpperCase(Locale.ENGLISH),
                    dimensions,
                    scaledFontSize,
                    padding,
                    font,
                    Math.round(letterSpacing * sizeScaleFactor),
                    substitutions, features
            );

            // 严格维持 1.5F 的高度比例
            final int totalWidth = dimensions[0];
            final int totalHeight = Math.round(actualScale * 1.5F);

            final NativeImage nativeImage = new NativeImage(NativeImageFormat.getAbgrMapped(), totalWidth, totalHeight, true);
            nativeImage.fillRect(0, 0, totalWidth, totalHeight, 0);

            // 居中对齐，多出来的 64 像素透明边缘会自动上下均分，不会改变字的相对位置
            drawString(nativeImage, textInformation.getValue(), totalWidth / 2, totalHeight / 2, dimensions,
                    IGui.HorizontalAlignment.CENTER, IGui.VerticalAlignment.CENTER,
                    0x00000000, textColor, false);

            return nativeImage;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Map.Entry<int[], byte[]> getTextPixels(String text, int[] dimensions, float fontSize, int padding, Font font, int letterSpacing,
                                                          GsubParser substitutions, FontFeature[] features) {
        try {
            BufferedImage tempImage = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_GRAY);
            Graphics2D g2d = tempImage.createGraphics();
            Font renderFont = font.deriveFont(Font.PLAIN, fontSize);
            g2d.setFont(renderFont);

            FontMetrics metrics = g2d.getFontMetrics();
            boolean useFeatures = false;
            for (FontFeature feature : features) useFeatures |= substitutions.supports(feature.getTag());
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            final GlyphVector glyphs = useFeatures ? FontFeatureLayout.create(renderFont, g2d.getFontRenderContext(), text, substitutions, features, letterSpacing) : null;
            final Rectangle2D glyphBounds = glyphs == null ? null : glyphs.getVisualBounds();
            final int leftBearing = glyphBounds == null ? 0 : (int) Math.floor(Math.min(0, glyphBounds.getMinX()));
            int textWidth = 0;
            if (glyphs != null) {
                textWidth = (int) Math.ceil(Math.max(glyphBounds.getMaxX(), glyphs.getGlyphPosition(glyphs.getNumGlyphs()).getX())) - leftBearing;
            } else if (letterSpacing == 0) {
                textWidth = metrics.stringWidth(text);
            } else {
                for (char c : text.toCharArray()) {
                    textWidth += metrics.charWidth(c) + letterSpacing;
                }
                textWidth -= letterSpacing;
            }

            int textHeight = metrics.getHeight();
            g2d.dispose();
            tempImage.flush();

            // 【修改点】：直接给 64 像素的超大安全缓冲（上下各 32 像素）
            int vBuffer = 64;

            int calculatedWidth = Math.max(1, textWidth + 2 * padding);
            int calculatedHeight = textHeight + 2 * padding + vBuffer;

            dimensions[0] = calculatedWidth;
            dimensions[1] = calculatedHeight;

            BufferedImage textImage = new BufferedImage(calculatedWidth, calculatedHeight, BufferedImage.TYPE_BYTE_GRAY);
            g2d = textImage.createGraphics();
            g2d.setFont(renderFont);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2d.setColor(Color.WHITE);

            int x = padding;
            // 【修改点】：基线直接硬下移 32 像素，这下任何字体都绝对不可能突破顶部了
            int y = padding + metrics.getAscent() + 32;

            if (glyphs != null) {
                g2d.drawGlyphVector(glyphs, x - leftBearing, y);
            } else if (letterSpacing == 0) {
                g2d.drawString(text, x, y);
            } else {
                for (char c : text.toCharArray()) {
                    g2d.drawString(String.valueOf(c), x, y);
                    x += metrics.charWidth(c) + letterSpacing;
                }
            }

            g2d.dispose();
            byte[] pixels = ((DataBufferByte) textImage.getRaster().getDataBuffer()).getData();
            textImage.flush();

            return new AbstractMap.SimpleEntry<>(dimensions, pixels);
        } catch (Exception e) {
            top.xfunny.mod.Init.LOGGER.error("Unable to rasterize font text", e);
            dimensions[0] = 0;
            dimensions[1] = 0;
            return new AbstractMap.SimpleEntry<>(dimensions, new byte[0]);
        }
    }

    private static void drawString(NativeImage nativeImage, byte[] pixels, int x, int y, int[] textDimensions,
                                   IGui.HorizontalAlignment horizontalAlignment,
                                   IGui.VerticalAlignment verticalAlignment,
                                   int backgroundColor, int textColor, boolean rotate90) {
        final int textR = (textColor >> 16) & 0xFF;
        final int textG = (textColor >> 8) & 0xFF;
        final int textB = textColor & 0xFF;

        if (((backgroundColor >> 24) & 0xFF) > 0) {
            for (int drawY = 0; drawY < textDimensions[rotate90 ? 0 : 1]; drawY++) {
                for (int drawX = 0; drawX < textDimensions[rotate90 ? 1 : 0]; drawX++) {
                    drawPixelSafe(nativeImage,
                            (int) horizontalAlignment.getOffset(drawX + x, textDimensions[rotate90 ? 1 : 0]),
                            (int) verticalAlignment.getOffset(drawY + y, textDimensions[rotate90 ? 0 : 1]),
                            backgroundColor);
                }
            }
        }

        int drawX = 0;
        int drawY = rotate90 ? textDimensions[0] - 1 : 0;

        for (int i = 0; i < textDimensions[0] * textDimensions[1]; i++) {
            final int alpha = pixels[i] & 0xFF;
            if (alpha > 0) {
                final int xPos = (int) horizontalAlignment.getOffset(x + drawX, textDimensions[rotate90 ? 1 : 0]);
                final int yPos = (int) verticalAlignment.getOffset(y + drawY, textDimensions[rotate90 ? 0 : 1]);

                if (Utilities.isBetween(xPos, 0, nativeImage.getWidth() - 1) &&
                        Utilities.isBetween(yPos, 0, nativeImage.getHeight() - 1)) {

                    final int existingPixel = nativeImage.getColor(xPos, yPos);
                    final int existingA = (existingPixel >> 24) & 0xFF;

                    if (existingA == 0) {
                        final int newColor = (alpha << 24) | (textB << 16) | (textG << 8) | textR;
                        nativeImage.setPixelColor(xPos, yPos, newColor);
                    } else {
                        blendPixel(nativeImage, xPos, yPos, alpha, textR, textG, textB);
                    }
                }
            }

            if (rotate90) {
                drawY--;
                if (drawY < 0) {
                    drawY = textDimensions[0] - 1;
                    drawX++;
                }
            } else {
                drawX++;
                if (drawX == textDimensions[0]) {
                    drawX = 0;
                    drawY++;
                }
            }
        }
    }

    private static void drawPixelSafe(NativeImage nativeImage, int x, int y, int color) {
        if (Utilities.isBetween(x, 0, nativeImage.getWidth() - 1) && Utilities.isBetween(y, 0, nativeImage.getHeight() - 1)) {
            nativeImage.setPixelColor(x, y, color);
        }
    }

    private static void blendPixel(NativeImage nativeImage, int x, int y, int alpha, int r, int g, int b) {
        final int existingPixel = nativeImage.getColor(x, y);
        final int existingA = (existingPixel >> 24) & 0xFF;
        final int existingR = existingPixel & 0xFF;
        final int existingG = (existingPixel >> 8) & 0xFF;
        final int existingB = (existingPixel >> 16) & 0xFF;

        final int invAlpha = 255 - alpha;
        final int newR = (existingR * invAlpha + r * alpha) / 255;
        final int newG = (existingG * invAlpha + g * alpha) / 255;
        final int newB = (existingB * invAlpha + b * alpha) / 255;
        final int newA = Math.min(255, existingA + alpha);

        final int finalColor = (newA << 24) | (newB << 16) | (newG << 8) | newR;
        nativeImage.setPixelColor(x, y, finalColor);
    }
}
