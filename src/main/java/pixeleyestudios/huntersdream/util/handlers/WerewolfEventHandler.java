package pixeleyestudios.huntersdream.util.handlers;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import pixeleyestudios.huntersdream.event.TransformationXPEvent;
import pixeleyestudios.huntersdream.util.helpers.ChanceHelper;
import pixeleyestudios.huntersdream.util.helpers.TransformationHelper;
import pixeleyestudios.huntersdream.util.helpers.TransformationHelper.TransformationXPSentReason;
import pixeleyestudios.huntersdream.util.helpers.TransformationHelper.Transformations;
import pixeleyestudios.huntersdream.util.helpers.WerewolfHelper;
import pixeleyestudios.huntersdream.util.interfaces.ITransformation;

@Mod.EventBusSubscriber
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
			if (transformationWerewolf.getTransformation() != Transformations.WEREWOLF) {
				return;
			} else {
				// now it is made sure that the attacked entity is a werewolf

				if (transformationAttacker != null) {
					ItemStack weapon = attacker.getHeldItemMainhand();
					// when the attacker is supernatural,
					if (transformationAttacker.getTransformation().isSupernatural()) {
						// has no weapon and is effective against werewolf
						if (weapon.isEmpty() && WerewolfHelper.effectiveAgainstWerewolf(attacker)) {
							event.setAmount(
									event.getAmount() * WerewolfHelper.getEffectivenessAgainstWerewolf(attacker));
							return;
						} else if (!weapon.isEmpty() && WerewolfHelper.effectiveAgainstWerewolf(weapon)) {
							event.setAmount(event.getAmount() * WerewolfHelper.getEffectivenessAgainstWerewolf(weapon));
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
						} else if (!weapon.isEmpty() && WerewolfHelper.effectiveAgainstWerewolf(weapon)) {
							event.setAmount(event.getAmount() * WerewolfHelper.getEffectivenessAgainstWerewolf(weapon));
							return;
						} else if (WerewolfHelper.effectiveAgainstWerewolf(attacker)) {
							event.setAmount(
									event.getAmount() * WerewolfHelper.getEffectivenessAgainstWerewolf(attacker));
							return;
						}
					}
				}

				// when nothing applies (this should also reduce any other damage from other
				// damage sources)
				event.setAmount(event.getAmount() / 20);
			}
		}
	}

	@SubscribeEvent
	public static void onEntityDeath(LivingDeathEvent event) {
		if (event.getSource().getTrueSource() instanceof EntityPlayerMP) {
			EntityPlayerMP player = (EntityPlayerMP) event.getSource().getTrueSource();
			ITransformation transformation = TransformationHelper.getITransformation(player);
			if ((transformation.getTransformation() == Transformations.WEREWOLF) && transformation.transformed()) {
				TransformationHelper.addXP(player, 5, TransformationXPSentReason.WEREWOLF_HAS_KILLED);
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
				if ((transformationAttacker.getTransformation() == Transformations.WEREWOLF)
						&& transformationAttacker.transformed()) {
					// now it is ensured that attacker was a werewolf

					// if the werewolf can infect
					if (WerewolfHelper.canInfect(attacker)) {
						if (ChanceHelper.chanceOf(WerewolfHelper.getInfectionPercentage(attacker))) {
							// and the entity can be infected
							if (TransformationHelper.canChangeTransformation(attacked)) {
								// infect the entity
								WerewolfHelper.infect(event.getEntityLiving());
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

	@SubscribeEvent
	public static void onSth(TransformationXPEvent event) {
		System.out.println(
				"Before: " + event.XP_BEFORE + " After: " + event.getAmount() + " Reason: " + event.REASON.toString());
	}
}
