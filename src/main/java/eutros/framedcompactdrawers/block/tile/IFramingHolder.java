package eutros.framedcompactdrawers.block.tile;

import com.jaquadro.minecraft.storagedrawers.block.tile.BaseBlockEntity;
import com.jaquadro.minecraft.storagedrawers.block.tile.tiledata.BlockEntityDataShim;
import eutros.framedcompactdrawers.render.model.FrameableModel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.model.data.ModelData;

public interface IFramingHolder {

    default void injectCustomData(BaseBlockEntity tile) {
        tile.injectData(new BlockEntityDataShim() {
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

    default ModelData getCustomModelData(ModelData data, IFramingHolder holder) {
        return data.derive()
                .with(FrameableModel.MaterialSide.SIDE.property, holder.getSide())
                .with(FrameableModel.MaterialSide.TRIM.property, holder.getTrim())
                .with(FrameableModel.MaterialSide.FRONT.property, holder.getFront())
                .build();
    }

    ItemStack getSide();

    void setSide(ItemStack side);

    ItemStack getTrim();

    void setTrim(ItemStack trim);

    ItemStack getFront();

    void setFront(ItemStack front);

}
