package top.xfunny.mod.client.font;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Consumer;

/** Latin/default-script SS/CV replacements (single or indexed alternate); not a general text shaper. */
public final class GsubParser {
    public static final GsubParser EMPTY = new GsubParser(Collections.emptyMap());
    private final Map<String, SortedMap<Integer, Map<Integer, Replacement>>> features;

    private GsubParser(Map<String, SortedMap<Integer, Map<Integer, Replacement>>> features) {
        this.features = features;
    }

    public boolean supports(String tag) {
        return features.containsKey(tag);
    }

    public int[] substitute(int[] glyphs, FontFeature... enabled) {
        final SortedMap<Integer, Map<Integer, Replacement>> selected = new TreeMap<>();
        final Map<Integer, Integer> variants = new HashMap<>();
        for (FontFeature feature : FontFeatures.normalize(enabled)) {
            final SortedMap<Integer, Map<Integer, Replacement>> lookups = features.get(feature.getTag());
            if (lookups != null) {
                final int variant = feature.getVariant();
                for (int index : lookups.keySet()) {
                    // An OpenType lookup shared by features runs once. Resolve shared values deterministically.
                    variants.merge(index, variant, Math::max);
                }
                selected.putAll(lookups);
            }
        }
        final int[] result = glyphs.clone();
        // A shared lookup is applied once, in font LookupList order, not caller order.
        for (Map.Entry<Integer, Map<Integer, Replacement>> lookup : selected.entrySet()) {
            for (int i = 0; i < result.length; i++) {
                final Replacement replacement = lookup.getValue().get(result[i]);
                if (replacement != null) result[i] = replacement.apply(result[i], variants.get(lookup.getKey()));
            }
        }
        return result;
    }

    public static GsubParser parse(byte[] bytes, Consumer<String> warning) {
        final Reader file = new Reader(bytes, 0, bytes.length);
        // Font.createFont reads the first face of a collection; use the same face.
        final int face = file.tag(0).equals("ttcf") ? file.offset32(0, 12) : 0;
        final int tableCount = file.u16(face + 4);
        file.check(face + 12, tableCount * 16);
        int gsub = -1, length = 0;
        for (int i = 0; i < tableCount; i++) {
            final int record = face + 12 + i * 16;
            if (file.tag(record).equals("GSUB")) {
                gsub = file.offset32(0, record + 8);
                length = file.u32(record + 12);
                file.check(gsub, length);
                break;
            }
        }
        if (gsub < 0) return EMPTY;
        final Reader r = new Reader(bytes, gsub, length);
        if (r.u16(gsub) != 1 || r.u16(gsub + 2) > 1) throw new IllegalArgumentException("Unsupported GSUB version");
        if (r.u16(gsub + 2) == 1 && r.u32(gsub + 10) != 0) {
            warning.accept("GSUB FeatureVariations is unsupported; SS/CV disabled");
            return EMPTY;
        }
        final int scripts = r.offset16(gsub, gsub + 4);
        final int featureList = r.offset16(gsub, gsub + 6);
        final int lookupList = r.offset16(gsub, gsub + 8);
        final int scriptCount = r.u16(scripts);
        r.check(scripts + 2, scriptCount * 6);
        int script = -1, defaultScript = -1;
        for (int i = 0; i < scriptCount; i++) {
            final int record = scripts + 2 + i * 6;
            final String tag = r.tag(record);
            if (tag.equals("latn")) script = r.offset16(scripts, record + 4);
            if (tag.equals("DFLT")) defaultScript = r.offset16(scripts, record + 4);
        }
        if (script < 0) script = defaultScript;
        if (script < 0 || r.u16(script) == 0) return EMPTY;
        final int language = r.offset16(script, script);
        final Set<Integer> featureIndices = new TreeSet<>();
        final int required = r.u16(language + 2);
        if (required != 0xFFFF) featureIndices.add(required);
        final int languageCount = r.u16(language + 4);
        r.check(language + 6, languageCount * 2);
        for (int i = 0; i < languageCount; i++) featureIndices.add(r.u16(language + 6 + i * 2));
        final int featureCount = r.u16(featureList);
        final int lookupCount = r.u16(lookupList);
        r.check(featureList + 2, featureCount * 6);
        r.check(lookupList + 2, lookupCount * 2);
        final Map<String, SortedMap<Integer, Map<Integer, Replacement>>> features = new HashMap<>();
        final Set<String> rejected = new HashSet<>();
        final Map<Integer, Map<Integer, Replacement>> parsedLookups = new HashMap<>();
        for (int index : featureIndices) {
            if (index >= featureCount) throw new IllegalArgumentException("Invalid feature index");
            final int record = featureList + 2 + index * 6;
            final String tag = r.tag(record);
            if (!FontFeatures.isSupportedTag(tag) || rejected.contains(tag)) continue;
            try {
                final int feature = r.offset16(featureList, record + 4);
                final int count = r.u16(feature + 2);
                r.check(feature + 4, count * 2);
                final SortedMap<Integer, Map<Integer, Replacement>> selected = new TreeMap<>();
                for (int i = 0; i < count; i++) {
                    final int lookupIndex = r.u16(feature + 4 + i * 2);
                    if (lookupIndex >= lookupCount) throw new IllegalArgumentException("Invalid lookup index");
                    Map<Integer, Replacement> lookup = parsedLookups.get(lookupIndex);
                    if (lookup == null) {
                        lookup = readLookup(r, r.offset16(lookupList, lookupList + 2 + lookupIndex * 2));
                        parsedLookups.put(lookupIndex, lookup);
                    }
                    selected.put(lookupIndex, lookup);
                }
                features.computeIfAbsent(tag, key -> new TreeMap<>()).putAll(selected);
            } catch (IllegalArgumentException e) {
                // Never partially apply a feature containing an unsupported lookup.
                features.remove(tag);
                rejected.add(tag);
                warning.accept(tag + " disabled: " + e.getMessage());
            }
        }
        return new GsubParser(features);
    }

