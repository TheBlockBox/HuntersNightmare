package theblockbox.huntersdream.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class TransformationWerewolfNightOverMessage extends MessageBase<TransformationWerewolfNightOverMessage> {
	private int player;

	public TransformationWerewolfNightOverMessage() {
	}

	public TransformationWerewolfNightOverMessage(EntityPlayer player) {
		this.player = player.getEntityId();
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.player = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(player);
	}

	@Override
	public String getName() {
		return "Transformation Werewolf Night Over";
	}

	@Override
	public MessageHandler<TransformationWerewolfNightOverMessage, ? extends IMessage> getMessageHandler() {
		return new Handler();
	}

	public static class Handler extends MessageHandler<TransformationWerewolfNightOverMessage, IMessage> {

		@Override
		public IMessage onMessageReceived(TransformationWerewolfNightOverMessage message, MessageContext ctx) {
			if (ctx.side == Side.CLIENT) {
				addScheduledTask(ctx, () -> {
					Minecraft.getMinecraft().setRenderViewEntity(
							(EntityPlayer) Minecraft.getMinecraft().world.getEntityByID(message.player));
				});
			}
			return null;
		}
	}
}
