package theblockbox.huntersdream.util.handlers;

// using static import for CLIENT and SERVER enum constants
import static net.minecraftforge.fml.relauncher.Side.CLIENT;
import static net.minecraftforge.fml.relauncher.Side.SERVER;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import theblockbox.huntersdream.Main;
import theblockbox.huntersdream.api.Skill;
import theblockbox.huntersdream.api.Transformation;
import theblockbox.huntersdream.network.*;
import theblockbox.huntersdream.util.ExecutionPath;
import theblockbox.huntersdream.util.Reference;
import theblockbox.huntersdream.util.VampireFoodStats;
import theblockbox.huntersdream.util.exceptions.WrongSideException;
import theblockbox.huntersdream.util.helpers.GeneralHelper;
import theblockbox.huntersdream.util.helpers.TransformationHelper;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformationPlayer;

public class PacketHandler {
	private static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MODID);
	public static int networkID = 0;

	public static void register() {
		PacketHandler.registerMessage(TransformationMessage.Handler.class, TransformationMessage.class);
		PacketHandler.INSTANCE.registerMessage(TransformationTextureIndexMessage.Handler.class,
				TransformationTextureIndexMessage.class, PacketHandler.networkID++, SERVER);
		PacketHandler.registerMessage(BloodMessage.Handler.class, BloodMessage.class);
		PacketHandler.INSTANCE.registerMessage(SkillUnlockMessage.Handler.class, SkillUnlockMessage.class,
				PacketHandler.networkID++, SERVER);
		PacketHandler.INSTANCE.registerMessage(ActivateSkillMessage.Handler.class, ActivateSkillMessage.class,
				PacketHandler.networkID++, SERVER);
	}

	private static <REQ extends IMessage> void registerMessage(
			Class<? extends IMessageHandler<REQ, IMessage>> messageHandler, Class<REQ> requestMessageType) {
		PacketHandler.INSTANCE.registerMessage(messageHandler, requestMessageType, PacketHandler.networkID++, CLIENT);
	}

	public static void afterPacketSent(Side receivingSide, Side currentSide,
			MessageBase<? extends MessageBase<?>> message, Runnable send) {
		if (receivingSide == GeneralHelper.getOppositeSide(currentSide)) {
			send.run();
			if (ConfigHandler.common.showPacketMessages)
				Main.getLogger().info(
						message.getName() + " packet sent on side " + currentSide + "\nPath: " + ExecutionPath.get(1));
		} else {
			// Receiving side can't be sending side
			throw new WrongSideException(
					"Packet " + message.getName() + " couldn't be sent\nPath: " + ExecutionPath.get(1),
					GeneralHelper.getOppositeSide(receivingSide));
		}
	}

	public static void sendTransformationMessage(EntityPlayerMP applyOn) {
		ITransformationPlayer cap = TransformationHelper.getITransformationPlayer(applyOn);
		TransformationMessage message = new TransformationMessage(cap.getTransformation(), applyOn,
				cap.getTextureIndex(), cap.getSkills(), cap.getUnlockedPages(), cap.getTransformationData(),
				cap.getActiveSkill().orElse(null));
		PacketHandler.afterPacketSent(CLIENT, GeneralHelper.getSideFromEntity(applyOn), message,
				() -> PacketHandler.INSTANCE.sendToDimension(message, applyOn.world.provider.getDimension()));
	}

	// TODO: Look where this is used to send that the player has transformed
	public static void sendTransformationMessageToPlayer(EntityPlayerMP applyOn, EntityPlayerMP sendTo) {
		ITransformationPlayer cap = TransformationHelper.getITransformationPlayer(applyOn);
		TransformationMessage message = new TransformationMessage(cap.getTransformation(), applyOn,
				cap.getTextureIndex(), cap.getSkills(), cap.getUnlockedPages(), cap.getTransformationData(),
				cap.getActiveSkill().orElse(null));
		if (cap.getTransformation() == Transformation.VAMPIRE)
			applyOn.foodStats = VampireFoodStats.INSTANCE;
		PacketHandler.afterPacketSent(CLIENT, GeneralHelper.getSideFromEntity(applyOn), message,
				() -> PacketHandler.INSTANCE.sendTo(message, sendTo));
	}

	public static void sendTextureIndexMessage(World forSideCheck) {
		TransformationTextureIndexMessage message = new TransformationTextureIndexMessage(
				TransformationHelper.getITransformationPlayer(Main.proxy.getPlayer()).getTextureIndex());
		PacketHandler.afterPacketSent(SERVER, GeneralHelper.getSideFromWorld(forSideCheck), message,
				() -> PacketHandler.INSTANCE.sendToServer(message));
	}

	public static void sendSkillUnlockMessage(World forSideCheck, Skill skillToUnlock) {
		SkillUnlockMessage message = new SkillUnlockMessage(skillToUnlock);
		PacketHandler.afterPacketSent(SERVER, GeneralHelper.getSideFromWorld(forSideCheck), message,
				() -> PacketHandler.INSTANCE.sendToServer(message));
	}

	public static void sendActivateSkillMessage(World forSideCheck, Skill toActivate) {
		ActivateSkillMessage message = new ActivateSkillMessage(toActivate);
		PacketHandler.afterPacketSent(SERVER, GeneralHelper.getSideFromWorld(forSideCheck), message,
				() -> PacketHandler.INSTANCE.sendToServer(message));
	}

	public static void sendBloodMessage(EntityPlayerMP player) {
		BloodMessage message = new BloodMessage(player);
		PacketHandler.afterPacketSent(CLIENT, GeneralHelper.getSideFromEntity(player), message,
				() -> PacketHandler.INSTANCE.sendTo(message, player));
	}
}