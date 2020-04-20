package eutros.framedcompactdrawers.integration.waila;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.integration.Waila;
import eutros.framedcompactdrawers.integration.IIntegrationPlugin;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaRegistrar;
import mcp.mobius.waila.api.impl.ConfigHandler;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.event.FMLInterModComms;

import javax.annotation.Nonnull;
import java.util.List;

public class WailaPlugin implements IIntegrationPlugin {

    @Nonnull
    @Override
    public String getModID() {
        return "waila";
    }

    @Override
    public void init() {
        FMLInterModComms.sendMessage("waila", "register", "eutros.framedcompactdrawers.integration.waila.WailaPlugin.register");
    }

    @Override
    public void postInit() {
    }

    @SuppressWarnings("unused")
    public static void register(IWailaRegistrar registrar) {
        ConfigHandler configHandler = ConfigHandler.instance();

        Waila.WailaDrawer provider = new OverwrittenWailaDrawer();

        registrar.registerBodyProvider(provider, BlockDrawers.class);
        registrar.registerStackProvider(provider, BlockDrawers.class);

        configHandler.addConfig(StorageDrawers.MOD_NAME, "display.content", I18n.format("storageDrawers.waila.config.displayContents"), true);
        configHandler.addConfig(StorageDrawers.MOD_NAME, "display.stacklimit", I18n.format("storageDrawers.waila.config.displayStackLimit"), true);
        configHandler.addConfig(StorageDrawers.MOD_NAME, "display.status", I18n.format("storageDrawers.waila.config.displayStatus"), true);
    }

    public static class OverwrittenWailaDrawer extends Waila.WailaDrawer {

        @Override
        public List<String> getWailaBody(@Nonnull ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
            TileEntity te = accessor.getTileEntity();
            if(!(te instanceof TileEntityDrawers))
                return currenttip;

            return super.getWailaBody(itemStack, currenttip, accessor, config);
        }

    }

}
