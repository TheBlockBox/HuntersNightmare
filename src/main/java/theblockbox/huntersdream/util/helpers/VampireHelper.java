package theblockbox.huntersdream.util.helpers;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.capabilities.Capability;
import theblockbox.huntersdream.event.TransformationXPEvent.TransformationXPSentReason;
import theblockbox.huntersdream.init.CapabilitiesInit;
import theblockbox.huntersdream.init.TransformationInit;
import theblockbox.huntersdream.util.enums.Rituals;
import theblockbox.huntersdream.util.exceptions.WrongTransformationException;
import theblockbox.huntersdream.util.handlers.PacketHandler;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformationPlayer;
import theblockbox.huntersdream.util.interfaces.transformation.IVampire;

public class VampireHelper {
	public static final Capability<IVampire> CAPABILITY_VAMPIRE = CapabilitiesInit.CAPABILITY_VAMPIRE;

	public static void drinkBlood(EntityLivingBase vampire, EntityLivingBase drinkFrom) {
		checkIsVampire(vampire);

		if (drinkFrom.isEntityUndead()) {
			vampire.addPotionEffect(new PotionEffect(MobEffects.HUNGER, 60, 2));
		}
		if (vampire instanceof EntityPlayerMP) {
			EntityPlayerMP player = (EntityPlayerMP) vampire;
			TransformationHelper.addXP(player, 2, TransformationXPSentReason.VAMPIRE_DRANK_BLOOD);
			IVampire vamp = getIVampire(player);
			if (vamp.getBlood() < 20) {
				vamp.incrementBlood();
			}
			PacketHandler.sendBloodMessage(player);
		}
		drinkFrom.attackEntityFrom(new DamageSource("vampireDrankBlood"), 1F);
	}

	public static IVampire getIVampire(EntityPlayer player) {
		return player.getCapability(CAPABILITY_VAMPIRE, null);
	}

	public static boolean isVampire(EntityLivingBase entity) {
		return TransformationHelper.getTransformation(entity) == TransformationInit.VAMPIRE;
	}

	public static float calculateProtection(EntityLivingBase vampire) {
		checkIsVampire(vampire);
		return 1F;
	}

	// TODO: Make better values
	public static float calculateDamage(EntityLivingBase vampire) {
		checkIsVampire(vampire);
		if (vampire.getHeldItemMainhand().isEmpty()) {
			return 10F;
		} else {
			return 1F;
		}
	}

	/**
	 * Throws a {@link WrongTransformationException} if the given entity is not a
	 * vampire
	 */
	public static void checkIsVampire(EntityLivingBase toCheck) {
		if (toCheck == null) {
			throw new NullPointerException("The given entity is not allowed to be null");
		} else if (!isVampire(toCheck)) {
			throw new WrongTransformationException("The given entity (" + toCheck.toString() + ") has to be a vampire",
					TransformationHelper.getTransformation(toCheck));
		}
	}

	public static double calculateLevel(EntityPlayerMP vampire) {
		checkIsVampire(vampire);
		ITransformationPlayer cap = TransformationHelper.getCap(vampire);
		double level = cap.getXP() / 500D;
		// maximum level
		if (level >= 13) {
			level = 12.99999D;
		}
		if (level >= 12 && !cap.hasRitual(Rituals.VAMPIRE_FIRST_RITUAL)) {
			level = 11.99999D;
		}
		if (level >= 6 && !cap.hasRitual(Rituals.VAMPIRE_SECOND_RITUAL)) {
			level = 5.99999D;
		}

		return level;
	}
}
