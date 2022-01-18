package eutros.framedcompactdrawers.recipe;

import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Temporary recipe to frame drawers, until Storage Drawers catches up.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class FramingRecipe extends CustomRecipe {

    public static final RecipeSerializer<FramingRecipe> SERIALIZER = new Serializer();
    final Ingredient ingredient;
    final boolean includeFront;

    public FramingRecipe(ResourceLocation idIn, Ingredient ingredient, boolean includeFront) {
        super(idIn);
        this.ingredient = ingredient;
        this.includeFront = includeFront;
    }

    @Override
    public boolean matches(CraftingContainer inv, Level worldIn) {
        return !assemble(inv).isEmpty();
    }

    private static CompoundTag materialNbt(ItemStack stack) {
        if (stack.getCount() != 1) {
            stack = stack.copy();
            stack.setCount(1);
        }
        return stack.save(new CompoundTag());
    }

    @Override
    public ItemStack assemble(CraftingContainer inv) {
        int drawerIndex = -1;
        for (int i = 0; i < inv.getContainerSize(); i++) {
            if (ingredient.test(inv.getItem(i))) {
                if (drawerIndex == -1) drawerIndex = i;
                else return ItemStack.EMPTY;
            }
        }

        if (drawerIndex == -1) {
            return ItemStack.EMPTY;
        }

        int drawerX = drawerIndex % inv.getWidth();
        int drawerY = drawerIndex / inv.getHeight();

        if (drawerX < 1 || drawerY < 1) {
            return ItemStack.EMPTY;
        }

        int sideSlot = (drawerY - 1) * inv.getHeight() + drawerX - 1;
        int trimSlot = (drawerY - 1) * inv.getHeight() + drawerX;
        int frontSlot = (drawerY) * inv.getHeight() + drawerX - 1;

        IntOpenHashSet applicable = new IntOpenHashSet(new int[]{
                drawerIndex,
                sideSlot,
                trimSlot,
                frontSlot
        });

        for (int i = 0; i < inv.getContainerSize(); i++) {
            if (!applicable.contains(i) && !inv.getItem(i).isEmpty()) {
                return ItemStack.EMPTY;
            }
        }

        ItemStack sideStack = inv.getItem(sideSlot);
        if (sideStack.isEmpty()) return ItemStack.EMPTY;

        ItemStack frontStack = inv.getItem(frontSlot);
        if (!includeFront && !frontStack.isEmpty()) return ItemStack.EMPTY;

        ItemStack trimStack = inv.getItem(trimSlot);

        ItemStack out = inv.getItem(drawerIndex).copy();
        out.setCount(1);
        CompoundTag tag = out.getOrCreateTag();
        tag.put("MatS", materialNbt(sideStack));
        tag.put("MatT", materialNbt(trimStack));
        if (includeFront) tag.put("MatF", materialNbt(frontStack));

        return out;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width >= 2 && height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    public static class Serializer
            extends ForgeRegistryEntry<RecipeSerializer<?>>
            implements RecipeSerializer<FramingRecipe> {

        @Override
        public FramingRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            return new FramingRecipe(recipeId,
                    Ingredient.fromJson(json.get("ingredient")),
                    json.has("includeFront") && json.get("includeFront").getAsBoolean());
        }

        @Override
        public FramingRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            return new FramingRecipe(recipeId, Ingredient.fromNetwork(buffer), buffer.readBoolean());
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, FramingRecipe recipe) {
            recipe.ingredient.toNetwork(buffer);
            buffer.writeBoolean(recipe.includeFront);
        }

    }

}
