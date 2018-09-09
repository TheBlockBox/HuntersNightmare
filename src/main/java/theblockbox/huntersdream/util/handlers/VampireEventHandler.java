package theblockbox.huntersdream.util.handlers;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumActionResult;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import theblockbox.huntersdream.util.Reference;
import theblockbox.huntersdream.util.helpers.VampireHelper;
import theblockbox.huntersdream.util.interfaces.transformation.IVampire;

@Mod.EventBusSubscriber(modid = Reference.MODID)
public class VampireEventHandler {

	@SubscribeEvent
	public static void onVampireDrinkingBlood(PlayerInteractEvent.EntityInteractSpecific event) {
		EntityPlayer player = event.getEntityPlayer();
		if (VampireHelper.isVampire(player) && player.isSneaking() && event.getTarget() instanceof EntityLivingBase) {
			IVampire vampire = VampireHelper.getIVampire(player);
			event.setCancellationResult(EnumActionResult.SUCCESS);
			event.setCanceled(true);
			if (!player.world.isRemote && (player.ticksExisted - vampire.getTimeDrinking()) > 20) {
				vampire.setTimeDrinking(player.ticksExisted);
				VampireHelper.drinkBlood(player, (EntityLivingBase) event.getTarget());
				System.out.println("Your blood: " + vampire.getBlood()); // TODO: Remove
			}
		}
	}
}
