package theblockbox.huntersdream.util.helpers;

import javax.annotation.Nonnull;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import theblockbox.huntersdream.util.effective_against_transformation.ArmorEffectiveAgainstTransformation;
import theblockbox.huntersdream.util.effective_against_transformation.EffectiveAgainstTransformation;
import theblockbox.huntersdream.util.enums.Transformations;

public class EffectivenessHelper {

	/** Shortcut for {@link EffectiveAgainstTransformation#getFromObject(Object)} */
	public static EffectiveAgainstTransformation getEAT(@Nonnull Object object) {
		if (object != null) {
			return EffectiveAgainstTransformation.getFromObject(object);
		} else {
			throw new NullPointerException("The given object is not allowed to be null");
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
	 * {@link #addEffectiveAgainst(Object, float, Transformations...)}
	 */
	public static boolean effectiveAgainstTransformation(Transformations effectiveAgainst, Object object) {
		return effectiveAgainstSomeTransformation(object) ? getEAT(object).effectiveAgainst(effectiveAgainst) : false;
	}

	/**
	 * Returns true when the object is either an instance of
	 * {@link IArmorEffectiveAgainstTransformation} or registered through
	 * {@link #addArmorEffectiveAgainst(Item, float, float, Transformations...)}
	 */
	public static boolean armorEffectiveAgainstTransformation(Transformations effectiveAgainst, ItemStack armorPart) {
		return armorEffectiveAgainstSomeTransformation(armorPart)
				? getAEAT(armorPart).effectiveAgainst(effectiveAgainst)
				: false;
	}

	/** Returns the given armor's protection against the given transformation */
	public static float armorGetProtectionAgainst(Transformations against, ItemStack armorPart) {
		if (armorEffectiveAgainstTransformation(against, armorPart)) {
			return getAEAT(armorPart).getProtectionAgainstTransformation(against);
		} else {
			throw new IllegalArgumentException("Given armor is not effective against the given transformation");
		}
	}

	/**
	 * Effectiveness = thorns (more info here:
	 * {@link ArmorEffectiveAgainstTransformation#ArmorEffectiveAgainstTransformation(Item, float, float, Transformations...)})
	 */
	public static float armorGetEffectivenessAgainst(Transformations against, ItemStack armorPart) {
		if (armorEffectiveAgainstTransformation(against, armorPart)) {
			return getAEAT(armorPart).getArmorEffectivenessAgainstTransformation(against);
		} else {
			throw new IllegalArgumentException("Given armor is not effective against the given transformation");
		}
	}

	/**
	 * (For armor parts, see
	 * {@link #armorGetEffectivenessAgainst(Transformations, Item)})
	 */
	public static float getEffectivenessAgainst(Transformations effectiveAgainst, Object object) {
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
}
