package top.xfunny.mod.packet;

import org.mtr.mapping.holder.MinecraftServer;
import org.mtr.mapping.holder.ServerPlayerEntity;
import org.mtr.mapping.registry.PacketHandler;
import org.mtr.mapping.tool.PacketBufferReceiver;
import org.mtr.mapping.tool.PacketBufferSender;
import top.xfunny.mod.lift.LiftDoorState;

/**
 * 光幕（C→S）：客户端检测到玩家处于层门区域 / 攻击层门时上报。
 * 服务端仅写 DoorQueue 标记，门值盲区（v&lt;0.15）、强关期判定与
 * 反向续开均在 MixinLift.tick 侧结合完整上下文执行。
 */
public final class PacketLiftDoorCurtain extends PacketHandler {

	private final long liftId;
	private final boolean blocked;

	public PacketLiftDoorCurtain(PacketBufferReceiver packetBufferReceiver) {
		liftId = packetBufferReceiver.readLong();
		blocked = packetBufferReceiver.readBoolean();
	}

	public PacketLiftDoorCurtain(long liftId, boolean blocked) {
		this.liftId = liftId;
		this.blocked = blocked;
	}

	@Override
	public void write(PacketBufferSender packetBufferSender) {
		packetBufferSender.writeLong(liftId);
		packetBufferSender.writeBoolean(blocked);
	}

	@Override
	public void runServer(MinecraftServer minecraftServer, ServerPlayerEntity serverPlayerEntity) {
		final LiftDoorState.DoorQueue queue = LiftDoorState.getOrCreate(liftId);
		if (blocked) {
			queue.obstructionUntilMillis = System.currentTimeMillis() + 1500;
			queue.curtainFlags |= LiftDoorState.CURTAIN_TOUCH;
		} else {
			queue.obstructionUntilMillis = 0;
			queue.curtainFlags &= ~LiftDoorState.CURTAIN_TOUCH;
		}
	}
}
