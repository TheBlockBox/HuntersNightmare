package theblockbox.huntersdream.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import theblockbox.huntersdream.util.enums.Transformations;
import theblockbox.huntersdream.util.helpers.TransformationHelper;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformationPlayer;

public class TransformationMessage extends MessageBase<TransformationMessage> {

	private int xp;
	private boolean transformed;
	private Transformations transformation;
	private int textureIndex;
	private EntityPlayer player;

	public TransformationMessage() {
	}

	public TransformationMessage(int xp, boolean transformed, Transformations transformation, EntityPlayer player,
			int textureIndex) {
		this.xp = xp;
		this.transformed = transformed;
		this.transformation = transformation;
		this.textureIndex = textureIndex;
		this.player = player;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.xp = buf.readInt();
		this.transformed = buf.readBoolean();
		this.transformation = readTransformation(buf);
		this.player = readPlayer(buf);
		this.textureIndex = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(xp);
		buf.writeBoolean(transformed);
		writeTransformation(buf, transformation);
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
				addScheduledTask(ctx, () -> {
					ITransformationPlayer cap = TransformationHelper.getCap(message.player);
					cap.setXP(message.xp);
					cap.setTransformed(message.transformed);
					cap.setTransformation(message.transformation);
					cap.setTextureIndex(message.textureIndex);
				});
			}
			return null;
		}
	}
}
