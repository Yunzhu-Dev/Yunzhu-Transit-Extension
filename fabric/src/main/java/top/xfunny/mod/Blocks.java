package top.xfunny.mod;

import org.mtr.mapping.holder.Block;
import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.mapper.BlockHelper;
import org.mtr.mapping.registry.BlockRegistryObject;
import org.mtr.mod.block.BlockTicketMachine;
import top.xfunny.mod.block.*;

import static org.mtr.mod.Blocks.createDefaultBlockSettings;


public class Blocks {


    public static final BlockRegistryObject TEST_LIFT_BUTTONS;
    public static final BlockRegistryObject TEST_LIFT_BUTTONS_WITHOUT_SCREEN;
    public static final BlockRegistryObject TEST_LIFT_HALL_LANTERNS;
    public static final BlockRegistryObject TEST_LIFT_PANEL;
    public static final BlockRegistryObject TEST_LIFT_DESTINATION_DISPATCH_TERMINAL;

    public static final BlockRegistryObject CES_SCREEN_1_ODD;
    public static final BlockRegistryObject CES_SCREEN_1_EVEN;

    public static final BlockRegistryObject DEWHURST_US89_BUTTON_1;
    public static final BlockRegistryObject DEWHURST_US91_BUTTON_1;
    public static final BlockRegistryObject DEWHURST_US91_BUTTON_1_BRAILLE;

    public static final BlockRegistryObject KONE_KDS330_BUTTON_1;
    public static final BlockRegistryObject KONE_KDS330_BUTTON_1_WITHOUT_SCREEN;
    public static final BlockRegistryObject KONE_KDS330_LANTERN_1;
    public static final BlockRegistryObject KONE_KSS280_BUTTON_1;
    public static final BlockRegistryObject KONE_KSS280_BUTTON_1_WITHOUT_SCREEN;
    public static final BlockRegistryObject KONE_KSS280_SCREEN_1_ODD;
    public static final BlockRegistryObject KONE_KSS280_SCREEN_1_EVEN;

    public static final BlockRegistryObject KONE_M_BUTTON_1;
    public static final BlockRegistryObject KONE_M_BUTTON_2;
    public static final BlockRegistryObject KONE_M_SCREEN_1_ODD;
    public static final BlockRegistryObject KONE_M_SCREEN_1_EVEN;

    public static final BlockRegistryObject OTIS_SERIES_1_BUTTON_1;
    public static final BlockRegistryObject OTIS_SERIES_1_BUTTON_2;
    public static final BlockRegistryObject OTIS_SERIES_1_SCREEN_1;
    public static final BlockRegistryObject OTIS_SERIES_3_BUTTON_1;
    public static final BlockRegistryObject OTIS_SERIES_3_SCREEN_1_ODD;
    public static final BlockRegistryObject OTIS_SERIES_3_SCREEN_1_EVEN;
    public static final BlockRegistryObject OTIS_SERIES_3_ELD_SCREEN_1_ODD;
    public static final BlockRegistryObject OTIS_SERIES_3_ELD_SCREEN_1_EVEN;
    public static final BlockRegistryObject OTIS_SERIES_3_LANTERN_1_ARROW_ODD;
    public static final BlockRegistryObject OTIS_SERIES_3_LANTERN_1_ARROW_EVEN;
    public static final BlockRegistryObject OTIS_SPEC_60_BUTTON_1;

    public static final BlockRegistryObject MITSUBISHI_GPS_BUTTON_1;
    public static final BlockRegistryObject MITSUBISHI_GPS_BUTTON_1_WITHOUT_SCREEN;
    public static final BlockRegistryObject MITSUBISHI_BUTTON_SHUN_HING_SQUARE;
    public static final BlockRegistryObject MITSUBISHI_RYODEN_SCREEN_1_EVEN;
    public static final BlockRegistryObject MITSUBISHI_RYODEN_SCREEN_1_ODD;
    public static final BlockRegistryObject MITSUBISHI_NEXWAY_BUTTON_1;
    public static final BlockRegistryObject MITSUBISHI_NEXWAY_BUTTON_1_SEGMENTED;
    public static final BlockRegistryObject MITSUBISHI_NEXWAY_BUTTON_1_WITHOUT_SCREEN;
    public static final BlockRegistryObject MITSUBISHI_NEXWAY_BUTTON_2;
    public static final BlockRegistryObject MITSUBISHI_NEXWAY_BUTTON_2_SEGMENTED;
    public static final BlockRegistryObject MITSUBISHI_NEXWAY_BUTTON_2_LCD_1;
    public static final BlockRegistryObject MITSUBISHI_NEXWAY_BUTTON_2_WITHOUT_SCREEN;
    public static final BlockRegistryObject MITSUBISHI_NEXWAY_BUTTON_3;
    public static final BlockRegistryObject MITSUBISHI_NEXWAY_BUTTON_3_SEGMENTED;
    public static final BlockRegistryObject MITSUBISHI_NEXWAY_BUTTON_3_WITHOUT_SCREEN;
    public static final BlockRegistryObject MITSUBISHI_NEXWAY_BUTTON_4;
    public static final BlockRegistryObject MITSUBISHI_NEXWAY_BUTTON_4_WITHOUT_SCREEN;
    public static final BlockRegistryObject MITSUBISHI_MAXIEZ_BUTTON_1_GOLD;
    public static final BlockRegistryObject MITSUBISHI_MAXIEZ_BUTTON_1_SILVER;
    public static final BlockRegistryObject MITSUBISHI_NEXWAY_LANTERN_1_HORIZONTAL_ODD;
    public static final BlockRegistryObject MITSUBISHI_NEXWAY_LANTERN_1_HORIZONTAL_EVEN;
    public static final BlockRegistryObject MITSUBISHI_NEXWAY_LANTERN_1_VERTICAL_ODD;
    public static final BlockRegistryObject MITSUBISHI_NEXWAY_LANTERN_1_VERTICAL_EVEN;
    public static final BlockRegistryObject MITSUBISHI_NEXWAY_SCREEN_1_EVEN;
    public static final BlockRegistryObject MITSUBISHI_NEXWAY_SCREEN_1_ODD;
    public static final BlockRegistryObject MITSUBISHI_NEXWAY_SCREEN_1_SEGMENTED_EVEN;
    public static final BlockRegistryObject MITSUBISHI_NEXWAY_SCREEN_1_SEGMENTED_ODD;
    public static final BlockRegistryObject MITSUBISHI_NEXWAY_SCREEN_2_EVEN;
    public static final BlockRegistryObject MITSUBISHI_NEXWAY_SCREEN_2_ODD;
    public static final BlockRegistryObject MITSUBISHI_NEXWAY_SCREEN_3_EVEN;
    public static final BlockRegistryObject MITSUBISHI_NEXWAY_SCREEN_3_ODD;
    public static final BlockRegistryObject MITSUBISHI_NEXWAY_SCREEN_3_SEGMENTED_EVEN;
    public static final BlockRegistryObject MITSUBISHI_NEXWAY_SCREEN_3_SEGMENTED_ODD;
    public static final BlockRegistryObject MITSUBISHI_MPVF_BUTTON_1;
    public static final BlockRegistryObject MITSUBISHI_MPVF_SCREEN_1_VERTICAL_ODD;
    public static final BlockRegistryObject MITSUBISHI_MPVF_SCREEN_1_VERTICAL_EVEN;

