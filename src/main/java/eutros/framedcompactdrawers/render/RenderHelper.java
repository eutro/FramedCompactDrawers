package eutros.framedcompactdrawers.render;

import net.minecraft.client.renderer.RenderType;

public final class RenderHelper {
    public static boolean canRenderFrameable(RenderType type) {
        return type == RenderType.translucent() || type == RenderType.cutout();
    }
}
