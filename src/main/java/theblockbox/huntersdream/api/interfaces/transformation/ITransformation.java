package theblockbox.huntersdream.api.interfaces.transformation;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import theblockbox.huntersdream.api.Transformation;
import theblockbox.huntersdream.api.helpers.TransformationHelper;
import theblockbox.huntersdream.util.exceptions.WrongTransformationException;

/**
 * This interface is for entites that can transform
 */
public interface ITransformation {
    public Transformation getTransformation();

    public void setTransformation(Transformation transformation);

    public int getTextureIndex();

    public void setTextureIndex(int index);

    /**
     * Sets the texture index of this capability to the texture returned from
     * {@link Transformation#getTextureIndexForEntity(EntityLivingBase)}. The entity
     * should be the entity to which this capability belongs, otherwise the texture
     * index won't be chosen properly and method may even throw a
     * {@link WrongTransformationException}.
     */
    public default void setTextureIndex(EntityLivingBase entity) {
        this.setTextureIndex(this.getTransformation().getTextureIndexForEntity(entity));
    }

    public default boolean textureIndexInBounds() {
        int textureLength = this.getTransformation().getTextures().length;
        // if the texture length is 0, the texture index won't be used and we can return
        // true because nothing needs to be changed
        return ((textureLength == 0) || (this.getTextureIndex() >= 0 && this.getTextureIndex() < textureLength));
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

    /**
     * Sets the transformation data to the given NBTTagCompound
     */
    public void setTransformationData(NBTTagCompound transformationData);

    /**
     * If the entity can change transformation. It's recommended to prefer
     * {@link TransformationHelper#canChangeTransformation(EntityLivingBase)} or
     * {@link TransformationHelper#canChangeTransformationOnInfection(EntityLivingBase)}
     * over this method. This method is NOT used to determine if an entity can
     * transform, but if its transformation can change
     *
     * @return Returns true if the transformation is changeable without accounting
     * for infection
     */
    public default boolean isTransformationChangeable() {
        return this.getTransformation().isTransformation() && (this.getTransformation() == Transformation.HUMAN
                || this.getTransformation() == Transformation.WEREWOLF);
    }
}
