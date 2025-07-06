package top.xfunny.mod.block;

import org.mtr.mapping.holder.*;
import org.mtr.mapping.mapper.BlockEntityExtension;
import org.mtr.mapping.tool.HolderBase;
import org.mtr.mod.block.IBlock;
import top.xfunny.mod.BlockEntityTypes;
import top.xfunny.mod.Init;
import top.xfunny.mod.Items;
import top.xfunny.mod.block.base.LiftDestinationDispatchTerminalBase;
import top.xfunny.mod.keymapping.DefaultButtonsKeyMapping;
import top.xfunny.mod.util.ArrayListToString;
import top.xfunny.mod.util.TransformPositionX;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TestLiftDestinationDispatchTerminal extends LiftDestinationDispatchTerminalBase {
    public String screenId = "test_lift_destination_dispatch_terminal_key_mapping_home";

    public TestLiftDestinationDispatchTerminal() {
        super(true);
    }

    public static void hasButtonsClient(BlockPos trackPosition, FloorLiftCallback callback) {
        LiftDestinationDispatchTerminalBase.hasButtonsClient(trackPosition, callback);//todo：需要注意
    }

    @Nonnull
    @Override
    public VoxelShape getOutlineShape2(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return IBlock.getVoxelShapeByDirection(0, 0, 0, 16, 11, 0.1, IBlock.getStatePropertySafe(state, FACING));
    }

    /**
     * 创建方块实体扩展
     * 此方法用于实例化与电梯按钮相关的方块实体
     *
     * @param blockPos   方块的位置
     * @param blockState 方块的状态
     * @return 返回一个新的 {@code BlockEntityExtension} 实例，代表电梯按钮的方块实体
     */
    @Nonnull
    @Override
    public BlockEntityExtension createBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new TestLiftDestinationDispatchTerminal.BlockEntity(blockPos, blockState);
    }

    /**
     * 添加块属性
     * 此方法用于向块的属性列表中添加方向和解锁状态属性
     *
     * @param properties 块的属性列表，包含所有与块相关的属性
     */
    @Override
    public void addBlockProperties(List<HolderBase<?>> properties) {
        // 添加块的方向属性
        properties.add(FACING);
        // 添加块的解锁状态属性
        properties.add(UNLOCKED);

    }

    @Nonnull
    @Override
    public ActionResult onUse2(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        final double hitY = MathHelper.fractionalPart(hit.getPos().getYMapped());

        final org.mtr.mapping.holder.BlockEntity blockEntity = world.getBlockEntity(pos);
        final LiftDestinationDispatchTerminalBase.BlockEntityBase data = (LiftDestinationDispatchTerminalBase.BlockEntityBase) blockEntity.data;
        final TestLiftDestinationDispatchTerminal.BlockEntity data1 = (TestLiftDestinationDispatchTerminal.BlockEntity) blockEntity.data;

        final DefaultButtonsKeyMapping keyMapping = data.getKeyMapping();
        final String focusButton = keyMapping.mapping(TransformPositionX.transform(MathHelper.fractionalPart(hit.getPos().getXMapped()), MathHelper.fractionalPart(hit.getPos().getZMapped()), IBlock.getStatePropertySafe(state, FACING)), hitY);

        if (player.isHolding(top.xfunny.mod.Items.YTE_LIFT_BUTTONS_LINK_CONNECTOR.get()) || player.isHolding(top.xfunny.mod.Items.YTE_LIFT_BUTTONS_LINK_REMOVER.get()) || player.isHolding(top.xfunny.mod.Items.YTE_GROUP_LIFT_BUTTONS_LINK_CONNECTOR.get()) || player.isHolding(Items.YTE_GROUP_LIFT_BUTTONS_LINK_REMOVER.get())) {
            return ActionResult.PASS;
        } else {
            //todo:以后区分不同id下的点击事件
            if (screenId.equals("test_lift_destination_dispatch_terminal_key_mapping_home")) {//todo:可能没有必要进行判断
                switch (focusButton) {
                    case "number1":
                        data1.addInputNumber(1);
                        break;
                    case "number2":
                        data1.addInputNumber(2);
                        break;
                    case "number3":
                        data1.addInputNumber(3);
                        break;
                    case "number4":
                        data1.addInputNumber(4);
                        break;
                    case "number5":
                        data1.addInputNumber(5);
                        break;
                    case "number6":
                        data1.addInputNumber(6);
                        break;
                    case "number7":
                        data1.addInputNumber(7);
                        break;
                    case "number8":
                        data1.addInputNumber(8);
                        break;
                    case "number9":
                        data1.addInputNumber(9);
                        break;
                    case "number0":
                        data1.addInputNumber(0);
                        break;
                    case "clearNumber":
                        data1.clearInputNumber();
                        break;
                    case "callLift":
                        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

                        data.callLift(world, pos, ArrayListToString.arrayListToString(data1.getInputNumber()));
                        data1.clearInputNumber();
                        data1.addInputNumber("To Lift:" + data.getLiftIdentifier());
                        scheduler.schedule(() -> {
                            data1.clearInputNumber();
                            data1.addInputNumber("Please input floor number!");
                        }, 1, TimeUnit.SECONDS);
                        scheduler.shutdown();
                        break;
                }
                player.sendMessage(Text.of(focusButton), true);
                Init.LOGGER.info("focusButton:" + focusButton);
            }
        }


        return ActionResult.SUCCESS;
    }

    /**
     * 表示一个可追踪位置的方块实体，扩展自BlockEntityExtension
     * 主要功能是通过CompoundTag来读取和写入特定位置集合
     */
    public static class BlockEntity extends LiftDestinationDispatchTerminalBase.BlockEntityBase {
        public ArrayList<Object> inputNumber = new ArrayList<>();

        public BlockEntity(BlockPos pos, BlockState state) {
            super(BlockEntityTypes.TEST_LIFT_DESTINATION_DISPATCH_TERMINAL.get(), pos, state);
            super.registerScreenId("test_lift_destination_dispatch_terminal_key_mapping_home");//初始化screen
            inputNumber.add("Please input floor number!");
        }

        public void addInputNumber(Object number) {
            if (ArrayListToString.arrayListToString(inputNumber).equals("Please input floor number!")) {
                clearInputNumber();
            }
            inputNumber.add(number);
        }

        public void clearInputNumber() {
            inputNumber.clear();
        }

        public ArrayList<Object> getInputNumber() {
            if (inputNumber.isEmpty()) {
                inputNumber.add("Please input floor number!");
            }
            return inputNumber;
        }
    }
}
