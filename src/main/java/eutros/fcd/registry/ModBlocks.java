package eutros.fcd.registry;

import eutros.fcd.block.CustomDrawersComp;
import eutros.fcd.item.ItemCustomDrawersComp;
import eutros.fcd.utils.Reference;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Objects;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class ModBlocks {

	@ObjectHolder(Reference.MOD_ID + ":framed_compact_drawer")
	public static CustomDrawersComp framedCompactDrawer;

	@SubscribeEvent
	public static void registerBlocks (RegistryEvent.Register<Block> event) {
		IForgeRegistry<Block> r = event.getRegistry();

		r.register(new CustomDrawersComp());
	}

	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event) {
		IForgeRegistry<Item> r = event.getRegistry();

		r.register(new ItemCustomDrawersComp(framedCompactDrawer)
				.setRegistryName(Objects.requireNonNull(framedCompactDrawer.getRegistryName())));
	}

}
