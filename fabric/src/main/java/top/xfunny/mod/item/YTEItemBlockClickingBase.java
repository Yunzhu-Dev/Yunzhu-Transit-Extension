package top.xfunny.mod.item;

import org.mtr.mapping.holder.*;
import org.mtr.mapping.mapper.ItemExtension;
import org.mtr.mod.generated.lang.TranslationProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public abstract class YTEItemBlockClickingBase extends ItemExtension {
    public static final String TAG_POS = "pos";
    public static final String TAG_SECOND_POS = "secondPos";

    public YTEItemBlockClickingBase(ItemSettings itemSettings) {
        super(itemSettings);
    }

    @Nonnull
    @Override
    public ActionResult useOnBlock2(ItemUsageContext context) {
        if (!context.getWorld().isClient()) {
            if (clickCondition(context)) {
                final CompoundTag compoundTag = context.getStack().getOrCreateTag();

                if (compoundTag.contains(TAG_SECOND_POS)) {
                    final BlockPos posEnd = BlockPos.fromLong(compoundTag.getLong(TAG_POS));
                    final BlockPos posStart = BlockPos.fromLong(compoundTag.getLong(TAG_SECOND_POS));
                    final BlockPos posThird = context.getBlockPos();

                    try {
                        onThirdClick(context, posStart, posEnd, posThird, compoundTag);
                    } finally {
                        compoundTag.remove(TAG_SECOND_POS);
                        compoundTag.remove(TAG_POS);
                    }

                } else if (compoundTag.contains(TAG_POS)) {
                    final BlockPos posEnd = BlockPos.fromLong(compoundTag.getLong(TAG_POS));
                    final BlockPos posStart = context.getBlockPos();
                    World world = context.getWorld();

                    final Block blockEnd = world.getBlockState(posEnd).getBlock();
                    final Block blockStart = world.getBlockState(posStart).getBlock();

                    // [修复] 按钮↔屏幕(allowPress 相反)，或按钮/屏幕↔键盘(目的地终端) 时触发三点批量模式
                    final boolean triggerThirdClick = isValidButtonDisplayPair(blockEnd.data, blockStart.data);

                    if (triggerThirdClick) {
                        compoundTag.putLong(TAG_SECOND_POS, posStart.asLong());
                    } else {
                        try {
                            onEndClick(context, posEnd, compoundTag);
                        } finally {
                            compoundTag.remove(TAG_POS);
                        }
                    }
                } else {
                    compoundTag.putLong(TAG_POS, context.getBlockPos().asLong());
                    onStartClick(context, compoundTag);
                }
                return ActionResult.SUCCESS;
            } else {
                return ActionResult.FAIL;
            }
        } else {
            return super.useOnBlock2(context);
        }
    }

    @Override
    public void addTooltips(ItemStack stack, @Nullable World world, List<MutableText> tooltip, TooltipContext options) {
        final CompoundTag compoundTag = stack.getOrCreateTag();
        if (compoundTag.contains(TAG_POS)) {
            final long posLong = compoundTag.getLong(TAG_POS);
            tooltip.add(TranslationProvider.TOOLTIP_MTR_SELECTED_BLOCK.getMutableText(BlockPos.fromLong(posLong).toShortString()).formatted(TextFormatting.GOLD));
        }
    }

    protected abstract void onStartClick(ItemUsageContext context, CompoundTag compoundTag);
    protected abstract void onEndClick(ItemUsageContext context, BlockPos posEnd, CompoundTag compoundTag);
    protected abstract void onThirdClick(ItemUsageContext context, BlockPos pos1, BlockPos pos2, BlockPos pos3, CompoundTag compoundTag);
    protected abstract boolean clickCondition(ItemUsageContext context);

    /**
     * 判断两个方块是否构成合法的批量配对。
     * <p>
     * 支持：
     * <ul>
     *   <li>按钮(LiftButtonsBase allowPress=true) ↔ 屏幕/到站灯(LiftButtonsBase allowPress=false)</li>
     *   <li>目的地终端(LiftDestinationDispatchTerminalBase) ↔ 按钮/屏幕(LiftButtonsBase)</li>
     * </ul>
     */
    protected static boolean isValidButtonDisplayPair(Object dataA, Object dataB) {
        return LinkerValidTypes.isValidConnection(dataA, dataB);
    }
}