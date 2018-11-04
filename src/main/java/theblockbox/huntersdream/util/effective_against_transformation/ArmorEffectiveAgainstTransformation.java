package theblockbox.huntersdream.util.effective_against_transformation;

import java.util.BitSet;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.Validate;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import theblockbox.huntersdream.util.Reference;
import theblockbox.huntersdream.util.Transformation;
import theblockbox.huntersdream.util.compat.OreDictionaryCompat;
import theblockbox.huntersdream.util.helpers.GeneralHelper;
import theblockbox.huntersdream.util.helpers.TranslationHelper;

public class ArmorEffectiveAgainstTransformation implements IEffectiveAgainstTransformation<ItemStack> {
	public static final Set<ArmorEffectiveAgainstTransformation> ARMOR_PARTS = new HashSet<>();
	private final Predicate<ItemStack> isForArmor;
	public static final float DEFAULT_PROTECTION = 1.5F;
	public static final float DEFAULT_EFFECTIVENESS = 2;
	public static final String TOOLTIP_KEY = Reference.MODID + ".armorEffectiveAgainst.tooltip";
	private final BitSet transformationsEffectiveAgainst = new BitSet(Transformation.getTransformationLength());
	private final float[] protection = new float[Transformation.getTransformationLength()];
	private final float[] thorns = new float[Transformation.getTransformationLength()];
	private final Transformation[] effectiveAgainst;

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
	public ArmorEffectiveAgainstTransformation(@Nonnull Predicate<ItemStack> isForArmor, TTPArray values) {
		this.isForArmor = isForArmor;
		this.effectiveAgainst = values.transformations.clone();
		for (int i = 0; i < values.length; i++) {
			final int index = this.effectiveAgainst[i].getTemporaryID();
			this.transformationsEffectiveAgainst.set(index);
			this.thorns[index] = values.thorns[i];
			this.protection[index] = values.protection[i];
		}

		for (ArmorEffectiveAgainstTransformation aeat : ARMOR_PARTS)
			if (aeat.isForArmor == isForArmor)
				throw new IllegalArgumentException("Object already registered");
		ARMOR_PARTS.add(this);
	}

	@Override
	public boolean isForObject(ItemStack armor) {
		return isForArmor.test(armor);
	}

	public float getArmorEffectivenessAgainstTransformation(Transformation transformation) {
		return this.thorns[transformation.getTemporaryID()];
	}

	public float getProtectionAgainstTransformation(Transformation transformation) {
		return this.protection[transformation.getTemporaryID()];
	}

	@Override
	public boolean effectiveAgainst(Transformation transformation) {
		return this.transformationsEffectiveAgainst.get(transformation.getTemporaryID());
	}

	@Override
	public Transformation[] transformations() {
		return this.effectiveAgainst.clone();
	}

	public static ArmorEffectiveAgainstTransformation getFromArmor(ItemStack armor) {
		for (ArmorEffectiveAgainstTransformation aeat : ARMOR_PARTS) {
			if (aeat.isForObject(armor)) {
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
	 * @param effectiveness    A two dimensional array of FloatPairs. The first
	 *                         dimension needs to have a length of four (one for
	 *                         every armor part). The second dimension's length has
	 *                         to be the same as the length of the effectiveAgainst
	 *                         array. In the FloatPair, the first value is the
	 *                         thorns one, the second is the protection one
	 * @param effectiveAgainst An array of transformations against which every armor
	 *                         part should be effective
	 * @return Returns an array of ArmorEffectiveAgainstTransformation objects with
	 *         a length of four. Index 0 is the one for the helmets, 1 for the
	 *         chestplates, 2 for the leggings and 3 for the boots
	 */
	public static ArmorEffectiveAgainstTransformation[] registerArmorSet(String armorName, TTPArray helmet,
			TTPArray chestplate, TTPArray leggings, TTPArray boots) {
		if (helmet.length != chestplate.length || chestplate.length != leggings.length
				|| leggings.length != boots.length) {
			throw new IllegalArgumentException(
					"The length of the helmet, chestplte, leggings and boots TransformationThornsProtectionArrays has to be the same");
		}

		ArmorEffectiveAgainstTransformation[] toReturn = new ArmorEffectiveAgainstTransformation[4];
		TTPArray[] values = new TTPArray[] { helmet, chestplate, leggings, boots };
		for (int i = 0; i < values.length; i++)
			toReturn[i] = new ArmorEffectiveAgainstTransformation(
					GeneralHelper.getPredicateMatchesOreDict(OreDictionaryCompat.ARMOR_PART_NAMES[i] + armorName),
					values[i]);
		return toReturn;
	}

	@Override
	public String getTooltip() {
		return I18n.format(TOOLTIP_KEY, TranslationHelper.getAsTranslatedList(this.effectiveAgainst));
	}

	/**
	 * An object representing a transformation and two float arrays. Used for
	 * creating a new ArmorEffectiveAgainstTransformation instance. (TTP =
	 * Transformation Thorns Protection)
	 */
	public static class TTPArray implements Cloneable {
		private int currentIndex = 0;
		public final Transformation[] transformations;
		public final float[] thorns;
		public final float[] protection;
		public final int length;

		private TTPArray(Transformation[] transformations, float[] thorns, float[] protection) {
			this.length = transformations.length;
			this.transformations = transformations;
			this.thorns = thorns;
			this.protection = protection;
		}

		public static TTPArray of(int length) {
			return new TTPArray(new Transformation[length], new float[length], new float[length]);
		}

		public TTPArray add(Transformation transformation, float t, float p) {
			Validate.notNull(transformation, "Transformation is not allowed to be null");
			this.transformations[currentIndex] = transformation;
			this.thorns[currentIndex] = t;
			this.protection[currentIndex] = p;
			this.currentIndex++;
			return this;
		}

		@Override
		public TTPArray clone() {
			return new TTPArray(transformations.clone(), thorns.clone(), protection.clone());
		}
	}
}
