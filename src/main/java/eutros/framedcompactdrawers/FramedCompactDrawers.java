package eutros.framedcompactdrawers;

import eutros.framedcompactdrawers.registry.ModBlocks;
import eutros.framedcompactdrawers.registry.ModelRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(FramedCompactDrawers.MOD_ID)
public class FramedCompactDrawers {

    public static final String MOD_ID = "framedcompactdrawers";
    public static final ItemGroup CREATIVE_TAB = new ItemGroup("framed_compacting_drawers") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(ModBlocks.framedCompactDrawer);
        }
    };

    public FramedCompactDrawers() {
        IEventBus eb = FMLJavaModLoadingContext.get().getModEventBus();
        eb.addGenericListener(Block.class, ModBlocks::registerBlocks);
        eb.addGenericListener(Item.class, ModBlocks::registerItems);
        eb.addListener(ModelRegistry::registerModels);
    }

}
