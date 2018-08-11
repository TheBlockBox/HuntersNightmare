package theblockbox.huntersdream.network;

import javax.annotation.Nonnull;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import theblockbox.huntersdream.Main;
import theblockbox.huntersdream.util.enums.Rituals;
import theblockbox.huntersdream.util.enums.Transformations;
import theblockbox.huntersdream.util.handlers.ConfigHandler;

public abstract class MessageBase<T extends MessageBase<T>> implements IMessage {

	public MessageBase() {
	}

	public abstract String getName();

	public static void writeString(ByteBuf buf, String string) {
		ByteBufUtils.writeUTF8String(buf, string);
	}

	public static String readString(ByteBuf buf) {
		return ByteBufUtils.readUTF8String(buf);
	}

	public static ResourceLocation readResourceLocation(ByteBuf buf) {
		return new ResourceLocation(readString(buf));
	}

	public static void writeResourceLocation(ByteBuf buf, ResourceLocation resourceLocation) {
		writeString(buf, resourceLocation.toString());
	}

	public static Transformations readTransformation(ByteBuf buf) {
		String transformationName = readString(buf);
		Transformations transformation = Transformations.fromName(transformationName);
		if (transformation == null)
			throw new NullPointerException("Found transformation is null. Name: " + transformationName);
		return transformation;
	}

	public static void writeTransformation(ByteBuf buf, @Nonnull Transformations transformation) {
		if (transformation != null)
			writeString(buf, transformation.toString());
		else
			throw new IllegalArgumentException("The transformation argument is null which it isn't allowed to be");
	}

	public static void writeRitualArray(ByteBuf buf, @Nonnull Rituals[] rituals) {
		buf.writeInt(rituals.length); // rituals length
		for (int i = 0; i < rituals.length; i++)
			writeString(buf, rituals[i].toString());
	}

	public static Rituals[] readRitualArray(ByteBuf buf) {
		Rituals[] rituals = new Rituals[buf.readInt()];
		for (int i = 0; i < rituals.length; i++)
			rituals[i] = Rituals.fromName(readString(buf));
		return rituals;
	}

	public static void addScheduledTask(MessageContext ctx, Runnable runnableToSchedule) {
		FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(runnableToSchedule);
	}

	public abstract MessageHandler<T, ? extends IMessage> getMessageHandler();

	public abstract static class MessageHandler<T extends MessageBase<T>, REPLY extends IMessage>
			implements IMessageHandler<T, REPLY> {
		public MessageHandler() {
		}

		@Override
		public REPLY onMessage(T message, MessageContext ctx) {
			REPLY answer = onMessageReceived(message, ctx);
			if (ConfigHandler.showPacketMessages)
				Main.LOGGER.info(name() + " packet received on side " + ctx.side.toString());
			return answer;
		}

		public abstract REPLY onMessageReceived(T message, MessageContext ctx);

		public String name() {
			try {
				Object instance = getClass().getDeclaringClass().getConstructor().newInstance();
				return (String) instance.getClass().getMethod("getName").invoke(instance);
			} catch (Exception e) {
				return null;
			}
		}
	}
}
