package top.xfunny.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import org.mtr.core.data.Lift;
import org.mtr.core.data.LiftDirection;
import org.mtr.core.data.LiftFloor;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArraySet;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectObjectImmutablePair;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.ClickableWidget;
import org.mtr.mapping.holder.ClientWorld;
import org.mtr.mapping.holder.World;
import org.mtr.mapping.mapper.TextHelper;
import org.mtr.mod.client.MinecraftClientData;
import org.mtr.mod.data.IGui;
import org.mtr.mod.screen.DashboardList;
import org.mtr.mod.screen.DashboardListItem;
import org.mtr.mod.screen.LiftSelectionScreen;
import org.mtr.mod.screen.MTRScreenBase;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.xfunny.mod.lift.FiremanOperationType;
import top.xfunny.mod.lift.LiftDoorButtonLightMode;
import top.xfunny.mod.lift.LiftDoorState;
import top.xfunny.mod.lift.LiftDisplayDirectionState;
import top.xfunny.mod.lift.LiftFloorCancelMode;
import top.xfunny.mod.lift.LiftModeState;
import top.xfunny.mod.client.InitClient;
import top.xfunny.mod.client.screen.widget.LiftSelectionButtonWidget;
import top.xfunny.mod.config.YteLiftConfigStore;
import top.xfunny.mod.packet.PacketLiftCarCall;
import top.xfunny.mod.packet.PacketLiftDoorControl;
import top.xfunny.mod.packet.PacketLiftFloorCancel;
import top.xfunny.mod.util.GetLiftDetails;

@Mixin(value = LiftSelectionScreen.class, remap = false)
public abstract class MixinLiftSelectionScreen extends MTRScreenBase {

    @Shadow @Final private DashboardList selectionList;
    @Shadow @Final private ObjectArrayList<BlockPos> floorLevels;
    @Shadow @Final private ObjectArrayList<String> floorDescriptions;
    @Shadow @Final private long liftId;

    @Unique private static final long YTE_DOUBLE_CLICK_WINDOW = 400;
    @Unique private static final long YTE_LONG_PRESS_TIME = 750;
    @Unique private static final long YTE_DOOR_REPEAT_INTERVAL = 250;
    @Unique private static final long YTE_TIMED_LIGHT_DURATION = 1000;

    @Unique private LiftSelectionButtonWidget yte$holdOpenButton;
    @Unique private LiftSelectionButtonWidget yte$openDoorButton;
    @Unique private LiftSelectionButtonWidget yte$closeDoorButton;
    @Unique private boolean yte$clearDoorButtonFocus;
    @Unique private boolean yte$lastHoldEnabled;
    @Unique private LiftDoorState.Command yte$pressedDoorCommand;
    @Unique private long yte$nextDoorRepeatTime;
    @Unique private boolean yte$holdCommandActive;
    @Unique private long yte$holdOpenLightUntil;
    @Unique private long yte$openDoorLightUntil;
    @Unique private long yte$closeDoorLightUntil;
    @Unique private int yte$lastFloorClickIndex = -1;
    @Unique private long yte$lastFloorClickTime;
    @Unique private boolean yte$lastFloorClickWasSelected;
    @Unique private int yte$heldFloorIndex = -1;
    @Unique private long yte$floorHoldStartTime;
    @Unique private boolean yte$floorHoldArmed;
    /** 消防员长按楼层键（HOLD_FLOOR_BUTTON）登记+关门 中，松开时未出发则重开门 */
    @Unique private boolean yte$firemanFloorPressed;
    /** 类型2延迟登记：长按的楼层索引，完全关门后登记（-1 无） */
    @Unique private int yte$firemanHeldFloorIndex = -1;
    /** 类型2延迟登记：长按行的目标楼层号（LiftInstruction.floor） */
    @Unique private int yte$firemanHeldFloorTarget;
    /** 类型2延迟登记：登记包已发出、等待服务端指令同步回客户端（行灯保持常亮桥接空窗） */
    @Unique private boolean yte$firemanRegistrationSent;
    @Unique private boolean yte$floorHoldPointerInside;

