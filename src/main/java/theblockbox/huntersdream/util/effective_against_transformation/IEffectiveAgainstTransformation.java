package theblockbox.huntersdream.util.effective_against_transformation;

import theblockbox.huntersdream.util.Transformation;

public interface IEffectiveAgainstTransformation<T> {
	public boolean isForObject(T object);

	public boolean effectiveAgainst(Transformation transformation);

	public Transformation[] transformations();

	public String getTooltip();
}
