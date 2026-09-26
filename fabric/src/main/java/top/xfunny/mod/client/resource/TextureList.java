package top.xfunny.mod.client.resource;

import top.xfunny.mod.client.DynamicTextureCache;
import top.xfunny.mod.client.FontResourceGenerator;
import top.xfunny.mod.client.client_data.DynamicResource;
import top.xfunny.mod.client.font.FontFeatures;
import top.xfunny.mod.client.font.FontFeature;
import top.xfunny.mod.client.font.GsubParser;
import org.mtr.mod.config.Config;

import java.awt.*;

public class TextureList {
    public static TextureList instance = new TextureList();

    public DynamicResource renderFont(String id, Long blockPos, String originalText, int textColor, Font font, float fontSize, float letterSpacing) {
        return renderFont(id, blockPos, originalText, textColor, font, fontSize, letterSpacing, new FontFeature[0]);
    }

    public DynamicResource renderFont(String id, Long blockPos, String originalText, int textColor, Font font, float fontSize, float letterSpacing, FontFeature... features) {
        final FontFeature[] enabled = FontFeatures.normalize(features);
        final GsubParser substitutions = FontList.instance.getSubstitutions(font);
        final String resourceKey = FontList.instance.getResourceKey(font);
        // Keep the screen prefix before '$' stable while isolating all rasterization settings.
        final String textureId = id + "$" + originalText.length() + ":" + originalText
                + ":" + resourceKey.length() + ":" + resourceKey + ":" + textColor
                + ":" + fontSize + ":" + letterSpacing + ":" + Config.getClient().getDynamicTextureResolution()
                + ":" + java.util.Arrays.toString(enabled);

        return DynamicTextureCache.instance.getResource(
                textureId,
                blockPos,
                () -> FontResourceGenerator.generateNativeImage(originalText, textColor, font, fontSize, 0, letterSpacing, substitutions, enabled)
        );
    }

    //弃用
    public DynamicResource getTestLiftPanelDisplay(String originalText, int textColor) {
        return DynamicTextureCache.instance.getResource(
                String.format("test_lift_panel_display_%s", originalText),
                null,
                () -> FontResourceGenerator.generateNativeImage(originalText, textColor, FontList.instance.getFont("testfont"), 4, 0, 4)
        );
    }
}
