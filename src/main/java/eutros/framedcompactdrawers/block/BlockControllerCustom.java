package eutros.framedcompactdrawers.block;

import com.jaquadro.minecraft.storagedrawers.block.BlockController;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityController;
import eutros.framedcompactdrawers.block.tile.IFramingHolder;
import eutros.framedcompactdrawers.block.tile.TileControllerCustom;
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

public class BlockControllerCustom extends BlockController {

    public BlockControllerCustom(Properties properties) {
        super(properties);
    }

    @Override
    public TileEntityController newBlockEntity(BlockPos pos, BlockState state) {
        return new TileControllerCustom(pos, state);
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter world, BlockPos pos, Player player) {
        BlockEntity tile = world.getBlockEntity(pos);
        ItemStack stack = super.getCloneItemStack(state, target, world, pos, player);
        if(tile instanceof IFramingHolder) {
            ((IFramingHolder) tile).writeToTag(stack.getOrCreateTag());
        }
        return stack;
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        BlockEntity tile = world.getBlockEntity(pos);
        if(tile instanceof IFramingHolder && !((IFramingHolder) tile).getSide().isEmpty()) {
            return super.use(state, world, pos, player, hand, hit);
        }
        return InteractionResult.PASS;
    }

}
