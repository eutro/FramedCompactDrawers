package eutros.framedcompactdrawers;

import eutros.framedcompactdrawers.registry.ModBlocks;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class FCDCreativeTab extends ItemGroup {

    public static final ItemGroup tab = new FCDCreativeTab("framed_compacting_drawers");

    public FCDCreativeTab(String name) {
        super(name);
    }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(ModBlocks.framedCompactDrawer);
    }

}
