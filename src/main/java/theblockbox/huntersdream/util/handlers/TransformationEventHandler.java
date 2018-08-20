package theblockbox.huntersdream.util.handlers;

import com.google.common.base.Predicate;

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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import theblockbox.huntersdream.Main;
import theblockbox.huntersdream.entity.EntityGoblinTD;
import theblockbox.huntersdream.entity.EntityWerewolf;
import theblockbox.huntersdream.entity.model.ModelLycanthropeBiped;
import theblockbox.huntersdream.entity.model.ModelLycanthropeQuadruped;
import theblockbox.huntersdream.event.TransformationXPEvent.TransformationXPSentReason;
import theblockbox.huntersdream.init.CapabilitiesInit;
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
import theblockbox.huntersdream.util.interfaces.transformation.ITransformation;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformationCreature;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformationPlayer;

@Mod.EventBusSubscriber(modid = Reference.MODID)
public class TransformationEventHandler {

	@SubscribeEvent
	public static void onPlayerTick(PlayerTickEvent event) {
		if (event.phase == Phase.END) {
			EntityPlayer player = event.player;
			ITransformationPlayer cap = TransformationHelper.getCap(player);

			if (player.ticksExisted % 80 == 0) {
				if (WerewolfHelper.transformedWerewolf(player)) {
					WerewolfHelper.applyLevelBuffs(player);
					if (!player.isCreative() && !player.isSpectator()) {
						if (!player.inventory.isEmpty()) {
							player.inventory.dropAllItems();
						}
					}
				}

				if (!player.world.isRemote) { // ensures that this is the server side

					for (ItemStack stack : player.getEquipmentAndArmor()) {
						Item item = stack.getItem();
						if (EffectivenessHelper.effectiveAgainstTransformation(cap.getTransformation(), item))
							player.attackEntityFrom(TransformationHelper.EFFECTIVE_AGAINST_TRANSFORMATION,
									cap.transformed() ? cap.getTransformation().getProtection() : 1);
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
						if (WerewolfHelper.playerNotUnderBlock(player) && WerewolfHelper.transformedWerewolf(player)
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
			if (WerewolfHelper.transformedWerewolf(player)) {
				if (player.isSprinting()) {
					// if quadruped
					GeneralHelper.changePlayerSize(player, ModelLycanthropeQuadruped.WIDTH,
							ModelLycanthropeQuadruped.HEIGHT);
					player.eyeHeight = ModelLycanthropeQuadruped.EYE_HEIGHT;
				} else {
					// if biped
					GeneralHelper.changePlayerSize(player, GeneralHelper.STANDARD_PLAYER_WIDTH,
							ModelLycanthropeBiped.HEIGHT);
					player.eyeHeight = ModelLycanthropeBiped.EYE_HEIGHT;
				}
			} else {
				player.eyeHeight = player.getDefaultEyeHeight();
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
						if (EffectivenessHelper.armorEffectiveAgainstTransformation(transformation, armorPart)) {
							// Attack the werewolf back (thorns). The werewolf will get (the damage *
							// effectiveness) / 20 (protection)
							attacker.attackEntityFrom(DamageSource.causeThornsDamage(attacked),
									(EffectivenessHelper.armorGetEffectivenessAgainst(transformation, armorPart)
											* event.getAmount()));
							event.setAmount(event.getAmount()
									/ EffectivenessHelper.armorGetProtectionAgainst(transformation, item.getItem()));
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
			} else if (elb instanceof EntityGoblinTD) {
				EntityGoblinTD goblin = (EntityGoblinTD) elb;
				if (ChanceHelper.chanceOf(1.5F)) {
					ITransformationCreature tc = TransformationHelper.getITransformationCreature(goblin);
					tc.setTransformation(Transformations.WEREWOLF);
				}
			}

			ITransformationCreature tc = elb.getCapability(CapabilitiesInit.CAPABILITY_TRANSFORMATION_CREATURE, null);
			if (tc != null && !(elb instanceof EntityPlayer)) {
				try {
					tc.setTransformationsNotImmuneTo(setEntityTransformationsNotImmuneTo(elb, tc));
				} catch (UnsupportedOperationException e) {
					Main.LOGGER.error("UnsupportedOperationException with message \"" + e.getMessage()
							+ "\" caught.\nEntity class: " + elb.getClass().getName()
							+ "\nImplements ITransformationCreature: " + (elb instanceof ITransformationCreature));
				}
			}
			if (elb instanceof EntityAgeable) {
				((EntityAgeable) elb).tasks.addTask(2, new EntityAIAvoidEntity<EntityLivingBase>((EntityCreature) elb,
						EntityLivingBase.class, WerewolfHelper::transformedWerewolf, 8.0F, 0.8F, 1.1F));
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
		if (event.getEntityLiving().ticksExisted % 100 == 0) {
			if (!event.getEntityLiving().world.isRemote) {

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
