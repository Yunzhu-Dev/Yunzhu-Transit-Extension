package top.xfunny.core;

import org.mtr.core.servlet.QueueObject;
import org.mtr.core.tool.Utilities;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectImmutableList;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectSet;
import top.xfunny.core.data.YteCoreLogger;
import top.xfunny.core.simulation.YteSimulator;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class YteMain implements Utilities {

    private final ObjectImmutableList<YteSimulator> simulators;
    private final ScheduledExecutorService scheduledExecutorService;

    public static final int MILLISECONDS_PER_TICK = 10;

    public YteMain(Path rootPath, boolean threadedSimulation, String... dimensions) {// 使用可变参数传入维度，不限制维度数量
        final ObjectArrayList<YteSimulator> tempSimulators = new ObjectArrayList<>();

        YteCoreLogger.info("YTE server loading files...");
        for (final String dimension : dimensions) {
            tempSimulators.add(new YteSimulator(dimension, rootPath));
        }

        simulators = new ObjectImmutableList<>(tempSimulators);

        if (threadedSimulation) {//暂时无法触发
            scheduledExecutorService = Executors.newScheduledThreadPool(simulators.size());
            simulators.forEach(simulator ->
                    scheduledExecutorService.scheduleAtFixedRate(
                            simulator::tick, 0, MILLISECONDS_PER_TICK, TimeUnit.MILLISECONDS));
        } else {
            scheduledExecutorService = null;
        }

        YteCoreLogger.info("YTE server started with dimensions {}", Arrays.toString(dimensions));
    }

    public void manualTick() {//目前用这个触发周期保存
        simulators.forEach(YteSimulator::tick);
    }

    public void sendMessageC2S(@Nullable Integer worldIndex, QueueObject queueObject) {
        if (worldIndex == null) {
            simulators.forEach(simulator -> simulator.sendMessageC2S(queueObject));
        } else if (worldIndex >= 0 && worldIndex < simulators.size()) {
            simulators.get(worldIndex).sendMessageC2S(queueObject);
        }
    }

    public void save() {
        simulators.forEach(YteSimulator::save);
    }

    public void stop() {
        YteCoreLogger.info("YTE stopping...");
        if (scheduledExecutorService != null) {
            scheduledExecutorService.shutdown();
            Utilities.awaitTermination(scheduledExecutorService);
        }
        YteCoreLogger.info("YTE starting full save...");
        simulators.forEach(YteSimulator::stop);
        YteCoreLogger.info("YTE stopped");
    }

    /**
     * 对所有维度执行清理
     */
    public void reconcileAll(ObjectSet<org.mtr.core.data.Lift> activeLifts) {
        simulators.forEach(simulator -> simulator.reconcile(activeLifts));
    }
}
