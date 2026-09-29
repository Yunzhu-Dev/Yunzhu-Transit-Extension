package top.xfunny.mod.packet;

import org.mtr.mapping.holder.MinecraftServer;
import org.mtr.mapping.holder.ServerPlayerEntity;
import org.mtr.mapping.registry.PacketHandler;
import org.mtr.mapping.tool.PacketBufferReceiver;
import org.mtr.mapping.tool.PacketBufferSender;
import top.xfunny.mod.lift.LiftCarCallState;

/** 消防员内呼登记（C2S）：直连服务端 pressButton，绕过 MTR press 包的 HTTP 调度链路。 */
public final class PacketLiftCarCall extends PacketHandler {

    private final long liftId;
    private final int floorIndex;

    public PacketLiftCarCall(PacketBufferReceiver packetBufferReceiver) {
        liftId = packetBufferReceiver.readLong();
        floorIndex = packetBufferReceiver.readInt();
    }

    public PacketLiftCarCall(long liftId, int floorIndex) {
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
        LiftCarCallState.request(liftId, floorIndex);
    }
}
