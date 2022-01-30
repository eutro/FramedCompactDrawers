package eutros.framedcompactdrawers.block.tile;

import eutros.framedcompactdrawers.block.ModBlocks;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.IModelData;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TileTrimCustom extends BlockEntity implements IFramingHolder {

    private ItemStack side = ItemStack.EMPTY;
    private ItemStack trim = ItemStack.EMPTY;

    public TileTrimCustom(BlockPos pos, BlockState state) {
        super(ModBlocks.Tile.trimCustom, pos, state);
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        super.deserializeNBT(nbt);
        readFromTag(nbt);
    }

    @Override
    public CompoundTag serializeNBT() {
        return writeToTag(super.serializeNBT());
    }

    @Override
    public CompoundTag getUpdateTag() {
        return serializeNBT();
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
        return ItemStack.EMPTY;
    }

    @Override
    public void setFront(ItemStack front) {
        // NOP
    }

}
