package eutros.framedcompactdrawers.block;

import com.jaquadro.minecraft.storagedrawers.client.renderer.TileEntityDrawersRenderer;
import eutros.framedcompactdrawers.FramedCompactDrawers;
import eutros.framedcompactdrawers.block.tile.TileCompDrawersCustom;
import eutros.framedcompactdrawers.item.ItemCompDrawersCustom;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Objects;

public class ModBlocks {

    public static BlockCompDrawersCustom framedCompactDrawer;
    public static BlockControllerCustom framedDrawerController;
    public static BlockSlaveCustom framedSlave;

    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        IForgeRegistry<Block> r = event.getRegistry();

        AbstractBlock.Properties properties = AbstractBlock.Properties.create(Material.WOOD).notSolid();
        framedCompactDrawer = new BlockCompDrawersCustom(properties);
        r.register(framedCompactDrawer
                .setRegistryName(FramedCompactDrawers.MOD_ID, "framed_compact_drawer"));

        framedDrawerController = new BlockControllerCustom(properties);
        r.register(framedDrawerController
                .setRegistryName(FramedCompactDrawers.MOD_ID, "framed_drawer_controller"));

        framedSlave = new BlockSlaveCustom(properties);
        r.register(framedSlave
                .setRegistryName(FramedCompactDrawers.MOD_ID, "framed_slave"));
    }

    public static void registerItems(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> r = event.getRegistry();

        Item.Properties properties = new Item.Properties()
                .group(FramedCompactDrawers.CREATIVE_TAB);

        r.register(new ItemCompDrawersCustom(framedCompactDrawer, properties)
                .setRegistryName(Objects.requireNonNull(framedCompactDrawer.getRegistryName())));

        r.register(new BlockItem(framedDrawerController, properties)
                .setRegistryName(Objects.requireNonNull(framedDrawerController.getRegistryName())));

        r.register(new BlockItem(framedSlave, properties)
                .setRegistryName(Objects.requireNonNull(framedSlave.getRegistryName())));
    }

    public static void setRenderLayers() {
        RenderTypeLookup.setRenderLayer(framedCompactDrawer, RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(framedDrawerController, RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(framedSlave, RenderType.getTranslucent());
    }

    public static class Tile {

        public static TileEntityType<TileCompDrawersCustom.Slot3> fractionalDrawers3;

        @SuppressWarnings("ConstantConditions")
        @SubscribeEvent
        public void registerTiles(RegistryEvent.Register<TileEntityType<?>> evt) {
            IForgeRegistry<TileEntityType<?>> r = evt.getRegistry();

            fractionalDrawers3 = TileEntityType.Builder
                    .create(TileCompDrawersCustom.Slot3::new, framedCompactDrawer)
                    .build(null);
            fractionalDrawers3.setRegistryName(Objects.requireNonNull(framedCompactDrawer.getRegistryName()));
            r.register(fractionalDrawers3);
        }

        @SubscribeEvent
        @OnlyIn(Dist.CLIENT)
        public void registerTers(ModelBakeEvent evt) {
            ClientRegistry.bindTileEntityRenderer(fractionalDrawers3, TileEntityDrawersRenderer::new);
        }

    }

}
