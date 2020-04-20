package eutros.framedcompactdrawers.item;

import com.jaquadro.minecraft.storagedrawers.item.ItemCustomDrawers;
import eutros.framedcompactdrawers.block.tile.TileControllerCustom;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemControllerCustom extends ItemCustomDrawers {

    public ItemControllerCustom(Block block) {
        super(block);
    }

    @Override
    public String getUnlocalizedName(@Nonnull ItemStack stack) {
        return this.getBlock().getUnlocalizedName();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(@Nonnull ItemStack itemStack, @Nullable World world, List<String> list, ITooltipFlag advanced) {
        list.add(I18n.format("storagedrawers.controller.description"));
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
        if(!defaultPlaceBlocKAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState))
            return false;

        TileControllerCustom tile = (TileControllerCustom) world.getTileEntity(pos);
        boolean hasCompound = stack.hasTagCompound() && stack.getTagCompound() != null;
        if(tile != null && hasCompound && !stack.getTagCompound().hasKey("tile")) {
            if(stack.getTagCompound().hasKey("MatS"))
                tile.material().setSide(new ItemStack(stack.getTagCompound().getCompoundTag("MatS")));
            if(stack.getTagCompound().hasKey("MatT"))
                tile.material().setTrim(new ItemStack(stack.getTagCompound().getCompoundTag("MatT")));
            if(stack.getTagCompound().hasKey("MatF"))
                tile.material().setFront(new ItemStack(stack.getTagCompound().getCompoundTag("MatF")));
        }

        return true;
    }

    private boolean defaultPlaceBlocKAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
        // default implementation
        if(!world.setBlockState(pos, newState, 11)) {
            return false;
        } else {
            IBlockState state = world.getBlockState(pos);
            if(state.getBlock() == this.block) {
                setTileEntityNBT(world, player, pos, stack);
                this.block.onBlockPlacedBy(world, pos, state, player, stack);
                if(player instanceof EntityPlayerMP) {
                    CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP) player, pos, stack);
                }
            }

            return true;
        }
    }

}
