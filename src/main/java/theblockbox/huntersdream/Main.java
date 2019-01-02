package theblockbox.huntersdream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import theblockbox.huntersdream.proxy.IProxy;
import theblockbox.huntersdream.util.Reference;
import theblockbox.huntersdream.util.handlers.RegistryHandler;

@Mod(modid = Reference.MODID, name = Reference.NAME, version = Reference.VERSION, acceptedMinecraftVersions = Reference.MC_VERSION, updateJSON = Reference.UPDATE_JSON)
public class Main {

	@Mod.Instance
	public static Main instance;

	@SidedProxy(clientSide = Reference.CLIENT, serverSide = Reference.SERVER)
	public static IProxy proxy;

	private static final Logger LOGGER = LogManager.getLogger(Reference.MODID);

	public static synchronized Logger getLogger() {
		return Main.LOGGER;
	}

	@Mod.EventHandler
	public static void preInit(FMLPreInitializationEvent event) {
		RegistryHandler.preInitCommon(event);
		Main.proxy.preInit();
	}

	@Mod.EventHandler
	public static void init(FMLInitializationEvent event) {
		RegistryHandler.initCommon(event);
		Main.proxy.init();
	}

	@Mod.EventHandler
	public static void postInit(FMLPostInitializationEvent event) {
		RegistryHandler.postInitCommon(event);
		Main.proxy.postInit();
	}

	@Mod.EventHandler
	public static void serverInit(FMLServerStartingEvent event) {
		RegistryHandler.onServerStart(event);
	}
}