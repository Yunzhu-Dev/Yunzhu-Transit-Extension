package top.xfunny.core.data;

import org.mtr.libraries.it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArraySet;

public abstract class YteData {

    public final ObjectArraySet<YteLiftConfig> liftConfigs = new ObjectArraySet<>();
    public final Object2ObjectOpenHashMap<Long, YteLiftConfig> liftConfigIdMap = new Object2ObjectOpenHashMap<>();

    public void sync() {
        try {
            liftConfigIdMap.clear();
            liftConfigs.forEach(config -> {
                liftConfigIdMap.put(config.getId(), config);
                top.xfunny.mod.config.YteLiftConfigStore.put(
                        config.getId(), config.getUpSpeed(), config.getDownSpeed(),
                        config.getUpAcceleration(), config.getDownAcceleration(), config.getAdoDistance(),
                        config.getLevellingDistance(), config.getLevellingSpeed(), config.getMotionProfile(),
                        config.isDoorHoldEnabled(), config.getDoorButtonLightMode(), config.getFloorCancelMode(),
                        config.isFloorCancelWhileMovingAllowed());
            });
        } catch (Exception e) {
            YteCoreLogger.error("YteData sync error", e);
        }
    }
}
