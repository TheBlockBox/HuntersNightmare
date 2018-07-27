package pixeleyestudios.huntersdream.util.handlers;

import java.io.File;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import pixeleyestudios.huntersdream.Main;
import pixeleyestudios.huntersdream.util.Reference;

public class ConfigHandler {
	public static int goblinID = 130;
	public static int werewolfVillagerID = 131;
	public static int werewolfID = 132;

	public static boolean customPlayerRender = true;
	public static boolean renderXPBar = true;

	public static boolean showPacketMessages = false;

	public static Configuration config;

	public static void init(File file) {
		config = new Configuration(file);

		String category;

		category = "advanced";
		config.addCustomCategoryComment(category,
				"Settings for advanced users (modders, modpack makers, server admins etc.)");
		showPacketMessages = config.getBoolean("Show packet messages", category, false,
				"Show messages in log when data between the client and the server is synced (doesn't include error chat messages)");

		category = "rendering";
		config.addCustomCategoryComment(category, "Activate/Deactivate render (client side only)");
		customPlayerRender = config.getBoolean("Custom player render", category, true,
				"Use different player render when transformed");
		renderXPBar = config.getBoolean("Render xp bar", category, true,
				"Render the player's (werewolf/vampire etc.) xp bar");

		config.save();
	}

	public static void registerConfig(FMLPreInitializationEvent event) {
		Main.setConfig(new File(event.getModConfigurationDirectory().toString()));
		Main.getConfig().mkdirs();
		init(new File(Main.getConfig().getPath(), Reference.MODID + ".cfg"));
	}
}
