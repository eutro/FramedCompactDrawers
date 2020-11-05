package eutros.framedcompactdrawers;

import eutros.framedcompactdrawers.block.ModBlocks;
import eutros.framedcompactdrawers.recipe.FramingRecipe;
import eutros.framedcompactdrawers.render.model.FrameableModelLoader;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(FramedCompactDrawers.MOD_ID)
public class FramedCompactDrawers {

    public static final String MOD_ID = "framedcompactdrawers";
    public static final ItemGroup CREATIVE_TAB = new ItemGroup("framed_compacting_drawers") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(ModBlocks.framedCompactDrawer);
        }

        @Override
        public void fill(NonNullList<ItemStack> items) {
            ModBlocks.fill(items);
        }
    };

    public FramedCompactDrawers() {
        IEventBus eb = FMLJavaModLoadingContext.get().getModEventBus();
        eb.addGenericListener(Block.class, ModBlocks::registerBlocks);
        eb.addGenericListener(Item.class, ModBlocks::registerItems);
        eb.addListener((ModelRegistryEvent evt) -> ModelLoaderRegistry.registerLoader(new ResourceLocation(FramedCompactDrawers.MOD_ID, "frameable"), new FrameableModelLoader()));
        eb.addListener((FMLClientSetupEvent evt) -> ModBlocks.setRenderLayers());
        eb.addGenericListener(IRecipeSerializer.class, (RegistryEvent.Register<IRecipeSerializer<?>> evt) ->
                evt.getRegistry().register(FramingRecipe.SERIALIZER.setRegistryName(new ResourceLocation(MOD_ID, "framing"))));
        eb.register(new ModBlocks.Tile());
    }

}
