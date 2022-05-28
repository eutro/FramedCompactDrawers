package eutros.framedcompactdrawers.data;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import eutros.framedcompactdrawers.FramedCompactDrawers;
import eutros.framedcompactdrawers.block.ModBlocks;
import eutros.framedcompactdrawers.recipe.FramingRecipe;
import eutros.framedcompactdrawers.recipe.ModTags;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.function.Consumer;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class FCDRecipeProvider extends RecipeProvider {

    public FCDRecipeProvider(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer) {
        for (Pair<? extends Block, TagKey<Item>> pair : ImmutableList.of(
                Pair.of(ModBlocks.framedCompactDrawer, ModTags.Items.COMPACTING),
                Pair.of(ModBlocks.framedDrawerController, ModTags.Items.CONTROLLER),
                Pair.of(ModBlocks.framedSlave, ModTags.Items.SLAVE)
        )) {
            ShapedRecipeBuilder.shaped(pair.getLeft())
                    .pattern("///")
                    .pattern("/D/")
                    .pattern("///")
                    .define('/', Tags.Items.RODS_WOODEN)
                    .define('D', pair.getRight())
                    .unlockedBy("has_" + pair.getRight().location().getNamespace(),
                            has(pair.getRight()))
                    .unlockedBy("has_sticks", has(Tags.Items.RODS_WOODEN))
                    .group("framed_drawers")
                    .save(consumer);
        }

        ShapedRecipeBuilder.shaped(ModBlocks.framedTrim, 4)
                .pattern("/X/")
                .pattern("X/X")
                .pattern("/X/")
                .define('/', Tags.Items.RODS_WOODEN)
                .define('X', ItemTags.PLANKS)
                .unlockedBy("has_sticks", has(Tags.Items.RODS_WOODEN))
                .unlockedBy("has_planks", has(ItemTags.PLANKS))
                .group("framed_drawers")
                .save(consumer);

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
                    .shaped(triple.getLeft(), triple.getMiddle())
                    .pattern(triple.getRight()[0])
                    .pattern(triple.getRight()[1])
                    .pattern(triple.getRight()[2])
                    .define('/', Tags.Items.RODS_WOODEN)
                    .define('C', Tags.Items.CHESTS_WOODEN)
                    .unlockedBy("has_chests_wooden", has(Tags.Items.CHESTS_WOODEN))
                    .unlockedBy("has_sticks", has(Tags.Items.RODS_WOODEN))
                    .group("framed_drawers");

            if (Arrays.stream(triple.getRight()).anyMatch(s -> s.contains("S"))) {
                srb.define('S', ItemTags.WOODEN_SLABS)
                        .unlockedBy("has_wooden_slabs", has(ItemTags.WOODEN_SLABS));
            }

            srb.save(consumer);
        }

        consumer.accept(new FinishedFramingRecipe("frame_three", Ingredient.of(ModTags.Items.FRAME_TRIPLE), true));
        consumer.accept(new FinishedFramingRecipe("frame_two", Ingredient.of(ModTags.Items.FRAME_DOUBLE), false));
    }

    private static class FinishedFramingRecipe implements FinishedRecipe {

        private final String path;
        private final Ingredient ingredient;
        private final boolean includeFront;

        public FinishedFramingRecipe(String path, Ingredient ingredient, boolean includeFront) {
            this.path = path;
            this.ingredient = ingredient;
            this.includeFront = includeFront;
        }

        @Override
        public void serializeRecipeData(JsonObject json) {
            json.add("ingredient", ingredient.toJson());
            json.addProperty("includeFront", includeFront);
        }

        @Override
        public ResourceLocation getId() {
            return new ResourceLocation(FramedCompactDrawers.MOD_ID, path);
        }

        @Override
        public RecipeSerializer<?> getType() {
            return FramingRecipe.SERIALIZER;
        }

        @Override
        @Nullable
        public JsonObject serializeAdvancement() {
            return null;
        }

        @Override
        @Nullable
        public ResourceLocation getAdvancementId() {
            return new ResourceLocation("");
        }
    }

}
