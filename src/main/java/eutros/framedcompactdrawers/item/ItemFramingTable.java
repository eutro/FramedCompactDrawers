package eutros.framedcompactdrawers.item;

import eutros.framedcompactdrawers.block.BlockFramingTable;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class ItemFramingTable extends BlockItem {
    public ItemFramingTable(BlockFramingTable block, Properties properties) {
        super(block, properties);
    }

    protected boolean placeBlock(BlockPlaceContext pContext, @NotNull BlockState pState) {
        return pContext.getLevel()
                .setBlock(
                        pContext.getClickedPos(),
                        pState,
                        // send to clients
                        // force re-renders on main thread
                        // no neighbour reactions (will do after other part is placed)
                        0b11010
                );
    }
}
