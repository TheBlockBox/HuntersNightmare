package theblockbox.huntersdream.util.handlers;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import theblockbox.huntersdream.Main;
import theblockbox.huntersdream.util.Reference;

@Config(modid = Reference.MODID, name = "huntersdream/huntersdream")
@Config.LangKey(Reference.CFG_LANG + "title")
public class ConfigHandler {

	@Config.LangKey(Reference.CFG_LANG + "client")
	public static Client client = new Client();

	@Config.LangKey(Reference.CFG_LANG + "server")
	public static Server server = new Server();

	@Config.LangKey(Reference.CFG_LANG + "common")
	public static Common common = new Common();

	public static class Client {
		@Config.LangKey(Reference.CFG_LANG + "customPlayerRender")
		public boolean customPlayerRender = true;

		@Config.LangKey(Reference.CFG_LANG + "xpBarPosition")
		public XPBarPosition xpBarPosition = XPBarPosition.BOTTOM_LEFT;

		public static enum XPBarPosition {
			@Config.LangKey(Reference.CFG_LANG + "dontRender")
			DONT_RENDER, @Config.LangKey(Reference.CFG_LANG + "topLeft")
			TOP_LEFT, @Config.LangKey(Reference.CFG_LANG + "topRight")
			TOP_RIGHT, @Config.LangKey(Reference.CFG_LANG + "bottomLeft")
			BOTTOM_LEFT, @Config.LangKey(Reference.CFG_LANG + "bottomRight")
			BOTTOM_RIGHT;
		}
	}

	public static class Common {
		@Config.LangKey(Reference.CFG_LANG + "showFullStackTrace")
		public boolean showFullStackTrace = false;

		@Config.LangKey(Reference.CFG_LANG + "showPacketMessages")
		public boolean showPacketMessages = false;
	}

	public static class Server {
		@Config.LangKey(Reference.CFG_LANG + "ores")
		public Ores ores = new Ores();

		@Config.LangKey(Reference.CFG_LANG + "generateCastle")
		@Config.RequiresWorldRestart
		public boolean generateCastle = true;

		@Config.LangKey(Reference.CFG_LANG + "generateHuntersCabin")
		@Config.RequiresWorldRestart
		public boolean generateHuntersCabin = true;

		public static class Ores {
			@Config.LangKey(Reference.CFG_LANG + "veinSize")
			@Config.RequiresWorldRestart
			@Config.RangeInt(min = 0, max = 20)
			public int veinSize = 4;

			@Config.LangKey(Reference.CFG_LANG + "generateSilverOre")
			@Config.RequiresWorldRestart
			public boolean generateSilverOre = true;

			@Config.LangKey(Reference.CFG_LANG + "silverMinY")
			@Config.RequiresWorldRestart
			@Config.RangeInt(min = 1, max = 70)
			public int silverMinY = 5;

			@Config.LangKey(Reference.CFG_LANG + "silverMaxY")
			@Config.RequiresWorldRestart
			@Config.RangeInt(min = 1, max = 70)
			public int silverMaxY = 35;

			@Config.LangKey(Reference.CFG_LANG + "silverChance")
			@Config.RequiresWorldRestart
			@Config.RangeInt(min = 0, max = 70)
			public int silverChance = 4;
		}
	}

	@Mod.EventBusSubscriber(modid = Reference.MODID)
	public static class ConfigEventHandler {
		@SubscribeEvent
		public static void onConfigChanged(final ConfigChangedEvent.OnConfigChangedEvent event) {
			if (event.getModID().equals(Reference.MODID)) {
				ConfigManager.sync(Reference.MODID, Config.Type.INSTANCE);
				Main.getLogger().info("Hunter's Dream Config has been changed");
			}
		}
	}
}