    private static Map<Integer, Replacement> readLookup(Reader r, int lookup) {
        final int type = r.u16(lookup);
        if (type != 1 && type != 3 && type != 7) throw new IllegalArgumentException("Only GSUB single/alternate substitution is supported (type " + type + ")");
        if (r.u16(lookup + 2) != 0) throw new IllegalArgumentException("Lookup flags require a full shaper");
        final int count = r.u16(lookup + 4);
        r.check(lookup + 6, count * 2);
        final Map<Integer, Replacement> replacements = new HashMap<>();
        for (int i = 0; i < count; i++) {
            int subtable = r.offset16(lookup, lookup + 6 + i * 2);
            int subtableType = type;
            if (type == 7) {
                subtableType = r.u16(subtable + 2);
                if (r.u16(subtable) != 1 || (subtableType != 1 && subtableType != 3)) throw new IllegalArgumentException("Only extension-wrapped single/alternate substitution is supported");
                subtable = r.offset32(subtable, subtable + 4);
            }
            final int format = r.u16(subtable);
            if (subtableType == 3) {
                if (format != 1) throw new IllegalArgumentException("Invalid AlternateSubst format");
                final int[] coverage = readCoverage(r, r.offset16(subtable, subtable + 2));
                final int alternateCount = r.u16(subtable + 4);
                if (alternateCount != coverage.length) throw new IllegalArgumentException("Coverage/alternate count mismatch");
                r.check(subtable + 6, alternateCount * 2);
                for (int j = 0; j < alternateCount; j++) {
                    final int set = r.offset16(subtable, subtable + 6 + j * 2);
                    final int glyphCount = r.u16(set);
                    if (glyphCount == 0) throw new IllegalArgumentException("Empty alternate set");
                    r.check(set + 2, glyphCount * 2);
                    final int[] candidates = new int[glyphCount];
                    for (int candidate = 0; candidate < glyphCount; candidate++) candidates[candidate] = r.u16(set + 2 + candidate * 2);
                    replacements.putIfAbsent(coverage[j], new Replacement(candidates, true));
                }
                continue;
            }
            if (format != 1 && format != 2) throw new IllegalArgumentException("Invalid SingleSubst format");
            final int[] coverage = readCoverage(r, r.offset16(subtable, subtable + 2));
            final int value = r.u16(subtable + 4);
            if (format == 2) {
                if (value != coverage.length) throw new IllegalArgumentException("Coverage/substitute count mismatch");
                r.check(subtable + 6, value * 2);
            }
            for (int j = 0; j < coverage.length; j++) {
                final int replacement = format == 1 ? (coverage[j] + (short) value) & 0xFFFF : r.u16(subtable + 6 + j * 2);
                // Within a lookup only the first matching subtable may substitute a glyph.
                replacements.putIfAbsent(coverage[j], new Replacement(new int[]{replacement}, false));
            }
        }
        return replacements;
    }

