package eutros.fcd.block.tile;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import com.jaquadro.minecraft.storagedrawers.block.modeldata.MaterialModelData;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.tiledata.MaterialData;

public class MaterialModelCarrier extends TileEntityDrawers {

    private final TileControllerCustom tile;

    private MaterialModelCarrier(TileControllerCustom tile) {
        this.tile = tile;
    }

    public static MaterialModelData materialFrom(TileControllerCustom te) {
        return new MaterialModelData(new MaterialModelCarrier(te));
    }

    @Override
    public IDrawerGroup getGroup() {
        return null;
    }

    @Override
    public MaterialData material() {
        return tile.material();
    }

}
