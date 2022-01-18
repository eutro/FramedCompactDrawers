package eutros.framedcompactdrawers.item;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static eutros.framedcompactdrawers.item.ItemDrawersCustom.setFrame;

@ParametersAreNonnullByDefault
public class ItemOtherCustom extends BlockItem {

    public ItemOtherCustom(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    protected boolean updateCustomBlockEntityTag(BlockPos pos, Level worldIn, @Nullable Player player, ItemStack stack, BlockState state) {
        super.updateCustomBlockEntityTag(pos, worldIn, player, stack, state);
        return setFrame(pos, worldIn, stack);
    }

}
