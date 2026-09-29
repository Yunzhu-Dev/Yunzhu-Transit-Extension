package top.xfunny.core.simulation;

import org.mtr.core.data.Lift;
import org.mtr.core.serializer.MessagePackReader;
import org.mtr.core.servlet.MessageQueue;
import org.mtr.core.servlet.QueueObject;
import org.mtr.core.simulation.FileLoader;
import org.mtr.core.tool.Utilities;
import org.mtr.libraries.it.unimi.dsi.fastutil.ints.IntIntImmutablePair;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectLongImmutablePair;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectSet;
import top.xfunny.core.data.YteCoreLogger;
import top.xfunny.core.data.YteData;
import top.xfunny.core.data.YteLiftConfig;
import top.xfunny.core.servlet.YteOperationProcessor;
import top.xfunny.mod.config.YteLiftConfigStore;
import top.xfunny.mod.lift.LiftDoorState;
import top.xfunny.mod.lift.LiftModeState;

import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;

public class YteSimulator extends YteData implements Utilities {

    private boolean autoSave = false;
    private final String dimension;
    private final FileLoader<YteLiftConfig> fileLoaderLiftConfigs;
    private final MessageQueue<Runnable> queuedRuns = new MessageQueue<>();
    private final MessageQueue<QueueObject> messageQueueC2S = new MessageQueue<>();

    private static final String KEY_LIFT_CONFIGS = "lift_configs";

    public YteSimulator(String dimension, Path rootPath) {
        this.dimension = dimension;
        final long startMillis = System.currentTimeMillis();
        final Path savePath = rootPath.resolve(dimension);

        this.fileLoaderLiftConfigs = new FileLoader<>(
                liftConfigs,
                YteLiftConfig::new,
                savePath,
                KEY_LIFT_CONFIGS
        );

        final long endMillis = System.currentTimeMillis();
        YteCoreLogger.debug("YteSimulator loaded {} lift configs for {} in {} second(s)",
                liftConfigs.size(), dimension, (float) (endMillis - startMillis) / MILLIS_PER_SECOND);
        sync();
    }

    public void tick() {
        try {
            if (autoSave) {
                save(true);
                autoSave = false;
            }
            queuedRuns.process(Runnable::run);
            messageQueueC2S.process(queueObject ->
                    queueObject.runCallback(YteOperationProcessor.process(
                            queueObject.key, queueObject.data, this)));
        } catch (Exception e) {
            YteCoreLogger.error("YteSimulator tick error for " + dimension, e);
        }
    }

    public void save() {
        autoSave = true;
    }

    public void stop() {
        save(false);
    }

    public void run(Runnable runnable) {
        queuedRuns.put(runnable);
    }

    public void sendMessageC2S(QueueObject queueObject) {
        messageQueueC2S.put(queueObject);
    }

    /**
     * 清理孤儿配置：对比活跃的 MTR Lift ID 集合，移除不存在的配置
     */
    public void reconcile(ObjectSet<Lift> activeLifts) {
        final Set<Long> activeIds = activeLifts.stream()
                .filter(Lift::isValid)
                .map(Lift::getId)
                .collect(Collectors.toSet());

        final ObjectArrayList<YteLiftConfig> orphans = new ObjectArrayList<>();
        liftConfigs.forEach(config -> {
            if (!activeIds.contains(config.getId())) {
                orphans.add(config);
            }
        });

        if (!orphans.isEmpty()) {
            orphans.forEach(config -> {
                liftConfigs.remove(config);
                LiftDoorState.remove(config.getId());
                LiftModeState.remove(config.getId());
                YteLiftConfigStore.remove(config.getId());
            });
            sync();
            save();
            YteCoreLogger.debug("YteSimulator cleaned {} orphan lift configs for {}",
                    orphans.size(), dimension);
        }
    }

    private void save(boolean useReducedHash) {
        final ObjectLongImmutablePair<Boolean> changedAndDuration = Utilities.measureDuration(() -> {
            final IntIntImmutablePair saveCounts = fileLoaderLiftConfigs.save(useReducedHash);
            final int changed = saveCounts.leftInt();
            final int deleted = saveCounts.rightInt();
            return changed > 0 || deleted > 0;
        });
        if (changedAndDuration.left() || !useReducedHash) {
            YteCoreLogger.debug("YteSimulator save complete for {} in {} second(s)",
                    dimension, changedAndDuration.rightLong() / 1000F);
        }
    }
}
