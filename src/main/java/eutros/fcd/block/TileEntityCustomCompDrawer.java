package eutros.fcd.block;

import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawersComp;
import net.minecraft.item.ItemStack;

public class TileEntityCustomCompDrawer extends TileEntityDrawersComp {

    public ItemStack matSide = ItemStack.EMPTY;
    public ItemStack matTrim = ItemStack.EMPTY;
    public ItemStack matFront = ItemStack.EMPTY;

    public TileEntityCustomCompDrawer() {
        super();
    }

}
