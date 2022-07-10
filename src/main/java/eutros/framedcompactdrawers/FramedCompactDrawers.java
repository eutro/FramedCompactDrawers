package eutros.framedcompactdrawers;

import eutros.framedcompactdrawers.block.ModBlocks;
import eutros.framedcompactdrawers.recipe.FramingRecipe;
import eutros.framedcompactdrawers.render.model.FrameableModelLoader;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.ModelEvent.RegisterGeometryLoaders;
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

    public FramedCompactDrawers() {
        IEventBus eb = FMLJavaModLoadingContext.get().getModEventBus();
        eb.addListener(ModBlocks::registerBlocks);
        eb.addListener(ModBlocks::registerItems);
        eb.addListener((RegisterGeometryLoaders evt) -> evt.register("frameable", new FrameableModelLoader()));
        eb.addListener((FMLClientSetupEvent evt) -> ModBlocks.setGeometryData());
        eb.addListener((RegisterEvent event) -> {
            if (Registry.RECIPE_SERIALIZER_REGISTRY.equals(event.getRegistryKey())) {
                ForgeRegistries.RECIPE_SERIALIZERS.register(new ResourceLocation(MOD_ID, "framing"), FramingRecipe.SERIALIZER);
            }
        });
        eb.register(new ModBlocks.Tile());
    }

}
