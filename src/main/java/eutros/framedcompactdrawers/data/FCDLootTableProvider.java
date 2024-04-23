package eutros.framedcompactdrawers.data;

import com.google.gson.Gson;
import eutros.framedcompactdrawers.block.ModBlocks;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.Deserializers;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.functions.CopyNbtFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.nbt.ContextNbtProvider;

import javax.annotation.ParametersAreNonnullByDefault;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class FCDLootTableProvider implements DataProvider {

    private final PackOutput.PathProvider pathProvider;

    public FCDLootTableProvider(PackOutput output) {
        pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "loot_tables");
    }

    @SuppressWarnings("rawtypes")
    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        Gson ser = Deserializers.createLootTableSerializer().create();
        BlockLootSubProvider bls = new BlockLootSubProvider(Set.of(), FeatureFlagSet.of()) {
            @Override
            protected void generate() {
            }
        };
        List<CompletableFuture<?>> futures = new ArrayList<>();
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
            Path path = pathProvider.json(block.getLootTable());
            LootTable table = bls.createSingleItemTable(block)
                    .apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY)
                            .copy("MatS", "MatS")
                            .copy("MatF", "MatF")
                            .copy("MatT", "MatT"))
                    .setParamSet(LootContextParamSets.BLOCK)
                    .build();
            futures.add(DataProvider.saveStable(output, ser.toJsonTree(table), path));
        }
        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() {
        return "FCD Loot Tables";
    }

}
