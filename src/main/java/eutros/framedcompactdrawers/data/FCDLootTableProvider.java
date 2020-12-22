package eutros.framedcompactdrawers.data;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import eutros.framedcompactdrawers.block.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraft.loot.LootParameterSet;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.functions.CopyNbt;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.data.ForgeLootTableProvider;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class FCDLootTableProvider extends ForgeLootTableProvider {

    public FCDLootTableProvider(DataGenerator gen) {
        super(gen);
    }

    @Override
    protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootParameterSet>> getTables() {
        return ImmutableList.of(Pair.of(() -> FCDBlockLootTables::getTables, LootParameterSets.BLOCK));
    }

    protected static class FCDBlockLootTables extends BlockLootTables {

        static void getTables(BiConsumer<ResourceLocation, LootTable.Builder> consumer) {
            for (Block block : new Block[]{
                    ModBlocks.framedCompactDrawer,
                    ModBlocks.framedDrawerController,
                    ModBlocks.framedSlave,
                    ModBlocks.framedTrim,
                    ModBlocks.framedFullOne,
                    ModBlocks.framedFullTwo,
                    ModBlocks.framedFullFour,
                    ModBlocks.framedHalfOne,
                    ModBlocks.framedHalfTwo,
                    ModBlocks.framedHalfFour,
            }) {
                consumer.accept(block.getLootTable(),
                        dropping(block)
                                .acceptFunction(CopyNbt.builder(CopyNbt.Source.BLOCK_ENTITY)
                                        .replaceOperation("MatS", "MatS")
                                        .replaceOperation("MatF", "MatF")
                                        .replaceOperation("MatT", "MatT")));
            }
        }

    }

}
