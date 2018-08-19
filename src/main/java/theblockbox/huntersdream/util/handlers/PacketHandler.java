package theblockbox.huntersdream.util.handlers;

// using static import for CLIENT and SERVER enum constants
import static net.minecraftforge.fml.relauncher.Side.CLIENT;
import static net.minecraftforge.fml.relauncher.Side.SERVER;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import theblockbox.huntersdream.Main;
import theblockbox.huntersdream.entity.EntityWerewolf;
import theblockbox.huntersdream.network.MessageBase;
import theblockbox.huntersdream.network.MessageBase.MessageHandler;
import theblockbox.huntersdream.network.PlaySoundMessage;
import theblockbox.huntersdream.network.TransformationMessage;
import theblockbox.huntersdream.network.TransformationReplyMessage;
import theblockbox.huntersdream.network.TransformationTextureIndexMessage;
import theblockbox.huntersdream.network.TransformationWerewolfNightOverMessage;
import theblockbox.huntersdream.network.TransformationWerewolfNoControlMessage;
import theblockbox.huntersdream.network.TransformationXPMessage;
import theblockbox.huntersdream.util.ExecutionPath;
import theblockbox.huntersdream.util.Reference;
import theblockbox.huntersdream.util.exceptions.WrongSideException;
import theblockbox.huntersdream.util.helpers.GeneralHelper;
import theblockbox.huntersdream.util.helpers.TransformationHelper;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformationPlayer;

public class PacketHandler {
	public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MODID);
	public static int networkID = 0;

	public static void register() {
		for (Packets packet : Packets.values())
			if (packet.REGISTER)
				packet.register();
	}

	public enum Packets {
		TRANSFORMATION(new TransformationMessage()), NIGHT_OVER(new TransformationWerewolfNightOverMessage()),
		NO_CONTROL(new TransformationWerewolfNoControlMessage()), XP(new TransformationXPMessage()),
		TEXTURE_INDEX(new TransformationTextureIndexMessage(), SERVER),
		TRANSFORMATION_REPLY(new TransformationReplyMessage()), PLAY_SOUND(new PlaySoundMessage()),
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
					INSTANCE.sendToDimension(new TransformationMessage(cap.getXP(), cap.transformed(),
							cap.getTransformation(), player, cap.getTextureIndex(), cap.getRituals()),
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
					sendMessageToPlayer(
							new TransformationReplyMessage((String) args[0], (EntityPlayer) args[1], (Item) args[2]),
							player);
					break;

				case PLAY_SOUND:
					sendMessageToPlayer(new PlaySoundMessage((String) args[0], (int) args[1], (int) args[2]), player);
					break;

				case TRANSFORMATION_ONE_CLIENT:
					sendMessageToPlayer(new TransformationMessage(cap.getXP(), cap.transformed(),
							cap.getTransformation(), player, cap.getTextureIndex(), cap.getRituals()),
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

				if (ConfigHandler.showPacketMessages)
					Main.LOGGER.info(this.MESSAGE_BASE.getName() + " packet sent on side "
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
