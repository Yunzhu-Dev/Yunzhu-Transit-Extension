package top.xfunny.mod.packet;

import org.mtr.mapping.holder.BlockEntity;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.MinecraftServer;
import org.mtr.mapping.holder.ServerPlayerEntity;
import org.mtr.mapping.registry.PacketHandler;
import org.mtr.mapping.tool.PacketBufferReceiver;
import org.mtr.mapping.tool.PacketBufferSender;
import top.xfunny.mod.Init;
import top.xfunny.mod.lift.LiftDoorControlState;
import top.xfunny.mod.lift.LiftDoorMaintenance;

/**
 * 三角钥匙预留（C→S）：切换某层门的检修开门状态并触发该电梯的联锁。
 * 目前没有发送方；钥匙物品落地后直接复用此包。
 */
public final class PacketLiftDoorMaintenance extends PacketHandler {

	private final long liftId;
	private final BlockPos blockPos;
	private final boolean open;

	public PacketLiftDoorMaintenance(PacketBufferReceiver packetBufferReceiver) {
		liftId = packetBufferReceiver.readLong();
		blockPos = BlockPos.fromLong(packetBufferReceiver.readLong());
		open = packetBufferReceiver.readBoolean();
	}

	public PacketLiftDoorMaintenance(long liftId, BlockPos blockPos, boolean open) {
		this.liftId = liftId;
		this.blockPos = blockPos;
		this.open = open;
	}

	@Override
	public void write(PacketBufferSender packetBufferSender) {
		packetBufferSender.writeLong(liftId);
		packetBufferSender.writeLong(blockPos.asLong());
		packetBufferSender.writeBoolean(open);
	}

	@Override
	public void runServer(MinecraftServer minecraftServer, ServerPlayerEntity serverPlayerEntity) {
		if (!Init.isChunkLoaded(serverPlayerEntity.getEntityWorld(), blockPos)) {
			return;
		}
		final BlockEntity entity = serverPlayerEntity.getEntityWorld().getBlockEntity(blockPos);
		if (entity != null && entity.data instanceof LiftDoorMaintenance) {
			((LiftDoorMaintenance) entity.data).yte$setMaintenanceOpen(open);
			// 门 NBT 经方块实体同步广播给所有客户端；联锁写在该电梯的服务端队列
			LiftDoorControlState.getOrCreate(liftId).maintenanceLocked = open;
		}
	}
}
