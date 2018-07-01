package huntersdream.util.handlers;

import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class CapabilityHandler {

	@SubscribeEvent
	public static void onPlayerClone(PlayerEvent.Clone event) {
		// EntityPlayer player = event.getEntityPlayer();
	}
}
