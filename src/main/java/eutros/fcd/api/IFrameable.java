package eutros.fcd.api;

import net.minecraft.item.ItemStack;

public interface IFrameable {

    ItemStack framed(ItemStack frameable, ItemStack matSide, ItemStack matTrim, ItemStack matFront);

}
