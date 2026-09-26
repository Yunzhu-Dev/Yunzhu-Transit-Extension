package top.xfunny.mod.lift;

import java.util.function.DoubleUnaryOperator;

/**
 * Per-lift door animation curve, applied to both the opening and the closing
 * phase of the door value calculation (0 = closed, 1 = fully open).
 */
public enum DoorMotionCurve {
	LINEAR("gui.yte.door_curve_linear", t -> t),
	SMOOTHSTEP("gui.yte.door_curve_smoothstep", t -> t * t * (3 - 2 * t)),
	/** 缓入 */
	EASE_IN("gui.yte.door_curve_ease_in", t -> cubicBezier(t, 0.42, 0, 1, 1)),
	/** 缓出 */
	EASE_OUT("gui.yte.door_curve_ease_out", t -> cubicBezier(t, 0, 0, 0.58, 1)),
	/** 缓入缓出 */
	EASE_IN_OUT("gui.yte.door_curve_ease_in_out", t -> cubicBezier(t, 0.42, 0, 0.58, 1)),
	/** 缓入匀速 */
	EASE_IN_LINEAR("gui.yte.door_curve_ease_in_linear", t -> {
		if (t <= 0.6) {
			final double s = t / 0.6;
			return 0.275 * s * s * s + 0.075 * s * s;
		}
		return 0.35 + 0.65 * (t - 0.6) / 0.4;
	});

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
		if (this == LINEAR) {
			return v;
		}
		// 单调曲线二分求反：牛顿迭代在曲线平缓段（导数接近 0，如缓入段起点）会失稳并
		// 返回错误的 t（反向续开因此算出 coolDown=total，门被吸到全关再重播开门动画）；
		// 二分对任意单调曲线稳定，40 次迭代误差 < 1e-12
		double low = 0;
		double high = 1;
		for (int i = 0; i < 40; i++) {
			final double mid = (low + high) / 2;
			if (apply(mid) < v) {
				low = mid;
			} else {
				high = mid;
			}
		}
		return (low + high) / 2;
	}

	/** 三次贝塞尔曲线（P0=(0,0)、P3=(1,1)）在 x 处的 y 值：牛顿迭代解参数 t。 */
	private static double cubicBezier(double x, double x1, double y1, double x2, double y2) {
		double t = Math.max(0, Math.min(x, 1));
		for (int i = 0; i < 8; i++) {
			final double error = bezierX(t, x1, x2) - x;
			if (Math.abs(error) < 1e-6) {
				break;
			}
			final double derivative = 3 * (1 - t) * (1 - t) * x1 + 6 * (1 - t) * t * (x2 - x1) + 3 * t * t * (1 - x2);
			if (Math.abs(derivative) < 1e-9) {
				break;
			}
			t = Math.max(0, Math.min(1, t - error / derivative));
		}
		return bezierY(t, y1, y2);
	}

	private static double bezierX(double t, double x1, double x2) {
		return 3 * (1 - t) * (1 - t) * t * x1 + 3 * (1 - t) * t * t * x2 + t * t * t;
	}

	private static double bezierY(double t, double y1, double y2) {
		return 3 * (1 - t) * (1 - t) * t * y1 + 3 * (1 - t) * t * t * y2 + t * t * t;
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
