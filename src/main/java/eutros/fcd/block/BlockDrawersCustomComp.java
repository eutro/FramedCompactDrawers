package eutros.fcd.block;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.INetworked;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawersCustom;
import com.jaquadro.minecraft.storagedrawers.block.EnumCompDrawer;
import com.jaquadro.minecraft.storagedrawers.block.dynamic.StatusModelData;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawersComp;
import eutros.fcd.FCDCreativeTab;
import eutros.fcd.item.ItemDrawersCustomComp;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockDrawersCustomComp extends BlockDrawersCustom implements INetworked {

    public static final PropertyEnum<EnumCompDrawer> SLOTS = PropertyEnum.create("slots", EnumCompDrawer.class);
    private StatusModelData statusInfo;

    public BlockDrawersCustomComp() {
        super("framedcompactdrawers:framed_compact_drawer", "framedcompactdrawers.framed_compact_drawer");
        // I can't be bothered to make an AT.
        ObfuscationReflectionHelper.setPrivateValue(Block.class, this, createTrueBlockState(), "blockState");
        setDefaultState(blockState.getBaseState().withProperty(SLOTS, EnumCompDrawer.OPEN3).withProperty(FACING, EnumFacing.NORTH));
        setUnlocalizedName("framedcompactdrawers.framed_compact_drawer");
        setCreativeTab(FCDCreativeTab.tab);
    }

    @Nonnull
    protected BlockStateContainer createTrueBlockState() { // overwrite the original
        return new ExtendedBlockState(this, new IProperty[] {SLOTS, FACING}, new IUnlistedProperty[] {MAT_MODEL, STATE_MODEL});
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }

    @Override
    public boolean isHalfDepth(IBlockState state) {
        return false;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState();
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return getDefaultState().withProperty(FACING, placer.getHorizontalFacing());
    }

    @Override
    protected int getDrawerSlot(int drawerCount, int side, float hitX, float hitY, float hitZ) {
        if(hitTop(hitY))
            return 0;

        if(hitLeft(side, hitX, hitZ))
            return 1;
        else
            return 2;
    }

    @Override
    public int getDrawerCount(IBlockState state) {
        return 3;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void initDynamic() {
        ResourceLocation location = new ResourceLocation(StorageDrawers.MOD_ID + ":models/dynamic/compDrawers.json");
        statusInfo = new StatusModelData(3, location);
    }

    @Override
    public StatusModelData getStatusInfo(IBlockState state) {
        return statusInfo;
    }

    @Override
    public void getSubBlocks(CreativeTabs creativeTabs, NonNullList<ItemStack> list) {
        // default implementation
        list.add(new ItemStack(this));
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        TileEntityDrawers tile = getTileEntity(world, pos);
        if(tile != null && tile.material().getSide().isEmpty())
            return false;

        return super.onBlockActivated(world, pos, state, player, hand, side, hitX, hitY, hitZ);
    }

    @Deprecated
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        // simplified default implementation, super does funky stuff
        return !blockAccess.getBlockState(pos.offset(side)).doesSideBlockRendering(blockAccess, pos.offset(side), side.getOpposite());
    }

    @Override
    public boolean doesSideBlockRendering(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing face) {
        // again, super does funky stuff
        return false;
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        TileEntityDrawers tile = getTileEntity(world, pos);
        if(tile == null)
            return state;

        EnumCompDrawer slots = EnumCompDrawer.OPEN1;
        if(tile.getGroup().getDrawer(1).isEnabled())
            slots = EnumCompDrawer.OPEN2;
        if(tile.getGroup().getDrawer(2).isEnabled())
            slots = EnumCompDrawer.OPEN3;

        EnumFacing facing = EnumFacing.getFront(tile.getDirection());
        if(facing.getAxis() == EnumFacing.Axis.Y)
            facing = EnumFacing.NORTH;

        return state.withProperty(SLOTS, slots).withProperty(FACING, facing);
    }

    @Nullable
    @Override
    public TileEntityDrawers createNewTileEntity(World worldIn, int meta) {
        return new TileEntityDrawersComp();
    }

    @Override
    @Nonnull
    protected ItemStack getMainDrop(IBlockAccess world, BlockPos pos, IBlockState state) {
        TileEntityDrawers tile = getTileEntity(world, pos);
        if(tile == null)
            return ItemDrawersCustomComp.makeItemStack(state, 1, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY);

        ItemStack drop = ItemDrawersCustomComp.makeItemStack(state, 1, tile.material().getSide(), tile.material().getTrim(), tile.material().getFront());
        if(drop.isEmpty())
            return ItemStack.EMPTY;

        NBTTagCompound data = drop.getTagCompound();
        if(data == null)
            data = new NBTTagCompound();

        boolean hasContents = false;
        withContents:
        {
            if(StorageDrawers.config.cache.keepContentsOnBreak) {
                for(int i = 0; i < tile.getGroup().getDrawerCount(); i++) {
                    IDrawer drawer = tile.getGroup().getDrawer(i);
                    if(!drawer.isEmpty()) {
                        hasContents = true;
                        break withContents;
                    }
                }
                for(int i = 0; i < tile.upgrades().getSlotCount(); i++) {
                    if(!tile.upgrades().getUpgrade(i).isEmpty()) {
                        hasContents = true;
                        break withContents;
                    }
                }
            }
        }

        if(tile.isSealed() || (StorageDrawers.config.cache.keepContentsOnBreak && hasContents)) {
            NBTTagCompound tiledata = new NBTTagCompound();
            tile.writeToNBT(tiledata);
            data.setTag("tile", tiledata);
        }

        drop.setTagCompound(data);
        return drop;
    }

}
