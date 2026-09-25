package top.xfunny.mod.client.font;

/** Inclusive Unicode range with explicit playback lifetime. Access on the client thread. */
public final class FontAnimation {
    // Values never retain their owner. Unloaded screen instances can be collected.
    private static final java.util.Map<Object, java.util.Map<String, FontAnimation>> OWNED = new java.util.WeakHashMap<>();
    private final String first;
    private final String last;
    private final int firstCodePoint;
    private final int frameCount;
    private final double framesPerSecond;
    private boolean running;
    private double startedAt;

    public static FontAnimation forScreen(Object owner, String slot, String first, String last, double speed) {
        java.util.Objects.requireNonNull(owner, "owner");
        java.util.Objects.requireNonNull(slot, "Set a stable texture ID before configuring the animation");
        final java.util.Map<String, FontAnimation> slots = OWNED.computeIfAbsent(owner, ignored -> new java.util.HashMap<>());
        final FontAnimation current = slots.get(slot);
        if (current != null && current.first.equalsIgnoreCase(first) && current.last.equalsIgnoreCase(last)
                && current.framesPerSecond == speed) return current;
        final FontAnimation replacement = new FontAnimation(first, last, speed);
        if (current != null) current.stop();
        slots.put(slot, replacement);
        return replacement;
    }

    /** Repeated starts while running preserve the current playback. Time is in seconds. */
    public void start(double currentSeconds) {
        if (!Double.isFinite(currentSeconds) || currentSeconds < 0) {
            throw new IllegalArgumentException("Animation clock must be finite and nonnegative");
        }
        if (!running) {
            startedAt = currentSeconds;
            running = true;
        }
    }

    public void stop() {
        running = false;
        startedAt = 0;
    }

    public boolean isRunning() {
        return running;
    }

    /** Null means ordinary text should be rendered; no frame is evaluated while stopped. */
    public String getCurrentFrame(double currentSeconds) {
        if (!running) return null;
        if (!Double.isFinite(currentSeconds) || currentSeconds < 0) {
            throw new IllegalArgumentException("Animation clock must be finite and nonnegative");
        }
        // A reset client clock starts a new cycle rather than freezing at the first frame.
        if (currentSeconds < startedAt) startedAt = currentSeconds;
        return getFrame(currentSeconds - startedAt);
    }

    public FontAnimation(String first, String last, double framesPerSecond) {
        final int start = parseCodePoint(first);
        final int end = parseCodePoint(last);
        if (end < start || (start <= Character.MAX_SURROGATE && end >= Character.MIN_SURROGATE)) {
            throw new IllegalArgumentException("Animation must be an ascending range of Unicode scalar values");
        }
        if (!Double.isFinite(framesPerSecond) || framesPerSecond <= 0) {
            throw new IllegalArgumentException("Animation speed must be a finite positive number of frames per second");
        }
        this.firstCodePoint = start;
        this.first = first;
        this.last = last;
        this.frameCount = end - start + 1;
        this.framesPerSecond = framesPerSecond;
    }

    public String getFrame(double elapsedSeconds) {
        if (!Double.isFinite(elapsedSeconds) || elapsedSeconds < 0) {
            throw new IllegalArgumentException("Animation time must be finite and nonnegative");
        }
        // Reduce time before multiplication so even long-running clocks stay within the cycle.
        final double duration = frameCount / framesPerSecond;
        final int frame = (int) Math.min(frameCount - 1, Math.floor((elapsedSeconds % duration) * framesPerSecond));
        return new String(Character.toChars(firstCodePoint + frame));
    }

    public int getFrameCount() {
        return frameCount;
    }

    private static int parseCodePoint(String value) {
        if (value == null || !value.matches("[0-9a-fA-F]{1,6}")) {
            throw new IllegalArgumentException("Expected a hexadecimal Unicode code point, e.g. E000");
        }
        final int codePoint = Integer.parseInt(value, 16);
        if (!Character.isValidCodePoint(codePoint) || (codePoint >= Character.MIN_SURROGATE && codePoint <= Character.MAX_SURROGATE)) {
            throw new IllegalArgumentException("Invalid Unicode scalar value: " + value);
        }
        return codePoint;
    }
}
