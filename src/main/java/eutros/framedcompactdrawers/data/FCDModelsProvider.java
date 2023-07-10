package eutros.framedcompactdrawers.data;

import com.google.common.base.Preconditions;
import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import eutros.framedcompactdrawers.FramedCompactDrawers;
import eutros.framedcompactdrawers.block.BlockFramingTable;
import eutros.framedcompactdrawers.block.ModBlocks;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.core.Direction;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.model.generators.*;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

public class FCDModelsProvider extends BlockStateProvider {
    public FCDModelsProvider(DataGenerator gen, ExistingFileHelper efh) {
        super(gen, FramedCompactDrawers.MOD_ID, efh);
    }

    public static void register(DataGenerator gen, ExistingFileHelper efh) {
        gen.addProvider(true, new FCDModelsProvider(gen, efh));
    }

    @Override
    protected void registerStatesAndModels() {
        VariantBlockStateBuilder vb = getVariantBuilder(ModBlocks.framingTable);
        Map<BlockFramingTable.TableSide, ModelFile> sideCache = new EnumMap<>(BlockFramingTable.TableSide.class);
        for (BlockFramingTable.TableSide side : BlockFramingTable.TableSide.values()) {
            for (Direction direction : Direction.Plane.HORIZONTAL) {
                vb.partialState()
                        .with(BlockFramingTable.FACING, direction)
                        .with(BlockFramingTable.SIDE, side)
                        .modelForState()
                        .modelFile(sideCache.computeIfAbsent(side, this::computeSide))
                        .rotationY((int) direction.toYRot())
                        .addModel();
            }
        }
        // https://github.com/jaquadro/StorageDrawers/blob/6af860cf2740a2934a6eee6e612ab83fb3c8d969/src/main/java/com/jaquadro/minecraft/storagedrawers/client/model/FramingTableModel.java#L122-L129
        BlockModelBuilder ftItemModel = computeSide(null)
                .transforms()
                .transform(TransformType.GUI).rotation(30, 225, 0).translation(.15f, 0, 0).scale(.45f, .45f, .45f).end()
                .transform(TransformType.FIRST_PERSON_RIGHT_HAND).rotation(0, -30, 0).translation(-.15f, .05f, 0).scale(.3f, .3f, .3f).end()
                .transform(TransformType.FIRST_PERSON_LEFT_HAND).rotation(0, 150, 0).translation(-.15f, .05f, 0).scale(.3f, .3f, .3f).end()
                .transform(TransformType.THIRD_PERSON_RIGHT_HAND).rotation(75, -30, 0).translation(-.2f, .2f, -.15f).scale(.3f, .3f, .3f).end()
                .transform(TransformType.THIRD_PERSON_LEFT_HAND).rotation(75, 150, 0).translation(-.2f, .2f, -.15f).scale(.3f, .3f, .3f).end()
                .transform(TransformType.HEAD).rotation(0, 0, 0).translation(0, 0, 0).scale(.5f, .5f, .5f).end()
                .transform(TransformType.FIXED).rotation(0, 0, 0).translation(0, 0, 0).scale(.5f, .5f, .5f).end()
                .transform(TransformType.GROUND).rotation(0, 0, 0).translation(0, 0, 0).scale(.25f, .25f, .25f).end()
                .end();
        itemModels()
                .getBuilder(Objects.requireNonNull(ForgeRegistries.ITEMS
                                .getKey(ModBlocks.framingTable.asItem()))
                        .toString())
                .parent(ftItemModel);
    }

