package theblockbox.huntersdream.util.helpers;

import javax.annotation.Nonnull;

import net.minecraft.item.Item;
import theblockbox.huntersdream.util.enums.Transformations;
import theblockbox.huntersdream.util.interfaces.effective.ArmorEffectiveAgainstTransformation;
import theblockbox.huntersdream.util.interfaces.effective.EffectiveAgainstTransformation;

public class EffectivenessHelper {

	/** Shortcut for {@link EffectiveAgainstTransformation#getFromObject(Object)} */
	public static <T> EffectiveAgainstTransformation<T> getEAT(@Nonnull T object) {
		if (object != null) {
			GeneralHelper.notItemstack(object);
			return EffectiveAgainstTransformation.getFromObject(object);
		} else {
			throw new NullPointerException("The given object is not allowed to be null");
		}
	}

	/**
	 * Shortcut for {@link ArmorEffectiveAgainstTransformation#getFromArmor(Item)}
	 */
	public static ArmorEffectiveAgainstTransformation getAEAT(@Nonnull Item armorPart) {
		if (armorPart != null) {
			GeneralHelper.notItemstack(armorPart);
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
	public static boolean armorEffectiveAgainstSomeTransformation(Item armorPart) {
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
	public static boolean armorEffectiveAgainstTransformation(Transformations effectiveAgainst, Item armorPart) {
		return armorEffectiveAgainstSomeTransformation(armorPart)
				? getAEAT(armorPart).effectiveAgainst(effectiveAgainst)
				: false;
	}

	/** Returns the given armor's protection against the given transformation */
	public static float armorGetProtectionAgainst(Transformations against, Item armorPart) {
		if (armorEffectiveAgainstTransformation(against, armorPart)) {
			return getAEAT(armorPart).getProtection();
		} else {
			throw new IllegalArgumentException("Given armor is not effective against the given transformation");
		}
	}

	/**
	 * Effectiveness = thorns (more info here:
	 * {@link ArmorEffectiveAgainstTransformation#ArmorEffectiveAgainstTransformation(Item, float, float, Transformations...)})
	 */
	public static float armorGetEffectivenessAgainst(Transformations against, Item armorPart) {
		if (armorEffectiveAgainstTransformation(against, armorPart)) {
			return getAEAT(armorPart).getArmorEffectiveness();
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
			return getEAT(object).getEffectiveness();
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
