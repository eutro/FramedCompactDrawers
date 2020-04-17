package eutros.fcd.block;

import eutros.fcd.block.tile.MaterialModelCarrier;
import eutros.fcd.block.tile.TileSlaveCustom;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockSlaveCustom extends AbstractBlockDrawersCustom {

    private static final ThreadLocal<Boolean> inTileLookup = ThreadLocal.withInitial(() -> false);

    public BlockSlaveCustom() {
        super("framedcompactdrawers:framed_slave", "framedcompactdrawers.framed_slave");
    }

    @Nonnull
    @Override
    protected BlockStateContainer createTrueBlockState() {
        return new ExtendedBlockState(this, new IProperty[0], new IUnlistedProperty[] {MAT_MODEL});
    }

    @Override
    protected void replaceDefaultState() {
        blockState.getBaseState();
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        state = super.getActualState(state, world, pos);
        if(!(state instanceof IExtendedBlockState))
            return state;

        TileSlaveCustom tile = getTrueTileEntity(world, pos);
        if(tile == null)
            return state;

        return ((IExtendedBlockState) state).withProperty(MAT_MODEL, MaterialModelCarrier.materialFrom(tile));
    }

    @Override
    public TileSlaveCustom createTileEntity(World world, @Nullable IBlockState state) {
        return new TileSlaveCustom();
    }

    public TileSlaveCustom getTrueTileEntity(IBlockAccess blockAccess, BlockPos pos) {
        if(inTileLookup.get())
            return null;

        inTileLookup.set(true);
        TileEntity tile = blockAccess.getTileEntity(pos);
        inTileLookup.set(false);

        return (tile instanceof TileSlaveCustom) ? (TileSlaveCustom) tile : null;
    }

    public TileSlaveCustom getTrueTileEntitySafe(World world, BlockPos pos) {
        TileSlaveCustom tile = getTrueTileEntity(world, pos);
        if(tile == null) {
            tile = createTileEntity(world, null);
            world.setTileEntity(pos, tile);
        }

        return tile;
    }

}
