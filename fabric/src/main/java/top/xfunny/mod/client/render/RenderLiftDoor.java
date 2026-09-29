package top.xfunny.mod.client.render;

import org.mtr.mapping.holder.*;
import org.mtr.mapping.mapper.BlockEntityRenderer;
import org.mtr.mapping.mapper.EntityModelExtension;
import org.mtr.mapping.mapper.GraphicsHolder;
import org.mtr.mapping.mapper.ModelPartExtension;
import org.mtr.mod.Init;
import org.mtr.mod.block.BlockAPGDoor;
import org.mtr.mod.block.BlockPSDAPGDoorBase;
import org.mtr.mod.block.IBlock;
import org.mtr.mod.data.IGui;
import org.mtr.mod.render.QueuedRenderLayer;
import org.mtr.mod.render.StoredMatrixTransformations;
import top.xfunny.mod.client.view.DirectRenderer;
import top.xfunny.mod.lift.LiftDoorState;

public class RenderLiftDoor<T extends BlockAPGDoor.BlockEntityBase> extends BlockEntityRenderer<T> implements IGui, IBlock {

    private static final ModelSingleCube MODEL_PSD_DOOR_LOCKED = new ModelSingleCube(6, 6, 5, 6, 1, 6, 6, 0);
    private static final ModelSingleCube MODEL_LIFT_LEFT = new ModelSingleCube(28, 18, 0, 0, 0, 12, 16, 2);
    private static final ModelSingleCube MODEL_LIFT_RIGHT = new ModelSingleCube(28, 18, 4, 0, 0, 12, 16, 2);
    //使用mtr的BlockAPGDoor
    private final int type;

    public RenderLiftDoor(Argument dispatcher, int type) {
        super(dispatcher);
        this.type = type;
    }


    @Override
    public void render(T entity, float tickDelta, GraphicsHolder graphicsHolder, int light, int overlay) {
        final World world = entity.getWorld2();
        if (world == null) {
            return;
        }

        entity.tick(tickDelta);

        final BlockPos blockPos = entity.getPos2();
        final Direction facing = IBlock.getStatePropertySafe(world, blockPos, BlockPSDAPGDoorBase.FACING);
        final boolean side = IBlock.getStatePropertySafe(world, blockPos, BlockPSDAPGDoorBase.SIDE) == EnumSide.RIGHT;
        final boolean half = IBlock.getStatePropertySafe(world, blockPos, BlockPSDAPGDoorBase.HALF) == DoubleBlockHalf.UPPER;
        final boolean unlocked = IBlock.getStatePropertySafe(world, blockPos, BlockPSDAPGDoorBase.UNLOCKED);
        final double open = Math.min(entity.getDoorValue(), LiftDoorState.DOOR_MAX_OPEN_SCALE);


        final StoredMatrixTransformations storedMatrixTransformations = new StoredMatrixTransformations(0.5, 0, 0.5);
        storedMatrixTransformations.add(graphicsHolderNew -> {
            graphicsHolderNew.rotateYDegrees(-facing.asRotation());
            graphicsHolderNew.rotateXDegrees(180);
        });

        storedMatrixTransformations.add(matricesNew -> matricesNew.translate(open * (side ? -1 : 1), 0, 0));

        final String texturePrefix;
        switch (type) {
            case 5:
                texturePrefix = "mitsubishi_nexway_door";
                break;
            case 6:
                texturePrefix = "kone_m_door";
                break;
            case 7:
                texturePrefix = "hitachi_b85_door";
                break;
            case 8:
                texturePrefix = "otis_e411_us_door";
                break;
            default:
                texturePrefix = "schindler_qks9_door";
        }
        renderDoor(new Identifier(String.format("yte:textures/block/%s_%s_%s_1.png", texturePrefix, half ? "top" : "bottom", side ? "right" : "left")), storedMatrixTransformations, side, half, unlocked, light, overlay);
    }

    private void renderDoor(Identifier doorTexture, StoredMatrixTransformations storedMatrixTransformations, boolean side, boolean half, boolean unlocked, int light, int overlay) {
        final GraphicsHolder graphicsHolder = DirectRenderer.prepare(QueuedRenderLayer.EXTERIOR, doorTexture, storedMatrixTransformations);
        if (graphicsHolder != null) {
            (side ? MODEL_LIFT_RIGHT : MODEL_LIFT_LEFT).render(graphicsHolder, light, overlay, 1, 1, 1, 1);
            graphicsHolder.pop();
        }

        if (half && !unlocked) {
            final GraphicsHolder lockedGraphicsHolder = DirectRenderer.prepare(QueuedRenderLayer.EXTERIOR, new Identifier(Init.MOD_ID, "textures/block/sign/door_not_in_use.png"), storedMatrixTransformations);
            if (lockedGraphicsHolder != null) {
                lockedGraphicsHolder.translate(side ? 0.125 : -0.125, 0, 0);
                MODEL_PSD_DOOR_LOCKED.render(lockedGraphicsHolder, light, overlay, 1, 1, 1, 1);
                lockedGraphicsHolder.pop();
            }
        }
    }

    @Override
    public boolean rendersOutsideBoundingBox2(T blockEntity) {
        return true;
    }

    private static class ModelSingleCube extends EntityModelExtension<EntityAbstractMapping> {

        private final ModelPartExtension cube;

        private ModelSingleCube(int textureWidth, int textureHeight, int x, int y, int z, int length, int height, int depth) {
            super(textureWidth, textureHeight);
            cube = createModelPart();
            cube.setTextureUVOffset(0, 0).addCuboid(x - 8, y - 16, z - 8, length, height, depth, 0, false);
            buildModel();
        }

        @Override
        public void render(GraphicsHolder graphicsHolder, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
            cube.render(graphicsHolder, 0, 0, 0, packedLight, packedOverlay);
        }

        @Override
        public void setAngles2(EntityAbstractMapping entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        }
    }
}
