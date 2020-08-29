package eutros.framedcompactdrawers.block;

import com.jaquadro.minecraft.storagedrawers.block.BlockSlave;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntitySlave;
import eutros.framedcompactdrawers.block.tile.TileSlaveCustom;
import net.minecraft.block.BlockState;
import net.minecraft.world.IBlockReader;

public class BlockSlaveCustom extends BlockSlave {

    public BlockSlaveCustom(Properties properties) {
        super(properties);
    }

    @Override
    public TileEntitySlave createTileEntity(BlockState state, IBlockReader world) {
        return new TileSlaveCustom();
    }

}
