package eutros.framedcompactdrawers.block;

import com.google.common.collect.ImmutableList;
import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.client.renderer.BlockEntityDrawersRenderer;
import eutros.framedcompactdrawers.FramedCompactDrawers;
import eutros.framedcompactdrawers.block.tile.*;
import eutros.framedcompactdrawers.item.ItemDrawersCustom;
import eutros.framedcompactdrawers.item.ItemOtherCustom;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegisterEvent;

public class ModBlocks {

    public static BlockCompDrawersCustom framedCompactDrawer;
    public static BlockControllerCustom framedDrawerController;
    public static BlockSlaveCustom framedSlave;
    public static BlockTrimCustom framedTrim;

    public static BlockDrawersStandardCustom framedFullOne;
    public static BlockDrawersStandardCustom framedFullTwo;
    public static BlockDrawersStandardCustom framedFullFour;
    public static BlockDrawersStandardCustom framedHalfOne;
    public static BlockDrawersStandardCustom framedHalfTwo;
    public static BlockDrawersStandardCustom framedHalfFour;

    public static void registerBlocks(RegisterEvent event) {
        if (!Registry.BLOCK_REGISTRY.equals(event.getRegistryKey())) return;
        IForgeRegistry<Block> r = ForgeRegistries.BLOCKS;
        BlockBehaviour.Properties properties = BlockBehaviour.Properties
                .of(Material.WOOD)
                .strength(3.0F, 5.0F)
                .sound(SoundType.WOOD)
                .isSuffocating((_1, _2, _3) -> false)
                .isRedstoneConductor((_1, _2, _3) -> false)
                // it should be possible to do occlusion conditionally, but I tried briefly and gave up
                .noOcclusion();

        r.register(new ResourceLocation(FramedCompactDrawers.MOD_ID, "framed_compact_drawer"),
                (framedCompactDrawer = new BlockCompDrawersCustom(properties)));
        r.register(new ResourceLocation(FramedCompactDrawers.MOD_ID, "framed_drawer_controller"),
                (framedDrawerController = new BlockControllerCustom(properties)));
        r.register(new ResourceLocation(FramedCompactDrawers.MOD_ID, "framed_slave"),
                (framedSlave = new BlockSlaveCustom(properties)));
        r.register(new ResourceLocation(FramedCompactDrawers.MOD_ID, "framed_trim"),
                (framedTrim = new BlockTrimCustom(properties)));

        r.register(new ResourceLocation(FramedCompactDrawers.MOD_ID, "framed_full_one"),
                (framedFullOne = new BlockDrawersStandardCustom(1, false, properties)));
        r.register(new ResourceLocation(FramedCompactDrawers.MOD_ID, "framed_full_two"),
                (framedFullTwo = new BlockDrawersStandardCustom(2, false, properties)));
        r.register(new ResourceLocation(FramedCompactDrawers.MOD_ID, "framed_full_four"),
                (framedFullFour = new BlockDrawersStandardCustom(4, false, properties)));
        r.register(new ResourceLocation(FramedCompactDrawers.MOD_ID, "framed_half_one"),
                (framedHalfOne = new BlockDrawersStandardCustom(1, true, properties)));
        r.register(new ResourceLocation(FramedCompactDrawers.MOD_ID, "framed_half_two"),
                (framedHalfTwo = new BlockDrawersStandardCustom(2, true, properties)));
        r.register(new ResourceLocation(FramedCompactDrawers.MOD_ID, "framed_half_four"),
                (framedHalfFour = new BlockDrawersStandardCustom(4, true, properties)));
    }

