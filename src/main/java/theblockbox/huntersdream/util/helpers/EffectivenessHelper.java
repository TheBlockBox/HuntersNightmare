package theblockbox.huntersdream.util.helpers;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.Validate;

import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import theblockbox.huntersdream.util.Transformation;
import theblockbox.huntersdream.util.effective_against_transformation.EffectiveAgainstTransformation;
import theblockbox.huntersdream.util.effective_against_transformation.EntityEffectiveAgainstTransformation;
import theblockbox.huntersdream.util.effective_against_transformation.IEffectiveAgainstTransformation;
import theblockbox.huntersdream.util.effective_against_transformation.ItemEffectiveAgainstTransformation;

public class EffectivenessHelper {
	public static final String THORNS_DAMAGE_NAME = "huntersdream:effectiveAgainstTransformationThorns";

	/** Shortcut for {@link EffectiveAgainstTransformation#getFromObject(Object)} */
	@SuppressWarnings("unchecked")
	public static <T> EffectiveAgainstTransformation<T> getEAT(@Nonnull T t) {
		if (t instanceof ItemStack) {
			return (EffectiveAgainstTransformation<T>) ItemEffectiveAgainstTransformation
					.getFromItemStack((ItemStack) t);
		} else if (t instanceof Entity) {
			return (EffectiveAgainstTransformation<T>) EntityEffectiveAgainstTransformation.getFromEntity((Entity) t);
		} else {
			throw new IllegalArgumentException(
					"The given object has to be an instance of ItemStack or Entity and not null");
		}
	}

	/**
	 * Returns true when the given object is effective against at least one
	 * transformation
	 */
	public static boolean effectiveAgainstSomeTransformation(Object object) {
		return (getEAT(object) != null);
	}

	/**
	 * Returns true when the object is either an instance of
	 * {@link IEffectiveAgainstTransformation} or registered through
	 * {@link #addEffectiveAgainst(Object, float, Transformation...)} This is
	 * dynamic, so one object you passed in twenty minutes ago could return false
	 * but when you do the same thing with the same object later, it could return
	 * true
	 */
	public static boolean effectiveAgainstTransformation(Transformation effectiveAgainst, Object object) {
		Validate.notNull(effectiveAgainst, "The transformation isn't allowed to be null");
		return effectiveAgainstSomeTransformation(object) ? getEAT(object).effectiveAgainst(effectiveAgainst) : false;
	}

	/**
	 * (For armor parts, see
	 * {@link #armorGetEffectivenessAgainst(Transformation, Item)})
	 */
	public static float getEffectivenessAgainst(Transformation effectiveAgainst, Object object) {
		Validate.notNull(effectiveAgainst, "The transformation isn't allowed to be null");
		if (effectiveAgainstTransformation(effectiveAgainst, object)) {
			return getEAT(object).getEffectivenessAgainstTransformation(effectiveAgainst);
		} else {
			throw new IllegalArgumentException("The given object is not effective against the given transformation ("
					+ effectiveAgainst.toString() + ")");
		}
	}

	public static DamageSource causeEffectivenessThornsDamage(Entity source) {
		return (new EntityDamageSource(THORNS_DAMAGE_NAME, source)).setIsThornsDamage().setMagicDamage();
	}
}
