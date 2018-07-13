package pixeleyestudios.huntersdream.util.helpers;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import pixeleyestudios.huntersdream.network.TransformationMessage;
import pixeleyestudios.huntersdream.network.TransformationTextureIndexMessage;
import pixeleyestudios.huntersdream.util.handlers.HuntersDreamPacketHandler;
import pixeleyestudios.huntersdream.util.interfaces.ITransformationPlayer;

public class PacketHelper {

	public static void syncPlayerTransformationData(EntityPlayer player) {
		ITransformationPlayer cap = TransformationHelper.getCap(player);
		if (!player.world.isRemote) {
			HuntersDreamPacketHandler.INSTANCE.sendToAll(new TransformationMessage(cap.getXP(), cap.transformed(),
					cap.getTransformationInt(), player.getEntityId(), cap.getTextureIndex()));
			packetSentMessage(player, "Transformation");
		}
	}

	public static void syncTransformationTextureIndex(EntityPlayer player) {
		if (player.world.isRemote) {
			HuntersDreamPacketHandler.INSTANCE.sendToServer(
					new TransformationTextureIndexMessage(TransformationHelper.getCap(player).getTextureIndex()));
			packetSentMessage(player, "Transformation Texture Index");
		}
	}

	private static void packetSentMessage(EntityLivingBase entity, String packetName) {
		System.out.println(
				packetName + " packet sent on side " + (entity.world.isRemote ? Side.CLIENT : Side.SERVER).toString());
	}
}
