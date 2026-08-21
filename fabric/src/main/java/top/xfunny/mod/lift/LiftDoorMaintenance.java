package top.xfunny.mod.lift;

/**
 * Implemented by door block entities that support maintenance opening with a
 * key item (currently MTR's driver key, triangular key item reserved).
 * The door value override lives in {@code MixinLiftDoorBlockEntity}.
 */
public interface LiftDoorMaintenance {

	void yte$setMaintenanceOpen(boolean open);

	boolean yte$isMaintenanceOpen();
}
