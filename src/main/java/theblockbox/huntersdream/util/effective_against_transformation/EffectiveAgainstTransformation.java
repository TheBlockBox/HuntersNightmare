package theblockbox.huntersdream.util.effective_against_transformation;

import java.util.function.Predicate;
import java.util.stream.Stream;

import gnu.trove.map.hash.TObjectFloatHashMap;
import net.minecraft.client.resources.I18n;
import theblockbox.huntersdream.util.Reference;
import theblockbox.huntersdream.util.Transformation;
import theblockbox.huntersdream.util.helpers.TranslationHelper;

/**
 * Create a new instance of this class (preferably from the subclasses) to make
 * a specific item, entity etc. effective against a specific transformation
 * (currently only item and entity are supported)
 */
public abstract class EffectiveAgainstTransformation<T> implements IEffectiveAgainstTransformation<T> {
	/** A reserved value for the effectiveness that is not allowed to be used */
	public static final float WILDCARD_EFFECTIVENESS = Float.MIN_VALUE;
	public static final float DEFAULT_EFFECTIVENESS = 1.5F;
	public static final String TOOLTIP_KEY = Reference.MODID + ".effectiveAgainst.tooltip";
	private final Object[] tooltipFormatArgs;
	private final Predicate<T> isForObject;
	private final boolean effectiveAgainstUndead;

	private final Transformation[] effectiveAgainst;
	/**
	 * An array containing the effectiveness values of the effective
	 * transformations. The effectiveness for each transformation is at the index of
	 * the transformation's temporary id ({@link Transformation#getTemporaryID()})
	 * to hopefully make accessing the values faster
	 */
	private final float[] effectiveness;

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
	// TODO: Create own transformation map?
	protected EffectiveAgainstTransformation(Predicate<T> isForObject, boolean effectiveAgainstUndead,
			TObjectFloatHashMap<Transformation> values) {
		this.isForObject = isForObject;
		this.effectiveAgainstUndead = effectiveAgainstUndead;
		this.effectiveAgainst = values.keys(new Transformation[values.size()]);

		this.effectiveness = new float[Transformation.getTransformationLength()];
		for (int i = 0; i < this.effectiveAgainst.length; i++) {
			Transformation transformation = Transformation.fromTemporaryID(i);
			this.effectiveness[i] = values.containsKey(transformation) ? values.get(transformation)
					: WILDCARD_EFFECTIVENESS;
		}

		this.tooltipFormatArgs = effectiveAgainstUndead
				? Stream.concat(Stream.of(this.effectiveAgainst), Stream.of(Reference.MODID + ".undead")).toArray()
				: this.effectiveAgainst;
	}

	/** The damage multiplier when used against the specified creature */
	public float getEffectivenessAgainstTransformation(Transformation transformation) {
		return this.effectiveness[transformation.getTemporaryID()];
	}

	@Override
	public boolean isForObject(T t) {
		return this.isForObject.test(t);
	}

	/**
	 * Returns true when the object is effective against undead
	 */
	public boolean effectiveAgainstUndead() {
		return this.effectiveAgainstUndead;
	}

	@Override
	public boolean effectiveAgainst(Transformation transformation) {
		return this.effectiveness[transformation.getTemporaryID()] != WILDCARD_EFFECTIVENESS;
	}

	@Override
	public Transformation[] transformations() {
		return this.effectiveAgainst.clone();
	}

	@Override
	public String getTooltip() {
		return I18n.format(TOOLTIP_KEY, TranslationHelper.getAsTranslatedList(this.tooltipFormatArgs));
	}
}
