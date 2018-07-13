package pixeleyestudios.huntersdream;

import java.io.File;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import pixeleyestudios.huntersdream.proxy.CommonProxy;
import pixeleyestudios.huntersdream.util.Reference;
import pixeleyestudios.huntersdream.util.handlers.RegistryHandler;

@Mod(modid = Reference.MODID, name = Reference.NAME, version = Reference.VERSION)
public class Main {

	private static File config;

	@Instance
	public static Main instance;

	@SidedProxy(clientSide = Reference.CLIENT, serverSide = Reference.COMMON)
	public static CommonProxy proxy;

	@EventHandler
	public static void preInit(FMLPreInitializationEvent event) {
		RegistryHandler.otherRegistries(event);
		RegistryHandler.preInitRegistries(event);
	}

	@EventHandler
	public static void init(FMLInitializationEvent event) {
		RegistryHandler.initRegistries(event);
	}

	@EventHandler
	public static void postInit(FMLPostInitializationEvent event) {
		RegistryHandler.postInitRegistries(event);
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