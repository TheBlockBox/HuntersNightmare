package theblockbox.huntersdream.util.effective_against_transformation;

import java.util.function.Predicate;
import java.util.stream.Stream;

import net.minecraft.client.resources.I18n;
import theblockbox.huntersdream.util.Reference;
import theblockbox.huntersdream.util.Transformation;
import theblockbox.huntersdream.util.collection.TransformationToFloatMap;
import theblockbox.huntersdream.util.helpers.TranslationHelper;

/**
 * Create a new instance of this class (preferably from the subclasses) to make
 * a specific item, entity etc. effective against a specific transformation
 * (currently only item and entity are supported)
 */
public abstract class EffectiveAgainstTransformation<T> implements IEffectiveAgainstTransformation<T> {
	public static final float DEFAULT_EFFECTIVENESS = 1.5F;
	public static final String TOOLTIP_KEY = Reference.MODID + ".effectiveAgainst.tooltip";
	private final Object[] tooltipFormatArgs;
	private final Predicate<T> isForObject;
	private final boolean effectiveAgainstUndead;
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
	protected EffectiveAgainstTransformation(Predicate<T> isForObject, boolean effectiveAgainstUndead,
			TransformationToFloatMap effectivenessMap) {
		this.isForObject = isForObject;
		this.effectiveAgainstUndead = effectiveAgainstUndead;
		this.effectivenessMap = effectivenessMap.clone();
		this.tooltipFormatArgs = effectiveAgainstUndead
				? Stream.concat(this.effectivenessMap.transformationStream(), Stream.of(Reference.MODID + ".undead"))
						.toArray()
				: this.transformations();
	}

	/** The damage multiplier when used against the specified creature */
	public float getEffectivenessAgainstTransformation(Transformation transformation) {
		return this.effectivenessMap.get(transformation);
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
		return this.effectivenessMap.hasKey(transformation);
	}

	@Override
	public Transformation[] transformations() {
		return this.effectivenessMap.toArray();
	}

	@Override
	public String getTooltip() {
		return I18n.format(TOOLTIP_KEY, TranslationHelper.getAsTranslatedList(this.tooltipFormatArgs));
	}
}
