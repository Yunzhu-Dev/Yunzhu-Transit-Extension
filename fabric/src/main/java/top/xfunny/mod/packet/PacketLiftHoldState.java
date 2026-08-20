package top.xfunny.mod.packet;

import org.mtr.mapping.holder.MinecraftServer;
import org.mtr.mapping.holder.ServerPlayerEntity;
import org.mtr.mapping.registry.PacketHandler;
import org.mtr.mapping.tool.PacketBufferReceiver;
import org.mtr.mapping.tool.PacketBufferSender;
import top.xfunny.mod.Init;
import top.xfunny.mod.lift.LiftDoorControlState;

public final class PacketLiftHoldState extends PacketHandler {

    private final long liftId;
    private final boolean query;
    private final boolean active;
    private final long remainingMillis;

    public PacketLiftHoldState(PacketBufferReceiver packetBufferReceiver) {
        liftId = packetBufferReceiver.readLong();
        query = packetBufferReceiver.readBoolean();
        active = packetBufferReceiver.readBoolean();
        remainingMillis = packetBufferReceiver.readLong();
    }

    private PacketLiftHoldState(long liftId, boolean query, boolean active, long remainingMillis) {
        this.liftId = liftId;
        this.query = query;
        this.active = active;
        this.remainingMillis = remainingMillis;
    }

    public static PacketLiftHoldState query(long liftId) {
        return new PacketLiftHoldState(liftId, true, false, 0);
    }

    public static PacketLiftHoldState update(long liftId, boolean active, long remainingMillis) {
        return new PacketLiftHoldState(liftId, false, active, remainingMillis);
    }

    @Override
    public void write(PacketBufferSender packetBufferSender) {
        packetBufferSender.writeLong(liftId);
        packetBufferSender.writeBoolean(query);
        packetBufferSender.writeBoolean(active);
        packetBufferSender.writeLong(remainingMillis);
    }

    @Override
    public void runServer(MinecraftServer minecraftServer, ServerPlayerEntity serverPlayerEntity) {
        if (query) {
            final long remaining = LiftDoorControlState.getHoldRemainingMillis(liftId);
            Init.REGISTRY.sendPacketToClient(serverPlayerEntity,
                    update(liftId, LiftDoorControlState.isHoldActive(liftId) && remaining > 0, remaining));
        }
    }

    @Override
    public void runClient() {
        if (!query) {
            LiftDoorControlState.updateClientHold(liftId, active, remainingMillis);
        }
    }
}
