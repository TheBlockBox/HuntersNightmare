package theblockbox.huntersdream.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import theblockbox.huntersdream.util.helpers.WerewolfHelper;

public class WerewolfTransformedMessage extends MessageBase<WerewolfTransformedMessage> {

	private int player;
	private boolean transformed;

	public WerewolfTransformedMessage() {
	}

	public WerewolfTransformedMessage(EntityPlayerMP player, boolean transformed) {
		this.player = player.getEntityId();
		this.transformed = transformed;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.player = buf.readInt();
		this.transformed = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.player);
		buf.writeBoolean(this.transformed);
	}

	@Override
	public String getName() {
		return "Werewolf Transformed";
	}

	@Override
	public MessageHandler<WerewolfTransformedMessage, ? extends IMessage> getMessageHandler() {
		return new Handler();
	}

	public static class Handler extends MessageHandler<WerewolfTransformedMessage, IMessage> {
		public Handler() {
		}

		@Override
		public IMessage onMessageReceived(WerewolfTransformedMessage message, MessageContext ctx) {
			if (ctx.side == Side.CLIENT) {
				addScheduledTask(ctx, () -> {
					WerewolfHelper.getIWerewolf(getPlayerFromID(message.player)).setTransformed(message.transformed);
				});
			}
			return null;
		}
	}
}
