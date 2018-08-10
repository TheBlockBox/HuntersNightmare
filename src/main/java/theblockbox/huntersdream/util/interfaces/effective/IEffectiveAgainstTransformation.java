package theblockbox.huntersdream.util.interfaces.effective;

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
}
