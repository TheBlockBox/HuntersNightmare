package pixeleyestudios.huntersdream.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public abstract class MessageBase<T extends IMessage> implements IMessage, IMessageHandler<T, IMessage> {

	@Override
	public final IMessage onMessage(T message, MessageContext ctx) {
		IMessage iMessage = onMessageReceived(message, ctx);
		System.out.println(getName() + " packet received on side " + ctx.side.toString());
		return iMessage;
	}

	public abstract IMessage onMessageReceived(T message, MessageContext ctx);

	public abstract String getName();

	public static void writeString(ByteBuf buf, String string) {
		ByteBufUtils.writeUTF8String(buf, string);
	}

	public static String readString(ByteBuf buf) {
		return ByteBufUtils.readUTF8String(buf);
	}
}
