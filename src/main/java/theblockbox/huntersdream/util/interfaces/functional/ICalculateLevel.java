package theblockbox.huntersdream.util.interfaces.functional;

import net.minecraft.entity.player.EntityPlayerMP;

@FunctionalInterface
/**
 * This interface is for TransformationHelper$Transformations to get the
 * player's level
 */
public interface ICalculateLevel {
	public double getLevel(EntityPlayerMP player);
}
