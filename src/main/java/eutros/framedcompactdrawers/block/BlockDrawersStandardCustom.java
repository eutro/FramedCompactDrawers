package eutros.framedcompactdrawers.block;

import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.jaquadro.minecraft.storagedrawers.block.BlockStandardDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawersStandard;
import com.jaquadro.minecraft.storagedrawers.core.ModBlocks;
import eutros.framedcompactdrawers.block.tile.IFramingHolder;
import eutros.framedcompactdrawers.block.tile.TileDrawersStandardCustom;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class BlockDrawersStandardCustom extends BlockStandardDrawers {

    public BlockDrawersStandardCustom(int drawerCount, boolean halfDepth, Properties properties) {
        super(drawerCount, halfDepth, properties);
    }

    @Override
    public TileEntityDrawersStandard newBlockEntity(BlockPos pos, BlockState state) {
        return TileDrawersStandardCustom.createEntity(getDrawerCount(), pos, state);
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter world, BlockPos pos, Player player) {
        BlockEntity tile = world.getBlockEntity(pos);
        ItemStack stack = super.getCloneItemStack(state, target, world, pos, player);
        if (tile instanceof IFramingHolder) {
            ((IFramingHolder) tile).writeToTag(stack.getOrCreateTag());
        }
        return stack;
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        BlockEntity tile = world.getBlockEntity(pos);
        if (tile instanceof IFramingHolder && !((IFramingHolder) tile).getSide().isEmpty()) {
            return super.use(state, world, pos, player, hand, hit);
        }
        return InteractionResult.PASS;
    }

    public void setGeometryData() {
        BlockDrawers source = getGeometrySource();
        System.arraycopy(source.countGeometry, 0, countGeometry, 0, countGeometry.length);
        System.arraycopy(source.labelGeometry, 0, labelGeometry, 0, labelGeometry.length);
        System.arraycopy(source.slotGeometry, 0, slotGeometry, 0, slotGeometry.length);
    }

    private BlockDrawers getGeometrySource() {
        if (isHalfDepth()) {
            return (switch (getDrawerCount()) {
                case 1 -> ModBlocks.OAK_HALF_DRAWERS_1;
                case 2 -> ModBlocks.OAK_HALF_DRAWERS_2;
                case 4 -> ModBlocks.OAK_HALF_DRAWERS_4;
                default -> throw new IllegalArgumentException("Illegal drawer count.");
            }).get();
        } else {
            return (switch (getDrawerCount()) {
                case 1 -> ModBlocks.OAK_FULL_DRAWERS_1;
                case 2 -> ModBlocks.OAK_FULL_DRAWERS_2;
                case 4 -> ModBlocks.OAK_FULL_DRAWERS_4;
                default -> throw new IllegalArgumentException("Illegal drawer count.");
            }).get();
        }
    }

    @Override
    protected ItemStack getMainDrop(BlockState state, TileEntityDrawers tile) {
        // SD not using loot tables grumble grumble
        ItemStack stack = super.getMainDrop(state, tile);
        if (tile instanceof IFramingHolder) {
            ((IFramingHolder) tile).writeToTag(stack.getOrCreateTag());
        }
        return stack;
    }

}