    public static final BlockRegistryObject SCHINDLER_D_SERIES_D2BUTTON;
    public static final BlockRegistryObject SCHINDLER_D_SERIES_SCREEN_1_ODD;
    public static final BlockRegistryObject SCHINDLER_D_SERIES_SCREEN_1_EVEN;
    public static final BlockRegistryObject SCHINDLER_D_SERIES_SCREEN_2_GREEN_EVEN;
    public static final BlockRegistryObject SCHINDLER_D_SERIES_SCREEN_2_BLUE_EVEN;
    public static final BlockRegistryObject SCHINDLER_D_SERIES_SCREEN_2_RED_EVEN;
    public static final BlockRegistryObject SCHINDLER_D_SERIES_SCREEN_2_GREEN_ODD;
    public static final BlockRegistryObject SCHINDLER_D_SERIES_SCREEN_2_BLUE_ODD;
    public static final BlockRegistryObject SCHINDLER_D_SERIES_SCREEN_2_RED_ODD;
    public static final BlockRegistryObject SCHINDLER_M_SERIES_BUTTON;
    public static final BlockRegistryObject SCHINDLER_M_SERIES_TOUCH_BUTTON;
    public static final BlockRegistryObject SCHINDLER_M_SERIES_ROUND_TOUCH_BUTTON;
    public static final BlockRegistryObject SCHINDLER_S_SERIES_GREY_BUTTON;
    public static final BlockRegistryObject SCHINDLER_M_SERIES_ROUND_LANTERN_1_ODD;
    public static final BlockRegistryObject SCHINDLER_M_SERIES_ROUND_LANTERN_1_EVEN;
    public static final BlockRegistryObject SCHINDLER_M_SERIES_SCREEN_1;
    public static final BlockRegistryObject SCHINDLER_M_SERIES_SCREEN_2_ODD;
    public static final BlockRegistryObject SCHINDLER_M_SERIES_SCREEN_2_EVEN;
    public static final BlockRegistryObject SCHINDLER_M_SERIES_SCREEN_3_ODD;
    public static final BlockRegistryObject SCHINDLER_M_SERIES_SCREEN_3_EVEN;
    public static final BlockRegistryObject SCHINDLER_M_SERIES_SCREEN_4_ODD;
    public static final BlockRegistryObject SCHINDLER_M_SERIES_SCREEN_4_EVEN;
    public static final BlockRegistryObject SCHINDLER_LINEA_BUTTON_1_WHITE;
    public static final BlockRegistryObject SCHINDLER_LINEA_BUTTON_1_WHITE_WITHOUT_SCREEN;
    public static final BlockRegistryObject SCHINDLER_LINEA_BUTTON_1_BLACK;
    public static final BlockRegistryObject SCHINDLER_LINEA_BUTTON_1_BLACK_WITHOUT_SCREEN;
    public static final BlockRegistryObject SCHINDLER_LINEA_BUTTON_2_WHITE;
    public static final BlockRegistryObject SCHINDLER_LINEA_BUTTON_2_WHITE_WITHOUT_SCREEN;
    public static final BlockRegistryObject SCHINDLER_LINEA_BUTTON_2_BLACK;
    public static final BlockRegistryObject SCHINDLER_LINEA_BUTTON_2_BLACK_WITHOUT_SCREEN;
    public static final BlockRegistryObject SCHINDLER_LINEA_SCREEN_1_WHITE_HORIZONTAL_ODD;
    public static final BlockRegistryObject SCHINDLER_LINEA_SCREEN_1_WHITE_HORIZONTAL_EVEN;
    public static final BlockRegistryObject SCHINDLER_LINEA_SCREEN_1_WHITE_VERTICAL_ODD;
    public static final BlockRegistryObject SCHINDLER_LINEA_SCREEN_1_WHITE_VERTICAL_EVEN;
    public static final BlockRegistryObject SCHINDLER_LINEA_SCREEN_1_BLACK_HORIZONTAL_ODD;
    public static final BlockRegistryObject SCHINDLER_LINEA_SCREEN_1_BLACK_HORIZONTAL_EVEN;
    public static final BlockRegistryObject SCHINDLER_LINEA_SCREEN_1_BLACK_VERTICAL_ODD;
    public static final BlockRegistryObject SCHINDLER_LINEA_SCREEN_1_BLACK_VERTICAL_EVEN;
    public static final BlockRegistryObject SCHINDLER_LINEA_SCREEN_2_WHITE_HORIZONTAL_ODD;
    public static final BlockRegistryObject SCHINDLER_LINEA_SCREEN_2_WHITE_HORIZONTAL_EVEN;
    public static final BlockRegistryObject SCHINDLER_LINEA_SCREEN_2_WHITE_VERTICAL_ODD;
    public static final BlockRegistryObject SCHINDLER_LINEA_SCREEN_2_WHITE_VERTICAL_EVEN;
    public static final BlockRegistryObject SCHINDLER_LINEA_SCREEN_2_BLACK_HORIZONTAL_ODD;
    public static final BlockRegistryObject SCHINDLER_LINEA_SCREEN_2_BLACK_HORIZONTAL_EVEN;
    public static final BlockRegistryObject SCHINDLER_LINEA_SCREEN_2_BLACK_VERTICAL_ODD;
    public static final BlockRegistryObject SCHINDLER_LINEA_SCREEN_2_BLACK_VERTICAL_EVEN;
    public static final BlockRegistryObject SCHINDLER_Z_LINE_3_KEYPAD_1;

    public static final BlockRegistryObject THYSSENKRUPP_TEGL1_BUTTON_1;

