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
import top.xfunny.mod.lift.LiftDoorState;

public final class PacketLiftDoorControl extends PacketHandler {

    private final long liftId;
    private final LiftDoorState.Command command;
    private final long stoppingCoolDown;
    private final boolean resetIdleDirection;

    public PacketLiftDoorControl(PacketBufferReceiver packetBufferReceiver) {
        liftId = packetBufferReceiver.readLong();
        final int commandInt = packetBufferReceiver.readInt();
        command = commandInt == 0 ? LiftDoorState.Command.OPEN
                : commandInt == 1 ? LiftDoorState.Command.CLOSE
                : LiftDoorState.Command.HOLD_OPEN;
        command = commandInt == 0 ? LiftDoorControlState.Command.OPEN
                : commandInt == 1 ? LiftDoorControlState.Command.CLOSE
                : commandInt == 2 ? LiftDoorControlState.Command.HOLD_OPEN
                : LiftDoorControlState.Command.RELEASE_CLOSE;
        stoppingCoolDown = packetBufferReceiver.readLong();
        resetIdleDirection = packetBufferReceiver.readBoolean();
    }

    public PacketLiftDoorControl(long liftId, LiftDoorState.Command command) {
        this.liftId = liftId;
        this.command = command;
        stoppingCoolDown = -1;
        resetIdleDirection = false;
    }

    public PacketLiftDoorControl(long liftId, LiftDoorState.Command command,
            long stoppingCoolDown, boolean resetIdleDirection) {
        this.liftId = liftId;
        this.command = command;
        this.stoppingCoolDown = stoppingCoolDown;
        this.resetIdleDirection = resetIdleDirection;
    }

    @Override
    public void write(PacketBufferSender packetBufferSender) {
        packetBufferSender.writeLong(liftId);
        packetBufferSender.writeInt(command == LiftDoorState.Command.OPEN ? 0
                : command == LiftDoorState.Command.CLOSE ? 1 : 2);
        packetBufferSender.writeInt(command == LiftDoorControlState.Command.OPEN ? 0
                : command == LiftDoorControlState.Command.CLOSE ? 1
                : command == LiftDoorControlState.Command.HOLD_OPEN ? 2 : 3);
        packetBufferSender.writeLong(stoppingCoolDown);
        packetBufferSender.writeBoolean(resetIdleDirection);
    }

    @Override
    public void runServer(MinecraftServer minecraftServer, ServerPlayerEntity serverPlayerEntity) {
        LiftDoorState.request(liftId, command);
    }

    @Override
    public void runClient() {
        if (command != LiftDoorState.Command.OPEN && command != LiftDoorState.Command.HOLD_OPEN) {
            return;
        }
        final Lift lift = MinecraftClientData.getLift(liftId);
        if (lift != null) {
            if (stoppingCoolDown >= 0) {
                ((MixinLiftSchema) lift).setStoppingCoolDown(LiftDoorControlState.reconcileClientOpenCoolDown(
                        liftId, stoppingCoolDown));
                ((MixinLiftSchema) lift).setStoppingCoolDown(stoppingCoolDown);
            }
            if (resetIdleDirection) {
                LiftDisplayDirectionState.get(liftId).resetForIdleDoorCycle();
            }
        }
    }
}
