package top.xfunny.mod.client.font;

import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.Point2D;

/** Simple left-to-right glyph layout for lift numbers and labels. */
public final class FontFeatureLayout {
    private FontFeatureLayout() {
    }

    public static GlyphVector create(Font font, FontRenderContext context, String text,
                                     GsubParser substitutions, FontFeature[] features, float spacing) {
        final GlyphVector original = font.createGlyphVector(context, text);
        final int count = original.getNumGlyphs();
        final int[] before = original.getGlyphCodes(0, count, null);
        final int[] after = substitutions.substitute(before, features);
        for (int i = 0; i < count; i++) {
            if (after[i] != before[i] && after[i] >= font.getNumGlyphs()) {
                throw new IllegalArgumentException("GSUB replacement exceeds this font's glyph count");
            }
        }
        final GlyphVector result = font.createGlyphVector(context, after);
        // Rebuild positions from substituted glyph advances, then add spacing between visible slots.
        float offset = 0;
        boolean hasPreviousGlyph = false;
        for (int i = 0; i <= count; i++) {
            if (i < count && before[i] != 0xFFFF && before[i] != 0xFFFE) {
                if (hasPreviousGlyph) offset += spacing;
                hasPreviousGlyph = true;
            }
            final Point2D position = result.getGlyphPosition(i);
            result.setGlyphPosition(i, new Point2D.Float((float) position.getX() + offset, (float) position.getY()));
        }
        return result;
    }
}
