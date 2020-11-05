package eutros.framedcompactdrawers.block;

import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.jaquadro.minecraft.storagedrawers.block.BlockStandardDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawersStandard;
import com.jaquadro.minecraft.storagedrawers.core.ModBlocks;
import eutros.framedcompactdrawers.block.tile.IFramingHolder;
import eutros.framedcompactdrawers.block.tile.TileDrawersStandardCustom;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockDrawersStandardCustom extends BlockStandardDrawers {

    public BlockDrawersStandardCustom(int drawerCount, boolean halfDepth, Properties properties) {
        super(drawerCount, halfDepth, properties);
    }

    @Override
    public TileEntityDrawersStandard createTileEntity(BlockState state, IBlockReader world) {
        return TileDrawersStandardCustom.createEntity(getDrawerCount());
    }

    @Override
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
        TileEntity tile = world.getTileEntity(pos);
        ItemStack stack = super.getPickBlock(state, target, world, pos, player);
        if(tile instanceof IFramingHolder) {
            ((IFramingHolder) tile).writeToTag(stack.getOrCreateTag());
        }
        return stack;
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        TileEntity tile = world.getTileEntity(pos);
        if(tile instanceof IFramingHolder && !((IFramingHolder) tile).getSide().isEmpty()) {
            return super.onBlockActivated(state, world, pos, player, hand, hit);
        }
        return ActionResultType.PASS;
    }

    public void setGeometryData() {
        BlockDrawers source = getGeometrySource();
        System.arraycopy(source.countGeometry, 0, countGeometry, 0, countGeometry.length);
        System.arraycopy(source.labelGeometry, 0, labelGeometry, 0, labelGeometry.length);
        System.arraycopy(source.slotGeometry, 0, slotGeometry, 0, slotGeometry.length);
    }

    private BlockDrawers getGeometrySource() {
        if(isHalfDepth()) {
            switch(getDrawerCount()) {
                case 1:
                    return ModBlocks.OAK_HALF_DRAWERS_1;
                case 2:
                    return ModBlocks.OAK_HALF_DRAWERS_2;
                case 4:
                    return ModBlocks.OAK_HALF_DRAWERS_4;
                default:
                    throw new IllegalArgumentException("Illegal drawer count.");
            }
        } else {
            switch(getDrawerCount()) {
                case 1:
                    return ModBlocks.OAK_FULL_DRAWERS_1;
                case 2:
                    return ModBlocks.OAK_FULL_DRAWERS_2;
                case 4:
                    return ModBlocks.OAK_FULL_DRAWERS_4;
                default:
                    throw new IllegalArgumentException("Illegal drawer count.");
            }
        }
    }

}
