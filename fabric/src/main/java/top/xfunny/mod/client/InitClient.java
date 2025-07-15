package top.xfunny.mod.client;


import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.holder.RenderLayer;
import org.mtr.mapping.registry.RegistryClient;
import org.mtr.mod.client.MinecraftClientData;
import org.mtr.mod.item.ItemBlockClickingBase;
import top.xfunny.mod.BlockEntityTypes;
import top.xfunny.mod.Blocks;
import top.xfunny.mod.Init;
import top.xfunny.mod.Items;
import top.xfunny.mod.client.render.*;


public final class InitClient {
    public static final RegistryClient REGISTRY_CLIENT = new RegistryClient(Init.REGISTRY);
    private static long lastMillis = 0;
    private static long gameMillis = 0;


    public static void init() {
        REGISTRY_CLIENT.registerBlockRenderType(RenderLayer.getCutout(), Blocks.SCHINDLER_QKS9_DOOR_1);
        REGISTRY_CLIENT.registerBlockRenderType(RenderLayer.getCutout(), Blocks.MITSUBISHI_NEXWAY_DOOR_1);


        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.TEST_LIFT_BUTTONS, RenderTestLiftButtons4::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.TEST_LIFT_HALL_LANTERNS, dispatcher -> new top.xfunny.mod.client.render.RenderTestLiftHallLanterns(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.TEST_LIFT_BUTTONS_WITHOUT_SCREEN, RenderTestLiftButtonsWithoutScreen::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.TEST_LIFT_PANEL, dispatcher -> new top.xfunny.mod.client.render.RenderTestLiftPanel(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.TEST_LIFT_DESTINATION_DISPATCH_TERMINAL, dispatcher -> new top.xfunny.mod.client.render.RenderTestLiftDestinationDispatchTerminal(dispatcher, true));

        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.CES_SCREEN_1_ODD, dispatcher -> new top.xfunny.mod.client.render.RenderCESScreen1<>(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.CES_SCREEN_1_EVEN, dispatcher -> new top.xfunny.mod.client.render.RenderCESScreen1<>(dispatcher, false));

        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.DEWHURST_US89_BUTTON_1, RenderDewhurstUS89Button1::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.DEWHURST_US91_BUTTON_1, RenderDewhurstUS91Button1::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.DEWHURST_US91_BUTTON_1_BRAILLE, RenderDewhurstUS91Button1Braille::new);

        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.KONE_KDS330_BUTTON_1, RenderKoneKDS330Button1::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.KONE_KDS330_BUTTON_1_WITHOUT_SCREEN, RenderKoneKDS330Button1WithoutScreen::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.KONE_KDS330_LANTERN_1, RenderKoneKDS330Lantern1::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.KONE_KSS280_BUTTON_1, RenderKoneKSS280Button1::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.KONE_KSS280_BUTTON_1_WITHOUT_SCREEN, RenderKoneKSS280Button1WithoutScreen::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.KONE_KSS280_SCREEN_1_ODD, dispatcher -> new top.xfunny.mod.client.render.RenderKoneKSS280Screen1<>(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.KONE_KSS280_SCREEN_1_EVEN, dispatcher -> new top.xfunny.mod.client.render.RenderKoneKSS280Screen1<>(dispatcher, false));

        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.KONE_M_BUTTON_1, RenderKoneMButton1::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.KONE_M_BUTTON_2, RenderKoneMButton2::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.KONE_M_SCREEN_1_ODD, dispatcher -> new top.xfunny.mod.client.render.RenderKoneMScreen1<>(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.KONE_M_SCREEN_1_EVEN, dispatcher -> new top.xfunny.mod.client.render.RenderKoneMScreen1<>(dispatcher, false));

        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.MITSUBISHI_GPS_BUTTON_1, RenderMitsubishiGPSButton1::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.MITSUBISHI_GPS_BUTTON_1_WITHOUT_SCREEN, RenderMitsubishiGPSButton1WithoutScreen::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.MITSUBISHI_BUTTON_SHUN_HING_SQUARE, RenderMitsubishiButtonShunHingSquare::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.MITSUBISHI_RYODEN_SCREEN_1_EVEN, dispatcher -> new top.xfunny.mod.client.render.RenderMitsubishiRyodenScreen1<>(dispatcher, false));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.MITSUBISHI_RYODEN_SCREEN_1_ODD, dispatcher -> new top.xfunny.mod.client.render.RenderMitsubishiRyodenScreen1<>(dispatcher, true));
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
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.MITSUBISHI_MAXIEZ_BUTTON_1_GOLD, RenderMitsubishiMaxiezButton1Gold::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.MITSUBISHI_MAXIEZ_BUTTON_1_SILVER, RenderMitsubishiMaxiezButton1Silver::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.MITSUBISHI_MPVF_BUTTON_1, RenderMitsubishiMPVFButton1::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.MITSUBISHI_MPVF_SCREEN_1_VERTICAL_ODD, dispatcher -> new top.xfunny.mod.client.render.RenderMitsubishiMPVFScreen1Vertical<>(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.MITSUBISHI_MPVF_SCREEN_1_VERTICAL_EVEN, dispatcher -> new top.xfunny.mod.client.render.RenderMitsubishiMPVFScreen1Vertical<>(dispatcher, false));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.MITSUBISHI_NEXWAY_LANTERN_1_HORIZONTAL_EVEN, dispatcher -> new top.xfunny.mod.client.render.RenderMitsubishiNexWayLantern1Horizontal<>(dispatcher, false));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.MITSUBISHI_NEXWAY_LANTERN_1_HORIZONTAL_ODD, dispatcher -> new top.xfunny.mod.client.render.RenderMitsubishiNexWayLantern1Horizontal<>(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.MITSUBISHI_NEXWAY_LANTERN_1_VERTICAL_EVEN, dispatcher -> new top.xfunny.mod.client.render.RenderMitsubishiNexWayLantern1Vertical<>(dispatcher, false));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.MITSUBISHI_NEXWAY_LANTERN_1_VERTICAL_ODD, dispatcher -> new top.xfunny.mod.client.render.RenderMitsubishiNexWayLantern1Vertical<>(dispatcher, true));
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

        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.THYSSENKRUPP_TEGL1_BUTTON_1, RenderThyssenkruppTEGL1Button1::new);

        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.OTIS_SERIES_1_BUTTON_1, RenderOtisSeries1Button::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.OTIS_SERIES_1_BUTTON_2, RenderOtisSeries1Button::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.OTIS_SERIES_1_SCREEN_1, dispatcher -> new top.xfunny.mod.client.render.RenderOtisSeries1Screen(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.OTIS_SERIES_3_BUTTON_1, RenderOtisSeries3Button1::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.OTIS_SERIES_3_SCREEN_1_ODD, dispatcher -> new top.xfunny.mod.client.render.RenderOtisSeries3Screen1<>(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.OTIS_SERIES_3_SCREEN_1_EVEN, dispatcher -> new top.xfunny.mod.client.render.RenderOtisSeries3Screen1<>(dispatcher, false));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.OTIS_SERIES_3_ELD_SCREEN_1_ODD, dispatcher -> new top.xfunny.mod.client.render.RenderOtisSeries3ELDScreen1<>(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.OTIS_SERIES_3_ELD_SCREEN_1_EVEN, dispatcher -> new top.xfunny.mod.client.render.RenderOtisSeries3ELDScreen1<>(dispatcher, false));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.OTIS_SERIES_3_LANTERN_1_ARROW_ODD, dispatcher -> new top.xfunny.mod.client.render.RenderOtisSeries3Lantern1Arrow<>(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.OTIS_SERIES_3_LANTERN_1_ARROW_EVEN, dispatcher -> new top.xfunny.mod.client.render.RenderOtisSeries3Lantern1Arrow<>(dispatcher, false));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.OTIS_SPEC_60_BUTTON_1, RenderOtisSPEC60Button1::new);


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
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.SCHINDLER_M_SERIES_SCREEN_1, dispatcher -> new top.xfunny.mod.client.render.RenderSchindlerMSeriesScreen1(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.SCHINDLER_M_SERIES_SCREEN_2_ODD, dispatcher -> new top.xfunny.mod.client.render.RenderSchindlerMSeriesScreen2<>(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.SCHINDLER_M_SERIES_SCREEN_2_EVEN, dispatcher -> new top.xfunny.mod.client.render.RenderSchindlerMSeriesScreen2<>(dispatcher, false));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.SCHINDLER_M_SERIES_SCREEN_3_ODD, dispatcher -> new top.xfunny.mod.client.render.RenderSchindlerMSeriesScreen3<>(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.SCHINDLER_M_SERIES_SCREEN_3_EVEN, dispatcher -> new top.xfunny.mod.client.render.RenderSchindlerMSeriesScreen3<>(dispatcher, false));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.SCHINDLER_M_SERIES_SCREEN_4_ODD, dispatcher -> new top.xfunny.mod.client.render.RenderSchindlerMSeriesScreen4<>(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.SCHINDLER_M_SERIES_SCREEN_4_EVEN, dispatcher -> new top.xfunny.mod.client.render.RenderSchindlerMSeriesScreen4<>(dispatcher, false));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.SCHINDLER_S_SERIES_GREY_BUTTON, RenderSchindlerSSeriesGreyButton::new);
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
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.SCHINDLER_Z_LINE_3_KEYPAD_1, dispatcher -> new top.xfunny.mod.client.render.RenderSchindlerZLine3Keypad1(dispatcher, true));

        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.HITACHI_VIB320_BUTTON, RenderHitachiVIB320Button::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.HITACHI_VIB320_BUTTON_DOT_MATRIX, RenderHitachiVIB320ButtonDotMatrix::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.HITACHI_VIB322_BUTTON, RenderHitachiVIB322Button::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.HITACHI_VIB322_BUTTON_DOT_MATRIX, RenderHitachiVIB322ButtonDotMatrix::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.HITACHI_VIB221_BUTTON, RenderHitachiVIB221Button::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.HITACHI_VIB221_BUTTON_DOT_MATRIX, RenderHitachiVIB221ButtonDotMatrix::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.HITACHI_VIB820_BUTTON, RenderHitachiVIB820Button::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.HITACHI_VIB820_BUTTON_LCD, RenderHitachiVIB820ButtonLCD::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.HITACHI_VIB820PRO_BUTTON, RenderHitachiVIB820proButton::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.HITACHI_HB820_BUTTON, RenderHitachiHB820Button::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.HITACHI_HSB820_BUTTON, RenderHitachiHSB820Button::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.HITACHI_HSB820PRO_BUTTON, RenderHitachiHSB820proButton::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.HITACHI_GHD820PRO_SCREEN_EVEN, dispatcher -> new RenderHitachiGHD820proScreen<>(dispatcher, false));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.HITACHI_GHD820PRO_SCREEN_ODD, dispatcher -> new RenderHitachiGHD820proScreen<>(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.HITACHI_GHI675_SCREEN_EVEN, dispatcher -> new RenderHitachiGHI675Screen<>(dispatcher, false));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.HITACHI_GHI675_SCREEN_ODD, dispatcher -> new RenderHitachiGHI675Screen<>(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.HITACHI_VIB668_BUTTON, RenderHitachiVIB668Button::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.HITACHI_VIB658_BUTTON, RenderHitachiVIB658Button::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.HITACHI_VIB663_BUTTON, RenderHitachiVIB663Button::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.HITACHI_B85_BUTTON_1, RenderHitachiB85Button1::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.HITACHI_B85_BUTTON_1_WITHOUT_SCREEN, RenderHitachiB85Button1WithoutScreen::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.HITACHI_B85_BUTTON_2, RenderHitachiB85Button2::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.HITACHI_BUTTON_PAFC, RenderHitachiButtonPAFC::new);

        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.TONIC_DS_SCREEN_1_ODD, dispatcher -> new top.xfunny.mod.client.render.RenderTonicDSScreen1<>(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.TONIC_DS_SCREEN_1_EVEN, dispatcher -> new top.xfunny.mod.client.render.RenderTonicDSScreen1<>(dispatcher, false));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.TONIC_DM_SCREEN_1_GREEN_ODD, dispatcher -> new top.xfunny.mod.client.render.RenderTonicDMScreen1<>(dispatcher, true, RenderTonicDMScreen1.renderTonicDMScreen1Color.GREEN));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.TONIC_DM_SCREEN_1_GREEN_EVEN, dispatcher -> new top.xfunny.mod.client.render.RenderTonicDMScreen1<>(dispatcher, false, RenderTonicDMScreen1.renderTonicDMScreen1Color.GREEN));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.TONIC_DM_SCREEN_1_YELLOW_ODD, dispatcher -> new top.xfunny.mod.client.render.RenderTonicDMScreen1<>(dispatcher, true, RenderTonicDMScreen1.renderTonicDMScreen1Color.YELLOW));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.TONIC_DM_SCREEN_1_YELLOW_EVEN, dispatcher -> new top.xfunny.mod.client.render.RenderTonicDMScreen1<>(dispatcher, false, RenderTonicDMScreen1.renderTonicDMScreen1Color.YELLOW));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.TONIC_DM_SCREEN_1_RED_ODD, dispatcher -> new top.xfunny.mod.client.render.RenderTonicDMScreen1<>(dispatcher, true, RenderTonicDMScreen1.renderTonicDMScreen1Color.RED));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.TONIC_DM_SCREEN_1_RED_EVEN, dispatcher -> new top.xfunny.mod.client.render.RenderTonicDMScreen1<>(dispatcher, false, RenderTonicDMScreen1.renderTonicDMScreen1Color.RED));


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

        REGISTRY_CLIENT.registerItemModelPredicate(Items.YTE_LIFT_BUTTONS_LINK_CONNECTOR, new Identifier(org.mtr.mod.Init.MOD_ID, "selected"), checkItemPredicateTag());
        REGISTRY_CLIENT.registerItemModelPredicate(Items.YTE_LIFT_BUTTONS_LINK_REMOVER, new Identifier(org.mtr.mod.Init.MOD_ID, "selected"), checkItemPredicateTag());
        REGISTRY_CLIENT.registerItemModelPredicate(Items.YTE_GROUP_LIFT_BUTTONS_LINK_CONNECTOR, new Identifier(org.mtr.mod.Init.MOD_ID, "selected"), checkItemPredicateTag());
        REGISTRY_CLIENT.registerItemModelPredicate(Items.YTE_GROUP_LIFT_BUTTONS_LINK_REMOVER, new Identifier(org.mtr.mod.Init.MOD_ID, "selected"), checkItemPredicateTag());

        REGISTRY_CLIENT.setupPackets(new Identifier(Init.MOD_ID, "packet"));

        REGISTRY_CLIENT.eventRegistryClient.registerClientJoin(() -> {
            MinecraftClientData.reset();
            DynamicTextureCache.instance = new DynamicTextureCache();
            lastMillis = System.currentTimeMillis();
            gameMillis = 0;

            DynamicTextureCache.instance.reload();
        });
        REGISTRY_CLIENT.init();
    }


    private static RegistryClient.ModelPredicateProvider checkItemPredicateTag() {
        return (itemStack, clientWorld, livingEntity) -> itemStack.getOrCreateTag().contains(ItemBlockClickingBase.TAG_POS) ? 1 : 0;
    }

    public static float getGameTick() {
        return (float) gameMillis / 50.0F;
    }
}