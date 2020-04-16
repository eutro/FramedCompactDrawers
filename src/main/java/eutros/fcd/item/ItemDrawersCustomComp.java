package eutros.fcd.item;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.item.ItemCustomDrawers;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemDrawersCustomComp extends ItemCustomDrawers {

    public ItemDrawersCustomComp(Block block) {
        super(block);
        setHasSubtypes(false);
    }

    @Override
    public String getUnlocalizedName(@Nonnull ItemStack stack) {
        return this.getBlock().getUnlocalizedName();
    }

    @Override
    public boolean placeBlockAt(@Nonnull ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
        if(!super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState))
            return false;

        TileEntityDrawers tile = (TileEntityDrawers) world.getTileEntity(pos);
        if(tile != null) {
            //noinspection ConstantConditions
            if(stack.hasTagCompound() && stack.getTagCompound().hasKey("tile"))
                tile.readFromPortableNBT(stack.getTagCompound().getCompoundTag("tile"));

            tile.setIsSealed(false);
        }

        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation (@Nonnull ItemStack itemStack, @Nullable World world, List<String> list, ITooltipFlag advanced) {
        NBTTagCompound cmp = itemStack.getTagCompound();
        boolean hasCompound = itemStack.hasTagCompound() && cmp != null;

        if (hasCompound && cmp.hasKey("material")) {
            String key = cmp.getString("material");
            list.add(I18n.format("storagedrawers.material", I18n.format("storagedrawers.material." + key)));
        }

        list.add(I18n.format("storagedrawers.drawers.description", StorageDrawers.config.getBlockBaseStorage("compdrawers")));

        if (hasCompound && cmp.hasKey("tile"))
            list.add(ChatFormatting.YELLOW + I18n.format("storagedrawers.drawers.sealed"));
    }

}
