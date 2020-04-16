package eutros.fcd.block;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import javax.annotation.Nullable;
import java.util.BitSet;
import java.util.List;

public class CustomDrawerRenderer extends TileEntitySpecialRenderer<TileEntityCustomCompDrawer> {

    @Override
    public void render(TileEntityCustomCompDrawer te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        Block frontBlock = ((ItemBlock) te.matFront.getItem()).getBlock();
        Block sideBlock = ((ItemBlock) te.matSide.getItem()).getBlock();
        Block trimBlock = ((ItemBlock) te.matTrim.getItem()).getBlock();
        long rand = MathHelper.getPositionRandom(te.getPos());

        renderSides(te.getPos(), frontBlock, sideBlock, trimBlock, rand, EnumFacing.values()[te.getDirection()]);
    }

    public static void renderSides(BlockPos pos, Block frontBlock, Block sideBlock, Block trimBlock, long rand, EnumFacing direction) {
        BlockRendererDispatcher blockRenderer = Minecraft.getMinecraft().getBlockRendererDispatcher();
        IBakedModel frontModel = blockRenderer.getModelForState(frontBlock.getDefaultState());
        IBakedModel sideModel = blockRenderer.getModelForState(sideBlock.getDefaultState());
        IBakedModel trimModel = blockRenderer.getModelForState(trimBlock.getDefaultState());

        List<BakedQuad> quads = frontModel.getQuads(frontBlock.getDefaultState(), direction, rand);

        renderQuadsSmooth(pos, new BufferBuilder(4), quads, new float[] {0, 0, 1, 1}, new BitSet());
    }

    private static void renderQuadsSmooth(BlockPos posIn, BufferBuilder buffer, List<BakedQuad> list, float[] quadBounds, BitSet bitSet) {
        double x = posIn.getX();
        double y = posIn.getY();
        double z = posIn.getZ();
        int i = 0;

        for(int j = list.size(); i < j; ++i) {
            BakedQuad bakedquad = list.get(i);
            fillQuadBounds(bakedquad.getVertexData(), bakedquad.getFace(), quadBounds, bitSet);
            buffer.addVertexData(bakedquad.getVertexData());
            buffer.putPosition(x, y, z);
        }

    }

    private static void fillQuadBounds(int[] vertexData, EnumFacing face, @Nullable float[] quadBounds, BitSet boundsFlags) {
        float f = 32.0F;
        float f1 = 32.0F;
        float f2 = 32.0F;
        float f3 = -32.0F;
        float f4 = -32.0F;
        float f5 = -32.0F;

        int j;
        float f10;
        for(j = 0; j < 4; ++j) {
            f10 = Float.intBitsToFloat(vertexData[j * 7]);
            float f7 = Float.intBitsToFloat(vertexData[j * 7 + 1]);
            float f8 = Float.intBitsToFloat(vertexData[j * 7 + 2]);
            f = Math.min(f, f10);
            f1 = Math.min(f1, f7);
            f2 = Math.min(f2, f8);
            f3 = Math.max(f3, f10);
            f4 = Math.max(f4, f7);
            f5 = Math.max(f5, f8);
        }

        if (quadBounds != null) {
            quadBounds[EnumFacing.WEST.getIndex()] = f;
            quadBounds[EnumFacing.EAST.getIndex()] = f3;
            quadBounds[EnumFacing.DOWN.getIndex()] = f1;
            quadBounds[EnumFacing.UP.getIndex()] = f4;
            quadBounds[EnumFacing.NORTH.getIndex()] = f2;
            quadBounds[EnumFacing.SOUTH.getIndex()] = f5;
            j = EnumFacing.values().length;
            quadBounds[EnumFacing.WEST.getIndex() + j] = 1.0F - f;
            quadBounds[EnumFacing.EAST.getIndex() + j] = 1.0F - f3;
            quadBounds[EnumFacing.DOWN.getIndex() + j] = 1.0F - f1;
            quadBounds[EnumFacing.UP.getIndex() + j] = 1.0F - f4;
            quadBounds[EnumFacing.NORTH.getIndex() + j] = 1.0F - f2;
            quadBounds[EnumFacing.SOUTH.getIndex() + j] = 1.0F - f5;
        }

        switch(face) {
            case DOWN:
            case UP:
                boundsFlags.set(1, f >= 1.0E-4F || f2 >= 1.0E-4F || f3 <= 0.9999F || f5 <= 0.9999F);
                boundsFlags.set(0, f1 == f4);
                break;
            case NORTH:
            case SOUTH:
                boundsFlags.set(1, f >= 1.0E-4F || f1 >= 1.0E-4F || f3 <= 0.9999F || f4 <= 0.9999F);
                boundsFlags.set(0, f2 == f5);
                break;
            case WEST:
            case EAST:
                boundsFlags.set(1, f1 >= 1.0E-4F || f2 >= 1.0E-4F || f4 <= 0.9999F || f5 <= 0.9999F);
                boundsFlags.set(0, f == f3);
                break;
        }

    }

}