    public static final BlockRegistryObject HITACHI_B85_BUTTON_1;
    public static final BlockRegistryObject HITACHI_B85_BUTTON_1_WITHOUT_SCREEN;
    public static final BlockRegistryObject HITACHI_B85_BUTTON_2;

    public static final BlockRegistryObject HITACHI_BUTTON_PAFC;

    public static final BlockRegistryObject HITACHI_VIB320_BUTTON;
    public static final BlockRegistryObject HITACHI_VIB320_BUTTON_DOT_MATRIX;
    public static final BlockRegistryObject HITACHI_VIB322_BUTTON;
    public static final BlockRegistryObject HITACHI_VIB322_BUTTON_DOT_MATRIX;
    public static final BlockRegistryObject HITACHI_VIB221_BUTTON;
    public static final BlockRegistryObject HITACHI_VIB221_BUTTON_DOT_MATRIX;
    public static final BlockRegistryObject HITACHI_VIB820_BUTTON;
    public static final BlockRegistryObject HITACHI_VIB820_BUTTON_LCD;
    public static final BlockRegistryObject HITACHI_HB820_BUTTON;
    public static final BlockRegistryObject HITACHI_HSB820_BUTTON;
    public static final BlockRegistryObject HITACHI_VIB820PRO_BUTTON;
    public static final BlockRegistryObject HITACHI_HSB820PRO_BUTTON;
    public static final BlockRegistryObject HITACHI_GHD820PRO_SCREEN_ODD;
    public static final BlockRegistryObject HITACHI_GHD820PRO_SCREEN_EVEN;
    public static final BlockRegistryObject HITACHI_GHI675_SCREEN_ODD;
    public static final BlockRegistryObject HITACHI_GHI675_SCREEN_EVEN;
    public static final BlockRegistryObject HITACHI_VIB668_BUTTON;
    public static final BlockRegistryObject HITACHI_VIB658_BUTTON;
    public static final BlockRegistryObject HITACHI_VIB663_BUTTON;

    public static final BlockRegistryObject TONIC_DS_SCREEN_1_ODD;
    public static final BlockRegistryObject TONIC_DS_SCREEN_1_EVEN;
    public static final BlockRegistryObject TONIC_DM_SCREEN_1_RED_ODD;
    public static final BlockRegistryObject TONIC_DM_SCREEN_1_RED_EVEN;
    public static final BlockRegistryObject TONIC_DM_SCREEN_1_GREEN_ODD;
    public static final BlockRegistryObject TONIC_DM_SCREEN_1_GREEN_EVEN;
    public static final BlockRegistryObject TONIC_DM_SCREEN_1_YELLOW_ODD;
    public static final BlockRegistryObject TONIC_DM_SCREEN_1_YELLOW_EVEN;

    public static final BlockRegistryObject PAT_P01_TICKET_BARRIER_ENTRANCE;
    public static final BlockRegistryObject PAT_P01_TICKET_BARRIER_EXIT;
    public static final BlockRegistryObject PAT_P01_TICKET_BARRIER_BARE;
    public static final BlockRegistryObject PAT_TM01_TICKET_MACHINE;
    public static final BlockRegistryObject PAT_RS01_RAILWAY_SIGN_2_EVEN;
    public static final BlockRegistryObject PAT_RS01_RAILWAY_SIGN_2_ODD;
    public static final BlockRegistryObject PAT_RS01_RAILWAY_SIGN_3_EVEN;
    public static final BlockRegistryObject PAT_RS01_RAILWAY_SIGN_3_ODD;
    public static final BlockRegistryObject PAT_RS01_RAILWAY_SIGN_4_EVEN;
    public static final BlockRegistryObject PAT_RS01_RAILWAY_SIGN_4_ODD;
    public static final BlockRegistryObject PAT_RS01_RAILWAY_SIGN_5_EVEN;
    public static final BlockRegistryObject PAT_RS01_RAILWAY_SIGN_5_ODD;
    public static final BlockRegistryObject PAT_RS01_RAILWAY_SIGN_6_EVEN;
    public static final BlockRegistryObject PAT_RS01_RAILWAY_SIGN_6_ODD;
    public static final BlockRegistryObject PAT_RS01_RAILWAY_SIGN_7_EVEN;
    public static final BlockRegistryObject PAT_RS01_RAILWAY_SIGN_7_ODD;
    public static final BlockRegistryObject PAT_RS01_RAILWAY_SIGN_MIDDLE;
    public static final BlockRegistryObject PAT_RS01_RAILWAY_SIGN_POLE;

    public static final BlockRegistryObject LIFT_TRACK_EMPTY_FLOOR;

    public static final BlockRegistryObject HITACHI_B85_DOOR_1;
    public static final BlockRegistryObject KONE_M_DOOR_1;
    public static final BlockRegistryObject MITSUBISHI_NEXWAY_DOOR_1;
    public static final BlockRegistryObject OTIS_E411_US_DOOR_1;
    public static final BlockRegistryObject SCHINDLER_QKS9_DOOR_1;


