package theblockbox.huntersdream.util.handlers;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import theblockbox.huntersdream.event.TransformationXPEvent.TransformationXPSentReason;
import theblockbox.huntersdream.init.CapabilitiesInit;
import theblockbox.huntersdream.util.Reference;
import theblockbox.huntersdream.util.enums.Transformations;
import theblockbox.huntersdream.util.helpers.ChanceHelper;
import theblockbox.huntersdream.util.helpers.TransformationHelper;
import theblockbox.huntersdream.util.helpers.WerewolfHelper;
import theblockbox.huntersdream.util.interfaces.IInfectOnNextMoon;
import theblockbox.huntersdream.util.interfaces.IInfectOnNextMoon.InfectionStatus;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformation;

@Mod.EventBusSubscriber(modid = Reference.MODID)
public class WerewolfEventHandler {

	// use LivingDamage only for removing damage and LivingHurt for damage and
	// damaged resources
	@SubscribeEvent
	public static void onWerewolfHurt(LivingHurtEvent event) {
		// what this method should do:
		// if attacker is supernatural, use ability or weapon
		// if attacker isn't supernatural use direct attacker, then weapon, then
		// attacker

		EntityLivingBase hurtWerewolf = event.getEntityLiving();
		ITransformation transformationWerewolf = TransformationHelper.getITransformation(hurtWerewolf);
		// this cast may cause exceptions, but we'll have to test
		EntityLivingBase attacker = (EntityLivingBase) event.getSource().getTrueSource();
		ITransformation transformationAttacker = TransformationHelper.getITransformation(attacker);

		if (transformationWerewolf != null) {
			if (WerewolfHelper.transformedWerewolf(hurtWerewolf)) {
				// now it is made sure that the attacked entity is a werewolf

				if (!event.getSource().damageType.equals("thorns")) {
					if (attacker != null) {
						ItemStack weaponItemStack = attacker.getHeldItemMainhand();
						Item weapon = weaponItemStack.getItem();
						// when the attacker is supernatural,
						if (transformationAttacker != null
								&& transformationAttacker.getTransformation().isSupernatural()) {
							// has no weapon and is effective against werewolf
							if (weaponItemStack.isEmpty() && WerewolfHelper.effectiveAgainstWerewolf(attacker)) {
								event.setAmount(
										event.getAmount() * WerewolfHelper.getEffectivenessAgainstWerewolf(attacker));
								return;
							} else if (!weaponItemStack.isEmpty() && WerewolfHelper.effectiveAgainstWerewolf(weapon)) {
								event.setAmount(
										event.getAmount() * WerewolfHelper.getEffectivenessAgainstWerewolf(weapon));
								return;
							}
						} else {
							// when attacker is not supernatural

							// first test if immediate source (for example arrow) is effective
							Entity immediateSource = event.getSource().getImmediateSource();
							if (WerewolfHelper.effectiveAgainstWerewolf(immediateSource)) {
								event.setAmount(event.getAmount()
										* WerewolfHelper.getEffectivenessAgainstWerewolf(immediateSource));
								return;
							} else if (!weaponItemStack.isEmpty() && WerewolfHelper.effectiveAgainstWerewolf(weapon)) {
								event.setAmount(
										event.getAmount() * WerewolfHelper.getEffectivenessAgainstWerewolf(weapon));
								return;
							} else if (WerewolfHelper.effectiveAgainstWerewolf(attacker)) {
								event.setAmount(
										event.getAmount() * WerewolfHelper.getEffectivenessAgainstWerewolf(attacker));
								return;
							}
						}
					}
				}

				// when nothing applies (this should also reduce any other damage from other
				// damage sources)
				event.setAmount((event.getAmount() / Transformations.WEREWOLF.getProtection()));
			}
		}
	}

	@SubscribeEvent
	public static void onEntityDeath(LivingDeathEvent event) {
		if (event.getSource().getTrueSource() instanceof EntityPlayerMP) {
			EntityPlayerMP player = (EntityPlayerMP) event.getSource().getTrueSource();
			if (WerewolfHelper.transformedWerewolf(player)) {
				TransformationHelper.addXP(player, 10, TransformationXPSentReason.WEREWOLF_HAS_KILLED);
			}
		}
	}

	@SubscribeEvent
	public static void onWerewolfAttack(LivingHurtEvent event) {
		// handle werewolf infection
		if (event.getSource().getTrueSource() instanceof EntityLivingBase) {
			EntityLivingBase attacker = (EntityLivingBase) event.getSource().getTrueSource();
			ITransformation transformationAttacker = TransformationHelper.getITransformation(attacker);
			EntityLivingBase attacked = event.getEntityLiving();

			if (transformationAttacker != null) {
				if (WerewolfHelper.transformedWerewolf(attacker)) {
					// now it is ensured that the attacker is a werewolf

					// if the werewolf can infect
					if (WerewolfHelper.canInfect(attacker)) {
						if (ChanceHelper.chanceOf(WerewolfHelper.getInfectionPercentage(attacker))) {
							// and the entity can be infected
							if (TransformationHelper.canChangeTransformation(attacked)
									&& TransformationHelper.canBeInfectedWith(Transformations.WEREWOLF, attacked)
									&& (!TransformationHelper.isInfected(attacked))) {
								// infect the entity
								WerewolfHelper.infectEntityAsWerewolf(attacked);
							}
						}
					}

					if (attacker instanceof EntityPlayer) {
						// fill hunger
						EntityPlayer player = (EntityPlayer) attacker;
						player.getFoodStats().addStats(1, 1);
					}
				}
			}
		}
	}

	/**
	 * Called in
	 * {@link TransformationEventHandler#onEntityTick(net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent)}
	 */
	public static void handleInfection(EntityLivingBase entity) {
		if (entity.hasCapability(CapabilitiesInit.CAPABILITY_INFECT_ON_NEXT_MOON, null)) {
			IInfectOnNextMoon ionm = WerewolfHelper.getIInfectOnNextMoon(entity);
			if (ionm.getInfectionTransformation() == Transformations.WEREWOLF) {
				if (!WerewolfHelper.isWerewolfTime(entity)) {
					if (ionm.getInfectionStatus() == InfectionStatus.MOON_ON_INFECTION) {
						ionm.setInfectionStatus(InfectionStatus.AFTER_INFECTION);
					}
				} else if (WerewolfHelper.isWerewolfTime(entity)) {
					if (ionm.getInfectionStatus() == InfectionStatus.AFTER_INFECTION) {
						ionm.setInfectionStatus(InfectionStatus.NOT_INFECTED);
						ionm.setInfectionTick(-1);
						ionm.setInfectionTransformation(Transformations.HUMAN);
						// change transformation
						TransformationHelper.changeTransformation(entity, Transformations.WEREWOLF);
					}
				}
			}
		}
	}
}
