package eutros.framedcompactdrawers.block.tile;

import com.jaquadro.minecraft.storagedrawers.block.tile.ChamTileEntity;
import com.jaquadro.minecraft.storagedrawers.block.tile.tiledata.TileDataShim;
import eutros.framedcompactdrawers.model.FrameableModel;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;

public interface IFramingHolder {

    default void injectCustomData(ChamTileEntity tile, IFramingHolder holder) {
        tile.injectData(new TileDataShim() {
            @Override
            public void read(CompoundNBT compoundNBT) {
                holder.setSide(ItemStack.read(compoundNBT.getCompound("MatS")));
                holder.setTrim(ItemStack.read(compoundNBT.getCompound("MatS")));
                holder.setFront(ItemStack.read(compoundNBT.getCompound("MatS")));
            }

            @Override
            public CompoundNBT write(CompoundNBT compoundNBT) {
                compoundNBT.put("MatS", holder.getSide().write(new CompoundNBT()));
                compoundNBT.put("MatT", holder.getTrim().write(new CompoundNBT()));
                compoundNBT.put("MatF", holder.getFront().write(new CompoundNBT()));
                return compoundNBT;
            }
        });
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
