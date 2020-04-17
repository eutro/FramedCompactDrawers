package eutros.fcd.proxy;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import eutros.fcd.FramedCompactDrawers;
import eutros.fcd.integration.IntegrationRegistry;
import eutros.fcd.integration.waila.WailaPlugin;
import net.minecraftforge.fml.common.Loader;

public class ClientProxy implements IProxy {

    private boolean didSuspendWailaConfig = false;

    @Override
    public void preInit() {
        didSuspendWailaConfig = StorageDrawers.config.cache.enableWailaIntegration;
        StorageDrawers.config.cache.enableWailaIntegration = false; // Overwrite their integration hahaha
    }

    @Override
    public void init() {
        IntegrationRegistry reg = FramedCompactDrawers.integrationRegistry;
        if (Loader.isModLoaded("waila") && didSuspendWailaConfig) {
            reg.add(new WailaPlugin());
        }
    }

    @Override
    public void postInit() {
        StorageDrawers.config.cache.enableWailaIntegration = didSuspendWailaConfig;
    }

}
