package eutros.fcd.registry;

import com.jaquadro.minecraft.chameleon.Chameleon;
import eutros.fcd.model.CustomDrawersCompModel;
import eutros.fcd.utils.Reference;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(value = Side.CLIENT, modid = Reference.MOD_ID)
public class ModModels {

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        ModBlocks.framedCompactDrawer.initDynamic();

        Chameleon.instance.modelRegistry.registerModel(new CustomDrawersCompModel.Register());
    }

}
