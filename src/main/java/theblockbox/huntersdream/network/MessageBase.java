package theblockbox.huntersdream.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.apache.commons.lang3.Validate;
import theblockbox.huntersdream.Main;
import theblockbox.huntersdream.api.Transformation;
import theblockbox.huntersdream.api.helpers.GeneralHelper;
import theblockbox.huntersdream.util.handlers.ConfigHandler;

import javax.annotation.Nonnull;
import java.util.function.Function;
import java.util.function.IntFunction;

public abstract class MessageBase<T extends MessageBase<T>> implements IMessage {

	public abstract String getName();

	public static void writeString(ByteBuf buf, String string) {
		ByteBufUtils.writeUTF8String(buf, string);
	}

	public static String readString(ByteBuf buf) {
		return ByteBufUtils.readUTF8String(buf);
	}

	public static ResourceLocation readResourceLocation(ByteBuf buf) {
		return GeneralHelper.newResLoc(MessageBase.readString(buf));
	}

	public static void writeResourceLocation(ByteBuf buf, ResourceLocation resourceLocation) {
		MessageBase.writeString(buf, resourceLocation.toString());
	}

	public static Transformation readTransformation(ByteBuf buf) {
		String transformationName = MessageBase.readString(buf);
		return Validate.notNull(Transformation.fromName(transformationName),
				"Found null transformation with name %s while reading transformation", transformationName);
	}

	public static void writeTransformation(ByteBuf buf, Transformation transformation) {
		MessageBase.writeString(buf, Validate.notNull(transformation, "The transformation is not allowed to be null").toString());
	}

	public static <T> void writeArray(ByteBuf buf, @Nonnull T[] array, Function<T, String> tToString) {
		buf.writeInt(array.length);
		for (T t : array) MessageBase.writeString(buf, tToString.apply(t));
	}

	public static <T> T[] readArray(ByteBuf buf, Function<String, T> stringToT,
			IntFunction<T[]> newEmptyArrayWithSize) {
		T[] array = newEmptyArrayWithSize.apply(buf.readInt());
		for (int i = 0; i < array.length; i++)
			array[i] = stringToT.apply(MessageBase.readString(buf));
		return array;
	}

	public static void writeByteArray(ByteBuf buf, @Nonnull byte[] bytes) {
		buf.writeInt(bytes.length);
		for (byte aByte : bytes) buf.writeByte(aByte);
	}

	public static byte[] readByteArray(ByteBuf buf) {
		byte[] bytes = new byte[buf.readInt()];
		for (int i = 0; i < bytes.length; i++)
			bytes[i] = buf.readByte();
		return bytes;
	}

	public static NBTTagCompound readTag(ByteBuf buf) {
		return ByteBufUtils.readTag(buf);
	}

	public static void writeTag(ByteBuf buf, NBTTagCompound tag) {
		ByteBufUtils.writeTag(buf, tag);
	}

	public static EntityPlayer getPlayerFromID(int id) {
		return MessageBase.getEntityFromID(id);
	}

	public static <T extends Entity> T getEntityFromID(int id) {
		return Main.proxy.getEntityFromID(id);
	}

	public static void addScheduledTask(MessageContext ctx, Runnable runnableToSchedule) {
		FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(runnableToSchedule);
	}

	public abstract MessageBase.MessageHandler<T, ? extends IMessage> getMessageHandler();

	public abstract static class MessageHandler<T extends MessageBase<T>, REPLY extends IMessage>
			implements IMessageHandler<T, REPLY> {

		@Override
		public REPLY onMessage(T message, MessageContext ctx) {
			REPLY answer = this.onMessageReceived(message, ctx);
			if (ConfigHandler.common.showPacketMessages)
				Main.getLogger().info(this.name() + " packet received on side " + ctx.side);
			return answer;
		}

		public abstract REPLY onMessageReceived(T message, MessageContext ctx);

		public String name() {
			try {
				Object instance = this.getClass().getDeclaringClass().getConstructor().newInstance();
				return (String) instance.getClass().getMethod("getName").invoke(instance);
			} catch (Exception e) {
				return null;
			}
		}
	}
}
