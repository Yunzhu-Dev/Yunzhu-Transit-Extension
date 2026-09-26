package top.xfunny.mod.lift;

/**
 * Implemented by door block entities that support maintenance opening with a
 * key item (currently MTR's driver key, triangular key item reserved).
 * The door value override lives in {@code MixinLiftDoorBlockEntity}.
 */
public interface LiftDoorMaintenance {

	/**
	 * 切换检修开门状态并启动过渡动画。开门线性播放 {@code durationMs}；
	 * 关门按时长与曲线套用该梯配置（由实现内部按 {@code liftId} 查询）。
	 */
	void yte$setMaintenanceOpen(boolean open, long liftId, long durationMs);

	boolean yte$isMaintenanceOpen();

	/** 是否存在尚未播完的过渡动画（供 S→C 回声包守卫）。 */
	boolean yte$isMaintenanceAnimating();

	void yte$applyMaintenanceState(boolean open, float fromValue, long startNanos, long durationMs,
			DoorMotionCurve curve);

	double yte$maintenanceAnimValue();
}
