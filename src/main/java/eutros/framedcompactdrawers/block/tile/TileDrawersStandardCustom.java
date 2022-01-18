package eutros.framedcompactdrawers.block.tile;

import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawersStandard;
import eutros.framedcompactdrawers.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.IModelData;

import javax.annotation.Nonnull;

public abstract class TileDrawersStandardCustom {

    @Nonnull
    public static TileEntityDrawersStandard createEntity(int drawerCount, BlockPos pos, BlockState state) {
        return switch (drawerCount) {
            case 1 -> new Slot1(pos, state);
            case 2 -> new Slot2(pos, state);
            case 4 -> new Slot4(pos, state);
            default -> throw new IllegalArgumentException("Bad drawer count: " + drawerCount);
        };
    }

    public static class Slot4 extends TileEntityDrawersStandard.Slot4 implements IFramingHolder {

        private ItemStack side = ItemStack.EMPTY;
        private ItemStack trim = ItemStack.EMPTY;
        private ItemStack front = ItemStack.EMPTY;

        public Slot4(BlockPos pos, BlockState state) {
            super(pos, state);
            type = ModBlocks.Tile.standardDrawers4;
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

    public static class Slot2 extends TileEntityDrawersStandard.Slot2 implements IFramingHolder {

        private ItemStack side = ItemStack.EMPTY;
        private ItemStack trim = ItemStack.EMPTY;
        private ItemStack front = ItemStack.EMPTY;

        public Slot2(BlockPos pos, BlockState state) {
            super(pos, state);
            type = ModBlocks.Tile.standardDrawers2;
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

    public static class Slot1 extends TileEntityDrawersStandard.Slot1 implements IFramingHolder {

        private ItemStack side = ItemStack.EMPTY;
        private ItemStack trim = ItemStack.EMPTY;
        private ItemStack front = ItemStack.EMPTY;

        public Slot1(BlockPos pos, BlockState state) {
            super(pos, state);
            type = ModBlocks.Tile.standardDrawers1;
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
