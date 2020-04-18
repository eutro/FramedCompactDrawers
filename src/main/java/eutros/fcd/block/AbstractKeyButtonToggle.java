package eutros.fcd.block;

import com.jaquadro.minecraft.storagedrawers.block.BlockKeyButton;
import com.jaquadro.minecraft.storagedrawers.block.EnumKeyType;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityKeyButton;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashSet;
import java.util.Set;

public abstract class AbstractKeyButtonToggle extends AbstractBlockCustomNonDrawer { // who needs a proper API when you can just do this?

    protected final Set<Block> watchedBlocks = new HashSet<>();

    public AbstractKeyButtonToggle(String registryName, String blockName) {
        super(registryName, blockName);
        MinecraftForge.EVENT_BUS.register(new RightClickManager());
        watchedBlocks.add(this);
    }

    abstract void toggle(World world, BlockPos pos, EntityPlayer player, EnumKeyType keyType);

    private class RightClickManager {

        /**
         * If applicable, completely replace the execution of {@link BlockKeyButton#onBlockActivated(World, BlockPos, IBlockState, EntityPlayer, EnumHand, EnumFacing, float, float, float)}.
         * <p>
         * This is applicable whenever a button is pressed that is attached to a Controller Slave of any kind, or the Framed Controller. The original controller gets a pass.
         */
        @SubscribeEvent
        public void playerRightClick(PlayerInteractEvent.RightClickBlock event) {
            BlockPos pos = event.getPos();
            World world = event.getWorld();
            IBlockState state = world.getBlockState(pos);
            Block button = state.getBlock();
            if(!(button instanceof BlockKeyButton))
                return;

            BlockPos targetPos = pos.offset(state.getValue(BlockKeyButton.FACING).getOpposite());
            Block target = world.getBlockState(targetPos).getBlock();
            if(!watchedBlocks.contains(target)) {
                return;
            }

            EntityPlayer player = event.getEntityPlayer();
            event.setResult(Event.Result.DENY);

            if(fakeButtonPress(world, state, pos, player, event.getHand()))
                toggle(world, targetPos, player, state.getValue(BlockKeyButton.VARIANT));
        }

    }

    /**
     * Pretty much just a copy paste of {@link BlockKeyButton#onBlockActivated(World, BlockPos, IBlockState, EntityPlayer, EnumHand, EnumFacing, float, float, float)}.
     * @return true if the button was not already pressed
     */
    private boolean fakeButtonPress(World world, IBlockState state, BlockPos pos, EntityPlayer player, EnumHand hand) {
        state = state.getActualState(world, pos);
        TileEntity tile = world.getTileEntity(pos);

        if(state.getValue(BlockKeyButton.POWERED)) {
            player.swingArm(hand);
            return false;
        }

        if(tile instanceof TileEntityKeyButton) {
            ((TileEntityKeyButton) tile).setPowered(true);
        }

        Block button = state.getBlock();

        world.setBlockState(pos, state.withProperty(BlockKeyButton.POWERED, true), 3);
        world.markBlockRangeForRenderUpdate(pos, pos);
        world.playSound(player, pos, SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON, SoundCategory.BLOCKS, 0.3F, 0.6F);
        world.notifyNeighborsOfStateChange(pos, button, false);
        world.notifyNeighborsOfStateChange(pos.offset(state.getValue(BlockKeyButton.FACING).getOpposite()), button, false);
        world.scheduleUpdate(pos, button, button.tickRate(world));

        player.swingArm(hand);

        return true;
    }

}
