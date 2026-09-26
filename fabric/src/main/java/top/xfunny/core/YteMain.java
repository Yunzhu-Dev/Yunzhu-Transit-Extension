package top.xfunny.core;

import org.mtr.core.servlet.QueueObject;
import org.mtr.core.simulation.FileLoader;
import org.mtr.core.tool.Utilities;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArraySet;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectImmutableList;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectSet;
import top.xfunny.core.data.YteCoreLogger;
import top.xfunny.core.data.YteLiftState;
import top.xfunny.core.simulation.YteSimulator;
import top.xfunny.mod.lift.LiftModeState;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class YteMain implements Utilities {

    private final ObjectImmutableList<YteSimulator> simulators;
    private final ScheduledExecutorService scheduledExecutorService;

    /** 电梯运行状态持久化（全局单文件，不按维度拆分——LiftModeState 的 STATES 是全局 Map） */
    private final ObjectArraySet<YteLiftState> liftStates = new ObjectArraySet<>();
    private final FileLoader<YteLiftState> fileLoaderLiftStates;

    public static final int MILLISECONDS_PER_TICK = 10;

    private static final String KEY_LIFT_STATES = "lift_states";

    public YteMain(Path rootPath, boolean threadedSimulation, String... dimensions) {// 使用可变参数传入维度，不限制维度数量
        final ObjectArrayList<YteSimulator> tempSimulators = new ObjectArrayList<>();

        YteCoreLogger.info("YTE server loading files...");
        for (final String dimension : dimensions) {
            tempSimulators.add(new YteSimulator(dimension, rootPath));
        }

        simulators = new ObjectImmutableList<>(tempSimulators);

        this.fileLoaderLiftStates = new FileLoader<>(
                liftStates,
                YteLiftState::new,
                rootPath,
                KEY_LIFT_STATES
        );
        // 恢复电梯运行状态（消防迫降、自动救援等待续跑的模式会置 modePending 由首 tick 续跑）
        LiftModeState.restoreStates(liftStates);
        LiftModeState.markStateDirty();

        if (threadedSimulation) {
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
        // 电梯状态变更近实时落盘
        if (LiftModeState.consumeStateDirty()) {
            saveLiftStates();
        }
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
        saveLiftStates();
    }

    /** 快照当前电梯运行状态并写盘。 */
    private void saveLiftStates() {
        liftStates.clear();
        LiftModeState.exportStates().forEach(liftStates::add);
        fileLoaderLiftStates.save(false);
    }

    public void stop() {
        YteCoreLogger.info("YTE stopping...");
        if (scheduledExecutorService != null) {
            scheduledExecutorService.shutdown();
            Utilities.awaitTermination(scheduledExecutorService);
        }
        YteCoreLogger.info("YTE starting full save...");
        simulators.forEach(YteSimulator::stop);
        saveLiftStates();
        YteCoreLogger.info("YTE stopped");
    }

    /**
     * 对所有维度执行清理
     */
    public void reconcileAll(ObjectSet<org.mtr.core.data.Lift> activeLifts) {
        simulators.forEach(simulator -> simulator.reconcile(activeLifts));
    }
}
