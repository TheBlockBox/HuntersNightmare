package theblockbox.huntersdream.util.handlers;

import com.google.common.base.Predicate;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import theblockbox.huntersdream.Main;
import theblockbox.huntersdream.entity.EntityGoblinTD;
import theblockbox.huntersdream.entity.EntityWerewolf;
import theblockbox.huntersdream.event.TransformationXPEvent.TransformationXPSentReason;
import theblockbox.huntersdream.init.CapabilitiesInit;
import theblockbox.huntersdream.util.Reference;
import theblockbox.huntersdream.util.TimedRunnable;
import theblockbox.huntersdream.util.enums.Transformations;
import theblockbox.huntersdream.util.exceptions.UnexpectedBehaviorException;
import theblockbox.huntersdream.util.handlers.PacketHandler.Packets;
import theblockbox.huntersdream.util.helpers.ChanceHelper;
import theblockbox.huntersdream.util.helpers.GeneralHelper;
import theblockbox.huntersdream.util.helpers.TransformationHelper;
import theblockbox.huntersdream.util.helpers.WerewolfHelper;
import theblockbox.huntersdream.util.interfaces.IInfectInTicks;
import theblockbox.huntersdream.util.interfaces.effective.IEffectiveAgainstTransformation;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformation;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformationCreature;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformationPlayer;

@Mod.EventBusSubscriber(modid = Reference.MODID)
public class TransformationEventHandler {

	@SubscribeEvent
	public static void onPlayerTick(PlayerTickEvent event) {
		EntityPlayer player = event.player;
		ITransformationPlayer cap = TransformationHelper.getCap(player);

		if (WerewolfHelper.transformedWerewolf(player)) {
			WerewolfEventHandler.onWerewolfTick(player);
		}

		if (player.ticksExisted % 20 == 0) {
			if (WerewolfHelper.transformedWerewolf(player)) {
				WerewolfHelper.applyLevelBuffs(player);
			}

			if (!player.world.isRemote) { // ensures that this is the server side

				for (ItemStack stack : player.getEquipmentAndArmor()) {
					Item item = stack.getItem();
					if (item instanceof IEffectiveAgainstTransformation) {
						if (((IEffectiveAgainstTransformation) item).effectiveAgainst(cap.getTransformation())) {
							player.attackEntityFrom(TransformationHelper.EFFECTIVE_AGAINST_TRANSFORMATION,
									cap.transformed() ? cap.getTransformation().getProtection() : 1);
						}
					}
				}

				// only check for the first element because the list is always sorted
				if (!GeneralHelper.RUNNABLES_TO_BE_EXECUTED.isEmpty()) {
					TimedRunnable timedRunnable = GeneralHelper.RUNNABLES_TO_BE_EXECUTED.get(0);
					if (timedRunnable.ON_TICK >= player.ticksExisted && timedRunnable.PLAYER.equals(player)) {
						timedRunnable.RUNNABLE.run();
						GeneralHelper.RUNNABLES_TO_BE_EXECUTED.remove(0);
					}
				}

				WerewolfEventHandler.onPlayerTick(event, (EntityPlayerMP) player, cap);

				if (player.ticksExisted % 1200 == 0) {
					// every minute when the player is not under a block, transformed and a
					// werewolf, one xp gets added
					if (WerewolfHelper.playerNotUnderBlock(player) && WerewolfHelper.transformedWerewolf(player)) {
						TransformationHelper.addXP((EntityPlayerMP) player, 5,
								TransformationXPSentReason.WEREWOLF_UNDER_MOON);
					}
					// this piece of code syncs the player data every three minutes, so basically
					// you
					// don't have to sync the data every time you change something
					// (though it is recommended)
					if (player.ticksExisted % 3600 == 0) {
						Packets.TRANSFORMATION.sync(player);
					}
				}
			}
		}
	}

	@SubscribeEvent
	public static void onEntityHurt(LivingHurtEvent event) {
		EntityLivingBase attacker = (EntityLivingBase) event.getSource().getTrueSource();
		EntityLivingBase attacked = event.getEntityLiving();
		ITransformation transformationAttacker = TransformationHelper.getITransformation(attacker);

		// make that player deals more damage
		if (transformationAttacker != null) {
			ItemStack weapon = attacker.getHeldItemMainhand();
			if (weapon.isEmpty() && transformationAttacker.transformed() && attacker instanceof EntityPlayer) {
				event.setAmount(event.getAmount() * transformationAttacker.getTransformation().getGeneralDamage());
			}
		}

		// add thorns
		if (attacked != null) {
			if (attacker != null) {
				Transformations transformation = TransformationHelper.getTransformation(attacker);
				if (transformation != null) {
					ItemStack[] armor = { attacked.getItemStackFromSlot(EntityEquipmentSlot.HEAD),
							attacked.getItemStackFromSlot(EntityEquipmentSlot.CHEST),
							attacked.getItemStackFromSlot(EntityEquipmentSlot.LEGS),
							attacked.getItemStackFromSlot(EntityEquipmentSlot.FEET) };
					for (ItemStack item : armor) {
						Item armorPart = item.getItem();
						if (TransformationHelper.armorEffectiveAgainstTransformation(transformation, armorPart)) {
							// Attack the werewolf back (thorns). The werewolf will get (the damage *
							// effectiveness) / 20 (protection)
							attacker.attackEntityFrom(DamageSource.causeThornsDamage(attacked),
									(TransformationHelper.armorGetEffectivenessAgainst(transformation, armorPart)
											* event.getAmount()));
							event.setAmount(event.getAmount()
									/ TransformationHelper.armorGetProtectionAgainst(transformation, item.getItem()));
							item.damageItem(4, attacked);
						}
					}
				}
			}
		}
	}

