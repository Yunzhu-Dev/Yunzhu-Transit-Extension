package top.xfunny.mod;

import org.mtr.mapping.holder.Block;
import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.registry.BlockRegistryObject;
import top.xfunny.mod.block.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 电梯配件注册中间件：收集配件方块，注册时自动排序。
 * 「电梯配件」标签页顺序 = 外呼 → 显示屏 → 到站灯，各分区内按品牌首字母排列。
 * 新增配件：在下方条目表任意位置加一行 add(...)，并在 Blocks.java 中声明字段
 * public static final BlockRegistryObject XXX = BlockFixtures.get("id"); 即可，无需关心顺序。
 * 新增品牌：在 BRAND_ORDER 的合适位置加入其注册 id 前缀。
 */
public final class BlockFixtures {

    /** 品牌顺序（按品牌名首字母升序） */
    private static final List<String> BRAND_ORDER = Arrays.asList(
            "ces", "dewhurst", "dover", "fujitec", "hitachi", "kone", "mitsubishi",
            "otis", "schindler", "shanghai_mitsubishi", "thyssenkrupp", "tke", "tonic", "toshiba");

    private static final List<Entry> ENTRIES = new ArrayList<>();
    private static final Map<String, BlockRegistryObject> REGISTERED = new HashMap<>();

