package eutros.framedcompactdrawers.block;

import com.jaquadro.minecraft.storagedrawers.block.BlockSlave;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntitySlave;
import eutros.framedcompactdrawers.block.tile.IFramingHolder;
import eutros.framedcompactdrawers.block.tile.TileSlaveCustom;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockReader;

public class BlockSlaveCustom extends BlockSlave {

    public BlockSlaveCustom(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
        TileEntity tile = world.getTileEntity(pos);
        ItemStack stack = super.getPickBlock(state, target, world, pos, player);
        if(tile instanceof IFramingHolder) {
            ((IFramingHolder) tile).writeToTag(stack.getOrCreateTag());
        }
        return stack;
    }

    @Override
    public TileEntitySlave createTileEntity(BlockState state, IBlockReader world) {
        return new TileSlaveCustom();
    }

}
