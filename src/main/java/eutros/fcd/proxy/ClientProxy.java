package eutros.fcd.proxy;

import com.jaquadro.minecraft.chameleon.Chameleon;
import eutros.fcd.model.CustomDrawersCompModel;
import eutros.fcd.registry.ModBlocks;
import eutros.fcd.utils.Reference;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientProxy extends CommonProxy {
    @Override
    public void preInit(FMLPreInitializationEvent e) {
        super.preInit(e);
    }

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        ModelLoader.setCustomMeshDefinition(Item.getItemFromBlock(ModBlocks.framedCompactDrawer), stack -> new ModelResourceLocation(Reference.MOD_ID + ":framed_compact_drawer", "inventory"));

        ModBlocks.framedCompactDrawer.initDynamic();

        Chameleon.instance.modelRegistry.registerModel(new CustomDrawersCompModel.Register());
    }
}
