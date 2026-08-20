package top.xfunny.mod.lift;

import java.util.function.DoubleUnaryOperator;

/**
 * Per-lift door animation curve, applied to both the opening and the closing
 * phase of the door value calculation (0 = closed, 1 = fully open).
 */
public enum DoorMotionCurve {
	LINEAR("gui.yte.door_curve_linear", t -> t),
	SMOOTHSTEP("gui.yte.door_curve_smoothstep", t -> t * t * (3 - 2 * t)),
	EASE_IN_OUT_QUAD("gui.yte.door_curve_ease_in_out_quad", t -> t < 0.5 ? 2 * t * t : 1 - Math.pow(-2 * t + 2, 2) / 2);

	private final String translationKey;
	private final DoubleUnaryOperator function;

	DoorMotionCurve(String translationKey, DoubleUnaryOperator function) {
		this.translationKey = translationKey;
		this.function = function;
	}

	/** Eases a progress value clamped to [0, 1] into the range [0, 1]. */
	public double apply(double t) {
		return function.applyAsDouble(Math.max(0, Math.min(t, 1)));
	}

	/** Inverse of {@link #apply}: maps an eased value back to the raw progress t. */
	public double invert(double v) {
		v = Math.max(0, Math.min(v, 1));
		switch (this) {
			case LINEAR:
				return v;
			case EASE_IN_OUT_QUAD:
				return v < 0.5 ? Math.sqrt(v / 2) : 1 - Math.sqrt((1 - v) / 2);
			case SMOOTHSTEP:
			default:
				// Newton iterations on f(t) = 3t² - 2t³; 10 rounds converge to ~1e-6
				double t = v;
				for (int i = 0; i < 10; i++) {
					t = t - (t * t * (3 - 2 * t) - v) / (6 * t * (1 - t));
				}
				return Math.max(0, Math.min(t, 1));
		}
	}

	public String getTranslationKey() {
		return translationKey;
	}

	public DoorMotionCurve next() {
		final DoorMotionCurve[] values = values();
		return values[(ordinal() + 1) % values.length];
	}

	public static DoorMotionCurve fromSerializedName(String name) {
		for (final DoorMotionCurve value : values()) {
			if (value.name().equals(name)) {
				return value;
			}
		}
		return LINEAR;
	}
}
