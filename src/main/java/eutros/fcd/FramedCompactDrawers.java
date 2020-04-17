package eutros.fcd;

import eutros.fcd.integration.IntegrationRegistry;
import eutros.fcd.proxy.IProxy;
import eutros.fcd.utils.Reference;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;

@Mod(
        modid = Reference.MOD_ID,
        name = Reference.NAME,
        version = Reference.VERSION,
        dependencies = "required-before:storagedrawers;required-after:chameleon;"
)
public class FramedCompactDrawers {

    public static IntegrationRegistry integrationRegistry = new IntegrationRegistry();

    @SidedProxy(modId = Reference.MOD_ID, clientSide = Reference.CLIENT_PROXY, serverSide = Reference.SERVER_PROXY)
    public static IProxy proxy;

    @Mod.EventHandler
    public static void init(FMLInitializationEvent evt) {
        proxy.init();
        integrationRegistry.init();
    }

    @Mod.EventHandler
    public static void postInit(FMLPostInitializationEvent evt) {
        proxy.postInit();
        integrationRegistry.postInit();
    }

}
