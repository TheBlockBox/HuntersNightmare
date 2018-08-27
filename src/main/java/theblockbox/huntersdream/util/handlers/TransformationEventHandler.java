package theblockbox.huntersdream.util.handlers;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
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
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import theblockbox.huntersdream.entity.EntityGoblinTD;
import theblockbox.huntersdream.entity.EntityWerewolf;
import theblockbox.huntersdream.event.TransformationEvent.TransformationEventReason;
import theblockbox.huntersdream.event.TransformationXPEvent.TransformationXPSentReason;
import theblockbox.huntersdream.init.CapabilitiesInit;
import theblockbox.huntersdream.init.PotionInit;
import theblockbox.huntersdream.util.Reference;
import theblockbox.huntersdream.util.enums.Transformations;
import theblockbox.huntersdream.util.exceptions.UnexpectedBehaviorException;
import theblockbox.huntersdream.util.handlers.PacketHandler.Packets;
import theblockbox.huntersdream.util.helpers.ChanceHelper;
import theblockbox.huntersdream.util.helpers.EffectivenessHelper;
import theblockbox.huntersdream.util.helpers.GeneralHelper;
import theblockbox.huntersdream.util.helpers.TransformationHelper;
import theblockbox.huntersdream.util.helpers.WerewolfHelper;
import theblockbox.huntersdream.util.interfaces.IInfectInTicks;
import theblockbox.huntersdream.util.interfaces.IInfectOnNextMoon;
import theblockbox.huntersdream.util.interfaces.IInfectOnNextMoon.InfectionStatus;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformation;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformationCreature;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformationEntityTransformed;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformationPlayer;
import theblockbox.huntersdream.util.interfaces.transformation.IUntransformedCreatureExtraData;

@Mod.EventBusSubscriber(modid = Reference.MODID)
public class TransformationEventHandler {

	@SubscribeEvent
	public static void onPlayerTick(PlayerTickEvent event) {
		if (event.phase == Phase.END) {
			EntityPlayer player = event.player;
			ITransformationPlayer cap = TransformationHelper.getCap(player);

			if (WerewolfHelper.transformedWerewolf(player)) {
				if (!player.isCreative() && !player.isSpectator()) {
					if (!player.inventory.isEmpty()) {
						player.inventory.dropAllItems();
					}
				}
				if (player.ticksExisted % 80 == 0) {
					WerewolfHelper.applyLevelBuffs(player);
				}
			}

			if (player.ticksExisted % 80 == 0) {

				if (!player.world.isRemote) { // ensures that this is the server side

					for (ItemStack stack : player.getEquipmentAndArmor()) {
						Item item = stack.getItem();
						if (EffectivenessHelper.effectiveAgainstTransformation(cap.getTransformation(), item))
							player.attackEntityFrom(TransformationHelper.EFFECTIVE_AGAINST_TRANSFORMATION,
									cap.transformed() ? cap.getTransformation().getProtection(player) : 1);
					}

					EntityPlayerMP playerMP = (EntityPlayerMP) player;

					if (cap.getTransformation() == Transformations.WEREWOLF) {
						if (WerewolfHelper.isWerewolfTime(player)) {
							if (!cap.transformed()) {
								WerewolfEventHandler.werewolfTimeNotTransformed(playerMP, cap);
							} else {
								WerewolfEventHandler.werewolfTimeTransformed(playerMP, cap);
							}
						} else {
							if (cap.transformed()) {
								WerewolfEventHandler.notWerewolfTimeTransformed(playerMP, cap);
							} else {
								WerewolfEventHandler.notWerewolfTimeNotTransformed(playerMP, cap);
							}
						}
					}

					if (player.ticksExisted % 1200 == 0) {
						// every minute when the player is not under a block, transformed and a
						// werewolf, one xp gets added
						if (GeneralHelper.playerNotUnderBlock(player) && WerewolfHelper.transformedWerewolf(player)
								&& WerewolfHelper.hasMainlyControl(player)) {
							TransformationHelper.addXP((EntityPlayerMP) player, 5,
									TransformationXPSentReason.WEREWOLF_UNDER_MOON);
						}
						// this piece of code syncs the player data every six minutes, so basically
						// you
						// don't have to sync the data every time you change something
						// (though it is recommended)
						if (player.ticksExisted % 7200 == 0) {
							Packets.TRANSFORMATION.sync(player);
						}
					}
				}
			}

			// setting player size here (have to set every tick because it's also set every
			// tick in the EntityPlayer class) We don't have to care about resetting the
			// size because it gets resetted automatically every tick
			TransformationHelper.configurePlayerSize(player);
		}
	}

