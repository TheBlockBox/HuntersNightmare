package theblockbox.huntersdream.util.effective_against_transformation;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import javax.annotation.Nonnull;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import theblockbox.huntersdream.util.Reference;
import theblockbox.huntersdream.util.Transformation;
import theblockbox.huntersdream.util.collection.TransformationToFloatFloatMap;
import theblockbox.huntersdream.util.compat.OreDictionaryCompat;
import theblockbox.huntersdream.util.helpers.GeneralHelper;
import theblockbox.huntersdream.util.helpers.TranslationHelper;

public class ArmorEffectiveAgainstTransformation implements IEffectiveAgainstTransformation<ItemStack> {
	private static final Set<ArmorEffectiveAgainstTransformation> ARMOR_PARTS = new HashSet<>();
	private final Predicate<ItemStack> isForArmor;
	public static final float DEFAULT_PROTECTION = 1.5F;
	public static final float DEFAULT_EFFECTIVENESS = 2;
	public static final String TOOLTIP_KEY = Reference.MODID + ".armorEffectiveAgainst.tooltip";
	private final Transformation[] effectiveAgainst;
	private final TransformationToFloatFloatMap valueMap;

	/**
	 * Creates a new ArmorEffectiveAgainstTransformation object from the given
	 * arguments (Caution! You have to call {@link #register()} to register it,
	 * otherwise it won't work!)
	 * 
	 * @param isForArmor A Predicate that returns true if this
	 *                   ArmorEffectiveAgainstTransformation object should be used
	 *                   to get the protection and thorns values for the passed
	 *                   ItemStack.
	 * @param valueMap   A TransformationToFloatFloatMap that is used to get the
	 *                   protection and thorns values against specific
	 *                   transformations. The first floats are the thorns values and
	 *                   the second ones are the protection values.
	 */
	public ArmorEffectiveAgainstTransformation(@Nonnull Predicate<ItemStack> isForArmor,
			TransformationToFloatFloatMap valueMap) {
		this.isForArmor = isForArmor;
		this.valueMap = valueMap.clone();
		this.effectiveAgainst = this.valueMap.toArray();
	}

	public static ArmorEffectiveAgainstTransformation getFromArmor(ItemStack armor) {
		for (ArmorEffectiveAgainstTransformation aeat : ARMOR_PARTS) {
			if (aeat.isForObject(armor)) {
				return aeat;
			}
		}
		return null;
	}

	public static Set<ArmorEffectiveAgainstTransformation> getArmorParts() {
		return Collections.unmodifiableSet(ARMOR_PARTS);
	}

	@Override
	public boolean register() {
		return ARMOR_PARTS.add(this);
	}

	@Override
	public boolean isForObject(ItemStack armor) {
		return this.isForArmor.test(armor);
	}

	/** Returns the thorns values against the given transformation */
	public float getArmorEffectivenessAgainstTransformation(Transformation transformation) {
		return this.valueMap.getFloat1(transformation);
	}

	/** Returns the protection values against the given transformation */
	public float getProtectionAgainstTransformation(Transformation transformation) {
		return this.valueMap.getFloat2(transformation);
	}

	@Override
	public boolean effectiveAgainst(Transformation transformation) {
		return this.valueMap.hasKey(transformation);
	}

	@Override
	public Transformation[] transformations() {
		return this.effectiveAgainst.clone();
	}

	/**
	 * Registers a new ArmorEffectiveAgainstTransformation object for each armor
	 * part with the defined value maps and a predicate matching all armor parts
	 * that are registered in the ore dict with the given armorName.
	 * 
	 * @param armorName  The ore dict name that the items have to match so that the
	 *                   created ArmorEffectiveAgainstTransformation objects apply.
	 *                   Should be the material with the first letter uppercased
	 *                   (for example for silver it's "Silver").
	 * @param helmet     The map that should be used to register the
	 *                   ArmorEffectiveAgainstTransformation object of the helmet.
	 * @param chestplate The map that should be used to register the
	 *                   ArmorEffectiveAgainstTransformation object of the
	 *                   chestplate.
	 * @param leggings   The map that should be used to register the
	 *                   ArmorEffectiveAgainstTransformation object of the leggings.
	 * @param boots      The map that should be used to register the
	 *                   ArmorEffectiveAgainstTransformation object of the boots.
	 * @return Returns the registered objects in an array with a size of four (index
	 *         0 is helmet, 1 is chestplate, 2 is leggings and 3 is boots).
	 */
	public static ArmorEffectiveAgainstTransformation[] registerArmorSet(String armorName,
			TransformationToFloatFloatMap helmet, TransformationToFloatFloatMap chestplate,
			TransformationToFloatFloatMap leggings, TransformationToFloatFloatMap boots) {
		ArmorEffectiveAgainstTransformation[] toReturn = new ArmorEffectiveAgainstTransformation[4];

		TransformationToFloatFloatMap[] values = { helmet, chestplate, leggings, boots };
		for (int i = 0; i < values.length; i++) {
			toReturn[i] = new ArmorEffectiveAgainstTransformation(
					GeneralHelper.getPredicateMatchesOreDict(OreDictionaryCompat.ARMOR_PART_NAMES[i] + armorName),
					values[i]);
			toReturn[i].register();
		}
		return toReturn;
	}

	@Override
	public String getTooltip() {
		return I18n.format(TOOLTIP_KEY, TranslationHelper.getAsTranslatedList(this.effectiveAgainst));
	}
}
