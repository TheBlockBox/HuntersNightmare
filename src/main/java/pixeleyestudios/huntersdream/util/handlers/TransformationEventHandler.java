package pixeleyestudios.huntersdream.util.handlers;

import java.util.ArrayList;
import java.util.Iterator;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import pixeleyestudios.huntersdream.entity.EntityWerewolf;
import pixeleyestudios.huntersdream.util.ExecutionPath;
import pixeleyestudios.huntersdream.util.handlers.PacketHandler.Packets;
import pixeleyestudios.huntersdream.util.helpers.TransformationHelper;
import pixeleyestudios.huntersdream.util.helpers.TransformationHelper.TransformationXPSentReason;
import pixeleyestudios.huntersdream.util.helpers.TransformationHelper.Transformations;
import pixeleyestudios.huntersdream.util.helpers.WerewolfHelper;
import pixeleyestudios.huntersdream.util.interfaces.ITransformation;
import pixeleyestudios.huntersdream.util.interfaces.ITransformationPlayer;

@Mod.EventBusSubscriber
public class TransformationEventHandler {
	private static final ArrayList<EntityWerewolf> PLAYER_WEREWOLVES = new ArrayList<>();

	@SubscribeEvent
	public static void onPlayerTick(PlayerTickEvent event) {
		EntityPlayer player = event.player;
		ITransformationPlayer cap = TransformationHelper.getCap(player);

		if (cap.transformed()) {
			if (cap.getTransformation() == Transformations.WEREWOLF) {
				event.player.inventory.dropAllItems();
			}
		}

		if (player.ticksExisted % 20 == 0) {
			if (!player.world.isRemote) { // ensures that this is the server side

				// werewolf
				if (WerewolfHelper.isWerewolfTime(player)) {
					if (cap.getTransformation() == Transformations.WEREWOLF) {

						if (!cap.transformed()) {
							cap.setTransformed(true);
							Packets.TRANSFORMATION.sync(new ExecutionPath(), player);

							if (!WerewolfHelper.hasControl(player)) {
								World world = player.world;
								EntityWerewolf werewolf = new EntityWerewolf(world,
										TransformationHelper.getCap(player).getTextureIndex(),
										"player" + player.getName());
								werewolf.setPosition(player.posX, player.posY, player.posZ);
								PLAYER_WEREWOLVES.add(werewolf);
								world.spawnEntity(werewolf);
								Packets.NO_CONTROL.sync(new ExecutionPath(), player, werewolf);
							}
						}

						// remove werewolves
					} else if (!PLAYER_WEREWOLVES.isEmpty()) {

						Iterator<EntityWerewolf> iterator = PLAYER_WEREWOLVES.iterator();

						while (iterator.hasNext()) {
							EntityWerewolf werewolf = iterator.next();
							World world = werewolf.world;
							Packets.NIGHT_OVER.sync(new ExecutionPath(), WerewolfHelper.getPlayer(werewolf));
							world.removeEntity(werewolf);

							PLAYER_WEREWOLVES.remove(werewolf);
						}
					}
				} else if ((cap.getTransformation() == Transformations.WEREWOLF) && cap.transformed()) {
					cap.setTransformed(false);
					Packets.TRANSFORMATION.sync(new ExecutionPath(), player);
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
						Packets.TRANSFORMATION.sync(new ExecutionPath(), player);
					}
				}
			}
			if ((cap.getTransformation() == Transformations.WEREWOLF) && cap.transformed()) {
				WerewolfHelper.applyLevelBuffs(player);
			}
		}
	}

	@SubscribeEvent
	public static void onEntityHurt(LivingHurtEvent event) {
		EntityLivingBase attacker = (EntityLivingBase) event.getSource().getTrueSource();
		ITransformation transformationAttacker = TransformationHelper.getITransformation(attacker);

		if (transformationAttacker != null) {
			ItemStack weapon = attacker.getHeldItemMainhand();
			if (weapon.isEmpty() && transformationAttacker.transformed() && attacker instanceof EntityPlayer) {
				event.setAmount(event.getAmount() * transformationAttacker.getTransformation().GENERAL_DAMAGE);
			}
		}
	}

	public static EntityWerewolf[] getPlayerWerewolves() {
		return PLAYER_WEREWOLVES.toArray(new EntityWerewolf[0]);
	}
}