	@SubscribeEvent
	public static void onEntityHurt(LivingHurtEvent event) {
		if (event.getSource().getTrueSource() instanceof EntityLivingBase) {
			EntityLivingBase attacker = (EntityLivingBase) event.getSource().getTrueSource();
			EntityLivingBase attacked = event.getEntityLiving();
			ITransformation transformationAttacker = TransformationHelper.getITransformation(attacker);

			// make that player deals more damage
			if (transformationAttacker != null) {
				ItemStack weapon = attacker.getHeldItemMainhand();
				if (weapon.isEmpty() && attacker instanceof EntityPlayer) {
					event.setAmount(
							event.getAmount() * transformationAttacker.getTransformation().getGeneralDamage(attacker));
				}
			}

			if (attacked != null) {
				if (attacker != null) {
					// effective against undead
					if (attacker.getHeldItemMainhand().getItem() != null) {
						if (attacked.isEntityUndead() && EffectivenessHelper
								.effectiveAgainstUndead(attacker.getHeldItemMainhand().getItem())) {
							event.setAmount(event.getAmount() + 2.5F);
						}
					}

					// add thorns
					Transformations transformation = TransformationHelper.getTransformation(attacker);
					if (transformation != null) {
						ItemStack[] armor = { attacked.getItemStackFromSlot(EntityEquipmentSlot.HEAD),
								attacked.getItemStackFromSlot(EntityEquipmentSlot.CHEST),
								attacked.getItemStackFromSlot(EntityEquipmentSlot.LEGS),
								attacked.getItemStackFromSlot(EntityEquipmentSlot.FEET) };
						for (ItemStack item : armor) {
							Item armorPart = item.getItem();
							if (EffectivenessHelper.armorEffectiveAgainstTransformation(transformation, armorPart)) {
								// Attack the werewolf back (thorns). The werewolf will get (the damage *
								// effectiveness) / 20 (protection)
								attacker.attackEntityFrom(DamageSource.causeThornsDamage(attacked),
										(EffectivenessHelper.armorGetEffectivenessAgainst(transformation, armorPart)
												* event.getAmount()));
								event.setAmount(event.getAmount() / EffectivenessHelper
										.armorGetProtectionAgainst(transformation, item.getItem()));
								item.damageItem(4, attacked);
							}
						}
					}

					Transformations transformationAttacked = TransformationHelper.getTransformation(attacked);

					// add protection
					if (transformationAttacked != null) {
						event.setAmount(event.getAmount() / transformationAttacked.getProtection(attacked));
					}

					// add effective against
					Object effectiveAgainst = null;
					if (!event.getSource().damageType.equals("thorns")) {
						ItemStack weaponItemStack = attacker.getHeldItemMainhand();
						Item weapon = weaponItemStack.getItem();
						// when the attacker is supernatural,
						if (transformationAttacker != null
								&& transformationAttacker.getTransformation().isSupernatural()) {
							// has no weapon and is effective against werewolf
							if (weaponItemStack.isEmpty() && EffectivenessHelper
									.effectiveAgainstTransformation(transformationAttacked, attacker.getClass())) {
								effectiveAgainst = attacker.getClass();
							} else if (!weaponItemStack.isEmpty() && EffectivenessHelper
									.effectiveAgainstTransformation(transformationAttacked, weapon)) {
								effectiveAgainst = weapon;
								return;
							}
						} else {
							// when attacker is not supernatural

							// first test if immediate source (for example arrow) is effective
							Entity immediateSource = event.getSource().getImmediateSource();
							if (EffectivenessHelper.effectiveAgainstTransformation(transformationAttacked,
									immediateSource.getClass())) {
								effectiveAgainst = immediateSource.getClass();
							} else if (!weaponItemStack.isEmpty() && EffectivenessHelper
									.effectiveAgainstTransformation(transformationAttacked, weapon)) {
								effectiveAgainst = weapon;
							} else if (EffectivenessHelper.effectiveAgainstTransformation(transformationAttacked,
									attacker.getClass())) {
								effectiveAgainst = attacker.getClass();
							}
						}
						if (effectiveAgainst != null) {
							event.setAmount(event.getAmount() * EffectivenessHelper
									.getEffectivenessAgainst(transformationAttacked, effectiveAgainst)
									* transformationAttacked.getProtection(attacked));
						}
					}
				}
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onEntityJoin(EntityJoinWorldEvent event) {
		if (event.getEntity() instanceof EntityLivingBase) {
			EntityLivingBase elb = (EntityLivingBase) event.getEntity();
			ITransformationCreature tc = TransformationHelper.getITransformationCreature(elb);

			if (!elb.world.isRemote) {
				if (tc != null) {
					if (elb instanceof EntityCreature) {
						IUntransformedCreatureExtraData<?> uced = IUntransformedCreatureExtraData
								.getFromEntity((EntityCreature) elb);
						if (uced != null) {
							Transformations[] transformations = uced.getTransformationsNotImmuneTo();
							if (transformations != null && transformations.length > 0) {
								tc.setTransformationsNotImmuneTo(transformations);
							}
						}
					}
				}

				if (elb instanceof EntityGolem) {
					EntityGolem entity = (EntityGolem) elb;
					entity.targetTasks.addTask(2,
							new EntityAINearestAttackableTarget<EntityWerewolf>(entity, EntityWerewolf.class, true));
					entity.targetTasks.addTask(2, new EntityAINearestAttackableTarget<EntityPlayer>(entity,
							EntityPlayer.class, 10, true, false, WerewolfHelper::transformedWerewolf));
				} else if (elb instanceof EntityGoblinTD) {
					if (ChanceHelper.chanceOf(1.5F) && (tc.getTransformation() == Transformations.HUMAN)) {
						TransformationHelper.changeTransformationWhenPossible(elb, Transformations.WEREWOLF,
								TransformationEventReason.SPAWN);
					}
				} else if (elb instanceof EntityVillager) {
					if (ChanceHelper.chanceOf(5) && (tc.getTransformation() == Transformations.HUMAN)) {
						TransformationHelper.changeTransformationWhenPossible(elb, Transformations.WEREWOLF,
								TransformationEventReason.SPAWN);
					}
				}

				if (tc != null) {
					// set texture index when it's not in bounds
					if (tc.getTransformation().getTextures().length > 0) {
						tc.setTextureIndexWhenNeeded();
					}
				}
			}

			if (elb instanceof EntityAgeable) {
				((EntityAgeable) elb).tasks.addTask(2, new EntityAIAvoidEntity<EntityLivingBase>((EntityCreature) elb,
						EntityLivingBase.class, WerewolfHelper::transformedWerewolf, 8.0F, 0.8F, 1.1F));
			}
		}
	}

	@SubscribeEvent
	public static void onEntityTick(LivingEvent.LivingUpdateEvent event) {
		if (event.getEntityLiving().ticksExisted % 100 == 0) {
			if (!event.getEntityLiving().world.isRemote) {
				EntityLivingBase entity = event.getEntityLiving();

				// remove infection when wolfsbane active
				if (entity.isPotionActive(PotionInit.POTION_WOLFSBANE)
						&& entity.hasCapability(CapabilitiesInit.CAPABILITY_INFECT_ON_NEXT_MOON, null)) {
					IInfectOnNextMoon ionm = WerewolfHelper.getIInfectOnNextMoon(entity);
					if (ionm.isInfected()) {
						ionm.setInfectionStatus(InfectionStatus.NOT_INFECTED);
						ionm.setInfectionTick(-1);
						ionm.setInfectionTransformation(Transformations.HUMAN);
					}
				}

				if (!(entity instanceof ITransformationEntityTransformed) && entity instanceof EntityLiving) {
					Transformations transformation = TransformationHelper.getTransformation(entity);
					if (transformation != null) {
						transformation.transformCreatureWhenPossible((EntityLiving) entity);
					}
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
