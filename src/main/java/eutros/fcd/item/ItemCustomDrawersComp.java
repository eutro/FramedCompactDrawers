package eutros.fcd.item;

import com.jaquadro.minecraft.storagedrawers.item.ItemCustomDrawers;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class ItemCustomDrawersComp extends ItemCustomDrawers {

    public ItemCustomDrawersComp(Block block) {
        super(block);
    }

    @Override
    public String getUnlocalizedName(@Nonnull ItemStack stack) {
        return this.getBlock().getUnlocalizedName();
    }

}
