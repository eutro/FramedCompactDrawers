package eutros.framedcompactdrawers.block.tile;

import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntitySlave;
import eutros.framedcompactdrawers.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;

import javax.annotation.Nonnull;

public class TileSlaveCustom extends BlockEntitySlave implements IFramingHolder {

    private ItemStack side = ItemStack.EMPTY;
    private ItemStack trim = ItemStack.EMPTY;
    private ItemStack front = ItemStack.EMPTY;

    public TileSlaveCustom(BlockPos pos, BlockState state) {
        super(ModBlocks.Tile.slaveCustom, pos, state);
        injectCustomData(this);
    }

    @Nonnull
    @Override
    public ModelData getModelData() {
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
