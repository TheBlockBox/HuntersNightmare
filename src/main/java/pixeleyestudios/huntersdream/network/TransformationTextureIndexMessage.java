package pixeleyestudios.huntersdream.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import pixeleyestudios.huntersdream.util.helpers.TransformationHelper;
import pixeleyestudios.huntersdream.util.interfaces.ITransformationPlayer;

public class TransformationTextureIndexMessage implements IMessage {

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
		buf.writeInt(textureIndex);
	}

	public static class MessageHandler implements IMessageHandler<TransformationTextureIndexMessage, IMessage> {

		@Override
		public IMessage onMessage(TransformationTextureIndexMessage message, MessageContext ctx) {
			if (ctx.side == Side.SERVER) {
				EntityPlayer player = ctx.getServerHandler().player;
				ITransformationPlayer cap = TransformationHelper.getCap(player);
				cap.setTextureIndex(message.textureIndex);
			}
			System.out.println("Transformation Texture Index package received on side " + ctx.side.toString());
			return null;
		}

	}
}
