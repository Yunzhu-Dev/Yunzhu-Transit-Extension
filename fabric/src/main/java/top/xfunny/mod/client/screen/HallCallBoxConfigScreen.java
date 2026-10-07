package top.xfunny.mod.client.screen;

import org.jetbrains.annotations.NotNull;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.mapper.GraphicsHolder;
import org.mtr.mapping.mapper.ScreenExtension;
import org.mtr.mod.data.IGui;
import top.xfunny.mod.block.HallCallBox;

public class HallCallBoxConfigScreen extends ScreenExtension implements IGui {
    private final BlockPos blockPos;

    public HallCallBoxConfigScreen(BlockPos blockPos, HallCallBox.BlockEntity data) {
        super();
        this.blockPos = blockPos;
    }

    @Override
    protected void init2() {
        super.init2();
        GuiHelper.clearScreenChildren(this);
    }

    @Override
    public void tick2() {
    }

    @Override
    public void render(@NotNull GraphicsHolder graphicsHolder, int mouseX, int mouseY, float delta) {
        renderBackground(graphicsHolder);
    }

    @Override
    public void onClose2() {
        super.onClose2();
    }

    @Override
    public boolean isPauseScreen2() {
        return false;
    }
}
