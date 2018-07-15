package pixeleyestudios.huntersdream.util.interfaces;

import net.minecraft.entity.player.EntityPlayer;

@FunctionalInterface
/**
 * This interface is for TransformationHelper$Transformations to get the
 * player's level
 */
public interface ICalculateLevel {
	public double getLevel(EntityPlayer player);
}
