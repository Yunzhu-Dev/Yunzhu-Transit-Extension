package top.xfunny.core.data;

import org.mtr.libraries.it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArraySet;
import top.xfunny.mod.config.YteLiftConfigStore;

public abstract class YteData {

    public final ObjectArraySet<YteLiftConfig> liftConfigs = new ObjectArraySet<>();
    public final Object2ObjectOpenHashMap<Long, YteLiftConfig> liftConfigIdMap = new Object2ObjectOpenHashMap<>();

    public void sync() {
        try {
            liftConfigIdMap.clear();
            liftConfigs.forEach(config -> {
                liftConfigIdMap.put(config.getId(), config);
                YteLiftConfigStore.put(config);
            });
        } catch (Exception e) {
            YteCoreLogger.error("YteData sync error", e);
        }
    }
}
