package theblockbox.huntersdream.util.interfaces.transformation;

import theblockbox.huntersdream.util.enums.Transformations;

/**
 * This interface is for creatures that can transform (for the capability)
 */
public interface ITransformationCreature extends ITransformation {
	public Transformations[] getTransformationsNotImmuneTo();

	default public void setTransformationsNotImmuneTo(Transformations... transformationsNotImmuneTo) {
		throw new UnsupportedOperationException("Can't set transformations not immune to");
	}

	default public boolean notImmuneToTransformation(Transformations transformation) {
		for (Transformations t : getTransformationsNotImmuneTo())
			if (t == transformation)
				return true;
		return false;
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
}
