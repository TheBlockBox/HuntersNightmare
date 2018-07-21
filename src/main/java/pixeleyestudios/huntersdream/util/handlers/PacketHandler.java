package pixeleyestudios.huntersdream.util.handlers;

// using static import for CLIENT and SERVER enum constants
import static net.minecraftforge.fml.relauncher.Side.CLIENT;
import static net.minecraftforge.fml.relauncher.Side.SERVER;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import pixeleyestudios.huntersdream.network.MessageBase;
import pixeleyestudios.huntersdream.network.TransformationMessage;
import pixeleyestudios.huntersdream.network.TransformationTextureIndexMessage;
import pixeleyestudios.huntersdream.network.TransformationWerewolfNightOverMessage;
import pixeleyestudios.huntersdream.network.TransformationWerewolfNoControlMessage;
import pixeleyestudios.huntersdream.network.TransformationXPMessage;
import pixeleyestudios.huntersdream.util.Reference;
import pixeleyestudios.huntersdream.util.helpers.TransformationHelper;
import pixeleyestudios.huntersdream.util.interfaces.ITransformationPlayer;

public class PacketHandler {
	public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MODID);
	public static int id = 0;

	public static void register() {
		for (Packets packet : Packets.values()) {
			INSTANCE.registerMessage(packet.getMessageClass(), packet.CLASS, id++, packet.SIDE);
		}
	}

	public enum Packets {
		TRANSFORMATION(new TransformationMessage()), NIGHT_OVER(
				new TransformationWerewolfNightOverMessage()), NO_CONTROL(
						new TransformationWerewolfNoControlMessage()), XP(new TransformationXPMessage()), TEXTURE_INDEX(
								new TransformationTextureIndexMessage(), SERVER);

		public final MessageBase<?> MESSAGE_BASE;
		public final Class<? extends IMessage> CLASS;
		/** Side that receives package */
		public final Side SIDE;
		/** The result of {@link MessageBase#getName()} */
		public final String NAME;

		private Packets(MessageBase<?> messageBase) {
			this(messageBase, CLIENT);
		}
		
		// I have literally no idea what this does but it works
		@SuppressWarnings("unchecked")
		public <T> Class<T> getMessageClass(){
			return (Class<T>) this.CLASS;
		}

		// Remember: Don't trust the client
		private Packets(MessageBase<?> messageBase, Side side) {
			this.MESSAGE_BASE = messageBase;
			this.SIDE = side;
			this.CLASS = messageBase.getClass();
			this.NAME = messageBase.getName();
		}

		public void sync(EntityPlayer player, Object... args) {
			ITransformationPlayer cap = TransformationHelper.getCap(player);
			int id = player.getEntityId();
			EntityPlayerMP playerMP = null;
			if (!player.world.isRemote) {
				playerMP = (EntityPlayerMP) player;
			}
			// you can get the sp player through Minecraft.getMinecraft().player;

			if ((this.SIDE == SERVER && player.world.isRemote)
					|| (this.SIDE == Side.CLIENT && (!player.world.isRemote))) {

				switch (this) {
				// Server
				case TRANSFORMATION:
					// could contain render changes
					INSTANCE.sendToAllTracking(new TransformationMessage(cap.getXP(), cap.transformed(),
							cap.getTransformationInt(), id, cap.getTextureIndex()), playerMP);
					break;
				case XP:
					INSTANCE.sendToAllTracking(new TransformationXPMessage(cap.getXP(), id), playerMP);
					break;
				case NIGHT_OVER:
					// only changes player view
					INSTANCE.sendTo(new TransformationWerewolfNightOverMessage(id), playerMP);
					break;
				case NO_CONTROL:
					// only changes player view
					INSTANCE.sendTo(
							new TransformationWerewolfNoControlMessage(id, Integer.parseInt(args[0].toString())),
							playerMP);
					break;

				// Client
				case TEXTURE_INDEX:
					// only cosmetic changes, so you can trust the client
					INSTANCE.sendToServer(new TransformationTextureIndexMessage(cap.getTextureIndex()));
					break;

				default:
					break;
				}

				System.out.println(this.MESSAGE_BASE.getName() + " packet sent on side "
						+ (player.world.isRemote ? Side.CLIENT : Side.SERVER).toString());
			} else {
				throw new IllegalArgumentException("Packet " + this.NAME + " couldn't be sent: Side "
						+ (player.world.isRemote ? CLIENT : SERVER).toString() + " can't send this packet!");
			}
		}
	}
}
