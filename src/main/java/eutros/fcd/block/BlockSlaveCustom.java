package eutros.fcd.block;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.BlockController;
import com.jaquadro.minecraft.storagedrawers.block.BlockKeyButton;
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
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.jaquadro.minecraft.storagedrawers.core.ModBlocks.controllerSlave;

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

    public void toggle(World world, BlockPos pos, EntityPlayer player, EnumKeyType keyType) {
        TileEntitySlave tile = getTrueTileEntity(world, pos);
        if(tile == null)
            return;

        BlockPos controllerPos = tile.getControllerPos();
        if(controllerPos == null)
            return;

        Block block = world.getBlockState(controllerPos).getBlock();
        if(block instanceof BlockController) {
            ((BlockController) block).toggle(world, controllerPos, player, keyType);
        } else if(block instanceof BlockControllerCustom) {
            ((BlockControllerCustom) block).toggle(world, controllerPos, player, keyType);
        }
    }

    @SubscribeEvent
    public void onBlockChange(BlockEvent.NeighborNotifyEvent event) {
        World world = event.getWorld();
        BlockPos pos = event.getPos();
        IBlockState button = world.getBlockState(pos);
        if(!(button.getBlock() instanceof BlockKeyButton))
            return;

        if(!button.getValue(BlockKeyButton.POWERED))
            return;

        BlockPos targetPos = pos.offset(button.getValue(BlockKeyButton.FACING).getOpposite());
        IBlockState targetState = world.getBlockState(targetPos);
        if(targetState.getBlock() == controllerSlave) {
            advancedNeighbourChanged(world, targetPos, button, pos);
        }
    }

    private void advancedNeighbourChanged(World world, BlockPos pos, IBlockState button, BlockPos fromPos) {
        TileEntity tile = world.getTileEntity(pos);
        if(!(tile instanceof TileEntitySlave))
            return;

        BlockPos controllerPos = ((TileEntitySlave) tile).getControllerPos();
        if(controllerPos == null)
            return;

        EntityPlayer player = buttonPosPlayerMap.remove(fromPos);

        if(player == null)
            return;

        Block block = world.getBlockState(controllerPos).getBlock();
        if(block instanceof BlockControllerCustom) {
            ((BlockControllerCustom) block).toggle(world, controllerPos, player, button.getValue(BlockKeyButton.VARIANT));
        }
    }

}
