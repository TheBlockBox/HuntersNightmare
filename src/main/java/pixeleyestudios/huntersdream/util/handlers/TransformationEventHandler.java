package pixeleyestudios.huntersdream.util.handlers;

import java.util.ArrayList;
import java.util.Iterator;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pixeleyestudios.huntersdream.entity.EntityWerewolf;
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
	public static final ArrayList<EntityWerewolf> PLAYER_WEREWOLVES = new ArrayList<>();

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

							if (!WerewolfHelper.hasControl(player)) {
								World world = player.world;
								EntityWerewolf werewolf = new EntityWerewolf(world,
										TransformationHelper.getCap(player).getTextureIndex(),
										"player" + player.getName());
								werewolf.setPosition(player.posX, player.posY, player.posZ);
								PLAYER_WEREWOLVES.add(werewolf);
								world.spawnEntity(werewolf);
								PacketHelper.syncPlayerTransformationWerewolfNoControl(player, werewolf);
							}
						}
						// remove werewolves
					} else if (!PLAYER_WEREWOLVES.isEmpty()) {

						Iterator<EntityWerewolf> iterator = PLAYER_WEREWOLVES.iterator();

						while (iterator.hasNext()) {
							EntityWerewolf werewolf = iterator.next();
							World world = werewolf.world;
							PacketHelper.syncPlayerTransformationWerewolfNightOver(WerewolfHelper.getPlayer(werewolf));
							world.removeEntity(werewolf);

							PLAYER_WEREWOLVES.remove(werewolf);
						}
					}
				} else if ((cap.getTransformation() == Transformations.WEREWOLF) && cap.transformed()) {
					cap.setTransformed(false);
					PacketHelper.syncPlayerTransformationData(player);
				}

				if (player.ticksExisted % 1200 == 0) {
					// every minute when the player is not under a block, transformed and a
					// werewolf, one xp gets added
					if (WerewolfHelper.playerNotUnderBlock(player) && cap.transformed()
							&& cap.getTransformation() == Transformations.WEREWOLF) {
						TransformationHelper.incrementXP((EntityPlayerMP) player);
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
			if ((cap.getTransformation() == Transformations.WEREWOLF) && cap.transformed()
					&& WerewolfHelper.hasControl(player)) {
				event.setCanceled(true);
				if (renderWerewolf == null)
					renderWerewolf = new RenderWolfmanPlayer(Minecraft.getMinecraft().getRenderManager());
				renderWerewolf.doRender(player, event.getX(), event.getY(), event.getZ(), player.rotationYaw,
						event.getPartialRenderTick());
			}
		}
	}

	// TODO: Custom hand render
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void onRenderPlayerHand(RenderHandEvent event) {
		if (ConfigHandler.customPlayerRender) {

		}
	}

	// TODO: Transformation xp handler
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void onGameOverlayRenderPre(RenderGameOverlayEvent.Pre event) {
		if (ConfigHandler.renderXPBar) {
			if (event.getType() == ElementType.EXPERIENCE) {
			}
		}
	}
}