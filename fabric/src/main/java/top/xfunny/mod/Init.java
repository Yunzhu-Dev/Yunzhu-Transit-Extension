package top.xfunny.mod;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mtr.core.data.Position;
import org.mtr.core.serializer.SerializedDataBase;
import org.mtr.core.servlet.QueueObject;
import org.mtr.core.tool.Utilities;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.holder.MinecraftServer;
import org.mtr.mapping.holder.World;
import org.mtr.mapping.holder.WorldSavePath;
import org.mtr.mapping.mapper.MinecraftServerHelper;
import org.mtr.mapping.registry.Registry;
import top.xfunny.core.YteMain;
import top.xfunny.mod.lift.LiftDoorState;
import top.xfunny.mod.lift.LiftModeState;
import top.xfunny.mod.packet.*;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

public final class Init implements Utilities {
    public static final String MOD_ID = "yte";
    public static final Logger LOGGER = LogManager.getLogger("Yunzhu Transit Extension");
    public static final Registry REGISTRY = new Registry();
    public static int HAS_UPDATE = -1;

    private static YteMain yteMain;
    @Nullable
    private static MinecraftServer minecraftServer;
    private static long lastSavedMillis;
    private static final ObjectArrayList<String> WORLD_ID_LIST = new ObjectArrayList<>();
    public static final int AUTOSAVE_INTERVAL = 30000;

    @Nullable
    public static YteMain getYteMain() {
        return yteMain;
    }

    private static final String[] LOGO = {
            "__  __                  __         ______                      _ __  ______     __                  _           ",
            "\\ \\/ /_  ______  ____  / /_  __  _/_  __/________ _____  _____(_) /_/ ____/  __/ /____  ____  _____(_)___  ____ ",
            " \\  / / / / __ \\/_  / / __ \\/ / / // / / ___/ __ `/ __ \\/ ___/ / __/ __/ | |/_/ __/ _ \\/ __ \\/ ___/ / __ \\/ __ \\",
            " / / /_/ / / / / / /_/ / / / /_/ // / / /  / /_/ / / / (__  ) / /_/ /____>  </ /_/  __/ / / (__  ) / /_/ / / / /",
            "/_/\\__,_/_/ /_/ /___/_/ /_/\\__,_//_/ /_/   \\__,_/_/ /_/____/_/\\__/_____/_/|_|\\__/\\___/_/ /_/____/_/\\____/_/ /_/ "
    };
    private static final int LOGO_WIDTH = Arrays.stream(LOGO).mapToInt(String::length).max().orElse(0);

