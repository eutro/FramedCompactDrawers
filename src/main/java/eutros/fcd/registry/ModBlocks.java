package eutros.fcd.registry;

import eutros.fcd.block.CustomDrawersComp;
import eutros.fcd.block.TileEntityCustomCompDrawer;
import eutros.fcd.utils.Reference;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class ModBlocks {

	@ObjectHolder(Reference.MOD_ID + ":framed_compact_drawer")
	public static CustomDrawersComp framedCompactDrawer;

	@SubscribeEvent
	public static void registerBlocks (RegistryEvent.Register<Block> event) {
		IForgeRegistry<Block> r = event.getRegistry();

		register(r, new CustomDrawersComp(), "framed_compact_drawer");

		GameRegistry.registerTileEntity(TileEntityCustomCompDrawer.class, Reference.MOD_ID + ":framed_compact_drawer");
	}

	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event) {
		IForgeRegistry<Item> r = event.getRegistry();

		register(r, new ItemBlock(framedCompactDrawer), framedCompactDrawer.getRegistryName());
	}

	public static <V extends IForgeRegistryEntry<V>> void register(IForgeRegistry<V> reg, IForgeRegistryEntry<V> thing, ResourceLocation name) {
		reg.register(thing.setRegistryName(name));
	}

	public static <V extends IForgeRegistryEntry<V>> void register(IForgeRegistry<V> reg, IForgeRegistryEntry<V> thing, String name) {
		register(reg, thing, new ResourceLocation(Reference.MOD_ID, name));
	}

}
