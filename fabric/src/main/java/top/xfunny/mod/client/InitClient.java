package top.xfunny.mod.client;


import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.holder.RenderLayer;
import org.mtr.mapping.registry.RegistryClient;
import org.mtr.mod.client.MinecraftClientData;
import top.xfunny.mod.BlockEntityTypes;
import top.xfunny.mod.Blocks;
import top.xfunny.mod.Init;
import top.xfunny.mod.Items;
import top.xfunny.mod.lift.LiftArrivalLanternState;
import top.xfunny.mod.lift.LiftDisplayDirectionState;
import top.xfunny.mod.lift.LiftDisplayState;
import top.xfunny.mod.lift.LiftDoorControlState;
import top.xfunny.mod.lift.policy.MitsubishiNexWayLanternPolicy;
import top.xfunny.mod.client.render.*;
import top.xfunny.mod.client.resource.FontList;
import top.xfunny.mod.client.sound.SoundPlaybackManager;
import top.xfunny.mod.config.ClientConfig;
import top.xfunny.mod.config.YteLiftConfigStore;
import top.xfunny.mod.item.YTEItemBlockClickingBase;
import top.xfunny.mod.packet.YtePacketRequestData;


public final class InitClient {
    private static final ClientConfig config = new ClientConfig();
    public static RegistryClient REGISTRY_CLIENT = new RegistryClient(Init.REGISTRY);
    private static long lastMillis = 0;
    private static long gameMillis = 0;

