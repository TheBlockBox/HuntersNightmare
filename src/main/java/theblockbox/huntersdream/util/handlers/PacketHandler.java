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
import theblockbox.huntersdream.init.TransformationInit;
import theblockbox.huntersdream.network.BloodMessage;
import theblockbox.huntersdream.network.MessageBase;
import theblockbox.huntersdream.network.TransformationMessage;
import theblockbox.huntersdream.network.TransformationTextureIndexMessage;
import theblockbox.huntersdream.network.TransformationXPMessage;
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
		registerMessage(TransformationMessage.Handler.class, TransformationMessage.class);
		registerMessage(TransformationXPMessage.Handler.class, TransformationXPMessage.class);
		INSTANCE.registerMessage(TransformationTextureIndexMessage.Handler.class,
				TransformationTextureIndexMessage.class, networkID++, Side.SERVER);
		registerMessage(BloodMessage.Handler.class, BloodMessage.class);
	}

	private static <REQ extends IMessage> void registerMessage(
			Class<? extends IMessageHandler<REQ, IMessage>> messageHandler, Class<REQ> requestMessageType) {
		INSTANCE.registerMessage(messageHandler, requestMessageType, networkID++, Side.CLIENT);
	}

	public static void afterPacketSent(Side receivingSide, Side currentSide,
			MessageBase<? extends MessageBase<?>> message, Runnable send) {
		if (receivingSide == GeneralHelper.getOppositeSide(currentSide)) {
			send.run();
			if (ConfigHandler.common.showPacketMessages)
				Main.getLogger().info(message.getName() + " packet sent on side " + currentSide + "\nPath: "
						+ (new ExecutionPath()).get(1));
		} else {
			// Receiving side can't be sending side
			throw new WrongSideException(
					"Packet " + message.getName() + " couldn't be sent\nPath: " + (new ExecutionPath()).get(1),
					GeneralHelper.getOppositeSide(receivingSide));
		}
	}

	public static void sendTransformationMessage(EntityPlayerMP applyOn) {
		ITransformationPlayer cap = TransformationHelper.getCap(applyOn);
		cap.setLevel(cap.getTransformation().getLevel(applyOn));
		TransformationMessage message = new TransformationMessage(cap.getXP(), cap.transformed(),
				cap.getTransformation(), applyOn, cap.getTextureIndex(), cap.getRituals(), cap.getUnlockedPages());
		afterPacketSent(CLIENT, GeneralHelper.getSideFromEntity(applyOn), message,
				() -> INSTANCE.sendToDimension(message, applyOn.world.provider.getDimension()));
	}

	public static void sendTransformationMessageToPlayer(EntityPlayerMP applyOn, EntityPlayerMP sendTo) {
		ITransformationPlayer cap = TransformationHelper.getCap(applyOn);
		cap.setLevel(cap.getTransformation().getLevel(applyOn));
		TransformationMessage message = new TransformationMessage(cap.getXP(), cap.transformed(),
				cap.getTransformation(), applyOn, cap.getTextureIndex(), cap.getRituals(), cap.getUnlockedPages());
		if (cap.getTransformation() == TransformationInit.VAMPIRE)
			applyOn.foodStats = VampireFoodStats.INSTANCE;
		afterPacketSent(CLIENT, GeneralHelper.getSideFromEntity(applyOn), message,
				() -> INSTANCE.sendTo(message, sendTo));
	}

	public static void sendTransformationXPMessage(EntityPlayerMP applyOn) {
		TransformationXPMessage message = new TransformationXPMessage(TransformationHelper.getCap(applyOn).getXP(),
				applyOn);
		afterPacketSent(CLIENT, GeneralHelper.getSideFromEntity(applyOn), message, () -> {
			INSTANCE.sendToDimension(message, applyOn.world.provider.getDimension());
		});
	}

	public static void sendTextureIndexMessage(World forSideCheck) {
		TransformationTextureIndexMessage message = new TransformationTextureIndexMessage(
				TransformationHelper.getCap(Main.proxy.getPlayer()).getTextureIndex());
		afterPacketSent(SERVER, GeneralHelper.getSideFromWorld(forSideCheck), message,
				() -> INSTANCE.sendToServer(message));
	}

	public static void sendBloodMessage(EntityPlayerMP player) {
		BloodMessage message = new BloodMessage(player);
		afterPacketSent(CLIENT, GeneralHelper.getSideFromEntity(player), message,
				() -> INSTANCE.sendTo(message, player));
	}
}