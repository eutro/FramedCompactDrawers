package eutros.framedcompactdrawers.data;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import eutros.framedcompactdrawers.block.BlockFramingTable;
import eutros.framedcompactdrawers.block.ModBlocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.functions.CopyNbtFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.nbt.ContextNbtProvider;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class FCDLootTableProvider extends LootTableProvider {

    public FCDLootTableProvider(DataGenerator gen) {
        super(gen);
    }

    @Nonnull
    @Override
    protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> getTables() {
        return ImmutableList.of(Pair.of(() -> FCDBlockLootTables::getTables, LootContextParamSets.BLOCK));
    }

    protected static class FCDBlockLootTables extends BlockLoot {

        public static void getTables(BiConsumer<ResourceLocation, LootTable.Builder> consumer) {
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
                        createSingleItemTable(block)
                                .apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY)
                                        .copy("MatS", "MatS")
                                        .copy("MatF", "MatF")
                                        .copy("MatT", "MatT")));
            }
            consumer.accept(ModBlocks.framingTable.getLootTable(),
                    createSinglePropConditionTable(ModBlocks.framingTable,
                            BlockFramingTable.SIDE,
                            BlockFramingTable.TableSide.RIGHT));
        }

    }

}
