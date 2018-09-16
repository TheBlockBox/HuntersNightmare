package theblockbox.huntersdream.util.effective_against_transformation;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import theblockbox.huntersdream.util.compat.OreDictionaryCompat;
import theblockbox.huntersdream.util.enums.Transformations;
import theblockbox.huntersdream.util.helpers.GeneralHelper;

public class ArmorEffectiveAgainstTransformation {
	public static final Set<ArmorEffectiveAgainstTransformation> ARMOR_PARTS = new HashSet<>();
	private final Predicate<ItemStack> isForArmor;
	public static final float DEFAULT_PROTECTION = 1.5F;
	public static final float DEFAULT_EFFECTIVENESS = 2;
	/**
	 * EnumMap holding the thorns and protection values against a specific
	 * transformation. First index in the array are the thorns values, second are
	 * the protection values
	 */
	private final EnumMap<Transformations, float[]> effectivenessMap;

	/**
	 * Creates a new ArmorEffectiveAgainstTransformation object from the given
	 * arguments
	 * 
	 * @param isForArmor       A predicate that is used to determine if the given
	 *                         armor part is for the transformation
	 * @param effectiveAgainst An array of transformations against which this is
	 *                         effective
	 * @param thorns           An array of floats that represent the thorns values
	 *                         (the array has to have the same length as the
	 *                         effectiveAgainst array. If a thorns value against a
	 *                         transformation is searched, the thorns value at the
	 *                         same index as the transformation will be used)
	 * @param protection       An array of floats that represent the protection
	 *                         values (the array has to have the same length as the
	 *                         effectiveAgainst array. If a protection value against
	 *                         a transformation is searched, the protection value at
	 *                         the same index as the transformation will be used)
	 */
	public ArmorEffectiveAgainstTransformation(@Nonnull Predicate<ItemStack> isForArmor,
			EnumMap<Transformations, float[]> effectivenessMap) {
		this.isForArmor = isForArmor;
		this.effectivenessMap = effectivenessMap;

		if (effectivenessMap.isEmpty()) {
			throw new IllegalArgumentException("The given map has to contain at least one object");
		}

		for (ArmorEffectiveAgainstTransformation aeat : ARMOR_PARTS)
			if (aeat.isForArmor == isForArmor)
				throw new IllegalArgumentException("Object already registered");
		ARMOR_PARTS.add(this);
	}

	public ArmorEffectiveAgainstTransformation(@Nonnull Predicate<ItemStack> isForArmor,
			@Nonnull Transformations effectiveAgainst, @Nonnull float thorns, @Nonnull float protection) {
		this(isForArmor, newEnumMap(effectiveAgainst, thorns, protection));
	}

	private static EnumMap<Transformations, float[]> newEnumMap(@Nonnull Transformations effectiveAgainst, float thorns,
			float protection) {
		EnumMap<Transformations, float[]> map = new EnumMap<>(Transformations.class);
		map.put(effectiveAgainst, new float[] { thorns, protection });
		return map;
	}

	public boolean isForArmor(ItemStack armor) {
		return isForArmor.test(armor);
	}

	public float getArmorEffectivenessAgainstTransformation(Transformations transformation) {
		return this.effectivenessMap.get(transformation)[0];
	}

	public float getProtectionAgainstTransformation(Transformations transformation) {
		return this.effectivenessMap.get(transformation)[1];
	}

	public boolean effectiveAgainst(Transformations transformation) {
		return this.effectivenessMap.containsKey(transformation);
	}

	public Set<Transformations> transformations() {
		return this.effectivenessMap.keySet();
	}

	public static ArmorEffectiveAgainstTransformation getFromArmor(ItemStack armor) {
		for (ArmorEffectiveAgainstTransformation aeat : ARMOR_PARTS) {
			if (aeat.isForArmor(armor)) {
				return aeat;
			}
		}
		return null;
	}

	/**
	 * Creates an ArmorEffectiveAgainstTransformation object for every armor part of
	 * the given material that is registered in the oredictionary
	 * 
	 * @param armorName        The name of the armor's material to be search in the
	 *                         ore dict. For example if you use "Silver", the method
	 *                         will search for "helmetSilver", "chestplateSilver",
	 *                         "leggingsSilver" and "bootsSilver" in the ore dict
	 * @param effectiveness    A three dimensional array of floats. The first
	 *                         dimension needs to have a length of four (one for
	 *                         every armor part). The second dimensions length has
	 *                         to be the same as the length of the effectiveAgainst.
	 *                         The last array has to have a size of 2. Index 0 is
	 *                         the thorns value, the second one the protection value
	 * @param effectiveAgainst An array of transformations against which every armor
	 *                         part should be effective
	 * @return Returns an array of ArmorEffectiveAgainstTransformation objects with
	 *         a length of four. Index 0 is the one for the helmets, 1 for the
	 *         chestplates, 2 for the leggings and 3 for the boots
	 */
	public static ArmorEffectiveAgainstTransformation[] registerArmorSet(String armorName, float[][][] effectiveness,
			Transformations... effectiveAgainst) {
		if (effectiveness.length != 4) {
			throw new IllegalArgumentException(
					"The size of the given three dimensional float array's first dimension has to be 4");
		}
		ArmorEffectiveAgainstTransformation[] toReturn = new ArmorEffectiveAgainstTransformation[4];
		for (int i = 0; i < 4; i++) {
			EnumMap<Transformations, float[]> effectivenessMap = new EnumMap<>(Transformations.class);
			for (int j = 0; j < effectiveAgainst.length; j++) {
				effectivenessMap.put(effectiveAgainst[j], effectiveness[i][j]);
			}
			toReturn[i] = new ArmorEffectiveAgainstTransformation(
					GeneralHelper.getPredicateMatchesOreDict(OreDictionaryCompat.ARMOR_PART_NAMES[i] + armorName),
					effectivenessMap);
		}
		return toReturn;
	}
}
