package eutros.fcd.registry;

import eutros.fcd.block.CustomDrawerRenderer;
import eutros.fcd.block.TileEntityCustomCompDrawer;
import eutros.fcd.utils.Reference;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class ModelRegistry {

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent evt) {
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCustomCompDrawer.class, new CustomDrawerRenderer());
    }

}