    public static void registerItems(RegisterEvent event) {
        if (!Registry.ITEM_REGISTRY.equals(event.getRegistryKey())) return;
        IForgeRegistry<Item> r = ForgeRegistries.ITEMS;

        Item.Properties properties = new Item.Properties().tab(FramedCompactDrawers.CREATIVE_TAB);

        r.register(ForgeRegistries.BLOCKS.getKey(framedCompactDrawer), new ItemDrawersCustom(framedCompactDrawer, properties));
        r.register(ForgeRegistries.BLOCKS.getKey(framedDrawerController), new ItemOtherCustom(framedDrawerController, properties));
        r.register(ForgeRegistries.BLOCKS.getKey(framedSlave), new ItemOtherCustom(framedSlave, properties));
        r.register(ForgeRegistries.BLOCKS.getKey(framedTrim), new ItemOtherCustom(framedTrim, properties));

        r.register(ForgeRegistries.BLOCKS.getKey(framedFullOne), new ItemDrawersCustom(framedFullOne, properties));
        r.register(ForgeRegistries.BLOCKS.getKey(framedFullTwo), new ItemDrawersCustom(framedFullTwo, properties));
        r.register(ForgeRegistries.BLOCKS.getKey(framedFullFour), new ItemDrawersCustom(framedFullFour, properties));
        r.register(ForgeRegistries.BLOCKS.getKey(framedHalfOne), new ItemDrawersCustom(framedHalfOne, properties));
        r.register(ForgeRegistries.BLOCKS.getKey(framedHalfTwo), new ItemDrawersCustom(framedHalfTwo, properties));
        r.register(ForgeRegistries.BLOCKS.getKey(framedHalfFour), new ItemDrawersCustom(framedHalfFour, properties));
    }

    public static void setGeometryData() {
        framedCompactDrawer.setGeometryData();
        for (BlockDrawersStandardCustom bdsc : new BlockDrawersStandardCustom[]{
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

        public static BlockEntityType<TileCompDrawersCustom.Slot3> fractionalDrawers3;
        public static BlockEntityType<TileControllerCustom> controllerCustom;
        public static BlockEntityType<TileSlaveCustom> slaveCustom;
        public static BlockEntityType<TileTrimCustom> trimCustom;

        public static BlockEntityType<TileDrawersStandardCustom.Slot1> standardDrawers1;
        public static BlockEntityType<TileDrawersStandardCustom.Slot2> standardDrawers2;
        public static BlockEntityType<TileDrawersStandardCustom.Slot4> standardDrawers4;

        @SubscribeEvent
        public void registerTiles(RegisterEvent evt) {
            if (!Registry.BLOCK_ENTITY_TYPE_REGISTRY.equals(evt.getRegistryKey())) return;
            IForgeRegistry<BlockEntityType<?>> r = ForgeRegistries.BLOCK_ENTITY_TYPES;

            fractionalDrawers3 = registerTile(r, TileCompDrawersCustom.Slot3::new, framedCompactDrawer);
            controllerCustom = registerTile(r, TileControllerCustom::new, framedDrawerController);
            slaveCustom = registerTile(r, TileSlaveCustom::new, framedSlave);
            trimCustom = registerTile(r, TileTrimCustom::new, framedTrim);

            standardDrawers1 = registerTile(r, TileDrawersStandardCustom.Slot1::new, framedFullOne, framedHalfOne);
            standardDrawers2 = registerTile(r, TileDrawersStandardCustom.Slot2::new, framedFullTwo, framedHalfTwo);
            standardDrawers4 = registerTile(r, TileDrawersStandardCustom.Slot4::new, framedFullFour, framedHalfFour);
        }

        private <T extends BlockEntity> BlockEntityType<T> registerTile(
                IForgeRegistry<BlockEntityType<?>> registry,
                BlockEntityType.BlockEntitySupplier<T> supplier,
                Block... blocks
        ) {
            @SuppressWarnings("ConstantConditions")
            BlockEntityType<T> type = BlockEntityType.Builder
                    .of(supplier, blocks)
                    .build(null);
            registry.register(ForgeRegistries.BLOCKS.getKey(blocks[0]), type);
            return type;
        }

        @SubscribeEvent
        @OnlyIn(Dist.CLIENT)
        public void registerTERs(EntityRenderersEvent.RegisterRenderers evt) {
            for (BlockEntityType<? extends BlockEntityDrawers> drawer : ImmutableList.of(
                    fractionalDrawers3,
                    standardDrawers1,
                    standardDrawers2,
                    standardDrawers4
            ))
                evt.registerBlockEntityRenderer(drawer, BlockEntityDrawersRenderer::new);
        }

    }

    public static void fill(NonNullList<ItemStack> items) {
        for (Block block : new Block[]{
                framedCompactDrawer,
                framedDrawerController,
                framedSlave,
                framedTrim,
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
