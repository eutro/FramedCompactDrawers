package eutros.framedcompactdrawers.registry;

import com.jaquadro.minecraft.storagedrawers.item.ItemDrawers;
import eutros.framedcompactdrawers.FCDCreativeTab;
import eutros.framedcompactdrawers.block.BlockControllerCustom;
import eutros.framedcompactdrawers.block.BlockDrawersCustomComp;
import eutros.framedcompactdrawers.block.BlockSlaveCustom;
import eutros.framedcompactdrawers.utils.Reference;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Objects;

public class ModBlocks {

    public static BlockDrawersCustomComp framedCompactDrawer;
    public static BlockControllerCustom framedDrawerController;
    public static BlockSlaveCustom framedSlave;

    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        IForgeRegistry<Block> r = event.getRegistry();

        AbstractBlock.Properties properties = AbstractBlock.Properties.create(Material.WOOD);
        framedCompactDrawer = new BlockDrawersCustomComp(properties);
        r.register(framedCompactDrawer
                .setRegistryName(Reference.MOD_ID, "framed_compact_drawer"));

        framedDrawerController = new BlockControllerCustom(properties);
        r.register(framedDrawerController
                .setRegistryName(Reference.MOD_ID, "framed_drawer_controller"));

        framedSlave = new BlockSlaveCustom(properties);
        r.register(framedSlave
                .setRegistryName(Reference.MOD_ID, "framed_slave"));
    }

    public static void registerItems(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> r = event.getRegistry();

        Item.Properties properties = new Item.Properties()
                .group(FCDCreativeTab.tab);

        r.register(new ItemDrawers(framedCompactDrawer, properties)
                .setRegistryName(Objects.requireNonNull(framedCompactDrawer.getRegistryName())));

        r.register(new BlockItem(framedDrawerController, properties)
                .setRegistryName(Objects.requireNonNull(framedDrawerController.getRegistryName())));

        r.register(new BlockItem(framedSlave, properties)
                .setRegistryName(Objects.requireNonNull(framedSlave.getRegistryName())));
    }

}