    private static int[] readCoverage(Reader r, int coverage) {
        final int format = r.u16(coverage);
        final int count = r.u16(coverage + 2);
        if (format == 1) {
            r.check(coverage + 4, count * 2);
            final int[] glyphs = new int[count];
            for (int i = 0; i < count; i++) {
                glyphs[i] = r.u16(coverage + 4 + i * 2);
                if (i > 0 && glyphs[i] <= glyphs[i - 1]) throw new IllegalArgumentException("Unsorted coverage");
            }
            return glyphs;
        }
        if (format != 2) throw new IllegalArgumentException("Invalid Coverage format");
        r.check(coverage + 4, count * 6);
        final int[] glyphs = new int[65536];
        int size = 0, previous = -1;
        for (int i = 0; i < count; i++) {
            final int record = coverage + 4 + i * 6;
            final int start = r.u16(record), end = r.u16(record + 2);
            if (start <= previous || end < start || r.u16(record + 4) != size) throw new IllegalArgumentException("Invalid coverage range");
            for (int glyph = start; glyph <= end; glyph++) glyphs[size++] = glyph;
            previous = end;
        }
        return Arrays.copyOf(glyphs, size);
    }

    private static final class Replacement {
        private final int[] candidates;
        private final boolean alternate;

        private Replacement(int[] candidates, boolean alternate) {
            this.candidates = candidates;
            this.alternate = alternate;
        }

        private int apply(int original, int variant) {
            if (!alternate) return candidates[0];
            return variant <= candidates.length ? candidates[variant - 1] : original;
        }
    }

    private static final class Reader {
        private final byte[] bytes;
        private final int start, end;

        private Reader(byte[] bytes, int start, int length) {
            this.bytes = bytes;
            if (start < 0 || length < 0 || (long) start + length > bytes.length) throw new IllegalArgumentException("Invalid font table bounds");
            this.start = start;
            this.end = start + length;
        }

        private void check(int offset, int size) {
            if (offset < start || size < 0 || (long) offset + size > end) throw new IllegalArgumentException("Truncated font table");
        }

        private int u16(int offset) {
            check(offset, 2);
            return (bytes[offset] & 255) << 8 | (bytes[offset + 1] & 255);
        }

        private int u32(int offset) {
            final long value = ((long) u16(offset) << 16) | u16(offset + 2);
            if (value > Integer.MAX_VALUE) throw new IllegalArgumentException("Font offset too large");
            return (int) value;
        }

        private int offset16(int base, int field) {
            return offset(base, u16(field));
        }

        private int offset32(int base, int field) {
            return offset(base, u32(field));
        }

        private int offset(int base, int relative) {
            final long result = (long) base + relative;
            if (relative == 0 || result >= end) throw new IllegalArgumentException("Invalid font offset");
            check((int) result, 2);
            return (int) result;
        }

        private String tag(int offset) {
            check(offset, 4);
            return new String(bytes, offset, 4, StandardCharsets.US_ASCII);
        }
    }
}
