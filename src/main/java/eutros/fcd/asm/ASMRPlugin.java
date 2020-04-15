package eutros.fcd.asm;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

import javax.annotation.Nullable;
import java.util.Map;

import static net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.*;

@TransformerExclusions({"eutros.fcd.asm"})
@MCVersion("1.12.2")
@Name("Framed Storage Drawers Tweaker")
public class ASMRPlugin implements IFMLLoadingPlugin {

    @Override
    public String[] getASMTransformerClass() {
        return new String[] {"eutros.fcd.asm.FCDTransformer"};
    }

    @Override
    public String getModContainerClass() {
        return "eutros.fcd.asm.FCDContainer";
    }

    @Nullable
    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {

    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }

}
