package eutros.framedcompactdrawers.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

import static eutros.framedcompactdrawers.item.ItemCompDrawersCustom.setFrame;

public class ItemOtherCustom extends BlockItem {

    public ItemOtherCustom(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    protected boolean onBlockPlaced(BlockPos pos, World worldIn, @Nullable PlayerEntity player, ItemStack stack, BlockState state) {
        super.onBlockPlaced(pos, worldIn, player, stack, state);
        return setFrame(pos, worldIn, stack);
    }

}