    @Inject(method = "lambda$new$0", at = @At("TAIL"))
    private void yte$getRealFloorDetailsForSelection(
            ClientWorld clientWorld, Lift lift, LiftFloor floor, CallbackInfo ci) {
        floorDescriptions.remove(floorDescriptions.size() - 1);
        final ObjectObjectImmutablePair<LiftDirection, ObjectObjectImmutablePair<String, String>> details =
                GetLiftDetails.getLiftDetails(new World(clientWorld.data), lift,
                        org.mtr.mod.Init.positionToBlockPos(floor.getPosition()));
        floorDescriptions.add(String.format("%s %s",
                details.right().left(), IGui.formatStationName(details.right().right())));
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void yte$createDoorButtons(long liftId, CallbackInfo ci) {
        yte$holdOpenButton = new LiftSelectionButtonWidget(0, 0, 0, IGui.SQUARE_SIZE,
                TextHelper.translatable("gui.yte.lift_hold_open"), button -> yte$sendDoorCommand(LiftDoorState.Command.HOLD_OPEN));
        yte$openDoorButton = new LiftSelectionButtonWidget(0, 0, 0, IGui.SQUARE_SIZE,
                TextHelper.literal("◀▶"), button -> yte$sendDoorCommand(LiftDoorState.Command.OPEN));
        yte$closeDoorButton = new LiftSelectionButtonWidget(0, 0, 0, IGui.SQUARE_SIZE,
                TextHelper.literal("▶◀"), button -> yte$sendDoorCommand(LiftDoorState.Command.CLOSE));
    }

    @Inject(method = "onPress", at = @At("HEAD"))
    private void yte$handleFloorButtonPress(
            DashboardListItem ignoredItem, int index, CallbackInfo ci) {
        final Lift lift = MinecraftClientData.getLift(liftId);
        if (lift == null || index < 0 || index >= floorLevels.size()) {
            return;
        }

        final MixinLiftSchema schema = (MixinLiftSchema) lift;
        final int selectedFloor = lift.getFloorIndex(org.mtr.mod.Init.blockPosToPosition(
                floorLevels.get(floorLevels.size() - index - 1)));
        final boolean selected = lift.hasInstruction(selectedFloor).contains(LiftDirection.NONE);
        final long currentTime = System.currentTimeMillis();
        final LiftFloorCancelMode cancelMode = YteLiftConfigStore.getFloorCancelMode(liftId);
        final boolean firemanMode = LiftModeState.getFireMode(liftId) == LiftModeState.FireMode.FIREMAN_MODE;

        // 消防员模式：出发后无法取消楼层（双击取消禁用，服务端亦拦截）
        if (firemanMode) {
            final FiremanOperationType operation = YteLiftConfigStore.getFiremanOperation(liftId);
            if (operation == FiremanOperationType.HOLD_FLOOR_BUTTON) {
                // 类型2：长按楼层键驱动关门，完全关门后才登记该层（松开未关则重开门）
                yte$firemanHeldFloorIndex = index;
                yte$firemanHeldFloorTarget = selectedFloor;
                yte$firemanFloorPressed = true;
                yte$firemanRegistrationSent = false;
                yte$sendDoorCommand(LiftDoorState.Command.CLOSE);
                ci.cancel();
                return;
            }
            // 类型1：关门后才能登记楼层（门未关时点击无效）
            if (operation == FiremanOperationType.HOLD_DOOR_BUTTON && lift.getDoorValue() != 0) {
                ci.cancel();
                return;
            }
            // 类型3 / 类型1门已关：MTR onPress 正常登记
            return;
        }

        if (cancelMode == LiftFloorCancelMode.DOUBLE_CLICK) {
            if (selected && yte$lastFloorClickWasSelected && yte$lastFloorClickIndex == index
                    && currentTime - yte$lastFloorClickTime <= YTE_DOUBLE_CLICK_WINDOW) {
                yte$sendFloorCancellation(selectedFloor);
                yte$resetFloorClickTracking();
                ci.cancel();
                return;
            }
            yte$lastFloorClickIndex = index;
            yte$lastFloorClickTime = currentTime;
            yte$lastFloorClickWasSelected = selected;
        } else if (selected) {
            yte$heldFloorIndex = index;
            yte$floorHoldStartTime = currentTime;
            yte$floorHoldArmed = true;
        }

        final int currentFloor = lift.getFloorIndex(lift.getCurrentFloor().getPosition());
        if (selectedFloor == currentFloor
                && schema.getSpeed() == 0
                && schema.getInstructions().isEmpty()
                && schema.getStoppingCoolDown() <= YteLiftConfigStore.getDoorParams(liftId).runDelay
                && lift.getDoorValue() == 0) {
            LiftDisplayDirectionState.get(liftId).resetForCarSameFloorOpen();
        }
    }

    @Inject(
            method = "onPress",
            at = @At(value = "INVOKE", target = "Lorg/mtr/mod/screen/LiftSelectionScreen;onClose2()V"),
            cancellable = true
    )
    private void yte$keepSelectionScreenOpen(DashboardListItem item, int index, CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "init2", at = @At("TAIL"))
    private void yte$initDoorButtons(CallbackInfo ci) {
        selectionList.height = Math.max(selectionList.height - IGui.SQUARE_SIZE, IGui.SQUARE_SIZE * 2);
        final int buttonY = selectionList.y + selectionList.height;

        addChild(new ClickableWidget(yte$holdOpenButton));
        addChild(new ClickableWidget(yte$openDoorButton));
        addChild(new ClickableWidget(yte$closeDoorButton));

        yte$lastHoldEnabled = YteLiftConfigStore.isDoorHoldEnabled(liftId);
        yte$updateDoorButtonLayout(buttonY);
    }

    /** 类型2：按住未登记期间行亮（注入 MTR tick2 行灯判定，hasInstruction 返回副本可安全修改）；
     * 登记在途（sent）期间保持常亮桥接同步空窗；服务端指令到达后由 hasInstruction 原生常亮、到站熄灭。 */
    @ModifyExpressionValue(
            method = "tick2",
            at = @At(value = "INVOKE",
                    target = "Lorg/mtr/core/data/Lift;hasInstruction(I)Lorg/mtr/libraries/it/unimi/dsi/fastutil/objects/ObjectArraySet;")
    )
    private ObjectArraySet<LiftDirection> yte$lightHeldFloorRow(
            ObjectArraySet<LiftDirection> original, @Local(ordinal = 0) int floorLevelIndex) {
        if ((yte$firemanFloorPressed || yte$firemanRegistrationSent) && yte$firemanHeldFloorIndex >= 0
                && floorLevelIndex == floorLevels.size() - 1 - yte$firemanHeldFloorIndex) {
            original.add(LiftDirection.NONE);
        }
        return original;
    }

    @Inject(method = "tick2", at = @At("TAIL"))
    private void yte$updateDoorButtonAvailability(CallbackInfo ci) {
        if (yte$clearDoorButtonFocus) {
            setFocused(null);
            yte$clearDoorButtonFocus = false;
        }

        final boolean holdEnabled = YteLiftConfigStore.isDoorHoldEnabled(liftId);
        if (holdEnabled != yte$lastHoldEnabled) {
            yte$lastHoldEnabled = holdEnabled;
            yte$updateDoorButtonLayout(selectionList.y + selectionList.height);
        }

        final Lift lift = MinecraftClientData.getLift(liftId);
        yte$holdOpenButton.setActiveMapped(true);
        yte$openDoorButton.setActiveMapped(true);
        yte$closeDoorButton.setActiveMapped(true);

        final long currentTime = System.currentTimeMillis();
        if (yte$pressedDoorCommand != null) {
            final LiftSelectionButtonWidget pressedButton = yte$getDoorButton(yte$pressedDoorCommand);
            if (pressedButton == null || !pressedButton.getVisibleMapped()) {
                yte$finishDoorButtonPress(currentTime);
            } else if (yte$pressedDoorCommand != LiftDoorState.Command.HOLD_OPEN
                    && currentTime >= yte$nextDoorRepeatTime) {
                yte$sendDoorCommand(yte$pressedDoorCommand);
                yte$nextDoorRepeatTime = currentTime + YTE_DOOR_REPEAT_INTERVAL;
            }
        }

        if (yte$floorHoldArmed) {
            if (selectionList.getHoverItemIndex() != yte$heldFloorIndex) {
                yte$resetFloorHold();
            } else if (currentTime - yte$floorHoldStartTime >= YTE_LONG_PRESS_TIME) {
                yte$cancelHeldFloorIfAllowed(lift);
            }
        }

        // 类型2：门完全关闭后自动登记长按的楼层；登记在途期间行灯保持常亮，
        // 直到服务端指令同步回客户端（hasInstruction）或电梯出发后交给原生行灯
        if (yte$firemanHeldFloorIndex >= 0 && lift != null) {
            if (((MixinLiftSchema) lift).getSpeed() != 0) {
                // 已出发：登记必然生效，交给原生行灯
                yte$firemanHeldFloorIndex = -1;
                yte$firemanRegistrationSent = false;
            } else if (!yte$firemanRegistrationSent && lift.getDoorValue() == 0) {
                InitClient.REGISTRY_CLIENT.sendPacketToServer(
                        new PacketLiftCarCall(liftId, yte$firemanHeldFloorTarget));
                yte$firemanRegistrationSent = true;
            } else if (yte$firemanRegistrationSent
                    && lift.hasInstruction(yte$firemanHeldFloorTarget).contains(LiftDirection.NONE)) {
                // 真实指令已同步到客户端：交给原生行灯
                yte$firemanHeldFloorIndex = -1;
                yte$firemanRegistrationSent = false;
            }
        }

        yte$updateDoorButtonLights(currentTime);
    }

    @Unique
    private void yte$updateDoorButtonLayout(int buttonY) {
        final boolean holdEnabled = YteLiftConfigStore.isDoorHoldEnabled(liftId);
        yte$holdOpenButton.setVisibleMapped(holdEnabled);

        if (holdEnabled) {
            final int buttonWidth = selectionList.width / 3;
            yte$holdOpenButton.setX2(selectionList.x);
            yte$holdOpenButton.setY2(buttonY);
            yte$holdOpenButton.setWidth2(buttonWidth);
            yte$openDoorButton.setX2(selectionList.x + buttonWidth);
            yte$openDoorButton.setY2(buttonY);
            yte$openDoorButton.setWidth2(buttonWidth);
            yte$closeDoorButton.setX2(selectionList.x + buttonWidth * 2);
            yte$closeDoorButton.setY2(buttonY);
            yte$closeDoorButton.setWidth2(selectionList.width - buttonWidth * 2);
        } else {
            final int buttonWidth = selectionList.width / 2;
            yte$openDoorButton.setX2(selectionList.x);
            yte$openDoorButton.setY2(buttonY);
            yte$openDoorButton.setWidth2(buttonWidth);
            yte$closeDoorButton.setX2(selectionList.x + buttonWidth);
            yte$closeDoorButton.setY2(buttonY);
            yte$closeDoorButton.setWidth2(selectionList.width - buttonWidth);
        }
    }

    @Override
    public boolean mouseClicked2(double mouseX, double mouseY, int button) {
        if (button == 0) {
            final LiftDoorState.Command command = yte$getDoorCommandAt(mouseX, mouseY);
            if (command != null) {
                // 类型2（HOLD_FLOOR_BUTTON）：关门键屏蔽——关门由长按楼层键驱动
                if (command == LiftDoorState.Command.CLOSE
                        && LiftModeState.getFireMode(liftId) == LiftModeState.FireMode.FIREMAN_MODE
                        && YteLiftConfigStore.getFiremanOperation(liftId) == FiremanOperationType.HOLD_FLOOR_BUTTON) {
                    return true;
                }
                // 类型3（REGISTER_TO_CLOSE）：未登记楼层时关门无效（开门不受限）
                if (command == LiftDoorState.Command.CLOSE
                        && LiftModeState.getFireMode(liftId) == LiftModeState.FireMode.FIREMAN_MODE
                        && YteLiftConfigStore.getFiremanOperation(liftId) == FiremanOperationType.REGISTER_TO_CLOSE
                        && yte$firemanInstructionsEmpty()) {
                    return true;
                }
                yte$startDoorButtonPress(command);
                return true;
            }

            if (yte$isFloorListPosition(mouseX, mouseY)) {
                final Lift lift = MinecraftClientData.getLift(liftId);
                final int selectionIndex = selectionList.getHoverItemIndex();
                if (lift != null && selectionIndex >= 0 && selectionIndex < floorLevels.size()) {
                    final int floorIndex = yte$getFloorIndex(lift, selectionIndex);
                    final boolean selected = lift.hasInstruction(floorIndex).contains(LiftDirection.NONE);
                    if (YteLiftConfigStore.getFloorCancelMode(liftId) == LiftFloorCancelMode.DOUBLE_CLICK) {
                        if (!selected) {
                            yte$resetFloorClickTracking();
                            return super.mouseClicked2(mouseX, mouseY, button);
                        }
                        final long currentTime = System.currentTimeMillis();
                        if (yte$lastFloorClickIndex == selectionIndex
                                && currentTime - yte$lastFloorClickTime <= YTE_DOUBLE_CLICK_WINDOW) {
                            yte$sendFloorCancellation(floorIndex);
                            yte$resetFloorClickTracking();
                            return true;
                        }
                        yte$lastFloorClickIndex = selectionIndex;
                        yte$lastFloorClickTime = currentTime;
                        // The first click of the cancellation gesture only arms
                        // the double-click; do not send another normal car call.
                        return true;
                    } else if (selected) {
                        yte$heldFloorIndex = selectionIndex;
                        yte$floorHoldStartTime = System.currentTimeMillis();
                        yte$floorHoldArmed = true;
                        yte$floorHoldPointerInside = true;
                        // A short press on an already selected floor is a no-op;
                        // holding it long enough sends the cancellation instead.
                        return true;
                    }
                }
            }
        }
        return super.mouseClicked2(mouseX, mouseY, button);
    }

    @Inject(method = "mouseMoved2", at = @At("TAIL"))
    private void yte$trackFloorHoldPointer(double mouseX, double mouseY, CallbackInfo ci) {
        if (yte$floorHoldArmed) {
            yte$floorHoldPointerInside = yte$isFloorListPosition(mouseX, mouseY)
                    && selectionList.getHoverItemIndex() == yte$heldFloorIndex;
            if (!yte$floorHoldPointerInside) {
                yte$resetFloorHold();
            }
        }
    }

    @Override
    public boolean mouseReleased2(double mouseX, double mouseY, int button) {
        if (button == 0 && yte$pressedDoorCommand != null) {
            // 消防员模式：未完成即松开 → 从当前位置反向（关门中松开重开门；开门中松开继续关门）
            if (LiftModeState.getFireMode(liftId) == LiftModeState.FireMode.FIREMAN_MODE) {
                yte$reverseDoorIfIncomplete(yte$pressedDoorCommand);
            }
            yte$finishDoorButtonPress(System.currentTimeMillis());
            return true;
        }
        final boolean handled = super.mouseReleased2(mouseX, mouseY, button);
        if (button == 0) {
            // 类型2（HOLD_FLOOR_BUTTON）：完全关门登记后松开不干预；未登记即松开则取消意图并重开门
            if (yte$firemanFloorPressed) {
                yte$firemanFloorPressed = false;
                if (LiftModeState.getFireMode(liftId) == LiftModeState.FireMode.FIREMAN_MODE
                        && YteLiftConfigStore.getFiremanOperation(liftId) == FiremanOperationType.HOLD_FLOOR_BUTTON
                        && yte$firemanHeldFloorIndex >= 0 && !yte$firemanRegistrationSent) {
                    yte$firemanHeldFloorIndex = -1;
                    final Lift lift = MinecraftClientData.getLift(liftId);
                    if (lift != null && ((MixinLiftSchema) lift).getSpeed() == 0) {
                        yte$sendDoorCommand(LiftDoorState.Command.OPEN);
                    }
                }
            }
            yte$resetFloorHold();
        }
        return handled;
    }

    @Unique
    private boolean yte$firemanInstructionsEmpty() {
        final Lift lift = MinecraftClientData.getLift(liftId);
        return lift == null || ((MixinLiftSchema) lift).getInstructions().isEmpty();
    }

    /** 消防员模式：门未完成（0 < doorValue < 1）时松开按钮 → 从当前位置反向。 */
    @Unique
    private void yte$reverseDoorIfIncomplete(LiftDoorState.Command command) {
        final Lift lift = MinecraftClientData.getLift(liftId);
        if (lift == null) {
            return;
        }
        final float doorValue = lift.getDoorValue();
        if (doorValue <= 0 || doorValue >= 1) {
            return;
        }
        yte$sendDoorCommand(command == LiftDoorState.Command.CLOSE
                ? LiftDoorState.Command.OPEN
                : LiftDoorState.Command.CLOSE);
    }

    @Unique
    private LiftDoorState.Command yte$getDoorCommandAt(double mouseX, double mouseY) {
        if (yte$isClickableDoorButton(yte$holdOpenButton, mouseX, mouseY)) {
            return LiftDoorState.Command.HOLD_OPEN;
        }
        if (yte$isClickableDoorButton(yte$openDoorButton, mouseX, mouseY)) {
            return LiftDoorState.Command.OPEN;
        }
        if (yte$isClickableDoorButton(yte$closeDoorButton, mouseX, mouseY)) {
            return LiftDoorState.Command.CLOSE;
        }
        return null;
    }

    @Unique
    private static boolean yte$isClickableDoorButton(LiftSelectionButtonWidget button, double mouseX, double mouseY) {
        return button.getVisibleMapped() && button.isMouseOver2(mouseX, mouseY);
    }

    @Unique
    private boolean yte$isFloorListPosition(double mouseX, double mouseY) {
        return mouseX >= selectionList.x && mouseX < selectionList.x + selectionList.width
                && mouseY >= selectionList.y + 24 && mouseY < selectionList.y + selectionList.height;
    }

    @Unique
    private void yte$startDoorButtonPress(LiftDoorState.Command command) {
        final long currentTime = System.currentTimeMillis();
        if (yte$pressedDoorCommand != null) {
            yte$finishDoorButtonPress(currentTime);
        }
        yte$pressedDoorCommand = command;
        yte$nextDoorRepeatTime = currentTime + YTE_DOOR_REPEAT_INTERVAL;
        yte$sendDoorCommand(command);
        yte$updateDoorButtonLights(currentTime);
    }

    @Unique
    private void yte$finishDoorButtonPress(long currentTime) {
        if (yte$pressedDoorCommand == null) {
            return;
        }
        if (YteLiftConfigStore.getDoorButtonLightMode(liftId) == LiftDoorButtonLightMode.TIMED) {
            yte$setDoorLightUntil(yte$pressedDoorCommand, currentTime + YTE_TIMED_LIGHT_DURATION);
        }
        yte$pressedDoorCommand = null;
        yte$updateDoorButtonLights(currentTime);
    }

    @Unique
    private void yte$updateDoorButtonLights(long currentTime) {
        final LiftDoorButtonLightMode lightMode = YteLiftConfigStore.getDoorButtonLightMode(liftId);
        // HOLD 键：按下发送 HOLD 指令即点亮，直到发送其他门指令（OPEN/CLOSE）才熄灭
        yte$holdOpenButton.setLit(yte$holdCommandActive || lightMode.isLit(
                yte$pressedDoorCommand == LiftDoorState.Command.HOLD_OPEN,
                currentTime < yte$holdOpenLightUntil, false));
        yte$openDoorButton.setLit(lightMode.isLit(
                yte$pressedDoorCommand == LiftDoorState.Command.OPEN,
                currentTime < yte$openDoorLightUntil, false));
        yte$closeDoorButton.setLit(lightMode.isLit(
                yte$pressedDoorCommand == LiftDoorState.Command.CLOSE,
                currentTime < yte$closeDoorLightUntil, false));
    }

    @Unique
    private void yte$setDoorLightUntil(LiftDoorState.Command command, long lightUntil) {
        switch (command) {
            case HOLD_OPEN:
                yte$holdOpenLightUntil = lightUntil;
                break;
            case OPEN:
                yte$openDoorLightUntil = lightUntil;
                break;
            case CLOSE:
                yte$closeDoorLightUntil = lightUntil;
                break;
            default:
                break;
        }
    }

    @Unique
    private LiftSelectionButtonWidget yte$getDoorButton(LiftDoorState.Command command) {
        switch (command) {
            case HOLD_OPEN:
                return yte$holdOpenButton;
            case OPEN:
                return yte$openDoorButton;
            case CLOSE:
                return yte$closeDoorButton;
            default:
                return null;
        }
    }

    @Unique
    private void yte$cancelHeldFloorIfAllowed(Lift lift) {
        if (lift != null && yte$floorHoldPointerInside
                && yte$heldFloorIndex >= 0 && yte$heldFloorIndex < floorLevels.size()) {
            final int floorIndex = yte$getFloorIndex(lift, yte$heldFloorIndex);
            yte$sendFloorCancellation(floorIndex);
        }
        yte$resetFloorHold();
    }

    @Unique
    private int yte$getFloorIndex(Lift lift, int selectionIndex) {
        return lift.getFloorIndex(org.mtr.mod.Init.blockPosToPosition(
                floorLevels.get(floorLevels.size() - selectionIndex - 1)));
    }

    @Unique
    private void yte$sendFloorCancellation(int floorIndex) {
        InitClient.REGISTRY_CLIENT.sendPacketToServer(new PacketLiftFloorCancel(liftId, floorIndex));
    }

    @Unique
    private void yte$resetFloorClickTracking() {
        yte$lastFloorClickIndex = -1;
        yte$lastFloorClickTime = 0;
    }

    @Unique
    private void yte$resetFloorHold() {
        yte$heldFloorIndex = -1;
        yte$floorHoldStartTime = 0;
        yte$floorHoldArmed = false;
        yte$floorHoldPointerInside = false;
    }

    @Unique
    private void yte$sendDoorCommand(LiftDoorState.Command command) {
        if (command == LiftDoorState.Command.HOLD_OPEN) {
            yte$holdCommandActive = true;
        } else {
            yte$holdCommandActive = false;
            if (command == LiftDoorState.Command.CLOSE) {
                yte$holdOpenLightUntil = 0;
                yte$applyClientCloseCommand();
            }
        }
        if (command == LiftDoorState.Command.OPEN || command == LiftDoorState.Command.HOLD_OPEN) {
            yte$applyClientOpenCommand(command);
        }
        InitClient.REGISTRY_CLIENT.sendPacketToServer(new PacketLiftDoorControl(liftId, command));
        // Minecraft keeps the last clicked active button keyboard-focused,
        // and assigns that focus after the press callback has returned. Clear
        // it on the next screen tick instead of too early in this callback.
        yte$clearDoorButtonFocus = true;
    }

    @Unique
    private void yte$applyClientCloseCommand() {
        // MTR 的 Client.update 是异步队列，CLOSE 又没有 S→C 包，客户端收不到关门指令；
        // 与服务端同门控（仅全开生效）地本地立即进入关门段
        final Lift lift = MinecraftClientData.getLift(liftId);
        if (lift == null || !yte$isStoppedAtFloor(lift)) {
            return;
        }
        // ponytail: 用 coolDown 相位区间判定「门已全开」，取代平滑后的浮点门值，
        // 避免门值因临界阻尼平滑暂时 <0.999 而漏判、退回网络往返造成偶发延迟
        final MixinLiftSchema schema = (MixinLiftSchema) lift;
        final YteLiftConfigStore.DoorParams p = YteLiftConfigStore.getDoorParams(liftId);
        final long coolDown = schema.getStoppingCoolDown();
        if (coolDown > p.closeStartCoolDown() && coolDown <= p.fullOpenCoolDown()) {
            schema.setStoppingCoolDown(p.closeStartCoolDown());
        }
    }

    @Unique
    private void yte$applyClientOpenCommand(LiftDoorState.Command command) {
        final Lift lift = MinecraftClientData.getLift(liftId);
        if (lift == null || !yte$isStoppedAtFloor(lift)) {
            return;
        }
        final MixinLiftSchema schema = (MixinLiftSchema) lift;
        final YteLiftConfigStore.DoorParams p = YteLiftConfigStore.getDoorParams(liftId);
        final long coolDown = schema.getStoppingCoolDown();
        final LiftDoorState.DoorState phase = LiftDoorState.getDoorState(coolDown, p);

        // HOLD 在门已关时无可重置，忽略
        if (command == LiftDoorState.Command.HOLD_OPEN && phase == LiftDoorState.DoorState.CLOSED) {
            return;
        }

        if (phase == LiftDoorState.DoorState.FULLY_OPEN) {
            schema.setStoppingCoolDown(p.fullOpenCoolDown());
        } else if (coolDown <= p.runDelay) {
            // 刚关（CLOSED 下半段）：本地立即开门；其余（开门中 / 关门中）不做本地动作，
            // 等待服务端同步，避免前后跳动
            schema.setStoppingCoolDown(p.total());
            if (schema.getInstructions().isEmpty()) {
                LiftDisplayDirectionState.get(liftId).resetForIdleDoorCycle();
            }
        }
    }

    @Unique
    private static boolean yte$isStoppedAtFloor(Lift lift) {
        final MixinLiftSchema schema = (MixinLiftSchema) lift;
        if (schema.getSpeed() != 0) {
            return false;
        }
        for (int i = 0; i < schema.getFloors().size(); i++) {
            if (Math.abs(schema.getRailProgress() - ((MixinLiftFields) lift).invokeGetProgress(i)) < 0.000001) {
                return true;
            }
        }
        return false;
    }

}
