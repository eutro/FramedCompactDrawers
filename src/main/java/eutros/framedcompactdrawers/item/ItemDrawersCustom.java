package eutros.framedcompactdrawers.item;

import com.jaquadro.minecraft.storagedrawers.item.ItemDrawers;
import eutros.framedcompactdrawers.block.tile.IFramingHolder;
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

public class ItemDrawersCustom extends ItemDrawers {

    public ItemDrawersCustom(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    protected boolean onBlockPlaced(BlockPos pos, World worldIn, @Nullable PlayerEntity player, ItemStack stack, BlockState state) {
        super.onBlockPlaced(pos, worldIn, player, stack, state);
        return setFrame(pos, worldIn, stack);
    }

    public static boolean setFrame(BlockPos pos, World worldIn, ItemStack stack) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if(!(tile instanceof IFramingHolder)) {
            return false;
        }

        if(!stack.hasTag()) {
            return true;
        }

        IFramingHolder frameable = (IFramingHolder) tile;
        CompoundNBT tag = Objects.requireNonNull(stack.getTag());

        if(tag.contains("MatS"))
            frameable.setSide(ItemStack.read(tag.getCompound("MatS")));
        if(tag.contains("MatT"))
            frameable.setTrim(ItemStack.read(tag.getCompound("MatT")));
        if(tag.contains("MatF"))
            frameable.setFront(ItemStack.read(tag.getCompound("MatF")));

        return true;
    }

}
