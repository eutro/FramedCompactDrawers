package eutros.framedcompactdrawers.block;

import com.jaquadro.minecraft.storagedrawers.item.ItemCustomDrawers;
import eutros.framedcompactdrawers.block.tile.MaterialModelCarrier;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.block.Block;
import net.minecraftforge.common.property.IExtendedBlockState;

import javax.annotation.Nonnull;

abstract public class AbstractBlockCustomNonDrawer extends AbstractBlockDrawersCustom {

    private final Block blockDelegate;

    public AbstractBlockCustomNonDrawer(String registryName, String blockName) {
        super(registryName, blockName);
        blockDelegate = new Block(Material.WOOD);
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
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        return false;
    }

    @Override
    public boolean canProvidePower(IBlockState state) {
        return false;
    }

    @Override
    @Nonnull
    protected ItemStack getMainDrop(IBlockAccess world, BlockPos pos, IBlockState state) {
        MaterialModelCarrier.IMaterialDataCarrier tile = getTrueTileEntity(world, pos);
        if(tile == null)
            return ItemCustomDrawers.makeItemStack(droppedStateMorph(state),
                    1,
                    ItemStack.EMPTY,
                    ItemStack.EMPTY,
                    ItemStack.EMPTY);

        return ItemCustomDrawers.makeItemStack(droppedStateMorph(state),
                1,
                tile.material().getSide(),
                tile.material().getTrim(),
                tile.material().getFront());
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        state = super.getActualState(state, world, pos);
        if(!(state instanceof IExtendedBlockState))
            return state;

        MaterialModelCarrier.IMaterialDataCarrier tile = getTrueTileEntity(world, pos);
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

    protected IBlockState droppedStateMorph(IBlockState state) {
        return state;
    }

    protected abstract MaterialModelCarrier.IMaterialDataCarrier getTrueTileEntity(IBlockAccess world, BlockPos pos);

    @Override
    public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis) {
        return blockDelegate.rotateBlock(world, pos, axis);
    }

}