    static {
        add("ces_screen_1_odd", () -> new Block(new CESScreen1Odd()));
        add("ces_screen_1_even", () -> new Block(new CESScreen1Even()));
        add("dewhurst_us81_button_1", () -> new Block(new DewhurstUS81Button1()));
        add("dewhurst_us89_button_1", () -> new Block(new DewhurstUS89Button1()));
        add("dewhurst_us91_button_1", () -> new Block(new DewhurstUS91Button1()));
        add("dewhurst_us91_button_1_braille", () -> new Block(new DewhurstUS91Button1Braille()));
        add("dover_impulse_button_1", () -> new Block(new DoverImpulseButton1()));
        add("dover_impulse_lantern_1_horizontal_odd", () -> new Block(new DoverImpulseLantern1HorizontalOdd()));
        add("dover_impulse_lantern_1_horizontal_even", () -> new Block(new DoverImpulseLantern1HorizontalEven()));
        add("fujitec_mic400_button_1", () -> new Block(new FujitecMIC400Button1()));
        add("fujitec_mic400_button_1_without_screen", () -> new Block(new FujitecMIC400Button1WithoutScreen()));
        add("fujitec_mic400_button_1_old", () -> new Block(new FujitecMIC400Button1Old()));
        add("fujitec_mic400_button_1_old_without_screen", () -> new Block(new FujitecMIC400Button1OldWithoutScreen()));
        add("fujitec_mic400_screen_1_horizontal_odd", () -> new Block(new FujitecMIC400Screen1HorizontalOdd()));
        add("fujitec_mic400_screen_1_horizontal_even", () -> new Block(new FujitecMIC400Screen1HorizontalEven()));
        add("fujitec_mic400_screen_1_vertical_odd", () -> new Block(new FujitecMIC400Screen1VerticalOdd()));
        add("fujitec_mic400_screen_1_vertical_even", () -> new Block(new FujitecMIC400Screen1VerticalEven()));
        add("kone_kds220_surface_button_1", () -> new Block(new KoneKDS220Button1()));
        add("kone_kds220_surface_button_1_without_screen", () -> new Block(new KoneKDS220Button1WithoutScreen()));
        add("kone_kds220_surface_screen_1_odd", () -> new Block(new KoneKDS220Screen1Odd()));
        add("kone_kds220_surface_screen_1_even", () -> new Block(new KoneKDS220Screen1Even()));
        add("kone_kds330_surface_button_1", () -> new Block(new KoneKDS330Button1()));
        add("kone_kds330_surface_button_1_without_screen", () -> new Block(new KoneKDS330Button1WithoutScreen()));
        add("kone_kds330_surface_button_1_touch", () -> new Block(new KoneKDS330Button1Touch()));
        add("kone_kds330_surface_button_1_touch_without_screen", () -> new Block(new KoneKDS330Button1TouchWithoutScreen()));
        add("kone_kds330_surface_lantern_1", () -> new Block(new KoneKDS330Lantern1()));
        add("kone_kds330_surface_screen_1_odd", () -> new Block(new KoneKDS330Screen1Odd()));
        add("kone_kds330_surface_screen_1_even", () -> new Block(new KoneKDS330Screen1Even()));
        add("kone_kds360_surface_button_1", () -> new Block(new KoneKDS360Button1()));
        add("kone_kds360_surface_button_1_display_top", () -> new Block(new KoneKDS360Button1DisplayTop()));
        add("kone_kds360_surface_button_1_without_screen", () -> new Block(new KoneKDS360Button1WithoutScreen()));
        add("kone_kss280_surface_button_1", () -> new Block(new KoneKSS280Button1()));
        add("kone_kss280_surface_button_1_without_screen", () -> new Block(new KoneKSS280Button1WithoutScreen()));
        add("kone_kss280_surface_screen_1_odd", () -> new Block(new KoneKSS280Screen1Odd()));
        add("kone_kss280_surface_screen_1_even", () -> new Block(new KoneKSS280Screen1Even()));
        add("kone_m_button_1", () -> new Block(new KoneMButton1()));
        add("kone_m_button_2", () -> new Block(new KoneMButton2()));
        add("kone_m_screen_1_odd", () -> new Block(new KoneMScreen1Odd()));
        add("kone_m_screen_1_even", () -> new Block(new KoneMScreen1Even()));
        add("kone_m_screen_1_small_odd", () -> new Block(new KoneMScreen1SmallOdd()));
        add("kone_m_screen_1_small_even", () -> new Block(new KoneMScreen1SmallEven()));
        add("mitsubishi_gps_button_1", () -> new Block(new MitsubishiGPSButton1()));
        add("mitsubishi_gps_button_1_without_screen", () -> new Block(new MitsubishiGPSButton1WithoutScreen()));
        add("mitsubishi_button_shun_hing_square", () -> new Block(new MitsubishiButtonShunHingSquare()));
        add("mitsubishi_ryoden_screen_1_odd", () -> new Block(new MitsubishiRyodenScreen1Odd()));
        add("mitsubishi_ryoden_screen_1_even", () -> new Block(new MitsubishiRyodenScreen1Even()));
        add("mitsubishi_ryoden_screen_2_odd", () -> new Block(new MitsubishiRyodenScreen2Odd()));
        add("mitsubishi_ryoden_screen_2_even", () -> new Block(new MitsubishiRyodenScreen2Even()));
        add("mitsubishi_gps3_button_1", () -> new Block(new MitsubishiGPS3Button1()));
        add("mitsubishi_gps3_button_1_without_screen", () -> new Block(new MitsubishiGPS3Button1WithoutScreen()));
        add("mitsubishi_gps3_button_2", () -> new Block(new MitsubishiGPS3Button2()));
        add("mitsubishi_gps3_button_2_without_screen", () -> new Block(new MitsubishiGPS3Button2WithoutScreen()));
        add("mitsubishi_gps3_screen_1_odd", () -> new Block(new MitsubishiGPS3Screen1Odd()));
        add("mitsubishi_gps3_screen_1_even", () -> new Block(new MitsubishiGPS3Screen1Even()));
        add("mitsubishi_nexway_button_1", () -> new Block(new MitsubishiNexWayButton1()));
        add("mitsubishi_nexway_button_1_segmented", () -> new Block(new MitsubishiNexWayButton1Segmented()));
        add("mitsubishi_nexway_button_1_without_screen", () -> new Block(new MitsubishiNexWayButton1WithoutScreen()));
        add("mitsubishi_nexway_button_2", () -> new Block(new MitsubishiNexWayButton2()));
        add("mitsubishi_nexway_button_2_segmented", () -> new Block(new MitsubishiNexWayButton2Segmented()));
        add("mitsubishi_nexway_button_2_lcd_1", () -> new Block(new MitsubishiNexWayButton2LCD1()));
        add("mitsubishi_nexway_button_2_without_screen", () -> new Block(new MitsubishiNexWayButton2WithoutScreen()));
        add("mitsubishi_nexway_button_3", () -> new Block(new MitsubishiNexWayButton3()));
        add("mitsubishi_nexway_button_3_segmented", () -> new Block(new MitsubishiNexWayButton3Segmented()));
        add("mitsubishi_nexway_button_3_without_screen", () -> new Block(new MitsubishiNexWayButton3WithoutScreen()));
        add("mitsubishi_nexway_button_4", () -> new Block(new MitsubishiNexWayButton4()));
        add("mitsubishi_nexway_button_4_without_screen", () -> new Block(new MitsubishiNexWayButton4WithoutScreen()));
        add("mitsubishi_nexway_button_5", () -> new Block(new MitsubishiNexWayButton5()));
        add("mitsubishi_nexway_button_5_without_screen", () -> new Block(new MitsubishiNexWayButton5WithoutScreen()));
        add("mitsubishi_button_sht", () -> new Block(new MitsubishiButtonSHT()));
        add("mitsubishi_maxiez_button_1_gold", () -> new Block(new MitsubishiMaxiezButton1Gold()));
        add("mitsubishi_maxiez_button_1_silver", () -> new Block(new MitsubishiMaxiezButton1Silver()));
        add("mitsubishi_maxiez_button_2", () -> new Block(new MitsubishiMaxiezButton2()));
        add("mitsubishi_maxiez_button_2_lcd", () -> new Block(new MitsubishiMaxiezButton2LCD()));
        add("mitsubishi_maxiez_button_2_without_screen", () -> new Block(new MitsubishiMaxiezButton2WithoutScreen()));
        add("mitsubishi_maxiez_button_3", () -> new Block(new MitsubishiMaxiezButton3()));
        add("mitsubishi_maxiez_button_3_lcd", () -> new Block(new MitsubishiMaxiezButton3LCD()));
        add("mitsubishi_maxiez_button_3_without_screen", () -> new Block(new MitsubishiMaxiezButton3WithoutScreen()));
        add("mitsubishi_nexway_lantern_1_horizontal_odd", () -> new Block(new MitsubishiNexWayLantern1HorizontalOdd()));
        add("mitsubishi_nexway_lantern_1_horizontal_even", () -> new Block(new MitsubishiNexWayLantern1HorizontalEven()));
        add("mitsubishi_nexway_lantern_1_vertical_odd", () -> new Block(new MitsubishiNexWayLantern1VerticalOdd()));
        add("mitsubishi_nexway_lantern_1_vertical_even", () -> new Block(new MitsubishiNexWayLantern1VerticalEven()));
        add("mitsubishi_nexway_lantern_2_horizontal_odd", () -> new Block(new MitsubishiNexWayLantern2HorizontalOdd()));
        add("mitsubishi_nexway_lantern_2_horizontal_even", () -> new Block(new MitsubishiNexWayLantern2HorizontalEven()));
        add("mitsubishi_nexway_lantern_2_vertical_odd", () -> new Block(new MitsubishiNexWayLantern2VerticalOdd()));
        add("mitsubishi_nexway_lantern_2_vertical_even", () -> new Block(new MitsubishiNexWayLantern2VerticalEven()));
        add("mitsubishi_nexway_lantern_3_odd", () -> new Block(new MitsubishiNexWayLantern3Odd()));
        add("mitsubishi_nexway_lantern_3_even", () -> new Block(new MitsubishiNexWayLantern3Even()));
        add("mitsubishi_nexway_screen_1_odd", () -> new Block(new MitsubishiNexWayScreen1Odd()));
        add("mitsubishi_nexway_screen_1_even", () -> new Block(new MitsubishiNexWayScreen1Even()));
        add("mitsubishi_nexway_screen_1_segmented_odd", () -> new Block(new MitsubishiNexWayScreen1SegmentedOdd()));
        add("mitsubishi_nexway_screen_1_segmented_even", () -> new Block(new MitsubishiNexWayScreen1SegmentedEven()));
        add("mitsubishi_nexway_screen_2_odd", () -> new Block(new MitsubishiNexWayScreen2Odd()));
        add("mitsubishi_nexway_screen_2_even", () -> new Block(new MitsubishiNexWayScreen2Even()));
        add("mitsubishi_nexway_screen_3_odd", () -> new Block(new MitsubishiNexWayScreen3Odd()));
        add("mitsubishi_nexway_screen_3_even", () -> new Block(new MitsubishiNexWayScreen3Even()));
        add("mitsubishi_nexway_screen_3_segmented_odd", () -> new Block(new MitsubishiNexWayScreen3SegmentedOdd()));
        add("mitsubishi_nexway_screen_3_segmented_even", () -> new Block(new MitsubishiNexWayScreen3SegmentedEven()));
        add("mitsubishi_nexway_screen_3_wide_odd", () -> new Block(new MitsubishiNexWayScreen3WideOdd()));
        add("mitsubishi_nexway_screen_3_wide_even", () -> new Block(new MitsubishiNexWayScreen3WideEven()));
        add("mitsubishi_nexway_screen_3_wide_segmented_odd", () -> new Block(new MitsubishiNexWayScreen3WideSegmentedOdd()));
        add("mitsubishi_nexway_screen_3_wide_segmented_even", () -> new Block(new MitsubishiNexWayScreen3WideSegmentedEven()));
        add("mitsubishi_maxiez_screen_1_odd", () -> new Block(new MitsubishiMaxiezScreen1Odd()));
        add("mitsubishi_maxiez_screen_1_even", () -> new Block(new MitsubishiMaxiezScreen1Even()));
        add("mitsubishi_maxiez_screen_2_odd", () -> new Block(new MitsubishiMaxiezScreen2Odd()));
        add("mitsubishi_maxiez_screen_2_even", () -> new Block(new MitsubishiMaxiezScreen2Even()));
        add("mitsubishi_mpvf_button_1", () -> new Block(new MitsubishiMPVFButton1()));
        add("mitsubishi_mpvf_screen_1_horizontal_odd", () -> new Block(new MitsubishiMPVFScreen1HorizontalOdd()));
        add("mitsubishi_mpvf_screen_1_horizontal_even", () -> new Block(new MitsubishiMPVFScreen1HorizontalEven()));
        add("mitsubishi_mpvf_screen_1_vertical_odd", () -> new Block(new MitsubishiMPVFScreen1VerticalOdd()));
        add("mitsubishi_mpvf_screen_1_vertical_even", () -> new Block(new MitsubishiMPVFScreen1VerticalEven()));
        add("mitsubishi_mpvf_lantern_1_horizontal_odd", () -> new Block(new MitsubishiMPVFLantern1HorizontalOdd()));
        add("mitsubishi_mpvf_lantern_1_horizontal_even", () -> new Block(new MitsubishiMPVFLantern1HorizontalEven()));
        add("mitsubishi_mpvf_lantern_1_vertical_odd", () -> new Block(new MitsubishiMPVFLantern1VerticalOdd()));
        add("mitsubishi_mpvf_lantern_1_vertical_even", () -> new Block(new MitsubishiMPVFLantern1VerticalEven()));
        add("mitsubishi_mp_button_1", () -> new Block(new MitsubishiMPButton1()));
        add("mitsubishi_mp_button_1_touch", () -> new Block(new MitsubishiMPButton1Touch()));
        add("shanghai_mitsubishi_nexway_cr_button_1", () -> new Block(new ShanghaiMitsubishiNexWayCRButton1()));
        add("shanghai_mitsubishi_lehy_3_button_1", () -> new Block(new ShanghaiMitsubishiLehy3Button1()));
        add("shanghai_mitsubishi_lehy_3_button_1_without_screen", () -> new Block(new ShanghaiMitsubishiLehy3Button1WithoutScreen()));
        add("shanghai_mitsubishi_lehy_3_button_2", () -> new Block(new ShanghaiMitsubishiLehy3Button2()));
        add("shanghai_mitsubishi_lehy_3_button_3_lcd", () -> new Block(new ShanghaiMitsubishiLehy3Button3LCD()));
        add("shanghai_mitsubishi_lehy_3_button_4", () -> new Block(new ShanghaiMitsubishiLehy3Button4()));
        add("shanghai_mitsubishi_lehy_3_screen_1_odd", () -> new Block(new ShanghaiMitsubishiLehy3Screen1Odd()));
        add("shanghai_mitsubishi_lehy_3_screen_1_even", () -> new Block(new ShanghaiMitsubishiLehy3Screen1Even()));
        add("shanghai_mitsubishi_lehy_3_screen_1_wide_odd", () -> new Block(new ShanghaiMitsubishiLehy3Screen1WideOdd()));
        add("shanghai_mitsubishi_lehy_3_screen_1_wide_even", () -> new Block(new ShanghaiMitsubishiLehy3Screen1WideEven()));
        add("otis_gen3_button_1", () -> new Block(new OtisGen3Button1()));
        add("otis_series_1_button_1", () -> new Block(new OtisSeries1Button()));
        add("otis_series_1_screen_1", () -> new Block(new OtisSeries1Screen()));
        add("otis_series_1_screen_1_even", () -> new Block(new OtisSeries1ScreenEven()));
        add("otis_series_1_screen_1_horizontal", () -> new Block(new OtisSeries1ScreenHorizontal()));
        add("otis_series_1_screen_1_horizontal_even", () -> new Block(new OtisSeries1ScreenHorizontalEven()));
        add("otis_series_1_lantern_1_even", () -> new Block(new OtisSeries1Lantern1Even()));
        add("otis_series_1_lantern_1_odd", () -> new Block(new OtisSeries1Lantern1Odd()));
        add("otis_series_1_lantern_1_horizontal_even", () -> new Block(new OtisSeries1Lantern1HorizontalEven()));
        add("otis_series_1_lantern_1_horizontal_odd", () -> new Block(new OtisSeries1Lantern1HorizontalOdd()));
        add("otis_series_1_lantern_screen_1_even", () -> new Block(new OtisSeries1LanternScreen1Even()));
        add("otis_series_1_lantern_screen_1_odd", () -> new Block(new OtisSeries1LanternScreen1Odd()));
        add("otis_series_1_lantern_screen_1_horizontal_even", () -> new Block(new OtisSeries1LanternScreen1HorizontalEven()));
        add("otis_series_1_lantern_screen_1_horizontal_odd", () -> new Block(new OtisSeries1LanternScreen1HorizontalOdd()));
        add("otis_series_1_button_2", () -> new Block(new OtisSeries1Button2()));
        add("otis_series_1_screen_2", () -> new Block(new OtisSeries1Screen2()));
        add("otis_series_1_screen_2_even", () -> new Block(new OtisSeries1Screen2Even()));
        add("otis_series_1_screen_2_horizontal", () -> new Block(new OtisSeries1Screen2Horizontal()));
        add("otis_series_1_screen_2_horizontal_even", () -> new Block(new OtisSeries1Screen2HorizontalEven()));
        add("otis_series_1_lantern_2_even", () -> new Block(new OtisSeries1Lantern2Even()));
        add("otis_series_1_lantern_2_odd", () -> new Block(new OtisSeries1Lantern2Odd()));
        add("otis_series_1_lantern_2_horizontal_even", () -> new Block(new OtisSeries1Lantern2HorizontalEven()));
        add("otis_series_1_lantern_2_horizontal_odd", () -> new Block(new OtisSeries1Lantern2HorizontalOdd()));
        add("otis_series_1_lantern_screen_2_even", () -> new Block(new OtisSeries1LanternScreen2Even()));
        add("otis_series_1_lantern_screen_2_odd", () -> new Block(new OtisSeries1LanternScreen2Odd()));
        add("otis_series_1_lantern_screen_2_horizontal_even", () -> new Block(new OtisSeries1LanternScreen2HorizontalEven()));
        add("otis_series_1_lantern_screen_2_horizontal_odd", () -> new Block(new OtisSeries1LanternScreen2HorizontalOdd()));
        add("otis_series_3_button_1", () -> new Block(new OtisSeries3Button1()));
        add("otis_series_3_screen_1_odd", () -> new Block(new OtisSeries3Screen1Odd()));
        add("otis_series_3_screen_1_even", () -> new Block(new OtisSeries3Screen1Even()));
        add("otis_series_3_eld_screen_1_odd", () -> new Block(new OtisSeries3ELDScreen1Odd()));
        add("otis_series_3_eld_screen_1_even", () -> new Block(new OtisSeries3ELDScreen1Even()));
        add("otis_series_3_lantern_1_arrow_odd", () -> new Block(new OtisSeries3Lantern1ArrowOdd()));
        add("otis_series_3_lantern_1_arrow_even", () -> new Block(new OtisSeries3Lantern1ArrowEven()));
        add("otis_spec_60_button_1", () -> new Block(new OtisSPEC60Button1()));
        add("otis_spec_90_button_1_black", () -> new Block(new OtisSPEC90Button1Black()));
        add("otis_spec_90_button_1_white", () -> new Block(new OtisSPEC90Button1White()));
        add("otis_spec_90_button_2_black", () -> new Block(new OtisSPEC90Button2Black()));
        add("otis_spec_90_button_2_white", () -> new Block(new OtisSPEC90Button2White()));
        add("schindler_d_series_d2button", () -> new Block(new SchindlerDSeriesD2Button()));
        add("schindler_d_series_screen_1_odd", () -> new Block(new SchindlerDSeriesScreen1Odd()));
        add("schindler_d_series_screen_1_even", () -> new Block(new SchindlerDSeriesScreen1Even()));
        add("schindler_d_series_screen_2_green_even", () -> new Block(new SchindlerDSeriesScreen2GreenEven()));
        add("schindler_d_series_screen_2_green_odd", () -> new Block(new SchindlerDSeriesScreen2GreenOdd()));
        add("schindler_d_series_screen_2_blue_even", () -> new Block(new SchindlerDSeriesScreen2BlueEven()));
        add("schindler_d_series_screen_2_blue_odd", () -> new Block(new SchindlerDSeriesScreen2BlueOdd()));
        add("schindler_d_series_screen_2_red_even", () -> new Block(new SchindlerDSeriesScreen2RedEven()));
        add("schindler_d_series_screen_2_red_odd", () -> new Block(new SchindlerDSeriesScreen2RedOdd()));
        add("schindler_m_series_button", () -> new Block(new SchindlerMSeriesButton()));
        add("schindler_m_series_touch_button", () -> new Block(new SchindlerMSeriesTouchButton()));
        add("schindler_m_series_round_touch_button", () -> new Block(new SchindlerMSeriesRoundTouchButton()));
        add("schindler_m_series_round_lantern_1_odd", () -> new Block(new SchindlerMSeriesRoundLantern1Odd()));
        add("schindler_m_series_round_lantern_1_even", () -> new Block(new SchindlerMSeriesRoundLantern1Even()));
        add("schindler_m_series_screen_1", () -> new Block(new SchindlerMSeriesScreen1()));
        add("schindler_m_series_screen_1_even", () -> new Block(new SchindlerMSeriesScreen1Even()));
        add("schindler_m_series_screen_2_odd", () -> new Block(new SchindlerMSeriesScreen2Odd()));
        add("schindler_m_series_screen_2_even", () -> new Block(new SchindlerMSeriesScreen2Even()));
        add("schindler_m_series_screen_3_odd", () -> new Block(new SchindlerMSeriesScreen3Odd()));
        add("schindler_m_series_screen_3_even", () -> new Block(new SchindlerMSeriesScreen3Even()));
        add("schindler_m_series_screen_4_odd", () -> new Block(new SchindlerMSeriesScreen4Odd()));
        add("schindler_m_series_screen_4_even", () -> new Block(new SchindlerMSeriesScreen4Even()));
        add("schindler_m_series_screen_5_odd", () -> new Block(new SchindlerMSeriesScreen5Odd()));
        add("schindler_m_series_screen_5_even", () -> new Block(new SchindlerMSeriesScreen5Even()));
        add("schindler_s_series_grey_button", () -> new Block(new SchindlerSSeriesGreyButton()));
        add("schindler_s_series_dark_grey_button", () -> new Block(new SchindlerSSeriesDarkGreyButton()));
        add("schindler_s_series_blue_button", () -> new Block(new SchindlerSSeriesBlueButton()));
        add("schindler_r_series_round_button", () -> new Block(new SchindlerRSeriesRoundButton()));
        add("schindler_r_series_screen_1_odd", () -> new Block(new SchindlerRSeriesScreen1Odd()));
        add("schindler_r_series_screen_1_even", () -> new Block(new SchindlerRSeriesScreen1Even()));
        add("schindler_linea_button_1_white", () -> new Block(new SchindlerLineaButton1White()));
        add("schindler_linea_button_1_white_without_screen", () -> new Block(new SchindlerLineaButton1WhiteWithoutScreen()));
        add("schindler_linea_button_1_black", () -> new Block(new SchindlerLineaButton1Black()));
        add("schindler_linea_button_1_black_without_screen", () -> new Block(new SchindlerLineaButton1BlackWithoutScreen()));
        add("schindler_linea_button_2_white", () -> new Block(new SchindlerLineaButton2White()));
        add("schindler_linea_button_2_white_without_screen", () -> new Block(new SchindlerLineaButton2WhiteWithoutScreen()));
        add("schindler_linea_button_2_black", () -> new Block(new SchindlerLineaButton2Black()));
        add("schindler_linea_button_2_black_without_screen", () -> new Block(new SchindlerLineaButton2BlackWithoutScreen()));
        add("schindler_linea_screen_1_white_horizontal_odd", () -> new Block(new SchindlerLineaScreen1WhiteHorizontalOdd()));
        add("schindler_linea_screen_1_white_horizontal_even", () -> new Block(new SchindlerLineaScreen1WhiteHorizontalEven()));
        add("schindler_linea_screen_1_white_vertical_odd", () -> new Block(new SchindlerLineaScreen1WhiteVerticalOdd()));
        add("schindler_linea_screen_1_white_vertical_even", () -> new Block(new SchindlerLineaScreen1WhiteVerticalEven()));
        add("schindler_linea_screen_1_black_horizontal_odd", () -> new Block(new SchindlerLineaScreen1BlackHorizontalOdd()));
        add("schindler_linea_screen_1_black_horizontal_even", () -> new Block(new SchindlerLineaScreen1BlackHorizontalEven()));
        add("schindler_linea_screen_1_black_vertical_odd", () -> new Block(new SchindlerLineaScreen1BlackVerticalOdd()));
        add("schindler_linea_screen_1_black_vertical_even", () -> new Block(new SchindlerLineaScreen1BlackVerticalEven()));
        add("schindler_linea_screen_2_white_horizontal_odd", () -> new Block(new SchindlerLineaScreen2WhiteHorizontalOdd()));
        add("schindler_linea_screen_2_white_horizontal_even", () -> new Block(new SchindlerLineaScreen2WhiteHorizontalEven()));
        add("schindler_linea_screen_2_white_vertical_odd", () -> new Block(new SchindlerLineaScreen2WhiteVerticalOdd()));
        add("schindler_linea_screen_2_white_vertical_even", () -> new Block(new SchindlerLineaScreen2WhiteVerticalEven()));
        add("schindler_linea_screen_2_black_horizontal_odd", () -> new Block(new SchindlerLineaScreen2BlackHorizontalOdd()));
        add("schindler_linea_screen_2_black_horizontal_even", () -> new Block(new SchindlerLineaScreen2BlackHorizontalEven()));
        add("schindler_linea_screen_2_black_vertical_odd", () -> new Block(new SchindlerLineaScreen2BlackVerticalOdd()));
        add("schindler_linea_screen_2_black_vertical_even", () -> new Block(new SchindlerLineaScreen2BlackVerticalEven()));
        add("schindler_fi_gs_button_1", () -> new Block(new SchindlerFIGSButton1()));
        add("schindler_fi_gs_touch_button_1", () -> new Block(new SchindlerFIGSTouchButton1()));
        add("schindler_fi_gs_touch_button_1_without_screen", () -> new Block(new SchindlerFIGSTouchButton1WithoutScreen()));
        add("schindler_fi_gs_button_1_steel", () -> new Block(new SchindlerFIGSButton1Steel()));
        add("schindler_fi_gs_button_1_without_screen", () -> new Block(new SchindlerFIGSButton1WithoutScreen()));
        add("schindler_fi_gs_screen_1_steel_odd", () -> new Block(new SchindlerFIGSScreen1SteelOdd()));
        add("schindler_fi_gs_screen_1_steel_even", () -> new Block(new SchindlerFIGSScreen1SteelEven()));
        add("schindler_fi_gs_screen_1_black_odd", () -> new Block(new SchindlerFIGSScreen1BlackOdd()));
        add("schindler_fi_gs_screen_1_black_even", () -> new Block(new SchindlerFIGSScreen1BlackEven()));
        add("schindler_fi_gs_screen_1_grey_odd", () -> new Block(new SchindlerFIGSScreen1GreyOdd()));
        add("schindler_fi_gs_screen_1_grey_even", () -> new Block(new SchindlerFIGSScreen1GreyEven()));
        add("schindler_z_line_3_keypad_1", () -> new Block(new SchindlerZLine3Keypad1()));
        add("hitachi_vib320_button_1", () -> new Block(new HitachiVIB320Button()));
        add("hitachi_vib320_button_1_dot_matrix", () -> new Block(new HitachiVIB320ButtonDotMatrix()));
        add("hitachi_vib320_button_1_hip43", () -> new Block(new HitachiVIB320ButtonHIP43()));
        add("hitachi_vib322_button_1", () -> new Block(new HitachiVIB322Button()));
        add("hitachi_vib322_button_1_dot_matrix", () -> new Block(new HitachiVIB322ButtonDotMatrix()));
        add("hitachi_vib325_button_1", () -> new Block(new HitachiVIB325Button()));
        add("hitachi_vib325_button_1_dot_matrix", () -> new Block(new HitachiVIB325ButtonDotMatrix()));
        add("hitachi_vib221_button_1", () -> new Block(new HitachiVIB221Button()));
        add("hitachi_vib221_button_1_dot_matrix", () -> new Block(new HitachiVIB221ButtonDotMatrix()));
        add("hitachi_vib221_button_1_lcd_segmented", () -> new Block(new HitachiVIB221ButtonLCDSegmented()));
        add("hitachi_vib221_button_1_hip43", () -> new Block(new HitachiVIB221ButtonHIP43()));
        add("hitachi_vib820_button_1", () -> new Block(new HitachiVIB820Button()));
        add("hitachi_vib820_button_1_lcd", () -> new Block(new HitachiVIB820ButtonLCD()));
        add("hitachi_vib68_button_1", () -> new Block(new HitachiVIB68Button()));
        add("hitachi_vib191_button_1", () -> new Block(new HitachiVIB191Button()));
        add("hitachi_vib192_button_1", () -> new Block(new HitachiVIB192Button()));
        add("hitachi_hb820_button_1", () -> new Block(new HitachiHB820Button()));
        add("hitachi_hsb820_button_1", () -> new Block(new HitachiHSB820Button()));
        add("hitachi_ghl820_lantern_1_odd", () -> new Block(new HitachiGHL820Lantern1Odd()));
        add("hitachi_ghl820_lantern_1_even", () -> new Block(new HitachiGHL820Lantern1Even()));
        add("hitachi_ghl668_lantern_1_odd", () -> new Block(new HitachiGHL668Lantern1Odd()));
        add("hitachi_ghl668_lantern_1_even", () -> new Block(new HitachiGHL668Lantern1Even()));
        add("hitachi_ghl673_lantern_1_odd", () -> new Block(new HitachiGHL673Lantern1Odd()));
        add("hitachi_ghl673_lantern_1_even", () -> new Block(new HitachiGHL673Lantern1Even()));
        add("hitachi_ghi675_screen_1_odd", () -> new Block(new HitachiGHI675Screen1Odd()));
        add("hitachi_ghi675_screen_1_even", () -> new Block(new HitachiGHI675Screen1Even()));
        add("hitachi_vib820pro_button_1", () -> new Block(new HitachiVIB820proButton()));
        add("hitachi_hsb820pro_button_1", () -> new Block(new HitachiHSB820proButton()));
        add("hitachi_ghd820pro_screen_1_odd", () -> new Block(new HitachiGHD820proScreen1Odd()));
        add("hitachi_ghd820pro_screen_1_even", () -> new Block(new HitachiGHD820proScreen1Even()));
        add("hitachi_vib628_button_1", () -> new Block(new HitachiVIB628Button()));
        add("hitachi_hb628_button_1", () -> new Block(new HitachiHB628Button()));
        add("hitachi_vib668_button_1", () -> new Block(new HitachiVIB668Button()));
        add("hitachi_vib658_button_1", () -> new Block(new HitachiVIB658Button()));
        add("hitachi_hb658_button_1", () -> new Block(new HitachiHB658Button()));
        add("hitachi_vib663_button_1", () -> new Block(new HitachiVIB663Button()));
        add("hitachi_vib681_button_1", () -> new Block(new HitachiVIB681Button()));
        add("hitachi_vib676_button_1", () -> new Block(new HitachiVIB676Button()));
        add("hitachi_vib679_button_1", () -> new Block(new HitachiVIB679Button()));
        add("hitachi_vib673_button_1", () -> new Block(new HitachiVIB673Button()));
        add("hitachi_vib673_button_1_hip43", () -> new Block(new HitachiVIB673ButtonHIP43()));
        add("hitachi_hb673_button_1", () -> new Block(new HitachiHB673Button()));
        add("hitachi_vib181a_button_1", () -> new Block(new HitachiVIB181AButton()));
        add("hitachi_vib182a_button_1", () -> new Block(new HitachiVIB182AButton()));
        add("hitachi_hb181a_button_1", () -> new Block(new HitachiHB181AButton()));
        add("hitachi_b85_button_1", () -> new Block(new HitachiB85Button1()));
        add("hitachi_b85_button_1_without_screen", () -> new Block(new HitachiB85Button1WithoutScreen()));
        add("hitachi_b85_button_2", () -> new Block(new HitachiB85Button2()));
        add("hitachi_b85_screen_1_vertical_odd", () -> new Block(new HitachiB85Screen1VerticalOdd()));
        add("hitachi_b85_screen_1_vertical_even", () -> new Block(new HitachiB85Screen1VerticalEven()));
        add("hitachi_b85_screen_1_horizontal_odd", () -> new Block(new HitachiB85Screen1HorizontalOdd()));
        add("hitachi_b85_screen_1_horizontal_even", () -> new Block(new HitachiB85Screen1HorizontalEven()));
        add("hitachi_b89_button_1", () -> new Block(new HitachiB89Button1()));
        add("hitachi_b89_button_1_without_screen", () -> new Block(new HitachiB89Button1WithoutScreen()));
        add("hitachi_b89_button_2", () -> new Block(new HitachiB89Button2()));
        add("hitachi_b89_screen_1_vertical_odd", () -> new Block(new HitachiB89Screen1VerticalOdd()));
        add("hitachi_b89_screen_1_vertical_even", () -> new Block(new HitachiB89Screen1VerticalEven()));
        add("hitachi_b89_screen_1_horizontal_odd", () -> new Block(new HitachiB89Screen1HorizontalOdd()));
        add("hitachi_b89_screen_1_horizontal_even", () -> new Block(new HitachiB89Screen1HorizontalEven()));
        add("hitachi_button_pafc", () -> new Block(new HitachiButtonPAFC()));
        add("tke_ms5e_button_1", () -> new Block(new TKEMS5EButton1()));
        add("thyssenkrupp_ms5e_button_1", () -> new Block(new ThyssenKruppMS5EButton1()));
        add("thyssenkrupp_ms3e_button_1", () -> new Block(new ThyssenKruppMS3EButton1()));
        add("thyssenkrupp_al_c01_button_1", () -> new Block(new ThyssenKruppALC01Button1()));
        add("thyssenkrupp_s001_button_1", () -> new Block(new ThyssenKruppS001Button1()));
        add("thyssenkrupp_s001_button_1_without_screen", () -> new Block(new ThyssenKruppS001Button1WithoutScreen()));
        add("thyssenkrupp_sf000_screen_1_odd", () -> new Block(new ThyssenKruppSF000Screen1Odd()));
        add("thyssenkrupp_sf000_screen_1_even", () -> new Block(new ThyssenKruppSF000Screen1Even()));
        add("tonic_ds_screen_1_odd", () -> new Block(new TonicDSScreen1Odd()));
        add("tonic_ds_screen_1_even", () -> new Block(new TonicDSScreen1Even()));
        add("tonic_dm_screen_1_red_odd", () -> new Block(new TonicDMScreen1RedOdd()));
        add("tonic_dm_screen_1_red_even", () -> new Block(new TonicDMScreen1RedEven()));
        add("tonic_dm_screen_1_green_odd", () -> new Block(new TonicDMScreen1GreenOdd()));
        add("tonic_dm_screen_1_green_even", () -> new Block(new TonicDMScreen1GreenEven()));
        add("tonic_dm_screen_1_yellow_odd", () -> new Block(new TonicDMScreen1YellowOdd()));
        add("tonic_dm_screen_1_yellow_even", () -> new Block(new TonicDMScreen1YellowEven()));
        add("toshiba_button_1", () -> new Block(new ToshibaButton1()));
        add("toshiba_lantern_1_odd", () -> new Block(new ToshibaLantern1Odd()));
        add("toshiba_lantern_1_even", () -> new Block(new ToshibaLantern1Even()));
        add("toshiba_screen_1_odd", () -> new Block(new ToshibaScreen1Odd()));
        add("toshiba_screen_1_even", () -> new Block(new ToshibaScreen1Even()));
        add("toshiba_screen_2_odd", () -> new Block(new ToshibaScreen2Odd()));
        add("toshiba_screen_2_even", () -> new Block(new ToshibaScreen2Even()));
        commit(); // 在本条前添加 add(...)
    }

