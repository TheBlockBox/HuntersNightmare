package theblockbox.huntersdream.util.interfaces.transformation;

import net.minecraft.util.ResourceLocation;
import theblockbox.huntersdream.util.enums.Transformations;

/**
 * This interface is for creatures that can transform (for the capability)
 */
public interface ITransformationCreature extends ITransformation {
	public Transformations[] getTransformationsNotImmuneTo();

	default public ResourceLocation[] getTransformationRLsNotImmuneTo() {
		ResourceLocation[] transformations = new ResourceLocation[getTransformationsNotImmuneTo().length];
		for (int i = 0; i < getTransformationsNotImmuneTo().length; i++) {
			transformations[i] = getTransformationsNotImmuneTo()[i].getResourceLocation();
		}
		return transformations;
	}

	default public void setTransformationsNotImmuneTo(Transformations... transformationsNotImmuneTo) {
		ResourceLocation[] transformations = new ResourceLocation[transformationsNotImmuneTo.length];
		for (int i = 0; i < transformationsNotImmuneTo.length; i++) {
			transformations[i] = transformationsNotImmuneTo[i].getResourceLocation();
		}
		setTransformationsNotImmuneTo(transformations);
	}

	default public void setTransformationsNotImmuneTo(ResourceLocation... transformations) {
		throw new UnsupportedOperationException("Can't set transformations not immune to");
	}

	default public boolean notImmuneToTransformation(Transformations transformation) {
		return this.notImmuneToTransformationRL(transformation.getResourceLocation());
	}

	default public boolean notImmuneToTransformationRL(ResourceLocation resourceLocation) {
		for (Transformations transformation : getTransformationsNotImmuneTo()) {
			if (transformation.toString().equals(resourceLocation.toString())) {
				return true;
			}
		}

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
