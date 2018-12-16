package theblockbox.huntersdream.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

// TODO: Remove this class if it won't be of any use
public class TransformationTextureIndexMessage extends MessageBase<TransformationTextureIndexMessage> {
	private int textureIndex;

	public TransformationTextureIndexMessage() {
	}

	public TransformationTextureIndexMessage(int textureIndex) {
		this.textureIndex = textureIndex;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.textureIndex = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.textureIndex);
	}

	@Override
	public String getName() {
		return "Transformation Texture Index";
	}

	@Override
	public MessageHandler<TransformationTextureIndexMessage, ? extends IMessage> getMessageHandler() {
		return new Handler();
	}

	public static class Handler extends MessageHandler<TransformationTextureIndexMessage, IMessage> {
		public Handler() {
		}

		@Override
		public IMessage onMessageReceived(TransformationTextureIndexMessage message, MessageContext ctx) {
			if (ctx.side == Side.SERVER) {
//				addScheduledTask(ctx, () -> {
//					EntityPlayerMP player = ctx.getServerHandler().player;
//					TransformationHelper.getITransformationPlayer(player).setTextureIndex(message.textureIndex);
//					// notify everyone that one player has a new texture
//					PacketHandler.sendTransformationMessage(player);
//				});
			}
			return null;
		}
	}
}
