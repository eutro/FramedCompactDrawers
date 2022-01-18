package eutros.framedcompactdrawers.block.tile;

import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityController;
import eutros.framedcompactdrawers.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.ticks.LevelTickAccess;
import net.minecraft.world.ticks.TickPriority;
import net.minecraftforge.client.model.data.IModelData;

import javax.annotation.Nonnull;

public class TileControllerCustom extends TileEntityController implements IFramingHolder {

    private ItemStack side = ItemStack.EMPTY;
    private ItemStack trim = ItemStack.EMPTY;
    private ItemStack front = ItemStack.EMPTY;

    public TileControllerCustom(BlockPos pos, BlockState state) {
        super(ModBlocks.Tile.controllerCustom, pos, state);
        injectCustomData(this);
    }

    public void clearRemoved() {
        super.clearRemoved();
        if (level != null) {
            LevelTickAccess<Block> pendingTicks = level.getBlockTicks();
            if(!pendingTicks.willTickThisTick(worldPosition, ModBlocks.framedDrawerController)) {
                level.scheduleTick(worldPosition, ModBlocks.framedDrawerController, 1, TickPriority.NORMAL);
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
