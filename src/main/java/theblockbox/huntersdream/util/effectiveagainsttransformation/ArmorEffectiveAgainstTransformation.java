package theblockbox.huntersdream.util.effectiveagainsttransformation;

import java.util.ArrayList;
import java.util.function.Predicate;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import theblockbox.huntersdream.util.compat.OreDictionaryCompat;
import theblockbox.huntersdream.util.enums.Transformations;
import theblockbox.huntersdream.util.helpers.GeneralHelper;

public class ArmorEffectiveAgainstTransformation implements IEffective {
	public static final ArrayList<ArmorEffectiveAgainstTransformation> ARMOR_PARTS = new ArrayList<>();
	private Predicate<ItemStack> isForArmor;
	public static final float DEFAULT_PROTECTION = 1.5F;
	public static final float DEFAULT_EFFECTIVENESS = 2;
	private Transformations[] effectiveAgainst;
	private float[] thorns;
	private float[] protection;

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
			@Nonnull Transformations[] effectiveAgainst, @Nonnull float[] thorns, @Nonnull float[] protection) {
		this.effectiveAgainst = effectiveAgainst;
		this.thorns = thorns;
		this.protection = protection;
		this.isForArmor = isForArmor;

		if (thorns.length != effectiveAgainst.length || protection.length != effectiveAgainst.length) {
			throw new IllegalArgumentException(
					"The array of thorns values and the array of protection values need to have the same length as the array of transformations");
		}

		if (effectiveAgainst.length < 1) {
			throw new IllegalArgumentException("The given arrays have to contain at least one object");
		}

		for (ArmorEffectiveAgainstTransformation aeat : ARMOR_PARTS)
			if (aeat.isForArmor == isForArmor)
				throw new IllegalArgumentException("Object already registered");
		ARMOR_PARTS.add(this);
	}

	public ArmorEffectiveAgainstTransformation(@Nonnull Predicate<ItemStack> isForArmor,
			@Nonnull Transformations effectiveAgainst, @Nonnull float thorns, @Nonnull float protection) {
		this(isForArmor, new Transformations[] { effectiveAgainst }, new float[] { thorns },
				new float[] { protection });
	}

	public boolean isForArmor(ItemStack armor) {
		return isForArmor.test(armor);
	}

	public float getArmorEffectivenessAgainstTransformation(Transformations transformation) {
		return this.thorns[this.getTransformationArrayIndex(transformation)];
	}

	public float getProtectionAgainstTransformation(Transformations transformation) {
		return this.protection[this.getTransformationArrayIndex(transformation)];
	}

	@Override
	public Transformations[] transformations() {
		return this.effectiveAgainst;
	}

	public static ArmorEffectiveAgainstTransformation getFromArmor(ItemStack armor) {
		for (ArmorEffectiveAgainstTransformation aeat : ARMOR_PARTS)
			if (aeat.isForArmor(armor))
				return aeat;
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
	 * @param thorns           An array of float arrays with a size of 4. Index 0
	 *                         are the thorns values for the helmet, 1 for the
	 *                         chestplate, 2 for the leggings and 3 for the boots.
	 *                         For more info look below in the "see" chapter
	 * @param protection       An array of float arrays with a size of 4. Index 0
	 *                         are the protection values for the helmet, 1 for the
	 *                         chestplate, 2 for the leggings and 3 for the boots.
	 *                         For more info look below in the "see" chapter
	 * @param effectiveAgainst An array of transformations against which every armor
	 *                         part should be effective
	 * @return Returns an array of an ArmorEffectiveAgainstTransformation array. The
	 *         array holds all the registered armor first index are all helmets, the
	 *         second one are all the chestplates, third leggings and fourth boots.
	 */
	public static ArmorEffectiveAgainstTransformation[][] registerArmorSet(String armorName, float[][] thorns,
			float[][] protection, Transformations... effectiveAgainst) {
		if (thorns.length != 4 || protection.length != 4) {
			throw new IllegalArgumentException("The given two float array arrays' size has to be 4");
		}
		ArmorEffectiveAgainstTransformation[][] toReturn = new ArmorEffectiveAgainstTransformation[4][];
		for (int i = 0; i < 4; i++) {
			final ItemStack[] stacks = OreDictionary.getOres(OreDictionaryCompat.ARMOR_PART_NAMES[i] + armorName)
					.toArray(new ItemStack[0]);
			toReturn[i] = new ArmorEffectiveAgainstTransformation[stacks.length];
			for (int j = 0; j < stacks.length; j++) {
				toReturn[i][j] = new ArmorEffectiveAgainstTransformation(
						GeneralHelper.getItemStackItemsEqual(stacks[j]), effectiveAgainst, thorns[i], protection[i]);
			}
		}
		return toReturn;
	}
}
