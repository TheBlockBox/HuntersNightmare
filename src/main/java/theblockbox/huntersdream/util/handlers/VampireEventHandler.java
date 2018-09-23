package theblockbox.huntersdream.util.handlers;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.FoodStats;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import theblockbox.huntersdream.event.TransformationEvent;
import theblockbox.huntersdream.init.SoundInit;
import theblockbox.huntersdream.init.TransformationInit;
import theblockbox.huntersdream.util.Reference;
import theblockbox.huntersdream.util.VampireFoodStats;
import theblockbox.huntersdream.util.helpers.GeneralHelper;
import theblockbox.huntersdream.util.helpers.TransformationHelper;
import theblockbox.huntersdream.util.helpers.VampireHelper;
import theblockbox.huntersdream.util.helpers.WerewolfHelper;
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
			int time = player.ticksExisted - vampire.getTimeDrinking();
			if (!player.world.isRemote && time > 20 && (vampire.getBlood() < 20)) {
				player.world.playSound(null, player.getPosition(), SoundInit.VAMPIRE_GULP, SoundCategory.PLAYERS, 10,
						1);
				vampire.setTimeDrinking(player.ticksExisted);
				GeneralHelper.executeOnMainThreadIn(() -> {
					VampireHelper.drinkBlood(player, (EntityLivingBase) event.getTarget());
				}, 400, player.world.getMinecraftServer());
			}
		}
	}

	// replace food stats
	@SubscribeEvent
	public static void onTransformationChange(TransformationEvent event) {
		if ((event.getEntityLiving() instanceof EntityPlayer)
				&& event.getTransformationAfter() != event.getTransformationBefore()) {
			EntityPlayer player = (EntityPlayer) event.getEntityLiving();
			if (event.getTransformationAfter() == TransformationInit.VAMPIRE) {
				player.foodStats = VampireFoodStats.INSTANCE;
			} else if (event.getTransformationBefore() == TransformationInit.VAMPIRE) {
				player.foodStats = new FoodStats();
			}
		}
	}

	/** Called from {@link EventHandler#onPlayerJoin(PlayerLoggedInEvent)} */
	public static void onPlayerJoin(PlayerLoggedInEvent event) {
		EntityPlayer player = event.player;
		if (!player.world.isRemote) {
			WerewolfHelper.resetTransformationStage((EntityPlayerMP) player);
			PacketHandler.sendBloodMessage((EntityPlayerMP) player);
		}
		if (TransformationHelper.getTransformation(player) == TransformationInit.VAMPIRE) {
			player.foodStats = VampireFoodStats.INSTANCE;
		}
	}
}
