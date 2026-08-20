package top.xfunny.mod.packet;

import org.mtr.mapping.holder.MinecraftServer;
import org.mtr.mapping.holder.ServerPlayerEntity;
import org.mtr.mapping.registry.PacketHandler;
import org.mtr.mapping.tool.PacketBufferReceiver;
import org.mtr.mapping.tool.PacketBufferSender;
import org.mtr.core.data.Lift;
import org.mtr.mod.client.MinecraftClientData;
import top.xfunny.mixin.MixinLiftSchema;
import top.xfunny.mod.lift.LiftDisplayDirectionState;
import top.xfunny.mod.lift.LiftDoorControlState;

public final class PacketLiftDoorControl extends PacketHandler {

    private final long liftId;
    private final LiftDoorControlState.Command command;
    private final long stoppingCoolDown;
    private final boolean resetIdleDirection;

    public PacketLiftDoorControl(PacketBufferReceiver packetBufferReceiver) {
        liftId = packetBufferReceiver.readLong();
        final int commandInt = packetBufferReceiver.readInt();
        command = commandInt == 0 ? LiftDoorControlState.Command.OPEN
                : commandInt == 1 ? LiftDoorControlState.Command.CLOSE
                : LiftDoorControlState.Command.HOLD_OPEN;
        stoppingCoolDown = packetBufferReceiver.readLong();
        resetIdleDirection = packetBufferReceiver.readBoolean();
    }

    public PacketLiftDoorControl(long liftId, LiftDoorControlState.Command command) {
        this.liftId = liftId;
        this.command = command;
        stoppingCoolDown = -1;
        resetIdleDirection = false;
    }

    public PacketLiftDoorControl(long liftId, LiftDoorControlState.Command command,
            long stoppingCoolDown, boolean resetIdleDirection) {
        this.liftId = liftId;
        this.command = command;
        this.stoppingCoolDown = stoppingCoolDown;
        this.resetIdleDirection = resetIdleDirection;
    }

    @Override
    public void write(PacketBufferSender packetBufferSender) {
        packetBufferSender.writeLong(liftId);
        packetBufferSender.writeInt(command == LiftDoorControlState.Command.OPEN ? 0
                : command == LiftDoorControlState.Command.CLOSE ? 1 : 2);
        packetBufferSender.writeLong(stoppingCoolDown);
        packetBufferSender.writeBoolean(resetIdleDirection);
    }

    @Override
    public void runServer(MinecraftServer minecraftServer, ServerPlayerEntity serverPlayerEntity) {
        LiftDoorControlState.request(liftId, command);
    }

    @Override
    public void runClient() {
        if (command != LiftDoorControlState.Command.OPEN && command != LiftDoorControlState.Command.HOLD_OPEN) {
            return;
        }
        final Lift lift = MinecraftClientData.getLift(liftId);
        if (lift != null) {
            if (stoppingCoolDown >= 0) {
                ((MixinLiftSchema) lift).setStoppingCoolDown(stoppingCoolDown);
            }
            if (resetIdleDirection) {
                LiftDisplayDirectionState.get(liftId).resetForIdleDoorCycle();
            }
        }
    }
}
