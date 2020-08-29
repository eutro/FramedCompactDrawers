package eutros.framedcompactdrawers.block;

import com.jaquadro.minecraft.storagedrawers.block.BlockController;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityController;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import eutros.framedcompactdrawers.block.tile.IFramingHolder;
import eutros.framedcompactdrawers.block.tile.TileCompDrawersCustom;
import eutros.framedcompactdrawers.block.tile.TileControllerCustom;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
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
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        TileEntity tile = world.getTileEntity(pos);
        if(tile instanceof IFramingHolder && !((IFramingHolder) tile).getSide().isEmpty()) {
            return super.onBlockActivated(state, world, pos, player, hand, hit);
        }
        return ActionResultType.PASS;
    }

}
