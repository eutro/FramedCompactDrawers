package eutros.framedcompactdrawers.block;

import com.jaquadro.minecraft.storagedrawers.block.BlockCompDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import eutros.framedcompactdrawers.block.tile.TileCompDrawersCustom;
import net.minecraft.block.BlockState;
import net.minecraft.world.IBlockReader;

public class BlockCompDrawersCustom extends BlockCompDrawers {

    public BlockCompDrawersCustom(Properties properties) {
        super(32, properties);
    }

    @Override
    public TileEntityDrawers createTileEntity(BlockState state, IBlockReader world) {
        return new TileCompDrawersCustom();
    }

}
