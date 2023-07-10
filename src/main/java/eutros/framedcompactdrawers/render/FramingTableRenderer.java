package eutros.framedcompactdrawers.render;

import com.mojang.blaze3d.vertex.PoseStack;
import eutros.framedcompactdrawers.block.tile.TileFramingTable;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class FramingTableRenderer implements BlockEntityRenderer<TileFramingTable> {
    private final BlockEntityRendererProvider.Context ctx;

    public FramingTableRenderer(BlockEntityRendererProvider.Context ctx) {
        this.ctx = ctx;
    }

    @Override
    public void render(TileFramingTable table,
                       float partialTick,
                       PoseStack poseStack,
                       MultiBufferSource bufferSource,
                       int packedLight,
                       int packedOverlay) {

    }
}
