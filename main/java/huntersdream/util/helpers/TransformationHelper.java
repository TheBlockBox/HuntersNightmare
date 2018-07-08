package huntersdream.util.helpers;

import huntersdream.capabilities.transformation.player.TransformationPlayerProvider;
import huntersdream.network.TransformationMessage;
import huntersdream.util.handlers.HuntersDreamPacketHandler;
import huntersdream.util.interfaces.ITransformationPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public interface TransformationHelper {
	public enum Transformations {
		HUMAN(0), WEREWOLF(1);

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

	public static void syncPlayerTransformationData(EntityPlayer player) {
		ITransformationPlayer cap = getCap(player);
		if (!player.world.isRemote) {
			HuntersDreamPacketHandler.INSTANCE.sendToAll(new TransformationMessage(cap.getXP(), cap.transformed(),
					cap.getTransformationInt(), player.getEntityId(), cap.getTextureIndex()));
			System.out.println("Transformation packet sent from server");
		} else {
			throw new IllegalArgumentException("Packet sent from client!");
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
		TransformationHelper.syncPlayerTransformationData(player); // sync data with client
	}
}
