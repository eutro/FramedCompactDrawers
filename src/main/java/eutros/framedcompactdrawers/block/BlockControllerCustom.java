package eutros.framedcompactdrawers.block;

import com.jaquadro.minecraft.storagedrawers.block.BlockController;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityController;
import eutros.framedcompactdrawers.block.tile.IFramingHolder;
import eutros.framedcompactdrawers.block.tile.TileControllerCustom;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockControllerCustom extends BlockController {

    public BlockControllerCustom(Properties properties) {
        super(properties);
    }

    @Override
    public TileEntityController createTileEntity(BlockState state, IBlockReader world) {
        return new TileControllerCustom();
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
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        TileEntity tile = world.getTileEntity(pos);
        if(tile instanceof IFramingHolder && !((IFramingHolder) tile).getSide().isEmpty()) {
            return super.onBlockActivated(state, world, pos, player, hand, hit);
        }
        return ActionResultType.PASS;
    }

}
