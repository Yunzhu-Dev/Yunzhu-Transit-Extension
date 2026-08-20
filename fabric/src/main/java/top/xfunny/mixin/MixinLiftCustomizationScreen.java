package top.xfunny.mixin;

import org.mtr.core.data.Lift;
import org.mtr.mapping.holder.ClickableWidget;
import org.mtr.mapping.holder.Text;
import org.mtr.mapping.mapper.ButtonWidgetExtension;
import org.mtr.mapping.mapper.GraphicsHolder;
import org.mtr.mapping.mapper.GuiDrawing;
import org.mtr.mapping.mapper.TextHelper;
import org.mtr.mapping.mapper.TextFieldWidgetExtension;
import org.mtr.mapping.tool.TextCase;
import org.mtr.mod.data.IGui;
import org.mtr.mod.screen.LiftCustomizationScreen;
import org.mtr.mod.screen.MTRScreenBase;
import org.mtr.mod.screen.WidgetShorterSlider;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.xfunny.core.data.YteLiftConfig;
import top.xfunny.core.operation.YteUpdateDataRequest;
import top.xfunny.mod.client.InitClient;
import top.xfunny.mod.client.YteMinecraftClientData;
import top.xfunny.mod.config.YteLiftConfigStore;
import top.xfunny.mod.packet.YtePacketUpdateData;
import top.xfunny.mod.lift.LiftDoorButtonLightMode;
import top.xfunny.mod.lift.LiftFloorCancelMode;
import top.xfunny.mod.lift.LiftMotionProfile;

import java.util.Locale;

@Mixin(value = LiftCustomizationScreen.class, remap = false)
public abstract class MixinLiftCustomizationScreen extends MTRScreenBase {

    @Shadow
    @Final
    private Lift lift;

    @Shadow
    @Final
    private int width2;

    @Shadow
    @Final
    private ButtonWidgetExtension buttonLiftStyle;

    @Unique
    private WidgetShorterSlider yte$sliderSpeed;

    @Unique
    private WidgetShorterSlider yte$sliderAcceleration;

    @Unique private WidgetShorterSlider yte$sliderDownSpeed;
    @Unique private WidgetShorterSlider yte$sliderDownAcceleration;

    @Unique private WidgetShorterSlider yte$sliderAdoDistance;
    @Unique private WidgetShorterSlider yte$sliderLevellingDistance;
    @Unique private WidgetShorterSlider yte$sliderLevellingSpeed;
    @Unique private ButtonWidgetExtension yte$professionalModeButton;
    @Unique private ButtonWidgetExtension yte$directionLinkButton;
    @Unique private ButtonWidgetExtension yte$motionProfileButton;
    @Unique private ButtonWidgetExtension yte$doorHoldButton;
    @Unique private ButtonWidgetExtension yte$doorButtonLightModeButton;
    @Unique private ButtonWidgetExtension yte$floorCancelModeButton;
    @Unique private TextFieldWidgetExtension yte$speedField;
    @Unique private TextFieldWidgetExtension yte$accelerationField;
    @Unique private TextFieldWidgetExtension yte$downSpeedField;
    @Unique private TextFieldWidgetExtension yte$downAccelerationField;
    @Unique private TextFieldWidgetExtension yte$adoDistanceField;
    @Unique private TextFieldWidgetExtension yte$levellingDistanceField;
    @Unique private TextFieldWidgetExtension yte$levellingSpeedField;

    @Unique
    private static final int SPEED_SLIDER_MAX = 40;

    @Unique
    private static final int ACCEL_SLIDER_MAX = 20;

    @Unique private static final int ADO_DISTANCE_SLIDER_MAX = 30;
    @Unique private static final int LEVELLING_DISTANCE_SLIDER_MAX = 20;
    @Unique private static final int LEVELLING_SPEED_SLIDER_MAX = 20;
    @Unique private static boolean yte$professionalMode;

    @Unique
    private double yte$lastSentSpeed = -1;

