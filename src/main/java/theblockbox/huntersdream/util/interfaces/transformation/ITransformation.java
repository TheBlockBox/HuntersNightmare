package theblockbox.huntersdream.util.interfaces.transformation;

import theblockbox.huntersdream.util.enums.Transformations;

/**
 * This interface is for entites that can transform
 */
public interface ITransformation {

	public boolean transformed();

	public void setTransformed(boolean transformed);

	public int getTransformationInt();

	default public Transformations getTransformation() {
		return Transformations.fromID(getTransformationInt());
	}

	public void setTransformationID(int id);

	default public void setTransformation(Transformations transformation) {
		setTransformationID(transformation.ID);
	}

	public int getTextureIndex();
}
