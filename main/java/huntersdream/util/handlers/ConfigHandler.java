package huntersdream.util.handlers;

import java.io.File;

import huntersdream.Main;
import huntersdream.util.Reference;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ConfigHandler {
	private static int goblinID = 130;

	public static Configuration config;

	public static int getGoblinID() {
		return goblinID;
	}

	public static void init(File file) {
		config = new Configuration(file);

		String category;

		category = "IDs";
		config.addCustomCategoryComment(category, "Set IDs for entities, worlds etc.");
		goblinID = config.getInt("Goblin ID", category, 130, 120, 300, "ID for the goblin");

		config.save();
	}

	public static void registerConfig(FMLPreInitializationEvent event) {
		Main.setConfig(new File(event.getModConfigurationDirectory() + "/" + Reference.MODID));
		Main.getConfig().mkdirs();
		init(new File(Main.getConfig().getPath(), Reference.MODID + ".cfg"));
	}
}
