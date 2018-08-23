package theblockbox.huntersdream.util.handlers;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.Iterator;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import theblockbox.huntersdream.Main;
import theblockbox.huntersdream.util.Reference;

@Mod.EventBusSubscriber(modid = Reference.MODID)
public class EventHandler {

	@SubscribeEvent
	public static void onPlayerJoin(PlayerLoggedInEvent event) {
		try {
			EntityPlayer player = event.player;
			if (!player.world.isRemote) {
				WerewolfEventHandler.resetTransformationStage((EntityPlayerMP) player);
			}
			JsonReader reader = new JsonReader(new InputStreamReader(new URL(Reference.UPDATE_JSON).openStream()));
			JsonObject jsonObject = new JsonParser().parse(reader).getAsJsonObject();
			Iterator<JsonElement> iterator = jsonObject.get("supportedmcversions").getAsJsonArray().iterator();
			while (iterator.hasNext())
				if (iterator.next().getAsString().equals(Reference.MC_VERSION))
					return;
			player.sendMessage(new TextComponentTranslation("huntersdream.versionNotSupported", Reference.MC_VERSION));
		} catch (Exception e) {
			Main.getLogger().error("Something went wrong while trying to test for supported minecraft versions");
		}
	}
}
