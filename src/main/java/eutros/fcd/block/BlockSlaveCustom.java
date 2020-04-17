package eutros.fcd.block;

import com.jaquadro.minecraft.storagedrawers.item.ItemCustomDrawers;
import eutros.fcd.block.tile.MaterialModelCarrier;
import eutros.fcd.block.tile.TileSlaveCustom;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
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
        setDefaultState(blockState.getBaseState());
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
    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entity, ItemStack itemStack) {
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

    @Override
    public void getSubBlocks(CreativeTabs creativeTabs, NonNullList<ItemStack> list) {
        list.add(new ItemStack(this));
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        return false;
    }

    @Override
    public boolean addHitEffects(IBlockState state, World worldObj, RayTraceResult target, ParticleManager manager) {
        return false;
    }

    @Override
    public void onBlockClicked(World worldIn, BlockPos pos, EntityPlayer playerIn) {
    }

    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        worldIn.removeTileEntity(pos);
    }

    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        if(willHarvest)
            return true;
        this.onBlockHarvested(world, pos, state, player);
        return world.setBlockState(pos, Blocks.AIR.getDefaultState(), world.isRemote ? 11 : 3);
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return getDefaultState();
    }

    @Nonnull
    @Override
    protected ItemStack getMainDrop(IBlockAccess world, BlockPos pos, IBlockState state) {
        TileSlaveCustom tile = getTrueTileEntity(world, pos);
        if(tile == null)
            return ItemCustomDrawers.makeItemStack(state,
                    1,
                    ItemStack.EMPTY,
                    ItemStack.EMPTY,
                    ItemStack.EMPTY);

        return ItemCustomDrawers.makeItemStack(state,
                1,
                tile.material().getSide(),
                tile.material().getTrim(),
                tile.material().getFront());
    }

}
