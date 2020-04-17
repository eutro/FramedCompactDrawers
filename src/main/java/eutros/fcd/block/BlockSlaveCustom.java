package eutros.fcd.block;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.BlockController;
import com.jaquadro.minecraft.storagedrawers.block.EnumKeyType;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntitySlave;
import eutros.fcd.block.tile.TileSlaveCustom;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockSlaveCustom extends AbstractKeyButtonToggle {

    private static final ThreadLocal<Boolean> inTileLookup = ThreadLocal.withInitial(() -> false);

    public BlockSlaveCustom() {
        super("framedcompactdrawers:framed_slave", "framedcompactdrawers.framed_slave");
        watchedBlocks.add(ForgeRegistries.BLOCKS.getValue(new ResourceLocation(StorageDrawers.MOD_ID, "controllerslave")));
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
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return getDefaultState();
    }

    @Override
    public void toggle(World world, BlockPos pos, EntityPlayer player, EnumKeyType keyType) {
        TileEntity tile = world.getTileEntity(pos);
        if(!(tile instanceof TileEntitySlave))
            return;

        BlockPos controllerPos = ((TileEntitySlave) tile).getControllerPos();
        if(controllerPos == null)
            return;

        Block block = world.getBlockState(controllerPos).getBlock();
        if(block instanceof BlockController) {
            ((BlockController) block).toggle(world, controllerPos, player, keyType);
        } else if(block instanceof BlockControllerCustom) {
            ((BlockControllerCustom) block).toggle(world, controllerPos, player, keyType);
        }
    }

}
