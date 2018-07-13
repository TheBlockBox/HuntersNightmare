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

	public static Configuration config;

	public static void init(File file) {
		config = new Configuration(file);

		String category;

		category = "IDs";
		config.addCustomCategoryComment(category, "Set IDs for entities, worlds etc.");
		goblinID = config.getInt("Goblin ID", category, 130, 120, Integer.MAX_VALUE, "ID for the goblin");
		werewolfVillagerID = config.getInt("Werewolf Villager ID", category, 131, 120, Integer.MAX_VALUE,
				"ID for werewolf villager");
		werewolfID = config.getInt("Werewolf ID", category, 132, 120, Integer.MAX_VALUE, "ID for werewolf");

		category = "Rendering";
		config.addCustomCategoryComment(category, "Activate/Deactivate render");
		customPlayerRender = config.getBoolean("Custom player render", category, true,
				"Use different player render when transformed");
		renderXPBar = config.getBoolean("Render xp bar", category, true,
				"Render the player's (werewolf/vampire etc.) xp bar");

		config.save();
	}

	public static void registerConfig(FMLPreInitializationEvent event) {
		Main.setConfig(new File(event.getModConfigurationDirectory() + "/" + Reference.MODID));
		Main.getConfig().mkdirs();
		init(new File(Main.getConfig().getPath(), Reference.MODID + ".cfg"));
	}
}
