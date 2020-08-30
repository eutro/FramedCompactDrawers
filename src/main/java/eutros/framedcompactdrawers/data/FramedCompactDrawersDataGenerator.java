package eutros.framedcompactdrawers.data;

import eutros.framedcompactdrawers.FramedCompactDrawers;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

@Mod.EventBusSubscriber(modid = FramedCompactDrawers.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class FramedCompactDrawersDataGenerator {

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent evt) {
        evt.getGenerator().addProvider(new FCDLootTableProvider(evt.getGenerator()));
    }

}
