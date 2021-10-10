package eutros.framedcompactdrawers.recipe;

import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Temporary recipe to frame drawers, until Storage Drawers catches up.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class FramingRecipe extends SpecialRecipe {

    public static final IRecipeSerializer<FramingRecipe> SERIALIZER = new Serializer();
    final Ingredient ingredient;
    final boolean includeFront;

    public FramingRecipe(ResourceLocation idIn, Ingredient ingredient, boolean includeFront) {
        super(idIn);
        this.ingredient = ingredient;
        this.includeFront = includeFront;
    }

    @Override
    public boolean matches(CraftingInventory inv, World worldIn) {
        return !getCraftingResult(inv).isEmpty();
    }

    private static CompoundNBT materialNbt(ItemStack stack) {
        if (stack.getCount() != 1) {
            stack = stack.copy();
            stack.setCount(1);
        }
        return stack.write(new CompoundNBT());
    }

    @Override
    public ItemStack getCraftingResult(CraftingInventory inv) {
        int drawerIndex = -1;
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            if (ingredient.test(inv.getStackInSlot(i))) {
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

        for (int i = 0; i < inv.getSizeInventory(); i++) {
            if (!applicable.contains(i) && !inv.getStackInSlot(i).isEmpty()) {
                return ItemStack.EMPTY;
            }
        }

        ItemStack sideStack = inv.getStackInSlot(sideSlot);
        if (sideStack.isEmpty()) return ItemStack.EMPTY;

        ItemStack frontStack = inv.getStackInSlot(frontSlot);
        if (!includeFront && !frontStack.isEmpty()) return ItemStack.EMPTY;

        ItemStack trimStack = inv.getStackInSlot(trimSlot);

        ItemStack out = inv.getStackInSlot(drawerIndex).copy();
        out.setCount(1);
        CompoundNBT tag = out.getOrCreateTag();
        tag.put("MatS", materialNbt(sideStack));
        tag.put("MatT", materialNbt(trimStack));
        if (includeFront) tag.put("MatF", materialNbt(frontStack));

        return out;
    }

    @Override
    public boolean canFit(int width, int height) {
        return width >= 2 && height >= 2;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    public static class Serializer
            extends ForgeRegistryEntry<IRecipeSerializer<?>>
            implements IRecipeSerializer<FramingRecipe> {

        @Override
        public FramingRecipe read(ResourceLocation recipeId, JsonObject json) {
            return new FramingRecipe(recipeId,
                    Ingredient.deserialize(json.get("ingredient")),
                    json.has("includeFront") && json.get("includeFront").getAsBoolean());
        }

        @Override
        public FramingRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
            return new FramingRecipe(recipeId, Ingredient.read(buffer), buffer.readBoolean());
        }

        @Override
        public void write(PacketBuffer buffer, FramingRecipe recipe) {
            recipe.ingredient.write(buffer);
            buffer.writeBoolean(recipe.includeFront);
        }

    }

}
