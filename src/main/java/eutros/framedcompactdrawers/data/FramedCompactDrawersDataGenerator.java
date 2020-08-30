package eutros.framedcompactdrawers.data;

import eutros.framedcompactdrawers.FramedCompactDrawers;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

@Mod.EventBusSubscriber(modid = FramedCompactDrawers.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class FramedCompactDrawersDataGenerator {

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent evt) {
        DataGenerator gen = evt.getGenerator();
        gen.addProvider(new FCDLootTableProvider(gen));
        gen.addProvider(new FCDRecipeProvider(gen));
    }

}
