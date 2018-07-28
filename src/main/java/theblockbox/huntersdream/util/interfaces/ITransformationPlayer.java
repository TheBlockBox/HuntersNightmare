package theblockbox.huntersdream.util.interfaces;

import net.minecraft.entity.player.EntityPlayerMP;
import theblockbox.huntersdream.util.helpers.TransformationHelper;;

/**
 * ITransform for players (players can have xp)
 */
public interface ITransformationPlayer extends ITransformation {
	public int getXP();

	/**
	 * Use {@link TransformationHelper#setXP(EntityPlayerMP, int)} for automatic
	 * packets and level up messages
	 */
	public void setXP(int xp);

	// getTextureIndex() already defined in ITransformation

	public void setTextureIndex(int textureIndex);
}
