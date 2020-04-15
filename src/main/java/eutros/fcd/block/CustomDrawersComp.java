package eutros.fcd.block;

import com.jaquadro.minecraft.chameleon.block.properties.UnlistedModelData;
import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.security.ISecurityProvider;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerAttributes;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerAttributesModifiable;
import com.jaquadro.minecraft.storagedrawers.api.storage.INetworked;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.LockAttribute;
import com.jaquadro.minecraft.storagedrawers.block.EnumCompDrawer;
import com.jaquadro.minecraft.storagedrawers.block.modeldata.MaterialModelData;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.capabilities.CapabilityDrawerAttributes;
import com.jaquadro.minecraft.storagedrawers.config.ConfigManager;
import com.jaquadro.minecraft.storagedrawers.config.PlayerConfigSetting;
import com.jaquadro.minecraft.storagedrawers.core.ModItems;
import com.jaquadro.minecraft.storagedrawers.core.handlers.GuiHandler;
import com.jaquadro.minecraft.storagedrawers.inventory.DrawerInventoryHelper;
import com.jaquadro.minecraft.storagedrawers.item.*;
import com.jaquadro.minecraft.storagedrawers.security.SecurityManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

public class CustomDrawersComp extends BlockContainer implements INetworked {

    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
    public static final PropertyEnum<EnumCompDrawer> SLOTS = PropertyEnum.create("slots", EnumCompDrawer.class);

    private static final IUnlistedProperty<MaterialModelData> MAT_MODEL = UnlistedModelData.create(MaterialModelData.class);

    public CustomDrawersComp() {
        super(Material.WOOD);
        setUnlocalizedName("framedcompactdrawers.framed_compact_drawer");
        setDefaultState(blockState.getBaseState().withProperty(SLOTS, EnumCompDrawer.OPEN3).withProperty(FACING, EnumFacing.NORTH));
    }

    @Nonnull
    @Override
    protected BlockStateContainer createBlockState() {
        return new ExtendedBlockState(this, new IProperty[] {SLOTS, FACING}, new IUnlistedProperty[] {MAT_MODEL});
    }

    public EnumFacing getDirection(IBlockAccess blockAccess, BlockPos pos) {
        TileEntityDrawers tile = getTileEntity(blockAccess, pos);
        return (tile != null) ? EnumFacing.getFront(tile.getDirection()) : EnumFacing.NORTH;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public IBlockState getStateForPlacement (World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return getDefaultState();
    }

    @Override
    public void getSubBlocks (CreativeTabs creativeTabs, NonNullList<ItemStack> list) {
        list.add(new ItemStack(this, 1, 0));
    }

    @Override
    public int getMetaFromState (IBlockState state) {
        return (state.getValue(SLOTS).getMetadata() << 2) + state.getValue(FACING).getHorizontalIndex();
    }

    @SuppressWarnings("deprecation")
    @Override
    public IBlockState getActualState (IBlockState state, IBlockAccess world, BlockPos pos) {
        TileEntityDrawers tile = getTileEntity(world, pos);
        if (tile == null)
            return state;

        EnumCompDrawer slots = EnumCompDrawer.OPEN1;
        if (tile.getGroup().getDrawer(1).isEnabled())
            slots = EnumCompDrawer.OPEN2;
        if (tile.getGroup().getDrawer(2).isEnabled())
            slots = EnumCompDrawer.OPEN3;

        return super.getActualState(state, world, pos).withProperty(SLOTS, slots);
    }

    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    @Override
    public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
        return layer == BlockRenderLayer.CUTOUT_MIPPED || layer == BlockRenderLayer.TRANSLUCENT;
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

            TileEntityDrawers tile = getTileEntitySafe(world, pos);
            tile.setDirection(facing.ordinal());
            tile.markDirty();

            world.setBlockState(pos, state.withProperty(FACING, facing));
        }

