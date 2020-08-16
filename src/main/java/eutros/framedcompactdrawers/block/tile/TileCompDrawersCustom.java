package eutros.framedcompactdrawers.block.tile;

import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawersComp;
import com.jaquadro.minecraft.storagedrawers.block.tile.tiledata.TileDataShim;
import eutros.framedcompactdrawers.block.ModBlocks;
import eutros.framedcompactdrawers.mixin.AccessorTileEntity;
import eutros.framedcompactdrawers.model.FrameableModel.MaterialSide;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;

import javax.annotation.Nonnull;

public class TileCompDrawersCustom extends TileEntityDrawersComp.Slot3 {

    public ItemStack side = ItemStack.EMPTY;
    public ItemStack trim = ItemStack.EMPTY;
    public ItemStack front = ItemStack.EMPTY;

    public TileCompDrawersCustom() {
        super();
        ((AccessorTileEntity) this).setType(ModBlocks.Tile.fractionalDrawers3);
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

}
