package theblockbox.huntersdream.util.interfaces.transformation;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import theblockbox.huntersdream.util.Transformation;
import theblockbox.huntersdream.util.helpers.TransformationHelper;

/**
 * This interface is for entites that can transform
 */
public interface ITransformation {
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
	 * Returns an NBTTagCompound of bytes with transformation specific data (made so
	 * that there doesn't have to be a new capability for each transformation). When
	 * the entity changes the transformation the NBTTagCompound always has the key
	 * "transformation" with the value of the string returned by
	 * {@link Transformation#toString()} where the transformation is the entity's
	 * new transformation. After changing the NBTTagCompound set it again with
	 * {@link #setTransformationData(NBTTagCompound)} so that entities depending on
	 * EntityDataManager to handle the server client communication won't have any
	 * problems
	 */
	public NBTTagCompound getTransformationData();

	/** Sets the transformation data to the given NBTTagCompound */
	public void setTransformationData(NBTTagCompound transformationData);

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
		return this.getTransformation().isTransformation() && (this.getTransformation() == Transformation.HUMAN
				|| this.getTransformation() == Transformation.WEREWOLF);
	}
}
