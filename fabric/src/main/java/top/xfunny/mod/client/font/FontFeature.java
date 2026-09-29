package top.xfunny.mod.client.font;

/** Immutable SS/CV selection, created by FontFeatures constants and CV methods. */
public final class FontFeature {
    private final String tag;
    private final int variant;

    FontFeature(String tag, int variant) {
        if (!FontFeatures.isSupportedTag(tag) || variant < 1 || variant > 65535
                || (tag.startsWith("ss") && variant != 1)) {
            throw new IllegalArgumentException("Invalid SS/CV feature selection");
        }
        this.tag = tag;
        this.variant = variant;
    }

    public String getTag() {
        return tag;
    }

    public int getVariant() {
        return variant;
    }

    @Override
    public String toString() {
        return tag + "=" + variant;
    }
}
