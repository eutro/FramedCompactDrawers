package eutros.fcd.block;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.security.ISecurityProvider;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.LockAttribute;
import com.jaquadro.minecraft.storagedrawers.block.EnumKeyType;
import com.jaquadro.minecraft.storagedrawers.block.dynamic.StatusModelData;
import com.jaquadro.minecraft.storagedrawers.core.ModItems;
import eutros.fcd.block.tile.TileControllerCustom;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Random;

public class BlockControllerCustom extends AbstractKeyButtonToggle {

    private static final ThreadLocal<Boolean> inTileLookup = ThreadLocal.withInitial(() -> false);
    private StatusModelData statusInfo;

    public BlockControllerCustom() {
        super("framedcompactdrawers:framed_drawer_controller", "framedcompactdrawers.framed_drawer_controller");
    }

    @Nonnull
    @Override
    protected BlockStateContainer createTrueBlockState() {
        return new ExtendedBlockState(this, new IProperty[] {FACING}, new IUnlistedProperty[] {MAT_MODEL});
    }

    @Override
    protected void replaceDefaultState() {
        setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void initDynamic() {
        ResourceLocation location = new ResourceLocation(StorageDrawers.MOD_ID + ":models/dynamic/basicDrawers_full1.json");
        statusInfo = new StatusModelData(1, location);
    }

    @Override
    public StatusModelData getStatusInfo(IBlockState state) {
        return statusInfo;
    }

    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        if(!world.isRemote) {
            IBlockState blockNorth = world.getBlockState(pos.north());
            IBlockState blockSouth = world.getBlockState(pos.south());
            IBlockState blockWest = world.getBlockState(pos.west());
            IBlockState blockEast = world.getBlockState(pos.east());

            EnumFacing facing = state.getValue(FACING);

            if(facing == EnumFacing.NORTH && blockNorth.isFullBlock() && !blockSouth.isFullBlock())
                facing = EnumFacing.SOUTH;
            if(facing == EnumFacing.SOUTH && blockSouth.isFullBlock() && !blockNorth.isFullBlock())
                facing = EnumFacing.NORTH;
            if(facing == EnumFacing.WEST && blockWest.isFullBlock() && !blockEast.isFullBlock())
                facing = EnumFacing.EAST;
            if(facing == EnumFacing.EAST && blockEast.isFullBlock() && !blockWest.isFullBlock())
                facing = EnumFacing.WEST;

            world.setBlockState(pos, state.withProperty(FACING, facing), 2);
        }
    }

    @Override
    public void getSubBlocks(CreativeTabs creativeTabs, NonNullList<ItemStack> list) {
        // default implementation
        list.add(new ItemStack(this));
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entity, ItemStack itemStack) {
        world.setBlockState(pos, state.withProperty(FACING, entity.getHorizontalFacing().getOpposite()), 2);
    }

    @Override
    public TileControllerCustom createTileEntity(World world, @Nullable IBlockState state) {
        return new TileControllerCustom();
    }

    public TileControllerCustom getTrueTileEntity(IBlockAccess blockAccess, BlockPos pos) {
        if(inTileLookup.get())
            return null;

        inTileLookup.set(true);
        TileEntity tile = blockAccess.getTileEntity(pos);
        inTileLookup.set(false);

        return (tile instanceof TileControllerCustom) ? (TileControllerCustom) tile : null;
    }

    public TileControllerCustom getTrueTileEntitySafe(World world, BlockPos pos) {
        TileControllerCustom tile = getTrueTileEntity(world, pos);
        if(tile == null) {
            tile = createTileEntity(world, null);
            world.setTileEntity(pos, tile);
        }

        return tile;
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        if(world.isRemote)
            return;

        TileControllerCustom te = getTrueTileEntitySafe(world, pos);
        if(te == null)
            return;

        te.updateCache();

        world.scheduleUpdate(pos, this, this.tickRate(world));
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        EnumFacing blockDir = state.getValue(FACING);
        TileControllerCustom tile = getTrueTileEntitySafe(world, pos);

        if(tile == null || tile.material().getSide().isEmpty())
            return false;

        ItemStack item = player.inventory.getCurrentItem();
        if(!item.isEmpty() && toggle(world, pos, player, item.getItem()))
            return true;

        if(blockDir != side)
            return false;

        if(!world.isRemote) {
            if(StorageDrawers.config.cache.debugTrace && item.isEmpty())
                tile.printDebugInfo();

            tile.interactPutItemsIntoInventory(player);
        }

        return true;
    }

    public boolean toggle(World world, BlockPos pos, EntityPlayer player, Item item) {
        if(world.isRemote || item == null)
            return false;

        if(item == ModItems.drawerKey)
            toggle(world, pos, player, EnumKeyType.DRAWER);
        else if(item == ModItems.shroudKey)
            toggle(world, pos, player, EnumKeyType.CONCEALMENT);
        else if(item == ModItems.quantifyKey)
            toggle(world, pos, player, EnumKeyType.QUANTIFY);
        else if(item == ModItems.personalKey)
            toggle(world, pos, player, EnumKeyType.PERSONAL);
        else
            return false;

        return true;
    }

    public void toggle(World world, BlockPos pos, EntityPlayer player, EnumKeyType keyType) {
        if(world.isRemote)
            return;

        TileControllerCustom te = getTrueTileEntitySafe(world, pos);
        if(te == null)
            return;

        switch(keyType) {
            case DRAWER:
                te.toggleLock(EnumSet.allOf(LockAttribute.class), LockAttribute.LOCK_POPULATED, player.getGameProfile());
                break;
            case CONCEALMENT:
                te.toggleShroud(player.getGameProfile());
                break;
            case QUANTIFY:
                te.toggleQuantified(player.getGameProfile());
                break;
            case PERSONAL:
                String securityKey = ModItems.personalKey.getSecurityProviderKey(0);
                ISecurityProvider provider = StorageDrawers.securityRegistry.getProvider(securityKey);

                te.toggleProtection(player.getGameProfile(), provider);
                break;
        }
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        EnumFacing facing = EnumFacing.getFront(meta + 2); // ensure that 0 maps to north
        if(facing.getAxis() == EnumFacing.Axis.Y)
            facing = EnumFacing.NORTH;

        return getDefaultState().withProperty(FACING, facing);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return ((state.getValue(FACING)).getIndex() + 4) % 6; // ensure that north maps to 0, required so item metadata remains 0
    }

    @Override
    protected IBlockState droppedStateMorph(IBlockState state) {
        return state.withProperty(FACING, EnumFacing.NORTH);
    }

    @Override
    public boolean isSideSolid (IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return state.getValue(FACING) != side;
    }

}
