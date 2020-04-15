package eutros.fcd;

import eutros.fcd.proxy.CommonProxy;
import eutros.fcd.utils.Reference;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

import java.io.File;

@Mod(
	modid = Reference.MOD_ID,
	name = Reference.NAME,
	version = Reference.VERSION,
	dependencies = "required-after:storagedrawers;required-after:chameleon;"
)
public class FramedCompactDrawers {
	
	public static File config;
	
	public static Logger logger;

	@SidedProxy(clientSide = Reference.CLIENT, serverSide = Reference.SERVER)
	public static CommonProxy proxy;

	@Instance(Reference.MOD_ID)
	public static FramedCompactDrawers instance;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger = event.getModLog();
		proxy.preInit(event);
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.init(event);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit(event);
	}
}