        super.onBlockAdded(world, pos, state);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entity, ItemStack itemStack) {
        EnumFacing facing = entity.getHorizontalFacing().getOpposite();

        TileEntityDrawers tile = getTileEntitySafe(world, pos);
        tile.setDirection(facing.ordinal());
        tile.markDirty();

        if(itemStack.hasDisplayName())
            tile.setInventoryName(itemStack.getDisplayName());

        world.setBlockState(pos, state.withProperty(FACING, facing), 3);

        if(entity.getHeldItemOffhand().getItem() == ModItems.drawerKey) {
            IDrawerAttributes _attrs = tile.getCapability(CapabilityDrawerAttributes.DRAWER_ATTRIBUTES_CAPABILITY, null);
            if(_attrs instanceof IDrawerAttributesModifiable) {
                IDrawerAttributesModifiable attrs = (IDrawerAttributesModifiable) _attrs;
                attrs.setItemLocked(LockAttribute.LOCK_EMPTY, true);
                attrs.setItemLocked(LockAttribute.LOCK_POPULATED, true);
            }
        }
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        ItemStack item = player.getHeldItem(hand);
        if(hand == EnumHand.OFF_HAND)
            return false;

        TileEntityDrawers tileDrawers = getTileEntitySafe(world, pos);

        if(!SecurityManager.hasAccess(player.getGameProfile(), tileDrawers))
            return false;

        if(StorageDrawers.config.cache.debugTrace) {
            StorageDrawers.log.info("BlockDrawers.onBlockActivated");
            StorageDrawers.log.info((item.isEmpty()) ? "  null item" : "  " + item.toString());
        }

        if(!item.isEmpty()) {
            if(item.getItem() instanceof ItemKey)
                return false;

            if(item.getItem() instanceof ItemTrim && player.isSneaking()) {
                return false;
            } else if(item.getItem() instanceof ItemUpgrade) {
                if(!tileDrawers.upgrades().canAddUpgrade(item)) {
                    if(!world.isRemote)
                        player.sendStatusMessage(new TextComponentTranslation("storagedrawers.msg.cannotAddUpgrade"), true);

                    return false;
                }

                if(!tileDrawers.upgrades().addUpgrade(item)) {
                    if(!world.isRemote)
                        player.sendStatusMessage(new TextComponentTranslation("storagedrawers.msg.maxUpgrades"), true);

                    return false;
                }

                world.notifyBlockUpdate(pos, state, state, 3);

                if(!player.capabilities.isCreativeMode) {
                    item.shrink(1);
                    if(item.getCount() <= 0)
                        player.inventory.setInventorySlotContents(player.inventory.currentItem, ItemStack.EMPTY);
                }

                return true;
            } else if(item.getItem() instanceof ItemPersonalKey) {
                String securityKey = ((ItemPersonalKey) item.getItem()).getSecurityProviderKey(item.getItemDamage());
                ISecurityProvider provider = StorageDrawers.securityRegistry.getProvider(securityKey);

                if(tileDrawers.getOwner() == null) {
                    tileDrawers.setOwner(player.getPersistentID());
                    tileDrawers.setSecurityProvider(provider);
                } else if(SecurityManager.hasOwnership(player.getGameProfile(), tileDrawers)) {
                    tileDrawers.setOwner(null);
                    tileDrawers.setSecurityProvider(null);
                } else
                    return false;
                return true;
            } else if(item.getItem() == ModItems.tape)
                return false;
        } else if(item.isEmpty() && player.isSneaking()) {
            if(tileDrawers.isSealed()) {
                tileDrawers.setIsSealed(false);
                return true;
            } else if(StorageDrawers.config.cache.enableDrawerUI) {
                player.openGui(StorageDrawers.instance, GuiHandler.drawersGuiID, world, pos.getX(), pos.getY(), pos.getZ());
                return true;
            }
        }

        if(tileDrawers.getDirection() != side.ordinal())
            return false;

        if(tileDrawers.isSealed())
            return false;

        int slot = getDrawerSlot(side.ordinal(), hitX, hitY, hitZ);
        tileDrawers.interactPutItemsIntoSlot(slot, player);

        if(item.isEmpty())
            player.setHeldItem(hand, ItemStack.EMPTY);

        return true;
    }

    protected int getDrawerSlot(int side, float hitX, float hitY, float hitZ) {
        if(hitTop(hitY)) {
            return 0;
        }

        return hitLeft(side, hitX, hitZ) ? 1 : 2;
    }

    protected boolean hitTop(float hitY) {
        return hitY > .5;
    }

    protected boolean hitLeft(int side, float hitX, float hitZ) {
        switch(side) {
            case 2:
                return hitX > .5;
            case 3:
                return hitX < .5;
            case 4:
                return hitZ < .5;
            case 5:
                return hitZ > .5;
            default:
                return true;
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public void onBlockClicked(World worldIn, BlockPos pos, EntityPlayer playerIn) {
        if(worldIn.isRemote) {
            return;
        }

        if(StorageDrawers.config.cache.debugTrace)
            StorageDrawers.log.info("onBlockClicked");

        RayTraceResult rayResult = net.minecraftforge.common.ForgeHooks.rayTraceEyes(playerIn, ((EntityPlayerMP) playerIn).interactionManager.getBlockReachDistance() + 1);
        if(rayResult == null)
            return;

        EnumFacing side = rayResult.sideHit;

        // adjust hitVec for drawers
        float hitX = (float) (rayResult.hitVec.x - pos.getX());
        float hitY = (float) (rayResult.hitVec.y - pos.getY());
        float hitZ = (float) (rayResult.hitVec.z - pos.getZ());

        TileEntityDrawers tileDrawers = getTileEntitySafe(worldIn, pos);
        if(tileDrawers.getDirection() != side.ordinal())
            return;

        if(tileDrawers.isSealed())
            return;

        if(!SecurityManager.hasAccess(playerIn.getGameProfile(), tileDrawers))
            return;

        int slot = getDrawerSlot(side.ordinal(), hitX, hitY, hitZ);
        IDrawer drawer = tileDrawers.getGroup().getDrawer(slot);

        ItemStack item;
        Map<String, PlayerConfigSetting<?>> configSettings = ConfigManager.serverPlayerConfigSettings.get(playerIn.getUniqueID());
        boolean invertShift = false;
        if(configSettings != null) {
            @SuppressWarnings("unchecked")
            PlayerConfigSetting<Boolean> setting = (PlayerConfigSetting<Boolean>) configSettings.get("invertShift");
            if(setting != null) {
                invertShift = setting.value;
            }
        }
        if(playerIn.isSneaking() != invertShift)
            item = tileDrawers.takeItemsFromSlot(slot, drawer.getStoredItemStackSize());
        else
            item = tileDrawers.takeItemsFromSlot(slot, 1);

        if(StorageDrawers.config.cache.debugTrace)
            StorageDrawers.log.info((item.isEmpty()) ? "  null item" : "  " + item.toString());

        IBlockState state = worldIn.getBlockState(pos);
        if(!item.isEmpty()) {
            if(!playerIn.inventory.addItemStackToInventory(item)) {
                dropItemStack(worldIn, pos.offset(side), item);
                worldIn.notifyBlockUpdate(pos, state, state, 3);
            } else
                worldIn.playSound(null, pos.getX() + .5f, pos.getY() + .5f, pos.getZ() + .5f, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, .2f, ((worldIn.rand.nextFloat() - worldIn.rand.nextFloat()) * .7f + 1) * 2);
        }
    }

    @Override
    public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis) {
        TileEntityDrawers tile = getTileEntitySafe(world, pos);
        if(tile.isSealed()) {
            dropBlockAsItem(world, pos, world.getBlockState(pos), 0);
            world.setBlockToAir(pos);
            return true;
        }

        boolean result = super.rotateBlock(world, pos, axis);
        if(result)
            tile.setDirection(world.getBlockState(pos).getValue(FACING).getIndex());

        return result;
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing side) {
        TileEntityDrawers tile = getTileEntity(world, pos);
        if(tile == null)
            return BlockFaceShape.SOLID;

        if(side == EnumFacing.DOWN) {
            Block blockUnder = world.getBlockState(pos.down()).getBlock();
            if(blockUnder instanceof BlockChest || blockUnder instanceof BlockEnderChest)
                return BlockFaceShape.UNDEFINED;
        }

        return side.ordinal() != tile.getDirection() ? BlockFaceShape.SOLID : BlockFaceShape.BOWL;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isSideSolid(IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, EnumFacing side) {
        return getBlockFaceShape(world, state, pos, side) == BlockFaceShape.SOLID;
    }

    private void dropItemStack(World world, BlockPos pos, @Nonnull ItemStack stack) {
        EntityItem entity = new EntityItem(world, pos.getX() + .5f, pos.getY() + .3f, pos.getZ() + .5f, stack);
        entity.addVelocity(-entity.motionX, -entity.motionY, -entity.motionZ);
        world.spawnEntity(entity);
    }

    @Override
    public int damageDropped(IBlockState state) {
        return getMetaFromState(state);
    }

    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        if(player.capabilities.isCreativeMode) {
            float blockReachDistance;
            if(world.isRemote) {
                blockReachDistance = Minecraft.getMinecraft().playerController.getBlockReachDistance() + 1;
            } else {
                blockReachDistance = (float) ((EntityPlayerMP) player).interactionManager.getBlockReachDistance() + 1;
            }

            RayTraceResult rayResult = net.minecraftforge.common.ForgeHooks.rayTraceEyes(player, blockReachDistance + 1);
            if(rayResult == null || getDirection(world, pos) != rayResult.sideHit)
                world.setBlockState(pos, net.minecraft.init.Blocks.AIR.getDefaultState(), world.isRemote ? 11 : 3);
            else
                onBlockClicked(world, pos, player);

            return false;
        }

        return willHarvest || super.removedByPlayer(state, world, pos, player, false);
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEntityDrawers tile = getTileEntity(world, pos);

        if(tile != null && !tile.isSealed() && !StorageDrawers.config.cache.keepContentsOnBreak) {
            for(int i = 0; i < tile.upgrades().getSlotCount(); i++) {
                ItemStack stack = tile.upgrades().getUpgrade(i);
                if(!stack.isEmpty()) {
                    if(stack.getItem() instanceof ItemUpgradeCreative)
                        continue;
                    spawnAsEntity(world, pos, stack);
                }
            }

            if(!tile.getDrawerAttributes().isUnlimitedVending())
                DrawerInventoryHelper.dropInventoryItems(world, pos, tile.getGroup());
        }

        super.breakBlock(world, pos, state);
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        drops.add(getMainDrop(world, pos, state));
    }

    protected ItemStack getMainDrop(IBlockAccess world, BlockPos pos, IBlockState state) {
        ItemStack drop = new ItemStack(Item.getItemFromBlock(this), 1, state.getBlock().getMetaFromState(state));

        TileEntityDrawers tile = getTileEntity(world, pos);
        if(tile == null)
            return drop;

        NBTTagCompound data = drop.getTagCompound();
        if(data == null)
            data = new NBTTagCompound();

        boolean hasContents = false;
        if(StorageDrawers.config.cache.keepContentsOnBreak) {
            for(int i = 0; i < tile.getGroup().getDrawerCount(); i++) {
                IDrawer drawer = tile.getGroup().getDrawer(i);
                if(!drawer.isEmpty())
                    hasContents = true;
            }
            for(int i = 0; i < tile.upgrades().getSlotCount(); i++) {
                if(!tile.upgrades().getUpgrade(i).isEmpty())
                    hasContents = true;
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

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        return getMainDrop(world, pos, state);
    }

    @Override
    public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te, @Nonnull ItemStack stack) {
        super.harvestBlock(world, player, pos, state, te, stack);
        world.setBlockToAir(pos);
    }

    @Override
    public float getExplosionResistance(World world, BlockPos pos, Entity exploder, Explosion explosion) {
        TileEntityDrawers tile = getTileEntity(world, pos);
        if(tile != null) {
            for(int slot = 0; slot < 5; slot++) {
                ItemStack stack = tile.upgrades().getUpgrade(slot);
                if(stack.isEmpty() || !(stack.getItem() instanceof ItemUpgradeStorage))
                    continue;

                if(EnumUpgradeStorage.byMetadata(stack.getMetadata()) != EnumUpgradeStorage.OBSIDIAN)
                    continue;

                return 1000;
            }
        }

        return super.getExplosionResistance(world, pos, exploder, explosion);
    }

    @Nullable
    public TileEntityDrawers getTileEntity(IBlockAccess blockAccess, BlockPos pos) {
        TileEntity tile = blockAccess.getTileEntity(pos);

        return (tile instanceof TileEntityDrawers) ? (TileEntityDrawers) tile : null;
    }

    public TileEntityDrawers getTileEntitySafe(World world, BlockPos pos) {
        TileEntityDrawers tile = getTileEntity(world, pos);
        if(tile == null) {
            tile = (TileEntityDrawers) createNewTileEntity(world, 0);
            world.setTileEntity(pos, tile);
        }

        return tile;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean addHitEffects(IBlockState state, World worldObj, RayTraceResult target, ParticleManager manager) {
        if(getDirection(worldObj, target.getBlockPos()) == target.sideHit)
            return true;

        return super.addHitEffects(state, worldObj, target, manager);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean addDestroyEffects(World world, BlockPos pos, ParticleManager manager) {
        //TileEntityDrawers tile = getTileEntity(world, pos);
        //if (tile != null && !tile.getWillDestroy())
        //    return true;

        return super.addDestroyEffects(world, pos, manager);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean canProvidePower(IBlockState state) {
        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getWeakPower(IBlockState state, IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
        if(!canProvidePower(state))
            return 0;

        TileEntityDrawers tile = getTileEntity(worldIn, pos);
        if(tile == null || !tile.isRedstone())
            return 0;

        return tile.getRedstoneLevel();
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getStrongPower(IBlockState state, IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
        return (side == EnumFacing.UP) ? getWeakPower(state, worldIn, pos, side) : 0;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityCustomCompDrawer();
    }

}
