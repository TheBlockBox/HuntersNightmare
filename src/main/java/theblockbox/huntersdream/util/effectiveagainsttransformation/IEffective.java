package theblockbox.huntersdream.util.effectiveagainsttransformation;

import theblockbox.huntersdream.util.enums.Transformations;
import theblockbox.huntersdream.util.exceptions.WrongTransformationException;

@FunctionalInterface
public interface IEffective {
	/**
	 * Should return the transformation against which the implementing class is
	 * effective
	 */
	public Transformations[] transformations();

	default public boolean effectiveAgainst(Transformations transformation) {
		for (Transformations t : transformations()) {
			if (t == transformation)
				return true;
		}
		return false;
	}

	/**
	 * Returns the index from the given transformation in the array obtained with
	 * the method {@link #transformations()}
	 * 
	 * @throws WrongTransformationException The exception gets thrown when the
	 *                                      object is not effective against the
	 *                                      given transformation
	 */
	default public int getTransformationArrayIndex(Transformations transformation) {
		Transformations[] transformations = transformations();
		for (int i = 0; i < transformations.length; i++)
			if (transformations[i] == transformation)
				return i;
		throw new WrongTransformationException("The object is not effective against the given transformation",
				transformation);
	}
}
