package theblockbox.huntersdream.util.interfaces.effective;

import theblockbox.huntersdream.util.enums.Transformations;

/**
 * This interface is specifically for silver items, because they all have deal
 * more damage on specific transformations
 */
public interface ISilverEffectiveAgainstTransformation extends IEffectiveAgainstTransformation {
	public static final Transformations[] EFFECTIVE_AGAINST = { Transformations.WEREWOLF };

	@Override
	default Transformations[] transformations() {
		return EFFECTIVE_AGAINST;
	}
}
