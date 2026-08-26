package top.xfunny.mixin;

import org.mtr.core.data.Lift;
import org.mtr.mapping.holder.BlockEntity;
import org.mtr.mapping.holder.BlockHitResult;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.Hand;
import org.mtr.mapping.holder.HitResult;
import org.mtr.mapping.holder.PlayerEntity;
import org.mtr.mapping.holder.World;
import org.mtr.mapping.mapper.ItemExtension;
import org.mtr.mod.client.MinecraftClientData;
import org.mtr.mod.item.ItemDriverKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.xfunny.mod.client.InitClient;
import top.xfunny.mod.lift.LiftModeState;
import top.xfunny.mod.lift.LiftDoorMaintenance;
import top.xfunny.mod.packet.PacketLiftDoorMaintenance;
import top.xfunny.mod.util.LiftShaftLocator;

import java.util.concurrent.ThreadLocalRandom;

/**
 * 三角钥匙临时接入：MTR 司机钥匙右键电梯层门 → 切换检修开门（带动画）。
 * 注意：mixin 目标为 ItemExtension（useWithoutResult 的声明类），用 instanceof
 * 门控仅对司机钥匙（含 Depot 司机钥匙）生效。
 * 点击者本地即时生效；其他客户端经 PacketLiftDoorMaintenance 的 S→C 广播同步动画，
 * 服务端置联锁并进入故障隔离（MixinLift.pressButton 注入拒绝派梯）。
 */
@Mixin(value = ItemExtension.class, remap = false)
public abstract class MixinItemDriverKey {

	/** 检修开门动画随机时长范围（ms），固定值不可配置。 */
	@Unique
    private static final long MAINTENANCE_OPEN_MIN_MS = 1600;
	@Unique
    private static final long MAINTENANCE_OPEN_MAX_MS = 3200;

	@Inject(method = "useWithoutResult", at = @At("HEAD"), cancellable = true)
	private void yte$toggleMaintenanceDoor(World world, PlayerEntity player, Hand hand, CallbackInfo ci) {
		if (!world.isClient() || !((Object) this instanceof ItemDriverKey)) {
			return;
		}
		final HitResult hitResult = player.raycast(4.5, 0, false);
		if (!BlockHitResult.isInstance(hitResult)) {
			return;
		}
		final BlockPos blockPos = BlockHitResult.cast(hitResult).getBlockPos();
		final BlockEntity blockEntity = world.getBlockEntity(blockPos);
		if (blockEntity == null || !(blockEntity.data instanceof LiftDoorMaintenance)) {
			return;
		}

		final Long liftId = LiftShaftLocator.findForDoor(blockPos);
		if (liftId == null) {
			return;
		}

		final boolean open = !((LiftDoorMaintenance) blockEntity.data).yte$isMaintenanceOpen();
		// 开门时长：1600~3200ms 随机，切换时定死并随包广播给所有玩家
		final long durationMs = open ? ThreadLocalRandom.current().nextLong(
				MAINTENANCE_OPEN_MIN_MS, MAINTENANCE_OPEN_MAX_MS + 1) : 0;
		// 本地立即生效；服务端同步标志 + 置联锁（禁止该电梯运行）并广播其他客户端
		((LiftDoorMaintenance) blockEntity.data).yte$setMaintenanceOpen(open, liftId, durationMs);
		// 本地立即联锁：点击视角电梯即刻急停/解锁，不等服务端广播往返；
		// 同时清空本地指令副本，与服务端“上锁即弃全部内外呼”对齐。
		// ponytail: 客户端仅镜像锁定标志（同 PacketLiftDoorMaintenance.runClient），
		// 服务端才走 lock/unlock/requestMode 完整流程
		LiftModeState.getOrCreate(liftId).maintenanceLocked = open;
		final Lift localLift = MinecraftClientData.getLift(liftId);
		if (localLift != null) {
			((MixinLiftSchema) localLift).getInstructions().clear();
		}
		InitClient.REGISTRY_CLIENT.sendPacketToServer(new PacketLiftDoorMaintenance(liftId, blockPos, open, durationMs));
		ci.cancel();
	}
}
