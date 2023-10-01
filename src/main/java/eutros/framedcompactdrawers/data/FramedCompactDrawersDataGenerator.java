package eutros.framedcompactdrawers.data;

import eutros.framedcompactdrawers.FramedCompactDrawers;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = FramedCompactDrawers.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class FramedCompactDrawersDataGenerator {

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent evt) {
        DataGenerator gen = evt.getGenerator();
        PackOutput output = gen.getPackOutput();
        gen.addProvider(evt.includeServer(), new FCDLootTableProvider(output));
        gen.addProvider(evt.includeServer(), new FCDRecipeProvider(output));
        FCDTagProvider.register(evt);
    }

}
