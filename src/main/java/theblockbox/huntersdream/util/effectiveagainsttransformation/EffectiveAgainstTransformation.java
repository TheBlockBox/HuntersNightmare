package theblockbox.huntersdream.util.effectiveagainsttransformation;

import java.util.ArrayList;
import java.util.function.Predicate;

import javax.annotation.Nonnull;

import theblockbox.huntersdream.util.enums.Transformations;

/**
 * Create a new instance of this class (preferably from the subclasses) to make
 * a specific item, entity etc. effective against a specific transformation
 * (currently only item and entity are supported)
 */
public class EffectiveAgainstTransformation implements IEffective {
	public static final ArrayList<EffectiveAgainstTransformation> OBJECTS = new ArrayList<>();
	private Transformations[] effectiveAgainst;
	private Predicate<Object> isForObject;
	private float[] effectiveness;
	private boolean effectiveAgainstUndead;
	public static final float DEFAULT_EFFECTIVENESS = 1.5F;

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
	public EffectiveAgainstTransformation(Predicate<Object> isForObject, boolean effectiveAgainstUndead,
			@Nonnull Transformations[] effectiveAgainst, @Nonnull float[] effectiveness) {
		this.isForObject = isForObject;
		this.effectiveness = effectiveness;
		this.effectiveAgainst = effectiveAgainst;
		this.effectiveAgainstUndead = effectiveAgainstUndead;

		if (effectiveAgainst.length != effectiveness.length) {
			throw new IllegalArgumentException(
					"The array of effectiveness values needs to have the same length as the array of transformations");
		}

		if (effectiveAgainst.length < 1) {
			throw new IllegalArgumentException("The given arrays have to contain at least one object");
		}

		for (EffectiveAgainstTransformation eat : OBJECTS)
			if (eat.isForObject == isForObject)
				throw new IllegalArgumentException("Object already registered");

		OBJECTS.add(this);
	}

	@Override
	public Transformations[] transformations() {
		return this.effectiveAgainst;
	}

	/** The damage multiplier when used against the specified creature */
	public float getEffectivenessAgainstTransformation(Transformations transformation) {
		return this.effectiveness[this.getTransformationArrayIndex(transformation)];
	}

	public static EffectiveAgainstTransformation getFromObject(Object object) {
		for (EffectiveAgainstTransformation eat : OBJECTS)
			if (eat.isForObject(object))
				return eat;
		return null;
	}

	/**
	 * Returns all instances of this class and subclasses
	 */
	public static ArrayList<EffectiveAgainstTransformation> getObjects() {
		return OBJECTS;
	}

	public boolean isForObject(Object object) {
		return this.isForObject.test(object);
	}

	/**
	 * Returns true when the object is effective against undead
	 */
	public boolean effectiveAgainstUndead() {
		return this.effectiveAgainstUndead;
	}
}
