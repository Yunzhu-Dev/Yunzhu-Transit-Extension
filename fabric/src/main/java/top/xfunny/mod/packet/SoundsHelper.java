package top.xfunny.mod.packet;

import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.ServerPlayerEntity;
import org.mtr.mapping.holder.SoundCategory;
import top.xfunny.mod.SoundEvents;

public final class SoundsHelper {
    public static void playSound(BlockPos blockPos, ServerPlayerEntity serverPlayerEntity, String soundInstruction) {
        switch (soundInstruction) {// 播放声音
            case "hitachi_ca_lantern_1":
                serverPlayerEntity.getEntityWorld().playSound(
                        null,
                        blockPos,
                        SoundEvents.HITACHI_CA_LANTERN_1.get(),
                        SoundCategory.BLOCKS,
                        1.0F,
                        1.0F
                );
                break;
            case "hitachi_ca_lantern_2":
                serverPlayerEntity.getEntityWorld().playSound(
                        null,
                        blockPos,
                        SoundEvents.HITACHI_CA_LANTERN_2.get(),
                        SoundCategory.BLOCKS,
                        1.0F,
                        1.0F
                );
                break;
            case "otis_series_1_lantern_up":
                serverPlayerEntity.getEntityWorld().playSound(
                        null,
                        blockPos,
                        SoundEvents.OTIS_SERIES_1_LANTERN_1_UP.get(),
                        SoundCategory.BLOCKS,
                        1.0F,
                        1.0F
                );
                break;

            case "otis_series_1_lantern_down":
                serverPlayerEntity.getEntityWorld().playSound(
                        null,
                        blockPos,
                        SoundEvents.OTIS_SERIES_1_LANTERN_1_DOWN.get(),
                        SoundCategory.BLOCKS,
                        1.0F,
                        1.0F
                );
                break;
            case "otis_series_1_lantern_up_2":
                serverPlayerEntity.getEntityWorld().playSound(
                        null,
                        blockPos,
                        SoundEvents.OTIS_SERIES_1_LANTERN_2_UP.get(),
                        SoundCategory.BLOCKS,
                        1.0F,
                        1.0F
                );
                break;

            case "otis_series_1_lantern_down_2":
                serverPlayerEntity.getEntityWorld().playSound(
                        null,
                        blockPos,
                        SoundEvents.OTIS_SERIES_1_LANTERN_2_DOWN.get(),
                        SoundCategory.BLOCKS,
                        1.0F,
                        1.0F
                );
                break;
            case "otis_series_1_button_1":
                serverPlayerEntity.getEntityWorld().playSound(
                        null,
                        blockPos,
                        SoundEvents.OTIS_SERIES_1_BUTTON_1.get(),
                        SoundCategory.BLOCKS,
                        1.0F,
                        1.0F
                );
                break;
            case "otis_series_3_lantern_up":
                serverPlayerEntity.getEntityWorld().playSound(
                        null,
                        blockPos,
                        SoundEvents.OTIS_SERIES_3_LANTERN_1_UP.get(),
                        SoundCategory.BLOCKS,
                        1.0F,
                        1.0F
                );
                break;

            case "otis_series_3_lantern_down":
                serverPlayerEntity.getEntityWorld().playSound(
                        null,
                        blockPos,
                        SoundEvents.OTIS_SERIES_3_LANTERN_1_DOWN.get(),
                        SoundCategory.BLOCKS,
                        1.0F,
                        1.0F
                );
                break;
            case "otis_series_3_button_1":
                serverPlayerEntity.getEntityWorld().playSound(
                        null,
                        blockPos,
                        SoundEvents.OTIS_SERIES_3_BUTTON_1.get(),
                        SoundCategory.BLOCKS,
                        1.0F,
                        1.0F
                );
                break;
            case "schindler_d_series_button_1":
                serverPlayerEntity.getEntityWorld().playSound(
                        null,
                        blockPos,
                        SoundEvents.SCHINDLER_D_SERIES_BUTTON_1.get(),
                        SoundCategory.BLOCKS,
                        1.0F,
                        1.0F
                );
                break;
            case "schindler_m_series_lantern_1":
                serverPlayerEntity.getEntityWorld().playSound(
                        null,
                        blockPos,
                        SoundEvents.SCHINDLER_M_SERIES_LANTERN_1.get(),
                        SoundCategory.BLOCKS,
                        1.0F,
                        1.0F
                );
                break;
            case "schindler_m_series_lantern_2":
                serverPlayerEntity.getEntityWorld().playSound(
                        null,
                        blockPos,
                        SoundEvents.SCHINDLER_M_SERIES_LANTERN_2.get(),
                        SoundCategory.BLOCKS,
                        1.0F,
                        1.0F
                );
                break;
            case "schindler_m_series_button_1":
                serverPlayerEntity.getEntityWorld().playSound(
                        null,
                        blockPos,
                        SoundEvents.SCHINDLER_M_SERIES_BUTTON_1.get(),
                        SoundCategory.BLOCKS,
                        1.0F,
                        1.0F
                );
                break;
            case "schindler_r_series_button_1":
                serverPlayerEntity.getEntityWorld().playSound(
                        null,
                        blockPos,
                        SoundEvents.SCHINDLER_R_SERIES_BUTTON_1.get(),
                        SoundCategory.BLOCKS,
                        1.0F,
                        1.0F
                );
                break;
            case "schindler_s_series_button_1":
                serverPlayerEntity.getEntityWorld().playSound(
                        null,
                        blockPos,
                        SoundEvents.SCHINDLER_S_SERIES_BUTTON_1.get(),
                        SoundCategory.BLOCKS,
                        1.0F,
                        1.0F
                );
                break;
            case "schindler_fi_gs_button_1":
                serverPlayerEntity.getEntityWorld().playSound(
                        null,
                        blockPos,
                        SoundEvents.SCHINDLER_FI_GS_BUTTON_1.get(),
                        SoundCategory.BLOCKS,
                        1.0F,
                        1.0F
                );
                break;
            case "schindler_linea_button_1":
                serverPlayerEntity.getEntityWorld().playSound(
                        null,
                        blockPos,
                        SoundEvents.SCHINDLER_LINEA_BUTTON_1.get(),
                        SoundCategory.BLOCKS,
                        1.0F,
                        1.0F
                );
                break;
            case "mitsubishi_nexway_lantern_1_up":
                serverPlayerEntity.getEntityWorld().playSound(
                        null,
                        blockPos,
                        SoundEvents.MITSUBISHI_NEXWAY_LANTERN_1_UP.get(),
                        SoundCategory.BLOCKS,
                        1.0F,
                        1.0F
                );
                break;
            case "mitsubishi_nexway_lantern_1_down":
                serverPlayerEntity.getEntityWorld().playSound(
                        null,
                        blockPos,
                        SoundEvents.MITSUBISHI_NEXWAY_LANTERN_1_DOWN.get(),
                        SoundCategory.BLOCKS,
                        1.0F,
                        1.0F
                );
                break;
            case "mitsubishi_nexway_button_1":
                serverPlayerEntity.getEntityWorld().playSound(
                        null,
                        blockPos,
                        SoundEvents.MITSUBISHI_NEXWAY_BUTTON_1.get(),
                        SoundCategory.BLOCKS,
                        1.0F,
                        1.0F
                );
                break;
            case "mitsubishi_nexway_button_2":
                serverPlayerEntity.getEntityWorld().playSound(
                        null,
                        blockPos,
                        SoundEvents.MITSUBISHI_NEXWAY_BUTTON_2.get(),
                        SoundCategory.BLOCKS,
                        1.0F,
                        1.0F
                );
                break;
            case "mitsubishi_mpvf_button_1":
                serverPlayerEntity.getEntityWorld().playSound(
                        null,
                        blockPos,
                        SoundEvents.MITSUBISHI_MPVF_BUTTON_1.get(),
                        SoundCategory.BLOCKS,
                        1.0F,
                        1.0F
                );
                break;
            case "mitsubishi_mp_button_1":
                serverPlayerEntity.getEntityWorld().playSound(
                        null,
                        blockPos,
                        SoundEvents.MITSUBISHI_MP_BUTTON_1.get(),
                        SoundCategory.BLOCKS,
                        1.0F,
                        1.0F
                );
                break;
            case "toshiba_lantern_1_up":
                serverPlayerEntity.getEntityWorld().playSound(
                        null,
                        blockPos,
                        SoundEvents.TOSHIBA_LANTERN_1_UP.get(),
                        SoundCategory.BLOCKS,
                        1.0F,
                        1.0F
                );
                break;

            case "toshiba_lantern_1_down":
                serverPlayerEntity.getEntityWorld().playSound(
                        null,
                        blockPos,
                        SoundEvents.TOSHIBA_LANTERN_1_DOWN.get(),
                        SoundCategory.BLOCKS,
                        1.0F,
                        1.0F
                );
                break;
            case "kone_m_lantern_1_up":
                serverPlayerEntity.getEntityWorld().playSound(
                        null,
                        blockPos,
                        SoundEvents.KONE_M_LANTERN_1_UP.get(),
                        SoundCategory.BLOCKS,
                        1.0F,
                        1.0F
                );
                break;

            case "kone_m_lantern_1_down":
                serverPlayerEntity.getEntityWorld().playSound(
                        null,
                        blockPos,
                        SoundEvents.KONE_M_LANTERN_1_DOWN.get(),
                        SoundCategory.BLOCKS,
                        1.0F,
                        1.0F
                );
                break;
            case "mitsubishi_mp_lantern_1":
                serverPlayerEntity.getEntityWorld().playSound(
                        null,
                        blockPos,
                        SoundEvents.MITSUBISHI_MP_LANTERN_1.get(),
                        SoundCategory.BLOCKS,
                        1.0F,
                        1.0F
                );
                break;
            case "tke_stepc_button_1":
                serverPlayerEntity.getEntityWorld().playSound(
                        null,
                        blockPos,
                        SoundEvents.TKE_STEPC_BUTTON_1.get(),
                        SoundCategory.BLOCKS,
                        0.8F,
                        1.0F
                );
                break;
            case "hitachi_wlmw_button_1":
                serverPlayerEntity.getEntityWorld().playSound(
                        null,
                        blockPos,
                        SoundEvents.HITACHI_WLMW_BUTTON_1.get(),
                        SoundCategory.BLOCKS,
                        0.8F,
                        1.0F
                );
                break;
        }
    }
}
