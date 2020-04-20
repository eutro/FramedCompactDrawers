package eutros.framedcompactdrawers.integration;

import net.minecraftforge.fml.common.Loader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class IntegrationRegistry {

    private final Logger LOGGER = LogManager.getLogger("Framed Compact Drawers Integration");

    private final List<IIntegrationPlugin> registry;

    public IntegrationRegistry() {
        this.registry = new ArrayList<>();
    }

    public void add(IIntegrationPlugin module) {
        if(module.versionCheck())
            registry.add(module);
    }

    public void init() {
        for(int i = 0; i < registry.size(); i++) {
            IIntegrationPlugin module = registry.get(i);
            if(!Loader.isModLoaded(module.getModID())) {
                registry.remove(i--);
                continue;
            }

            try {
                module.init();
            } catch(Throwable t) {
                registry.remove(i--);
                LOGGER.warn("Could not load integration module: " + module.getClass().getName());
            }
        }
    }

    public void postInit() {
        for(IIntegrationPlugin module : registry)
            module.postInit();
    }

}