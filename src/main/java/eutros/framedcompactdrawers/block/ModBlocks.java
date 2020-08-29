package eutros.framedcompactdrawers.block;

import com.jaquadro.minecraft.storagedrawers.client.renderer.TileEntityDrawersRenderer;
import eutros.framedcompactdrawers.FramedCompactDrawers;
import eutros.framedcompactdrawers.block.tile.TileCompDrawersCustom;
import eutros.framedcompactdrawers.block.tile.TileControllerCustom;
import eutros.framedcompactdrawers.item.ItemCompDrawersCustom;
import eutros.framedcompactdrawers.item.ItemOtherCustom;
import eutros.framedcompactdrawers.render.RenderHelper;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Objects;
import java.util.function.Supplier;

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

        r.register(new ItemOtherCustom(framedDrawerController, properties)
                .setRegistryName(Objects.requireNonNull(framedDrawerController.getRegistryName())));

        r.register(new BlockItem(framedSlave, properties)
                .setRegistryName(Objects.requireNonNull(framedSlave.getRegistryName())));
    }

    public static void setRenderLayers() {
        RenderTypeLookup.setRenderLayer(framedCompactDrawer, RenderHelper::canRenderFrameable);
        RenderTypeLookup.setRenderLayer(framedDrawerController, RenderHelper::canRenderFrameable);
        RenderTypeLookup.setRenderLayer(framedSlave, RenderHelper::canRenderFrameable);

        framedCompactDrawer.setGeometryData();
    }

    public static class Tile {

        public static TileEntityType<TileCompDrawersCustom.Slot3> fractionalDrawers3;
        public static TileEntityType<TileControllerCustom> controllerCustom;

        @SubscribeEvent
        public void registerTiles(RegistryEvent.Register<TileEntityType<?>> evt) {
            IForgeRegistry<TileEntityType<?>> r = evt.getRegistry();

            fractionalDrawers3 = registerTile(r, TileCompDrawersCustom.Slot3::new, framedCompactDrawer);
            controllerCustom = registerTile(r, TileControllerCustom::new, framedDrawerController);
        }

        private <T extends TileEntity> TileEntityType<T> registerTile(IForgeRegistry<TileEntityType<?>> registry,
                                                                      Supplier<T> supplier,
                                                                      Block block) {
            @SuppressWarnings("ConstantConditions")
            TileEntityType<T> type = TileEntityType.Builder
                    .create(supplier, block)
                    .build(null);
            type.setRegistryName(Objects.requireNonNull(block.getRegistryName()));
            registry.register(type);
            return type;
        }

        @SubscribeEvent
        @OnlyIn(Dist.CLIENT)
        public void registerTERs(ModelBakeEvent evt) {
            ClientRegistry.bindTileEntityRenderer(fractionalDrawers3, TileEntityDrawersRenderer::new);
        }

    }

}