    @Unique
    private double yte$lastSentAccel = -1;
    @Unique private double yte$lastSentDownSpeed = -1;
    @Unique private double yte$lastSentDownAccel = -1;
    @Unique private boolean yte$directionParametersLinked = true;
    @Unique private boolean yte$lastSentDirectionParametersLinked = true;
    @Unique private LiftMotionProfile yte$motionProfile = LiftMotionProfile.STANDARD;
    @Unique private LiftMotionProfile yte$lastSentMotionProfile = LiftMotionProfile.STANDARD;
    @Unique private boolean yte$doorHoldEnabled;
    @Unique private boolean yte$lastSentDoorHoldEnabled;
    @Unique private LiftDoorButtonLightMode yte$doorButtonLightMode = LiftDoorButtonLightMode.MOMENTARY;
    @Unique private LiftDoorButtonLightMode yte$lastSentDoorButtonLightMode = LiftDoorButtonLightMode.MOMENTARY;
    @Unique private LiftFloorCancelMode yte$floorCancelMode = LiftFloorCancelMode.DOUBLE_CLICK;
    @Unique private LiftFloorCancelMode yte$lastSentFloorCancelMode = LiftFloorCancelMode.DOUBLE_CLICK;
    @Unique private double yte$lastSentAdoDistance = -1;
    @Unique private double yte$lastSentLevellingDistance = -1;
    @Unique private double yte$lastSentLevellingSpeed = -1;
    @Unique private final double[] yte$easyModeValues = new double[7];
    @Unique private final int[] yte$easyModeSliderAnchors = new int[7];
    @Unique private final boolean[] yte$easyModeSliderTouched = new boolean[7];
    @Unique private double yte$scrollOffset;
    @Unique private boolean yte$contentTransformPushed;
    @Unique private boolean yte$scrollbarDragging;
    @Unique private double yte$scrollbarDragOffset;
    @Unique private int yte$liftStyleButtonContentY;
    @Unique private int yte$directionLinkButtonContentY;
    @Unique private int yte$motionProfileButtonContentY;
    @Unique private int yte$doorHoldButtonContentY;
    @Unique private int yte$doorButtonLightModeButtonContentY;
    @Unique private int yte$floorCancelModeButtonContentY;
    @Unique private boolean yte$scrollingTextButtonsSuppressed;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onConstructed(Lift liftParam, CallbackInfo ci) {
        final long liftId = liftParam.getId();
        final YteLiftConfig config = YteMinecraftClientData.getInstance().getConfig(liftId);

        final double currentSpeed = config != null ? config.getUpSpeed() : YteLiftConfig.DEFAULT_SPEED;
        final double currentDownSpeed = config != null ? config.getDownSpeed() : currentSpeed;
        final double currentAccel = config != null ? config.getUpAcceleration() : YteLiftConfig.DEFAULT_ACCELERATION;
        final double currentDownAccel = config != null ? config.getDownAcceleration() : currentAccel;
        yte$directionParametersLinked = config == null || config.areDirectionParametersLinked();
        yte$motionProfile = config == null ? LiftMotionProfile.STANDARD : config.getMotionProfile();
        yte$doorHoldEnabled = config != null && config.isDoorHoldEnabled();
        yte$doorButtonLightMode = config == null ? LiftDoorButtonLightMode.MOMENTARY : config.getDoorButtonLightMode();
        yte$floorCancelMode = config == null ? LiftFloorCancelMode.DOUBLE_CLICK : config.getFloorCancelMode();
        final double currentAdoDistance = config != null ? config.getAdoDistance() : YteLiftConfig.DEFAULT_ADO_DISTANCE;
        final double currentLevellingDistance = config != null ? config.getLevellingDistance() : YteLiftConfig.DEFAULT_LEVELLING_DISTANCE;
        final double currentLevellingSpeed = config != null ? config.getLevellingSpeed() : YteLiftConfig.DEFAULT_LEVELLING_SPEED;

        // 不显示内置值文字，由 render 手绘
        yte$sliderSpeed = new WidgetShorterSlider(0, 60, SPEED_SLIDER_MAX,
                value -> "", null);
        yte$sliderSpeed.setValue(speedToValue(currentSpeed));

        yte$sliderAcceleration = new WidgetShorterSlider(0, 60, ACCEL_SLIDER_MAX,
                value -> "", null);
        yte$sliderAcceleration.setValue(accelToValue(currentAccel));

        yte$sliderDownSpeed = new WidgetShorterSlider(0, 60, SPEED_SLIDER_MAX, value -> "", null);
        yte$sliderDownSpeed.setValue(speedToValue(currentDownSpeed));
        yte$sliderDownAcceleration = new WidgetShorterSlider(0, 60, ACCEL_SLIDER_MAX, value -> "", null);
        yte$sliderDownAcceleration.setValue(accelToValue(currentDownAccel));

        yte$sliderAdoDistance = new WidgetShorterSlider(0, 60, ADO_DISTANCE_SLIDER_MAX,
                value -> "", null);
        yte$sliderAdoDistance.setValue(adoDistanceToValue(currentAdoDistance));

        yte$sliderLevellingDistance = new WidgetShorterSlider(0, 60, LEVELLING_DISTANCE_SLIDER_MAX,
                value -> "", null);
        yte$sliderLevellingDistance.setValue(levellingDistanceToValue(currentLevellingDistance));

        yte$sliderLevellingSpeed = new WidgetShorterSlider(0, 60, LEVELLING_SPEED_SLIDER_MAX,
                value -> "", null);
        yte$sliderLevellingSpeed.setValue(levellingSpeedToValue(currentLevellingSpeed));

        yte$setEasyModeValues(currentSpeed, currentDownSpeed, currentAccel, currentDownAccel,
                currentAdoDistance, currentLevellingDistance, currentLevellingSpeed);

        yte$professionalModeButton = new ButtonWidgetExtension(0, 0, 0, IGui.SQUARE_SIZE,
                TextHelper.literal(""), button -> yte$toggleProfessionalMode());
        yte$directionLinkButton = new ButtonWidgetExtension(0, 0, 0, IGui.SQUARE_SIZE,
                TextHelper.literal(""), button -> yte$toggleDirectionParametersLinked());
        yte$motionProfileButton = new ButtonWidgetExtension(0, 0, 0, IGui.SQUARE_SIZE,
                TextHelper.literal(""), button -> yte$toggleMotionProfile());
        yte$doorHoldButton = new ButtonWidgetExtension(0, 0, 0, IGui.SQUARE_SIZE,
                TextHelper.literal(""), button -> yte$toggleDoorHold());
        yte$doorButtonLightModeButton = new ButtonWidgetExtension(0, 0, 0, IGui.SQUARE_SIZE,
                TextHelper.literal(""), button -> yte$toggleDoorButtonLightMode());
        yte$floorCancelModeButton = new ButtonWidgetExtension(0, 0, 0, IGui.SQUARE_SIZE,
                TextHelper.literal(""), button -> yte$toggleFloorCancelMode());

        yte$speedField = yte$createNumberField(currentSpeed);
        yte$accelerationField = yte$createNumberField(currentAccel);
        yte$downSpeedField = yte$createNumberField(currentDownSpeed);
        yte$downAccelerationField = yte$createNumberField(currentDownAccel);
        yte$adoDistanceField = yte$createNumberField(currentAdoDistance);
        yte$levellingDistanceField = yte$createNumberField(currentLevellingDistance);
        yte$levellingSpeedField = yte$createNumberField(currentLevellingSpeed);

        yte$lastSentSpeed = currentSpeed;
        yte$lastSentAccel = currentAccel;
        yte$lastSentDownSpeed = currentDownSpeed;
        yte$lastSentDownAccel = currentDownAccel;
        yte$lastSentDirectionParametersLinked = yte$directionParametersLinked;
        yte$lastSentMotionProfile = yte$motionProfile;
        yte$lastSentAdoDistance = currentAdoDistance;
        yte$lastSentLevellingDistance = currentLevellingDistance;
        yte$lastSentLevellingSpeed = currentLevellingSpeed;
        yte$lastSentDoorHoldEnabled = yte$doorHoldEnabled;
        yte$lastSentDoorButtonLightMode = yte$doorButtonLightMode;
        yte$lastSentFloorCancelMode = yte$floorCancelMode;
    }

