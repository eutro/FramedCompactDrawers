package eutros.framedcompactdrawers.block.tile;

import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawersStandard;
import eutros.framedcompactdrawers.block.ModBlocks;
import eutros.framedcompactdrawers.mixin.AccessorTileEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.data.IModelData;

import javax.annotation.Nonnull;

public abstract class TileDrawersStandardCustom {

    @Nonnull
    public static TileEntityDrawersStandard createEntity(int drawerCount) {
        switch(drawerCount) {
            case 1:
                return new Slot1();
            case 2:
                return new Slot2();
            case 4:
                return new Slot4();
            default:
                throw new IllegalArgumentException("Bad drawer count: " + drawerCount);
        }
    }

    public static class Slot4 extends TileEntityDrawersStandard.Slot4 implements IFramingHolder {

        private ItemStack side = ItemStack.EMPTY;
        private ItemStack trim = ItemStack.EMPTY;
        private ItemStack front = ItemStack.EMPTY;

        public Slot4() {
            super();
            ((AccessorTileEntity) this).setType(ModBlocks.Tile.standardDrawers4);
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

        public Slot2() {
            super();
            ((AccessorTileEntity) this).setType(ModBlocks.Tile.standardDrawers2);
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

        public Slot1() {
            super();
            ((AccessorTileEntity) this).setType(ModBlocks.Tile.standardDrawers1);
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
