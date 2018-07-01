package huntersdream.util.handlers;

import java.io.File;

import huntersdream.Main;
import huntersdream.util.Reference;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ConfigHandler {
	public static int goblinID = 130;
	public static int villagerWerewolfID = 131;

	public static Configuration config;

	public static void init(File file) {
		config = new Configuration(file);

		String category;

		category = "IDs";
		config.addCustomCategoryComment(category, "Set IDs for entities, worlds etc.");
		goblinID = config.getInt("Goblin ID", category, 130, 120, Integer.MAX_VALUE, "ID for the goblin");
		villagerWerewolfID = config.getInt("Werewolf Villager ID", category, 131, 120, Integer.MAX_VALUE,
				"ID for werewolf villager");

		config.save();
	}

	public static void registerConfig(FMLPreInitializationEvent event) {
		Main.setConfig(new File(event.getModConfigurationDirectory() + "/" + Reference.MODID));
		Main.getConfig().mkdirs();
		init(new File(Main.getConfig().getPath(), Reference.MODID + ".cfg"));
	}
}
