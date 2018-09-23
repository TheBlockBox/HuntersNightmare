package theblockbox.huntersdream.util.interfaces.transformation;

import net.minecraft.entity.EntityLivingBase;
import theblockbox.huntersdream.init.TransformationInit;
import theblockbox.huntersdream.util.Transformation;
import theblockbox.huntersdream.util.helpers.TransformationHelper;

/**
 * This interface is for entites that can transform
 */
public interface ITransformation {

	public boolean transformed();

	public void setTransformed(boolean transformed);

	public Transformation getTransformation();

	public void setTransformation(Transformation transformation);

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

	/**
	 * If the entity can change transformation. It's recommended to prefer
	 * {@link TransformationHelper#canChangeTransformation(EntityLivingBase)} or
	 * {@link TransformationHelper#canChangeTransformationOnInfection(EntityLivingBase)}
	 * over this method. This method is NOT used to determine if an entity can
	 * transform, but if its transformation can change
	 * 
	 * @return Returns true if the transformation is changeable without accounting
	 *         for infection
	 */
	default public boolean isTransformationChangeable() {
		return this.getTransformation() != null && (this.getTransformation() == TransformationInit.HUMAN
				|| this.getTransformation() == TransformationInit.WEREWOLF);
	}
}
