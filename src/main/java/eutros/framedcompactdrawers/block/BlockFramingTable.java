package eutros.framedcompactdrawers.block;

import eutros.framedcompactdrawers.block.tile.TileFramingTable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.PushReaction;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

// much of this copied (with love) from BedBlock
public class BlockFramingTable extends HorizontalDirectionalBlock implements EntityBlock {
    public static final EnumProperty<TableSide> SIDE = EnumProperty.create("side", TableSide.class);

    public enum TableSide implements StringRepresentable {
        LEFT("left"),
        RIGHT("right"); // main side

        private final String name;

        TableSide(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }

        @Override
        public String getSerializedName() {
            return name;
        }
    }

    protected BlockFramingTable(Properties pProperties) {
        super(pProperties);
        registerDefaultState(getStateDefinition()
                .any()
                .setValue(SIDE, TableSide.RIGHT));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        Direction direction = pContext.getHorizontalDirection().getOpposite();
        BlockPos targetPos = pContext.getClickedPos();
        BlockPos neighbourPos = targetPos.relative(direction.getClockWise());
        Level level = pContext.getLevel();
        if (!level.getBlockState(neighbourPos).canBeReplaced(pContext)
                || !level.getWorldBorder().isWithinBounds(neighbourPos)) {
            return null;
        }
        return this.defaultBlockState().setValue(FACING, direction);
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState pState) {
        return PushReaction.DESTROY;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING, SIDE);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        if (pState.getValue(SIDE) == TableSide.LEFT) return null;
        return new TileFramingTable(pPos, pState);
    }

    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
        if (!pLevel.isClientSide) {
            pLevel.setBlock(getNeighbourPos(pState, pPos),
                    pState
                            .setValue(SIDE, TableSide.LEFT)
                            .setValue(FACING, pState.getValue(FACING)),
                    3);
            pLevel.blockUpdated(pPos, Blocks.AIR);
            pState.updateNeighbourShapes(
                    pLevel,
                    pPos,
                    // block update, send to clients
                    3);
        }
    }

    @Override
    public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pNeighborPos) {
        if (pDirection == getNeighbourDirection(pState)) {
            if (pNeighborState.is(this) && pNeighborState.getValue(SIDE) != pState.getValue(SIDE)) {
                return pState;
            } else {
                return Blocks.AIR.defaultBlockState();
            }
        } else {
            return super.updateShape(pState, pDirection, pNeighborState, pLevel, pCurrentPos, pNeighborPos);
        }
    }

    public void playerWillDestroy(Level pLevel, BlockPos pPos, BlockState pState, Player pPlayer) {
        if (!pLevel.isClientSide && pPlayer.isCreative()) {
            TableSide side = pState.getValue(SIDE);
            if (side == TableSide.RIGHT) {
                BlockPos neighbourPos = pPos.relative(getNeighbourDirection(pState));
                BlockState neighbourState = pLevel.getBlockState(neighbourPos);
                if (neighbourState.is(this) && neighbourState.getValue(SIDE) == TableSide.LEFT) {
                    pLevel.setBlock(neighbourPos, Blocks.AIR.defaultBlockState(),
                            // block update, sync to clients, prevent neighbours spawning drops
                            0b100011);
                    pLevel.levelEvent(null, LevelEvent.PARTICLES_DESTROY_BLOCK, neighbourPos, Block.getId(neighbourState));
                }
            }
        }

        super.playerWillDestroy(pLevel, pPos, pState, pPlayer);
    }

    public static BlockPos getNeighbourPos(BlockState state, BlockPos pos) {
        Direction neighbourDirection = getNeighbourDirection(state);
        return pos.relative(neighbourDirection);
    }

    @NotNull
    private static Direction getNeighbourDirection(BlockState state) {
        Direction facing = state.getValue(FACING);
        Direction neighbourDirection;
        if (state.getValue(SIDE) == TableSide.LEFT) {
            neighbourDirection = facing.getCounterClockWise();
        } else {
            neighbourDirection = facing.getClockWise();
        }
        return neighbourDirection;
    }

    public static BlockPos getRightPos(BlockState state, BlockPos pos) {
        if (state.getValue(SIDE) == TableSide.RIGHT) {
            return pos;
        } else {
            Direction facing = state.getValue(FACING);
            return pos.relative(facing.getCounterClockWise());
        }
    }

    public long getSeed(BlockState pState, BlockPos pPos) {
        return Mth.getSeed(getRightPos(pState, pPos));
    }
}
