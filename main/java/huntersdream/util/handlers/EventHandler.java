package huntersdream.util.handlers;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

@Mod.EventBusSubscriber
public class EventHandler {

	@SubscribeEvent
	public static void onPlayerTick(PlayerTickEvent event) {
		TransformationEventHandler.onPlayerTick(event);
	}
}
