package eutros.framedcompactdrawers.block.tile;

import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityController;
import eutros.framedcompactdrawers.block.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.ITickList;
import net.minecraft.world.TickPriority;
import net.minecraftforge.client.model.data.IModelData;

import javax.annotation.Nonnull;

public class TileControllerCustom extends TileEntityController implements IFramingHolder {

    private ItemStack side = ItemStack.EMPTY;
    private ItemStack trim = ItemStack.EMPTY;
    private ItemStack front = ItemStack.EMPTY;

    public TileControllerCustom() {
        super(ModBlocks.Tile.controllerCustom);
    }

    public void validate() {
        super.validate();
        if (world != null) {
            ITickList<Block> pendingTicks = world.getPendingBlockTicks();
            if(!pendingTicks.isTickScheduled(pos, ModBlocks.framedDrawerController)) {
                pendingTicks.scheduleTick(pos, ModBlocks.framedDrawerController, 1, TickPriority.NORMAL);
            }
        }
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
