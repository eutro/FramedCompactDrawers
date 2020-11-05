package eutros.framedcompactdrawers.recipe;

import eutros.framedcompactdrawers.FramedCompactDrawers;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;

public class ModTags {

    public static class Items {

        public static Tags.IOptionalNamedTag<Item> FRAME_TRIPLE = tag("frame_triple");

        public static Tags.IOptionalNamedTag<Item> COMPACTING = tag("compacting");
        public static Tags.IOptionalNamedTag<Item> SLAVE = tag("slave");
        public static Tags.IOptionalNamedTag<Item> CONTROLLER = tag("controller");

        private static Tags.IOptionalNamedTag<Item> tag(String name) {
            return ItemTags.createOptional(new ResourceLocation(FramedCompactDrawers.MOD_ID, name));
        }

    }

    public static class Blocks {

        public static Tags.IOptionalNamedTag<Block> FRAME_TRIPLE = tag("frame_triple");

        public static Tags.IOptionalNamedTag<Block> COMPACTING = tag("compacting");
        public static Tags.IOptionalNamedTag<Block> SLAVE = tag("slave");
        public static Tags.IOptionalNamedTag<Block> CONTROLLER = tag("controller");

        private static Tags.IOptionalNamedTag<Block> tag(String name) {
            return BlockTags.createOptional(new ResourceLocation(FramedCompactDrawers.MOD_ID, name));
        }

    }

}
