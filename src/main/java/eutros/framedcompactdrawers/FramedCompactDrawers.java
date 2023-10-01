package eutros.framedcompactdrawers;

import eutros.framedcompactdrawers.block.ModBlocks;
import eutros.framedcompactdrawers.recipe.FramingRecipe;
import eutros.framedcompactdrawers.render.model.FrameableModelLoader;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.ModelEvent.RegisterGeometryLoaders;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

@Mod(FramedCompactDrawers.MOD_ID)
public class FramedCompactDrawers {

    public static final String MOD_ID = "framedcompactdrawers";

    public FramedCompactDrawers() {
        IEventBus eb = FMLJavaModLoadingContext.get().getModEventBus();
        eb.addListener(ModBlocks::registerBlocks);
        eb.addListener(ModBlocks::registerItems);
        eb.addListener(ModBlocks::registerCreativeTab);
        eb.addListener((RegisterGeometryLoaders evt) -> evt.register("frameable", new FrameableModelLoader()));
        eb.addListener((FMLClientSetupEvent evt) -> ModBlocks.setGeometryData());
        eb.addListener((RegisterEvent event) -> {
            if (Registries.RECIPE_SERIALIZER.equals(event.getRegistryKey())) {
                ForgeRegistries.RECIPE_SERIALIZERS.register(new ResourceLocation(MOD_ID, "framing"), FramingRecipe.SERIALIZER);
            }
        });
        eb.register(new ModBlocks.Tile());
    }

}