	@SubscribeEvent
	public static void onEntityJoin(EntityJoinWorldEvent event) {
		if (event.getEntity() instanceof EntityLivingBase) {
			EntityLivingBase elb = (EntityLivingBase) event.getEntity();
			if (elb instanceof EntityGolem) {
				EntityGolem entity = (EntityGolem) elb;
				Predicate<EntityPlayer> predicate = input -> {
					return WerewolfHelper.transformedWerewolf(input);
				};
				entity.targetTasks.addTask(2,
						new EntityAINearestAttackableTarget<EntityWerewolf>(entity, EntityWerewolf.class, true));
				entity.targetTasks.addTask(2, new EntityAINearestAttackableTarget<EntityPlayer>(entity,
						EntityPlayer.class, 10, true, false, predicate));
			} else if (elb instanceof EntityVillager) {
				EntityVillager villager = (EntityVillager) elb;
				if (ChanceHelper.chanceOf(5)) {
					ITransformationCreature tc = TransformationHelper.getITransformationCreature(villager);
					tc.setTransformation(Transformations.WEREWOLF);
				}
				Predicate<EntityLivingBase> predicate = WerewolfHelper::transformedWerewolf;
				villager.tasks.addTask(1, new EntityAIAvoidEntity<EntityLivingBase>(villager, EntityLivingBase.class,
						predicate, 8.0F, 0.6D, 0.6D));
			} else if (elb instanceof EntityGoblinTD) {
				EntityGoblinTD goblin = (EntityGoblinTD) elb;
				if (ChanceHelper.chanceOf(1.5F)) {
					ITransformationCreature tc = TransformationHelper.getITransformationCreature(goblin);
					tc.setTransformation(Transformations.WEREWOLF);
				}
				Predicate<EntityLivingBase> predicate = WerewolfHelper::transformedWerewolf;
				goblin.tasks.addTask(1, new EntityAIAvoidEntity<EntityLivingBase>(goblin, EntityLivingBase.class,
						predicate, 8.0F, 0.6D, 0.6D));
			}

			ITransformationCreature tc = TransformationHelper.getITransformationCreature(elb);
			if (tc != null && !(elb instanceof EntityPlayer)) {
				try {
					tc.setTransformationsNotImmuneTo(setEntityTransformationsNotImmuneTo(elb, tc));
				} catch (UnsupportedOperationException e) {
					Main.LOGGER.error("UnsupportedOperationException with message \"" + e.getMessage()
							+ "\" caught.\nEntity class: " + elb.getClass().getName()
							+ "\nImplements ITransformationCreature: " + (elb instanceof ITransformationCreature));
				}
			}
		}
	}

	private static Transformations[] setEntityTransformationsNotImmuneTo(EntityLivingBase entity,
			ITransformationCreature tc) {
		tc.setTextureIndex(tc.getTransformation().getRandomTextureIndex());
		if (entity instanceof EntityVillager) {
			return new Transformations[] { Transformations.WEREWOLF, Transformations.VAMPIRE };
		}
		return new Transformations[0];
	}

	@SubscribeEvent
	public static void onEntityTick(LivingEvent.LivingUpdateEvent event) {
		if (!event.getEntityLiving().world.isRemote) {
			if (event.getEntityLiving() instanceof EntityWerewolf) {
				WerewolfEventHandler.onWerewolfTick(event.getEntityLiving());
			}
			if (event.getEntityLiving().ticksExisted % 80 == 0) {

				EntityLivingBase entity = event.getEntityLiving();
				try {
					EntityCreature creature = (EntityCreature) entity;
					TransformationHelper.getITransformationCreature(creature).getTransformation()
							.transformCreatureWhenPossible(creature);
				} catch (NullPointerException | ClassCastException e) {
				}

				if (entity.hasCapability(CapabilitiesInit.CAPABILITY_INFECT_IN_TICKS, null)) {
					IInfectInTicks iit = TransformationHelper.getIInfectInTicks(entity);
					if (iit.getTime() > -1) {
						if (iit.currentlyInfected()) {
							if (iit.getTimeUntilInfection() > 0) {
								iit.setTimeUntilInfection(iit.getTimeUntilInfection() - 80);
							} else if (iit.getTimeUntilInfection() <= 0) {
								// when already done,

								// infect entity
								iit.getInfectionTransformation().getInfect().infectEntity(entity);

								// set to standard (-1)
								iit.setTime(-1);
							}
						} else {
							throw new UnexpectedBehaviorException(
									"IInfectInTicks#currentlyInfected returns false although there is definitely an infection going on");
						}
					}
				}

				WerewolfEventHandler.handleInfection(entity);
			}
		}
	}
}
