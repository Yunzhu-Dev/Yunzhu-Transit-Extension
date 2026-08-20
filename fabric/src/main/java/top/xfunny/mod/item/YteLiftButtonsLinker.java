package top.xfunny.mod.item;

import org.mtr.mapping.holder.*;
import org.mtr.mod.block.BlockLiftButtons;
import org.mtr.mod.block.BlockLiftPanelBase;
import org.mtr.mod.block.BlockLiftTrackFloor;
import org.mtr.mod.item.ItemBlockClickingBase;
import top.xfunny.mod.ButtonRegistry;
import top.xfunny.mod.lift.LiftFloorRegistry;
import top.xfunny.mod.block.base.LiftButtonsBase;
import top.xfunny.mod.block.base.LiftDestinationDispatchTerminalBase;

import static top.xfunny.mod.item.LinkerValidTypes.VALID_TYPES;

public class YteLiftButtonsLinker extends ItemBlockClickingBase {
    private final boolean isConnector;

    public YteLiftButtonsLinker(boolean isConnector, ItemSettings itemSettings) {
        super(itemSettings.maxCount(1));
        this.isConnector = isConnector;
    }

    private static void connect(World world, BlockPos blockPos1, BlockPos blockPos2, boolean isAdd) {
        final BlockEntity blockEntity1 = world.getBlockEntity(blockPos1);
        final BlockEntity blockEntity2 = world.getBlockEntity(blockPos2);

        if (blockEntity1 != null && blockEntity2 != null) {
            if (blockEntity2.data instanceof BlockLiftTrackFloor.BlockEntity) {
                if (blockEntity1.data instanceof LiftFloorRegistry) {
                    ((LiftFloorRegistry) blockEntity1.data).registerFloor(blockPos1, world, blockPos2, isAdd);
                } else if (blockEntity1.data instanceof BlockLiftButtons.BlockEntity) {
                    ((BlockLiftButtons.BlockEntity) blockEntity1.data).registerFloor(blockPos2, isAdd);
                } else if (blockEntity1.data instanceof BlockLiftPanelBase.BlockEntityBase) {
                    ((BlockLiftPanelBase.BlockEntityBase) blockEntity1.data).registerFloor(world, blockPos2, isAdd);
                }

            } else if (blockEntity2.data instanceof LiftButtonsBase.BlockEntityBase || blockEntity2.data instanceof LiftDestinationDispatchTerminalBase.BlockEntityBase) {
                if (blockEntity1.data instanceof ButtonRegistry) {
                    ((ButtonRegistry) blockEntity2.data).registerButton(world, blockPos1, isAdd);
                    ((ButtonRegistry) blockEntity1.data).registerButton(world, blockPos2, isAdd);
                }
            }
        }
    }

    @Override
    protected void onStartClick(ItemUsageContext context, CompoundTag compoundTag) {
    }

    @Override
    protected void onEndClick(ItemUsageContext context, BlockPos posEnd, CompoundTag compoundTag) {
        final World world = context.getWorld();
        final BlockPos posStart = context.getBlockPos();
        connect(world, posStart, posEnd, isConnector);
        connect(world, posEnd, posStart, isConnector);
    }

    private boolean isValidType(Object data) {
        return VALID_TYPES.contains(data.getClass());
    }

    @Override
    protected boolean clickCondition(ItemUsageContext context) {
        final Block block = context.getWorld().getBlockState(context.getBlockPos()).getBlock();
        return isValidType(block.data);
    }
}
