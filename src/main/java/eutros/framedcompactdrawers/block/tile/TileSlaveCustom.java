package eutros.framedcompactdrawers.block.tile;

import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntitySlave;
import eutros.framedcompactdrawers.block.ModBlocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.data.IModelData;

import javax.annotation.Nonnull;

public class TileSlaveCustom extends TileEntitySlave implements IFramingHolder {

    private ItemStack side = ItemStack.EMPTY;
    private ItemStack trim = ItemStack.EMPTY;
    private ItemStack front = ItemStack.EMPTY;

    public TileSlaveCustom() {
        super(ModBlocks.Tile.slaveCustom);
        injectCustomData(this);
    }

    @Nonnull
    @Override
    public IModelData getModelData() {
        return getCustomModelData(super.getModelData(), this);
    }

    @Override
    public ItemStack getSide() {
        return side;
    }

    @Override
    public void setSide(ItemStack side) {
        this.side = side;
    }

    @Override
    public ItemStack getTrim() {
        return trim;
    }

    @Override
    public void setTrim(ItemStack trim) {
        this.trim = trim;
    }

    @Override
    public ItemStack getFront() {
        return front;
    }

    @Override
    public void setFront(ItemStack front) {
        this.front = front;
    }

}
