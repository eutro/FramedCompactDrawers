package eutros.framedcompactdrawers.data;

import com.jaquadro.minecraft.storagedrawers.core.ModBlocks;
import eutros.framedcompactdrawers.recipe.ModTags;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ForgeBlockTagsProvider;
import net.minecraftforge.common.data.ForgeItemTagsProvider;

import static eutros.framedcompactdrawers.block.ModBlocks.*;

public class FCDTagProvider {

    public static void register(DataGenerator gen) {
        BlockTags blockTags = new BlockTags(gen);
        gen.addProvider(blockTags);
        gen.addProvider(new ItemTags(gen, blockTags));
    }

    private static class BlockTags extends ForgeBlockTagsProvider {

        public BlockTags(DataGenerator gen) {
            super(gen);
        }

        @Override
        public void registerTags() {
            getOrCreateBuilder(ModTags.Blocks.FRAME_DOUBLE).add(framedTrim);
            getOrCreateBuilder(ModTags.Blocks.FRAME_TRIPLE)
                    .add(
                            framedCompactDrawer,
                            framedDrawerController,
                            framedSlave,
                            framedFullOne,
                            framedFullTwo,
                            framedFullFour,
                            framedHalfOne,
                            framedHalfTwo,
                            framedHalfFour
                    );
            getOrCreateBuilder(ModTags.Blocks.COMPACTING).add(ModBlocks.COMPACTING_DRAWERS_3);
            getOrCreateBuilder(ModTags.Blocks.SLAVE).add(ModBlocks.CONTROLLER_SLAVE);
            getOrCreateBuilder(ModTags.Blocks.CONTROLLER).add(ModBlocks.CONTROLLER);
        }

    }

    private static class ItemTags extends ForgeItemTagsProvider {

        public ItemTags(DataGenerator gen, BlockTagsProvider blockTagProvider) {
            super(gen, blockTagProvider);
        }

        @Override
        public void registerTags() {
            copy(ModTags.Blocks.FRAME_DOUBLE, ModTags.Items.FRAME_DOUBLE);
            copy(ModTags.Blocks.FRAME_TRIPLE, ModTags.Items.FRAME_TRIPLE);
            copy(ModTags.Blocks.COMPACTING, ModTags.Items.COMPACTING);
            copy(ModTags.Blocks.SLAVE, ModTags.Items.SLAVE);
            copy(ModTags.Blocks.CONTROLLER, ModTags.Items.CONTROLLER);
        }

    }

}
