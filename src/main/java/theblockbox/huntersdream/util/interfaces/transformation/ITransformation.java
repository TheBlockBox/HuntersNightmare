package theblockbox.huntersdream.util.interfaces.transformation;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import theblockbox.huntersdream.api.Transformation;
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
	 * When the texture index is not in the bounds, it will be set, otherwise not.
	 * The world's random is used to decide which texture index should be used.
	 */
	default public void setTextureIndexWhenNeeded(World worldIn) {
		if (!textureIndexInBounds()) {
			this.setTextureIndex(this.getTransformation().getRandomTextureIndex(worldIn));
		}
	}

	/**
	 * Returns an NBTTagCompound with transformation specific data (made so that
	 * there doesn't have to be a new capability for each transformation). When the
	 * entity changes the transformation the NBTTagCompound always has the key
	 * "transformation" with the value of the string returned by
	 * {@link Transformation#toString()} where the transformation is the entity's
	 * new transformation. Prefer to call
	 * {@link TransformationHelper#getTransformationData(EntityLivingBase)} instead
	 * of this method to avoid crashes when loading old worlds
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