    private BlockModelBuilder computeSide(BlockFramingTable.TableSide tableSide) {
        Vec3 leftOffset = null, rightOffset = null;
        String side;
        if (tableSide == null) {
            leftOffset = new Vec3(-0.5, 0, 0);
            rightOffset = new Vec3(+0.5, 0, 0);
            side = "full";
        } else {
            switch (tableSide) {
                case LEFT -> leftOffset = Vec3.ZERO;
                case RIGHT -> rightOffset = Vec3.ZERO;
            }
            side = tableSide.toString();
        }

        BlockModelBuilder model = models()
                .withExistingParent("framing_table_" + side, "block")
                .texture("base", new ResourceLocation(StorageDrawers.MOD_ID, "blocks/base/base_oak"))
                .texture("trim", new ResourceLocation(StorageDrawers.MOD_ID, "blocks/base/trim_oak"))
                .texture("particle", new ResourceLocation(StorageDrawers.MOD_ID, "blocks/base/base_oak"))
                .renderType("translucent") // needed for overlay
                .ao(false);
        CommonFramingRenderer cfr = new CommonFramingRenderer(new ChamRender(model));
        if (leftOffset != null) {
            cfr.renderLeft(leftOffset, "#base", "#trim");
            model.texture("overlay_left", new ResourceLocation(StorageDrawers.MOD_ID,
                    "blocks/overlay/shading_worktable_left"));
            cfr.renderOverlayLeft(leftOffset, "#overlay_left");
        }
        if (rightOffset != null) {
            cfr.renderRight(rightOffset, "#base", "#trim");
            model.texture("overlay_right", new ResourceLocation(StorageDrawers.MOD_ID,
                    "blocks/overlay/shading_worktable_right"));
            cfr.renderOverlayRight(rightOffset, "#overlay_right");
        }

        return model;
    }

    // https://github.com/jaquadro/Chameleon/blob/e262847f2701036b08c169290d9e31b46e6dbc00/src/com/jaquadro/minecraft/chameleon/render/ChamRender.java#L22
    private static final class ChamRender {

        public static final Direction FACE_YNEG = Direction.DOWN;
        public static final Direction FACE_YPOS = Direction.UP;
        public static final Direction FACE_ZNEG = Direction.NORTH;
        public static final Direction FACE_ZPOS = Direction.SOUTH;
        public static final Direction FACE_XNEG = Direction.WEST;
        public static final Direction FACE_XPOS = Direction.EAST;

        private final BlockModelBuilder model;

        private ChamRender(BlockModelBuilder model) {
            this.model = model;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (ChamRender) obj;
            return Objects.equals(this.model, that.model);
        }

        @Override
        public int hashCode() {
            return Objects.hash(model);
        }

        @Override
        public String toString() {
            return "ChamRender[" +
                    "model=" + model + ']';
        }

        private double[] renderBounds;
        private final ModelBuilder.FaceRotation[] rotations = new ModelBuilder.FaceRotation[Direction.values().length];

        {
            clearUVRotation();
        }

        public void renderFaces(Vec3 pos, String tex, Direction... faces) {
            Preconditions.checkNotNull(renderBounds, "call setRenderBounds() first");

            double[] rb = renderBounds;
            float sf = 16F;
            ModelBuilder<BlockModelBuilder>.ElementBuilder elt = model.element()
                    .from(sf * (float) (rb[0] + pos.x()), sf * (float) (rb[1] + pos.y()), sf * (float) (rb[2] + pos.z()))
                    .to(sf * (float) (rb[3] + pos.x()), sf * (float) (rb[4] + pos.y()), sf * (float) (rb[5] + pos.z()));

            for (Direction face : faces) {
                ModelBuilder.FaceRotation rotation = rotations[face.ordinal()];
                float[] uvs = getUvsFor(rotation, face);
                elt.face(face)
                        .rotation(rotation)
                        .uvs(uvs[0], uvs[1], uvs[2], uvs[3]);
            }

            elt.texture(tex);
        }

        private static float rotationDegrees(ModelBuilder.FaceRotation rotation) {
            return switch (rotation) {
                case ZERO -> 0.0F;
                case CLOCKWISE_90 -> 90.0F;
                case UPSIDE_DOWN -> 180.0F;
                case COUNTERCLOCKWISE_90 -> 270.0F;
            };
        }

