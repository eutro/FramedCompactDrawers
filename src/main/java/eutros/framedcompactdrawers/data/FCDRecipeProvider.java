package eutros.framedcompactdrawers.data;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import eutros.framedcompactdrawers.FramedCompactDrawers;
import eutros.framedcompactdrawers.block.ModBlocks;
import eutros.framedcompactdrawers.recipe.FramingRecipe;
import eutros.framedcompactdrawers.recipe.ModTags;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ForgeRecipeProvider;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.function.Consumer;

public class FCDRecipeProvider extends ForgeRecipeProvider {

    public FCDRecipeProvider(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
        for (Pair<? extends Block, Tags.IOptionalNamedTag<Item>> pair : ImmutableList.of(
                Pair.of(ModBlocks.framedCompactDrawer, ModTags.Items.COMPACTING),
                Pair.of(ModBlocks.framedDrawerController, ModTags.Items.CONTROLLER),
                Pair.of(ModBlocks.framedSlave, ModTags.Items.SLAVE)
        )) {
            ShapedRecipeBuilder.shapedRecipe(pair.getLeft())
                    .patternLine("///")
                    .patternLine("/D/")
                    .patternLine("///")
                    .key('/', Tags.Items.RODS_WOODEN)
                    .key('D', pair.getRight())
                    .addCriterion("has_" + pair.getRight().getName().getNamespace(),
                            hasItem(pair.getRight()))
                    .addCriterion("has_sticks", hasItem(Tags.Items.RODS_WOODEN))
                    .setGroup("framed_drawers")
                    .build(consumer);
        }

        ShapedRecipeBuilder.shapedRecipe(ModBlocks.framedTrim, 4)
                .patternLine("/X/")
                .patternLine("X/X")
                .patternLine("/X/")
                .key('/', Tags.Items.RODS_WOODEN)
                .key('X', ItemTags.PLANKS)
                .addCriterion("has_sticks", hasItem(Tags.Items.RODS_WOODEN))
                .addCriterion("has_planks", hasItem(ItemTags.PLANKS))
                .setGroup("framed_drawers")
                .build(consumer);

        for (Triple<? extends Block, Integer, String[]> triple : ImmutableList.of(
                Triple.of(ModBlocks.framedFullOne, 1, new String[]{
                        "///",
                        " C ",
                        "///"
                }),
                Triple.of(ModBlocks.framedFullTwo, 2, new String[]{
                        "/C/",
                        "///",
                        "/C/"
                }),
                Triple.of(ModBlocks.framedFullFour, 4, new String[]{
                        "C/C",
                        "///",
                        "C/C"
                }),

                Triple.of(ModBlocks.framedHalfOne, 1, new String[]{
                        "/S/",
                        " C ",
                        "/S/"
                }),
                Triple.of(ModBlocks.framedHalfTwo, 2, new String[]{
                        "/C/",
                        "/S/",
                        "/C/"
                }),
                Triple.of(ModBlocks.framedHalfFour, 4, new String[]{
                        "C/C",
                        "/S/",
                        "C/C"
                })
        )) {
            ShapedRecipeBuilder srb = ShapedRecipeBuilder
                    .shapedRecipe(triple.getLeft(), triple.getMiddle())
                    .patternLine(triple.getRight()[0])
                    .patternLine(triple.getRight()[1])
                    .patternLine(triple.getRight()[2])
                    .key('/', Tags.Items.RODS_WOODEN)
                    .key('C', Tags.Items.CHESTS_WOODEN)
                    .addCriterion("has_chests_wooden", hasItem(Tags.Items.CHESTS_WOODEN))
                    .addCriterion("has_sticks", hasItem(Tags.Items.RODS_WOODEN))
                    .setGroup("framed_drawers");

            if (Arrays.stream(triple.getRight()).anyMatch(s -> s.contains("S"))) {
                srb.key('S', ItemTags.WOODEN_SLABS)
                        .addCriterion("has_wooden_slabs", hasItem(ItemTags.WOODEN_SLABS));
            }

            srb.build(consumer);
        }

        consumer.accept(new FinishedFramingRecipe("frame_three", Ingredient.fromTag(ModTags.Items.FRAME_TRIPLE), true));
        consumer.accept(new FinishedFramingRecipe("frame_two", Ingredient.fromTag(ModTags.Items.FRAME_DOUBLE), false));
    }

    private static class FinishedFramingRecipe implements IFinishedRecipe {

        private final String path;
        private final Ingredient ingredient;
        private final boolean includeFront;

        public FinishedFramingRecipe(String path, Ingredient ingredient, boolean includeFront) {
            this.path = path;
            this.ingredient = ingredient;
            this.includeFront = includeFront;
        }

        @Override
        public void serialize(JsonObject json) {
            json.add("ingredient", ingredient.serialize());
            json.addProperty("includeFront", includeFront);
        }

        @Override
        public ResourceLocation getID() {
            return new ResourceLocation(FramedCompactDrawers.MOD_ID, path);
        }

        @Override
        public IRecipeSerializer<?> getSerializer() {
            return FramingRecipe.SERIALIZER;
        }

        @Nullable
        public JsonObject getAdvancementJson() {
            return null;
        }

        public ResourceLocation getAdvancementID() {
            return new ResourceLocation("");
        }
    }

}