    public static void init() {
        // 重置 YTE 客户端数据
        YteMinecraftClientData.reset();
        YteLiftConfigStore.clear();
        LiftArrivalLanternState.clear();
        LiftDisplayState.clear();
        LiftDisplayDirectionState.clear();
        MitsubishiNexWayLanternPolicy.INSTANCE.clear();
        LiftDoorControlState.clearClientState();

        initializeConfig();

        REGISTRY_CLIENT.registerBlockRenderType(RenderLayer.getCutout(), Blocks.SCHINDLER_QKS9_DOOR_1);
        REGISTRY_CLIENT.registerBlockRenderType(RenderLayer.getCutout(), Blocks.MITSUBISHI_NEXWAY_DOOR_1);


        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.TEST_LIFT_BUTTONS, RenderTestLiftButtons4::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.TEST_LIFT_HALL_LANTERNS, dispatcher -> new top.xfunny.mod.client.render.RenderTestLiftHallLanterns(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.TEST_LIFT_BUTTONS_WITHOUT_SCREEN, RenderTestLiftButtonsWithoutScreen::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.TEST_LIFT_PANEL, dispatcher -> new top.xfunny.mod.client.render.RenderTestLiftPanel(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.TEST_LIFT_DESTINATION_DISPATCH_TERMINAL, dispatcher -> new top.xfunny.mod.client.render.RenderTestLiftDestinationDispatchTerminal(dispatcher, true));

        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.CES_SCREEN_1_ODD, dispatcher -> new top.xfunny.mod.client.render.RenderCESScreen1<>(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.CES_SCREEN_1_EVEN, dispatcher -> new top.xfunny.mod.client.render.RenderCESScreen1<>(dispatcher, false));

        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.DEWHURST_US81_BUTTON_1, RenderDewhurstUS81Button1::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.DEWHURST_US89_BUTTON_1, RenderDewhurstUS89Button1::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.DEWHURST_US91_BUTTON_1, RenderDewhurstUS91Button1::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.DEWHURST_US91_BUTTON_1_BRAILLE, RenderDewhurstUS91Button1Braille::new);

        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.DOVER_IMPULSE_BUTTON_1, RenderDoverImpulseButton1::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.DOVER_IMPULSE_LANTERN_1_HORIZONTAL_ODD, dispatcher -> new top.xfunny.mod.client.render.RenderDoverImpulseLantern1Horizontal<>(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.DOVER_IMPULSE_LANTERN_1_HORIZONTAL_EVEN, dispatcher -> new top.xfunny.mod.client.render.RenderDoverImpulseLantern1Horizontal<>(dispatcher, false));

        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.FUJITEC_MIC400_BUTTON_1, RenderFujitecMIC400Button1::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.FUJITEC_MIC400_BUTTON_1_WITHOUT_SCREEN, RenderFujitecMIC400Button1WithoutScreen::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.FUJITEC_MIC400_BUTTON_1_OLD, RenderFujitecMIC400Button1Old::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.FUJITEC_MIC400_BUTTON_1_OLD_WITHOUT_SCREEN, RenderFujitecMIC400Button1OldWithoutScreen::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.FUJITEC_MIC400_SCREEN_1_HORIZONTAL_ODD, dispatcher -> new RenderFujitecMIC400Screen1Horizontal<>(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.FUJITEC_MIC400_SCREEN_1_HORIZONTAL_EVEN, dispatcher -> new RenderFujitecMIC400Screen1Horizontal<>(dispatcher, false));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.FUJITEC_MIC400_SCREEN_1_VERTICAL_ODD, dispatcher -> new RenderFujitecMIC400Screen1Vertical<>(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.FUJITEC_MIC400_SCREEN_1_VERTICAL_EVEN, dispatcher -> new RenderFujitecMIC400Screen1Vertical<>(dispatcher, false));

        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.KONE_KDS220_BUTTON_1, RenderKoneKDS220Button1::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.KONE_KDS220_BUTTON_1_WITHOUT_SCREEN, RenderKoneKDS220Button1WithoutScreen::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.KONE_KDS220_SCREEN_1_ODD, dispatcher -> new top.xfunny.mod.client.render.RenderKoneKDS220Screen1<>(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.KONE_KDS220_SCREEN_1_EVEN, dispatcher -> new top.xfunny.mod.client.render.RenderKoneKDS220Screen1<>(dispatcher, false));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.KONE_KDS330_BUTTON_1, RenderKoneKDS330Button1::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.KONE_KDS330_BUTTON_1_WITHOUT_SCREEN, RenderKoneKDS330Button1WithoutScreen::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.KONE_KDS330_BUTTON_1_TOUCH, RenderKoneKDS330Button1Touch::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.KONE_KDS330_BUTTON_1_TOUCH_WITHOUT_SCREEN, RenderKoneKDS330Button1TouchWithoutScreen::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.KONE_KDS330_LANTERN_1, RenderKoneKDS330Lantern1::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.KONE_KDS330_SCREEN_1_ODD, dispatcher -> new top.xfunny.mod.client.render.RenderKoneKDS330Screen1<>(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.KONE_KDS330_SCREEN_1_EVEN, dispatcher -> new top.xfunny.mod.client.render.RenderKoneKDS330Screen1<>(dispatcher, false));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.KONE_KDS360_BUTTON_1, RenderKoneKDS360Button1::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.KONE_KDS360_BUTTON_1_DISPLAY_TOP, RenderKoneKDS360Button1DisplayTop::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.KONE_KDS360_BUTTON_1_WITHOUT_SCREEN, RenderKoneKDS360Button1WithoutScreen::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.KONE_KSS280_BUTTON_1, RenderKoneKSS280Button1::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.KONE_KSS280_BUTTON_1_WITHOUT_SCREEN, RenderKoneKSS280Button1WithoutScreen::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.KONE_KSS280_SCREEN_1_ODD, dispatcher -> new top.xfunny.mod.client.render.RenderKoneKSS280Screen1<>(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.KONE_KSS280_SCREEN_1_EVEN, dispatcher -> new top.xfunny.mod.client.render.RenderKoneKSS280Screen1<>(dispatcher, false));

        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.KONE_M_BUTTON_1, RenderKoneMButton1::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.KONE_M_BUTTON_2, RenderKoneMButton2::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.KONE_M_SCREEN_1_ODD, dispatcher -> new top.xfunny.mod.client.render.RenderKoneMScreen1<>(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.KONE_M_SCREEN_1_EVEN, dispatcher -> new top.xfunny.mod.client.render.RenderKoneMScreen1<>(dispatcher, false));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.KONE_M_SCREEN_1_SMALL_ODD, dispatcher -> new top.xfunny.mod.client.render.RenderKoneMScreen1Small<>(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.KONE_M_SCREEN_1_SMALL_EVEN, dispatcher -> new top.xfunny.mod.client.render.RenderKoneMScreen1Small<>(dispatcher, false));

        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.MITSUBISHI_GPS_BUTTON_1, RenderMitsubishiGPSButton1::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.MITSUBISHI_GPS_BUTTON_1_WITHOUT_SCREEN, RenderMitsubishiGPSButton1WithoutScreen::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.MITSUBISHI_GPS3_BUTTON_1, RenderMitsubishiGPS3Button1::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.MITSUBISHI_GPS3_BUTTON_1_WITHOUT_SCREEN, RenderMitsubishiGPS3Button1WithoutScreen::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.MITSUBISHI_GPS3_BUTTON_2, RenderMitsubishiGPS3Button2::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.MITSUBISHI_GPS3_BUTTON_2_WITHOUT_SCREEN, RenderMitsubishiGPS3Button2WithoutScreen::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.MITSUBISHI_BUTTON_SHUN_HING_SQUARE, RenderMitsubishiButtonShunHingSquare::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.MITSUBISHI_RYODEN_SCREEN_1_EVEN, dispatcher -> new top.xfunny.mod.client.render.RenderMitsubishiRyodenScreen1<>(dispatcher, false));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.MITSUBISHI_RYODEN_SCREEN_1_ODD, dispatcher -> new top.xfunny.mod.client.render.RenderMitsubishiRyodenScreen1<>(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.MITSUBISHI_RYODEN_SCREEN_2_EVEN, dispatcher -> new top.xfunny.mod.client.render.RenderMitsubishiRyodenScreen2<>(dispatcher, false));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.MITSUBISHI_RYODEN_SCREEN_2_ODD, dispatcher -> new top.xfunny.mod.client.render.RenderMitsubishiRyodenScreen2<>(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.MITSUBISHI_GPS3_SCREEN_1_EVEN, dispatcher -> new top.xfunny.mod.client.render.RenderMitsubishiGPS3Screen1<>(dispatcher, false));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.MITSUBISHI_GPS3_SCREEN_1_ODD, dispatcher -> new top.xfunny.mod.client.render.RenderMitsubishiGPS3Screen1<>(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.MITSUBISHI_NEXWAY_BUTTON_1, RenderMitsubishiNexWayButton1::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.MITSUBISHI_NEXWAY_BUTTON_1_SEGMENTED, RenderMitsubishiNexWayButton1Segmented::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.MITSUBISHI_NEXWAY_BUTTON_1_WITHOUT_SCREEN, RenderMitsubishiNexWayButton1WithoutScreen::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.MITSUBISHI_NEXWAY_BUTTON_2, RenderMitsubishiNexWayButton2::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.MITSUBISHI_NEXWAY_BUTTON_2_SEGMENTED, RenderMitsubishiNexWayButton2Segmented::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.MITSUBISHI_NEXWAY_BUTTON_2_LCD_1, RenderMitsubishiNexWayButton2LCD1::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.MITSUBISHI_NEXWAY_BUTTON_2_WITHOUT_SCREEN, RenderMitsubishiNexWayButton2WithoutScreen::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.MITSUBISHI_NEXWAY_BUTTON_3, RenderMitsubishiNexWayButton3::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.MITSUBISHI_NEXWAY_BUTTON_3_SEGMENTED, RenderMitsubishiNexWayButton3Segmented::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.MITSUBISHI_NEXWAY_BUTTON_3_WITHOUT_SCREEN, RenderMitsubishiNexWayButton3WithoutScreen::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.MITSUBISHI_NEXWAY_BUTTON_4, RenderMitsubishiNexWayButton4::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.MITSUBISHI_NEXWAY_BUTTON_4_WITHOUT_SCREEN, RenderMitsubishiNexWayButton4WithoutScreen::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.MITSUBISHI_NEXWAY_BUTTON_5, RenderMitsubishiNexWayButton5::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.MITSUBISHI_NEXWAY_BUTTON_5_WITHOUT_SCREEN, RenderMitsubishiNexWayButton5WithoutScreen::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.MITSUBISHI_BUTTON_SHT, RenderMitsubishiButtonSHT::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.MITSUBISHI_MAXIEZ_BUTTON_1_GOLD, RenderMitsubishiMaxiezButton1Gold::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.MITSUBISHI_MAXIEZ_BUTTON_1_SILVER, RenderMitsubishiMaxiezButton1Silver::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.MITSUBISHI_MAXIEZ_BUTTON_2, RenderMitsubishiMaxiezButton2::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.MITSUBISHI_MAXIEZ_BUTTON_2_LCD, RenderMitsubishiMaxiezButton2LCD::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.MITSUBISHI_MAXIEZ_BUTTON_2_WITHOUT_SCREEN, RenderMitsubishiMaxiezButton2WithoutScreen::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.MITSUBISHI_MAXIEZ_BUTTON_3, RenderMitsubishiMaxiezButton3::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.MITSUBISHI_MAXIEZ_BUTTON_3_LCD, RenderMitsubishiMaxiezButton3LCD::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.MITSUBISHI_MAXIEZ_BUTTON_3_WITHOUT_SCREEN, RenderMitsubishiMaxiezButton3WithoutScreen::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.MITSUBISHI_MPVF_BUTTON_1, RenderMitsubishiMPVFButton1::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.MITSUBISHI_MPVF_SCREEN_1_VERTICAL_ODD, dispatcher -> new top.xfunny.mod.client.render.RenderMitsubishiMPVFScreen1Vertical<>(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.MITSUBISHI_MPVF_SCREEN_1_VERTICAL_EVEN, dispatcher -> new top.xfunny.mod.client.render.RenderMitsubishiMPVFScreen1Vertical<>(dispatcher, false));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.MITSUBISHI_MPVF_SCREEN_1_HORIZONTAL_ODD, dispatcher -> new top.xfunny.mod.client.render.RenderMitsubishiMPVFScreen1Horizontal<>(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.MITSUBISHI_MPVF_SCREEN_1_HORIZONTAL_EVEN, dispatcher -> new top.xfunny.mod.client.render.RenderMitsubishiMPVFScreen1Horizontal<>(dispatcher, false));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.MITSUBISHI_MPVF_LANTERN_1_VERTICAL_ODD, dispatcher -> new top.xfunny.mod.client.render.RenderMitsubishiMPVFLantern1Vertical<>(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.MITSUBISHI_MPVF_LANTERN_1_VERTICAL_EVEN, dispatcher -> new top.xfunny.mod.client.render.RenderMitsubishiMPVFLantern1Vertical<>(dispatcher, false));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.MITSUBISHI_MPVF_LANTERN_1_HORIZONTAL_ODD, dispatcher -> new top.xfunny.mod.client.render.RenderMitsubishiMPVFLantern1Horizontal<>(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.MITSUBISHI_MPVF_LANTERN_1_HORIZONTAL_EVEN, dispatcher -> new top.xfunny.mod.client.render.RenderMitsubishiMPVFLantern1Horizontal<>(dispatcher, false));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.MITSUBISHI_MP_BUTTON_1, RenderMitsubishiMPButton1::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.MITSUBISHI_MP_BUTTON_1_TOUCH, RenderMitsubishiMPButton1Touch::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.MITSUBISHI_NEXWAY_LANTERN_1_HORIZONTAL_EVEN, dispatcher -> new top.xfunny.mod.client.render.RenderMitsubishiNexWayLantern1Horizontal<>(dispatcher, false));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.MITSUBISHI_NEXWAY_LANTERN_1_HORIZONTAL_ODD, dispatcher -> new top.xfunny.mod.client.render.RenderMitsubishiNexWayLantern1Horizontal<>(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.MITSUBISHI_NEXWAY_LANTERN_1_VERTICAL_EVEN, dispatcher -> new top.xfunny.mod.client.render.RenderMitsubishiNexWayLantern1Vertical<>(dispatcher, false));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.MITSUBISHI_NEXWAY_LANTERN_1_VERTICAL_ODD, dispatcher -> new top.xfunny.mod.client.render.RenderMitsubishiNexWayLantern1Vertical<>(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.MITSUBISHI_NEXWAY_LANTERN_2_HORIZONTAL_EVEN, dispatcher -> new top.xfunny.mod.client.render.RenderMitsubishiNexWayLantern2Horizontal<>(dispatcher, false));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.MITSUBISHI_NEXWAY_LANTERN_2_HORIZONTAL_ODD, dispatcher -> new top.xfunny.mod.client.render.RenderMitsubishiNexWayLantern2Horizontal<>(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.MITSUBISHI_NEXWAY_LANTERN_2_VERTICAL_EVEN, dispatcher -> new top.xfunny.mod.client.render.RenderMitsubishiNexWayLantern2Vertical<>(dispatcher, false));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.MITSUBISHI_NEXWAY_LANTERN_2_VERTICAL_ODD, dispatcher -> new top.xfunny.mod.client.render.RenderMitsubishiNexWayLantern2Vertical<>(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.MITSUBISHI_NEXWAY_LANTERN_3_EVEN, dispatcher -> new top.xfunny.mod.client.render.RenderMitsubishiNexWayLantern3<>(dispatcher, false));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.MITSUBISHI_NEXWAY_LANTERN_3_ODD, dispatcher -> new top.xfunny.mod.client.render.RenderMitsubishiNexWayLantern3<>(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.MITSUBISHI_NEXWAY_SCREEN_1_SEGMENTED_EVEN, dispatcher -> new top.xfunny.mod.client.render.RenderMitsubishiNexWayScreen1Segmented<>(dispatcher, false));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.MITSUBISHI_NEXWAY_SCREEN_1_SEGMENTED_ODD, dispatcher -> new top.xfunny.mod.client.render.RenderMitsubishiNexWayScreen1Segmented<>(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.MITSUBISHI_NEXWAY_SCREEN_1_EVEN, dispatcher -> new top.xfunny.mod.client.render.RenderMitsubishiNexWayScreen1<>(dispatcher, false));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.MITSUBISHI_NEXWAY_SCREEN_1_ODD, dispatcher -> new top.xfunny.mod.client.render.RenderMitsubishiNexWayScreen1<>(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.MITSUBISHI_NEXWAY_SCREEN_2_EVEN, dispatcher -> new top.xfunny.mod.client.render.RenderMitsubishiNexWayScreen2<>(dispatcher, false));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.MITSUBISHI_NEXWAY_SCREEN_2_ODD, dispatcher -> new top.xfunny.mod.client.render.RenderMitsubishiNexWayScreen2<>(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.MITSUBISHI_NEXWAY_SCREEN_3_EVEN, dispatcher -> new top.xfunny.mod.client.render.RenderMitsubishiNexWayScreen3<>(dispatcher, false));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.MITSUBISHI_NEXWAY_SCREEN_3_ODD, dispatcher -> new top.xfunny.mod.client.render.RenderMitsubishiNexWayScreen3<>(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.MITSUBISHI_NEXWAY_SCREEN_3_SEGMENTED_EVEN, dispatcher -> new top.xfunny.mod.client.render.RenderMitsubishiNexWayScreen3Segmented<>(dispatcher, false));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.MITSUBISHI_NEXWAY_SCREEN_3_SEGMENTED_ODD, dispatcher -> new top.xfunny.mod.client.render.RenderMitsubishiNexWayScreen3Segmented<>(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.MITSUBISHI_NEXWAY_SCREEN_3_WIDE_EVEN, dispatcher -> new top.xfunny.mod.client.render.RenderMitsubishiNexWayScreen3Wide<>(dispatcher, false));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.MITSUBISHI_NEXWAY_SCREEN_3_WIDE_ODD, dispatcher -> new top.xfunny.mod.client.render.RenderMitsubishiNexWayScreen3Wide<>(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.MITSUBISHI_NEXWAY_SCREEN_3_WIDE_SEGMENTED_EVEN, dispatcher -> new top.xfunny.mod.client.render.RenderMitsubishiNexWayScreen3WideSegmented<>(dispatcher, false));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.MITSUBISHI_NEXWAY_SCREEN_3_WIDE_SEGMENTED_ODD, dispatcher -> new top.xfunny.mod.client.render.RenderMitsubishiNexWayScreen3WideSegmented<>(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.MITSUBISHI_MAXIEZ_SCREEN_1_EVEN, dispatcher -> new top.xfunny.mod.client.render.RenderMitsubishiMaxiezScreen1<>(dispatcher, false));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.MITSUBISHI_MAXIEZ_SCREEN_1_ODD, dispatcher -> new top.xfunny.mod.client.render.RenderMitsubishiMaxiezScreen1<>(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.MITSUBISHI_MAXIEZ_SCREEN_2_EVEN, dispatcher -> new top.xfunny.mod.client.render.RenderMitsubishiMaxiezScreen2<>(dispatcher, false));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.MITSUBISHI_MAXIEZ_SCREEN_2_ODD, dispatcher -> new top.xfunny.mod.client.render.RenderMitsubishiMaxiezScreen2<>(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.SHANGHAI_MITSUBISHI_NEXWAY_CR_BUTTON_1, RenderShanghaiMitsubishiNexWayCRButton1::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.SHANGHAI_MITSUBISHI_LEHY_3_BUTTON_1, RenderShanghaiMitsubishiLehy3Button1::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.SHANGHAI_MITSUBISHI_LEHY_3_BUTTON_1_WITHOUT_SCREEN, RenderShanghaiMitsubishiLehy3Button1WithoutScreen::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.SHANGHAI_MITSUBISHI_LEHY_3_BUTTON_2, RenderShanghaiMitsubishiLehy3Button2::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.SHANGHAI_MITSUBISHI_LEHY_3_BUTTON_3_LCD, RenderShanghaiMitsubishiLehy3Button3LCD::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.SHANGHAI_MITSUBISHI_LEHY_3_SCREEN_1_EVEN, dispatcher -> new top.xfunny.mod.client.render.RenderShanghaiMitsubishiLehy3Screen1<>(dispatcher, false));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.SHANGHAI_MITSUBISHI_LEHY_3_SCREEN_1_ODD, dispatcher -> new top.xfunny.mod.client.render.RenderShanghaiMitsubishiLehy3Screen1<>(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.SHANGHAI_MITSUBISHI_LEHY_3_SCREEN_1_WIDE_EVEN, dispatcher -> new top.xfunny.mod.client.render.RenderShanghaiMitsubishiLehy3Screen1Wide<>(dispatcher, false));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.SHANGHAI_MITSUBISHI_LEHY_3_SCREEN_1_WIDE_ODD, dispatcher -> new top.xfunny.mod.client.render.RenderShanghaiMitsubishiLehy3Screen1Wide<>(dispatcher, true));

        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.TKE_MS5E_BUTTON_1, RenderTKEMS5EButton1::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.THYSSENKRUPP_MS5E_BUTTON_1, RenderThyssenKruppMS5EButton1::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.THYSSENKRUPP_MS3E_BUTTON_1, RenderThyssenKruppMS3EButton1::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.THYSSENKRUPP_AL_C01_BUTTON_1, RenderThyssenKruppALC01Button1::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.THYSSENKRUPP_S001_BUTTON_1, RenderThyssenKruppS001Button1::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.THYSSENKRUPP_S001_BUTTON_1_WITHOUT_SCREEN, RenderThyssenKruppS001Button1WithoutScreen::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.THYSSENKRUPP_SF000_SCREEN_1_ODD, dispatcher -> new top.xfunny.mod.client.render.RenderThyssenKruppSF000Screen1<>(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.THYSSENKRUPP_SF000_SCREEN_1_EVEN, dispatcher -> new top.xfunny.mod.client.render.RenderThyssenKruppSF000Screen1<>(dispatcher, false));

        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.OTIS_GEN3_BUTTON_1, RenderOtisGen3Button1::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.OTIS_SERIES_1_BUTTON_1, RenderOtisSeries1Button::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.OTIS_SERIES_1_BUTTON_2, RenderOtisSeries1Button2::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.OTIS_SERIES_1_SCREEN_1, dispatcher -> new top.xfunny.mod.client.render.RenderOtisSeries1Screen<>(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.OTIS_SERIES_1_SCREEN_1_EVEN, dispatcher -> new top.xfunny.mod.client.render.RenderOtisSeries1Screen<>(dispatcher, false));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.OTIS_SERIES_1_SCREEN_1_HORIZONTAL, dispatcher -> new top.xfunny.mod.client.render.RenderOtisSeries1ScreenHorizontal<>(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.OTIS_SERIES_1_SCREEN_1_HORIZONTAL_EVEN, dispatcher -> new top.xfunny.mod.client.render.RenderOtisSeries1ScreenHorizontal<>(dispatcher, false));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.OTIS_SERIES_1_LANTERN_1_EVEN, dispatcher -> new top.xfunny.mod.client.render.RenderOtisSeries1Lantern1<>(dispatcher, false));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.OTIS_SERIES_1_LANTERN_1_ODD, dispatcher -> new top.xfunny.mod.client.render.RenderOtisSeries1Lantern1<>(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.OTIS_SERIES_1_LANTERN_1_HORIZONTAL_EVEN, dispatcher -> new top.xfunny.mod.client.render.RenderOtisSeries1Lantern1Horizontal<>(dispatcher, false));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.OTIS_SERIES_1_LANTERN_1_HORIZONTAL_ODD, dispatcher -> new top.xfunny.mod.client.render.RenderOtisSeries1Lantern1Horizontal<>(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.OTIS_SERIES_1_LANTERN_SCREEN_1_EVEN, dispatcher -> new top.xfunny.mod.client.render.RenderOtisSeries1LanternScreen1<>(dispatcher, false));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.OTIS_SERIES_1_LANTERN_SCREEN_1_ODD, dispatcher -> new top.xfunny.mod.client.render.RenderOtisSeries1LanternScreen1<>(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.OTIS_SERIES_1_LANTERN_SCREEN_1_HORIZONTAL_EVEN, dispatcher -> new top.xfunny.mod.client.render.RenderOtisSeries1LanternScreen1Horizontal<>(dispatcher, false));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.OTIS_SERIES_1_LANTERN_SCREEN_1_HORIZONTAL_ODD, dispatcher -> new top.xfunny.mod.client.render.RenderOtisSeries1LanternScreen1Horizontal<>(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.OTIS_SERIES_1_SCREEN_2, dispatcher -> new top.xfunny.mod.client.render.RenderOtisSeries1Screen2<>(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.OTIS_SERIES_1_SCREEN_2_EVEN, dispatcher -> new top.xfunny.mod.client.render.RenderOtisSeries1Screen2<>(dispatcher, false));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.OTIS_SERIES_1_SCREEN_2_HORIZONTAL, dispatcher -> new top.xfunny.mod.client.render.RenderOtisSeries1Screen2Horizontal<>(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.OTIS_SERIES_1_SCREEN_2_HORIZONTAL_EVEN, dispatcher -> new top.xfunny.mod.client.render.RenderOtisSeries1Screen2Horizontal<>(dispatcher, false));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.OTIS_SERIES_1_LANTERN_2_EVEN, dispatcher -> new top.xfunny.mod.client.render.RenderOtisSeries1Lantern2<>(dispatcher, false));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.OTIS_SERIES_1_LANTERN_2_ODD, dispatcher -> new top.xfunny.mod.client.render.RenderOtisSeries1Lantern2<>(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.OTIS_SERIES_1_LANTERN_2_HORIZONTAL_EVEN, dispatcher -> new top.xfunny.mod.client.render.RenderOtisSeries1Lantern2Horizontal<>(dispatcher, false));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.OTIS_SERIES_1_LANTERN_2_HORIZONTAL_ODD, dispatcher -> new top.xfunny.mod.client.render.RenderOtisSeries1Lantern2Horizontal<>(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.OTIS_SERIES_1_LANTERN_SCREEN_2_EVEN, dispatcher -> new top.xfunny.mod.client.render.RenderOtisSeries1LanternScreen2<>(dispatcher, false));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.OTIS_SERIES_1_LANTERN_SCREEN_2_ODD, dispatcher -> new top.xfunny.mod.client.render.RenderOtisSeries1LanternScreen2<>(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.OTIS_SERIES_1_LANTERN_SCREEN_2_HORIZONTAL_EVEN, dispatcher -> new top.xfunny.mod.client.render.RenderOtisSeries1LanternScreen2Horizontal<>(dispatcher, false));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.OTIS_SERIES_1_LANTERN_SCREEN_2_HORIZONTAL_ODD, dispatcher -> new top.xfunny.mod.client.render.RenderOtisSeries1LanternScreen2Horizontal<>(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.OTIS_SERIES_3_BUTTON_1, RenderOtisSeries3Button1::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.OTIS_SERIES_3_SCREEN_1_ODD, dispatcher -> new top.xfunny.mod.client.render.RenderOtisSeries3Screen1<>(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.OTIS_SERIES_3_SCREEN_1_EVEN, dispatcher -> new top.xfunny.mod.client.render.RenderOtisSeries3Screen1<>(dispatcher, false));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.OTIS_SERIES_3_ELD_SCREEN_1_ODD, dispatcher -> new top.xfunny.mod.client.render.RenderOtisSeries3ELDScreen1<>(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.OTIS_SERIES_3_ELD_SCREEN_1_EVEN, dispatcher -> new top.xfunny.mod.client.render.RenderOtisSeries3ELDScreen1<>(dispatcher, false));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.OTIS_SERIES_3_LANTERN_1_ARROW_ODD, dispatcher -> new top.xfunny.mod.client.render.RenderOtisSeries3Lantern1Arrow<>(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.OTIS_SERIES_3_LANTERN_1_ARROW_EVEN, dispatcher -> new top.xfunny.mod.client.render.RenderOtisSeries3Lantern1Arrow<>(dispatcher, false));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.OTIS_SPEC_60_BUTTON_1, RenderOtisSPEC60Button1::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.OTIS_SPEC_90_BUTTON_1_BLACK, RenderOtisSPEC90Button1Black::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.OTIS_SPEC_90_BUTTON_1_WHITE, RenderOtisSPEC90Button1White::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.OTIS_SPEC_90_BUTTON_2_BLACK, RenderOtisSPEC90Button2Black::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.OTIS_SPEC_90_BUTTON_2_WHITE, RenderOtisSPEC90Button2White::new);


        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.SCHINDLER_D_SERIES_D2BUTTON, RenderSchindlerDSeriesD2Button::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.SCHINDLER_D_SERIES_SCREEN_1_ODD, dispatcher -> new top.xfunny.mod.client.render.RenderSchindlerDSeriesScreen1<>(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.SCHINDLER_D_SERIES_SCREEN_1_EVEN, dispatcher -> new top.xfunny.mod.client.render.RenderSchindlerDSeriesScreen1<>(dispatcher, false));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.SCHINDLER_D_SERIES_SCREEN_2_GREEN_ODD, dispatcher -> new top.xfunny.mod.client.render.RenderSchindlerDSeriesScreen2<>(dispatcher, true, top.xfunny.mod.client.render.RenderSchindlerDSeriesScreen2.renderSchindlerDSeriesScreen2Color.GREEN));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.SCHINDLER_D_SERIES_SCREEN_2_GREEN_EVEN, dispatcher -> new top.xfunny.mod.client.render.RenderSchindlerDSeriesScreen2<>(dispatcher, false, top.xfunny.mod.client.render.RenderSchindlerDSeriesScreen2.renderSchindlerDSeriesScreen2Color.GREEN));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.SCHINDLER_D_SERIES_SCREEN_2_BLUE_ODD, dispatcher -> new top.xfunny.mod.client.render.RenderSchindlerDSeriesScreen2<>(dispatcher, true, top.xfunny.mod.client.render.RenderSchindlerDSeriesScreen2.renderSchindlerDSeriesScreen2Color.BLUE));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.SCHINDLER_D_SERIES_SCREEN_2_BLUE_EVEN, dispatcher -> new top.xfunny.mod.client.render.RenderSchindlerDSeriesScreen2<>(dispatcher, false, top.xfunny.mod.client.render.RenderSchindlerDSeriesScreen2.renderSchindlerDSeriesScreen2Color.BLUE));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.SCHINDLER_D_SERIES_SCREEN_2_RED_ODD, dispatcher -> new top.xfunny.mod.client.render.RenderSchindlerDSeriesScreen2<>(dispatcher, true, top.xfunny.mod.client.render.RenderSchindlerDSeriesScreen2.renderSchindlerDSeriesScreen2Color.RED));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.SCHINDLER_D_SERIES_SCREEN_2_RED_EVEN, dispatcher -> new top.xfunny.mod.client.render.RenderSchindlerDSeriesScreen2<>(dispatcher, false, top.xfunny.mod.client.render.RenderSchindlerDSeriesScreen2.renderSchindlerDSeriesScreen2Color.RED));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.SCHINDLER_M_SERIES_BUTTON, RenderSchindlerMSeriesButton::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.SCHINDLER_M_SERIES_TOUCH_BUTTON, RenderSchindlerMSeriesTouchButton::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.SCHINDLER_M_SERIES_ROUND_TOUCH_BUTTON, RenderSchindlerMSeriesRoundTouchButton::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.SCHINDLER_M_SERIES_ROUND_LANTERN_1_ODD, dispatcher -> new top.xfunny.mod.client.render.RenderSchindlerMSeriesRoundLantern1<>(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.SCHINDLER_M_SERIES_ROUND_LANTERN_1_EVEN, dispatcher -> new top.xfunny.mod.client.render.RenderSchindlerMSeriesRoundLantern1<>(dispatcher, false));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.SCHINDLER_M_SERIES_SCREEN_1, dispatcher -> new top.xfunny.mod.client.render.RenderSchindlerMSeriesScreen1<>(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.SCHINDLER_M_SERIES_SCREEN_1_EVEN, dispatcher -> new top.xfunny.mod.client.render.RenderSchindlerMSeriesScreen1<>(dispatcher, false));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.SCHINDLER_M_SERIES_SCREEN_2_ODD, dispatcher -> new top.xfunny.mod.client.render.RenderSchindlerMSeriesScreen2<>(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.SCHINDLER_M_SERIES_SCREEN_2_EVEN, dispatcher -> new top.xfunny.mod.client.render.RenderSchindlerMSeriesScreen2<>(dispatcher, false));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.SCHINDLER_M_SERIES_SCREEN_3_ODD, dispatcher -> new top.xfunny.mod.client.render.RenderSchindlerMSeriesScreen3<>(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.SCHINDLER_M_SERIES_SCREEN_3_EVEN, dispatcher -> new top.xfunny.mod.client.render.RenderSchindlerMSeriesScreen3<>(dispatcher, false));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.SCHINDLER_M_SERIES_SCREEN_4_ODD, dispatcher -> new top.xfunny.mod.client.render.RenderSchindlerMSeriesScreen4<>(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.SCHINDLER_M_SERIES_SCREEN_4_EVEN, dispatcher -> new top.xfunny.mod.client.render.RenderSchindlerMSeriesScreen4<>(dispatcher, false));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.SCHINDLER_M_SERIES_SCREEN_5_ODD, dispatcher -> new top.xfunny.mod.client.render.RenderSchindlerMSeriesScreen5<>(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.SCHINDLER_M_SERIES_SCREEN_5_EVEN, dispatcher -> new top.xfunny.mod.client.render.RenderSchindlerMSeriesScreen5<>(dispatcher, false));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.SCHINDLER_R_SERIES_SCREEN_1_ODD, dispatcher -> new top.xfunny.mod.client.render.RenderSchindlerRSeriesScreen1<>(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.SCHINDLER_R_SERIES_SCREEN_1_EVEN, dispatcher -> new top.xfunny.mod.client.render.RenderSchindlerRSeriesScreen1<>(dispatcher, false));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.SCHINDLER_S_SERIES_GREY_BUTTON, RenderSchindlerSSeriesGreyButton::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.SCHINDLER_S_SERIES_DARK_GREY_BUTTON, RenderSchindlerSSeriesDarkGreyButton::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.SCHINDLER_S_SERIES_BLUE_BUTTON, RenderSchindlerSSeriesBlueButton::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.SCHINDLER_R_SERIES_ROUND_BUTTON, RenderSchindlerRSeriesRoundButton::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.SCHINDLER_LINEA_BUTTON_1_WHITE, RenderSchindlerLineaButton1White::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.SCHINDLER_LINEA_BUTTON_1_BLACK, RenderSchindlerLineaButton1Black::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.SCHINDLER_LINEA_BUTTON_2_WHITE, RenderSchindlerLineaButton2White::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.SCHINDLER_LINEA_BUTTON_2_BLACK, RenderSchindlerLineaButton2Black::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.SCHINDLER_LINEA_BUTTON_1_BLACK_WITHOUT_SCREEN, RenderSchindlerLineaButton1BlackWithoutScreen::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.SCHINDLER_LINEA_BUTTON_1_WHITE_WITHOUT_SCREEN, RenderSchindlerLineaButton1WhiteWithoutScreen::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.SCHINDLER_LINEA_BUTTON_2_BLACK_WITHOUT_SCREEN, RenderSchindlerLineaButton2BlackWithoutScreen::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.SCHINDLER_LINEA_BUTTON_2_WHITE_WITHOUT_SCREEN, RenderSchindlerLineaButton2WhiteWithoutScreen::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.SCHINDLER_LINEA_SCREEN_1_WHITE_HORIZONTAL_ODD, dispatcher -> new RenderSchindlerLineaScreen1WhiteHorizontal<>(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.SCHINDLER_LINEA_SCREEN_1_WHITE_HORIZONTAL_EVEN, dispatcher -> new RenderSchindlerLineaScreen1WhiteHorizontal<>(dispatcher, false));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.SCHINDLER_LINEA_SCREEN_1_WHITE_VERTICAL_ODD, dispatcher -> new RenderSchindlerLineaScreen1WhiteVertical<>(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.SCHINDLER_LINEA_SCREEN_1_WHITE_VERTICAL_EVEN, dispatcher -> new RenderSchindlerLineaScreen1WhiteVertical<>(dispatcher, false));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.SCHINDLER_LINEA_SCREEN_1_BLACK_HORIZONTAL_ODD, dispatcher -> new RenderSchindlerLineaScreen1BlackHorizontal<>(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.SCHINDLER_LINEA_SCREEN_1_BLACK_HORIZONTAL_EVEN, dispatcher -> new RenderSchindlerLineaScreen1BlackHorizontal<>(dispatcher, false));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.SCHINDLER_LINEA_SCREEN_1_BLACK_VERTICAL_ODD, dispatcher -> new RenderSchindlerLineaScreen1BlackVertical<>(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.SCHINDLER_LINEA_SCREEN_1_BLACK_VERTICAL_EVEN, dispatcher -> new RenderSchindlerLineaScreen1BlackVertical<>(dispatcher, false));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.SCHINDLER_LINEA_SCREEN_2_WHITE_HORIZONTAL_ODD, dispatcher -> new RenderSchindlerLineaScreen2WhiteHorizontal<>(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.SCHINDLER_LINEA_SCREEN_2_WHITE_HORIZONTAL_EVEN, dispatcher -> new RenderSchindlerLineaScreen2WhiteHorizontal<>(dispatcher, false));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.SCHINDLER_LINEA_SCREEN_2_WHITE_VERTICAL_ODD, dispatcher -> new RenderSchindlerLineaScreen2WhiteVertical<>(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.SCHINDLER_LINEA_SCREEN_2_WHITE_VERTICAL_EVEN, dispatcher -> new RenderSchindlerLineaScreen2WhiteVertical<>(dispatcher, false));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.SCHINDLER_LINEA_SCREEN_2_BLACK_HORIZONTAL_ODD, dispatcher -> new RenderSchindlerLineaScreen2BlackHorizontal<>(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.SCHINDLER_LINEA_SCREEN_2_BLACK_HORIZONTAL_EVEN, dispatcher -> new RenderSchindlerLineaScreen2BlackHorizontal<>(dispatcher, false));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.SCHINDLER_LINEA_SCREEN_2_BLACK_VERTICAL_ODD, dispatcher -> new RenderSchindlerLineaScreen2BlackVertical<>(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.SCHINDLER_LINEA_SCREEN_2_BLACK_VERTICAL_EVEN, dispatcher -> new RenderSchindlerLineaScreen2BlackVertical<>(dispatcher, false));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.SCHINDLER_FI_GS_BUTTON_1, RenderSchindlerFIGSButton1::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.SCHINDLER_FI_GS_BUTTON_1_WITHOUT_SCREEN, RenderSchindlerFIGSButton1WithoutScreen::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.SCHINDLER_FI_GS_TOUCH_BUTTON_1, RenderSchindlerFIGSTouchButton1::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.SCHINDLER_FI_GS_TOUCH_BUTTON_1_WITHOUT_SCREEN, RenderSchindlerFIGSTouchButton1WithoutScreen::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.SCHINDLER_FI_GS_BUTTON_1_STEEL, RenderSchindlerFIGSButton1Steel::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.SCHINDLER_FI_GS_SCREEN_1_STEEL_ODD, dispatcher -> new RenderSchindlerFIGSScreen1Steel<>(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.SCHINDLER_FI_GS_SCREEN_1_STEEL_EVEN, dispatcher -> new RenderSchindlerFIGSScreen1Steel<>(dispatcher, false));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.SCHINDLER_FI_GS_SCREEN_1_BLACK_ODD, dispatcher -> new RenderSchindlerFIGSScreen1Black<>(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.SCHINDLER_FI_GS_SCREEN_1_BLACK_EVEN, dispatcher -> new RenderSchindlerFIGSScreen1Black<>(dispatcher, false));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.SCHINDLER_FI_GS_SCREEN_1_GREY_ODD, dispatcher -> new RenderSchindlerFIGSScreen1Grey<>(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.SCHINDLER_FI_GS_SCREEN_1_GREY_EVEN, dispatcher -> new RenderSchindlerFIGSScreen1Grey<>(dispatcher, false));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.SCHINDLER_Z_LINE_3_KEYPAD_1, dispatcher -> new top.xfunny.mod.client.render.RenderSchindlerZLine3Keypad1(dispatcher, true));

        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.HITACHI_VIB320_BUTTON, RenderHitachiVIB320Button::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.HITACHI_VIB320_BUTTON_DOT_MATRIX, RenderHitachiVIB320ButtonDotMatrix::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.HITACHI_VIB320_BUTTON_HIP43, RenderHitachiVIB320ButtonHIP43::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.HITACHI_VIB322_BUTTON, RenderHitachiVIB322Button::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.HITACHI_VIB322_BUTTON_DOT_MATRIX, RenderHitachiVIB322ButtonDotMatrix::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.HITACHI_VIB325_BUTTON, RenderHitachiVIB325Button::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.HITACHI_VIB325_BUTTON_DOT_MATRIX, RenderHitachiVIB325ButtonDotMatrix::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.HITACHI_VIB221_BUTTON, RenderHitachiVIB221Button::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.HITACHI_VIB221_BUTTON_DOT_MATRIX, RenderHitachiVIB221ButtonDotMatrix::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.HITACHI_VIB221_BUTTON_LCD_SEGMENTED, RenderHitachiVIB221ButtonLCDSegmented::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.HITACHI_VIB221_BUTTON_HIP43, RenderHitachiVIB221ButtonHIP43::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.HITACHI_VIB820_BUTTON, RenderHitachiVIB820Button::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.HITACHI_VIB820_BUTTON_LCD, RenderHitachiVIB820ButtonLCD::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.HITACHI_VIB68_BUTTON, RenderHitachiVIB68Button::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.HITACHI_VIB191_BUTTON, RenderHitachiVIB191Button::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.HITACHI_VIB192_BUTTON, RenderHitachiVIB192Button::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.HITACHI_VIB820PRO_BUTTON, RenderHitachiVIB820proButton::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.HITACHI_HB820_BUTTON, RenderHitachiHB820Button::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.HITACHI_HSB820_BUTTON, RenderHitachiHSB820Button::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.HITACHI_HSB820PRO_BUTTON, RenderHitachiHSB820proButton::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.HITACHI_GHD820PRO_SCREEN_EVEN, dispatcher -> new RenderHitachiGHD820proScreen<>(dispatcher, false));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.HITACHI_GHD820PRO_SCREEN_ODD, dispatcher -> new RenderHitachiGHD820proScreen<>(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.HITACHI_GHI675_SCREEN_EVEN, dispatcher -> new RenderHitachiGHI675Screen<>(dispatcher, false));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.HITACHI_GHI675_SCREEN_ODD, dispatcher -> new RenderHitachiGHI675Screen<>(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.HITACHI_GHL820_LANTERN_EVEN, dispatcher -> new RenderHitachiGHL820Lantern1<>(dispatcher, false));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.HITACHI_GHL820_LANTERN_ODD, dispatcher -> new RenderHitachiGHL820Lantern1<>(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.HITACHI_GHL668_LANTERN_EVEN, dispatcher -> new RenderHitachiGHL668Lantern1<>(dispatcher, false));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.HITACHI_GHL668_LANTERN_ODD, dispatcher -> new RenderHitachiGHL668Lantern1<>(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.HITACHI_GHL673_LANTERN_EVEN, dispatcher -> new RenderHitachiGHL673Lantern1<>(dispatcher, false));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.HITACHI_GHL673_LANTERN_ODD, dispatcher -> new RenderHitachiGHL673Lantern1<>(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.HITACHI_VIB628_BUTTON, RenderHitachiVIB628Button::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.HITACHI_HB628_BUTTON, RenderHitachiHB628Button::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.HITACHI_VIB668_BUTTON, RenderHitachiVIB668Button::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.HITACHI_VIB658_BUTTON, RenderHitachiVIB658Button::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.HITACHI_HB658_BUTTON, RenderHitachiHB658Button::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.HITACHI_VIB663_BUTTON, RenderHitachiVIB663Button::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.HITACHI_VIB681_BUTTON, RenderHitachiVIB681Button::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.HITACHI_VIB676_BUTTON, RenderHitachiVIB676Button::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.HITACHI_VIB679_BUTTON, RenderHitachiVIB679Button::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.HITACHI_VIB673_BUTTON, RenderHitachiVIB673Button::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.HITACHI_VIB673_BUTTON_HIP43, RenderHitachiVIB673ButtonHIP43::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.HITACHI_HB673_BUTTON, RenderHitachiHB673Button::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.HITACHI_VIB181A_BUTTON, RenderHitachiVIB181AButton::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.HITACHI_VIB182A_BUTTON, RenderHitachiVIB182AButton::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.HITACHI_HB181A_BUTTON, RenderHitachiHB181AButton::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.HITACHI_B85_BUTTON_1, RenderHitachiB85Button1::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.HITACHI_B85_BUTTON_1_WITHOUT_SCREEN, RenderHitachiB85Button1WithoutScreen::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.HITACHI_B85_BUTTON_2, RenderHitachiB85Button2::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.HITACHI_B85_SCREEN_1_VERTICAL_ODD, dispatcher -> new RenderHitachiB85Screen1Vertical<>(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.HITACHI_B85_SCREEN_1_VERTICAL_EVEN, dispatcher -> new RenderHitachiB85Screen1Vertical<>(dispatcher, false));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.HITACHI_B85_SCREEN_1_HORIZONTAL_ODD, dispatcher -> new RenderHitachiB85Screen1Horizontal<>(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.HITACHI_B85_SCREEN_1_HORIZONTAL_EVEN, dispatcher -> new RenderHitachiB85Screen1Horizontal<>(dispatcher, false));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.HITACHI_B89_BUTTON_1, RenderHitachiB89Button1::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.HITACHI_B89_BUTTON_1_WITHOUT_SCREEN, RenderHitachiB89Button1WithoutScreen::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.HITACHI_B89_BUTTON_2, RenderHitachiB89Button2::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.HITACHI_B89_SCREEN_1_VERTICAL_ODD, dispatcher -> new RenderHitachiB89Screen1Vertical<>(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.HITACHI_B89_SCREEN_1_VERTICAL_EVEN, dispatcher -> new RenderHitachiB89Screen1Vertical<>(dispatcher, false));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.HITACHI_B89_SCREEN_1_HORIZONTAL_ODD, dispatcher -> new RenderHitachiB89Screen1Horizontal<>(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.HITACHI_B89_SCREEN_1_HORIZONTAL_EVEN, dispatcher -> new RenderHitachiB89Screen1Horizontal<>(dispatcher, false));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.HITACHI_BUTTON_PAFC, RenderHitachiButtonPAFC::new);

        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.TONIC_DS_SCREEN_1_ODD, dispatcher -> new top.xfunny.mod.client.render.RenderTonicDSScreen1<>(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.TONIC_DS_SCREEN_1_EVEN, dispatcher -> new top.xfunny.mod.client.render.RenderTonicDSScreen1<>(dispatcher, false));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.TONIC_DM_SCREEN_1_GREEN_ODD, dispatcher -> new top.xfunny.mod.client.render.RenderTonicDMScreen1<>(dispatcher, true, RenderTonicDMScreen1.renderTonicDMScreen1Color.GREEN));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.TONIC_DM_SCREEN_1_GREEN_EVEN, dispatcher -> new top.xfunny.mod.client.render.RenderTonicDMScreen1<>(dispatcher, false, RenderTonicDMScreen1.renderTonicDMScreen1Color.GREEN));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.TONIC_DM_SCREEN_1_YELLOW_ODD, dispatcher -> new top.xfunny.mod.client.render.RenderTonicDMScreen1<>(dispatcher, true, RenderTonicDMScreen1.renderTonicDMScreen1Color.YELLOW));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.TONIC_DM_SCREEN_1_YELLOW_EVEN, dispatcher -> new top.xfunny.mod.client.render.RenderTonicDMScreen1<>(dispatcher, false, RenderTonicDMScreen1.renderTonicDMScreen1Color.YELLOW));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.TONIC_DM_SCREEN_1_RED_ODD, dispatcher -> new top.xfunny.mod.client.render.RenderTonicDMScreen1<>(dispatcher, true, RenderTonicDMScreen1.renderTonicDMScreen1Color.RED));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.TONIC_DM_SCREEN_1_RED_EVEN, dispatcher -> new top.xfunny.mod.client.render.RenderTonicDMScreen1<>(dispatcher, false, RenderTonicDMScreen1.renderTonicDMScreen1Color.RED));

        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.TOSHIBA_BUTTON_1, RenderToshibaButton1::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.TOSHIBA_LANTERN_1_ODD, dispatcher -> new top.xfunny.mod.client.render.RenderToshibaLantern1<>(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.TOSHIBA_LANTERN_1_EVEN, dispatcher -> new top.xfunny.mod.client.render.RenderToshibaLantern1<>(dispatcher, false));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.TOSHIBA_SCREEN_1_ODD, dispatcher -> new top.xfunny.mod.client.render.RenderToshibaScreen1<>(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.TOSHIBA_SCREEN_1_EVEN, dispatcher -> new top.xfunny.mod.client.render.RenderToshibaScreen1<>(dispatcher, false));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.TOSHIBA_SCREEN_2_ODD, dispatcher -> new top.xfunny.mod.client.render.RenderToshibaScreen2<>(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.TOSHIBA_SCREEN_2_EVEN, dispatcher -> new top.xfunny.mod.client.render.RenderToshibaScreen2<>(dispatcher, false));

        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.HITACHI_B85_DOOR_1, dispatcher -> new top.xfunny.mod.client.render.RenderLiftDoor<>(dispatcher, 7));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.KONE_M_DOOR_1, dispatcher -> new top.xfunny.mod.client.render.RenderLiftDoor<>(dispatcher, 6));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.MITSUBISHI_NEXWAY_DOOR_1, dispatcher -> new top.xfunny.mod.client.render.RenderLiftDoor<>(dispatcher, 5));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.SCHINDLER_QKS9_DOOR_1, dispatcher -> new top.xfunny.mod.client.render.RenderLiftDoor<>(dispatcher, 3));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.OTIS_E411_US_DOOR_1, dispatcher -> new top.xfunny.mod.client.render.RenderLiftDoor<>(dispatcher, 8));

        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.PAT_RS01_RAILWAY_SIGN_2_EVEN, RenderPATRS01RailwaySign::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.PAT_RS01_RAILWAY_SIGN_2_ODD, RenderPATRS01RailwaySign::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.PAT_RS01_RAILWAY_SIGN_3_EVEN, RenderPATRS01RailwaySign::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.PAT_RS01_RAILWAY_SIGN_3_ODD, RenderPATRS01RailwaySign::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.PAT_RS01_RAILWAY_SIGN_4_EVEN, RenderPATRS01RailwaySign::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.PAT_RS01_RAILWAY_SIGN_4_ODD, RenderPATRS01RailwaySign::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.PAT_RS01_RAILWAY_SIGN_5_EVEN, RenderPATRS01RailwaySign::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.PAT_RS01_RAILWAY_SIGN_5_ODD, RenderPATRS01RailwaySign::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.PAT_RS01_RAILWAY_SIGN_6_EVEN, RenderPATRS01RailwaySign::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.PAT_RS01_RAILWAY_SIGN_6_ODD, RenderPATRS01RailwaySign::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.PAT_RS01_RAILWAY_SIGN_7_EVEN, RenderPATRS01RailwaySign::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.PAT_RS01_RAILWAY_SIGN_7_ODD, RenderPATRS01RailwaySign::new);

        REGISTRY_CLIENT.registerItemModelPredicate(Items.YTE_LIFT_BUTTONS_LINK_CONNECTOR, new Identifier(Init.MOD_ID, "selected"), checkItemPredicateTag());
        REGISTRY_CLIENT.registerItemModelPredicate(Items.YTE_LIFT_BUTTONS_LINK_REMOVER, new Identifier(Init.MOD_ID, "selected"), checkItemPredicateTag());
        REGISTRY_CLIENT.registerItemModelPredicate(Items.YTE_GROUP_LIFT_BUTTONS_LINK_CONNECTOR, new Identifier(Init.MOD_ID, "selected"), checkItemPredicateTag());
        REGISTRY_CLIENT.registerItemModelPredicate(Items.YTE_GROUP_LIFT_BUTTONS_LINK_REMOVER, new Identifier(Init.MOD_ID, "selected"), checkItemPredicateTag());


        REGISTRY_CLIENT.setupPackets(new Identifier(Init.MOD_ID, "packet"));


        REGISTRY_CLIENT.eventRegistryClient.registerClientJoin(() -> {
            MinecraftClientData.reset();
            YteMinecraftClientData.reset();
            YteLiftConfigStore.clear();
            LiftArrivalLanternState.clear();
            LiftDisplayState.clear();
            LiftDisplayDirectionState.clear();
            MitsubishiNexWayLanternPolicy.INSTANCE.clear();
            LiftDoorControlState.clearClientState();
            lastMillis = System.currentTimeMillis();
            gameMillis = 0;

            DynamicTextureCache.instance.reload();
            FontList.instance.FontReload();

            SoundPlaybackManager.clearCache();

            // 请求电梯速度配置
            REGISTRY_CLIENT.sendPacketToServer(new YtePacketRequestData());
        });

        // TODO: 发布前请注释此行代码。
        //REGISTRY_CLIENT.eventRegistryClient.registerGuiRendering(RenderWatermark::render);
        REGISTRY_CLIENT.init();
    }


    private static RegistryClient.ModelPredicateProvider checkItemPredicateTag() {
        return (itemStack, clientWorld, livingEntity) -> {
            if (itemStack.getOrCreateTag().contains(YTEItemBlockClickingBase.TAG_SECOND_POS)) {
                return 1F;
            } else if (itemStack.getOrCreateTag().contains(YTEItemBlockClickingBase.TAG_POS)) {
                return 0.5F;
            } else {
                return 0;
            }
        };
    }

    private static void initializeConfig() {
        config.readConfig();
    }

    public static ClientConfig getConfig() {
        return config;
    }
}
