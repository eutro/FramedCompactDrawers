package eutros.framedcompactdrawers;

import eutros.framedcompactdrawers.registry.ModBlocks;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class FCDCreativeTab extends CreativeTabs {

    public static final CreativeTabs tab = new FCDCreativeTab("framed_compacting_drawers");

    public FCDCreativeTab(String name) {
        super(name);
    }

    @Override
    public ItemStack getTabIconItem() {
        return new ItemStack(ModBlocks.framedCompactDrawer);
    }

}
