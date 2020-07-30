package eutros.framedcompactdrawers;

import eutros.framedcompactdrawers.registry.ModBlocks;
import eutros.framedcompactdrawers.utils.Reference;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Reference.MOD_ID)
public class FramedCompactDrawers {

    public FramedCompactDrawers() {
        IEventBus eb = FMLJavaModLoadingContext.get().getModEventBus();
        eb.addGenericListener(Block.class, ModBlocks::registerBlocks);
        eb.addGenericListener(Item.class, ModBlocks::registerItems);
    }

}
