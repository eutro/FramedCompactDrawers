package eutros.framedcompactdrawers.registry;

import com.jaquadro.minecraft.chameleon.Chameleon;
import eutros.framedcompactdrawers.model.CustomControllerModel;
import eutros.framedcompactdrawers.model.CustomDrawersCompModel;
import eutros.framedcompactdrawers.model.CustomSlaveModel;
import eutros.framedcompactdrawers.utils.Reference;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(value = Side.CLIENT, modid = Reference.MOD_ID)
public class ModelRegistry {

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        ModBlocks.framedCompactDrawer.initDynamic();
        ModBlocks.framedDrawerController.initDynamic();

        Chameleon.instance.modelRegistry.registerModel(new CustomDrawersCompModel.Register());
        Chameleon.instance.modelRegistry.registerModel(new CustomControllerModel.Register());
        Chameleon.instance.modelRegistry.registerModel(new CustomSlaveModel.Register());
    }

}
