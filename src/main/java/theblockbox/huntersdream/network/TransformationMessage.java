package theblockbox.huntersdream.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import theblockbox.huntersdream.util.helpers.TransformationHelper;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformationPlayer;

public class TransformationMessage extends MessageBase<TransformationMessage> {

	private int xp;
	private boolean transformed;
	private ResourceLocation transformationRL;
	private int textureIndex;
	private EntityPlayer player;

	public TransformationMessage() {
	}

	public TransformationMessage(int xp, boolean transformed, ResourceLocation transformationRL, EntityPlayer player,
			int textureIndex) {
		this.xp = xp;
		this.transformed = transformed;
		this.transformationRL = transformationRL;
		this.textureIndex = textureIndex;
		this.player = player;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.xp = buf.readInt();
		this.transformed = buf.readBoolean();
		this.transformationRL = readResourceLocation(buf);
		this.player = readPlayer(buf);
		this.textureIndex = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(xp);
		buf.writeBoolean(transformed);
		writeResourceLocation(buf, transformationRL);
		writeEntity(buf, player);
		buf.writeInt(textureIndex);
	}

	@Override
	public String getName() {
		return "Transformation";
	}

	@Override
	public MessageHandler<TransformationMessage, ? extends IMessage> getMessageHandler() {
		return new Handler();
	}

	public static class Handler extends MessageHandler<TransformationMessage, IMessage> {
		public Handler() {
		}

		@Override
		public IMessage onMessageReceived(final TransformationMessage message, MessageContext ctx) {
			if (ctx.side == Side.CLIENT) {
				Minecraft.getMinecraft().addScheduledTask(() -> {
					ITransformationPlayer cap = TransformationHelper.getCap(message.player);
					cap.setXP(message.xp);
					cap.setTransformed(message.transformed);
					cap.setTransformationRL(message.transformationRL);
					cap.setTextureIndex(message.textureIndex);
				});
			}
			return null;
		}
	}
}
