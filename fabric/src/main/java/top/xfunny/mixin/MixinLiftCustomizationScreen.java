package top.xfunny.mixin;

import org.mtr.core.data.Lift;
import org.mtr.mapping.holder.ClickableWidget;
import org.mtr.mapping.holder.Text;
import org.mtr.mapping.mapper.ButtonWidgetExtension;
import org.mtr.mapping.mapper.CheckboxWidgetExtension;
import org.mtr.mapping.mapper.GraphicsHolder;
import org.mtr.mapping.mapper.GuiDrawing;
import org.mtr.mapping.mapper.TextHelper;
import org.mtr.mapping.mapper.TextFieldWidgetExtension;
import org.mtr.mapping.tool.TextCase;
import org.mtr.mod.data.IGui;
import org.mtr.mod.generated.lang.TranslationProvider;
import org.mtr.mod.screen.LiftCustomizationScreen;
import org.mtr.mod.screen.MTRScreenBase;
import org.mtr.mod.screen.WidgetShorterSlider;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.xfunny.core.data.YteLiftConfig;
import top.xfunny.core.operation.YteUpdateDataRequest;
import top.xfunny.mod.client.InitClient;
import top.xfunny.mod.client.YteMinecraftClientData;
import top.xfunny.mod.client.screen.GuiHelper;
import top.xfunny.mod.config.YteLiftConfigStore;
import top.xfunny.mod.packet.YtePacketUpdateData;
import top.xfunny.mod.lift.DoorMotionCurve;
import top.xfunny.mod.lift.LiftArrivalLanternTriggerMode;
import top.xfunny.mod.lift.LiftDoorButtonLightMode;
import top.xfunny.mod.lift.LiftFloorCancelMode;
import top.xfunny.mod.lift.FiremanOperationType;
import top.xfunny.mod.lift.LiftMotionProfile;

import java.util.Locale;

@Mixin(value = LiftCustomizationScreen.class, remap = false)
public abstract class MixinLiftCustomizationScreen extends MTRScreenBase {

    @Shadow
    @Final
    private Lift lift;

    @Shadow
    @Final
    private ButtonWidgetExtension buttonHeightMinus;

    @Shadow
    @Final
    private ButtonWidgetExtension buttonHeightAdd;

    @Shadow
    @Final
    private ButtonWidgetExtension buttonWidthMinus;

    @Shadow
    @Final
    private ButtonWidgetExtension buttonWidthAdd;

    @Shadow
    @Final
    private ButtonWidgetExtension buttonDepthMinus;

    @Shadow
    @Final
    private ButtonWidgetExtension buttonDepthAdd;

    @Shadow
    @Final
    private ButtonWidgetExtension buttonOffsetXMinus;

    @Shadow
    @Final
    private ButtonWidgetExtension buttonOffsetXAdd;

    @Shadow
    @Final
    private ButtonWidgetExtension buttonOffsetYMinus;

    @Shadow
    @Final
    private ButtonWidgetExtension buttonOffsetYAdd;

    @Shadow
    @Final
    private ButtonWidgetExtension buttonOffsetZMinus;

    @Shadow
    @Final
    private ButtonWidgetExtension buttonOffsetZAdd;

    @Shadow
    @Final
    private CheckboxWidgetExtension buttonIsDoubleSided;

    @Shadow
    @Final
    private ButtonWidgetExtension buttonLiftStyle;

    @Shadow
    @Final
    private ButtonWidgetExtension buttonRotateAnticlockwise;

    @Shadow
    @Final
    private ButtonWidgetExtension buttonRotateClockwise;

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
    @Unique private ButtonWidgetExtension yte$firemanLiftButton;
    @Unique private ButtonWidgetExtension yte$firemanOperationButton;
    @Unique private ButtonWidgetExtension yte$motionProfileButton;
    @Unique private ButtonWidgetExtension yte$doorHoldButton;
    @Unique private ButtonWidgetExtension yte$doorButtonLightModeButton;
    @Unique private ButtonWidgetExtension yte$floorCancelModeButton;
    @Unique private ButtonWidgetExtension yte$arrivalLanternTriggerModeButton;
    @Unique private ButtonWidgetExtension yte$serviceModeButton;
    @Unique private TextFieldWidgetExtension yte$speedField;
    @Unique private TextFieldWidgetExtension yte$accelerationField;
    @Unique private TextFieldWidgetExtension yte$downSpeedField;
    @Unique private TextFieldWidgetExtension yte$downAccelerationField;
    @Unique private TextFieldWidgetExtension yte$adoDistanceField;
    @Unique private TextFieldWidgetExtension yte$levellingDistanceField;
    @Unique private TextFieldWidgetExtension yte$levellingSpeedField;
    @Unique private TextFieldWidgetExtension yte$doorOpenMsField;
    @Unique private TextFieldWidgetExtension yte$doorCloseMsField;
    @Unique private TextFieldWidgetExtension yte$doorDwellMsField;
    @Unique private TextFieldWidgetExtension yte$doorRunDelayMsField;
    @Unique private ButtonWidgetExtension yte$doorCurveButton;
    @Unique private WidgetShorterSlider yte$sliderDoorOpenMs;
    @Unique private WidgetShorterSlider yte$sliderDoorCloseMs;
    @Unique private WidgetShorterSlider yte$sliderDoorDwellMs;
    @Unique private WidgetShorterSlider yte$sliderDoorRunDelayMs;
    @Unique private TextFieldWidgetExtension yte$recoverySpeedField;
    @Unique private TextFieldWidgetExtension yte$maxDoorOpenMsField;
    @Unique private TextFieldWidgetExtension yte$liftNumberField;
    @Unique private TextFieldWidgetExtension yte$fireRecallFloorField;
    @Unique private WidgetShorterSlider yte$sliderRecoverySpeed;
    @Unique private WidgetShorterSlider yte$sliderMaxDoorOpenMs;

    @Unique
    private static final int SPEED_SLIDER_MAX = 40;

    @Unique
    private static final int ACCEL_SLIDER_MAX = 20;

