package eutros.framedcompactdrawers.block;

import com.jaquadro.minecraft.storagedrawers.block.BlockSlave;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntitySlave;
import eutros.framedcompactdrawers.block.tile.IFramingHolder;
import eutros.framedcompactdrawers.block.tile.TileSlaveCustom;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;

public class BlockSlaveCustom extends BlockSlave {

    public BlockSlaveCustom(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack getPickBlock(BlockState state, HitResult target, BlockGetter world, BlockPos pos, Player player) {
        BlockEntity tile = world.getBlockEntity(pos);
        ItemStack stack = super.getPickBlock(state, target, world, pos, player);
        if(tile instanceof IFramingHolder) {
            ((IFramingHolder) tile).writeToTag(stack.getOrCreateTag());
        }
        return stack;
    }

    @Override
    public TileEntitySlave newBlockEntity(BlockPos pos, BlockState state) {
        return new TileSlaveCustom(pos, state);
    }

}
