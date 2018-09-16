package theblockbox.huntersdream.util.effective_against_transformation;

import java.util.EnumMap;
import java.util.Set;
import java.util.function.Predicate;

import theblockbox.huntersdream.util.enums.Transformations;

/**
 * Create a new instance of this class (preferably from the subclasses) to make
 * a specific item, entity etc. effective against a specific transformation
 * (currently only item and entity are supported)
 */
public abstract class EffectiveAgainstTransformation<T> {
	private final Predicate<T> isForObject;
	private final boolean effectiveAgainstUndead;
	public static final float DEFAULT_EFFECTIVENESS = 1.5F;
	private final EnumMap<Transformations, Float> effectivenessMap;

	/**
	 * Creates a new EffectiveAgainstTransformation object from the given arguments
	 * 
	 * @param isForObject            A predicate that is used to determine if an
	 *                               object should have this object's properties
	 *                               (like extra damage against werewolves)
	 * @param effectiveAgainstUndead If this should be effective against undead
	 *                               (does as much damage as Smite I)
	 * @param effectiveAgainst       An array of transformations against which this
	 *                               is effective
	 * @param effectiveness          An array of floats with the effectiveness
	 *                               values (has to have the same length as the
	 *                               effectiveAgainst array. When the effectiveness
	 *                               against a transformation is searched, the one
	 *                               at the same index as the transformation in the
	 *                               effectiveAgainst array will be used)
	 */
	protected EffectiveAgainstTransformation(Predicate<T> isForObject, boolean effectiveAgainstUndead,
			EnumMap<Transformations, Float> effectivenessMap) {
		this.isForObject = isForObject;
		this.effectiveAgainstUndead = effectiveAgainstUndead;
		this.effectivenessMap = effectivenessMap;

		if (effectivenessMap.isEmpty()) {
			throw new IllegalArgumentException("The given map has to contain at least one object");
		}
	}

	/** The damage multiplier when used against the specified creature */
	public float getEffectivenessAgainstTransformation(Transformations transformation) {
		return this.effectivenessMap.get(transformation);
	}

	public boolean isForObject(T t) {
		return this.isForObject.test(t);
	}

	/**
	 * Returns true when the object is effective against undead
	 */
	public boolean effectiveAgainstUndead() {
		return this.effectiveAgainstUndead;
	}

	public boolean effectiveAgainst(Transformations transformation) {
		return this.effectivenessMap.containsKey(transformation);
	}

	public Set<Transformations> transformations() {
		return this.effectivenessMap.keySet();
	}
}