    @Unique private static final int ADO_DISTANCE_SLIDER_MAX = 30;
    @Unique private static final int LEVELLING_DISTANCE_SLIDER_MAX = 20;
    @Unique private static final int LEVELLING_SPEED_SLIDER_MAX = 20;
    /** 开门/关门动画 1000–10000ms；保持时长 -1–20000ms；启动延迟 0–5000ms；步进 100。 */
    @Unique private static final long DOOR_ANIM_MIN = 1000;
    @Unique private static final long DOOR_MS_MIN = 2000;
    @Unique private static final long DOOR_MS_STEP = 100;
    @Unique private static final long DOOR_OPEN_CLOSE_MAX = 10000;
    @Unique private static final long DOOR_DWELL_MAX = 20000;
    @Unique private static final long DOOR_RUN_DELAY_MIN = 0;
    @Unique private static final long DOOR_RUN_DELAY_MAX = 5000;
    @Unique private static final int DOOR_OPEN_CLOSE_SLIDER_MAX = (int) ((DOOR_OPEN_CLOSE_MAX - DOOR_ANIM_MIN) / DOOR_MS_STEP);
    @Unique private static final int DOOR_DWELL_SLIDER_MAX = (int) ((DOOR_DWELL_MAX - DOOR_MS_MIN) / DOOR_MS_STEP);
    @Unique private static final int DOOR_RUN_DELAY_SLIDER_MAX = (int) ((DOOR_RUN_DELAY_MAX - DOOR_RUN_DELAY_MIN) / DOOR_MS_STEP);
    /** 救援速度滑块：0.1–1.0 m/s，步进 0.05。 */
    @Unique private static final double RECOVERY_SPEED_SLIDER_STEP = 0.05;
    @Unique private static final int RECOVERY_SPEED_SLIDER_MAX = 18;
    /** 光幕最大开门时长滑块：30–60s，步进 1s。 */
    @Unique private static final long MAX_DOOR_OPEN_SLIDER_STEP = 1000;
    @Unique private static final int MAX_DOOR_OPEN_SLIDER_MAX = 30;
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
    @Unique private LiftArrivalLanternTriggerMode yte$arrivalLanternTriggerMode = LiftArrivalLanternTriggerMode.DECELERATION;
    @Unique private LiftArrivalLanternTriggerMode yte$lastSentArrivalLanternTriggerMode = LiftArrivalLanternTriggerMode.DECELERATION;
    /** 服务模式（LiftMode 名）：NORMAL/INDEPENDENT/ATTENDANT 三态循环。 */
    @Unique private static final String[] SERVICE_MODE_CYCLE = {"NORMAL", "INDEPENDENT", "ATTENDANT"};
    @Unique private String yte$serviceMode = "NORMAL";
    @Unique private String yte$lastSentServiceMode = "NORMAL";
    @Unique private String yte$liftNumber = "";
    @Unique private String yte$lastSentLiftNumber = "";
    @Unique private long yte$doorOpenMs;
    @Unique private long yte$doorCloseMs;
    @Unique private long yte$doorDwellMs;
    @Unique private long yte$doorRunDelayMs;
    @Unique private DoorMotionCurve yte$doorCurve = DoorMotionCurve.LINEAR;
    @Unique private long yte$lastSentDoorOpenMs;
    @Unique private long yte$lastSentDoorCloseMs;
    @Unique private long yte$lastSentDoorDwellMs;
    @Unique private long yte$lastSentDoorRunDelayMs;
    @Unique private DoorMotionCurve yte$lastSentDoorCurve = DoorMotionCurve.LINEAR;
    @Unique private double yte$lastSentRecoverySpeed = -1;
    @Unique private long yte$lastSentMaxDoorOpenMs = -1;
    @Unique private double yte$lastSentAdoDistance = -1;
    @Unique private double yte$lastSentLevellingDistance = -1;
    @Unique private double yte$lastSentLevellingSpeed = -1;
    @Unique private boolean yte$firemanLift;
    @Unique private boolean yte$lastSentFiremanLift;
    @Unique private FiremanOperationType yte$firemanOperation = FiremanOperationType.HOLD_DOOR_BUTTON;
    @Unique private FiremanOperationType yte$lastSentFiremanOperation = FiremanOperationType.HOLD_DOOR_BUTTON;
    @Unique private String yte$fireRecallFloor = YteLiftConfig.DEFAULT_FIRE_RECALL_FLOOR;
    @Unique private String yte$lastSentFireRecallFloor = YteLiftConfig.DEFAULT_FIRE_RECALL_FLOOR;
    @Unique private final double[] yte$easyModeValues = new double[7];
    @Unique private final int[] yte$easyModeSliderAnchors = new int[7];
    @Unique private final boolean[] yte$easyModeSliderTouched = new boolean[7];

    // 标签页
    @Unique private static final int TAB_BASE = 0;
    @Unique private static final int TAB_MOTION = 1;
    @Unique private static final int TAB_LEVEL = 2;
    @Unique private static final int TAB_DOOR = 3;
    @Unique private static final int TAB_COUNT = 4;
    @Unique private static final int PANEL_BACKGROUND = 0xD9121212;
    @Unique private int yte$activeTab = TAB_BASE;
    @Unique private int yte$scrollOffset;
    @Unique private ButtonWidgetExtension yte$tabSizeButton;
    @Unique private ButtonWidgetExtension yte$tabMotionButton;
    @Unique private ButtonWidgetExtension yte$tabLevelButton;
    @Unique private ButtonWidgetExtension yte$tabDoorButton;

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
        yte$arrivalLanternTriggerMode = config == null
                ? LiftArrivalLanternTriggerMode.DECELERATION : config.getArrivalLanternTriggerMode();
        yte$serviceMode = config == null || config.getServiceMode() == null
                ? "NORMAL" : config.getServiceMode();
        yte$liftNumber = config == null ? "" : config.getLiftNumber();
        yte$firemanLift = config != null && config.isFiremanLift();
        yte$lastSentFiremanLift = yte$firemanLift;
        yte$firemanOperation = config == null ? FiremanOperationType.HOLD_DOOR_BUTTON : config.getFiremanOperation();
        yte$lastSentFiremanOperation = yte$firemanOperation;
        yte$fireRecallFloor = config == null ? YteLiftConfig.DEFAULT_FIRE_RECALL_FLOOR : config.getFireRecallFloor();
        yte$lastSentFireRecallFloor = yte$fireRecallFloor;
        final double currentAdoDistance = config != null ? config.getAdoDistance() : YteLiftConfig.DEFAULT_ADO_DISTANCE;
        final double currentLevellingDistance = config != null ? config.getLevellingDistance() : YteLiftConfig.DEFAULT_LEVELLING_DISTANCE;
        final double currentLevellingSpeed = config != null ? config.getLevellingSpeed() : YteLiftConfig.DEFAULT_LEVELLING_SPEED;
        yte$doorOpenMs = config == null ? YteLiftConfig.DEFAULT_DOOR_OPEN_MS : config.getDoorOpenMs();
        yte$doorCloseMs = config == null ? YteLiftConfig.DEFAULT_DOOR_CLOSE_MS : config.getDoorCloseMs();
        yte$doorDwellMs = config == null ? YteLiftConfig.DEFAULT_DOOR_DWELL_MS : config.getDoorDwellMs();
        yte$doorRunDelayMs = config == null ? YteLiftConfig.DEFAULT_DOOR_RUN_DELAY_MS : config.getDoorRunDelayMs();
        yte$doorCurve = config == null ? DoorMotionCurve.LINEAR : config.getDoorCurve();
        final double currentRecoverySpeed = config != null
                ? config.getRecoverySpeed() : YteLiftConfig.DEFAULT_RECOVERY_SPEED;
        final long currentMaxDoorOpenMs = config != null
                ? config.getMaxDoorOpenMs() : YteLiftConfig.DEFAULT_MAX_DOOR_OPEN_MS;

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

        yte$sliderDoorOpenMs = new WidgetShorterSlider(0, 60, DOOR_OPEN_CLOSE_SLIDER_MAX, value -> "", null);
        yte$sliderDoorOpenMs.setValue(doorMsToValue(yte$doorOpenMs, DOOR_ANIM_MIN, DOOR_OPEN_CLOSE_MAX));
        yte$sliderDoorCloseMs = new WidgetShorterSlider(0, 60, DOOR_OPEN_CLOSE_SLIDER_MAX, value -> "", null);
        yte$sliderDoorCloseMs.setValue(doorMsToValue(yte$doorCloseMs, DOOR_ANIM_MIN, DOOR_OPEN_CLOSE_MAX));
        yte$sliderDoorDwellMs = new WidgetShorterSlider(0, 60, DOOR_DWELL_SLIDER_MAX, value -> "", null);
        yte$sliderDoorDwellMs.setValue(doorMsToValue(yte$doorDwellMs, DOOR_MS_MIN, DOOR_DWELL_MAX));
        yte$sliderDoorRunDelayMs = new WidgetShorterSlider(0, 60, DOOR_RUN_DELAY_SLIDER_MAX, value -> "", null);
        yte$sliderDoorRunDelayMs.setValue(doorMsToValue(yte$doorRunDelayMs, DOOR_RUN_DELAY_MIN, DOOR_RUN_DELAY_MAX));

        yte$sliderRecoverySpeed = new WidgetShorterSlider(0, 60, RECOVERY_SPEED_SLIDER_MAX, value -> "", null);
        yte$sliderRecoverySpeed.setValue(recoverySpeedToValue(currentRecoverySpeed));
        yte$sliderMaxDoorOpenMs = new WidgetShorterSlider(0, 60, MAX_DOOR_OPEN_SLIDER_MAX, value -> "", null);
        yte$sliderMaxDoorOpenMs.setValue(maxDoorOpenMsToValue(currentMaxDoorOpenMs));

