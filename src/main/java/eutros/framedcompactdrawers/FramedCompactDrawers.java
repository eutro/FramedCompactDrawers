package eutros.framedcompactdrawers;

import eutros.framedcompactdrawers.block.ModBlocks;
import eutros.framedcompactdrawers.recipe.FramingRecipe;
import eutros.framedcompactdrawers.render.model.FrameableModelLoader;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

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

    @SuppressWarnings("DanglingJavadoc") // I just want the link man
    public FramedCompactDrawers() {
        IEventBus eb = FMLJavaModLoadingContext.get().getModEventBus();
        eb.addListener(ModBlocks::registerBlocks);
        eb.addListener(ModBlocks::registerItems);
        eb.addListener((ModelRegistryEvent evt) -> ModelLoaderRegistry.registerLoader(new ResourceLocation(FramedCompactDrawers.MOD_ID, "frameable"), new FrameableModelLoader()));
        /**
         * NB: StorageDrawers runs through ALL registered BlockDrawers instances and sets their render layers,
         * so we have to replace it with our own later.
         * @see com.jaquadro.minecraft.storagedrawers.client.ClientModBusSubscriber#clientSetup(FMLClientSetupEvent)
         *
         * The event is run in parallel so our only shot is to enqueue it.
         */
        eb.addListener((FMLClientSetupEvent evt) -> evt.enqueueWork(ModBlocks::setRenderLayers));
        eb.addListener((RegisterEvent event) -> {
            if (Registry.RECIPE_SERIALIZER_REGISTRY.equals(event.getRegistryKey())) {
                ForgeRegistries.RECIPE_SERIALIZERS.register(new ResourceLocation(MOD_ID, "framing"), FramingRecipe.SERIALIZER);
            }
        });
        eb.register(new ModBlocks.Tile());
    }

}
