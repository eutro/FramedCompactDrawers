package eutros.framedcompactdrawers.recipe;

import eutros.framedcompactdrawers.FramedCompactDrawers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;

public interface ModTags {

    interface Items {

        Tags.IOptionalNamedTag<Item> FRAME_DOUBLE = tag("frame_double");
        Tags.IOptionalNamedTag<Item> FRAME_TRIPLE = tag("frame_triple");

        Tags.IOptionalNamedTag<Item> COMPACTING = tag("compacting");
        Tags.IOptionalNamedTag<Item> SLAVE = tag("slave");
        Tags.IOptionalNamedTag<Item> CONTROLLER = tag("controller");

        static Tags.IOptionalNamedTag<Item> tag(String name) {
            return ItemTags.createOptional(new ResourceLocation(FramedCompactDrawers.MOD_ID, name));
        }

    }

    interface Blocks {

        Tags.IOptionalNamedTag<Block> FRAME_TRIPLE = tag("frame_triple");
        Tags.IOptionalNamedTag<Block> FRAME_DOUBLE = tag("frame_double");

        Tags.IOptionalNamedTag<Block> COMPACTING = tag("compacting");
        Tags.IOptionalNamedTag<Block> SLAVE = tag("slave");
        Tags.IOptionalNamedTag<Block> CONTROLLER = tag("controller");

        static Tags.IOptionalNamedTag<Block> tag(String name) {
            return BlockTags.createOptional(new ResourceLocation(FramedCompactDrawers.MOD_ID, name));
        }

    }

}
