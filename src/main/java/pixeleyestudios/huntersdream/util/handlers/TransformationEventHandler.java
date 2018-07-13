package pixeleyestudios.huntersdream.util.handlers;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pixeleyestudios.huntersdream.entity.renderer.RenderWolfmanPlayer;
import pixeleyestudios.huntersdream.util.helpers.PacketHelper;
import pixeleyestudios.huntersdream.util.helpers.TransformationHelper;
import pixeleyestudios.huntersdream.util.helpers.TransformationHelper.Transformations;
import pixeleyestudios.huntersdream.util.helpers.WerewolfHelper;
import pixeleyestudios.huntersdream.util.interfaces.ITransformationPlayer;

/**
 * Handles events which are important for transforming
 */
@Mod.EventBusSubscriber
public class TransformationEventHandler {

	private static RenderWolfmanPlayer renderWerewolf = null;

	public static void onPlayerTick(PlayerTickEvent event) {
		EntityPlayer player = event.player;
		if (player.ticksExisted % 20 == 0) {
			ITransformationPlayer cap = TransformationHelper.getCap(player);
			if (!player.world.isRemote) { // ensures that this is the server side

				// werewolf
				if (WerewolfHelper.isWerewolfTime(player)) {
					if (cap.getTransformation() == Transformations.WEREWOLF) {

						if (!cap.transformed()) {
							cap.setTransformed(true);
							PacketHelper.syncPlayerTransformationData(player);
						}
					}
				} else if ((cap.getTransformation() == Transformations.WEREWOLF) && cap.transformed()) {
					cap.setTransformed(false);
					PacketHelper.syncPlayerTransformationData(player);
				}

				if (player.ticksExisted % 1200 == 0) {
					if (WerewolfHelper.playerNotUnderBlock(player)) {
						cap.incrementXP();
					}
					// this piece of code syncs the player data every five minutes, so basically you
					// don't have to sync the data every time you change something
					// (though it is recommended)
					if (player.ticksExisted % 6000 == 0) {
						PacketHelper.syncPlayerTransformationData(player);
					}
				}
			}
			if ((cap.getTransformation() == Transformations.WEREWOLF) && cap.transformed()) {
				WerewolfHelper.applyLevelBuffs(player);
			}
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void onRenderPlayerPre(RenderPlayerEvent.Pre event) {
		if (ConfigHandler.customPlayerRender) {
			EntityPlayer player = event.getEntityPlayer();
			ITransformationPlayer cap = TransformationHelper.getCap(player);

			// werewolf
			if ((cap.getTransformation() == Transformations.WEREWOLF) && cap.transformed()) {
				event.setCanceled(true);
				if (renderWerewolf == null)
					renderWerewolf = new RenderWolfmanPlayer(Minecraft.getMinecraft().getRenderManager());
				renderWerewolf.doRender(player, event.getX(), event.getY(), event.getZ(), player.rotationYaw,
						event.getPartialRenderTick());
			}
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void onRenderPlayerHand(RenderHandEvent event) {
		if (ConfigHandler.renderXPBar) {

		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void onGameOverlayRenderPre(RenderGameOverlayEvent.Pre event) {
		if (ConfigHandler.renderXPBar) {
			if (event.getType() == ElementType.EXPERIENCE) {
				// Gui.drawRect(0, -10, 19, 20, 0130310);
			}
		}
	}
}