package eutros.fcd;

import eutros.fcd.registry.ModBlocks;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class FCDCreativeTab extends CreativeTabs {

    public FCDCreativeTab(String name) {
        super(name);
    }

    @Override
    public ItemStack getTabIconItem() {
        return new ItemStack(ModBlocks.framedCompactDrawer);
    }

}
