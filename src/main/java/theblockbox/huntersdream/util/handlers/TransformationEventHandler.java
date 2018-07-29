package theblockbox.huntersdream.util.handlers;

import java.util.ArrayList;
import java.util.Iterator;

import com.google.common.base.Predicate;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import theblockbox.huntersdream.entity.EntityWerewolf;
import theblockbox.huntersdream.util.ExecutionPath;
import theblockbox.huntersdream.util.enums.Transformations;
import theblockbox.huntersdream.util.handlers.PacketHandler.Packets;
import theblockbox.huntersdream.util.helpers.TransformationHelper;
import theblockbox.huntersdream.util.helpers.TransformationHelper.TransformationXPSentReason;
import theblockbox.huntersdream.util.helpers.WerewolfHelper;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformation;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformationPlayer;

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
							player.getEntityBoundingBox().grow(0, 1, 0);
							Packets.TRANSFORMATION.sync(new ExecutionPath(), player);

							if (!WerewolfHelper.hasControl(player)) {
								World world = player.world;
								EntityWerewolf werewolf = new EntityWerewolf(world,
										TransformationHelper.getCap(player).getTextureIndex(),
										"player" + player.getName());
								werewolf.setPosition(player.posX, player.posY, player.posZ);
								PLAYER_WEREWOLVES.add(werewolf);
								world.spawnEntity(werewolf);
								Packets.NO_CONTROL.sync(new ExecutionPath(), player, werewolf.getEntityId());
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
							player.getEntityBoundingBox().contract(0, 1, 0);
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
						TransformationHelper.addXP((EntityPlayerMP) player, 5,
								TransformationXPSentReason.WEREWOLF_UNDER_MOON, new ExecutionPath());
					}
					// this piece of code syncs the player data every ten minutes, so basically you
					// don't have to sync the data every time you change something
					// (though it is recommended)
					if (player.ticksExisted % 12000 == 0) {
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

	@SubscribeEvent
	public static void onEntityJoin(EntityJoinWorldEvent event) {
		if (event.getEntity() instanceof EntityGolem) {
			EntityGolem entity = (EntityGolem) event.getEntity();
			Predicate<EntityPlayer> predicate = input -> {
				ITransformation transformation = TransformationHelper.getITransformation(input);
				return (transformation.getTransformation() == Transformations.WEREWOLF) && transformation.transformed();
			};
			entity.targetTasks.addTask(2,
					new EntityAINearestAttackableTarget<EntityWerewolf>(entity, EntityWerewolf.class, true));
			entity.targetTasks.addTask(2, new EntityAINearestAttackableTarget<EntityPlayer>(entity, EntityPlayer.class,
					10, true, false, predicate));
		}
	}

	@SubscribeEvent
	public static void onEntityTick(LivingEvent.LivingUpdateEvent event) {
		// check every two seconds
		if (event.getEntity().ticksExisted % 40 == 0) {
			try {
				if (event.getEntityLiving() instanceof EntityCreature) {
					EntityCreature creature = (EntityCreature) event.getEntityLiving();
					TransformationHelper.getITransformationCreature(creature).getCurrentTransformation()
							.transformCreatureWhenPossible(creature);
				}
			} catch (NullPointerException e) {
			}
		}
	}

	public static EntityWerewolf[] getPlayerWerewolves() {
		return PLAYER_WEREWOLVES.toArray(new EntityWerewolf[0]);
	}
}
