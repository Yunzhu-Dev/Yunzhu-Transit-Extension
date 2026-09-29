package top.xfunny.core.data;

import org.mtr.core.serializer.ReaderBase;
import top.xfunny.core.generated.data.YteLiftStateSchema;

public class YteLiftState extends YteLiftStateSchema {

    public YteLiftState(ReaderBase readerBase) {
        super(readerBase);
    }

    public YteLiftState(long liftId, String mode, boolean maintenanceLocked, String fireFloorNumber,
            boolean fireCancelPending, boolean fireman, String returnMode) {
        super(liftId, mode, maintenanceLocked, fireFloorNumber, fireCancelPending, fireman, returnMode);
    }
}
