package eutros.framedcompactdrawers.block.tile;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerAttributes;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import com.jaquadro.minecraft.storagedrawers.block.BlockCompDrawers;
import com.jaquadro.minecraft.storagedrawers.block.EnumCompDrawer;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawersComp;
import com.jaquadro.minecraft.storagedrawers.block.tile.tiledata.FractionalDrawerGroup;
import com.jaquadro.minecraft.storagedrawers.block.tile.tiledata.TileDataShim;
import com.jaquadro.minecraft.storagedrawers.config.CommonConfig;
import com.jaquadro.minecraft.storagedrawers.network.CountUpdateMessage;
import com.jaquadro.minecraft.storagedrawers.network.MessageHandler;
import eutros.framedcompactdrawers.block.ModBlocks;
import eutros.framedcompactdrawers.model.FrameableModel.MaterialSide;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileCompDrawersCustom extends TileEntityDrawersComp {

    public ItemStack side = ItemStack.EMPTY;
    public ItemStack trim = ItemStack.EMPTY;
    public ItemStack front = ItemStack.EMPTY;

    public TileCompDrawersCustom(TileEntityType<?> tileEntityType) {
        super(tileEntityType);
        injectData(new TileDataShim() {
            @Override
            public void read(CompoundNBT compoundNBT) {
                side = ItemStack.read(compoundNBT.getCompound("MatS"));
                trim = ItemStack.read(compoundNBT.getCompound("MatS"));
                front = ItemStack.read(compoundNBT.getCompound("MatS"));
            }

            @Override
            public CompoundNBT write(CompoundNBT compoundNBT) {
                compoundNBT.put("MatS", side.write(new CompoundNBT()));
                compoundNBT.put("MatT", trim.write(new CompoundNBT()));
                compoundNBT.put("MatF", front.write(new CompoundNBT()));
                return compoundNBT;
            }
        });
    }

    @Nonnull
    @Override
    public IModelData getModelData() {
        IModelData data = super.getModelData();
        if(data == EmptyModelData.INSTANCE) {
            data = new ModelDataMap.Builder().build();
        }
        data.setData(MaterialSide.SIDE.property, side);
        data.setData(MaterialSide.TRIM.property, trim);
        data.setData(MaterialSide.FRONT.property, front);
        return data;
    }

    // the rest is copied because you know why not

    @CapabilityInject(IDrawerAttributes.class)
    static Capability<IDrawerAttributes> DRAWER_ATTRIBUTES_CAPABILITY = null;

    private class GroupData extends FractionalDrawerGroup {
        private final LazyOptional<?> capabilityAttributes = LazyOptional.of(TileCompDrawersCustom.this::getDrawerAttributes);

        public GroupData(int slotCount) {
            super(slotCount);
        }

        protected World getWorld() {
            return TileCompDrawersCustom.this.getWorld();
        }

        public boolean isGroupValid() {
            return TileCompDrawersCustom.this.isGroupValid();
        }

        protected void log(String message) {
            if (!this.getWorld().isRemote && CommonConfig.GENERAL.debugTrace.get()) {
                StorageDrawers.log.info(message);
            }

        }

        protected int getStackCapacity() {
            return TileCompDrawersCustom.this.upgrades().getStorageMultiplier() * TileCompDrawersCustom.this.getEffectiveDrawerCapacity();
        }

        protected void onItemChanged() {
            if (this.getWorld() != null && !this.getWorld().isRemote) {
                int usedSlots = 0;
                int[] var2 = this.getAccessibleDrawerSlots();

                for(int slot : var2) {
                    IDrawer drawer = this.getDrawer(slot);
                    if(!drawer.isEmpty()) {
                        ++usedSlots;
                    }
                }

                usedSlots = Math.max(usedSlots, 1);
                EnumCompDrawer open = TileCompDrawersCustom.this.getBlockState().get(BlockCompDrawers.SLOTS);
                if (open.getOpenSlots() != usedSlots) {
                    this.getWorld().setBlockState(TileCompDrawersCustom.this.pos, TileCompDrawersCustom.this.getBlockState().with(BlockCompDrawers.SLOTS, EnumCompDrawer.byOpenSlots(usedSlots)), 3);
                }

                TileCompDrawersCustom.this.markDirty();
                TileCompDrawersCustom.this.markBlockForUpdate();
            }

        }

        protected void onAmountChanged() {
            if (this.getWorld() != null && !this.getWorld().isRemote) {
                PacketDistributor.TargetPoint point = new PacketDistributor.TargetPoint(TileCompDrawersCustom.this.getPos().getX(), TileCompDrawersCustom.this.getPos().getY(), TileCompDrawersCustom.this.getPos().getZ(), 500.0D, this.getWorld().func_234923_W_());
                MessageHandler.INSTANCE.send(PacketDistributor.NEAR.with(() -> point), new CountUpdateMessage(TileCompDrawersCustom.this.getPos(), 0, this.getPooledCount()));
                TileCompDrawersCustom.this.markDirty();
            }

        }

        @Nonnull
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
            return capability == DRAWER_ATTRIBUTES_CAPABILITY ? this.capabilityAttributes.cast() : super.getCapability(capability, facing);
        }
    }

    public static class Slot3 extends TileCompDrawersCustom {

        private final TileCompDrawersCustom.GroupData groupData = new TileCompDrawersCustom.GroupData(3);

        public Slot3() {
            super(ModBlocks.Tile.fractionalDrawers3);
            this.groupData.setCapabilityProvider(this);
            this.injectPortableData(this.groupData);
        }

        public IDrawerGroup getGroup() {
            return this.groupData;
        }

        protected void onAttributeChanged() {
            super.onAttributeChanged();
            this.groupData.syncAttributes();
        }

    }

}
