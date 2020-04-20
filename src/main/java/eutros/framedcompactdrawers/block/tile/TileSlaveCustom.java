package eutros.framedcompactdrawers.block.tile;

import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntitySlave;
import com.jaquadro.minecraft.storagedrawers.block.tile.tiledata.MaterialData;

public class TileSlaveCustom extends TileEntitySlave implements MaterialModelCarrier.IMaterialDataCarrier {

    private final MaterialData materialData = new MaterialData();

    public MaterialData material() {
        return materialData;
    }

    public TileSlaveCustom() {
        super();

        injectPortableData(materialData);
    }

}
