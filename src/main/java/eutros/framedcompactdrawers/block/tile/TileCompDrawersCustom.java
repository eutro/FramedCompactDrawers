package eutros.framedcompactdrawers.block.tile;

import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawersComp;
import eutros.framedcompactdrawers.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.IModelData;

import javax.annotation.Nonnull;

public abstract class TileCompDrawersCustom {

    public static class Slot3 extends TileEntityDrawersComp.Slot3 implements IFramingHolder {

        private ItemStack side = ItemStack.EMPTY;
        private ItemStack trim = ItemStack.EMPTY;
        private ItemStack front = ItemStack.EMPTY;

        public Slot3(BlockPos pos, BlockState state) {
            super(pos, state);
            type = ModBlocks.Tile.fractionalDrawers3;
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

}