        /** @see net.minecraft.client.renderer.block.model.BlockElement#uvsByFace(Direction) */
        private final float[][][] uvsByFaceMats = {
                { // down
                        {1, 0, +0, 0},
                        {0, 0, -1, 1},
                },
                { // up
                        {1, 0, 0, 0},
                        {0, 0, 1, 0},
                },
                { // north
                        {-1, +0, 0, 1},
                        {+0, -1, 0, 1},
                },
                { // south
                        {1, +0, 0, 0},
                        {0, -1, 0, 1},
                },
                { // west
                        {0, +0, 1, 0},
                        {0, -1, 0, 1},
                },
                { // east
                        {0, +0, -1, 1},
                        {0, -1, +0, 1}
                }
        };
        private final Matrix4f[][] rotsAndFaces = new Matrix4f[/* rots */ 4][/* faces */ 6];

        private float[] getUvsFor(ModelBuilder.FaceRotation rotation, Direction face) {
            // multiply as a matrix:
            //    / x1  x2  0 \
            //    | y1  y2  0 |
            //    \ z1  z2  0 /
            // ->
            //    / u1  u2  0 \
            //    |  0   0  0 |
            //    \ v1  v2  0 /
            Matrix4f toTransform = new Matrix4f();

            // load in vectors
            FloatBuffer buf = FloatBuffer.allocate(16);

            buf.put(0, (float) renderBounds[0]);
            buf.put(1, (float) renderBounds[1]);
            buf.put(2, (float) renderBounds[2]);
            buf.put(3, 1);

            buf.put(4, (float) renderBounds[3]);
            buf.put(5, (float) renderBounds[4]);
            buf.put(6, (float) renderBounds[5]);
            buf.put(7, 1);

            toTransform.load(buf); // column major
            toTransform.multiplyBackward(getUvMatrix(rotation, face));
            toTransform.store(buf);

            float[] uvs = new float[4];
            uvs[0] = buf.get(0);
            uvs[1] = buf.get(1);
            uvs[2] = buf.get(4);
            uvs[3] = buf.get(5);
            minmax(uvs, 0, 2);
            minmax(uvs, 1, 3);
            return uvs;
        }

        private void minmax(float[] uvs, int a, int b) {
            if (uvs[a] > uvs[b]) {
                float tmp = uvs[b];
                uvs[b] = uvs[a];
                uvs[a] = tmp;
            }
        }

        private Matrix4f getUvMatrix(ModelBuilder.FaceRotation rotation, Direction face) {
            // action:
            //   bounds [0, 1]^3
            //   -> uv plane [0, 1]^2
            //   -> rotated uvs [-0.5, 0.5]^2
            //   -> scaled uvs [0, 16]^2

            Matrix4f mat = rotsAndFaces[rotation.ordinal()][face.ordinal()];

            if (mat == null) {
                // (read backwards, each multiply is on the right)
                mat = new Matrix4f();
                mat.setIdentity();

                mat.multiplyWithTranslation(0.5F, 0.5F, 0);
                mat.multiply(Vector3f.ZP.rotationDegrees(rotationDegrees(rotation)));
                mat.multiplyWithTranslation(-0.5F, -0.5F, 0);

                Matrix4f uvMatF = new Matrix4f();
                float[][] uvMat = uvsByFaceMats[face.ordinal()];
                FloatBuffer buf = FloatBuffer.allocate(16);
                buf.put(0, uvMat[0]);
                buf.put(4, uvMat[1]);
                buf.put(15, 1);
                uvMatF.loadTransposed(buf); // row major
                mat.multiply(uvMatF);

                mat.multiply(16); // scale

                mat.store(buf);
                for (int i = 0; i < buf.capacity(); i++) {
                    buf.put((float) Math.rint(buf.get(i)));
                }
                mat.load(buf);

                rotsAndFaces[rotation.ordinal()][face.ordinal()] = mat;
            }

            return mat;
        }

