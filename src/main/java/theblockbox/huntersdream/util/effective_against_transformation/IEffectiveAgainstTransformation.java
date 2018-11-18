package theblockbox.huntersdream.util.effective_against_transformation;

import theblockbox.huntersdream.util.Transformation;

/**
 * An interface implemented by {@link EffectiveAgainstTransformation},
 * {@link ArmorEffectiveAgainstTransformation} and their subclasses
 */
public interface IEffectiveAgainstTransformation<T> {
	/**
	 * Returns true if this object should be used to determine the
	 * effectiveness/thorns and protection values of the given object
	 */
	public boolean isForObject(T object);

	/** Returns true if this object is effective against the given transformation */
	public boolean effectiveAgainst(Transformation transformation);

	/**
	 * Returns a cloned array of the transformations against which this object is
	 * effective
	 */
	public Transformation[] transformations();

	/** Returns the tooltip that should be shown on items */
	public String getTooltip();

	/**
	 * Registers the object and returns true if it was successful and false if it
	 * has already been registered
	 */
	public boolean register();
}
