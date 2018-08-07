package theblockbox.huntersdream.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import theblockbox.huntersdream.util.helpers.TransformationHelper;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformationPlayer;

public class TransformationXPMessage extends MessageBase<TransformationXPMessage> {

	private int xp;
	private EntityPlayer player;

	public TransformationXPMessage() {
	}

	public TransformationXPMessage(int xp, EntityPlayer player) {
		this.player = player;
		this.xp = xp;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.xp = buf.readInt();
		this.player = readPlayer(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(xp);
		writeEntity(buf, player);
	}

	@Override
	public String getName() {
		return "Transformation XP";
	}

	@Override
	public MessageHandler<TransformationXPMessage, ? extends IMessage> getMessageHandler() {
		return new Handler();
	}

	public static class Handler extends MessageHandler<TransformationXPMessage, IMessage> {

		@Override
		public IMessage onMessageReceived(TransformationXPMessage message, MessageContext ctx) {
			if (ctx.side == Side.CLIENT) {
				addScheduledTask(ctx, () -> {
					ITransformationPlayer cap = TransformationHelper.getCap(message.player);
					cap.setXP(message.xp);
				});
			}
			return null;
		}

	}
}
