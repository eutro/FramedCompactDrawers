package eutros.framedcompactdrawers;

import eutros.framedcompactdrawers.block.ModBlocks;
import eutros.framedcompactdrawers.recipe.FramingRecipe;
import eutros.framedcompactdrawers.render.model.FrameableModelLoader;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import javax.annotation.Nonnull;

@Mod(FramedCompactDrawers.MOD_ID)
public class FramedCompactDrawers {

    public static final String MOD_ID = "framedcompactdrawers";
    public static final CreativeModeTab CREATIVE_TAB = new CreativeModeTab("framed_compacting_drawers") {
        @Override
        @Nonnull
        public ItemStack makeIcon() {
            return new ItemStack(ModBlocks.framedCompactDrawer);
        }

        @Override
        public void fillItemList(@Nonnull NonNullList<ItemStack> items) {
            ModBlocks.fill(items);
        }
    };

    public FramedCompactDrawers() {
        IEventBus eb = FMLJavaModLoadingContext.get().getModEventBus();
        eb.addGenericListener(Block.class, ModBlocks::registerBlocks);
        eb.addGenericListener(Item.class, ModBlocks::registerItems);
        eb.addListener((ModelRegistryEvent evt) -> ModelLoaderRegistry.registerLoader(new ResourceLocation(FramedCompactDrawers.MOD_ID, "frameable"), new FrameableModelLoader()));
        eb.addListener((FMLClientSetupEvent evt) -> ModBlocks.setRenderLayers());
        eb.addGenericListener(RecipeSerializer.class, (RegistryEvent.Register<RecipeSerializer<?>> evt) ->
                evt.getRegistry().register(FramingRecipe.SERIALIZER.setRegistryName(new ResourceLocation(MOD_ID, "framing"))));
        eb.register(new ModBlocks.Tile());
    }

}
