package eutros.framedcompactdrawers.block;

import com.google.common.collect.ImmutableList;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.client.renderer.TileEntityDrawersRenderer;
import eutros.framedcompactdrawers.FramedCompactDrawers;
import eutros.framedcompactdrawers.block.tile.TileCompDrawersCustom;
import eutros.framedcompactdrawers.block.tile.TileControllerCustom;
import eutros.framedcompactdrawers.block.tile.TileDrawersStandardCustom;
import eutros.framedcompactdrawers.block.tile.TileSlaveCustom;
import eutros.framedcompactdrawers.item.ItemDrawersCustom;
import eutros.framedcompactdrawers.item.ItemOtherCustom;
import eutros.framedcompactdrawers.render.RenderHelper;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.NonNullList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ModBlocks {

    public static BlockCompDrawersCustom framedCompactDrawer;
    public static BlockControllerCustom framedDrawerController;
    public static BlockSlaveCustom framedSlave;

    public static BlockDrawersStandardCustom framedFullOne;
    public static BlockDrawersStandardCustom framedFullTwo;
    public static BlockDrawersStandardCustom framedFullFour;
    public static BlockDrawersStandardCustom framedHalfOne;
    public static BlockDrawersStandardCustom framedHalfTwo;
    public static BlockDrawersStandardCustom framedHalfFour;

    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        IForgeRegistry<Block> r = event.getRegistry();
        AbstractBlock.Properties properties = AbstractBlock.Properties.create(Material.WOOD).notSolid();

        r.register((framedCompactDrawer = new BlockCompDrawersCustom(properties))
                .setRegistryName(FramedCompactDrawers.MOD_ID, "framed_compact_drawer"));
        r.register((framedDrawerController = new BlockControllerCustom(properties))
                .setRegistryName(FramedCompactDrawers.MOD_ID, "framed_drawer_controller"));
        r.register((framedSlave = new BlockSlaveCustom(properties))
                .setRegistryName(FramedCompactDrawers.MOD_ID, "framed_slave"));

        r.register((framedFullOne = new BlockDrawersStandardCustom(1, false, properties))
                .setRegistryName(FramedCompactDrawers.MOD_ID, "framed_full_one"));
        r.register((framedFullTwo = new BlockDrawersStandardCustom(2, false, properties))
                .setRegistryName(FramedCompactDrawers.MOD_ID, "framed_full_two"));
        r.register((framedFullFour = new BlockDrawersStandardCustom(4, false, properties))
                .setRegistryName(FramedCompactDrawers.MOD_ID, "framed_full_four"));
        r.register((framedHalfOne = new BlockDrawersStandardCustom(1, true, properties))
                .setRegistryName(FramedCompactDrawers.MOD_ID, "framed_half_one"));
        r.register((framedHalfTwo = new BlockDrawersStandardCustom(2, true, properties))
                .setRegistryName(FramedCompactDrawers.MOD_ID, "framed_half_two"));
        r.register((framedHalfFour = new BlockDrawersStandardCustom(4, true, properties))
                .setRegistryName(FramedCompactDrawers.MOD_ID, "framed_half_four"));
    }

    public static void registerItems(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> r = event.getRegistry();

        Item.Properties properties = new Item.Properties().group(FramedCompactDrawers.CREATIVE_TAB);

        r.register(new ItemDrawersCustom(framedCompactDrawer, properties)
                .setRegistryName(Objects.requireNonNull(framedCompactDrawer.getRegistryName())));
        r.register(new ItemOtherCustom(framedDrawerController, properties)
                .setRegistryName(Objects.requireNonNull(framedDrawerController.getRegistryName())));
        r.register(new ItemOtherCustom(framedSlave, properties)
                .setRegistryName(Objects.requireNonNull(framedSlave.getRegistryName())));

        r.register(new ItemDrawersCustom(framedFullOne, properties)
                .setRegistryName(Objects.requireNonNull(framedFullOne.getRegistryName())));
        r.register(new ItemDrawersCustom(framedFullTwo, properties)
                .setRegistryName(Objects.requireNonNull(framedFullTwo.getRegistryName())));
        r.register(new ItemDrawersCustom(framedFullFour, properties)
                .setRegistryName(Objects.requireNonNull(framedFullFour.getRegistryName())));
        r.register(new ItemDrawersCustom(framedHalfOne, properties)
                .setRegistryName(Objects.requireNonNull(framedHalfOne.getRegistryName())));
        r.register(new ItemDrawersCustom(framedHalfTwo, properties)
                .setRegistryName(Objects.requireNonNull(framedHalfTwo.getRegistryName())));
        r.register(new ItemDrawersCustom(framedHalfFour, properties)
                .setRegistryName(Objects.requireNonNull(framedHalfFour.getRegistryName())));
    }

    public static void setRenderLayers() {
        Predicate<RenderType> crf = RenderHelper::canRenderFrameable;
        for(Block b : new Block[] {
                framedCompactDrawer,
                framedDrawerController,
                framedSlave,
                framedFullOne,
                framedFullTwo,
                framedFullFour,
                framedHalfOne,
                framedHalfTwo,
                framedHalfFour,
        })
            RenderTypeLookup.setRenderLayer(b, crf);

        framedCompactDrawer.setGeometryData();
        for(BlockDrawersStandardCustom bdsc : new BlockDrawersStandardCustom[] {
                framedFullOne,
                framedFullTwo,
                framedFullFour,
                framedHalfOne,
                framedHalfTwo,
                framedHalfFour,
        })
            bdsc.setGeometryData();
    }

    public static class Tile {

        public static TileEntityType<TileCompDrawersCustom.Slot3> fractionalDrawers3;
        public static TileEntityType<TileControllerCustom> controllerCustom;
        public static TileEntityType<TileSlaveCustom> slaveCustom;

        public static TileEntityType<TileDrawersStandardCustom.Slot1> standardDrawers1;
        public static TileEntityType<TileDrawersStandardCustom.Slot2> standardDrawers2;
        public static TileEntityType<TileDrawersStandardCustom.Slot4> standardDrawers4;

        @SubscribeEvent
        public void registerTiles(RegistryEvent.Register<TileEntityType<?>> evt) {
            IForgeRegistry<TileEntityType<?>> r = evt.getRegistry();

            fractionalDrawers3 = registerTile(r, TileCompDrawersCustom.Slot3::new, framedCompactDrawer);
            controllerCustom = registerTile(r, TileControllerCustom::new, framedDrawerController);
            slaveCustom = registerTile(r, TileSlaveCustom::new, framedSlave);

            standardDrawers1 = registerTile(r, TileDrawersStandardCustom.Slot1::new, framedFullOne, framedHalfOne);
            standardDrawers2 = registerTile(r, TileDrawersStandardCustom.Slot2::new, framedFullTwo, framedHalfTwo);
            standardDrawers4 = registerTile(r, TileDrawersStandardCustom.Slot4::new, framedFullFour, framedHalfFour);
        }

        private <T extends TileEntity> TileEntityType<T> registerTile(IForgeRegistry<TileEntityType<?>> registry,
                                                                      Supplier<T> supplier,
                                                                      Block... blocks) {
            @SuppressWarnings("ConstantConditions")
            TileEntityType<T> type = TileEntityType.Builder
                    .create(supplier, blocks)
                    .build(null);
            type.setRegistryName(Objects.requireNonNull(blocks[0].getRegistryName()));
            registry.register(type);
            return type;
        }

        @SubscribeEvent
        @OnlyIn(Dist.CLIENT)
        public void registerTERs(ModelBakeEvent evt) {
            for(TileEntityType<? extends TileEntityDrawers> drawer : ImmutableList.of(
                    fractionalDrawers3,
                    standardDrawers1,
                    standardDrawers2,
                    standardDrawers4
            ))
                ClientRegistry.bindTileEntityRenderer(drawer, TileEntityDrawersRenderer::new);
        }

    }

    public static void fill(NonNullList<ItemStack> items) {
        for(Block block : new Block[] {
                framedCompactDrawer,
                framedDrawerController,
                framedSlave,
                framedFullOne,
                framedFullTwo,
                framedFullFour,
                framedHalfOne,
                framedHalfTwo,
                framedHalfFour,
        })
            items.add(new ItemStack(block));
    }

}
