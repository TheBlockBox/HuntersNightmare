package theblockbox.huntersdream;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
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

	private static File config;

	@Instance
	public static Main instance;

	@SidedProxy(clientSide = Reference.CLIENT, serverSide = Reference.SERVER)
	public static IProxy proxy;

	public static final Logger LOGGER = LogManager.getLogger(Reference.MODID);

	@EventHandler
	public static void preInit(FMLPreInitializationEvent event) {
		RegistryHandler.gameRegistry(event);
		RegistryHandler.preInitCommon(event);
		proxy.preInit();
	}

	@EventHandler
	public static void init(FMLInitializationEvent event) {
		RegistryHandler.initCommon(event);
		proxy.init();
	}

	@EventHandler
	public static void postInit(FMLPostInitializationEvent event) {
		RegistryHandler.postInitCommon(event);
		proxy.postInit();
	}

	@EventHandler
	public static void serverInit(FMLServerStartingEvent event) {
		RegistryHandler.serverRegistries(event);
	}

	public static File getConfig() {
		return config;
	}

	public static void setConfig(File config) {
		Main.config = config;
	}
}