    public static void init() {
        for (String line : LOGO) LOGGER.info(line);
        char SEPARATOR_CHAR = '─';
        String versionTag = " " + Keys.MOD_VERSION + " ";
        String suffix = String.valueOf(SEPARATOR_CHAR);
        char[] barChars = new char[Math.max(0, LOGO_WIDTH - versionTag.length() - suffix.length())];
        Arrays.fill(barChars, SEPARATOR_CHAR);
        LOGGER.info("{}{}{}", new String(barChars), versionTag, suffix);
        long startTime = System.currentTimeMillis();
        Map<String, Runnable> initSteps = new LinkedHashMap<>();

        initSteps.put("Creative Mode Tabs", CreativeModeTabs::init);
        initSteps.put("Sound Events", SoundEvents::init);
        initSteps.put("Blocks", Blocks::init);
        initSteps.put("Block Entity Types", BlockEntityTypes::init);
        initSteps.put("Items", Items::init);
        initSteps.put("MTR Packet", () -> {
            REGISTRY.setupPackets(new Identifier(MOD_ID, "packet"));
            REGISTRY.registerPacket(PacketYTEOpenBlockEntityScreen.class, PacketYTEOpenBlockEntityScreen::new);
            REGISTRY.registerPacket(PacketUpdateLiftTrackMagneticVaneConfig.class, PacketUpdateLiftTrackMagneticVaneConfig::new);
            REGISTRY.registerPacket(PacketUpdatePATRS01RailwaySignConfig.class, PacketUpdatePATRS01RailwaySignConfig::new);
            REGISTRY.registerPacket(PacketLanternSoundInstruction.class, PacketLanternSoundInstruction::new);
            REGISTRY.registerPacket(PacketSyncLiftDestinationDispatchTerminal.class, PacketSyncLiftDestinationDispatchTerminal::new);
            // YTE Lift Speed/Acceleration packets
            REGISTRY.registerPacket(YtePacketRequestData.class, YtePacketRequestData::new);
            REGISTRY.registerPacket(YtePacketUpdateData.class, YtePacketUpdateData::new);
            REGISTRY.registerPacket(PacketLiftAdoStart.class, PacketLiftAdoStart::new);
            REGISTRY.registerPacket(PacketLiftDoorControl.class, PacketLiftDoorControl::new);
            REGISTRY.registerPacket(PacketLiftFloorCancel.class, PacketLiftFloorCancel::new);
            REGISTRY.registerPacket(PacketLiftDoorMaintenance.class, PacketLiftDoorMaintenance::new);
            REGISTRY.registerPacket(PacketLiftDoorCurtain.class, PacketLiftDoorCurtain::new);
            REGISTRY.registerPacket(PacketLiftFireMode.class, PacketLiftFireMode::new);
            REGISTRY.registerPacket(PacketLiftCarCall.class, PacketLiftCarCall::new);
        });

        int currentStep = 1;
        for (Map.Entry<String, Runnable> step : initSteps.entrySet()) {
            LOGGER.info("Registering {} ({}/{})", step.getKey(), currentStep, initSteps.size());
            step.getValue().run();
            currentStep++;
        }

        // 注册 YTE 数据体系生命周期   初始化
        REGISTRY.eventRegistry.registerServerStarted(minecraftServer -> {
            Init.minecraftServer = minecraftServer;
            WORLD_ID_LIST.clear();
            MinecraftServerHelper.iterateWorlds(minecraftServer, serverWorld ->
                    WORLD_ID_LIST.add(getWorldId(new World(serverWorld.data))));
            lastSavedMillis = System.currentTimeMillis();
            yteMain = new YteMain(//todo:threadedSimulation值目前永久为false，未来可以考虑改为可配置项，以支持多线程读写
                    minecraftServer.getSavePath(WorldSavePath.getRootMapped()).resolve("yte"),
                    false,
                    WORLD_ID_LIST.toArray(new String[0]));
        });


        REGISTRY.eventRegistry.registerStartServerTick(() -> {
            // 固定时间周期保存yte数据
            if (yteMain != null) {
                yteMain.manualTick();
                final long currentMillis = System.currentTimeMillis();
                if (currentMillis - lastSavedMillis > AUTOSAVE_INTERVAL) {
                    yteMain.save();
                    lastSavedMillis = currentMillis;
                }
            }
        });

        REGISTRY.eventRegistry.registerPlayerDisconnect((minecraftServer, serverPlayerEntity) -> {
            // 玩家退出后进行数据保存
            if (yteMain != null) {
                yteMain.save();
            }
        });

        REGISTRY.eventRegistry.registerServerStopping(minecraftServer -> {
            Init.minecraftServer = null;
            // 关服时进行数据保存
            if (yteMain != null) {
                // 先停止并落盘（含电梯状态持久化），再清内存队列——顺序反了会把空状态写进文件
                yteMain.stop();
            }
            LiftDoorState.clearQueues();
            LiftModeState.clearQueues();
        });

        LOGGER.info("Yunzhu Transit Extension initialized successfully in {} ms.", System.currentTimeMillis() - startTime);
        REGISTRY.init();
    }

    public static void sendLiftAdoStart(long liftId, long stoppingCoolDown) {
        if (minecraftServer != null) {
            MinecraftServerHelper.iteratePlayers(minecraftServer, player ->
                    REGISTRY.sendPacketToClient(player, new PacketLiftAdoStart(liftId, stoppingCoolDown)));
        }
    }

    public static void sendLiftDoorOpen(long liftId, long stoppingCoolDown, boolean resetIdleDirection) {
        if (minecraftServer != null) {
            MinecraftServerHelper.iteratePlayers(minecraftServer, player ->
                    REGISTRY.sendPacketToClient(player,
                            new PacketLiftDoorControl(liftId, LiftDoorState.Command.OPEN,
                                    stoppingCoolDown, resetIdleDirection)));
        }
    }

    public static void sendLiftFireModeState(long liftId, LiftModeState.LiftMode mode, boolean fireman) {
        if (minecraftServer != null) {
            MinecraftServerHelper.iteratePlayers(minecraftServer, player ->
                    REGISTRY.sendPacketToClient(player,
                            new PacketLiftFireMode(liftId, false, mode.ordinal(), fireman)));
        }
    }

    public static Position blockPosToPosition(BlockPos blockPos) {
        return new Position(blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    public static BlockPos positionToBlockPos(Position position) {
        return new BlockPos((int) position.getX(), (int) position.getY(), (int) position.getZ());
    }

    public static boolean isChunkLoaded(World world, BlockPos blockPos) {
        return world.isChunkLoaded(blockPos.getX() >> 4, blockPos.getZ() >> 4);
    }

    public static <T extends SerializedDataBase> void sendMessageC2S(String key,
            @Nullable MinecraftServer minecraftServer, @Nullable World world,
            SerializedDataBase data, @Nullable Consumer<T> consumer,
            @Nullable Class<T> responseDataClass) {
        if (yteMain != null) {
            yteMain.sendMessageC2S(
                    world == null ? null : WORLD_ID_LIST.indexOf(getWorldId(world)),
                    new QueueObject(key, data,
                            consumer == null || minecraftServer == null ? null
                                    : responseData -> minecraftServer.execute(() -> consumer.accept(responseData)),
                            responseDataClass));
        }
    }

    private static String getWorldId(World world) {
        final Identifier identifier = MinecraftServerHelper.getWorldId(world);
        return String.format("%s/%s", identifier.getNamespace(), identifier.getPath());
    }

    public static void logException(Exception e, String message) {
        LOGGER.error(message, e);
    }
}
