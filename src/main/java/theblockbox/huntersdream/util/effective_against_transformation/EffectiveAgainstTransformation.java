package theblockbox.huntersdream.util.effective_against_transformation;

import java.util.function.Predicate;

import theblockbox.huntersdream.util.Transformation;
import theblockbox.huntersdream.util.collection.TransformationToFloatMap;

/**
 * Create a new instance of this class (preferably from the subclasses) to make
 * a specific item, entity etc. effective against a specific transformation
 * (currently only item and entity are supported)
 */
public abstract class EffectiveAgainstTransformation<T> implements IEffectiveAgainstTransformation<T> {
	public static final float DEFAULT_EFFECTIVENESS = 1.5F;
	private final Predicate<T> isForObject;
	private final TransformationToFloatMap effectivenessMap;

	/**
	 * Creates a new EffectiveAgainstTransformation object from the given arguments
	 * (Caution! You have to call {@link #register()} to register it, otherwise it
	 * won't work!)
	 * 
	 * @param isForObject            A Predicate that returns true if this
	 *                               EffectiveAgainstTransformation object should be
	 *                               used to get the effectiveness values for the
	 *                               passed object of type T
	 * @param effectiveAgainstUndead If the objects that match this
	 *                               EffectiveAgainstTransformation object should
	 *                               deal as much damage as Smite I against undead.
	 * @param effectivenessMap       A TransformationToFloatMap that is used to get
	 *                               the effectiveness values against specific
	 *                               transformations.
	 */
	protected EffectiveAgainstTransformation(Predicate<T> isForObject, TransformationToFloatMap effectivenessMap) {
		this.isForObject = isForObject;
		this.effectivenessMap = effectivenessMap.clone();
	}

	/** The damage multiplier when used against the specified creature */
	public float getEffectivenessAgainstTransformation(Transformation transformation) {
		return this.effectivenessMap.get(transformation);
	}

	@Override
	public boolean isForObject(T t) {
		return this.isForObject.test(t);
	}

	@Override
	public boolean effectiveAgainst(Transformation transformation) {
		return this.effectivenessMap.hasKey(transformation);
	}

	@Override
	public Transformation[] transformations() {
		return this.effectivenessMap.toArray();
	}
}
