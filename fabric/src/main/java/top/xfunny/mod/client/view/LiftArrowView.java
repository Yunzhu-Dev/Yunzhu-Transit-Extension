package top.xfunny.mod.client.view;

import org.mtr.core.data.Lift;
import org.mtr.core.data.LiftDirection;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectObjectImmutablePair;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.World;
import org.mtr.mod.Init;
import org.mtr.mod.InitClient;
import top.xfunny.mod.util.ClientGetLiftDetails;

public class LiftArrowView extends ImageView {
    private Lift lift;
    private ArrowType arrowType;
    private boolean needScroll;
    private float scrollSpeed;
    private boolean alwaysOn;

    @Override
    public void render() {
        float gameTick = InitClient.getGameTick();
        ObjectObjectImmutablePair<LiftDirection, ObjectObjectImmutablePair<String, String>> liftDetails = ClientGetLiftDetails.getLiftDetails(world, lift, Init.positionToBlockPos(lift.getCurrentFloor().getPosition()));
        final LiftDirection liftDirection = liftDetails.left();
        final float[] uv = uv(liftDirection, gameTick);
        setUv(uv);

        switch (arrowType) {
            case UP:
                if (alwaysOn || liftDirection.equals(LiftDirection.UP)) {
                    super.render();
                }
                break;
            case DOWN:
                if (alwaysOn || liftDirection.equals(LiftDirection.DOWN)) {
                    super.render();
                }
                break;
            case BOTH:
                if (alwaysOn || liftDirection.equals(LiftDirection.NONE) && lift.getDoorValue() != 0) {
                    super.render();
                }
                break;
            case AUTO:
                if (alwaysOn || !liftDirection.equals(LiftDirection.NONE)) {
                    super.render();
                }
                break;
        }
    }


    public void setBasicsAttributes(World world, BlockPos blockPos, Lift lift, ArrowType arrowType) {
        this.world = world;
        this.blockPos = blockPos;
        this.lift = lift;
        this.arrowType = arrowType;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setDefaultColor(int color) {
        this.color = color;
        this.alwaysOn = true;
    }

    private float[] uv(LiftDirection direction, float gameTick) {
        float[] uv = new float[4];
        float uvOffset = (gameTick * scrollSpeed) % 1;
        uv[0] = 0; // u1
        uv[1] = (!direction.equals(LiftDirection.UP) ? 0.0f : 1.0f) + (needScroll && !direction.equals(LiftDirection.NONE) ? uvOffset : 0.0f); // v1
        uv[2] = 1; // u2
        uv[3] = (!direction.equals(LiftDirection.UP) ? 1.0f : 0.0f) + (needScroll && !direction.equals(LiftDirection.NONE) ? uvOffset : 0.0f);// v2
        return uv;
    }

    public void setAnimationScrolling(boolean needScroll, float scrollSpeed) {
        this.needScroll = needScroll;
        this.scrollSpeed = scrollSpeed;
    }

    public enum ArrowType {
        UP,
        DOWN,
        BOTH,
        AUTO
    }
}
