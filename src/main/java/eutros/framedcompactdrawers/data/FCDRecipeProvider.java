package eutros.framedcompactdrawers.data;

import eutros.framedcompactdrawers.FramedCompactDrawers;
import eutros.framedcompactdrawers.block.ModBlocks;
import eutros.framedcompactdrawers.recipe.FramingRecipe;
import net.minecraft.block.Block;
import net.minecraft.data.CustomRecipeBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ForgeRecipeProvider;

import java.util.Objects;
import java.util.function.Consumer;

public class FCDRecipeProvider extends ForgeRecipeProvider {

    public FCDRecipeProvider(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
        for(Block[] blocks : new Block[][] {
                {ModBlocks.framedCompactDrawer,
                        com.jaquadro.minecraft.storagedrawers.core.ModBlocks.COMPACTING_DRAWERS_3},
                {ModBlocks.framedDrawerController,
                        com.jaquadro.minecraft.storagedrawers.core.ModBlocks.CONTROLLER},
                {ModBlocks.framedSlave,
                        com.jaquadro.minecraft.storagedrawers.core.ModBlocks.CONTROLLER_SLAVE}
        }) {
            ShapedRecipeBuilder.shapedRecipe(blocks[0])
                    .patternLine("///")
                    .patternLine("/D/")
                    .patternLine("///")
                    .key('/', Tags.Items.RODS_WOODEN)
                    .key('D', blocks[1])
                    .addCriterion("has_" + Objects.requireNonNull(blocks[1].getRegistryName()).getNamespace(),
                            hasItem(blocks[1]))
                    .addCriterion("has_sticks", hasItem(Tags.Items.RODS_WOODEN))
                    .setGroup("framing")
                    .build(consumer);
        }
        CustomRecipeBuilder.customRecipe(FramingRecipe.SERIALIZER)
                .build(consumer, FramedCompactDrawers.MOD_ID + ":framing");
    }

}
