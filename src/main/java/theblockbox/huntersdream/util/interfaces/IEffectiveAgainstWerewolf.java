package theblockbox.huntersdream.util.interfaces;

/**
 * Implement this interface to entities or items that should be effective
 * against werewolves (= deal more damage)
 */
public interface IEffectiveAgainstWerewolf {
	public static final float DEFAULT_EFFECTIVENESS = 2;

	/** The damage multiplier when used against a werewolf */
	default public float getEffectiveness() {
		return DEFAULT_EFFECTIVENESS;
	}
}
