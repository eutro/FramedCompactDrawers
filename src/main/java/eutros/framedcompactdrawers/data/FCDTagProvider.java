package eutros.framedcompactdrawers.data;

import com.jaquadro.minecraft.storagedrawers.core.ModBlocks;
import eutros.framedcompactdrawers.FramedCompactDrawers;
import eutros.framedcompactdrawers.recipe.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

import static eutros.framedcompactdrawers.block.ModBlocks.*;

public class FCDTagProvider {

    public static void register(GatherDataEvent evt) {
        DataGenerator gen = evt.getGenerator();
        PackOutput output = gen.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = evt.getLookupProvider();
        ExistingFileHelper efh = evt.getExistingFileHelper();
        BlockTags blockTags = new BlockTags(output, lookupProvider, efh);
        ItemTags itemTags = new ItemTags(output, lookupProvider, blockTags, efh);
        gen.addProvider(evt.includeServer(), blockTags);
        gen.addProvider(evt.includeServer(), itemTags);
    }

    private static class BlockTags extends BlockTagsProvider {

        public BlockTags(PackOutput output,
                         CompletableFuture<HolderLookup.Provider> lookupProvider,
                         ExistingFileHelper efh) {
            super(output, lookupProvider, FramedCompactDrawers.MOD_ID, efh);
        }

        @Override
        public void addTags(HolderLookup.Provider provider) {
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

        public ItemTags(PackOutput output,
                        CompletableFuture<HolderLookup.Provider> lookupProvider,
                        BlockTagsProvider blockTagProvider,
                        ExistingFileHelper efh) {
            super(output, lookupProvider, blockTagProvider.contentsGetter(), FramedCompactDrawers.MOD_ID, efh);
        }

        @Override
        public void addTags(HolderLookup.Provider provider) {
            copy(ModTags.Blocks.FRAME_DOUBLE, ModTags.Items.FRAME_DOUBLE);
            copy(ModTags.Blocks.FRAME_TRIPLE, ModTags.Items.FRAME_TRIPLE);
            copy(ModTags.Blocks.COMPACTING, ModTags.Items.COMPACTING);
            copy(ModTags.Blocks.SLAVE, ModTags.Items.SLAVE);
            copy(ModTags.Blocks.CONTROLLER, ModTags.Items.CONTROLLER);
        }
    }

}
