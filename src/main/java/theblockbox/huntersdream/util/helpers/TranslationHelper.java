package theblockbox.huntersdream.util.helpers;

import net.minecraft.client.resources.I18n;

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
	public static String getAsList(Object[] toList) {
		if (toList == null || toList.length == 0) {
			throw new NullPointerException("The array either has a length of 0 or is null itself");
		} else {
			String toReturn = "";
			for (int i = 0; i < toList.length; i++) {
				String translated = I18n.format(toList[i].toString());
				// if last index
				if (i == (toList.length - 1)) {
					toReturn += translated;
				} else if (i == (toList.length - 2)) {
					toReturn += ", " + translated + I18n.format("huntersdream.and") + " ";
				} else if (i == 0) {
					toReturn += translated;
				} else {
					toReturn += ", " + translated;
				}
			}
			return toReturn;
		}
	}

	/**
	 * Does the same as {@link #translateNumber(double, int)} but with preciseness
	 * 2. Client side only!
	 */
	public static String translateNumber(double number) {
		return translateNumber(number, 2);
	}

	/**
	 * Translates a number (like really, you have to translate numbers to German)
	 * with a given preciseness. Client side only!
	 * 
	 * @param number      The number to be translated and rounded
	 * @param preciseness To how many digits the number shouldn't be rounded
	 * @return Returns the translated and rounded number as a string
	 */
	public static String translateNumber(double number, int preciseness) {
		double calcPreciseness = Math.pow(10, preciseness);
		return String.valueOf(Math.round((number * calcPreciseness)) / calcPreciseness).replace(".",
				I18n.format("huntersdream.decimalpoint"));
	}
}
