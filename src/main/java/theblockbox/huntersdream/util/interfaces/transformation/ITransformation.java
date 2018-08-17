package theblockbox.huntersdream.util.interfaces.transformation;

import theblockbox.huntersdream.util.enums.Transformations;

/**
 * This interface is for entites that can transform
 */
public interface ITransformation {

	public boolean transformed();

	public void setTransformed(boolean transformed);

	public Transformations getTransformation();

	public void setTransformation(Transformations transformation);

	public int getTextureIndex();

	public void setTextureIndex(int index);
}
