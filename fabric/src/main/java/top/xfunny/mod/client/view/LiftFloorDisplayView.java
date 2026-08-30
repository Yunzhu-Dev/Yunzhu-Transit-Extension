package top.xfunny.mod.client.view;

import org.mtr.core.data.Lift;
import org.mtr.core.data.LiftDirection;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectObjectImmutablePair;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.World;
import org.mtr.mod.Init;
import top.xfunny.mod.config.YteLiftConfigStore;
import top.xfunny.mod.util.ClientGetLiftDetails;

import java.awt.*;

public class LiftFloorDisplayView extends TextView {
    private Lift lift;
    private boolean noFloorNumber;
    private boolean noFloorDisplay;

    @Override
    public void render() {
        formatFloorText();

        if (!YteLiftConfigStore.getServiceMode(lift.getId()).hidesHallDisplay()
                && (!noFloorNumber || !noFloorDisplay)) {
            super.render();
        }
    }

    private void formatFloorText() {
        ObjectObjectImmutablePair<LiftDirection, ObjectObjectImmutablePair<String, String>> liftDetails = ClientGetLiftDetails.getLiftDetails(world, lift, Init.positionToBlockPos(lift.getCurrentFloor().getPosition()));
        String floorNumber = liftDetails.right().left();
        String floorDescription = liftDetails.right().right();
        this.noFloorNumber = floorNumber.isEmpty();
        this.noFloorDisplay = floorDescription.isEmpty();
        this.gameTick = org.mtr.mod.InitClient.getGameTick();
        if (!noFloorNumber || !noFloorDisplay) {
            this.text = String.format("%s%s", floorNumber, noFloorNumber ? " " : "");
        } else {
            this.text = "";
        }
    }

    public void setBasicsAttributes(World world, BlockPos blockPos, Lift lift, Font font, float fontSize, int color) {
        this.world = world;
        this.blockPos = blockPos;
        this.lift = lift;
        this.font = font;
        this.fontSize = fontSize;
        this.color = color;
    }

    public int getTextLength() {
        formatFloorText();
        return text.length();
    }
}
