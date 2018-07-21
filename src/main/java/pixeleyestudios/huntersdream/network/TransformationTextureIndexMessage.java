package pixeleyestudios.huntersdream.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import pixeleyestudios.huntersdream.util.helpers.TransformationHelper;

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
		buf.writeInt(textureIndex);
	}

	@Override
	public IMessage onMessageReceived(TransformationTextureIndexMessage message, MessageContext ctx) {
		if (ctx.side == Side.SERVER) {
			EntityPlayerMP player = ctx.getServerHandler().player;
			player.getServerWorld().addScheduledTask(() -> {
				TransformationHelper.getCap(player).setTextureIndex(message.textureIndex);
			});
		}
		return null;
	}

	@Override
	public String getName() {
		return "Transformation Texture Index";
	}
}
