package top.xfunny.mod.packet;

import org.mtr.mapping.holder.MinecraftServer;
import org.mtr.mapping.holder.ServerPlayerEntity;
import org.mtr.mapping.registry.PacketHandler;
import org.mtr.mapping.tool.PacketBufferReceiver;
import org.mtr.mapping.tool.PacketBufferSender;
import top.xfunny.mod.lift.LiftFloorCancelState;

public final class PacketLiftFloorCancel extends PacketHandler {

    private final long liftId;
    private final int floorIndex;

    public PacketLiftFloorCancel(PacketBufferReceiver packetBufferReceiver) {
        liftId = packetBufferReceiver.readLong();
        floorIndex = packetBufferReceiver.readInt();
    }

    public PacketLiftFloorCancel(long liftId, int floorIndex) {
        this.liftId = liftId;
        this.floorIndex = floorIndex;
    }

    @Override
    public void write(PacketBufferSender packetBufferSender) {
        packetBufferSender.writeLong(liftId);
        packetBufferSender.writeInt(floorIndex);
    }

    @Override
    public void runServer(MinecraftServer minecraftServer, ServerPlayerEntity serverPlayerEntity) {
        LiftFloorCancelState.request(liftId, floorIndex);
    }
}
