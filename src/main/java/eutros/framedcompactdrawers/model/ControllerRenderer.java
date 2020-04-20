package eutros.framedcompactdrawers.model;

import com.jaquadro.minecraft.chameleon.render.ChamRender;
import com.jaquadro.minecraft.chameleon.render.ChamRenderState;
import com.jaquadro.minecraft.chameleon.render.helpers.ModularBoxRenderer;
import com.jaquadro.minecraft.chameleon.render.helpers.PanelBoxRenderer;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.jaquadro.minecraft.storagedrawers.block.dynamic.StatusModelData;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

/**
 * Fork of {@link com.jaquadro.minecraft.storagedrawers.client.model.dynamic.CommonDrawerRenderer} to render properly.
 */
@SuppressWarnings("SuspiciousNameCombination")
public class ControllerRenderer {

    private static double lessThanHalf = 0.4375;
    private static double moreThanHalf = 0.5625;
    private final ChamRender renderHelper;
    private final PanelBoxRenderer panelRenderer;
    private double trimWidth;
    private double trimDepth;

    public ControllerRenderer(ChamRender renderer) {
        this.renderHelper = renderer;
        this.panelRenderer = new PanelBoxRenderer(renderer);
    }

    private void start(IBlockState state, EnumFacing direction) {
        BlockDrawers block = (BlockDrawers) state.getBlock();
        StatusModelData status = block.getStatusInfo(state);

        trimWidth = .0625;
        trimDepth = status.getFrontDepth() / 16f;
        lessThanHalf = 0.4375;
        moreThanHalf = 0.5625;

        panelRenderer.setTrimWidth(trimWidth);
        panelRenderer.setTrimDepth(0);
        panelRenderer.setTrimColor(ModularBoxRenderer.COLOR_WHITE);
        panelRenderer.setPanelColor(ModularBoxRenderer.COLOR_WHITE);

        //if (world != null)
        //    renderHelper.setColorAndBrightness(world, state, pos);

        renderHelper.state.setRotateTransform(ChamRender.ZNEG, direction.getIndex());
        renderHelper.state.setUVRotation(ChamRender.YPOS, ChamRenderState.ROTATION_BY_FACE_FACE[ChamRender.ZNEG][direction.getIndex()]);
    }

    private void end() {
        renderHelper.state.clearRotateTransform();
        renderHelper.state.clearUVRotation(ChamRender.YPOS);
    }

    public void renderBasePass(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing direction, TextureAtlasSprite iconSide, TextureAtlasSprite iconTrim, TextureAtlasSprite iconFront) {
        start(state, direction);

        panelRenderer.setTrimIcon(iconTrim);
        panelRenderer.setPanelIcon(iconSide);

        for(EnumFacing dir : EnumFacing.VALUES) {
            if(dir != ChamRender.FACE_ZNEG) {
                renderHelper.targetFaceGroup(true);
                panelRenderer.renderFacePanel(dir, world, state, pos, 0, 0, 0, 1, 1, 1);
            } else renderHelper.targetFaceGroup(true);

            panelRenderer.renderFaceTrim(dir, world, state, pos, 0, 0, 0, 1, 1, 1);
            renderHelper.targetFaceGroup(false);
        }

        panelRenderer.setTrimDepth(trimDepth);
        panelRenderer.renderInteriorTrim(ChamRender.FACE_ZNEG, world, state, pos, 0, 0, 0, 1, 1, 1);

        renderHelper.setRenderBounds(trimWidth, trimWidth, trimDepth, 1 - trimWidth, lessThanHalf, 1);
        renderHelper.renderFace(ChamRender.FACE_ZNEG, world, state, pos, iconFront);

        renderHelper.setRenderBounds(trimWidth, moreThanHalf, trimDepth, 1 - trimWidth, 1 - trimWidth, 1);
        renderHelper.renderFace(ChamRender.FACE_ZNEG, world, state, pos, iconFront);

        renderHelper.setRenderBounds(trimWidth, lessThanHalf, trimDepth, 1 - trimWidth, moreThanHalf, 1);
        renderHelper.renderFace(ChamRender.FACE_ZNEG, world, state, pos, iconTrim);

        end();
    }

    public void renderOverlayPass(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing direction, TextureAtlasSprite trimShadow, TextureAtlasSprite handle, TextureAtlasSprite faceShadow) {
        start(state, direction);

        boolean shadeFace = faceShadow != null;

        renderHelper.setRenderBounds(trimWidth, trimWidth, trimDepth, 1 - trimWidth, lessThanHalf, 1);
        renderHelper.renderFace(ChamRender.FACE_ZNEG, world, state, pos, handle);
        if(shadeFace) renderHelper.renderFace(ChamRender.FACE_ZNEG, world, state, pos, faceShadow);

        renderHelper.setRenderBounds(trimWidth, moreThanHalf, trimDepth, 1 - trimWidth, 1 - trimWidth, 1);
        renderHelper.renderFace(ChamRender.FACE_ZNEG, world, state, pos, handle);
        if(shadeFace) renderHelper.renderFace(ChamRender.FACE_ZNEG, world, state, pos, faceShadow);

        renderHelper.setRenderBounds(trimWidth, lessThanHalf, trimDepth, 1 - trimWidth, moreThanHalf, 1);
        renderHelper.renderFace(ChamRender.FACE_ZNEG, world, state, pos, trimShadow);

        end();
    }

}