        public void setRenderBounds(double... bound) {
            renderBounds = bound;
        }

        public void setUVRotation(ModelBuilder.FaceRotation rot) {
            Arrays.fill(rotations, rot);
        }

        public void setUVRotations(ModelBuilder.FaceRotation rot, Direction... sides) {
            for (Direction side : sides) {
                rotations[side.ordinal()] = rot;
            }
        }

        public void clearUVRotation() {
            setUVRotation(ModelBuilder.FaceRotation.ZERO);
        }
    }

    // with love from https://github.com/jaquadro/StorageDrawers/blob/6af860cf2740a2934a6eee6e612ab83fb3c8d969/src/main/java/com/jaquadro/minecraft/storagedrawers/client/model/dynamic/CommonFramingRenderer.java#L11
    private record CommonFramingRenderer(ChamRender renderer) {
        private static final double unit = .0625;
        private static final double unit2 = unit * 2;
        private static final double unit4 = unit * 4;

        private static final double[][] baseBoundsLeftY = new double[][]{
                {unit, 1 - unit2, unit, 1, 1, 1 - unit}
        };

        private static final double[][] trimBoundsLeftY = new double[][]{
                {0, 1 - unit2, unit, unit, 1, 1 - unit},
                {0, 1 - unit2, 0, unit, 1, unit},
                {0, 1 - unit2, 1 - unit, unit, 1, 1},
                {unit, 1 - unit2, 0, 1, 1, unit},
                {unit, 1 - unit2, 1 - unit, 1, 1, 1},
        };

        private static final double[][] trimBoundsLeftZ = new double[][]{
                {0, 1 - unit2, 0, unit, 1, 1},
                {unit, 1 - unit2, 0, 1, 1, 1},
        };

        private static final double[][] trimBoundsLeftX = new double[][]{
                {0, 1 - unit2, 0, 1, 1, unit},
                {0, 1 - unit2, unit, 1, 1, 1 - unit},
                {0, 1 - unit2, 1 - unit, 1, 1, 1},
        };

        private static final double[][] baseBoundsRightY = new double[][]{
                {0, 1 - unit2, unit, 1 - unit, 1, 1 - unit}
        };

        private static final double[][] trimBoundsRightY = new double[][]{
                {1 - unit, 1 - unit2, unit, 1, 1, 1 - unit},
                {1 - unit, 1 - unit2, 0, 1, 1, unit},
                {1 - unit, 1 - unit2, 1 - unit, 1, 1, 1},
                {0, 1 - unit2, 0, 1 - unit, 1, unit},
                {0, 1 - unit2, 1 - unit, 1 - unit, 1, 1},
        };

        private static final double[][] trimBoundsRightZ = new double[][]{
                {1 - unit, 1 - unit2, 0, 1, 1, 1},
                {0, 1 - unit2, 0, 1 - unit, 1, 1},
        };

        private static final double[][] trimBoundsRightX = new double[][]{
                {0, 1 - unit2, 0, 1, 1, unit},
                {0, 1 - unit2, unit, 1, 1, 1 - unit},
                {0, 1 - unit2, 1 - unit, 1, 1, 1},
        };

        public void renderLeft(Vec3 pos, String iconBase, String iconTrim) {
            renderTableBox(pos, iconBase, iconTrim, baseBoundsLeftY, trimBoundsLeftY, trimBoundsLeftZ, trimBoundsLeftX, true);
            renderStructure(pos, iconBase, true);
        }

        public void renderRight(Vec3 pos, String iconBase, String iconTrim) {
            renderTableBox(pos, iconBase, iconTrim, baseBoundsRightY, trimBoundsRightY, trimBoundsRightZ, trimBoundsRightX, false);
            renderStructure(pos, iconBase, false);
        }

        public void renderOverlayLeft(Vec3 pos, String iconOverlay) {
            renderOverlay(pos, iconOverlay, baseBoundsLeftY);
        }

