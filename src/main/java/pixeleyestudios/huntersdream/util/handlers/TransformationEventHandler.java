package pixeleyestudios.huntersdream.util.handlers;

import java.util.ArrayList;
import java.util.Iterator;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import pixeleyestudios.huntersdream.entity.EntityWerewolf;
import pixeleyestudios.huntersdream.util.handlers.PacketHandler.Packets;
import pixeleyestudios.huntersdream.util.helpers.TransformationHelper;
import pixeleyestudios.huntersdream.util.helpers.TransformationHelper.TransformationXPSentReason;
import pixeleyestudios.huntersdream.util.helpers.TransformationHelper.Transformations;
import pixeleyestudios.huntersdream.util.helpers.WerewolfHelper;
import pixeleyestudios.huntersdream.util.interfaces.ITransformationPlayer;

@Mod.EventBusSubscriber
public class TransformationEventHandler {
	private static final ArrayList<EntityWerewolf> PLAYER_WEREWOLVES = new ArrayList<>();

	@SubscribeEvent
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
							// PacketHelper.syncPlayerTransformationData(player);
							Packets.TRANSFORMATION.sync(player);

							if (!WerewolfHelper.hasControl(player)) {
								World world = player.world;
								EntityWerewolf werewolf = new EntityWerewolf(world,
										TransformationHelper.getCap(player).getTextureIndex(),
										"player" + player.getName());
								werewolf.setPosition(player.posX, player.posY, player.posZ);
								PLAYER_WEREWOLVES.add(werewolf);
								world.spawnEntity(werewolf);
								Packets.NO_CONTROL.sync(player, werewolf);
							}
						}

						// remove werewolves
					} else if (!PLAYER_WEREWOLVES.isEmpty()) {

						Iterator<EntityWerewolf> iterator = PLAYER_WEREWOLVES.iterator();

						while (iterator.hasNext()) {
							EntityWerewolf werewolf = iterator.next();
							World world = werewolf.world;
							Packets.NIGHT_OVER.sync(WerewolfHelper.getPlayer(werewolf));
							world.removeEntity(werewolf);

							PLAYER_WEREWOLVES.remove(werewolf);
						}
					}
				} else if ((cap.getTransformation() == Transformations.WEREWOLF) && cap.transformed()) {
					cap.setTransformed(false);
					Packets.TRANSFORMATION.sync(player);
				}

				if (player.ticksExisted % 1200 == 0) {
					// every minute when the player is not under a block, transformed and a
					// werewolf, one xp gets added
					if (WerewolfHelper.playerNotUnderBlock(player) && cap.transformed()
							&& cap.getTransformation() == Transformations.WEREWOLF) {
						TransformationHelper.incrementXP((EntityPlayerMP) player,
								TransformationXPSentReason.WEREWOLF_UNDER_MOON);
					}
					// this piece of code syncs the player data every five minutes, so basically you
					// don't have to sync the data every time you change something
					// (though it is recommended)
					if (player.ticksExisted % 6000 == 0) {
						Packets.TRANSFORMATION.sync(player);
					}
				}
			}
			if ((cap.getTransformation() == Transformations.WEREWOLF) && cap.transformed()) {
				WerewolfHelper.applyLevelBuffs(player);
			}
		}
	}

	public static EntityWerewolf[] getPlayerWerewolves() {
		return PLAYER_WEREWOLVES.toArray(new EntityWerewolf[0]);
	}
}
