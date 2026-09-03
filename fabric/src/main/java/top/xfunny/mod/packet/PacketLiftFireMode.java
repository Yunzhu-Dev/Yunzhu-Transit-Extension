package top.xfunny.mod.packet;

import org.mtr.mapping.holder.MinecraftServer;
import org.mtr.mapping.holder.ServerPlayerEntity;
import org.mtr.mapping.mapper.MinecraftServerHelper;
import org.mtr.mapping.registry.PacketHandler;
import org.mtr.mapping.tool.PacketBufferReceiver;
import org.mtr.mapping.tool.PacketBufferSender;
import top.xfunny.mod.Init;
import top.xfunny.mod.config.YteLiftConfigStore;
import top.xfunny.mod.lift.LiftModeState;

/**
 * 消防模式测触发（双向包）：C→S 由 {@code TestLiftButtons} 在潜行+空手右键时发送；
 */
public final class PacketLiftFireMode extends PacketHandler {

	private final long liftId;
	private final boolean fireActive;
	private final int resultMode;
	private final boolean resultFireman;

	public PacketLiftFireMode(PacketBufferReceiver packetBufferReceiver) {
		liftId = packetBufferReceiver.readLong();
		fireActive = packetBufferReceiver.readBoolean();
		resultMode = packetBufferReceiver.readInt();
		resultFireman = packetBufferReceiver.readBoolean();
	}

	public PacketLiftFireMode(long liftId, boolean fireActive, int resultMode, boolean resultFireman) {
		this.liftId = liftId;
		this.fireActive = fireActive;
		this.resultMode = resultMode;
		this.resultFireman = resultFireman;
	}

	@Override
	public void write(PacketBufferSender packetBufferSender) {
		packetBufferSender.writeLong(liftId);
		packetBufferSender.writeBoolean(fireActive);
		packetBufferSender.writeInt(resultMode);
		packetBufferSender.writeBoolean(resultFireman);
	}

	@Override
	public void runServer(MinecraftServer minecraftServer, ServerPlayerEntity serverPlayerEntity) {
		if (fireActive) {
			LiftModeState.activateFireMode(liftId);
		} else {
			LiftModeState.exitFireMode(liftId);
		}
		MinecraftServerHelper.iteratePlayers(minecraftServer, player ->
				Init.REGISTRY.sendPacketToClient(player,
						new PacketLiftFireMode(liftId, fireActive,
								LiftModeState.getMode(liftId).ordinal(),
								LiftModeState.getFireMode(liftId) == LiftModeState.FireMode.FIREMAN_MODE)));
	}

	@Override
	public void runClient() {
		// 镜像到客户端 LiftModeState：按服务端应用后的实际模式
		final LiftModeState.State state = LiftModeState.getOrCreate(liftId);
		state.mode = resultMode >= 0 && resultMode < LiftModeState.LiftMode.values().length
				? LiftModeState.LiftMode.values()[resultMode]
				: LiftModeState.LiftMode.NORMAL;
		if (state.mode == LiftModeState.LiftMode.FIRE_MODE) {
			state.fireMode = resultFireman
					? LiftModeState.FireMode.FIREMAN_MODE
					: LiftModeState.FireMode.FIRE_RECALL;
			state.fireFloorNumber = YteLiftConfigStore.getFireRecallFloor(liftId);
		} else {
			state.fireMode = null;
			state.fireFloorNumber = null;
		}
	}
}
