package theblockbox.huntersdream.util.helpers;

import java.util.List;

import org.apache.commons.lang3.Validate;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import theblockbox.huntersdream.util.Reference;

/**
 * Util class for translations. All methods here are client side only!
 */
public class TranslationHelper {
	public static final char SPACE = ' ';
	public static final String KEY_COMMA = Reference.MODID + ".comma";
	public static final String KEY_AND = Reference.MODID + ".and";

	/**
	 * Tries to translate the objects and generates a list of them. Client side
	 * only!
	 * 
	 * @param toList The objects which should be made into a list
	 * @return Returns the given words as a list
	 */
	public static String getAsTranslatedList(final Object[] toList) {
		Validate.notEmpty(toList, "The given array is not allowed to be empty or null");
		StringBuilder toReturn = new StringBuilder();
		String comma = I18n.format(KEY_COMMA);
		String and = I18n.format(KEY_AND);

		for (int i = 0; i < toList.length; i++) {
			String translated = I18n.format(toList[i].toString());

			// special case where array length is 2
			if ((i == 0) && (i == toList.length - 2)) {
				toReturn.append(translated).append(SPACE).append(and).append(SPACE);
			} else if (i == 0) {
				toReturn.append(translated);
			} else if (i == (toList.length - 2)) {
				toReturn.append(comma).append(SPACE).append(translated).append(SPACE).append(and).append(SPACE);
			} else if (i == (toList.length - 1)) {
				toReturn.append(translated);
			} else {
				toReturn.append(comma).append(SPACE).append(translated);
			}
		}
		return toReturn.toString();
	}

	// argument is item because only items and itemblocks have tooltips
	public static void addEffectiveAgainstTransformationTooltips(ItemStack stack, List<String> tooltips) {
		if (EffectivenessHelper.effectiveAgainstSomeTransformation(stack)) {
			tooltips.add(EffectivenessHelper.getEAT(stack).getTooltip());
		}
		if (EffectivenessHelper.armorEffectiveAgainstSomeTransformation(stack)) {
			tooltips.add(EffectivenessHelper.getAEAT(stack).getTooltip());
		}
	}
}
