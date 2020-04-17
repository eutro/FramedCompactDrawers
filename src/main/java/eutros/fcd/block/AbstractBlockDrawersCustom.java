package eutros.fcd.block;

import com.jaquadro.minecraft.storagedrawers.block.BlockDrawersCustom;
import eutros.fcd.FCDCreativeTab;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

public abstract class AbstractBlockDrawersCustom extends BlockDrawersCustom {

    public AbstractBlockDrawersCustom(String registryName, String blockName) {
        super(registryName, blockName);
        // I can't be bothered to make an AT.
        ObfuscationReflectionHelper.setPrivateValue(Block.class, this, createTrueBlockState(), "field_176227_L"); // blockState
        replaceDefaultState();
        setCreativeTab(FCDCreativeTab.tab);
    }

    @Nonnull
    protected abstract BlockStateContainer createTrueBlockState();

    protected abstract void replaceDefaultState();

    @Deprecated
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        // simplified default implementation, super does funky stuff
        return !blockAccess.getBlockState(pos.offset(side)).doesSideBlockRendering(blockAccess, pos.offset(side), side.getOpposite());
    }

    @Override
    public boolean doesSideBlockRendering(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing face) {
        // again, super does funky stuff
        return false;
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState();
    }

    @Override
    public boolean isHalfDepth(IBlockState state) {
        return false;
    }

}
