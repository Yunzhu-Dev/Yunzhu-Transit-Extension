package top.xfunny.mod.packet;

import org.mtr.core.data.Lift;
import org.mtr.mapping.holder.BlockEntity;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.ClientWorld;
import org.mtr.mapping.holder.MinecraftClient;
import org.mtr.mapping.holder.MinecraftServer;
import org.mtr.mapping.holder.ServerPlayerEntity;
import org.mtr.mapping.holder.World;
import org.mtr.mapping.mapper.MinecraftServerHelper;
import org.mtr.mapping.registry.PacketHandler;
import org.mtr.mapping.tool.PacketBufferReceiver;
import org.mtr.mapping.tool.PacketBufferSender;
import org.mtr.mod.client.MinecraftClientData;
import top.xfunny.mixin.MixinLiftSchema;
import top.xfunny.mod.Init;
import top.xfunny.mod.config.YteLiftConfigStore;
import top.xfunny.mod.lift.LiftModeState;
import top.xfunny.mod.lift.LiftDoorMaintenance;

/**
 * 检修开门（双向包）：C→S 由 {@code MixinItemDriverKey} 在司机钥匙右击层门时发送；
 * 服务端应用后以同包 S→C 广播给所有玩家，使其他客户端播放同一动画。
 * {@code durationMs} 为开门动画时长（随机 1600~3200ms，切换时定死）；关门时长与曲线由各端按 liftId 查配置。
 */
public final class PacketLiftDoorMaintenance extends PacketHandler {

	private final long liftId;
	private final BlockPos blockPos;
	private final boolean open;
	private final long durationMs;

	public PacketLiftDoorMaintenance(PacketBufferReceiver packetBufferReceiver) {
		liftId = packetBufferReceiver.readLong();
		blockPos = BlockPos.fromLong(packetBufferReceiver.readLong());
		open = packetBufferReceiver.readBoolean();
		durationMs = packetBufferReceiver.readLong();
	}

	public PacketLiftDoorMaintenance(long liftId, BlockPos blockPos, boolean open, long durationMs) {
		this.liftId = liftId;
		this.blockPos = blockPos;
		this.open = open;
		this.durationMs = durationMs;
	}

	@Override
	public void write(PacketBufferSender packetBufferSender) {
		packetBufferSender.writeLong(liftId);
		packetBufferSender.writeLong(blockPos.asLong());
		packetBufferSender.writeBoolean(open);
		packetBufferSender.writeLong(durationMs);
	}

	@Override
	public void runServer(MinecraftServer minecraftServer, ServerPlayerEntity serverPlayerEntity) {
		if (!Init.isChunkLoaded(serverPlayerEntity.getEntityWorld(), blockPos)) {
			return;
		}
		final BlockEntity entity = serverPlayerEntity.getEntityWorld().getBlockEntity(blockPos);
		if (entity == null || !(entity.data instanceof LiftDoorMaintenance)) {
			return;
		}
		((LiftDoorMaintenance) entity.data).yte$setMaintenanceOpen(open, liftId, durationMs);

		if (open) {
			// 上锁（安全回路断开）：急停 + 弃全部内外呼，进入故障隔离
			LiftModeState.lock(liftId);
		} else {
			// 解锁：等层门关门动画播完（配置 closeMs）后自动就近平层救援
			LiftModeState.unlock(liftId);
			LiftModeState.requestMode(liftId, LiftModeState.LiftMode.MANUAL_DOOR_RECOVERY,
					YteLiftConfigStore.getDoorParams(liftId).closeMs);
		}
		MinecraftServerHelper.iteratePlayers(minecraftServer, player ->
				Init.REGISTRY.sendPacketToClient(player,
						new PacketLiftDoorMaintenance(liftId, blockPos, open, durationMs)));
	}

	@Override
	public void runClient() {
		final ClientWorld clientWorld = MinecraftClient.getInstance().getWorldMapped();
		if (clientWorld == null) {
			return;
		}
		// 镜像联锁到客户端：本地模拟同样冻结，避免被服务端逐帧同步拽回（“闪回”）
		// ponytail: 客户端仅镜像锁定标志，不走 lock/unlock API——instructionPurgePending
		// 只由服务端消费，客户端误置会残留；指令清空在下方手动完成
		LiftModeState.getOrCreate(liftId).maintenanceLocked = open;
		// 同步清空客户端指令副本：服务端上锁即弃全部内外呼，客户端提前清空
		// 可在广播到达时立刻停止朝旧目标模拟（服务端 purge 下一 tick 才生效）
		final Lift clientLift = MinecraftClientData.getLift(liftId);
		if (clientLift != null) {
			((MixinLiftSchema) clientLift).getInstructions().clear();
		}

		final BlockEntity entity = new World(clientWorld.data).getBlockEntity(blockPos);
		if (entity != null && entity.data instanceof LiftDoorMaintenance) {
			final LiftDoorMaintenance maintenance = (LiftDoorMaintenance) entity.data;
			// 回声守卫：点击者的本地同向过渡未播完时忽略广播，保留本地动画时序
			if (open == maintenance.yte$isMaintenanceOpen() && maintenance.yte$isMaintenanceAnimating()) {
				return;
			}
			maintenance.yte$setMaintenanceOpen(open, liftId, durationMs);
		}
	}
}
