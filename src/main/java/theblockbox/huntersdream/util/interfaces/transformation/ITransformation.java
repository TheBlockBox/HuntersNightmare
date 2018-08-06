package theblockbox.huntersdream.util.interfaces.transformation;

import net.minecraft.util.ResourceLocation;
import theblockbox.huntersdream.util.enums.Transformations;

/**
 * This interface is for entites that can transform
 */
public interface ITransformation {

	public boolean transformed();

	public void setTransformed(boolean transformed);

	public ResourceLocation getTransformationRL();

	default public Transformations getTransformation() {
		return Transformations.fromResourceLocation(getTransformationRL());
	}

	public void setTransformationRL(ResourceLocation resourceLocation);

	default public void setTransformation(Transformations transformation) {
		setTransformationRL(transformation.getResourceLocation());
	}

	public int getTextureIndex();
}
