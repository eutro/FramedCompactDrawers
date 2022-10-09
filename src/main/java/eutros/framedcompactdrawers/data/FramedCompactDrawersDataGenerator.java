package eutros.framedcompactdrawers.data;

import eutros.framedcompactdrawers.FramedCompactDrawers;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.data.event.GatherDataEvent;

@Mod.EventBusSubscriber(modid = FramedCompactDrawers.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class FramedCompactDrawersDataGenerator {

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent evt) {
        DataGenerator gen = evt.getGenerator();
        ExistingFileHelper efh = evt.getExistingFileHelper();
        gen.addProvider(evt.includeServer(), new FCDLootTableProvider(gen));
        gen.addProvider(evt.includeServer(), new FCDRecipeProvider(gen));
        FCDTagProvider.register(evt, gen, efh);
    }

}
