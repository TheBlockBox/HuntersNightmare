package theblockbox.huntersdream.util.handlers;

// using static import for CLIENT and SERVER enum constants
import static net.minecraftforge.fml.relauncher.Side.CLIENT;
import static net.minecraftforge.fml.relauncher.Side.SERVER;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import theblockbox.huntersdream.Main;
import theblockbox.huntersdream.entity.EntityWerewolf;
import theblockbox.huntersdream.init.TransformationInit;
import theblockbox.huntersdream.network.BloodMessage;
import theblockbox.huntersdream.network.MessageBase;
import theblockbox.huntersdream.network.MessageBase.MessageHandler;
import theblockbox.huntersdream.network.TransformationMessage;
import theblockbox.huntersdream.network.TransformationReplyMessage;
import theblockbox.huntersdream.network.TransformationTextureIndexMessage;
import theblockbox.huntersdream.network.TransformationWerewolfNightOverMessage;
import theblockbox.huntersdream.network.TransformationWerewolfNoControlMessage;
import theblockbox.huntersdream.network.TransformationXPMessage;
import theblockbox.huntersdream.util.ExecutionPath;
import theblockbox.huntersdream.util.Reference;
import theblockbox.huntersdream.util.VampireFoodStats;
import theblockbox.huntersdream.util.exceptions.WrongSideException;
import theblockbox.huntersdream.util.helpers.GeneralHelper;
import theblockbox.huntersdream.util.helpers.TransformationHelper;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformationPlayer;

@SuppressWarnings("deprecation")
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

	// TODO: Remove when new no control system is done
	/**
	 * @deprecated This will be removed soon. Instead use the methods in
	 *             {@link PacketHandler}
	 */
	@Deprecated
	public enum Packets {
		TRANSFORMATION(new TransformationMessage()), @Deprecated
		NIGHT_OVER(new TransformationWerewolfNightOverMessage()), @Deprecated
		NO_CONTROL(new TransformationWerewolfNoControlMessage()), XP(new TransformationXPMessage()),
		TEXTURE_INDEX(new TransformationTextureIndexMessage(), SERVER),
		TRANSFORMATION_REPLY(new TransformationReplyMessage()),
		TRANSFORMATION_ONE_CLIENT(new TransformationMessage(), CLIENT, false);

		private final MessageBase<?> MESSAGE_BASE;
		/** Side that receives package */
		public final Side SIDE;
		/** The result of {@link MessageBase#getName()} */
		public final String NAME;
		public final boolean REGISTER;

		private Packets(MessageBase<?> messageBase) {
			this(messageBase, CLIENT);
		}

		// Remember: Don't trust the client
		private Packets(MessageBase<?> messageBase, Side side, boolean register) {
			this.MESSAGE_BASE = messageBase;
			this.SIDE = side;
			this.NAME = messageBase.getName();
			this.REGISTER = register;
		}

		private Packets(MessageBase<?> messageBase, Side side) {
			this(messageBase, side, true);
		}

		// not a really beautiful way of doing this, but "it just works"
		@SuppressWarnings("unchecked")
		public <REQ extends MessageBase<REQ>, REPLY extends IMessage> void register() {
			INSTANCE.registerMessage(
					(Class<MessageHandler<REQ, REPLY>>) this.MESSAGE_BASE.getMessageHandler().getClass(),
					(Class<REQ>) this.MESSAGE_BASE.getClass(), networkID++, this.SIDE);
		}

		public void sync(EntityPlayer player, Object... args) {
			ITransformationPlayer cap = TransformationHelper.getCap(player);
			// you can get the sp player through Minecraft.getMinecraft().player;

			if (this.SIDE == GeneralHelper.getOppositeSide(GeneralHelper.getSideFromEntity(player))) {

				switch (this) {
				// Server
				case TRANSFORMATION:
					// could contain render changes
					INSTANCE.sendToDimension(
							new TransformationMessage(cap.getXP(), cap.transformed(), cap.getTransformation(), player,
									cap.getTextureIndex(), cap.getRituals(), cap.getUnlockedPages()),
							player.world.provider.getDimension());
					cap.setLevel(cap.getTransformation().getLevel((EntityPlayerMP) player));
					break;
				case XP:
					INSTANCE.sendToDimension(new TransformationXPMessage(cap.getXP(), player),
							player.world.provider.getDimension());
					break;
				case NIGHT_OVER:
					// only changes player view
					sendMessageToPlayer(new TransformationWerewolfNightOverMessage(player), player);
					break;
				case NO_CONTROL:
					// only changes player view
					sendMessageToPlayer(new TransformationWerewolfNoControlMessage(player, (EntityWerewolf) args[0]),
							player);
					break;

				// Client
				case TEXTURE_INDEX:
					// only cosmetic changes, so you can trust the client
					INSTANCE.sendToServer(new TransformationTextureIndexMessage(cap.getTextureIndex()));
					break;

				case TRANSFORMATION_REPLY:
					sendMessageToPlayer(new TransformationReplyMessage((String) args[0], (EntityLivingBase) args[1],
							(Item) args[2]), player);
					break;

				case TRANSFORMATION_ONE_CLIENT:
					sendMessageToPlayer(
							new TransformationMessage(cap.getXP(), cap.transformed(), cap.getTransformation(), player,
									cap.getTextureIndex(), cap.getRituals(), cap.getUnlockedPages()),
							(EntityPlayerMP) args[0]);
					break;

				default:
					throw new IllegalArgumentException("Illegal arguments: Couldn't find packet " + this.toString()
							+ "\nAdditional info:\nWrong side? "
							+ (GeneralHelper.getOppositeSide(GeneralHelper.getSideFromEntity(player)) == this.SIDE)
							+ "\nPlayer: " + player.getName() + "\nAdditional argument length: " + args.length
							+ "\nPath: " + (new ExecutionPath()).get(1, 4));
				}

				// Receiving side can't be sending side
				if (GeneralHelper.getSideFromEntity(player) == this.SIDE)
					throw new WrongSideException("Couldn't send packet " + this.NAME, player.world);

				if (ConfigHandler.common.showPacketMessages)
					Main.getLogger().info(this.MESSAGE_BASE.getName() + " packet sent on side "
							+ GeneralHelper.getSideFromEntity(player) + "\nPath: " + (new ExecutionPath()).get(1));
			} else {
				throw new WrongSideException(
						"Packet " + this.NAME + " couldn't be sent\nPath: " + (new ExecutionPath()).get(1),
						GeneralHelper.getOppositeSide(this.SIDE));
			}
		}

		public static void sendMessageToPlayer(IMessage message, EntityPlayer player) {
			INSTANCE.sendTo(message, (EntityPlayerMP) player);
		}
	}
}
