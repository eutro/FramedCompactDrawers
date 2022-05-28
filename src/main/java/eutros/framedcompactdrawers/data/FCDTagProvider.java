package eutros.framedcompactdrawers.data;

import com.jaquadro.minecraft.storagedrawers.core.ModBlocks;
import eutros.framedcompactdrawers.FramedCompactDrawers;
import eutros.framedcompactdrawers.recipe.ModTags;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import static eutros.framedcompactdrawers.block.ModBlocks.*;

public class FCDTagProvider {

    public static void register(DataGenerator gen, ExistingFileHelper efh) {
        BlockTags blockTags = new BlockTags(gen, efh);
        gen.addProvider(blockTags);
        gen.addProvider(new ItemTags(gen, blockTags, efh));
    }

    private static class BlockTags extends BlockTagsProvider {

        public BlockTags(DataGenerator gen, ExistingFileHelper efh) {
            super(gen, FramedCompactDrawers.MOD_ID, efh);
        }

        @Override
        public void addTags() {
            tag(ModTags.Blocks.FRAME_DOUBLE).add(framedTrim);
            tag(ModTags.Blocks.FRAME_TRIPLE).add(
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
            tag(ModTags.Blocks.COMPACTING).add(ModBlocks.COMPACTING_DRAWERS_3.get());
            tag(ModTags.Blocks.SLAVE).add(ModBlocks.CONTROLLER_SLAVE.get());
            tag(ModTags.Blocks.CONTROLLER).add(ModBlocks.CONTROLLER.get());
            tag(net.minecraft.tags.BlockTags.MINEABLE_WITH_AXE).add(
                    framedTrim,
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
        }

    }

    private static class ItemTags extends ItemTagsProvider {

        public ItemTags(DataGenerator gen, BlockTagsProvider blockTagProvider, ExistingFileHelper efh) {
            super(gen, blockTagProvider, FramedCompactDrawers.MOD_ID, efh);
        }

        @Override
        public void addTags() {
            copy(ModTags.Blocks.FRAME_DOUBLE, ModTags.Items.FRAME_DOUBLE);
            copy(ModTags.Blocks.FRAME_TRIPLE, ModTags.Items.FRAME_TRIPLE);
            copy(ModTags.Blocks.COMPACTING, ModTags.Items.COMPACTING);
            copy(ModTags.Blocks.SLAVE, ModTags.Items.SLAVE);
            copy(ModTags.Blocks.CONTROLLER, ModTags.Items.CONTROLLER);
        }

    }

}