    static {

        TEST_LIFT_BUTTONS = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "test_lift_buttons"), () -> new Block(new TestLiftButtons()));

        CES_SCREEN_1_ODD = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "ces_screen_1_odd"), () -> new Block(new CESScreen1Odd()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        CES_SCREEN_1_EVEN = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "ces_screen_1_even"), () -> new Block(new CESScreen1Even()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        DEWHURST_US89_BUTTON_1 = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "dewhurst_us89_button_1"), () -> new Block(new DewhurstUS89Button1()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        DEWHURST_US91_BUTTON_1 = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "dewhurst_us91_button_1"), () -> new Block(new DewhurstUS91Button1()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        DEWHURST_US91_BUTTON_1_BRAILLE = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "dewhurst_us91_button_1_braille"), () -> new Block(new DewhurstUS91Button1Braille()), CreativeModeTabs.YTE_LIFT_FIXTURES);

        KONE_KDS330_BUTTON_1 = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "kone_kds330_surface_button_1"), () -> new Block(new KoneKDS330Button1()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        KONE_KDS330_BUTTON_1_WITHOUT_SCREEN = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "kone_kds330_surface_button_1_without_screen"), () -> new Block(new KoneKDS330Button1WithoutScreen()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        KONE_KDS330_LANTERN_1 = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "kone_kds330_surface_lantern_1"), () -> new Block(new KoneKDS330Lantern1()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        KONE_KSS280_BUTTON_1 = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "kone_kss280_surface_button_1"), () -> new Block(new KoneKSS280Button1()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        KONE_KSS280_BUTTON_1_WITHOUT_SCREEN = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "kone_kss280_surface_button_1_without_screen"), () -> new Block(new KoneKSS280Button1WithoutScreen()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        KONE_KSS280_SCREEN_1_ODD = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "kone_kss280_surface_screen_1_odd"), () -> new Block(new KoneKSS280Screen1Odd()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        KONE_KSS280_SCREEN_1_EVEN = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "kone_kss280_surface_screen_1_even"), () -> new Block(new KoneKSS280Screen1Even()), CreativeModeTabs.YTE_LIFT_FIXTURES);

        KONE_M_BUTTON_1 = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "kone_m_button_1"), () -> new Block(new KoneMButton1()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        KONE_M_BUTTON_2 = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "kone_m_button_2"), () -> new Block(new KoneMButton2()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        KONE_M_SCREEN_1_ODD = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "kone_m_screen_1_odd"), () -> new Block(new KoneMScreen1Odd()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        KONE_M_SCREEN_1_EVEN = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "kone_m_screen_1_even"), () -> new Block(new KoneMScreen1Even()), CreativeModeTabs.YTE_LIFT_FIXTURES);

        MITSUBISHI_GPS_BUTTON_1 = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "mitsubishi_gps_button_1"), () -> new Block(new MitsubishiGPSButton1()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        MITSUBISHI_GPS_BUTTON_1_WITHOUT_SCREEN = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "mitsubishi_gps_button_1_without_screen"), () -> new Block(new MitsubishiGPSButton1WithoutScreen()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        MITSUBISHI_BUTTON_SHUN_HING_SQUARE = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "mitsubishi_button_shun_hing_plaza"), () -> new Block(new MitsubishiButtonShunHingSquare()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        MITSUBISHI_RYODEN_SCREEN_1_ODD = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "mitsubishi_ryoden_screen_1_odd"), () -> new Block(new MitsubishiRyodenScreen1Odd()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        MITSUBISHI_RYODEN_SCREEN_1_EVEN = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "mitsubishi_ryoden_screen_1_even"), () -> new Block(new MitsubishiRyodenScreen1Even()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        MITSUBISHI_NEXWAY_BUTTON_1 = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "mitsubishi_nexway_button_1"), () -> new Block(new MitsubishiNexWayButton1()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        MITSUBISHI_NEXWAY_BUTTON_1_SEGMENTED = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "mitsubishi_nexway_button_1_segmented"), () -> new Block(new MitsubishiNexWayButton1Segmented()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        MITSUBISHI_NEXWAY_BUTTON_1_WITHOUT_SCREEN = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "mitsubishi_nexway_button_1_without_screen"), () -> new Block(new MitsubishiNexWayButton1WithoutScreen()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        MITSUBISHI_NEXWAY_BUTTON_2 = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "mitsubishi_nexway_button_2"), () -> new Block(new MitsubishiNexWayButton2()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        MITSUBISHI_NEXWAY_BUTTON_2_SEGMENTED = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "mitsubishi_nexway_button_2_segmented"), () -> new Block(new MitsubishiNexWayButton2Segmented()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        MITSUBISHI_NEXWAY_BUTTON_2_LCD_1 = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "mitsubishi_nexway_button_2_lcd_1"), () -> new Block(new MitsubishiNexWayButton2LCD1()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        MITSUBISHI_NEXWAY_BUTTON_2_WITHOUT_SCREEN = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "mitsubishi_nexway_button_2_without_screen"), () -> new Block(new MitsubishiNexWayButton2WithoutScreen()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        MITSUBISHI_NEXWAY_BUTTON_3 = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "mitsubishi_nexway_button_3"), () -> new Block(new MitsubishiNexWayButton3()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        MITSUBISHI_NEXWAY_BUTTON_3_SEGMENTED = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "mitsubishi_nexway_button_3_segmented"), () -> new Block(new MitsubishiNexWayButton3Segmented()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        MITSUBISHI_NEXWAY_BUTTON_3_WITHOUT_SCREEN = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "mitsubishi_nexway_button_3_without_screen"), () -> new Block(new MitsubishiNexWayButton3WithoutScreen()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        MITSUBISHI_NEXWAY_BUTTON_4 = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "mitsubishi_nexway_button_4"), () -> new Block(new MitsubishiNexWayButton4()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        MITSUBISHI_NEXWAY_BUTTON_4_WITHOUT_SCREEN = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "mitsubishi_nexway_button_4_without_screen"), () -> new Block(new MitsubishiNexWayButton4WithoutScreen()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        MITSUBISHI_MAXIEZ_BUTTON_1_GOLD = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "mitsubishi_maxiez_button_1_gold"), () -> new Block(new MitsubishiMaxiezButton1Gold()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        MITSUBISHI_MAXIEZ_BUTTON_1_SILVER = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "mitsubishi_maxiez_button_1_silver"), () -> new Block(new MitsubishiMaxiezButton1Silver()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        MITSUBISHI_NEXWAY_LANTERN_1_HORIZONTAL_ODD = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "mitsubishi_nexway_lantern_1_horizontal_odd"), () -> new Block(new MitsubishiNexWayLantern1HorizontalOdd()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        MITSUBISHI_NEXWAY_LANTERN_1_HORIZONTAL_EVEN = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "mitsubishi_nexway_lantern_1_horizontal_even"), () -> new Block(new MitsubishiNexWayLantern1HorizontalEven()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        MITSUBISHI_NEXWAY_LANTERN_1_VERTICAL_ODD = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "mitsubishi_nexway_lantern_1_vertical_odd"), () -> new Block(new MitsubishiNexWayLantern1VerticalOdd()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        MITSUBISHI_NEXWAY_LANTERN_1_VERTICAL_EVEN = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "mitsubishi_nexway_lantern_1_vertical_even"), () -> new Block(new MitsubishiNexWayLantern1VerticalEven()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        MITSUBISHI_NEXWAY_SCREEN_1_EVEN = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "mitsubishi_nexway_screen_1_even"), () -> new Block(new MitsubishiNexWayScreen1Even()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        MITSUBISHI_NEXWAY_SCREEN_1_ODD = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "mitsubishi_nexway_screen_1_odd"), () -> new Block(new MitsubishiNexWayScreen1Odd()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        MITSUBISHI_NEXWAY_SCREEN_1_SEGMENTED_EVEN = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "mitsubishi_nexway_screen_1_segmented_even"), () -> new Block(new MitsubishiNexWayScreen1SegmentedEven()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        MITSUBISHI_NEXWAY_SCREEN_1_SEGMENTED_ODD = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "mitsubishi_nexway_screen_1_segmented_odd"), () -> new Block(new MitsubishiNexWayScreen1SegmentedOdd()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        MITSUBISHI_NEXWAY_SCREEN_2_EVEN = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "mitsubishi_nexway_screen_2_even"), () -> new Block(new MitsubishiNexWayScreen2Even()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        MITSUBISHI_NEXWAY_SCREEN_2_ODD = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "mitsubishi_nexway_screen_2_odd"), () -> new Block(new MitsubishiNexWayScreen2Odd()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        MITSUBISHI_NEXWAY_SCREEN_3_EVEN = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "mitsubishi_nexway_screen_3_even"), () -> new Block(new MitsubishiNexWayScreen3Even()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        MITSUBISHI_NEXWAY_SCREEN_3_ODD = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "mitsubishi_nexway_screen_3_odd"), () -> new Block(new MitsubishiNexWayScreen3Odd()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        MITSUBISHI_NEXWAY_SCREEN_3_SEGMENTED_EVEN = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "mitsubishi_nexway_screen_3_segmented_even"), () -> new Block(new MitsubishiNexWayScreen3SegmentedEven()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        MITSUBISHI_NEXWAY_SCREEN_3_SEGMENTED_ODD = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "mitsubishi_nexway_screen_3_segmented_odd"), () -> new Block(new MitsubishiNexWayScreen3SegmentedOdd()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        MITSUBISHI_MPVF_BUTTON_1 = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "mitsubishi_mpvf_button_1"), () -> new Block(new MitsubishiMPVFButton1()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        MITSUBISHI_MPVF_SCREEN_1_VERTICAL_ODD = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "mitsubishi_mpvf_screen_1_vertical_odd"), () -> new Block(new MitsubishiMPVFScreen1VerticalOdd()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        MITSUBISHI_MPVF_SCREEN_1_VERTICAL_EVEN = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "mitsubishi_mpvf_screen_1_vertical_even"), () -> new Block(new MitsubishiMPVFScreen1VerticalEven()), CreativeModeTabs.YTE_LIFT_FIXTURES);

        TEST_LIFT_HALL_LANTERNS = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "test_lift_hall_lanterns"), () -> new Block(new TestLiftHallLanterns()));
        TEST_LIFT_PANEL = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "test_lift_panel"), () -> new Block(new TestLiftPanel()));
        TEST_LIFT_BUTTONS_WITHOUT_SCREEN = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "test_lift_buttons_without_screen"), () -> new Block(new TestLiftButtonsWithoutScreen()));
        TEST_LIFT_DESTINATION_DISPATCH_TERMINAL = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "test_lift_destination_dispatch_terminal"), () -> new Block(new TestLiftDestinationDispatchTerminal()));

        THYSSENKRUPP_TEGL1_BUTTON_1 = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "thyssenkrupp_te_gl1_button_1"), () -> new Block(new ThyssenkruppTEGL1Button1()), CreativeModeTabs.YTE_LIFT_FIXTURES);

        OTIS_SERIES_1_BUTTON_1 = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "otis_series_1_button_1"), () -> new Block(new OtisSeries1Button()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        OTIS_SERIES_1_BUTTON_2 = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "otis_series_1_button_2"), () -> new Block(new OtisSeries1Button()));
        OTIS_SERIES_1_SCREEN_1 = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "otis_series_1_screen_1"), () -> new Block(new OtisSeries1Screen()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        OTIS_SERIES_3_BUTTON_1 = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "otis_series_3_button_1"), () -> new Block(new OtisSeries3Button1()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        OTIS_SERIES_3_SCREEN_1_ODD = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "otis_series_3_screen_1_odd"), () -> new Block(new OtisSeries3Screen1Odd()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        OTIS_SERIES_3_SCREEN_1_EVEN = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "otis_series_3_screen_1_even"), () -> new Block(new OtisSeries3Screen1Even()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        OTIS_SERIES_3_ELD_SCREEN_1_ODD = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "otis_series_3_eld_screen_1_odd"), () -> new Block(new OtisSeries3ELDScreen1Odd()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        OTIS_SERIES_3_ELD_SCREEN_1_EVEN = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "otis_series_3_eld_screen_1_even"), () -> new Block(new OtisSeries3ELDScreen1Even()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        OTIS_SERIES_3_LANTERN_1_ARROW_ODD = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "otis_series_3_lantern_1_arrow_odd"), () -> new Block(new OtisSeries3Lantern1ArrowOdd()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        OTIS_SERIES_3_LANTERN_1_ARROW_EVEN = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "otis_series_3_lantern_1_arrow_even"), () -> new Block(new OtisSeries3Lantern1ArrowEven()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        OTIS_SPEC_60_BUTTON_1 = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "otis_spec_60_button_1"), () -> new Block(new OtisSPEC60Button1()), CreativeModeTabs.YTE_LIFT_FIXTURES);

        SCHINDLER_D_SERIES_D2BUTTON = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "schindler_d_series_d2button"), () -> new Block(new SchindlerDSeriesD2Button()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        SCHINDLER_D_SERIES_SCREEN_1_ODD = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "schindler_d_series_screen_1_odd"), () -> new Block(new SchindlerDSeriesScreen1Odd()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        SCHINDLER_D_SERIES_SCREEN_1_EVEN = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "schindler_d_series_screen_1_even"), () -> new Block(new SchindlerDSeriesScreen1Even()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        SCHINDLER_D_SERIES_SCREEN_2_GREEN_EVEN = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "schindler_d_series_screen_2_green_even"), () -> new Block(new SchindlerDSeriesScreen2GreenEven()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        SCHINDLER_D_SERIES_SCREEN_2_GREEN_ODD = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "schindler_d_series_screen_2_green_odd"), () -> new Block(new SchindlerDSeriesScreen2GreenOdd()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        SCHINDLER_D_SERIES_SCREEN_2_BLUE_EVEN = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "schindler_d_series_screen_2_blue_even"), () -> new Block(new SchindlerDSeriesScreen2BlueEven()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        SCHINDLER_D_SERIES_SCREEN_2_BLUE_ODD = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "schindler_d_series_screen_2_blue_odd"), () -> new Block(new SchindlerDSeriesScreen2BlueOdd()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        SCHINDLER_D_SERIES_SCREEN_2_RED_EVEN = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "schindler_d_series_screen_2_red_even"), () -> new Block(new SchindlerDSeriesScreen2RedEven()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        SCHINDLER_D_SERIES_SCREEN_2_RED_ODD = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "schindler_d_series_screen_2_red_odd"), () -> new Block(new SchindlerDSeriesScreen2RedOdd()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        SCHINDLER_M_SERIES_BUTTON = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "schindler_m_series_button"), () -> new Block(new SchindlerMSeriesButton()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        SCHINDLER_M_SERIES_TOUCH_BUTTON = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "schindler_m_series_touch_button"), () -> new Block(new SchindlerMSeriesTouchButton()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        SCHINDLER_M_SERIES_ROUND_TOUCH_BUTTON = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "schindler_m_series_round_touch_button"), () -> new Block(new SchindlerMSeriesRoundTouchButton()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        SCHINDLER_M_SERIES_ROUND_LANTERN_1_ODD = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "schindler_m_series_round_lantern_1_odd"), () -> new Block(new SchindlerMSeriesRoundLantern1Odd()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        SCHINDLER_M_SERIES_ROUND_LANTERN_1_EVEN = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "schindler_m_series_round_lantern_1_even"), () -> new Block(new SchindlerMSeriesRoundLantern1Even()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        SCHINDLER_M_SERIES_SCREEN_1 = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "schindler_m_series_screen_1"), () -> new Block(new SchindlerMSeriesScreen1()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        SCHINDLER_M_SERIES_SCREEN_2_ODD = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "schindler_m_series_screen_2_odd"), () -> new Block(new SchindlerMSeriesScreen2Odd()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        SCHINDLER_M_SERIES_SCREEN_2_EVEN = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "schindler_m_series_screen_2_even"), () -> new Block(new SchindlerMSeriesScreen2Even()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        SCHINDLER_M_SERIES_SCREEN_3_ODD = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "schindler_m_series_screen_3_odd"), () -> new Block(new SchindlerMSeriesScreen3Odd()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        SCHINDLER_M_SERIES_SCREEN_3_EVEN = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "schindler_m_series_screen_3_even"), () -> new Block(new SchindlerMSeriesScreen3Even()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        SCHINDLER_M_SERIES_SCREEN_4_ODD = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "schindler_m_series_screen_4_odd"), () -> new Block(new SchindlerMSeriesScreen4Odd()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        SCHINDLER_M_SERIES_SCREEN_4_EVEN = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "schindler_m_series_screen_4_even"), () -> new Block(new SchindlerMSeriesScreen4Even()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        SCHINDLER_S_SERIES_GREY_BUTTON = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "schindler_s_series_grey_button"), () -> new Block(new SchindlerSSeriesGreyButton()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        SCHINDLER_LINEA_BUTTON_1_WHITE = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "schindler_linea_button_1_white"), () -> new Block(new SchindlerLineaButton1White()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        SCHINDLER_LINEA_BUTTON_1_WHITE_WITHOUT_SCREEN = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "schindler_linea_button_1_white_without_screen"), () -> new Block(new SchindlerLineaButton1WhiteWithoutScreen()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        SCHINDLER_LINEA_BUTTON_1_BLACK = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "schindler_linea_button_1_black"), () -> new Block(new SchindlerLineaButton1Black()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        SCHINDLER_LINEA_BUTTON_1_BLACK_WITHOUT_SCREEN = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "schindler_linea_button_1_black_without_screen"), () -> new Block(new SchindlerLineaButton1BlackWithoutScreen()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        SCHINDLER_LINEA_BUTTON_2_WHITE = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "schindler_linea_button_2_white"), () -> new Block(new SchindlerLineaButton2White()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        SCHINDLER_LINEA_BUTTON_2_WHITE_WITHOUT_SCREEN = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "schindler_linea_button_2_white_without_screen"), () -> new Block(new SchindlerLineaButton2WhiteWithoutScreen()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        SCHINDLER_LINEA_BUTTON_2_BLACK = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "schindler_linea_button_2_black"), () -> new Block(new SchindlerLineaButton2Black()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        SCHINDLER_LINEA_BUTTON_2_BLACK_WITHOUT_SCREEN = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "schindler_linea_button_2_black_without_screen"), () -> new Block(new SchindlerLineaButton2BlackWithoutScreen()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        SCHINDLER_LINEA_SCREEN_1_WHITE_HORIZONTAL_ODD = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "schindler_linea_screen_1_white_horizontal_odd"), () -> new Block(new SchindlerLineaScreen1WhiteHorizontalOdd()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        SCHINDLER_LINEA_SCREEN_1_WHITE_HORIZONTAL_EVEN = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "schindler_linea_screen_1_white_horizontal_even"), () -> new Block(new SchindlerLineaScreen1WhiteHorizontalEven()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        SCHINDLER_LINEA_SCREEN_1_WHITE_VERTICAL_ODD = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "schindler_linea_screen_1_white_vertical_odd"), () -> new Block(new SchindlerLineaScreen1WhiteVerticalOdd()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        SCHINDLER_LINEA_SCREEN_1_WHITE_VERTICAL_EVEN = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "schindler_linea_screen_1_white_vertical_even"), () -> new Block(new SchindlerLineaScreen1WhiteVerticalEven()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        SCHINDLER_LINEA_SCREEN_1_BLACK_HORIZONTAL_ODD = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "schindler_linea_screen_1_black_horizontal_odd"), () -> new Block(new SchindlerLineaScreen1BlackHorizontalOdd()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        SCHINDLER_LINEA_SCREEN_1_BLACK_HORIZONTAL_EVEN = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "schindler_linea_screen_1_black_horizontal_even"), () -> new Block(new SchindlerLineaScreen1BlackHorizontalEven()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        SCHINDLER_LINEA_SCREEN_1_BLACK_VERTICAL_ODD = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "schindler_linea_screen_1_black_vertical_odd"), () -> new Block(new SchindlerLineaScreen1BlackVerticalOdd()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        SCHINDLER_LINEA_SCREEN_1_BLACK_VERTICAL_EVEN = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "schindler_linea_screen_1_black_vertical_even"), () -> new Block(new SchindlerLineaScreen1BlackVerticalEven()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        SCHINDLER_LINEA_SCREEN_2_WHITE_HORIZONTAL_ODD = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "schindler_linea_screen_2_white_horizontal_odd"), () -> new Block(new SchindlerLineaScreen2WhiteHorizontalOdd()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        SCHINDLER_LINEA_SCREEN_2_WHITE_HORIZONTAL_EVEN = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "schindler_linea_screen_2_white_horizontal_even"), () -> new Block(new SchindlerLineaScreen2WhiteHorizontalEven()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        SCHINDLER_LINEA_SCREEN_2_WHITE_VERTICAL_ODD = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "schindler_linea_screen_2_white_vertical_odd"), () -> new Block(new SchindlerLineaScreen2WhiteVerticalOdd()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        SCHINDLER_LINEA_SCREEN_2_WHITE_VERTICAL_EVEN = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "schindler_linea_screen_2_white_vertical_even"), () -> new Block(new SchindlerLineaScreen2WhiteVerticalEven()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        SCHINDLER_LINEA_SCREEN_2_BLACK_HORIZONTAL_ODD = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "schindler_linea_screen_2_black_horizontal_odd"), () -> new Block(new SchindlerLineaScreen2BlackHorizontalOdd()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        SCHINDLER_LINEA_SCREEN_2_BLACK_HORIZONTAL_EVEN = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "schindler_linea_screen_2_black_horizontal_even"), () -> new Block(new SchindlerLineaScreen2BlackHorizontalEven()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        SCHINDLER_LINEA_SCREEN_2_BLACK_VERTICAL_ODD = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "schindler_linea_screen_2_black_vertical_odd"), () -> new Block(new SchindlerLineaScreen2BlackVerticalOdd()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        SCHINDLER_LINEA_SCREEN_2_BLACK_VERTICAL_EVEN = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "schindler_linea_screen_2_black_vertical_even"), () -> new Block(new SchindlerLineaScreen2BlackVerticalEven()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        SCHINDLER_Z_LINE_3_KEYPAD_1 = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "schindler_z_line_3_keypad_1"), () -> new Block(new SchindlerZLine3Keypad1()), CreativeModeTabs.YTE_LIFT_FIXTURES);

        HITACHI_VIB320_BUTTON = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "hitachi_vib320_button_1"), () -> new Block(new HitachiVIB320Button()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        HITACHI_VIB320_BUTTON_DOT_MATRIX = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "hitachi_vib320_button_1_dot_matrix"), () -> new Block(new HitachiVIB320ButtonDotMatrix()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        HITACHI_VIB322_BUTTON = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "hitachi_vib322_button_1"), () -> new Block(new HitachiVIB322Button()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        HITACHI_VIB322_BUTTON_DOT_MATRIX = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "hitachi_vib322_button_1_dot_matrix"), () -> new Block(new HitachiVIB322ButtonDotMatrix()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        HITACHI_VIB221_BUTTON = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "hitachi_vib221_button_1"), () -> new Block(new HitachiVIB221Button()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        HITACHI_VIB221_BUTTON_DOT_MATRIX = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "hitachi_vib221_button_1_dot_matrix"), () -> new Block(new HitachiVIB221ButtonDotMatrix()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        HITACHI_VIB820_BUTTON = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "hitachi_vib820_button_1"), () -> new Block(new HitachiVIB820Button()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        HITACHI_VIB820_BUTTON_LCD = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "hitachi_vib820_button_1_lcd"), () -> new Block(new HitachiVIB820ButtonLCD()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        HITACHI_HB820_BUTTON = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "hitachi_hb820_button_1"), () -> new Block(new HitachiHB820Button()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        HITACHI_HSB820_BUTTON = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "hitachi_hsb820_button_1"), () -> new Block(new HitachiHSB820Button()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        HITACHI_GHI675_SCREEN_ODD = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "hitachi_ghi675_screen_1_odd"), () -> new Block(new HitachiGHI675Screen1Odd()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        HITACHI_GHI675_SCREEN_EVEN = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "hitachi_ghi675_screen_1_even"), () -> new Block(new HitachiGHI675Screen1Even()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        HITACHI_VIB820PRO_BUTTON = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "hitachi_vib820pro_button_1"), () -> new Block(new HitachiVIB820proButton()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        HITACHI_HSB820PRO_BUTTON = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "hitachi_hsb820pro_button_1"), () -> new Block(new HitachiHSB820proButton()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        HITACHI_GHD820PRO_SCREEN_ODD = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "hitachi_ghd820pro_screen_1_odd"), () -> new Block(new HitachiGHD820proScreen1Odd()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        HITACHI_GHD820PRO_SCREEN_EVEN = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "hitachi_ghd820pro_screen_1_even"), () -> new Block(new HitachiGHD820proScreen1Even()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        HITACHI_VIB668_BUTTON = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "hitachi_vib668_button_1"), () -> new Block(new HitachiVIB668Button()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        HITACHI_VIB658_BUTTON = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "hitachi_vib658_button_1"), () -> new Block(new HitachiVIB658Button()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        HITACHI_VIB663_BUTTON = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "hitachi_vib663_button_1"), () -> new Block(new HitachiVIB663Button()), CreativeModeTabs.YTE_LIFT_FIXTURES);

        HITACHI_B85_BUTTON_1 = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "hitachi_b85_button_1"), () -> new Block(new HitachiB85Button1()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        HITACHI_B85_BUTTON_1_WITHOUT_SCREEN = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "hitachi_b85_button_1_without_screen"), () -> new Block(new HitachiB85Button1WithoutScreen()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        HITACHI_B85_BUTTON_2 = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "hitachi_b85_button_2"), () -> new Block(new HitachiB85Button2()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        HITACHI_BUTTON_PAFC = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "hitachi_button_pafc"), () -> new Block(new HitachiButtonPAFC()), CreativeModeTabs.YTE_LIFT_FIXTURES);

        TONIC_DS_SCREEN_1_ODD = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "tonic_ds_screen_1_odd"), () -> new Block(new TonicDSScreen1Odd()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        TONIC_DS_SCREEN_1_EVEN = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "tonic_ds_screen_1_even"), () -> new Block(new TonicDSScreen1Even()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        TONIC_DM_SCREEN_1_RED_ODD = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "tonic_dm_screen_1_red_odd"), () -> new Block(new TonicDMScreen1RedOdd()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        TONIC_DM_SCREEN_1_RED_EVEN = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "tonic_dm_screen_1_red_even"), () -> new Block(new TonicDMScreen1RedEven()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        TONIC_DM_SCREEN_1_GREEN_ODD = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "tonic_dm_screen_1_green_odd"), () -> new Block(new TonicDMScreen1GreenOdd()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        TONIC_DM_SCREEN_1_GREEN_EVEN = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "tonic_dm_screen_1_green_even"), () -> new Block(new TonicDMScreen1GreenEven()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        TONIC_DM_SCREEN_1_YELLOW_ODD = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "tonic_dm_screen_1_yellow_odd"), () -> new Block(new TonicDMScreen1YellowOdd()), CreativeModeTabs.YTE_LIFT_FIXTURES);
        TONIC_DM_SCREEN_1_YELLOW_EVEN = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "tonic_dm_screen_1_yellow_even"), () -> new Block(new TonicDMScreen1YellowEven()), CreativeModeTabs.YTE_LIFT_FIXTURES);

        PAT_P01_TICKET_BARRIER_ENTRANCE = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "pat_p01_ticket_barrier_entrance"), () -> new Block(new PATTicketBarrier(true)), CreativeModeTabs.YTE_RAILWAY_FACILITIES);
        PAT_P01_TICKET_BARRIER_EXIT = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "pat_p01_ticket_barrier_exit"), () -> new Block(new PATTicketBarrier(false)), CreativeModeTabs.YTE_RAILWAY_FACILITIES);
        PAT_P01_TICKET_BARRIER_BARE = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "pat_p01_ticket_barrier_bare"), () -> new Block(new PATTicketBarrierBareBlock(BlockHelper.createBlockSettings(false, true).strength(4.0f).nonOpaque())), CreativeModeTabs.YTE_RAILWAY_FACILITIES);
        PAT_TM01_TICKET_MACHINE = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "pat_tm01_ticket_machine"), () -> new Block(new BlockTicketMachine(BlockHelper.createBlockSettings(true, true, falseblockState -> 5))), CreativeModeTabs.YTE_RAILWAY_FACILITIES);
        PAT_RS01_RAILWAY_SIGN_2_EVEN = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "pat_rs01_railway_sign_2_even"), () -> new Block(new PATRS01RailwaySign(2, false)), CreativeModeTabs.YTE_RAILWAY_FACILITIES);
        PAT_RS01_RAILWAY_SIGN_2_ODD = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "pat_rs01_railway_sign_2_odd"), () -> new Block(new PATRS01RailwaySign(2, true)), CreativeModeTabs.YTE_RAILWAY_FACILITIES);
        PAT_RS01_RAILWAY_SIGN_3_EVEN = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "pat_rs01_railway_sign_3_even"), () -> new Block(new PATRS01RailwaySign(3, false)), CreativeModeTabs.YTE_RAILWAY_FACILITIES);
        PAT_RS01_RAILWAY_SIGN_3_ODD = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "pat_rs01_railway_sign_3_odd"), () -> new Block(new PATRS01RailwaySign(3, true)), CreativeModeTabs.YTE_RAILWAY_FACILITIES);
        PAT_RS01_RAILWAY_SIGN_4_EVEN = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "pat_rs01_railway_sign_4_even"), () -> new Block(new PATRS01RailwaySign(4, false)), CreativeModeTabs.YTE_RAILWAY_FACILITIES);
        PAT_RS01_RAILWAY_SIGN_4_ODD = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "pat_rs01_railway_sign_4_odd"), () -> new Block(new PATRS01RailwaySign(4, true)), CreativeModeTabs.YTE_RAILWAY_FACILITIES);
        PAT_RS01_RAILWAY_SIGN_5_EVEN = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "pat_rs01_railway_sign_5_even"), () -> new Block(new PATRS01RailwaySign(5, false)), CreativeModeTabs.YTE_RAILWAY_FACILITIES);
        PAT_RS01_RAILWAY_SIGN_5_ODD = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "pat_rs01_railway_sign_5_odd"), () -> new Block(new PATRS01RailwaySign(5, true)), CreativeModeTabs.YTE_RAILWAY_FACILITIES);
        PAT_RS01_RAILWAY_SIGN_6_EVEN = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "pat_rs01_railway_sign_6_even"), () -> new Block(new PATRS01RailwaySign(6, false)), CreativeModeTabs.YTE_RAILWAY_FACILITIES);
        PAT_RS01_RAILWAY_SIGN_6_ODD = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "pat_rs01_railway_sign_6_odd"), () -> new Block(new PATRS01RailwaySign(6, true)), CreativeModeTabs.YTE_RAILWAY_FACILITIES);
        PAT_RS01_RAILWAY_SIGN_7_EVEN = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "pat_rs01_railway_sign_7_even"), () -> new Block(new PATRS01RailwaySign(7, false)), CreativeModeTabs.YTE_RAILWAY_FACILITIES);
        PAT_RS01_RAILWAY_SIGN_7_ODD = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "pat_rs01_railway_sign_7_odd"), () -> new Block(new PATRS01RailwaySign(7, true)), CreativeModeTabs.YTE_RAILWAY_FACILITIES);
        PAT_RS01_RAILWAY_SIGN_MIDDLE = Init.REGISTRY.registerBlock(new Identifier(Init.MOD_ID, "pat_rs01_railway_sign_middle"), () -> new Block(new PATRS01RailwaySign(0, false)));
        PAT_RS01_RAILWAY_SIGN_POLE = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "pat_rs01_railway_sign_pole"), () -> new Block(new PATRS01RailwaySignPole(createDefaultBlockSettings(false))), CreativeModeTabs.YTE_RAILWAY_FACILITIES);

        LIFT_TRACK_EMPTY_FLOOR = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "lift_track_empty_floor"), () -> new Block(new EmptyFloor()));

        HITACHI_B85_DOOR_1 = Init.REGISTRY.registerBlock(new Identifier(Init.MOD_ID, "hitachi_b85_door_1"), () -> new Block(new HitachiB85Door1()));
        KONE_M_DOOR_1 = Init.REGISTRY.registerBlock(new Identifier(Init.MOD_ID, "kone_m_door_1"), () -> new Block(new KoneMDoor1()));
        MITSUBISHI_NEXWAY_DOOR_1 = Init.REGISTRY.registerBlock(new Identifier(Init.MOD_ID, "mitsubishi_nexway_door_1"), () -> new Block(new MitsubishiNexWayDoor1()));
        OTIS_E411_US_DOOR_1 = Init.REGISTRY.registerBlock(new Identifier(Init.MOD_ID, "otis_e411_us_door_1"), () -> new Block(new OtisE411USDoor1()));
        SCHINDLER_QKS9_DOOR_1 = Init.REGISTRY.registerBlock(new Identifier(Init.MOD_ID, "schindler_qks9_door_1"), () -> new Block(new SchindlerQKS9Door1()));
    }


    public static void init() {
        Init.LOGGER.info("正在注册方块");
    }
}
