package eutros.fcd.registry;

import eutros.fcd.block.BlockControllerCustom;
import eutros.fcd.block.BlockDrawersCustomComp;
import eutros.fcd.block.BlockSlaveCustom;
import eutros.fcd.block.tile.TileControllerCustom;
import eutros.fcd.block.tile.TileSlaveCustom;
import eutros.fcd.item.ItemControllerCustom;
import eutros.fcd.item.ItemDrawersCustomComp;
import eutros.fcd.item.ItemSlaveCustom;
import eutros.fcd.utils.Reference;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Objects;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class ModBlocks {

    @ObjectHolder(Reference.MOD_ID + ":framed_compact_drawer")
    public static BlockDrawersCustomComp framedCompactDrawer;

    @ObjectHolder(Reference.MOD_ID + ":framed_drawer_controller")
    public static BlockControllerCustom framedDrawerController;

    @ObjectHolder(Reference.MOD_ID + ":framed_slave")
    public static BlockControllerCustom framedSlave;

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        IForgeRegistry<Block> r = event.getRegistry();

        r.register(new BlockDrawersCustomComp());

        BlockControllerCustom controllerCustom = new BlockControllerCustom();
        r.register(controllerCustom);

        GameRegistry.registerTileEntity(TileControllerCustom.class,
                Objects.requireNonNull(controllerCustom.getRegistryName()).toString());

        BlockSlaveCustom slaveCustom = new BlockSlaveCustom();
        r.register(slaveCustom);

        GameRegistry.registerTileEntity(TileSlaveCustom.class,
                Objects.requireNonNull(slaveCustom.getRegistryName()).toString());
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> r = event.getRegistry();

        r.register(new ItemDrawersCustomComp(framedCompactDrawer)
                .setRegistryName(Objects.requireNonNull(framedCompactDrawer.getRegistryName())));

        r.register(new ItemControllerCustom(framedDrawerController)
                .setRegistryName(Objects.requireNonNull(framedDrawerController.getRegistryName())));

        r.register(new ItemSlaveCustom(framedSlave)
                .setRegistryName(Objects.requireNonNull(framedSlave.getRegistryName())));
    }

}
