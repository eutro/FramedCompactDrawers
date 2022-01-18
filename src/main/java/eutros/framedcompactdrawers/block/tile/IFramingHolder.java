package eutros.framedcompactdrawers.block.tile;

import com.jaquadro.minecraft.storagedrawers.block.tile.ChamTileEntity;
import com.jaquadro.minecraft.storagedrawers.block.tile.tiledata.TileDataShim;
import eutros.framedcompactdrawers.render.model.FrameableModel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;

public interface IFramingHolder {

    default void injectCustomData(ChamTileEntity tile) {
        tile.injectData(new TileDataShim() {
            @Override
            public void read(CompoundTag compoundNBT) {
                readFromTag(compoundNBT);
            }

            @Override
            public CompoundTag write(CompoundTag compoundNBT) {
                return writeToTag(compoundNBT);
            }
        });
    }

    default void readFromTag(CompoundTag compoundNBT) {
        setSide(ItemStack.of(compoundNBT.getCompound("MatS")));
        setTrim(ItemStack.of(compoundNBT.getCompound("MatT")));
        setFront(ItemStack.of(compoundNBT.getCompound("MatF")));
    }

    default CompoundTag writeToTag(CompoundTag compoundNBT) {
        compoundNBT.put("MatS", getSide().save(new CompoundTag()));
        compoundNBT.put("MatT", getTrim().save(new CompoundTag()));
        compoundNBT.put("MatF", getFront().save(new CompoundTag()));
        return compoundNBT;
    }

    default IModelData getCustomModelData(IModelData data, IFramingHolder holder) {
        if(data == EmptyModelData.INSTANCE) {
            data = new ModelDataMap.Builder().build();
        }
        data.setData(FrameableModel.MaterialSide.SIDE.property, holder.getSide());
        data.setData(FrameableModel.MaterialSide.TRIM.property, holder.getTrim());
        data.setData(FrameableModel.MaterialSide.FRONT.property, holder.getFront());
        return data;
    }

    ItemStack getSide();

    void setSide(ItemStack side);

    ItemStack getTrim();

    void setTrim(ItemStack trim);

    ItemStack getFront();

    void setFront(ItemStack front);

}
