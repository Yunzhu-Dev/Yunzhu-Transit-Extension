package top.xfunny.mod.lift;

/**
 * Implemented by door block entities that support maintenance opening with a
 * triangular key. The door value override and persistence live in
 * {@code MixinLiftDoorBlockEntity}; the key item and its interaction are a
 * future addition and only need to call {@link #yte$setMaintenanceOpen}.
 */
public interface LiftDoorMaintenance {

	void yte$setMaintenanceOpen(boolean open);
}
