package eutros.framedcompactdrawers.item;

import com.jaquadro.minecraft.storagedrawers.item.ItemDrawers;
import eutros.framedcompactdrawers.block.tile.TileCompDrawersCustom;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Objects;

public class ItemCompDrawersCustom extends ItemDrawers {

    public ItemCompDrawersCustom(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    protected boolean onBlockPlaced(BlockPos pos, World worldIn, @Nullable PlayerEntity player, ItemStack stack, BlockState state) {
        super.onBlockPlaced(pos, worldIn, player, stack, state);

        TileEntity tile = worldIn.getTileEntity(pos);
        if(!(tile instanceof TileCompDrawersCustom)) {
            return false;
        }

        TileCompDrawersCustom drawers = (TileCompDrawersCustom) tile;

        if(!stack.hasTag()) {
            return true;
        }

        CompoundNBT tag = Objects.requireNonNull(stack.getTag());

        if(tag.contains("MatS"))
            drawers.side = ItemStack.read(tag.getCompound("MatS"));
        if(tag.contains("MatT"))
            drawers.trim = ItemStack.read(tag.getCompound("MatT"));
        if(tag.contains("MatF"))
            drawers.front = ItemStack.read(tag.getCompound("MatF"));

        return true;
    }

}
