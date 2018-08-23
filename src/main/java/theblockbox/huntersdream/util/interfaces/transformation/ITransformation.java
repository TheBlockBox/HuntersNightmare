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

	default public boolean textureIndexInBounds() {
		return (this.getTextureIndex() >= 0)
				&& (this.getTextureIndex() < this.getTransformation().getTextures().length);
	}

	/**
	 * When the texture index is not in the bounds, it will be set, otherwise not
	 */
	default public void setTextureIndexWhenNeeded() {
		if (!textureIndexInBounds()) {
			this.setTextureIndex(this.getTransformation().getRandomTextureIndex());
		}
	}
}
