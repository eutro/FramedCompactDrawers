package eutros.framedcompactdrawers.model;

import com.jaquadro.minecraft.chameleon.render.ChamRender;
import com.jaquadro.minecraft.chameleon.render.helpers.ModularBoxRenderer;
import com.jaquadro.minecraft.chameleon.render.helpers.PanelBoxRenderer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

/**
 * Fork of {@link com.jaquadro.minecraft.storagedrawers.client.model.dynamic.CommonDrawerRenderer} to render properly.
 */
public class SlaveRenderer {

    private double trimWidth;
    private final ChamRender renderHelper;
    private final PanelBoxRenderer panelRenderer;

    public SlaveRenderer(ChamRender renderer) {
        this.renderHelper = renderer;
        this.panelRenderer = new PanelBoxRenderer(renderer);
    }

    private void start() {

        trimWidth = .0625;

        panelRenderer.setTrimWidth(trimWidth);
        panelRenderer.setTrimDepth(0);
        panelRenderer.setTrimColor(ModularBoxRenderer.COLOR_WHITE);
        panelRenderer.setPanelColor(ModularBoxRenderer.COLOR_WHITE);

    }

    private void end() {
        renderHelper.state.clearRotateTransform();
        renderHelper.state.clearUVRotation(ChamRender.YPOS);
    }

    public void renderBasePass(IBlockAccess world, IBlockState state, BlockPos pos, TextureAtlasSprite iconSide, TextureAtlasSprite iconTrim, TextureAtlasSprite iconTopBottom) {
        start();

        panelRenderer.setTrimIcon(iconTrim);

        renderHelper.targetFaceGroup(true);
        for(EnumFacing dir : EnumFacing.VALUES) {
            panelRenderer.setPanelIcon(dir.getAxis() == EnumFacing.Axis.Y ? iconTopBottom : iconSide);
            panelRenderer.renderFacePanel(dir, world, state, pos, 0, 0, 0, 1, 1, 1);
            panelRenderer.renderFaceTrim(dir, world, state, pos, 0, 0, 0, 1, 1, 1);
        }

        renderHelper.targetFaceGroup(false);

        end();
    }

    @SuppressWarnings("SuspiciousNameCombination") // IntelliJ more like DumbJ
    public void renderOverlayPass(IBlockAccess world, IBlockState state, BlockPos pos, TextureAtlasSprite sideShadow) {
        start();

        renderHelper.setRenderBounds(trimWidth, trimWidth, 0, 1 - trimWidth, 1 - trimWidth, 1);
        renderHelper.renderFace(ChamRender.FACE_ZNEG, world, state, pos, sideShadow);
        renderHelper.renderFace(ChamRender.FACE_ZPOS, world, state, pos, sideShadow);

        renderHelper.setRenderBounds(0, trimWidth, trimWidth, 1, 1 - trimWidth, 1 - trimWidth);
        renderHelper.renderFace(ChamRender.FACE_XNEG, world, state, pos, sideShadow);
        renderHelper.renderFace(ChamRender.FACE_XPOS, world, state, pos, sideShadow);

        end();
    }

}
