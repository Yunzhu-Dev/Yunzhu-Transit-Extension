package top.xfunny.core.generated.data;

import org.mtr.core.serializer.ReaderBase;
import org.mtr.core.serializer.SerializedDataBaseWithId;
import org.mtr.core.serializer.WriterBase;

/**
 * 电梯运行状态持久化记录：按 liftId 保存运行模式、联锁与消防子状态。
 * modePending/modeActive 属 tick 机器，不落盘——恢复时按 mode 重推导。
 * return_mode：通用模式回退（进入某模式前的模式，退出后恢复；目前消防迫降使用）。
 */
public abstract class YteLiftStateSchema implements SerializedDataBaseWithId {

    protected long liftId;
    protected String mode;
    protected boolean maintenanceLocked;
    protected String fireFloorNumber;
    protected boolean fireCancelPending;
    protected boolean fireman;
    protected String returnMode;

    private static final String KEY_LIFT_ID = "lift_id";
    private static final String KEY_MODE = "mode";
    private static final String KEY_MAINTENANCE_LOCKED = "maintenance_locked";
    private static final String KEY_FIRE_FLOOR_NUMBER = "fire_floor_number";
    private static final String KEY_FIRE_CANCEL_PENDING = "fire_cancel_pending";
    private static final String KEY_FIREMAN = "fireman";
    private static final String KEY_RETURN_MODE = "return_mode";

    public static final String DEFAULT_MODE = "NORMAL";
    public static final String DEFAULT_FIRE_FLOOR_NUMBER = "";
    public static final String DEFAULT_RETURN_MODE = "NORMAL";

    protected YteLiftStateSchema(long liftId, String mode, boolean maintenanceLocked, String fireFloorNumber,
            boolean fireCancelPending, boolean fireman, String returnMode) {
        this.liftId = liftId;
        this.mode = mode;
        this.maintenanceLocked = maintenanceLocked;
        this.fireFloorNumber = fireFloorNumber;
        this.fireCancelPending = fireCancelPending;
        this.fireman = fireman;
        this.returnMode = returnMode;
    }

    protected YteLiftStateSchema(ReaderBase readerBase) {
        mode = DEFAULT_MODE;
        maintenanceLocked = false;
        fireFloorNumber = DEFAULT_FIRE_FLOOR_NUMBER;
        fireCancelPending = false;
        fireman = false;
        returnMode = DEFAULT_RETURN_MODE;
        updateData(readerBase);
    }

    @Override
    public void updateData(ReaderBase readerBase) {
        readerBase.unpackLong(KEY_LIFT_ID, value -> liftId = value);
        mode = readerBase.getString(KEY_MODE, DEFAULT_MODE);
        maintenanceLocked = readerBase.getBoolean(KEY_MAINTENANCE_LOCKED, false);
        fireFloorNumber = readerBase.getString(KEY_FIRE_FLOOR_NUMBER, DEFAULT_FIRE_FLOOR_NUMBER);
        fireCancelPending = readerBase.getBoolean(KEY_FIRE_CANCEL_PENDING, false);
        fireman = readerBase.getBoolean(KEY_FIREMAN, false);
        returnMode = readerBase.getString(KEY_RETURN_MODE, DEFAULT_RETURN_MODE);
    }

    @Override
    public void serializeData(WriterBase writerBase) {
        writerBase.writeLong(KEY_LIFT_ID, liftId);
        writerBase.writeString(KEY_MODE, mode == null ? DEFAULT_MODE : mode);
        writerBase.writeBoolean(KEY_MAINTENANCE_LOCKED, maintenanceLocked);
        writerBase.writeString(KEY_FIRE_FLOOR_NUMBER, fireFloorNumber == null ? DEFAULT_FIRE_FLOOR_NUMBER : fireFloorNumber);
        writerBase.writeBoolean(KEY_FIRE_CANCEL_PENDING, fireCancelPending);
        writerBase.writeBoolean(KEY_FIREMAN, fireman);
        writerBase.writeString(KEY_RETURN_MODE, returnMode == null ? DEFAULT_RETURN_MODE : returnMode);
    }

    @Override
    public String getHexId() {
        return Long.toHexString(liftId);
    }

    @Override
    public boolean isValid() {
        return liftId != 0;
    }

    public long getId() {
        return liftId;
    }

    public String getMode() {
        return mode;
    }

    public boolean isMaintenanceLocked() {
        return maintenanceLocked;
    }

    public String getFireFloorNumber() {
        return fireFloorNumber;
    }

    public boolean isFireCancelPending() {
        return fireCancelPending;
    }

    public boolean isFireman() {
        return fireman;
    }

    public String getReturnMode() {
        return returnMode;
    }
}
