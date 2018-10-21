package theblockbox.huntersdream.util.helpers;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.Validate;

import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import theblockbox.huntersdream.util.Transformation;
import theblockbox.huntersdream.util.effective_against_transformation.ArmorEffectiveAgainstTransformation;
import theblockbox.huntersdream.util.effective_against_transformation.EffectiveAgainstTransformation;
import theblockbox.huntersdream.util.effective_against_transformation.EntityEffectiveAgainstTransformation;
import theblockbox.huntersdream.util.effective_against_transformation.IEffectiveAgainstTransformation;
import theblockbox.huntersdream.util.effective_against_transformation.ItemEffectiveAgainstTransformation;

public class EffectivenessHelper {
	public static final String THORNS_DAMAGE_NAME = "effectiveAgainstTransformationThorns";

	/** Shortcut for {@link EffectiveAgainstTransformation#getFromObject(Object)} */
	@SuppressWarnings("unchecked")
	public static <T> EffectiveAgainstTransformation<T> getEAT(@Nonnull T t) {
		if (t instanceof ItemStack) {
			return (EffectiveAgainstTransformation<T>) ItemEffectiveAgainstTransformation.getFromItem((ItemStack) t);
		} else if (t instanceof Entity) {
			return (EffectiveAgainstTransformation<T>) EntityEffectiveAgainstTransformation.getFromEntity((Entity) t);
		} else {
			throw new IllegalArgumentException(
					"The given object has to be an instance of ItemStack or Entity and not null");
		}
	}

	/**
	 * Shortcut for {@link ArmorEffectiveAgainstTransformation#getFromArmor(Item)}
	 */
	public static ArmorEffectiveAgainstTransformation getAEAT(@Nonnull ItemStack armorPart) {
		if (armorPart != null) {
			return ArmorEffectiveAgainstTransformation.getFromArmor(armorPart);
		} else {
			throw new NullPointerException("The given armor part is not allowed to be null");
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
	 * Returns true when the given armor part is effective against at least one
	 * transformation
	 */
	public static boolean armorEffectiveAgainstSomeTransformation(ItemStack armorPart) {
		return (getAEAT(armorPart) != null);
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
	 * Returns true when the object is either an instance of
	 * {@link IArmorEffectiveAgainstTransformation} or registered through
	 * {@link #addArmorEffectiveAgainst(Item, float, float, Transformation...)} This
	 * is dynamic, so one object you passed in twenty minutes ago could return false
	 * but when you do the same thing with the same object later, it could return
	 * true
	 */
	public static boolean armorEffectiveAgainstTransformation(Transformation effectiveAgainst, ItemStack armorPart) {
		Validate.notNull(effectiveAgainst, "The transformation isn't allowed to be null");
		return armorEffectiveAgainstSomeTransformation(armorPart)
				? getAEAT(armorPart).effectiveAgainst(effectiveAgainst)
				: false;
	}

	/** Returns the given armor's protection against the given transformation */
	public static float armorGetProtectionAgainst(Transformation against, ItemStack armorPart) {
		if (armorEffectiveAgainstTransformation(against, armorPart)) {
			return getAEAT(armorPart).getProtectionAgainstTransformation(against);
		} else {
			throw new IllegalArgumentException("Given armor is not effective against the given transformation");
		}
	}

	/**
	 * Effectiveness = thorns (more info here:
	 * {@link ArmorEffectiveAgainstTransformation#ArmorEffectiveAgainstTransformation(Item, float, float, Transformation...)})
	 */
	public static float armorGetEffectivenessAgainst(Transformation against, ItemStack armorPart) {
		Validate.notNull(against, "The transformation isn't allowed to be null");
		if (armorEffectiveAgainstTransformation(against, armorPart)) {
			return getAEAT(armorPart).getArmorEffectivenessAgainstTransformation(against);
		} else {
			throw new IllegalArgumentException("Given armor is not effective against the given transformation");
		}
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

	/** Returns true if the given entity is effective against undead */
	public static boolean effectiveAgainstUndead(Object object) {
		return effectiveAgainstSomeTransformation(object) ? getEAT(object).effectiveAgainstUndead() : false;
	}

	public static DamageSource causeEffectivenessThornsDamage(Entity source) {
		return (new EntityDamageSource(THORNS_DAMAGE_NAME, source)).setIsThornsDamage().setMagicDamage();
	}
}
