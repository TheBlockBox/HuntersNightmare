package theblockbox.huntersdream.util.handlers;

import java.util.Optional;
import java.util.stream.Stream;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import theblockbox.huntersdream.entity.EntityGoblinTD;
import theblockbox.huntersdream.entity.EntityWerewolf;
import theblockbox.huntersdream.event.TransformationEvent.TransformationEventReason;
import theblockbox.huntersdream.init.CapabilitiesInit;
import theblockbox.huntersdream.util.Reference;
import theblockbox.huntersdream.util.Transformation;
import theblockbox.huntersdream.util.exceptions.UnexpectedBehaviorException;
import theblockbox.huntersdream.util.helpers.ChanceHelper;
import theblockbox.huntersdream.util.helpers.EffectivenessHelper;
import theblockbox.huntersdream.util.helpers.TransformationHelper;
import theblockbox.huntersdream.util.helpers.WerewolfHelper;
import theblockbox.huntersdream.util.interfaces.IInfectInTicks;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformationCreature;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformationPlayer;

@Mod.EventBusSubscriber(modid = Reference.MODID)
public class TransformationEventHandler {

	@SubscribeEvent
	public static void onPlayerTick(PlayerTickEvent event) {
		if (event.phase == Phase.END) {
			EntityPlayer player = event.player;
			ITransformationPlayer cap = TransformationHelper.getITransformationPlayer(player);

			if (WerewolfHelper.isTransformed(player)) {
				if (!player.isCreative() && !player.isSpectator()) {
					if (!player.inventory.isEmpty()) {
						player.inventory.dropAllItems();
					}
				}
			}

			if (player.ticksExisted % 20 == 0) {

				if (!player.world.isRemote) {

					for (ItemStack stack : player.getEquipmentAndArmor()) {
						// damage player if item is effective against transformation (also works when
						// player is not transformed)
						if (EffectivenessHelper.effectiveAgainstTransformation(cap.getTransformation(), stack)) {
							player.attackEntityFrom(TransformationHelper.EFFECTIVE_AGAINST_TRANSFORMATION, 1);
							break;
						}
					}

					if (player.ticksExisted % 80 == 0) {
						EntityPlayerMP playerMP = (EntityPlayerMP) player;

						// call methods in WerewolfEventHandler
						if (cap.getTransformation() == Transformation.WEREWOLF) {
							if (WerewolfHelper.isWerewolfTime(player.world)) {
								if (WerewolfHelper.isTransformed(player)) {
									WerewolfEventHandler.werewolfTimeTransformed(playerMP, cap);
								} else {
									WerewolfEventHandler.werewolfTimeNotTransformed(playerMP, cap);
								}
							} else {
								if (WerewolfHelper.isTransformed(player)) {
									WerewolfEventHandler.notWerewolfTimeTransformed(playerMP, cap);
								} else {
									WerewolfEventHandler.notWerewolfTimeNotTransformed(playerMP, cap);
								}
							}
						}
						// this piece of code syncs the player data every six minutes, so basically
						// you
						// don't have to sync the data every time you change something
						// (though it is recommended)
						if (player.ticksExisted % 7200 == 0) {
							PacketHandler.sendTransformationMessage(playerMP);
							PacketHandler.sendTransformationMessage(playerMP);
						}
					}
				}
			}

			TransformationHelper.changePlayerSize(player);
		}
	}

	@SubscribeEvent
	public static void onEntityHurt(LivingHurtEvent event) {
		EntityLivingBase hurt = event.getEntityLiving();
		Transformation transformationHurt = TransformationHelper.getTransformation(hurt);
		if (event.getSource().getTrueSource() instanceof EntityLivingBase) {
			EntityLivingBase attacker = (EntityLivingBase) event.getSource().getTrueSource();
			Transformation transformationAtt = TransformationHelper.getTransformation(attacker);

			// make that player deals more damage
			if (transformationAtt.isTransformation()) {
				if (attacker instanceof EntityPlayer) {
					event.setAmount(transformationAtt.getDamage(attacker, event.getAmount()));
				}
			}

			if (hurt != null) {
				if (attacker != null) {
					// add effectiveness against undead
					if (attacker.getHeldItemMainhand() != null)
						if (hurt.isEntityUndead()
								&& EffectivenessHelper.effectiveAgainstUndead(attacker.getHeldItemMainhand()))
							event.setAmount(event.getAmount() + 2.5F);
					if (transformationHurt.isTransformation())
						addEffectiveAgainst(event, attacker, hurt, transformationHurt);
				}
			}
		}

		// add protection
		if (transformationHurt.isTransformation()
				&& !(event.getSource().damageType.equals(EffectivenessHelper.THORNS_DAMAGE_NAME))
				&& (event.getSource() != TransformationHelper.EFFECTIVE_AGAINST_TRANSFORMATION)) {
			event.setAmount(transformationHurt.getReducedDamage(hurt, event.getAmount()));
		}
	}

	private static void addEffectiveAgainst(LivingHurtEvent event, EntityLivingBase attacker, EntityLivingBase hurt,
			Transformation transformationHurt) {
		// don't apply when it was thorns damage
		if (!event.getSource().damageType.equals(EffectivenessHelper.THORNS_DAMAGE_NAME)) {
			// first check if immediate source is effective, then weapon and then attacker
			Stream.<Object>of(event.getSource().getImmediateSource(), attacker.getHeldItemMainhand(), attacker).filter(
					obj -> obj != null && EffectivenessHelper.effectiveAgainstTransformation(transformationHurt, obj))
					.findFirst().ifPresent(object -> {
						event.setAmount(EffectivenessHelper.getEffectivenessAgainst(transformationHurt, object)
								* transformationHurt.getReducedDamage(hurt, event.getAmount()));
					});
		}
	}

