package theblockbox.huntersdream.util.effective_against_transformation;

import java.util.BitSet;
import java.util.function.Predicate;

import theblockbox.huntersdream.util.Transformation;

/**
 * Create a new instance of this class (preferably from the subclasses) to make
 * a specific item, entity etc. effective against a specific transformation
 * (currently only item and entity are supported)
 */
public abstract class EffectiveAgainstTransformation<T> {
	private final Predicate<T> isForObject;
	private final boolean effectiveAgainstUndead;
	public static final float DEFAULT_EFFECTIVENESS = 1.5F;
	private final BitSet transformationsEffectiveAgainst = new BitSet(Transformation.getTransformationLength());
	private final Transformation[] effectiveAgainst;
	private final float[] effectiveness = new float[Transformation.getTransformationLength()];

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
	protected EffectiveAgainstTransformation(Predicate<T> isForObject, boolean effectiveAgainstUndead, TEArray values) {
		this.isForObject = isForObject;
		this.effectiveAgainstUndead = effectiveAgainstUndead;
		this.effectiveAgainst = values.transformations.clone();
		for (int i = 0; i < values.length; i++) {
			final int index = this.effectiveAgainst[i].getTemporaryID();
			this.transformationsEffectiveAgainst.set(index);
			this.effectiveness[index] = values.effectiveness[i];
		}
	}

	/** The damage multiplier when used against the specified creature */
	public float getEffectivenessAgainstTransformation(Transformation transformation) {
		return this.effectiveness[transformation.getTemporaryID()];
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

	public boolean effectiveAgainst(Transformation transformation) {
		return this.transformationsEffectiveAgainst.get(transformation.getTemporaryID());
	}

	public Transformation[] transformations() {
		return this.effectiveAgainst;
	}

	/**
	 * Object storing a transformation and float array with the same length. Used
	 * for creating new EffectiveAgainstTransformation instances (TE =
	 * Transformation Effectiveness)
	 */
	public static class TEArray implements Cloneable {
		private int currentIndex = 0;
		public final Transformation[] transformations;
		public final float[] effectiveness;
		public final int length;

		private TEArray(Transformation[] transformations, float[] effectiveness) {
			this.length = transformations.length;
			this.transformations = transformations;
			this.effectiveness = effectiveness;
		}

		public static TEArray of(int length) {
			return new TEArray(new Transformation[length], new float[length]);
		}

		public TEArray add(Transformation transformation, float effectiveness) {
			this.transformations[currentIndex] = transformation;
			this.effectiveness[currentIndex] = effectiveness;
			this.currentIndex++;
			return this;
		}

		@Override
		public TEArray clone() {
			return new TEArray(transformations.clone(), effectiveness.clone());
		}
	}
}
