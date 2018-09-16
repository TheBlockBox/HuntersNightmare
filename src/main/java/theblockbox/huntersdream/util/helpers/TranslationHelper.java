package theblockbox.huntersdream.util.helpers;

import java.util.List;
import java.util.stream.Stream;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import theblockbox.huntersdream.util.Reference;
import theblockbox.huntersdream.util.effective_against_transformation.EffectiveAgainstTransformation;

/**
 * Util class for translations. All methods here are client side only!
 */
public class TranslationHelper {

	/**
	 * Tries to translate the objects and generates a list of them. Client side
	 * only!
	 * 
	 * @param toList The objects which should be made into a list
	 * @return Returns the given words as a list
	 */
	public static String getAsLocalizedList(final Object[] toList) {
		if (toList == null || toList.length == 0) {
			throw new NullPointerException("The array either has a length of 0 or is null itself");
		} else {
			String toReturn = "";
			final String comma = I18n.format(Reference.MODID + ".comma");
			for (int i = 0; i < toList.length; i++) {
				String translated = I18n.format(toList[i].toString());

				// special case where array length is 2
				if ((i == 0) && (i == toList.length - 2)) {
					toReturn += translated + " " + I18n.format(Reference.MODID + ".and") + " ";
				} else if (i == 0) {
					toReturn += translated;
				} else if (i == (toList.length - 2)) {
					toReturn += comma + " " + translated + " " + I18n.format(Reference.MODID + ".and") + " ";
				} else if (i == (toList.length - 1)) {
					toReturn += translated;
				} else {
					toReturn += comma + " " + translated;
				}
			}
			return toReturn;
		}
	}

	/**
	 * Does the same as {@link #localizeNumber(double, int)} but with preciseness 2.
	 * Client side only!
	 */
	public static String localizeNumber(double number) {
		return localizeNumber(number, 2);
	}

	/**
	 * Translates a number (like really, you have to translate numbers to German)
	 * with a given preciseness. Client side only!
	 * 
	 * @param number      The number to be translated and rounded
	 * @param preciseness To how many digits the number shouldn't be rounded
	 * @return Returns the translated and rounded number as a string
	 */
	public static String localizeNumber(double number, int preciseness) {
		double calcPreciseness = Math.pow(10, preciseness);
		return String.valueOf(Math.round((number * calcPreciseness)) / calcPreciseness).replace(".",
				I18n.format(Reference.MODID + ".decimalpoint"));
	}

	// argument is item because only items and itemblocks have tooltips
	public static void addEffectiveAgainstTransformationTooltips(ItemStack stack, List<String> tooltips) {
		if (EffectivenessHelper.effectiveAgainstSomeTransformation(stack)) {
			EffectiveAgainstTransformation<ItemStack> eat = EffectivenessHelper.getEAT(stack);
			if (eat.effectiveAgainstUndead()) {
				tooltips.add(I18n.format(Reference.MODID + ".effectiveAgainst.tooltip", getAsLocalizedList(Stream
						.concat(eat.transformations().stream(), Stream.of(Reference.MODID + ".undead")).toArray())));
			} else {
				tooltips.add(I18n.format(Reference.MODID + ".effectiveAgainst.tooltip",
						getAsLocalizedList(eat.transformations().toArray())));
			}
		}
		if (EffectivenessHelper.armorEffectiveAgainstSomeTransformation(stack)) {
			tooltips.add(I18n.format(Reference.MODID + ".armorEffectiveAgainst.tooltip",
					getAsLocalizedList(EffectivenessHelper.getAEAT(stack).transformations().toArray())));
		}
	}
}
