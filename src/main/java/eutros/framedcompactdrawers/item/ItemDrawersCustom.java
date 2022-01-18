package eutros.framedcompactdrawers.item;

import com.jaquadro.minecraft.storagedrawers.item.ItemDrawers;
import eutros.framedcompactdrawers.block.tile.IFramingHolder;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ItemDrawersCustom extends ItemDrawers {

    public ItemDrawersCustom(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    protected boolean updateCustomBlockEntityTag(BlockPos pos, Level worldIn, @Nullable Player player,  ItemStack stack, BlockState state) {
        super.updateCustomBlockEntityTag(pos, worldIn, player, stack, state);
        return setFrame(pos, worldIn, stack);
    }

    public static boolean setFrame(BlockPos pos, Level worldIn, ItemStack stack) {
        BlockEntity tile = worldIn.getBlockEntity(pos);
        if(!(tile instanceof IFramingHolder frameable)) {
            return false;
        }

        if(!stack.hasTag()) {
            return true;
        }

        CompoundTag tag = Objects.requireNonNull(stack.getTag());

        if(tag.contains("MatS"))
            frameable.setSide(ItemStack.of(tag.getCompound("MatS")));
        if(tag.contains("MatT"))
            frameable.setTrim(ItemStack.of(tag.getCompound("MatT")));
        if(tag.contains("MatF"))
            frameable.setFront(ItemStack.of(tag.getCompound("MatF")));

        return true;
    }

}
