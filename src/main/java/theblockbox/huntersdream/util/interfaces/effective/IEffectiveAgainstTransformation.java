package theblockbox.huntersdream.util.interfaces.effective;

import net.minecraft.client.resources.I18n;
import theblockbox.huntersdream.util.helpers.TranslationHelper;

/**
 * Implement this interface to entities or items that should be effective
 * against werewolves (= deal more damage)
 */
public interface IEffectiveAgainstTransformation extends IEffective {
	public static final float DEFAULT_EFFECTIVENESS = 1.5F;

	/** The damage multiplier when used against the specified creature */
	default public float getEffectiveness() {
		return DEFAULT_EFFECTIVENESS;
	}

	/** Gets the string for the tooltip. Client side only! */
	default public String getTooltipEffectiveness() {
		return I18n.format("huntersdream.effectiveAgainst.tooltip", TranslationHelper.getAsList(transformations()),
				TranslationHelper.translateNumber(getEffectiveness()));
	}
}
