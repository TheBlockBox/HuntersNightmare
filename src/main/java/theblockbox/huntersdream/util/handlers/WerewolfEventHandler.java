package theblockbox.huntersdream.util.handlers;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import theblockbox.huntersdream.util.ExecutionPath;
import theblockbox.huntersdream.util.enums.Transformations;
import theblockbox.huntersdream.util.helpers.ChanceHelper;
import theblockbox.huntersdream.util.helpers.TransformationHelper;
import theblockbox.huntersdream.util.helpers.TransformationHelper.TransformationXPSentReason;
import theblockbox.huntersdream.util.helpers.WerewolfHelper;
import theblockbox.huntersdream.util.interfaces.IArmorEffectiveAgainstWerewolf;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformation;

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
			if (transformationWerewolf.getTransformation() != Transformations.WEREWOLF
					|| !transformationWerewolf.transformed()) {
				return;
			} else {
				// now it is made sure that the attacked entity is a werewolf

				if (!event.getSource().damageType.equals("thorns")) {
					if (attacker != null) {
						ItemStack weapon = attacker.getHeldItemMainhand();
						// when the attacker is supernatural,
						if (transformationAttacker != null
								&& transformationAttacker.getTransformation().isSupernatural()) {
							if (transformationAttacker.getTransformation().isSupernatural()) {
								// has no weapon and is effective against werewolf
								if (weapon.isEmpty() && WerewolfHelper.effectiveAgainstWerewolf(attacker)) {
									event.setAmount(event.getAmount()
											* WerewolfHelper.getEffectivenessAgainstWerewolf(attacker));
									return;
								} else if (!weapon.isEmpty() && WerewolfHelper.effectiveAgainstWerewolf(weapon)) {
									event.setAmount(
											event.getAmount() * WerewolfHelper.getEffectivenessAgainstWerewolf(weapon));
									return;
								}
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
				event.setAmount((event.getAmount() / Transformations.WEREWOLF.PROTECTION));
			}
		}
	}

	@SubscribeEvent
	public static void onEntityDeath(LivingDeathEvent event) {
		if (event.getSource().getTrueSource() instanceof EntityPlayerMP) {
			EntityPlayerMP player = (EntityPlayerMP) event.getSource().getTrueSource();
			ITransformation transformation = TransformationHelper.getITransformation(player);
			if ((transformation.getTransformation() == Transformations.WEREWOLF) && transformation.transformed()) {
				TransformationHelper.addXP(player, 10, TransformationXPSentReason.WEREWOLF_HAS_KILLED,
						new ExecutionPath());
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
					// now it is ensured that the attacker is a werewolf

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

					// add thorns
					if (attacked != null) {
						ItemStack[] armor = { attacked.getItemStackFromSlot(EntityEquipmentSlot.HEAD),
								attacked.getItemStackFromSlot(EntityEquipmentSlot.CHEST),
								attacked.getItemStackFromSlot(EntityEquipmentSlot.LEGS),
								attacked.getItemStackFromSlot(EntityEquipmentSlot.FEET) };
						for (ItemStack item : armor) {
							if (WerewolfHelper.effectiveAgainstWerewolf(item)) {
								// Attack the werewolf back (thorns). The werewolf will get (the damage *
								// effectiveness) / 20 (protection)
								attacker.attackEntityFrom(DamageSource.causeThornsDamage(attacked),
										(WerewolfHelper.getEffectivenessAgainstWerewolf(item) * event.getAmount()));
								if (item.getItem() instanceof IArmorEffectiveAgainstWerewolf) {
									event.setAmount(event.getAmount()
											/ ((IArmorEffectiveAgainstWerewolf) item.getItem()).getProtection());
								} else {
									System.err.println(
											"Item is armor and is registered as effective against werewolf but doesn't implement IArmorEffectiveAgainstWerewolf."
													+ "Using default value 1.1F");
									event.setAmount(event.getAmount() / 1.1F);
								}
								// TODO: Make better value for armour damage
								item.damageItem(2, attacked);
							}
						}
					}
				}
			}
		}
	}
}
