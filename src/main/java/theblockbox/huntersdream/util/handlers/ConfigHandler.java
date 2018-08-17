package theblockbox.huntersdream.util.handlers;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import theblockbox.huntersdream.util.Reference;

@Config(modid = Reference.MODID)
@Config.LangKey(Reference.CFG_LANG + "title")
public class ConfigHandler {
	@Config.LangKey(Reference.CFG_LANG + "customPlayerRender")
	public static boolean customPlayerRender = true;

	@Config.LangKey(Reference.CFG_LANG + "renderXPBar")
	public static boolean renderXPBar = true;

	@Config.LangKey(Reference.CFG_LANG + "xpBarLeft")
	public static boolean xpBarLeft = false;

	@Config.LangKey(Reference.CFG_LANG + "xpBarTop")
	public static boolean xpBarTop = false;

	@Config.LangKey(Reference.CFG_LANG + "showPacketMessages")
	public static boolean showPacketMessages = false;

	@Config.LangKey(Reference.CFG_LANG + "veinSize")
	@Config.RequiresWorldRestart
	@Config.RangeInt(min = 0, max = 20)
	public static int veinSize = 4;

	@Config.LangKey(Reference.CFG_LANG + "generateSilverOre")
	@Config.RequiresWorldRestart
	public static boolean generateSilverOre = true;

	@Config.LangKey(Reference.CFG_LANG + "silverMinY")
	@Config.RequiresWorldRestart
	@Config.RangeInt(min = 1, max = 70)
	public static int silverMinY = 5;

	@Config.LangKey(Reference.CFG_LANG + "silverMaxY")
	@Config.RequiresWorldRestart
	@Config.RangeInt(min = 1, max = 70)
	public static int silverMaxY = 35;

	@Config.LangKey(Reference.CFG_LANG + "silverChance")
	@Config.RequiresWorldRestart
	@Config.RangeInt(min = 0, max = 70)
	public static int silverChance = 4;

	@Config.LangKey(Reference.CFG_LANG + "showFullStackTrace")
	public static boolean showFullStackTrace = false;

	@Mod.EventBusSubscriber(modid = Reference.MODID)
	public static class ConfigEventHandler {
		@SubscribeEvent
		public static void onConfigChanged(final ConfigChangedEvent.OnConfigChangedEvent event) {
			if (event.getModID().equals(Reference.MODID))
				ConfigManager.sync(Reference.MODID, Config.Type.INSTANCE);
		}
	}
}
