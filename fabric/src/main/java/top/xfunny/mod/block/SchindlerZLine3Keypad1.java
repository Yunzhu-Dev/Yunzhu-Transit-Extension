package top.xfunny.mod.block;

import org.mtr.mapping.holder.*;
import org.mtr.mapping.mapper.BlockEntityExtension;
import org.mtr.mapping.mapper.TextHelper;
import org.mtr.mapping.tool.HolderBase;
import org.mtr.mod.block.IBlock;
import top.xfunny.mod.BlockEntityTypes;
import top.xfunny.mod.Items;
import top.xfunny.mod.block.base.LiftDestinationDispatchTerminalBase;
import top.xfunny.mod.keymapping.DefaultButtonsKeyMapping;
import top.xfunny.mod.util.ArrayListToString;
import top.xfunny.mod.util.TransformPositionX;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SchindlerZLine3Keypad1 extends LiftDestinationDispatchTerminalBase {

    public SchindlerZLine3Keypad1() {
        super(true);
    }

    public static void hasButtonsClient(BlockPos trackPosition, FloorLiftCallback callback) {
        LiftDestinationDispatchTerminalBase.hasButtonsClient(trackPosition, callback);
    }

    @Nonnull
    @Override
    public VoxelShape getOutlineShape2(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return IBlock.getVoxelShapeByDirection(5, 0, 0, 11, 16, 1.2, IBlock.getStatePropertySafe(state, FACING));
    }

    @Nonnull
    @Override
    public BlockEntityExtension createBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new BlockEntity(blockPos, blockState);
    }

    @Override
    public void addBlockProperties(List<HolderBase<?>> properties) {
        properties.add(FACING);
        properties.add(UNLOCKED);
    }

    @Nonnull
    @Override
    public ActionResult onUse2(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        // 手持刷子切换锁定
        final ActionResult brushResult = IBlock.checkHoldingBrush(world, player, () -> {
            final boolean unlocked = !IBlock.getStatePropertySafe(state, UNLOCKED);
            world.setBlockState(pos, state.with(new Property<>(UNLOCKED.data), unlocked));
            player.sendMessage(Text.cast(TextHelper.translatable(unlocked ? "hint.yte.unlocked" : "hint.yte.locked")), true);
        });
        if (brushResult == ActionResult.SUCCESS) {
            return ActionResult.SUCCESS;
        }

        // 手持 linker 工具时优先交给 MTR 连接系统处理（客户端+服务端都需要）
        if (player.isHolding(Items.YTE_LIFT_BUTTONS_LINK_CONNECTOR.get())
                || player.isHolding(Items.YTE_LIFT_BUTTONS_LINK_REMOVER.get())
                || player.isHolding(Items.YTE_GROUP_LIFT_BUTTONS_LINK_CONNECTOR.get())
                || player.isHolding(Items.YTE_GROUP_LIFT_BUTTONS_LINK_REMOVER.get())) {
            return ActionResult.PASS;
        }else{
            final boolean unlocked = IBlock.getStatePropertySafe(state, UNLOCKED);

            if(unlocked) {
                // 按键处理仅在客户端执行
                if (!world.isClient()) {
                    return ActionResult.SUCCESS;
                }

                final double hitY = MathHelper.fractionalPart(hit.getPos().getYMapped());

                final org.mtr.mapping.holder.BlockEntity blockEntity = world.getBlockEntity(pos);
                if (blockEntity == null || !(blockEntity.data instanceof BlockEntityBase)) {
                    return ActionResult.FAIL;
                }
                BlockEntityBase data = (BlockEntityBase) blockEntity.data;

                if (!(blockEntity.data instanceof BlockEntity)) {
                    return ActionResult.FAIL;
                }
                BlockEntity data1 = (BlockEntity) blockEntity.data;

                final DefaultButtonsKeyMapping keyMapping = data.getKeyMapping();
                final String output = keyMapping.mapping(
                        TransformPositionX.transform(
                                MathHelper.fractionalPart(hit.getPos().getXMapped()),
                                MathHelper.fractionalPart(hit.getPos().getZMapped()),
                                IBlock.getStatePropertySafe(state, FACING)),
                        hitY);

                processKeyInput(world, pos, data1, data, output);
                return ActionResult.SUCCESS;
            } else{
                return ActionResult.FAIL;
            }





        }


    }

    // ======================== 按键处理（状态机） ========================

    private static final Map<String, Integer> NUMBER_KEYS = new HashMap<String, Integer>() {{
        put("number1", 1);
        put("number2", 2);
        put("number3", 3);
        put("number4", 4);
        put("number5", 5);
        put("number6", 6);
        put("number7", 7);
        put("number8", 8);
        put("number9", 9);
        put("number0", 0);
    }};

    private void processKeyInput(World world, BlockPos pos, BlockEntity be,
                                 BlockEntityBase data, String output) {
        final String screenId = data.getScreenId();

        switch (screenId) {
            // ---- 首页 / 无障碍页 ----
            case "schindler_z_line_3_keypad_1_key_mapping_home":
            case "schindler_z_line_3_keypad_1_key_mapping_accessibility":
                if (NUMBER_KEYS.containsKey(output)) {
                    be.startNewInput(world, pos, String.valueOf(NUMBER_KEYS.get(output)));
                } else if ("basement".equals(output)) {
                    be.startNewInput(world, pos, "-");
                } else if ("accessibility".equals(output)) {
                    be.switchScreen("schindler_z_line_3_keypad_1_key_mapping_accessibility");
                } else if ("lobby".equals(output)) {
                    String result = data.callLift(world, pos, "1");
                    if (result.equals("?")) result = data.callLift(world, pos, "G");
                    be.showAssignment(world, pos, data, result,
                            "schindler_z_line_3_keypad_1_key_mapping_home");
                }
                break;

            // ---- 输入页 ----
            case "schindler_z_line_3_keypad_1_key_mapping_input":
                if (NUMBER_KEYS.containsKey(output)) {
                    be.continueInput(world, pos, NUMBER_KEYS.get(output));
                } else if ("basement".equals(output)) {
                    be.continueInput(world, pos, "-");
                } else if ("accessibility".equals(output)) {
                    be.continueInput(world, pos, "1");
                } else if ("lobby".equals(output)) {
                    String result = data.callLift(world, pos, "1");
                    if (result.equals("?")) result = data.callLift(world, pos, "G");
                    be.showAssignment(world, pos, data, result,
                            "schindler_z_line_3_keypad_1_key_mapping_home");
                }
                break;

            // ---- 分配结果显示页 ----
            case "schindler_z_line_3_keypad_1_key_mapping_identifier":
                if (NUMBER_KEYS.containsKey(output)) {
                    be.startNewInput(world, pos, String.valueOf(NUMBER_KEYS.get(output)));
                } else if ("basement".equals(output)) {
                    be.startNewInput(world, pos, "-");
                } else if ("accessibility".equals(output)) {
                    be.clearInputString();
                    be.switchScreen("schindler_z_line_3_keypad_1_key_mapping_accessibility");
                } else if ("lobby".equals(output)) {
                    String result = data.callLift(world, pos, "1");
                    if (result.equals("?")) result = data.callLift(world, pos, "G");
                    be.showAssignment(world, pos, data, result,
                            "schindler_z_line_3_keypad_1_key_mapping_home");
                }
                break;
        }
    }

    // ======================== BlockEntity（统一定时器） ========================

    public static class BlockEntity extends LiftDestinationDispatchTerminalBase.BlockEntityBase {

        public ArrayList<Object> inputString = new ArrayList<>();

        /** RETURN_HOME 动作的返回目标屏幕 */
        private String returnScreen = "schindler_z_line_3_keypad_1_key_mapping_home";

        private static final long AUTO_CALL_DELAY_MS = 3000;   // 3 秒无输入自动呼叫
        private static final long DISPLAY_DURATION_MS = 3000;  // 结果显示 3 秒后熄屏

        private static final byte ACT_AUTO_CALL = 1;
        private static final byte ACT_RETURN_HOME = 2;

        public BlockEntity(BlockPos pos, BlockState state) {
            super(BlockEntityTypes.SCHINDLER_Z_LINE_3_KEYPAD_1.get(), pos, state);
            super.registerScreenId("schindler_z_line_3_keypad_1_key_mapping_home");
        }

        // ========== 定时器回调 ==========

        @Override
        protected void onTimerFired(World world, BlockPos pos, byte actionId) {
            switch (actionId) {
                case ACT_AUTO_CALL: {
                    final String floorNumber = ArrayListToString.arrayListToString(inputString);
                    final String ident = callEnteredFloor(world, pos, floorNumber);
                    switchScreen("schindler_z_line_3_keypad_1_key_mapping_identifier");
                    inputString.clear();
                    if (!ident.equals("?")) {
                        inputString.add(formatLiftAssignment());
                        setDisplayText(formatLiftAssignment());
                    } else {
                        inputString.add("??");
                        setDisplayText("??");
                    }
                    startTimer(DISPLAY_DURATION_MS, ACT_RETURN_HOME);
                    returnScreen = "schindler_z_line_3_keypad_1_key_mapping_home";
                    syncDisplayState(world, pos);
                    break;
                }
                case ACT_RETURN_HOME: {
                    clearInputString();
                    switchScreen(returnScreen);
                    break;
                }
            }
        }

        // ========== 用户操作 ==========

        /**
         * 开始新输入（从 home/accessibility/identifier 页按数字键）。
         * 清空旧输入，取消所有定时器，开始输入。
         */
        private String callEnteredFloor(World world, BlockPos pos, String floorNumber) {
            String result = callLift(world, pos, floorNumber);
            if (result.equals("?") && floorNumber.matches("-\\d+")) {
                result = callLift(world, pos, "B" + floorNumber.substring(1));
            }
            return result;
        }

        void startNewInput(World world, BlockPos pos, String digit) {
            cancelTimer();
            clearInputString();
            inputString.add(digit);
            setDisplayText(digit);
            switchScreen("schindler_z_line_3_keypad_1_key_mapping_input");
            startTimer(AUTO_CALL_DELAY_MS, ACT_AUTO_CALL);
            syncDisplayState(world, pos);
        }

        /**
         * 继续输入（在输入页按数字键）。
         * 追加数字，重置自动呼叫定时器。
         */
        void continueInput(World world, BlockPos pos, Object digit) {
            cancelTimer();
            inputString.add(digit);
            setDisplayText(ArrayListToString.arrayListToString(inputString));
            startTimer(AUTO_CALL_DELAY_MS, ACT_AUTO_CALL);
            syncDisplayState(world, pos);
        }

        /**
         * 显示分配结果（lobby 键或自动呼叫后）。
         * 切换到 identifier 屏幕，启动熄屏定时器。
         */
        void showAssignment(World world, BlockPos pos, BlockEntityBase data,
                            String callResult, String returnToScreen) {
            cancelTimer();
            switchScreen("schindler_z_line_3_keypad_1_key_mapping_identifier");
            clearInputString();
            if (!callResult.equals("?")) {
                final String assignment = data.formatLiftAssignment();
                inputString.add(assignment);
                setDisplayText(assignment);
            } else {
                inputString.add("??");
                setDisplayText("??");
            }
            this.returnScreen = returnToScreen;
            startTimer(DISPLAY_DURATION_MS, ACT_RETURN_HOME);
            syncDisplayState(world, pos);
        }

        // ========== 屏幕 & 输入管理 ==========

        public void clearInputString() {
            inputString.clear();
            setDisplayText(" ");
        }

        public ArrayList<Object> getInputString() {
            if (inputString.isEmpty()) {
                inputString.add("");
            }
            return inputString;
        }

        public void switchScreen(String screenId) {
            super.registerScreenId(screenId);
            final World world = getWorld2();
            if (world != null) {
                syncDisplayState(world, getPos2());
            }
        }
    }
}
