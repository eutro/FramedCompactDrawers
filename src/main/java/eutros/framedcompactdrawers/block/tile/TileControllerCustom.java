package eutros.framedcompactdrawers.block.tile;

import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityController;
import com.jaquadro.minecraft.storagedrawers.block.tile.tiledata.MaterialData;
import eutros.framedcompactdrawers.registry.ModBlocks;
import net.minecraft.nbt.NBTTagCompound;

public class TileControllerCustom extends TileEntityController implements MaterialModelCarrier.IMaterialDataCarrier {

    private final MaterialData materialData = new MaterialData();

    public MaterialData material() {
        return materialData;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        materialData.readFromNBT(tag);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        materialData.writeToNBT(tag);
        return super.writeToNBT(tag);
    }

    @Override
    public void validate() {
        super.validate();

        if(!getWorld().isUpdateScheduled(getPos(), ModBlocks.framedDrawerController))
            getWorld().scheduleBlockUpdate(getPos(), ModBlocks.framedDrawerController, 1, 0);
    }

}
