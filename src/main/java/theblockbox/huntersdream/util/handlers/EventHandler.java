package theblockbox.huntersdream.util.handlers;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.Iterator;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import theblockbox.huntersdream.Main;
import theblockbox.huntersdream.init.LootTableInit;
import theblockbox.huntersdream.util.Reference;

@Mod.EventBusSubscriber(modid = Reference.MODID)
public class EventHandler {

	@SubscribeEvent
	public static void onPlayerJoin(PlayerLoggedInEvent event) {
		VampireEventHandler.onPlayerJoin(event);
		try {
			EntityPlayer player = event.player;
			JsonObject jsonObject = new JsonParser()
					.parse(new InputStreamReader(new URL(Reference.UPDATE_JSON).openStream())).getAsJsonObject();
			Iterator<JsonElement> iterator = jsonObject.get("supportedmcversions").getAsJsonArray().iterator();
			while (iterator.hasNext())
				if (iterator.next().getAsString().equals(Reference.MC_VERSION))
					return;
			TextComponentTranslation tct = new TextComponentTranslation(Reference.MODID + ".versionNotSupported",
					Reference.MC_VERSION);
			tct.getStyle().setColor(TextFormatting.DARK_RED);
			player.sendMessage(tct);
		} catch (Exception e) {
			Main.getLogger().error("Something went wrong while trying to test for supported minecraft versions");
		}
	}

	@SubscribeEvent
	public static void onLootTableLoad(LootTableLoadEvent event) {
		// add hunter's notes to chests
		// for a list of all loot tables see
		// net.minecraft.world.storage.loot.LootTableList
		if (event.getName().toString().startsWith("minecraft:chests/")) {
			event.getTable().addPool(LootTableInit.HUNTERS_JOURNAL_PAGE_POOL);
		}
	}
}