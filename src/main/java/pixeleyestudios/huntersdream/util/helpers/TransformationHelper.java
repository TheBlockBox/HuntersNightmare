package pixeleyestudios.huntersdream.util.helpers;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import pixeleyestudios.huntersdream.capabilities.transformation.player.TransformationPlayerProvider;
import pixeleyestudios.huntersdream.util.interfaces.ITransformation;
import pixeleyestudios.huntersdream.util.interfaces.ITransformationPlayer;

public interface TransformationHelper {
	public enum Transformations {
		HUMAN(0), WEREWOLF(1), VAMPIRE(2);
		// when an entity has no transformation, use null
		// (no transformation meaning not infectable)

		public final int ID;

		private Transformations(int id) {
			this.ID = id;
		}

		public static Transformations fromID(int id) {
			for (Transformations transformations : values()) {
				if (transformations.ID == id) {
					return transformations;
				}
			}
			return null;
		}
	}

	/**
	 * Returns the transformation capability of the given player (just a short-cut
	 * method)
	 */
	public static ITransformationPlayer getCap(EntityPlayer player) {
		return player.getCapability(TransformationPlayerProvider.TRANSFORMATION_PLAYER_CAPABILITY, null);
	}

	/**
	 * Changes the player's transformation also resets xp and transformed and sends
	 * the data to the client (this method is only server side!)
	 */
	public static void changeTransformation(EntityPlayerMP player, Transformations transformation) {
		ITransformationPlayer cap = TransformationHelper.getCap(player);
		cap.setXP(0); // reset xp
		cap.setTransformed(false); // reset transformed
		cap.setTransformation(transformation);
		PacketHelper.syncPlayerTransformationData(player); // sync data with client
	}

	public static void changeTransformationWhenPossible(EntityPlayerMP player, Transformations transformation) {
		if (canChangeTransformation(player)) {
			changeTransformation(player, transformation);
		}
	}

	/**
	 * Returns true when the given entity can change transformation without rituals
	 * (e.g. by werewolf infection)
	 */
	public static boolean canChangeTransformation(EntityLivingBase entity) {
		return (getTransformation(entity) == Transformations.HUMAN);
	}

	// TODO: Add handling of transformation change
	public static void changeTransformation(EntityLivingBase entity, Transformations transformation) {

	}

	public static Transformations getTransformation(EntityLivingBase entity) {
		Transformations transformation = null;

		if (entity instanceof EntityPlayer) {
			transformation = getCap((EntityPlayer) entity).getTransformation();
		} else if (entity instanceof ITransformation) {
			transformation = ((ITransformation) entity).getTransformation();
		}
		return transformation;
	}

	public static ITransformation getITransformation(EntityLivingBase entity) {
		if (entity instanceof EntityPlayer) {
			return getCap((EntityPlayer) entity);
		} else if (entity instanceof ITransformation) {
			return (ITransformation) entity;
		}

		return null;
	}
}