        public void renderOverlayRight(Vec3 pos, String iconOverlay) {
            renderOverlay(pos, iconOverlay, baseBoundsRightY);
        }

        public void renderOverlay(Vec3 pos, String iconOverlay, double[][] baseBoundsY) {
            for (double[] bound : baseBoundsY) {
                renderer.setRenderBounds(bound);
                renderer.renderFaces(pos, iconOverlay, ChamRender.FACE_YPOS);
            }
        }

        private void renderStructure(Vec3 pos, String iconBase, boolean left) {
            renderFoot(pos, iconBase, left);
            renderLegs(pos, iconBase, left);
            renderBraces(pos, iconBase, left);
        }

        private void renderTableBox(Vec3 pos, String iconBase, String iconTrim,
                                    double[][] baseBoundsY, double[][] trimBoundsY, double[][] trimBoundsZ, double[][] trimBoundsX, boolean left) {
            Direction xSide = left ? ChamRender.FACE_XNEG : ChamRender.FACE_XPOS;

            for (double[] bound : baseBoundsY)
                renderTableSurface(pos, iconBase, bound);

            for (double[] bound : trimBoundsY)
                renderTableSurface(pos, iconTrim, bound);

            for (double[] bound : trimBoundsZ) {
                renderer.setRenderBounds(bound);
                renderer.renderFaces(pos, iconTrim, ChamRender.FACE_ZNEG, ChamRender.FACE_ZPOS);
            }

            for (double[] bound : trimBoundsX) {
                renderer.setRenderBounds(bound);
                renderer.renderFaces(pos, iconTrim, xSide);
            }
        }

        private void renderTableSurface(Vec3 pos, String icon, double[] bound) {
            renderer.setRenderBounds(bound);
            renderer.renderFaces(pos, icon, ChamRender.FACE_YPOS, ChamRender.FACE_YNEG);
        }

        private void renderFoot(Vec3 pos, String icon, boolean left) {
            double xStart = left ? unit2 : 1 - unit2 - unit2;

            renderer.setRenderBounds(xStart, 0, 0, xStart + unit2, unit2, 1);
            renderer.setUVRotations(ModelBuilder.FaceRotation.CLOCKWISE_90, Direction.DOWN, Direction.UP);
            renderer.renderFaces(pos, icon, Direction.values());
            renderer.clearUVRotation();
        }

        private void renderLegs(Vec3 pos, String icon, boolean left) {
            double xStart = left ? unit2 : 1 - unit2 - unit2;

            Direction[] legFaces = Direction.Plane.HORIZONTAL.stream().toArray(Direction[]::new);
            renderer.setUVRotations(ModelBuilder.FaceRotation.CLOCKWISE_90, legFaces);

            renderer.setRenderBounds(xStart, unit2, unit2, xStart + unit2, 1 - unit2, unit2 + unit2);
            renderer.renderFaces(pos, icon, legFaces);

            renderer.setRenderBounds(xStart, unit2, 1 - unit2 - unit2, xStart + unit2, 1 - unit2, 1 - unit2);
            renderer.renderFaces(pos, icon, legFaces);

            renderer.clearUVRotation();
        }

        private void renderBraces(Vec3 pos, String icon, boolean left) {
            // float oldColor = renderer.state.colorMultYPos;
            // renderer.state.colorMultYPos = .85f;

            double xStart = left ? unit2 + unit2 : 0;
            double xStop = left ? 1 : 1 - unit2 - unit2;

            Direction[] braceFaces = new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.UP, Direction.DOWN};
            renderer.setRenderBounds(xStart, unit4, unit2, xStop, unit4 + unit2, unit2 + unit2);
            renderer.renderFaces(pos, icon, braceFaces);

            renderer.setRenderBounds(xStart, unit4, 1 - unit2 - unit2, xStop, unit4 + unit2, 1 - unit2);
            renderer.renderFaces(pos, icon, braceFaces);

            // renderer.state.colorMultYPos = oldColor;
        }
    }
}