    @Inject(method = "init2", at = @At("TAIL"))
    private void onInit2(CallbackInfo ci) {
        // 与原版全宽控件对齐：x=0, width=width2
        // Keep the custom settings and motion controls in one continuous list.
        yte$professionalModeButton.setX2(0);
        yte$professionalModeButton.setY2(IGui.SQUARE_SIZE * 11);
        yte$professionalModeButton.setWidth2(width2);
        yte$directionLinkButton.setX2(0);
        yte$directionLinkButton.setY2(IGui.SQUARE_SIZE * 12);
        yte$directionLinkButton.setWidth2(width2);
        yte$motionProfileButton.setX2(0);
        yte$motionProfileButton.setY2(IGui.SQUARE_SIZE * 13);
        yte$motionProfileButton.setWidth2(width2);
        yte$doorHoldButton.setX2(0);
        yte$doorHoldButton.setY2(IGui.SQUARE_SIZE * 14);
        yte$doorHoldButton.setWidth2(width2);
        yte$doorButtonLightModeButton.setX2(0);
        yte$doorButtonLightModeButton.setY2(IGui.SQUARE_SIZE * 15);
        yte$doorButtonLightModeButton.setWidth2(width2);
        yte$floorCancelModeButton.setX2(0);
        yte$floorCancelModeButton.setY2(IGui.SQUARE_SIZE * 16);
        yte$floorCancelModeButton.setWidth2(width2);

        addChild(new ClickableWidget(yte$professionalModeButton));
        addChild(new ClickableWidget(yte$directionLinkButton));
        addChild(new ClickableWidget(yte$motionProfileButton));
        addChild(new ClickableWidget(yte$doorHoldButton));
        addChild(new ClickableWidget(yte$doorButtonLightModeButton));
        addChild(new ClickableWidget(yte$floorCancelModeButton));
        addChild(new ClickableWidget(yte$sliderSpeed));
        addChild(new ClickableWidget(yte$sliderAcceleration));
        addChild(new ClickableWidget(yte$sliderDownSpeed));
        addChild(new ClickableWidget(yte$sliderDownAcceleration));
        addChild(new ClickableWidget(yte$sliderAdoDistance));
        addChild(new ClickableWidget(yte$sliderLevellingDistance));
        addChild(new ClickableWidget(yte$sliderLevellingSpeed));

        addChild(new ClickableWidget(yte$speedField));
        addChild(new ClickableWidget(yte$accelerationField));
        addChild(new ClickableWidget(yte$downSpeedField));
        addChild(new ClickableWidget(yte$downAccelerationField));
        addChild(new ClickableWidget(yte$adoDistanceField));
        addChild(new ClickableWidget(yte$levellingDistanceField));
        addChild(new ClickableWidget(yte$levellingSpeedField));

        // Text fields are recreated by the screen initialization lifecycle.
        // Restore their visible text after they have been attached to the screen.
        yte$layoutDirectionWidgets();
        yte$syncFieldsFromValues(yte$lastSentSpeed, yte$lastSentDownSpeed, yte$lastSentAccel, yte$lastSentDownAccel, yte$lastSentAdoDistance,
                yte$lastSentLevellingDistance, yte$lastSentLevellingSpeed);
        yte$updateModeWidgets();
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lorg/mtr/mod/screen/MTRScreenBase;render(Lorg/mtr/mapping/mapper/GraphicsHolder;IIF)V", shift = At.Shift.BEFORE))
    private void yte$beginScrollableContent(GraphicsHolder graphicsHolder, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        yte$drawExtendedBackground(graphicsHolder);
        // Long button labels use a screen-space horizontal scissor rectangle.
        // A matrix-only vertical translation moves the button but not that
        // rectangle, so suppress these text buttons and render them later at a
        // physically adjusted Y coordinate.
        yte$liftStyleButtonContentY = buttonLiftStyle.getY2();
        yte$directionLinkButtonContentY = yte$directionLinkButton.getY2();
        yte$motionProfileButtonContentY = yte$motionProfileButton.getY2();
        yte$doorHoldButtonContentY = yte$doorHoldButton.getY2();
        yte$doorButtonLightModeButtonContentY = yte$doorButtonLightModeButton.getY2();
        yte$floorCancelModeButtonContentY = yte$floorCancelModeButton.getY2();
        buttonLiftStyle.setVisibleMapped(false);
        yte$directionLinkButton.setVisibleMapped(false);
        yte$motionProfileButton.setVisibleMapped(false);
        yte$doorHoldButton.setVisibleMapped(false);
        yte$doorButtonLightModeButton.setVisibleMapped(false);
        yte$floorCancelModeButton.setVisibleMapped(false);
        yte$scrollingTextButtonsSuppressed = true;
        graphicsHolder.push();
        graphicsHolder.translate(0, -yte$scrollOffset, 0);
        yte$contentTransformPushed = true;
    }

    @Unique
    private void yte$drawExtendedBackground(GraphicsHolder graphicsHolder) {
        final int extendedRight = yte$getPanelRight();
        if (extendedRight <= width2) {
            return;
        }
        final GuiDrawing guiDrawing = new GuiDrawing(graphicsHolder);
        guiDrawing.beginDrawingRectangle();
        guiDrawing.drawRectangle(width2, 0, extendedRight, getHeightMapped(), 0xFF121212);
        guiDrawing.finishDrawingRectangle();
    }

    @Unique
    private int yte$getPanelRight() {
        return Math.min(getWidthMapped(), width2 + 15);
    }

    @ModifyVariable(method = "render", at = @At("HEAD"), argsOnly = true, ordinal = 1)
    private int yte$adjustMouseYForScroll(int mouseY) {
        return mouseY + (int) yte$scrollOffset;
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(GraphicsHolder graphicsHolder, int mouseX, int mouseY, float delta,
            CallbackInfo ci) {
        final double upSpeed = yte$professionalMode
                ? yte$parseNumber(yte$speedField, yte$lastSentSpeed, YteLiftConfig.MIN_SPEED, YteLiftConfig.MAX_SPEED)
                : yte$getEasyModeValue(0, yte$sliderSpeed, valueToSpeed(yte$sliderSpeed.getIntValue()));
        final double downSpeed = yte$directionParametersLinked ? upSpeed : yte$professionalMode
                ? yte$parseNumber(yte$downSpeedField, yte$lastSentDownSpeed, YteLiftConfig.MIN_SPEED, YteLiftConfig.MAX_SPEED)
                : yte$getEasyModeValue(1, yte$sliderDownSpeed, valueToSpeed(yte$sliderDownSpeed.getIntValue()));
        final double upAccel = yte$professionalMode
                ? yte$parseNumber(yte$accelerationField, yte$lastSentAccel, YteLiftConfig.MIN_ACCELERATION, YteLiftConfig.MAX_ACCELERATION)
                : yte$getEasyModeValue(2, yte$sliderAcceleration, valueToAccel(yte$sliderAcceleration.getIntValue()));
        final double downAccel = yte$directionParametersLinked ? upAccel : yte$professionalMode
                ? yte$parseNumber(yte$downAccelerationField, yte$lastSentDownAccel, YteLiftConfig.MIN_ACCELERATION, YteLiftConfig.MAX_ACCELERATION)
                : yte$getEasyModeValue(3, yte$sliderDownAcceleration, valueToAccel(yte$sliderDownAcceleration.getIntValue()));
        final double adoDistance = yte$professionalMode
                ? yte$parseNumber(yte$adoDistanceField, yte$lastSentAdoDistance, YteLiftConfig.MAX_ADO_DISTANCE)
                : yte$getEasyModeValue(4, yte$sliderAdoDistance, valueToAdoDistance(yte$sliderAdoDistance.getIntValue()));
        final double levellingDistance = yte$professionalMode
                ? yte$parseNumber(yte$levellingDistanceField, yte$lastSentLevellingDistance, YteLiftConfig.MAX_LEVELLING_DISTANCE)
                : yte$getEasyModeValue(5, yte$sliderLevellingDistance, valueToLevellingDistance(yte$sliderLevellingDistance.getIntValue()));
        final double levellingSpeed = yte$professionalMode
                ? yte$parseNumber(yte$levellingSpeedField, yte$lastSentLevellingSpeed, YteLiftConfig.MAX_LEVELLING_SPEED)
                : yte$getEasyModeValue(6, yte$sliderLevellingSpeed, valueToLevellingSpeed(yte$sliderLevellingSpeed.getIntValue()));

        if (yte$directionParametersLinked) {
            yte$drawModeLabel(graphicsHolder, "gui.yte.lift_speed", "gui.yte.lift_speed_value", upSpeed, 17);
            yte$drawModeLabel(graphicsHolder, "gui.yte.lift_acceleration", "gui.yte.lift_acceleration_value", upAccel, 19);
        } else {
            yte$drawModeLabel(graphicsHolder, "gui.yte.lift_up_speed", "gui.yte.lift_up_speed_value", upSpeed, 17);
            yte$drawModeLabel(graphicsHolder, "gui.yte.lift_down_speed", "gui.yte.lift_down_speed_value", downSpeed, 19);
            yte$drawModeLabel(graphicsHolder, "gui.yte.lift_up_acceleration", "gui.yte.lift_up_acceleration_value", upAccel, 21);
            yte$drawModeLabel(graphicsHolder, "gui.yte.lift_down_acceleration", "gui.yte.lift_down_acceleration_value", downAccel, 23);
        }
        final int extraStartRow = yte$directionParametersLinked ? 21 : 25;
        yte$drawModeLabel(graphicsHolder, "gui.yte.lift_ado_distance", "gui.yte.lift_ado_distance_value", adoDistance, extraStartRow);
        yte$drawModeLabel(graphicsHolder, "gui.yte.lift_levelling_distance", "gui.yte.lift_levelling_distance_value", levellingDistance, extraStartRow + 2);
        yte$drawModeLabel(graphicsHolder, "gui.yte.lift_levelling_speed", "gui.yte.lift_levelling_speed_value", levellingSpeed, extraStartRow + 4);

        if (upSpeed != yte$lastSentSpeed || downSpeed != yte$lastSentDownSpeed
                || upAccel != yte$lastSentAccel || downAccel != yte$lastSentDownAccel
                || yte$directionParametersLinked != yte$lastSentDirectionParametersLinked
                || yte$motionProfile != yte$lastSentMotionProfile
                || adoDistance != yte$lastSentAdoDistance || levellingDistance != yte$lastSentLevellingDistance
                || levellingSpeed != yte$lastSentLevellingSpeed
                || yte$doorHoldEnabled != yte$lastSentDoorHoldEnabled
                || yte$doorButtonLightMode != yte$lastSentDoorButtonLightMode
                || yte$floorCancelMode != yte$lastSentFloorCancelMode) {
            yte$lastSentSpeed = upSpeed;
            yte$lastSentDownSpeed = downSpeed;
            yte$lastSentAccel = upAccel;
            yte$lastSentDownAccel = downAccel;
            yte$lastSentDirectionParametersLinked = yte$directionParametersLinked;
            yte$lastSentMotionProfile = yte$motionProfile;
            yte$lastSentAdoDistance = adoDistance;
            yte$lastSentLevellingDistance = levellingDistance;
            yte$lastSentLevellingSpeed = levellingSpeed;
            yte$lastSentDoorHoldEnabled = yte$doorHoldEnabled;
            yte$lastSentDoorButtonLightMode = yte$doorButtonLightMode;
            yte$lastSentFloorCancelMode = yte$floorCancelMode;

            final long liftId = lift.getId();
            final YteLiftConfig config = new YteLiftConfig(liftId, upSpeed, downSpeed, upAccel, downAccel,
                    yte$directionParametersLinked, adoDistance, levellingDistance, levellingSpeed, yte$motionProfile,
                    yte$doorHoldEnabled, yte$doorButtonLightMode, yte$floorCancelMode, false);
            YteLiftConfigStore.put(liftId, upSpeed, downSpeed, upAccel, downAccel,
                    adoDistance, levellingDistance, levellingSpeed, yte$motionProfile, yte$doorHoldEnabled,
                    yte$doorButtonLightMode, yte$floorCancelMode, false);

            final YteUpdateDataRequest request = new YteUpdateDataRequest(
                    config, YteMinecraftClientData.getInstance());
            InitClient.REGISTRY_CLIENT.sendPacketToServer(
                    new YtePacketUpdateData(request));
        }

        if (yte$contentTransformPushed) {
            graphicsHolder.pop();
            yte$contentTransformPushed = false;
        }
        if (yte$scrollingTextButtonsSuppressed) {
            final int screenMouseY = mouseY - (int) yte$scrollOffset;
            yte$renderScrollSafeButton(graphicsHolder, buttonLiftStyle, yte$liftStyleButtonContentY,
                    mouseX, screenMouseY, delta);
            yte$renderScrollSafeButton(graphicsHolder, yte$directionLinkButton, yte$directionLinkButtonContentY,
                    mouseX, screenMouseY, delta);
            yte$renderScrollSafeButton(graphicsHolder, yte$motionProfileButton, yte$motionProfileButtonContentY,
                    mouseX, screenMouseY, delta);
            yte$renderScrollSafeButton(graphicsHolder, yte$doorHoldButton, yte$doorHoldButtonContentY,
                    mouseX, screenMouseY, delta);
            yte$renderScrollSafeButton(graphicsHolder, yte$doorButtonLightModeButton,
                    yte$doorButtonLightModeButtonContentY, mouseX, screenMouseY, delta);
            yte$renderScrollSafeButton(graphicsHolder, yte$floorCancelModeButton,
                    yte$floorCancelModeButtonContentY, mouseX, screenMouseY, delta);
            yte$scrollingTextButtonsSuppressed = false;
        }
        yte$drawScrollbar(graphicsHolder);
    }

    @Unique
    private void yte$renderScrollSafeButton(GraphicsHolder graphicsHolder, ButtonWidgetExtension button,
            int contentY, int mouseX, int mouseY, float delta) {
        final int screenY = contentY - (int) Math.round(yte$scrollOffset);
        button.setY2(screenY);
        button.setVisibleMapped(true);
        if (screenY + IGui.SQUARE_SIZE > 0 && screenY < getHeightMapped()) {
            button.render(graphicsHolder, mouseX, mouseY, delta);
        }
        button.setY2(contentY);
    }

    @Override
    public boolean mouseScrolled2(double mouseX, double mouseY, double amount) {
        if (mouseX >= 0 && mouseX <= yte$getPanelRight() && yte$getMaxScroll() > 0) {
            yte$scrollOffset = Math.max(0, Math.min(yte$getMaxScroll(), yte$scrollOffset - amount * IGui.SQUARE_SIZE));
            return true;
        }
        return super.mouseScrolled2(mouseX, mouseY, amount);
    }

    @Override
    public boolean mouseClicked2(double mouseX, double mouseY, int button) {
        final int panelRight = yte$getPanelRight();
        if (button == 0 && yte$getMaxScroll() > 0 && mouseX >= panelRight - 4 && mouseX <= panelRight) {
            final int thumbY = yte$getScrollbarThumbY();
            final int thumbHeight = yte$getScrollbarThumbHeight();
            yte$scrollbarDragging = true;
            yte$scrollbarDragOffset = mouseY >= thumbY && mouseY <= thumbY + thumbHeight
                    ? mouseY - thumbY : thumbHeight / 2.0;
            yte$setScrollFromThumb(mouseY - yte$scrollbarDragOffset);
            return true;
        }
        return super.mouseClicked2(mouseX, mouseY + yte$scrollOffset, button);
    }

    @Override
    public boolean mouseDragged2(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (yte$scrollbarDragging) {
            yte$setScrollFromThumb(mouseY - yte$scrollbarDragOffset);
            return true;
        }
        return super.mouseDragged2(mouseX, mouseY + yte$scrollOffset, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased2(double mouseX, double mouseY, int button) {
        if (yte$scrollbarDragging) {
            yte$scrollbarDragging = false;
            return true;
        }
        return super.mouseReleased2(mouseX, mouseY + yte$scrollOffset, button);
    }

    @Unique
    private static TextFieldWidgetExtension yte$createNumberField(double value) {
        final TextFieldWidgetExtension field = new TextFieldWidgetExtension(0, 0, 0, IGui.SQUARE_SIZE, 12, TextCase.DEFAULT, null, "0");
        field.setText2(Double.toString(value));
        return field;
    }

    @Unique
    private void yte$positionField(TextFieldWidgetExtension field, int row) {
        field.setX2(0);
        field.setY2(IGui.SQUARE_SIZE * row);
        field.setWidth2(width2);
    }

    @Unique
    private void yte$positionSlider(WidgetShorterSlider slider, int row) {
        slider.setX2(0);
        slider.setY2(IGui.SQUARE_SIZE * row);
        slider.setHeight(IGui.SQUARE_SIZE);
        slider.setWidth2(width2);
    }

    @Unique
    private void yte$layoutDirectionWidgets() {
        yte$positionSlider(yte$sliderSpeed, 18);
        yte$positionField(yte$speedField, 18);

        if (yte$directionParametersLinked) {
            yte$positionSlider(yte$sliderAcceleration, 20);
            yte$positionField(yte$accelerationField, 20);
        } else {
            yte$positionSlider(yte$sliderDownSpeed, 20);
            yte$positionField(yte$downSpeedField, 20);
            yte$positionSlider(yte$sliderAcceleration, 22);
            yte$positionField(yte$accelerationField, 22);
            yte$positionSlider(yte$sliderDownAcceleration, 24);
            yte$positionField(yte$downAccelerationField, 24);
        }

        final int extraControlRow = yte$directionParametersLinked ? 22 : 26;
        yte$positionSlider(yte$sliderAdoDistance, extraControlRow);
        yte$positionField(yte$adoDistanceField, extraControlRow);
        yte$positionSlider(yte$sliderLevellingDistance, extraControlRow + 2);
        yte$positionField(yte$levellingDistanceField, extraControlRow + 2);
        yte$positionSlider(yte$sliderLevellingSpeed, extraControlRow + 4);
        yte$positionField(yte$levellingSpeedField, extraControlRow + 4);
        yte$scrollOffset = Math.min(yte$scrollOffset, yte$getMaxScroll());
    }

    @Unique
    private void yte$toggleDirectionParametersLinked() {
        final double upSpeed = yte$professionalMode
                ? yte$parseNumber(yte$speedField, yte$lastSentSpeed, YteLiftConfig.MIN_SPEED, YteLiftConfig.MAX_SPEED)
                : yte$getEasyModeValue(0, yte$sliderSpeed, valueToSpeed(yte$sliderSpeed.getIntValue()));
        final double upAcceleration = yte$professionalMode
                ? yte$parseNumber(yte$accelerationField, yte$lastSentAccel, YteLiftConfig.MIN_ACCELERATION, YteLiftConfig.MAX_ACCELERATION)
                : yte$getEasyModeValue(2, yte$sliderAcceleration, valueToAccel(yte$sliderAcceleration.getIntValue()));

        yte$downSpeedField.setText2(Double.toString(upSpeed));
        yte$downAccelerationField.setText2(Double.toString(upAcceleration));
        yte$easyModeValues[1] = upSpeed;
        yte$easyModeValues[3] = upAcceleration;
        yte$sliderDownSpeed.setValue(speedToValue(upSpeed));
        yte$sliderDownAcceleration.setValue(accelToValue(upAcceleration));
        yte$easyModeSliderAnchors[1] = yte$sliderDownSpeed.getIntValue();
        yte$easyModeSliderAnchors[3] = yte$sliderDownAcceleration.getIntValue();
        yte$easyModeSliderTouched[1] = false;
        yte$easyModeSliderTouched[3] = false;

        yte$directionParametersLinked = !yte$directionParametersLinked;
        yte$layoutDirectionWidgets();
        yte$updateModeWidgets();
    }

    @Unique
    private void yte$toggleMotionProfile() {
        yte$motionProfile = yte$motionProfile.next();
        yte$updateModeWidgets();
    }

    @Unique
    private void yte$toggleDoorHold() {
        yte$doorHoldEnabled = !yte$doorHoldEnabled;
        yte$updateModeWidgets();
    }

    @Unique
    private void yte$toggleDoorButtonLightMode() {
        yte$doorButtonLightMode = yte$doorButtonLightMode.nextSelectable();
        yte$updateModeWidgets();
    }

    @Unique
    private void yte$toggleFloorCancelMode() {
        yte$floorCancelMode = yte$floorCancelMode.next();
        yte$updateModeWidgets();
    }

    @Unique
    private void yte$toggleProfessionalMode() {
        if (yte$professionalMode) {
            yte$setEasyModeValues(
                    yte$parseNumber(yte$speedField, yte$lastSentSpeed, YteLiftConfig.MIN_SPEED, YteLiftConfig.MAX_SPEED),
                    yte$directionParametersLinked ? yte$parseNumber(yte$speedField, yte$lastSentSpeed, YteLiftConfig.MIN_SPEED, YteLiftConfig.MAX_SPEED)
                            : yte$parseNumber(yte$downSpeedField, yte$lastSentDownSpeed, YteLiftConfig.MIN_SPEED, YteLiftConfig.MAX_SPEED),
                    yte$parseNumber(yte$accelerationField, yte$lastSentAccel, YteLiftConfig.MIN_ACCELERATION, YteLiftConfig.MAX_ACCELERATION),
                    yte$directionParametersLinked ? yte$parseNumber(yte$accelerationField, yte$lastSentAccel, YteLiftConfig.MIN_ACCELERATION, YteLiftConfig.MAX_ACCELERATION)
                            : yte$parseNumber(yte$downAccelerationField, yte$lastSentDownAccel, YteLiftConfig.MIN_ACCELERATION, YteLiftConfig.MAX_ACCELERATION),
                    yte$parseNumber(yte$adoDistanceField, yte$lastSentAdoDistance, YteLiftConfig.MAX_ADO_DISTANCE),
                    yte$parseNumber(yte$levellingDistanceField, yte$lastSentLevellingDistance, YteLiftConfig.MAX_LEVELLING_DISTANCE),
                    yte$parseNumber(yte$levellingSpeedField, yte$lastSentLevellingSpeed, YteLiftConfig.MAX_LEVELLING_SPEED));
        } else {
            yte$syncFieldsFromValues(
                    yte$getEasyModeValue(0, yte$sliderSpeed, valueToSpeed(yte$sliderSpeed.getIntValue())),
                    yte$directionParametersLinked ? yte$getEasyModeValue(0, yte$sliderSpeed, valueToSpeed(yte$sliderSpeed.getIntValue()))
                            : yte$getEasyModeValue(1, yte$sliderDownSpeed, valueToSpeed(yte$sliderDownSpeed.getIntValue())),
                    yte$getEasyModeValue(2, yte$sliderAcceleration, valueToAccel(yte$sliderAcceleration.getIntValue())),
                    yte$directionParametersLinked ? yte$getEasyModeValue(2, yte$sliderAcceleration, valueToAccel(yte$sliderAcceleration.getIntValue()))
                            : yte$getEasyModeValue(3, yte$sliderDownAcceleration, valueToAccel(yte$sliderDownAcceleration.getIntValue())),
                    yte$getEasyModeValue(4, yte$sliderAdoDistance, valueToAdoDistance(yte$sliderAdoDistance.getIntValue())),
                    yte$getEasyModeValue(5, yte$sliderLevellingDistance, valueToLevellingDistance(yte$sliderLevellingDistance.getIntValue())),
                    yte$getEasyModeValue(6, yte$sliderLevellingSpeed, valueToLevellingSpeed(yte$sliderLevellingSpeed.getIntValue())));
        }
        yte$professionalMode = !yte$professionalMode;
        yte$updateModeWidgets();
    }

    @Unique
    private void yte$setEasyModeValues(double upSpeed, double downSpeed, double upAcceleration, double downAcceleration, double adoDistance,
            double levellingDistance, double levellingSpeed) {
        yte$easyModeValues[0] = upSpeed;
        yte$easyModeValues[1] = downSpeed;
        yte$easyModeValues[2] = upAcceleration;
        yte$easyModeValues[3] = downAcceleration;
        yte$easyModeValues[4] = adoDistance;
        yte$easyModeValues[5] = levellingDistance;
        yte$easyModeValues[6] = levellingSpeed;

        yte$sliderSpeed.setValue(speedToValue(upSpeed));
        yte$sliderDownSpeed.setValue(speedToValue(downSpeed));
        yte$sliderAcceleration.setValue(accelToValue(upAcceleration));
        yte$sliderDownAcceleration.setValue(accelToValue(downAcceleration));
        yte$sliderAdoDistance.setValue(adoDistanceToValue(adoDistance));
        yte$sliderLevellingDistance.setValue(levellingDistanceToValue(levellingDistance));
        yte$sliderLevellingSpeed.setValue(levellingSpeedToValue(levellingSpeed));

        yte$easyModeSliderAnchors[0] = yte$sliderSpeed.getIntValue();
        yte$easyModeSliderAnchors[1] = yte$sliderDownSpeed.getIntValue();
        yte$easyModeSliderAnchors[2] = yte$sliderAcceleration.getIntValue();
        yte$easyModeSliderAnchors[3] = yte$sliderDownAcceleration.getIntValue();
        yte$easyModeSliderAnchors[4] = yte$sliderAdoDistance.getIntValue();
        yte$easyModeSliderAnchors[5] = yte$sliderLevellingDistance.getIntValue();
        yte$easyModeSliderAnchors[6] = yte$sliderLevellingSpeed.getIntValue();
        for (int i = 0; i < yte$easyModeSliderTouched.length; i++) {
            yte$easyModeSliderTouched[i] = false;
        }
    }

    @Unique
    private double yte$getEasyModeValue(int index, WidgetShorterSlider slider, double sliderValue) {
        if (slider.getIntValue() != yte$easyModeSliderAnchors[index]) {
            yte$easyModeSliderTouched[index] = true;
        }
        return yte$easyModeSliderTouched[index] ? sliderValue : yte$easyModeValues[index];
    }

    @Unique
    private void yte$syncFieldsFromValues(double upSpeed, double downSpeed, double upAcceleration, double downAcceleration, double adoDistance,
            double levellingDistance, double levellingSpeed) {
        yte$speedField.setText2(Double.toString(upSpeed));
        yte$downSpeedField.setText2(Double.toString(downSpeed));
        yte$accelerationField.setText2(Double.toString(upAcceleration));
        yte$downAccelerationField.setText2(Double.toString(downAcceleration));
        yte$adoDistanceField.setText2(Double.toString(adoDistance));
        yte$levellingDistanceField.setText2(Double.toString(levellingDistance));
        yte$levellingSpeedField.setText2(Double.toString(levellingSpeed));
    }

    @Unique
    private void yte$updateModeWidgets() {
        yte$professionalModeButton.setMessage2(new Text(TextHelper.translatable(yte$professionalMode
                ? "gui.yte.lift_professional_mode_on"
                : "gui.yte.lift_professional_mode_off").data));

        yte$sliderSpeed.setVisibleMapped(!yte$professionalMode);
        yte$sliderAcceleration.setVisibleMapped(!yte$professionalMode);
        yte$sliderDownSpeed.setVisibleMapped(!yte$professionalMode && !yte$directionParametersLinked);
        yte$sliderDownAcceleration.setVisibleMapped(!yte$professionalMode && !yte$directionParametersLinked);
        yte$sliderAdoDistance.setVisibleMapped(!yte$professionalMode);
        yte$sliderLevellingDistance.setVisibleMapped(!yte$professionalMode);
        yte$sliderLevellingSpeed.setVisibleMapped(!yte$professionalMode);

        yte$speedField.setVisibleMapped(yte$professionalMode);
        yte$accelerationField.setVisibleMapped(yte$professionalMode);
        yte$downSpeedField.setVisibleMapped(yte$professionalMode && !yte$directionParametersLinked);
        yte$downAccelerationField.setVisibleMapped(yte$professionalMode && !yte$directionParametersLinked);
        yte$adoDistanceField.setVisibleMapped(yte$professionalMode);
        yte$levellingDistanceField.setVisibleMapped(yte$professionalMode);
        yte$levellingSpeedField.setVisibleMapped(yte$professionalMode);

        yte$directionLinkButton.setMessage2(new Text(TextHelper.translatable(yte$directionParametersLinked
                ? "gui.yte.lift_direction_link_on"
                : "gui.yte.lift_direction_link_off").data));
        yte$motionProfileButton.setMessage2(new Text(TextHelper.translatable(
                yte$motionProfile.getTranslationKey()).data));
        yte$doorHoldButton.setMessage2(new Text(TextHelper.translatable(yte$doorHoldEnabled
                ? "gui.yte.lift_door_hold_on"
                : "gui.yte.lift_door_hold_off").data));
        yte$doorButtonLightModeButton.setMessage2(new Text(TextHelper.translatable(
                yte$doorButtonLightMode.getTranslationKey()).data));
        yte$floorCancelModeButton.setMessage2(new Text(TextHelper.translatable(
                yte$floorCancelMode.getTranslationKey()).data));
    }

    @Unique
    private void yte$drawInputLabel(GraphicsHolder graphicsHolder, String key, int row) {
        graphicsHolder.drawText(TextHelper.translatable(key), 0,
                IGui.SQUARE_SIZE * row + IGui.TEXT_PADDING,
                IGui.ARGB_WHITE, false, GraphicsHolder.getDefaultLight());
    }

    @Unique
    private void yte$drawModeLabel(GraphicsHolder graphicsHolder, String inputKey, String valueKey, double value, int row) {
        graphicsHolder.drawText(TextHelper.translatable(yte$professionalMode ? inputKey : valueKey,
                        String.format(Locale.ROOT, "%.2f", value)), 0,
                IGui.SQUARE_SIZE * row + IGui.TEXT_PADDING,
                IGui.ARGB_WHITE, false, GraphicsHolder.getDefaultLight());
    }

    @Unique
    private static double yte$parseNumber(TextFieldWidgetExtension field, double fallback, double maximum) {
        return yte$parseNumber(field, fallback, 0, maximum);
    }

    @Unique
    private static double yte$parseNumber(TextFieldWidgetExtension field, double fallback, double minimum, double maximum) {
        try {
            final double value = Double.parseDouble(field.getText2().trim().replace(',', '.'));
            return Double.isFinite(value) ? Math.max(minimum, Math.min(maximum, value)) : fallback;
        } catch (NumberFormatException ignored) {
            return fallback;
        }
    }

    @Unique
    private double yte$getMaxScroll() {
        return Math.max(0, yte$getContentRows() * IGui.SQUARE_SIZE - getHeightMapped());
    }

    @Unique
    private void yte$drawScrollbar(GraphicsHolder graphicsHolder) {
        final double maxScroll = yte$getMaxScroll();
        if (maxScroll <= 0) {
            return;
        }
        final int screenHeight = getHeightMapped();
        final int trackWidth = 4;
        final int panelRight = yte$getPanelRight();
        final int trackX = Math.max(0, panelRight - trackWidth);
        final int thumbHeight = yte$getScrollbarThumbHeight();
        final int thumbY = yte$getScrollbarThumbY();
        final GuiDrawing guiDrawing = new GuiDrawing(graphicsHolder);
        guiDrawing.beginDrawingRectangle();
        guiDrawing.drawRectangle(trackX, 0, panelRight, screenHeight, 0x66000000);
        guiDrawing.drawRectangle(trackX, thumbY, panelRight, thumbY + thumbHeight, 0xFFAAAAAA);
        guiDrawing.finishDrawingRectangle();
    }

    @Unique
    private int yte$getScrollbarThumbHeight() {
        final int screenHeight = getHeightMapped();
        return Math.max(IGui.SQUARE_SIZE, screenHeight * screenHeight / (yte$getContentRows() * IGui.SQUARE_SIZE));
    }

    @Unique
    private int yte$getContentRows() {
        return yte$directionParametersLinked ? 27 : 31;
    }

    @Unique
    private int yte$getScrollbarThumbY() {
        final int availableHeight = getHeightMapped() - yte$getScrollbarThumbHeight();
        return (int) Math.round(yte$scrollOffset / yte$getMaxScroll() * availableHeight);
    }

    @Unique
    private void yte$setScrollFromThumb(double thumbY) {
        final int availableHeight = getHeightMapped() - yte$getScrollbarThumbHeight();
        if (availableHeight > 0) {
            yte$scrollOffset = Math.max(0, Math.min(yte$getMaxScroll(), thumbY / availableHeight * yte$getMaxScroll()));
        }
    }

    @Unique
    private static double valueToSpeed(int sliderValue) {
        return sliderValue == 0 ? 0.1 : sliderValue * 0.5;
    }

    @Unique
    private static int speedToValue(double speed) {
        return yte$floorToSlider(speed, 0.5, SPEED_SLIDER_MAX);
    }

    @Unique
    private static double valueToAccel(int sliderValue) {
        return sliderValue == 0 ? 0.1 : sliderValue * 0.5;
    }

    @Unique
    private static int accelToValue(double accel) {
        return yte$floorToSlider(accel, 0.5, ACCEL_SLIDER_MAX);
    }

    @Unique
    private static double valueToAdoDistance(int sliderValue) {
        return sliderValue / 100.0;
    }

    @Unique
    private static int adoDistanceToValue(double distance) {
        return yte$floorToSlider(distance, 0.01, ADO_DISTANCE_SLIDER_MAX);
    }

    @Unique
    private static double valueToLevellingDistance(int sliderValue) {
        return sliderValue / 20.0;
    }

    @Unique
    private static int levellingDistanceToValue(double distance) {
        return yte$floorToSlider(distance, 0.05, LEVELLING_DISTANCE_SLIDER_MAX);
    }

    @Unique
    private static double valueToLevellingSpeed(int sliderValue) {
        return sliderValue / 20.0;
    }

    @Unique
    private static int levellingSpeedToValue(double speed) {
        return yte$floorToSlider(speed, 0.05, LEVELLING_SPEED_SLIDER_MAX);
    }

    @Unique
    private static int yte$floorToSlider(double value, double step, int maximum) {
        return Math.max(0, Math.min(maximum, (int) Math.floor(value / step + 1E-9)));
    }
}
