package theblockbox.huntersdream.util.helpers;

import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import theblockbox.huntersdream.util.Reference;

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
	public static String getAsTranslatedList(final Object[] toList) {
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
