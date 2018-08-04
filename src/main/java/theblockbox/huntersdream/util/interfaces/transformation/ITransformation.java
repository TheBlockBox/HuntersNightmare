package theblockbox.huntersdream.util.interfaces.transformation;

import theblockbox.huntersdream.util.enums.Transformations;

/**
 * This interface is for entites that can transform
 */
public interface ITransformation {

	public boolean transformed();

	public void setTransformed(boolean transformed);

	public int getTransformationID();

	default public Transformations getTransformation() {
		return Transformations.fromID(getTransformationID());
	}

	public void setTransformationID(int id);

	default public void setTransformation(Transformations transformation) {
		setTransformationID(transformation.getID());
	}

	public int getTextureIndex();
}