        yte$setEasyModeValues(currentSpeed, currentDownSpeed, currentAccel, currentDownAccel,
                currentAdoDistance, currentLevellingDistance, currentLevellingSpeed);

        yte$professionalModeButton = new ButtonWidgetExtension(0, 0, 0, IGui.SQUARE_SIZE,
                TextHelper.literal(""), button -> yte$toggleProfessionalMode());
        yte$directionLinkButton = new ButtonWidgetExtension(0, 0, 0, IGui.SQUARE_SIZE,
                TextHelper.literal(""), button -> yte$toggleDirectionParametersLinked());
        yte$firemanLiftButton = new ButtonWidgetExtension(0, 0, 0, IGui.SQUARE_SIZE,
                TextHelper.literal(""), button -> yte$toggleFiremanLift());
        yte$firemanOperationButton = new ButtonWidgetExtension(0, 0, 0, IGui.SQUARE_SIZE,
                TextHelper.literal(""), button -> yte$toggleFiremanOperation());
        yte$motionProfileButton = new ButtonWidgetExtension(0, 0, 0, IGui.SQUARE_SIZE,
                TextHelper.literal(""), button -> yte$toggleMotionProfile());
        yte$doorHoldButton = new ButtonWidgetExtension(0, 0, 0, IGui.SQUARE_SIZE,
                TextHelper.literal(""), button -> yte$toggleDoorHold());
        yte$doorButtonLightModeButton = new ButtonWidgetExtension(0, 0, 0, IGui.SQUARE_SIZE,
                TextHelper.literal(""), button -> yte$toggleDoorButtonLightMode());
        yte$floorCancelModeButton = new ButtonWidgetExtension(0, 0, 0, IGui.SQUARE_SIZE,
                TextHelper.literal(""), button -> yte$toggleFloorCancelMode());
        yte$doorCurveButton = new ButtonWidgetExtension(0, 0, 0, IGui.SQUARE_SIZE,
                TextHelper.literal(""), button -> yte$toggleDoorCurve());
        yte$doorCurveButton.setMessage2(new Text(TextHelper.translatable(
                yte$doorCurve.getTranslationKey()).data));
        yte$arrivalLanternTriggerModeButton = new ButtonWidgetExtension(0, 0, 0, IGui.SQUARE_SIZE,
                TextHelper.literal(""), button -> yte$toggleArrivalLanternTriggerMode());
        yte$serviceModeButton = new ButtonWidgetExtension(0, 0, 0, IGui.SQUARE_SIZE,
                TextHelper.literal(""), button -> yte$toggleServiceMode());

        yte$tabSizeButton = new ButtonWidgetExtension(0, 0, 0, IGui.SQUARE_SIZE,
                TextHelper.translatable("gui.yte.lift_tab_base"), button -> yte$onSelectTab(TAB_BASE));
        yte$tabMotionButton = new ButtonWidgetExtension(0, 0, 0, IGui.SQUARE_SIZE,
                TextHelper.translatable("gui.yte.lift_tab_motion"), button -> yte$onSelectTab(TAB_MOTION));
        yte$tabLevelButton = new ButtonWidgetExtension(0, 0, 0, IGui.SQUARE_SIZE,
                TextHelper.translatable("gui.yte.lift_tab_level"), button -> yte$onSelectTab(TAB_LEVEL));
        yte$tabDoorButton = new ButtonWidgetExtension(0, 0, 0, IGui.SQUARE_SIZE,
                TextHelper.translatable("gui.yte.lift_tab_door"), button -> yte$onSelectTab(TAB_DOOR));

