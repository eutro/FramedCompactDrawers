package eutros.framedcompactdrawers.recipe;

import eutros.framedcompactdrawers.FramedCompactDrawers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public interface ModTags {

    interface Items {

        TagKey<Item> FRAME_DOUBLE = tag("frame_double");
        TagKey<Item> FRAME_TRIPLE = tag("frame_triple");

        TagKey<Item> COMPACTING = tag("compacting");
        TagKey<Item> SLAVE = tag("slave");
        TagKey<Item> CONTROLLER = tag("controller");

        static TagKey<Item> tag(String name) {
            return ItemTags.create(new ResourceLocation(FramedCompactDrawers.MOD_ID, name));
        }

    }

    interface Blocks {

        TagKey<Block> FRAME_TRIPLE = tag("frame_triple");
        TagKey<Block> FRAME_DOUBLE = tag("frame_double");

        TagKey<Block> COMPACTING = tag("compacting");
        TagKey<Block> SLAVE = tag("slave");
        TagKey<Block> CONTROLLER = tag("controller");

        static TagKey<Block> tag(String name) {
            return BlockTags.create(new ResourceLocation(FramedCompactDrawers.MOD_ID, name));
        }

    }

}
