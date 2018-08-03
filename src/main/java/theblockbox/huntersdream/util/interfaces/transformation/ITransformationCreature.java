package theblockbox.huntersdream.util.interfaces.transformation;

import theblockbox.huntersdream.util.enums.Transformations;

/**
 * This interface is for creatures that can transform (for the capability)
 */
public interface ITransformationCreature extends ITransformation {
	public Transformations[] getTransformationsNotImmuneTo();

	default public int[] getTransformationIntsNotImmuneTo() {
		int[] transformations = new int[getTransformationsNotImmuneTo().length];
		for (int i = 0; i < getTransformationsNotImmuneTo().length; i++) {
			transformations[i] = getTransformationsNotImmuneTo()[i].getID();
		}
		return transformations;
	}

	default public void setTransformationsNotImmuneTo(Transformations... transformationsNotImmuneTo) {
		int[] transformations = new int[transformationsNotImmuneTo.length];
		for (int i = 0; i < transformationsNotImmuneTo.length; i++) {
			transformations[i] = transformationsNotImmuneTo[i].getID();
		}
		setTransformationsNotImmuneTo(transformations);
	}

	default public void setTransformationsNotImmuneTo(int... transformations) {
		throw new UnsupportedOperationException("Can't set transformations not immune to");
	}

	@Override
	default boolean transformed() {
		return false;
	}

	@Override
	default void setTransformed(boolean transformed) {
		throw new UnsupportedOperationException("Entity is always not transformed");
	}

	default void setTextureIndex(int index) {
		throw new UnsupportedOperationException("Can't set texture index");
	}

	@Override
	default void setTransformationID(int id) {
		throw new UnsupportedOperationException("Transformation is always HUMAN");
	}

	/** This method is made to prevent interfering with setTransformation */
	public int getCurrentTransformationID();

	public void setCurrentTransformationID(int transformation);

	default public Transformations getCurrentTransformation() {
		return Transformations.fromID(getCurrentTransformationID());
	}

	default public void setCurrentTransformation(Transformations transformation) {
		setCurrentTransformationID(transformation.getID());
	}

	/**
	 * @deprecated The entity is ALWAYS a human. To get the transformation after the
	 *             mob transformed, use {@link #getCurrentTransformationID()}
	 */
	@Override
	@Deprecated
	default int getTransformationInt() {
		throw new UnsupportedOperationException("Use #getCurrentTransformation");
		// return 0;
	}

	/**
	 * @deprecated The entity is ALWAYS a human. To get the transformation after the
	 *             mob transformed, use {@link #getCurrentTransformation()}
	 */
	@Override
	@Deprecated
	default Transformations getTransformation() {
		throw new UnsupportedOperationException("Use #getCurrentTransformation");
		// return ITransformation.super.getTransformation();
	}
}