        yte$speedField = yte$createNumberField(currentSpeed);
        yte$accelerationField = yte$createNumberField(currentAccel);
        yte$downSpeedField = yte$createNumberField(currentDownSpeed);
        yte$downAccelerationField = yte$createNumberField(currentDownAccel);
        yte$adoDistanceField = yte$createNumberField(currentAdoDistance);
        yte$levellingDistanceField = yte$createNumberField(currentLevellingDistance);
        yte$levellingSpeedField = yte$createNumberField(currentLevellingSpeed);
        yte$doorOpenMsField = yte$createNumberField(yte$doorOpenMs);
        yte$doorCloseMsField = yte$createNumberField(yte$doorCloseMs);
        yte$doorDwellMsField = yte$createNumberField(yte$doorDwellMs);
        yte$doorRunDelayMsField = yte$createNumberField(yte$doorRunDelayMs);
        yte$recoverySpeedField = yte$createNumberField(currentRecoverySpeed);
        yte$maxDoorOpenMsField = yte$createNumberField(currentMaxDoorOpenMs);
        yte$liftNumberField = yte$createLiftNumberField();
        yte$liftNumberField.setText2(yte$liftNumber);
        yte$fireRecallFloorField = yte$createLiftNumberField();
        yte$fireRecallFloorField.setText2(yte$fireRecallFloor);

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
        yte$lastSentArrivalLanternTriggerMode = yte$arrivalLanternTriggerMode;
        yte$lastSentServiceMode = yte$serviceMode;
        yte$lastSentLiftNumber = yte$liftNumber;
        yte$lastSentDoorOpenMs = yte$doorOpenMs;
        yte$lastSentDoorCloseMs = yte$doorCloseMs;
        yte$lastSentDoorDwellMs = yte$doorDwellMs;
        yte$lastSentDoorRunDelayMs = yte$doorRunDelayMs;
        yte$lastSentDoorCurve = yte$doorCurve;
        yte$lastSentRecoverySpeed = currentRecoverySpeed;
        yte$lastSentMaxDoorOpenMs = currentMaxDoorOpenMs;
    }

    @Inject(method = "init2", at = @At("TAIL"))
    private void onInit2(CallbackInfo ci) {
        // 顶部标签栏：均分整个面板宽度
        final int tabWidth = IGui.PANEL_WIDTH / TAB_COUNT;
        yte$tabSizeButton.setX2(0);
        yte$tabSizeButton.setY2(0);
        yte$tabSizeButton.setWidth2(tabWidth);
        yte$tabMotionButton.setX2(tabWidth);
        yte$tabMotionButton.setY2(0);
        yte$tabMotionButton.setWidth2(tabWidth);
        yte$tabLevelButton.setX2(tabWidth * 2);
        yte$tabLevelButton.setY2(0);
        yte$tabLevelButton.setWidth2(tabWidth);
        yte$tabDoorButton.setX2(tabWidth * 3);
        yte$tabDoorButton.setY2(0);
        yte$tabDoorButton.setWidth2(IGui.PANEL_WIDTH - tabWidth * 3);

        addChild(new ClickableWidget(yte$tabSizeButton));
        addChild(new ClickableWidget(yte$tabMotionButton));
        addChild(new ClickableWidget(yte$tabLevelButton));
        addChild(new ClickableWidget(yte$tabDoorButton));

        addChild(new ClickableWidget(yte$professionalModeButton));
        addChild(new ClickableWidget(yte$directionLinkButton));
        addChild(new ClickableWidget(yte$firemanLiftButton));
        addChild(new ClickableWidget(yte$firemanOperationButton));
        addChild(new ClickableWidget(yte$fireRecallFloorField));
        addChild(new ClickableWidget(yte$motionProfileButton));
        addChild(new ClickableWidget(yte$doorHoldButton));
        addChild(new ClickableWidget(yte$doorButtonLightModeButton));
        addChild(new ClickableWidget(yte$floorCancelModeButton));
        addChild(new ClickableWidget(yte$arrivalLanternTriggerModeButton));
        addChild(new ClickableWidget(yte$serviceModeButton));
        addChild(new ClickableWidget(yte$sliderSpeed));
        addChild(new ClickableWidget(yte$sliderAcceleration));
        addChild(new ClickableWidget(yte$sliderDownSpeed));
        addChild(new ClickableWidget(yte$sliderDownAcceleration));
        addChild(new ClickableWidget(yte$sliderAdoDistance));
        addChild(new ClickableWidget(yte$sliderLevellingDistance));
        addChild(new ClickableWidget(yte$sliderLevellingSpeed));
        addChild(new ClickableWidget(yte$sliderDoorOpenMs));
        addChild(new ClickableWidget(yte$sliderDoorCloseMs));
        addChild(new ClickableWidget(yte$sliderDoorDwellMs));
        addChild(new ClickableWidget(yte$sliderDoorRunDelayMs));
        addChild(new ClickableWidget(yte$sliderRecoverySpeed));
        addChild(new ClickableWidget(yte$sliderMaxDoorOpenMs));

        addChild(new ClickableWidget(yte$speedField));
        addChild(new ClickableWidget(yte$accelerationField));
        addChild(new ClickableWidget(yte$downSpeedField));
        addChild(new ClickableWidget(yte$downAccelerationField));
        addChild(new ClickableWidget(yte$adoDistanceField));
        addChild(new ClickableWidget(yte$levellingDistanceField));
        addChild(new ClickableWidget(yte$levellingSpeedField));
        addChild(new ClickableWidget(yte$doorOpenMsField));
        addChild(new ClickableWidget(yte$doorCloseMsField));
        addChild(new ClickableWidget(yte$doorDwellMsField));
        addChild(new ClickableWidget(yte$doorRunDelayMsField));
        addChild(new ClickableWidget(yte$recoverySpeedField));
        addChild(new ClickableWidget(yte$maxDoorOpenMsField));
        addChild(new ClickableWidget(yte$liftNumberField));
        addChild(new ClickableWidget(yte$doorCurveButton));

        // 文本框会在界面初始化生命周期里被重建，挂载后恢复可见文本
        yte$syncFieldsFromValues(yte$lastSentSpeed, yte$lastSentDownSpeed, yte$lastSentAccel, yte$lastSentDownAccel, yte$lastSentAdoDistance,
                yte$lastSentLevellingDistance, yte$lastSentLevellingSpeed);
        yte$liftNumberField.setText2(yte$liftNumber);
        yte$doorOpenMsField.setText2(Long.toString(yte$lastSentDoorOpenMs));
        yte$doorCloseMsField.setText2(Long.toString(yte$lastSentDoorCloseMs));
        yte$doorDwellMsField.setText2(Long.toString(yte$lastSentDoorDwellMs));
        yte$doorRunDelayMsField.setText2(Long.toString(yte$lastSentDoorRunDelayMs));
        yte$recoverySpeedField.setText2(Double.toString(yte$lastSentRecoverySpeed));
        yte$maxDoorOpenMsField.setText2(Long.toString(yte$lastSentMaxDoorOpenMs));

        yte$activeTab = TAB_BASE;
        yte$onSelectTab(TAB_BASE);
    }

    @Override
    public void render(GraphicsHolder graphicsHolder, int mouseX, int mouseY, float delta) {
        // 半透明侧边栏背景，保留世界可见，方便边看边调
        final GuiDrawing guiDrawing = new GuiDrawing(graphicsHolder);
        guiDrawing.beginDrawingRectangle();
        guiDrawing.drawRectangle(0, 0, IGui.PANEL_WIDTH, getHeightMapped(), PANEL_BACKGROUND);
        guiDrawing.finishDrawingRectangle();

        // 全屏背景渐变（super.render 内部也会绘制，但会被下面的 scissor 裁到内容区，先画全屏避免面板外变亮）
        renderBackground(graphicsHolder);

        final double[] values = yte$computeCurrentValues();

        // 内容区裁剪到标签栏之下、footer 之上，滚动时内容不会盖住顶部标签栏
        GuiHelper.enableScissor(graphicsHolder, 0, IGui.SQUARE_SIZE, IGui.PANEL_WIDTH, yte$getContentBottom());
        try {
            super.render(graphicsHolder, mouseX, mouseY, delta);
            yte$drawScrollbar(graphicsHolder);
            yte$drawTabLabels(graphicsHolder, values);
        } finally {
            GuiHelper.disableScissor(graphicsHolder);
        }

        // 标签栏常驻顶部，渲染在内容之上
        yte$tabSizeButton.render(graphicsHolder, mouseX, mouseY, delta);
        yte$tabMotionButton.render(graphicsHolder, mouseX, mouseY, delta);
        yte$tabLevelButton.render(graphicsHolder, mouseX, mouseY, delta);
        yte$tabDoorButton.render(graphicsHolder, mouseX, mouseY, delta);

        if (yte$professionalModeButton.getVisibleMapped()) {
            // 常驻 footer 需盖在内容之上（小屏内容溢出时）
            yte$professionalModeButton.render(graphicsHolder, mouseX, mouseY, delta);
        }

        yte$syncValuesToServer(values);
    }

    @Unique
    private void yte$onSelectTab(int tab) {
        yte$activeTab = tab;
        yte$tabSizeButton.active = tab != TAB_BASE;
        yte$tabMotionButton.active = tab != TAB_MOTION;
        yte$tabLevelButton.active = tab != TAB_LEVEL;
        yte$tabDoorButton.active = tab != TAB_DOOR;
        yte$scrollOffset = 0;
        yte$layoutContent();
        yte$updateModeWidgets();
    }

    @Unique
    private void yte$layoutContent() {
        // 全局 footer：专业模式开关常驻底部（切换输入方式，作用于运行/平层标签）
        yte$professionalModeButton.setX2(0);
        yte$professionalModeButton.setY2(getHeightMapped() - IGui.SQUARE_SIZE);
        yte$professionalModeButton.setWidth2(IGui.PANEL_WIDTH);

        switch (yte$activeTab) {
            case TAB_BASE:
                yte$positionField(yte$liftNumberField, 2);
                yte$positionMinusPlus(buttonHeightMinus, buttonHeightAdd, 4);
                yte$positionMinusPlus(buttonWidthMinus, buttonWidthAdd, 5);
                yte$positionMinusPlus(buttonDepthMinus, buttonDepthAdd, 6);
                yte$positionMinusPlus(buttonOffsetXMinus, buttonOffsetXAdd, 7);
                yte$positionMinusPlus(buttonOffsetYMinus, buttonOffsetYAdd, 8);
                yte$positionMinusPlus(buttonOffsetZMinus, buttonOffsetZAdd, 9);
                yte$positionFullWidth(buttonIsDoubleSided, 11);
                yte$positionFullWidth(buttonLiftStyle, 12);
                yte$positionFullWidth(buttonRotateAnticlockwise, 13);
                yte$positionFullWidth(buttonRotateClockwise, 14);
                break;
            case TAB_MOTION:
                yte$positionFullWidth(yte$directionLinkButton, 1);
                yte$positionFullWidth(yte$motionProfileButton, 2);
                yte$positionFullWidth(yte$firemanLiftButton, 12);
                yte$positionFullWidth(yte$firemanOperationButton, 13);
                yte$positionField(yte$fireRecallFloorField, 15);
                yte$positionSlider(yte$sliderSpeed, 4);
                yte$positionField(yte$speedField, 4);
                yte$positionSlider(yte$sliderAcceleration, 6);
                yte$positionField(yte$accelerationField, 6);
                yte$positionSlider(yte$sliderDownSpeed, 8);
                yte$positionField(yte$downSpeedField, 8);
                yte$positionSlider(yte$sliderDownAcceleration, 10);
                yte$positionField(yte$downAccelerationField, 10);
                break;
            case TAB_LEVEL:
                yte$positionSlider(yte$sliderAdoDistance, 2);
                yte$positionField(yte$adoDistanceField, 2);
                yte$positionSlider(yte$sliderLevellingDistance, 4);
                yte$positionField(yte$levellingDistanceField, 4);
                yte$positionSlider(yte$sliderLevellingSpeed, 6);
                yte$positionField(yte$levellingSpeedField, 6);
                break;
            case TAB_DOOR:
                yte$positionFullWidth(yte$doorHoldButton, 1);
                yte$positionFullWidth(yte$doorButtonLightModeButton, 2);
                yte$positionFullWidth(yte$floorCancelModeButton, 3);
                yte$positionFullWidth(yte$doorCurveButton, 4);
                yte$positionSlider(yte$sliderDoorOpenMs, 6);
                yte$positionField(yte$doorOpenMsField, 6);
                yte$positionSlider(yte$sliderDoorCloseMs, 8);
                yte$positionField(yte$doorCloseMsField, 8);
                yte$positionSlider(yte$sliderDoorDwellMs, 10);
                yte$positionField(yte$doorDwellMsField, 10);
                yte$positionSlider(yte$sliderDoorRunDelayMs, 12);
                yte$positionField(yte$doorRunDelayMsField, 12);
                yte$positionSlider(yte$sliderRecoverySpeed, 14);
                yte$positionField(yte$recoverySpeedField, 14);
                yte$positionSlider(yte$sliderMaxDoorOpenMs, 16);
                yte$positionField(yte$maxDoorOpenMsField, 16);
                yte$positionFullWidth(yte$arrivalLanternTriggerModeButton, 17);
                yte$positionFullWidth(yte$serviceModeButton, 18);
                break;
            default:
                break;
        }

        yte$clampScroll();
    }

    @Unique
    private int yte$contentY(int row) {
        return IGui.SQUARE_SIZE * row - yte$scrollOffset;
    }

    @Unique
    private int yte$getContentBottom() {
        return (yte$activeTab == TAB_MOTION || yte$activeTab == TAB_LEVEL)
                ? getHeightMapped() - IGui.SQUARE_SIZE
                : getHeightMapped();
    }

    @Unique
    private int yte$getMaxVisibleRow() {
        switch (yte$activeTab) {
            case TAB_BASE:
                return 14;
            case TAB_MOTION:
                return 15;
            case TAB_LEVEL:
                return 6;
            case TAB_DOOR:
                return 18;
            default:
                return 1;
        }
    }

    @Unique
    private int yte$getMaxScroll() {
        return Math.max(0, IGui.SQUARE_SIZE * (yte$getMaxVisibleRow() + 1) - yte$getContentBottom());
    }

    @Unique
    private void yte$clampScroll() {
        yte$scrollOffset = Math.max(0, Math.min(yte$scrollOffset, yte$getMaxScroll()));
    }

    @Unique
    private void yte$drawScrollbar(GraphicsHolder graphicsHolder) {
        final int maxScroll = yte$getMaxScroll();
        if (maxScroll <= 0) {
            return;
        }
        final int contentTop = IGui.SQUARE_SIZE;
        final int contentBottom = yte$getContentBottom();
        final int trackHeight = contentBottom - contentTop;
        final int thumbHeight = Math.max(16, trackHeight * trackHeight / Math.max(1, trackHeight + maxScroll));
        final int thumbY = contentTop + (int) Math.round((double) yte$scrollOffset / maxScroll * (trackHeight - thumbHeight));
        final GuiDrawing guiDrawing = new GuiDrawing(graphicsHolder);
        guiDrawing.beginDrawingRectangle();
        guiDrawing.drawRectangle(IGui.PANEL_WIDTH - 4, contentTop, IGui.PANEL_WIDTH, contentBottom, 0x33000000);
        guiDrawing.drawRectangle(IGui.PANEL_WIDTH - 4, thumbY, IGui.PANEL_WIDTH, thumbY + thumbHeight, 0x99AAAAAA);
        guiDrawing.finishDrawingRectangle();
    }

    @Override
    public boolean mouseScrolled2(double mouseX, double mouseY, double amount) {
        if (mouseX <= IGui.PANEL_WIDTH) {
            final int maxScroll = yte$getMaxScroll();
            if (maxScroll > 0) {
                yte$scrollOffset = Math.max(0, Math.min(maxScroll, yte$scrollOffset - (int) Math.round(amount * IGui.SQUARE_SIZE)));
                yte$layoutContent();
                return true;
            }
        }
        return super.mouseScrolled2(mouseX, mouseY, amount);
    }

    @Unique
    private void yte$drawTabLabels(GraphicsHolder graphicsHolder, double[] values) {
        switch (yte$activeTab) {
            case TAB_BASE:
                graphicsHolder.drawText(TextHelper.translatable("gui.yte.lift_number"), 0,
                        yte$contentY(1) + IGui.TEXT_PADDING, IGui.ARGB_WHITE, false, GraphicsHolder.getDefaultLight());
                graphicsHolder.drawCenteredText(TranslationProvider.TOOLTIP_MTR_RAIL_ACTION_HEIGHT.getMutableText(lift.getHeight()),
                        IGui.PANEL_WIDTH / 2, yte$contentY(4) + IGui.TEXT_PADDING, IGui.ARGB_WHITE);
                graphicsHolder.drawCenteredText(TranslationProvider.TOOLTIP_MTR_RAIL_ACTION_WIDTH.getMutableText(lift.getWidth()),
                        IGui.PANEL_WIDTH / 2, yte$contentY(5) + IGui.TEXT_PADDING, IGui.ARGB_WHITE);
                graphicsHolder.drawCenteredText(TranslationProvider.TOOLTIP_MTR_RAIL_ACTION_DEPTH.getMutableText(lift.getDepth()),
                        IGui.PANEL_WIDTH / 2, yte$contentY(6) + IGui.TEXT_PADDING, IGui.ARGB_WHITE);
                graphicsHolder.drawCenteredText(TranslationProvider.GUI_MTR_OFFSET_X.getMutableText(lift.getOffsetX()),
                        IGui.PANEL_WIDTH / 2, yte$contentY(7) + IGui.TEXT_PADDING, IGui.ARGB_WHITE);
                graphicsHolder.drawCenteredText(TranslationProvider.GUI_MTR_OFFSET_Y.getMutableText(lift.getOffsetY()),
                        IGui.PANEL_WIDTH / 2, yte$contentY(8) + IGui.TEXT_PADDING, IGui.ARGB_WHITE);
                graphicsHolder.drawCenteredText(TranslationProvider.GUI_MTR_OFFSET_Z.getMutableText(lift.getOffsetZ()),
                        IGui.PANEL_WIDTH / 2, yte$contentY(9) + IGui.TEXT_PADDING, IGui.ARGB_WHITE);
                break;
            case TAB_MOTION:
                if (yte$directionParametersLinked) {
                    yte$drawModeLabel(graphicsHolder, "gui.yte.lift_speed", "gui.yte.lift_speed_value", values[0], 3);
                    yte$drawModeLabel(graphicsHolder, "gui.yte.lift_acceleration", "gui.yte.lift_acceleration_value", values[2], 5);
                } else {
                    yte$drawModeLabel(graphicsHolder, "gui.yte.lift_up_speed", "gui.yte.lift_up_speed_value", values[0], 3);
                    yte$drawModeLabel(graphicsHolder, "gui.yte.lift_up_acceleration", "gui.yte.lift_up_acceleration_value", values[2], 5);
                    yte$drawModeLabel(graphicsHolder, "gui.yte.lift_down_speed", "gui.yte.lift_down_speed_value", values[1], 7);
                    yte$drawModeLabel(graphicsHolder, "gui.yte.lift_down_acceleration", "gui.yte.lift_down_acceleration_value", values[3], 9);
                }
                graphicsHolder.drawText(TextHelper.translatable("gui.yte.lift_fire_recall_floor"), 0,
                        yte$contentY(14) + IGui.TEXT_PADDING, IGui.ARGB_WHITE, false, GraphicsHolder.getDefaultLight());
                break;
            case TAB_LEVEL:
                yte$drawModeLabel(graphicsHolder, "gui.yte.lift_ado_distance", "gui.yte.lift_ado_distance_value", values[4], 1);
                yte$drawModeLabel(graphicsHolder, "gui.yte.lift_levelling_distance", "gui.yte.lift_levelling_distance_value", values[5], 3);
                yte$drawModeLabel(graphicsHolder, "gui.yte.lift_levelling_speed", "gui.yte.lift_levelling_speed_value", values[6], 5);
                break;
            case TAB_DOOR:
                yte$drawModeLabelSeconds(graphicsHolder, "gui.yte.lift_door_open_ms", "gui.yte.lift_door_open_s_value", (long) values[7], 5);
                yte$drawModeLabelSeconds(graphicsHolder, "gui.yte.lift_door_close_ms", "gui.yte.lift_door_close_s_value", (long) values[8], 7);
                yte$drawModeLabelSeconds(graphicsHolder, "gui.yte.lift_door_dwell_ms", "gui.yte.lift_door_dwell_s_value", (long) values[9], 9);
                yte$drawModeLabelSeconds(graphicsHolder, "gui.yte.lift_door_run_delay_ms", "gui.yte.lift_door_run_delay_s_value", (long) values[10], 11);
                yte$drawModeLabel(graphicsHolder, "gui.yte.lift_recovery_speed", "gui.yte.lift_recovery_speed_value", values[11], 13);
                yte$drawModeLabelSeconds(graphicsHolder, "gui.yte.lift_max_door_open_ms", "gui.yte.lift_max_door_open_s_value", (long) values[12], 15);
                break;
            default:
                break;
        }
    }

    @Unique
    private double[] yte$computeCurrentValues() {
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
                ? yte$parseNumber(yte$levellingSpeedField, yte$lastSentLevellingSpeed, YteLiftConfig.MIN_LEVELLING_SPEED, YteLiftConfig.MAX_LEVELLING_SPEED)
                : yte$getEasyModeValue(6, yte$sliderLevellingSpeed, valueToLevellingSpeed(yte$sliderLevellingSpeed.getIntValue()));
        final long doorOpenMs = yte$professionalMode
                ? yte$parseDoorMs(yte$doorOpenMsField, yte$lastSentDoorOpenMs, DOOR_ANIM_MIN, DOOR_OPEN_CLOSE_MAX, false)
                : valueToDoorMs(yte$sliderDoorOpenMs.getIntValue(), DOOR_ANIM_MIN);
        final long doorCloseMs = yte$professionalMode
                ? yte$parseDoorMs(yte$doorCloseMsField, yte$lastSentDoorCloseMs, DOOR_ANIM_MIN, DOOR_OPEN_CLOSE_MAX, false)
                : valueToDoorMs(yte$sliderDoorCloseMs.getIntValue(), DOOR_ANIM_MIN);
        final long doorDwellMs = yte$professionalMode
                ? yte$parseDoorMs(yte$doorDwellMsField, yte$lastSentDoorDwellMs, DOOR_MS_MIN, DOOR_DWELL_MAX, true)
                : valueToDoorMs(yte$sliderDoorDwellMs.getIntValue(), DOOR_MS_MIN);
        final long doorRunDelayMs = yte$professionalMode
                ? yte$parseDoorMs(yte$doorRunDelayMsField, yte$lastSentDoorRunDelayMs, DOOR_RUN_DELAY_MIN, DOOR_RUN_DELAY_MAX, false)
                : valueToDoorMs(yte$sliderDoorRunDelayMs.getIntValue(), DOOR_RUN_DELAY_MIN);
        final double recoverySpeed = yte$professionalMode
                ? yte$parseNumber(yte$recoverySpeedField, yte$lastSentRecoverySpeed,
                        YteLiftConfig.MIN_RECOVERY_SPEED, YteLiftConfig.MAX_RECOVERY_SPEED)
                : valueToRecoverySpeed(yte$sliderRecoverySpeed.getIntValue());
        final long maxDoorOpenMs = yte$professionalMode
                ? yte$parseDoorMs(yte$maxDoorOpenMsField, yte$lastSentMaxDoorOpenMs,
                        YteLiftConfig.MIN_MAX_DOOR_OPEN_MS, YteLiftConfig.MAX_MAX_DOOR_OPEN_MS, false)
                : valueToMaxDoorOpenMs(yte$sliderMaxDoorOpenMs.getIntValue());
        return new double[]{upSpeed, downSpeed, upAccel, downAccel, adoDistance, levellingDistance, levellingSpeed,
                doorOpenMs, doorCloseMs, doorDwellMs, doorRunDelayMs, recoverySpeed, maxDoorOpenMs};
    }

    @Unique
    private void yte$syncValuesToServer(double[] values) {
        final double upSpeed = values[0];
        final double downSpeed = values[1];
        final double upAccel = values[2];
        final double downAccel = values[3];
        final double adoDistance = values[4];
        final double levellingDistance = values[5];
        final double levellingSpeed = values[6];
        final long doorOpenMs = (long) values[7];
        final long doorCloseMs = (long) values[8];
        final long doorDwellMs = (long) values[9];
        final long doorRunDelayMs = (long) values[10];
        final double recoverySpeed = values[11];
        final long maxDoorOpenMs = (long) values[12];
        final String liftNumber = yte$liftNumberField.getText2().trim();
        final String fireRecallFloor = yte$fireRecallFloorField.getText2().trim();

        if (upSpeed != yte$lastSentSpeed || downSpeed != yte$lastSentDownSpeed
                || upAccel != yte$lastSentAccel || downAccel != yte$lastSentDownAccel
                || yte$directionParametersLinked != yte$lastSentDirectionParametersLinked
                || yte$motionProfile != yte$lastSentMotionProfile
                || adoDistance != yte$lastSentAdoDistance || levellingDistance != yte$lastSentLevellingDistance
                || levellingSpeed != yte$lastSentLevellingSpeed
                || yte$doorHoldEnabled != yte$lastSentDoorHoldEnabled
                || yte$doorButtonLightMode != yte$lastSentDoorButtonLightMode
                || yte$floorCancelMode != yte$lastSentFloorCancelMode
                || doorOpenMs != yte$lastSentDoorOpenMs || doorCloseMs != yte$lastSentDoorCloseMs
                || doorDwellMs != yte$lastSentDoorDwellMs || doorRunDelayMs != yte$lastSentDoorRunDelayMs
                || yte$doorCurve != yte$lastSentDoorCurve
                || recoverySpeed != yte$lastSentRecoverySpeed
                || maxDoorOpenMs != yte$lastSentMaxDoorOpenMs
                || yte$firemanLift != yte$lastSentFiremanLift
                || yte$firemanOperation != yte$lastSentFiremanOperation
                || !fireRecallFloor.equals(yte$lastSentFireRecallFloor)
                || yte$arrivalLanternTriggerMode != yte$lastSentArrivalLanternTriggerMode
                || !yte$serviceMode.equals(yte$lastSentServiceMode)
                || !liftNumber.equals(yte$lastSentLiftNumber)) {
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
            yte$lastSentArrivalLanternTriggerMode = yte$arrivalLanternTriggerMode;
            yte$lastSentServiceMode = yte$serviceMode;
            yte$liftNumber = liftNumber;
            yte$lastSentLiftNumber = liftNumber;
            yte$lastSentDoorOpenMs = doorOpenMs;
            yte$lastSentDoorCloseMs = doorCloseMs;
            yte$lastSentDoorDwellMs = doorDwellMs;
            yte$lastSentDoorRunDelayMs = doorRunDelayMs;
            yte$lastSentDoorCurve = yte$doorCurve;
            yte$lastSentRecoverySpeed = recoverySpeed;
            yte$lastSentMaxDoorOpenMs = maxDoorOpenMs;
            yte$lastSentFiremanLift = yte$firemanLift;
            yte$lastSentFiremanOperation = yte$firemanOperation;
            yte$fireRecallFloor = fireRecallFloor;
            yte$lastSentFireRecallFloor = fireRecallFloor;

            final long liftId = lift.getId();
            final YteLiftConfig config = new YteLiftConfig(liftId, upSpeed, downSpeed, upAccel, downAccel,
                    yte$directionParametersLinked, adoDistance, levellingDistance, levellingSpeed, yte$motionProfile,
                    yte$doorHoldEnabled, yte$doorButtonLightMode, yte$floorCancelMode, false,
                    yte$arrivalLanternTriggerMode, yte$serviceMode,
                    doorOpenMs, doorCloseMs, doorDwellMs, doorRunDelayMs, yte$doorCurve, liftNumber,
                    yte$firemanLift, yte$firemanOperation.name(), fireRecallFloor);
            YteLiftConfigStore.put(liftId, upSpeed, downSpeed, upAccel, downAccel,
                    adoDistance, levellingDistance, levellingSpeed, yte$motionProfile, yte$doorHoldEnabled,
                    yte$doorButtonLightMode, yte$floorCancelMode, false, yte$arrivalLanternTriggerMode,
                    yte$firemanLift, yte$firemanOperation, fireRecallFloor);
            YteLiftConfigStore.putDoorParams(liftId, doorOpenMs, doorCloseMs, doorDwellMs, doorRunDelayMs, yte$doorCurve);
            YteLiftConfigStore.putRecoverySpeed(liftId, recoverySpeed);
            YteLiftConfigStore.putMaxDoorOpenMs(liftId, maxDoorOpenMs);
            YteLiftConfigStore.setLiftNumber(liftId, liftNumber);

            final YteUpdateDataRequest request = new YteUpdateDataRequest(
                    config, YteMinecraftClientData.getInstance());
            InitClient.REGISTRY_CLIENT.sendPacketToServer(
                    new YtePacketUpdateData(request));
        }
    }

    @Unique
    private void yte$positionMinusPlus(ButtonWidgetExtension minus, ButtonWidgetExtension plus, int row) {
        final int y = yte$contentY(row);
        minus.setX2(0);
        minus.setY2(y);
        minus.setWidth2(IGui.SQUARE_SIZE);
        plus.setX2(IGui.PANEL_WIDTH - IGui.SQUARE_SIZE);
        plus.setY2(y);
        plus.setWidth2(IGui.SQUARE_SIZE);
    }

    @Unique
    private void yte$positionFullWidth(ButtonWidgetExtension button, int row) {
        button.setX2(0);
        button.setY2(yte$contentY(row));
        button.setWidth2(IGui.PANEL_WIDTH);
    }

    @Unique
    private void yte$positionFullWidth(CheckboxWidgetExtension checkbox, int row) {
        checkbox.setX2(0);
        checkbox.setY2(yte$contentY(row));
        checkbox.setWidth2(IGui.PANEL_WIDTH);
    }

    @Unique
    private static TextFieldWidgetExtension yte$createNumberField(double value) {
        final TextFieldWidgetExtension field = new TextFieldWidgetExtension(0, 0, 0, IGui.SQUARE_SIZE, 12, TextCase.DEFAULT, null, "0");
        field.setWidth2(IGui.PANEL_WIDTH - IGui.TEXT_FIELD_PADDING);
        field.setText2(Double.toString(value));
        return field;
    }

    @Unique
    private static TextFieldWidgetExtension yte$createLiftNumberField() {
        final TextFieldWidgetExtension field = new TextFieldWidgetExtension(0, 0, 0, IGui.SQUARE_SIZE, 4, TextCase.DEFAULT, null, "");
        field.setWidth2(IGui.PANEL_WIDTH - IGui.TEXT_FIELD_PADDING);
        return field;
    }

    @Unique
    private void yte$positionField(TextFieldWidgetExtension field, int row) {
        field.setX2(IGui.TEXT_FIELD_PADDING / 2);
        field.setY2(yte$contentY(row));
        field.setWidth2(IGui.PANEL_WIDTH - IGui.TEXT_FIELD_PADDING);
    }

    @Unique
    private void yte$positionSlider(WidgetShorterSlider slider, int row) {
        slider.setX2(0);
        slider.setY2(yte$contentY(row));
        slider.setHeight(IGui.SQUARE_SIZE);
        slider.setWidth2(IGui.PANEL_WIDTH);
    }

    @Unique
    private void yte$toggleFiremanLift() {
        yte$firemanLift = !yte$firemanLift;
        yte$updateModeWidgets();
    }

    @Unique
    private void yte$toggleFiremanOperation() {
        yte$firemanOperation = yte$firemanOperation.next();
        yte$updateModeWidgets();
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
        yte$layoutContent();
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
    private void yte$toggleArrivalLanternTriggerMode() {
        yte$arrivalLanternTriggerMode = yte$arrivalLanternTriggerMode.next();
        yte$updateModeWidgets();
    }

    @Unique
    private void yte$toggleServiceMode() {
        final int index = Math.max(java.util.Arrays.asList(SERVICE_MODE_CYCLE).indexOf(yte$serviceMode), 0);
        yte$serviceMode = SERVICE_MODE_CYCLE[(index + 1) % SERVICE_MODE_CYCLE.length];
        yte$updateModeWidgets();
    }

    @Unique
    private void yte$toggleDoorCurve() {
        yte$doorCurve = yte$doorCurve.next();
        yte$doorCurveButton.setMessage2(new Text(TextHelper.translatable(
                yte$doorCurve.getTranslationKey()).data));
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
        final boolean sizeTab = yte$activeTab == TAB_BASE;
        final boolean motionTab = yte$activeTab == TAB_MOTION;
        final boolean levelTab = yte$activeTab == TAB_LEVEL;
        final boolean doorTab = yte$activeTab == TAB_DOOR;

        // 原版控件：尺寸与外观标签
        buttonHeightMinus.setVisibleMapped(sizeTab);
        buttonHeightAdd.setVisibleMapped(sizeTab);
        buttonWidthMinus.setVisibleMapped(sizeTab);
        buttonWidthAdd.setVisibleMapped(sizeTab);
        buttonDepthMinus.setVisibleMapped(sizeTab);
        buttonDepthAdd.setVisibleMapped(sizeTab);
        buttonOffsetXMinus.setVisibleMapped(sizeTab);
        buttonOffsetXAdd.setVisibleMapped(sizeTab);
        buttonOffsetYMinus.setVisibleMapped(sizeTab);
        buttonOffsetYAdd.setVisibleMapped(sizeTab);
        buttonOffsetZMinus.setVisibleMapped(sizeTab);
        buttonOffsetZAdd.setVisibleMapped(sizeTab);
        buttonIsDoubleSided.setVisibleMapped(sizeTab);
        buttonLiftStyle.setVisibleMapped(sizeTab);
        buttonRotateAnticlockwise.setVisibleMapped(sizeTab);
        buttonRotateClockwise.setVisibleMapped(sizeTab);
        yte$liftNumberField.setVisibleMapped(sizeTab);

        // 专业模式为全局 footer（运行/平层标签下显示），其余模式开关仅属于运行标签
        yte$professionalModeButton.setVisibleMapped(motionTab || levelTab);
        yte$directionLinkButton.setVisibleMapped(motionTab);
        yte$motionProfileButton.setVisibleMapped(motionTab);
        yte$firemanLiftButton.setVisibleMapped(motionTab);
        yte$firemanOperationButton.setVisibleMapped(motionTab);
        yte$fireRecallFloorField.setVisibleMapped(motionTab);

        // 门与楼层标签
        yte$doorHoldButton.setVisibleMapped(doorTab);
        yte$doorButtonLightModeButton.setVisibleMapped(doorTab);
        yte$floorCancelModeButton.setVisibleMapped(doorTab);
        yte$doorCurveButton.setVisibleMapped(doorTab);

        // 门参数滑条/文本框
        yte$sliderDoorOpenMs.setVisibleMapped(doorTab && !yte$professionalMode);
        yte$doorOpenMsField.setVisibleMapped(doorTab && yte$professionalMode);
        yte$sliderDoorCloseMs.setVisibleMapped(doorTab && !yte$professionalMode);
        yte$doorCloseMsField.setVisibleMapped(doorTab && yte$professionalMode);
        yte$sliderDoorDwellMs.setVisibleMapped(doorTab && !yte$professionalMode);
        yte$doorDwellMsField.setVisibleMapped(doorTab && yte$professionalMode);
        yte$sliderDoorRunDelayMs.setVisibleMapped(doorTab && !yte$professionalMode);
        yte$doorRunDelayMsField.setVisibleMapped(doorTab && yte$professionalMode);
        yte$sliderRecoverySpeed.setVisibleMapped(doorTab && !yte$professionalMode);
        yte$recoverySpeedField.setVisibleMapped(doorTab && yte$professionalMode);
        yte$sliderMaxDoorOpenMs.setVisibleMapped(doorTab && !yte$professionalMode);
        yte$maxDoorOpenMsField.setVisibleMapped(doorTab && yte$professionalMode);
        yte$arrivalLanternTriggerModeButton.setVisibleMapped(doorTab);
        yte$serviceModeButton.setVisibleMapped(doorTab);

        // 运行参数：滑块/文本框
        yte$sliderSpeed.setVisibleMapped(motionTab && !yte$professionalMode);
        yte$accelerationField.setVisibleMapped(motionTab && yte$professionalMode);
        yte$speedField.setVisibleMapped(motionTab && yte$professionalMode);
        yte$sliderAcceleration.setVisibleMapped(motionTab && !yte$professionalMode);
        yte$sliderDownSpeed.setVisibleMapped(motionTab && !yte$professionalMode && !yte$directionParametersLinked);
        yte$downSpeedField.setVisibleMapped(motionTab && yte$professionalMode && !yte$directionParametersLinked);
        yte$sliderDownAcceleration.setVisibleMapped(motionTab && !yte$professionalMode && !yte$directionParametersLinked);
        yte$downAccelerationField.setVisibleMapped(motionTab && yte$professionalMode && !yte$directionParametersLinked);

        // 平层参数：滑块/文本框
        yte$sliderAdoDistance.setVisibleMapped(levelTab && !yte$professionalMode);
        yte$adoDistanceField.setVisibleMapped(levelTab && yte$professionalMode);
        yte$sliderLevellingDistance.setVisibleMapped(levelTab && !yte$professionalMode);
        yte$levellingDistanceField.setVisibleMapped(levelTab && yte$professionalMode);
        yte$sliderLevellingSpeed.setVisibleMapped(levelTab && !yte$professionalMode);
        yte$levellingSpeedField.setVisibleMapped(levelTab && yte$professionalMode);

        // 按钮文本状态
        yte$professionalModeButton.setMessage2(new Text(TextHelper.translatable(yte$professionalMode
                ? "gui.yte.lift_professional_mode_on"
                : "gui.yte.lift_professional_mode_off").data));
        yte$directionLinkButton.setMessage2(new Text(TextHelper.translatable(yte$directionParametersLinked
                ? "gui.yte.lift_direction_link_on"
                : "gui.yte.lift_direction_link_off").data));
        yte$firemanLiftButton.setMessage2(new Text(TextHelper.translatable(yte$firemanLift
                ? "gui.yte.lift_fireman_lift_on"
                : "gui.yte.lift_fireman_lift_off").data));
        yte$firemanOperationButton.setMessage2(new Text(TextHelper.translatable(
                yte$firemanOperation.getTranslationKey()).data));
        yte$motionProfileButton.setMessage2(new Text(TextHelper.translatable(
                yte$motionProfile.getTranslationKey()).data));
        yte$doorHoldButton.setMessage2(new Text(TextHelper.translatable(yte$doorHoldEnabled
                ? "gui.yte.lift_door_hold_on"
                : "gui.yte.lift_door_hold_off").data));
        yte$doorButtonLightModeButton.setMessage2(new Text(TextHelper.translatable(
                yte$doorButtonLightMode.getTranslationKey()).data));
        yte$floorCancelModeButton.setMessage2(new Text(TextHelper.translatable(
                yte$floorCancelMode.getTranslationKey()).data));
        yte$arrivalLanternTriggerModeButton.setMessage2(new Text(TextHelper.translatable(
                yte$arrivalLanternTriggerMode.getTranslationKey()).data));
        yte$serviceModeButton.setMessage2(new Text(TextHelper.translatable(
                "gui.yte.lift_service_mode_" + yte$serviceMode.toLowerCase(Locale.ROOT)).data));
    }

    @Unique
    private void yte$drawModeLabelSeconds(GraphicsHolder graphicsHolder, String inputKey, String valueKey, long millis, int row) {
        graphicsHolder.drawText(TextHelper.translatable(yte$professionalMode ? inputKey : valueKey,
                        String.format(Locale.ROOT, "%.1f", millis / 1000.0)), 0,
                yte$contentY(row) + IGui.TEXT_PADDING,
                IGui.ARGB_WHITE, false, GraphicsHolder.getDefaultLight());
    }

    @Unique
    private void yte$drawModeLabel(GraphicsHolder graphicsHolder, String inputKey, String valueKey, double value, int row) {
        graphicsHolder.drawText(TextHelper.translatable(yte$professionalMode ? inputKey : valueKey,
                        String.format(Locale.ROOT, "%.2f", value)), 0,
                yte$contentY(row) + IGui.TEXT_PADDING,
                IGui.ARGB_WHITE, false, GraphicsHolder.getDefaultLight());
    }

    @Unique
    private static long valueToDoorMs(int sliderValue, long minimum) {
        return minimum + sliderValue * DOOR_MS_STEP;
    }

    private static int doorMsToValue(long ms, long minimum, long maximum) {
        return (int) Math.max(0, Math.min((maximum - minimum) / DOOR_MS_STEP, (ms - minimum) / DOOR_MS_STEP));
    }

    /** 解析门参数毫秒数；allowMinus 供保持时长 -1（无限开门）使用。 */
    private static long yte$parseDoorMs(TextFieldWidgetExtension field, long fallback,
                                        long minimum, long maximum, boolean allowMinus) {
        try {
            final long value = Long.parseLong(field.getText2().trim());
            if (allowMinus && value == -1) {
                return -1;
            }
            return Math.max(minimum, Math.min(maximum, value));
        } catch (Exception e) {
            return fallback;
        }
    }

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

    /** 救援速度滑块：首档 = 最小值 0.1 m/s，其后线性步进 0.05。 */
    @Unique
    private static double valueToRecoverySpeed(int sliderValue) {
        final double min = YteLiftConfig.MIN_RECOVERY_SPEED;
        return sliderValue == 0 ? min : Math.min(min + sliderValue * RECOVERY_SPEED_SLIDER_STEP,
                YteLiftConfig.MAX_RECOVERY_SPEED);
    }

    @Unique
    private static int recoverySpeedToValue(double speed) {
        return (int) Math.max(0, Math.min(RECOVERY_SPEED_SLIDER_MAX,
                Math.round((speed - YteLiftConfig.MIN_RECOVERY_SPEED) / RECOVERY_SPEED_SLIDER_STEP)));
    }

    @Unique
    private static long valueToMaxDoorOpenMs(int sliderValue) {
        return YteLiftConfig.MIN_MAX_DOOR_OPEN_MS + sliderValue * MAX_DOOR_OPEN_SLIDER_STEP;
    }

    @Unique
    private static int maxDoorOpenMsToValue(long ms) {
        return (int) Math.max(0, Math.min(MAX_DOOR_OPEN_SLIDER_MAX,
                (ms - YteLiftConfig.MIN_MAX_DOOR_OPEN_MS) / MAX_DOOR_OPEN_SLIDER_STEP));
    }

    @Unique
    private static int yte$floorToSlider(double value, double step, int maximum) {
        return Math.max(0, Math.min(maximum, (int) Math.floor(value / step + 1E-9)));
    }
}
