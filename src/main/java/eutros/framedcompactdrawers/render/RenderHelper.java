package eutros.framedcompactdrawers.render;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.vertex.VertexFormat;

public abstract class RenderHelper extends RenderType {

    public static boolean canRenderFrameable(RenderType type) {
        return type == getTranslucent() || type == getCutout();
    }

    private RenderHelper(String nameIn, VertexFormat formatIn, int drawModeIn, int bufferSizeIn, boolean useDelegateIn, boolean needsSortingIn, Runnable setupTaskIn, Runnable clearTaskIn) {
        super(nameIn, formatIn, drawModeIn, bufferSizeIn, useDelegateIn, needsSortingIn, setupTaskIn, clearTaskIn);
    }

}
