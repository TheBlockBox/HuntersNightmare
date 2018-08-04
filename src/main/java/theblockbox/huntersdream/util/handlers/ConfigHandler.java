package theblockbox.huntersdream.util.handlers;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import theblockbox.huntersdream.util.Reference;

@Config(modid = Reference.MODID, name = "Hunter's Dream Config")
@Config.RequiresWorldRestart()
@Config.LangKey(Reference.CFG_LANG + "title")
public class ConfigHandler {
	@Config.LangKey(Reference.CFG_LANG + "customPlayerRender")
	@Config.Name("Render transformed player differently")
	public static boolean customPlayerRender = true;

	@Config.LangKey(Reference.CFG_LANG + "renderxpbar")
	@Config.Name("Render transformation xp bar")
	public static boolean renderXPBar = true;

	@Config.LangKey(Reference.CFG_LANG + "xpbarleft")
	@Config.Name("Transformation XP bar on the left side")
	public static boolean xpBarLeft = false;

	@Config.LangKey(Reference.CFG_LANG + "xpbartop")
	@Config.Name("Transformation XP bar on the top")
	public static boolean xpBarTop = false;

	@Config.LangKey(Reference.CFG_LANG + "showpacketmessages")
	@Config.Name("Show packet messages")
	public static boolean showPacketMessages = false;

	@Mod.EventBusSubscriber
	public static class ConfigEventHandler {
		@SubscribeEvent
		public static void onConfigChanged(final ConfigChangedEvent.OnConfigChangedEvent event) {
			if (event.getModID().equals(Reference.MODID)) {
				ConfigManager.sync(Reference.MODID, Config.Type.INSTANCE);
			}
		}
	}
}