	// do this at the end so the real damage can be seen
	@SubscribeEvent(priority = EventPriority.LOW)
	public static void addThorns(LivingHurtEvent event) {
		Entity source = event.getSource().getTrueSource();
		EntityLivingBase hurt = event.getEntityLiving();
		if ((!event.getSource().getDamageType().equals(EffectivenessHelper.THORNS_DAMAGE_NAME))
				&& source instanceof EntityLivingBase && hurt != null) {
			EntityLivingBase attacker = (EntityLivingBase) source;
			Transformation transformationAttacker = TransformationHelper.getTransformation(attacker);
			if (transformationAttacker.isTransformation()) {
				ItemStack[] stacks = Stream
						.of(EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS,
								EntityEquipmentSlot.FEET)
						.map(hurt::getItemStackFromSlot).filter(is -> EffectivenessHelper
								.armorEffectiveAgainstTransformation(transformationAttacker, is))
						.toArray(ItemStack[]::new);
				if (stacks.length > 0) {
					float thorns = 0;
					float protection = 0;
					for (ItemStack stack : stacks) {
						thorns += EffectivenessHelper.armorGetEffectivenessAgainst(transformationAttacker, stack);
						protection += EffectivenessHelper.armorGetProtectionAgainst(transformationAttacker, stack);
						stack.damageItem(4, hurt);
					}
					float thorn = thorns * event.getAmount();
					if (thorns > 0)
						attacker.attackEntityFrom(EffectivenessHelper.causeEffectivenessThornsDamage(hurt), thorn);
					if (protection > 0)
						event.setAmount(event.getAmount() / protection);
				}
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void onEntityJoin(EntityJoinWorldEvent event) {
		if (event.getEntity() instanceof EntityCreature) {
			EntityCreature creature = (EntityCreature) event.getEntity();
			Optional<ITransformationCreature> tc = TransformationHelper.getITransformationCreature(creature);

			if (!creature.world.isRemote) {

				if (creature instanceof EntityGolem) {
					EntityGolem entity = (EntityGolem) creature;
					entity.targetTasks.addTask(2,
							new EntityAINearestAttackableTarget<>(entity, EntityWerewolf.class, true));
					entity.targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(entity, EntityPlayer.class, 10,
							true, false, WerewolfHelper::isTransformed));
				} else if (creature instanceof EntityGoblinTD) {
					if (ChanceHelper.chanceOf(1.5F) && (tc.get().getTransformation() == Transformation.HUMAN)) {
						TransformationHelper.changeTransformationWhenPossible(creature, Transformation.WEREWOLF,
								TransformationEventReason.SPAWN);
					}
				} else if (creature instanceof EntityVillager) {
					if (ChanceHelper.chanceOf(5) && (tc.get().getTransformation() == Transformation.HUMAN)) {
						TransformationHelper.changeTransformationWhenPossible(creature, Transformation.WEREWOLF,
								TransformationEventReason.SPAWN);
					}
				}

				tc.ifPresent(t -> {
					if (t.getTransformation().getTextures().length > 0)
						t.setTextureIndexWhenNeeded();
				});
			}

			if (creature instanceof EntityAgeable) {
				((EntityAgeable) creature).tasks.addTask(2, new EntityAIAvoidEntity<>(creature, EntityLivingBase.class,
						WerewolfHelper::isTransformed, 8.0F, 0.8F, 1.1F));
			}
		}
	}

	@SubscribeEvent
	public static void onEntityTick(LivingEvent.LivingUpdateEvent event) {
		if (event.getEntityLiving().ticksExisted % 101 == 0) {
			if (!event.getEntityLiving().world.isRemote) {
				EntityLivingBase entity = event.getEntityLiving();

				if (entity instanceof EntityCreature) {
					EntityCreature creature = (EntityCreature) entity;
					Transformation transformation = TransformationHelper.getTransformation(creature);
					if (transformation == Transformation.WEREWOLF) {
						WerewolfHelper.onWerewolfTick(creature);
					}
				}
				if (TransformationHelper.getTransformation(entity) == Transformation.VAMPIRE) {
					VampireEventHandler.onVampireTick(entity);
				}

				if (entity.hasCapability(CapabilitiesInit.CAPABILITY_INFECT_IN_TICKS, null)) {
					IInfectInTicks iit = TransformationHelper.getIInfectInTicks(entity).get();
					if (iit.getTime() > -1) {
						if (iit.currentlyInfected()) {
							if (iit.getTimeUntilInfection() > 0) {
								iit.setTimeUntilInfection(iit.getTimeUntilInfection() - 80);
							} else if (iit.getTimeUntilInfection() <= 0) {
								// when already done,

								// infect entity
								iit.getInfectionTransformation().getInfect().accept(entity);

								// set to standard (-1)
								iit.setTime(-1);
							}
						} else {
							throw new UnexpectedBehaviorException(
									"IInfectInTicks#currentlyInfected returns false although there is definitely an infection going on");
						}
					}
				}
				WerewolfEventHandler.handleWerewolfInfection(entity);
			}
		}
	}
}
