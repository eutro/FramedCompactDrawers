package eutros.fcd.block;

import com.jaquadro.minecraft.storagedrawers.block.BlockKeyButton;
import com.jaquadro.minecraft.storagedrawers.block.EnumKeyType;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityKeyButton;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashMap;
import java.util.HashSet;

public abstract class AbstractKeyButtonToggle extends AbstractBlockCustomNonDrawer {

    protected final HashMap<BlockPos, EntityPlayer> buttonPosPlayerMap = new HashMap<>();
    protected final HashSet<Block> watchedBlocks = new HashSet<>();

    public AbstractKeyButtonToggle(String registryName, String blockName) {
        super(registryName, blockName);
        MinecraftForge.EVENT_BUS.register(this);
        watchedBlocks.add(this);
    }

    abstract void toggle(World world, BlockPos pos, EntityPlayer player, EnumKeyType keyType);

    @SuppressWarnings("deprecation")
    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        IBlockState button = worldIn.getBlockState(fromPos);
        if(!(button.getBlock() instanceof BlockKeyButton))
            return;

        if(!button.getValue(BlockKeyButton.POWERED) ||
                !pos.equals(fromPos.offset(button.getValue(BlockKeyButton.FACING).getOpposite())))
            return;

        EntityPlayer player = buttonPosPlayerMap.remove(fromPos);

        if(player == null)
            return;

        toggle(worldIn, pos, player, button.getValue(BlockKeyButton.VARIANT));
    }

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
        state = getActualState(state, world, pos);
        TileEntity tile = world.getTileEntity(pos);

        fakeButtonPress(event, pos, world, state, button, player, tile);

        buttonPosPlayerMap.put(pos, player);
    }

    private void fakeButtonPress(PlayerInteractEvent.RightClickBlock event, BlockPos pos, World world, IBlockState state, Block button, EntityPlayer player, TileEntity tile) {
        if (state.getValue(BlockKeyButton.POWERED)) {
            player.swingArm(event.getHand());
            return;
        }

        if (tile instanceof TileEntityKeyButton) {
            ((TileEntityKeyButton) tile).setPowered(true);
        }

        world.setBlockState(pos, state.withProperty(BlockKeyButton.POWERED, true), 3);
        world.markBlockRangeForRenderUpdate(pos, pos);
        world.playSound(player, pos, SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON, SoundCategory.BLOCKS, 0.3F, 0.6F);
        world.notifyNeighborsOfStateChange(pos, button, false);
        world.notifyNeighborsOfStateChange(pos.offset(state.getValue(BlockKeyButton.FACING).getOpposite()), button, false);
        world.scheduleUpdate(pos, button, button.tickRate(world));

        player.swingArm(event.getHand());
    }

}
