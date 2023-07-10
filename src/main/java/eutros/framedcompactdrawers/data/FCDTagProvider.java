package eutros.framedcompactdrawers.data;

import com.jaquadro.minecraft.storagedrawers.core.ModBlocks;
import eutros.framedcompactdrawers.FramedCompactDrawers;
import eutros.framedcompactdrawers.recipe.ModTags;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;

import static eutros.framedcompactdrawers.block.ModBlocks.*;

public class FCDTagProvider {

    public static void register(GatherDataEvent evt, DataGenerator gen, ExistingFileHelper efh) {
        BlockTags blockTags = new BlockTags(gen, efh);
        gen.addProvider(evt.includeServer(), blockTags);
        gen.addProvider(evt.includeServer(), new ItemTags(gen, blockTags, efh));
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
            tag(ModTags.Blocks.TRIM).add(
                    ModBlocks.OAK_TRIM.get(),
                    ModBlocks.SPRUCE_TRIM.get(),
                    ModBlocks.BIRCH_TRIM.get(),
                    ModBlocks.JUNGLE_TRIM.get(),
                    ModBlocks.ACACIA_TRIM.get(),
                    ModBlocks.DARK_OAK_TRIM.get(),
                    ModBlocks.CRIMSON_TRIM.get(),
                    ModBlocks.WARPED_TRIM.get(),
                    framedTrim
            );
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
                    framedHalfFour,
                    framingTable
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
            copy(ModTags.Blocks.TRIM, ModTags.Items.TRIM);
        }

    }

}
