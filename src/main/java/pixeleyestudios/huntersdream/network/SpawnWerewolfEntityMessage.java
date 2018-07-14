package pixeleyestudios.huntersdream.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import pixeleyestudios.huntersdream.util.helpers.TransformationHelper;

public class SpawnWerewolfEntityMessage implements IMessage {

	private int textureIndex;

	public SpawnWerewolfEntityMessage() {
	}

	public SpawnWerewolfEntityMessage(int textureIndex) {
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

	public static class MessageHandler implements IMessageHandler<SpawnWerewolfEntityMessage, IMessage> {

		@Override
		public IMessage onMessage(SpawnWerewolfEntityMessage message, MessageContext ctx) {
			if (ctx.side == Side.SERVER) {
				EntityPlayerMP player = ctx.getServerHandler().player;
				player.getServerWorld().addScheduledTask(() -> {
					TransformationHelper.getCap(player).setTextureIndex(message.textureIndex);
				});
			}
			System.out.println("Transformation Texture Index package received on side " + ctx.side.toString());
			return null;
		}

	}
}