    private static void add(String id, Supplier<Block> constructor) {
        ENTRIES.add(new Entry(id, constructor, ENTRIES.size()));
    }

    /** 按排序规则注册全部配件，并建立 id → 注册结果的索引。 */
    private static void commit() {
        ENTRIES.sort(Comparator.comparingInt((Entry entry) -> category(entry.id))
                .thenComparingInt(entry -> brand(entry.id))
                .thenComparingInt(entry -> entry.seq));
        for (Entry entry : ENTRIES) {
            REGISTERED.put(entry.id, Init.REGISTRY.registerBlockWithBlockItem(
                    new Identifier(Init.MOD_ID, entry.id), entry.constructor, CreativeModeTabs.YTE_LIFT_FIXTURES));
        }
    }

    public static BlockRegistryObject get(String id) {
        final BlockRegistryObject block = REGISTERED.get(id);
        if (block == null) {
            throw new IllegalStateException("Unknown lift fixture id: " + id);
        }
        return block;
    }

    /**
     * 分区：0=外呼 1=显示屏 2=到站灯。
     * lantern_screen 组合件按 lang 名称（到站灯，带屏幕）归入到站灯；
     * without_screen 变体仍是外呼按钮，故 button 先于 screen 判断。
     */
    private static int category(String id) {
        if (id.contains("lantern")) {
            return 2;
        }
        if (id.contains("button") || id.contains("keypad")) {
            return 0;
        }
        if (id.contains("screen")) {
            return 1;
        }
        throw new IllegalStateException("Unclassified lift fixture id: " + id);
    }

    /** 品牌序号：取 BRAND_ORDER 中最长匹配前缀；未命中启动即报错，避免静默错序。 */
    private static int brand(String id) {
        int best = -1;
        int bestLength = -1;
        for (int i = 0; i < BRAND_ORDER.size(); i++) {
            final String prefix = BRAND_ORDER.get(i);
            if (id.startsWith(prefix + "_") && prefix.length() > bestLength) {
                best = i;
                bestLength = prefix.length();
            }
        }
        if (best < 0) {
            throw new IllegalStateException("Unknown lift fixture brand: " + id);
        }
        return best;
    }

    private static final class Entry {
        final String id;
        final Supplier<Block> constructor;
        final int seq;

        Entry(String id, Supplier<Block> constructor, int seq) {
            this.id = id;
            this.constructor = constructor;
            this.seq = seq;
        }
    }
}
