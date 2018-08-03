package theblockbox.huntersdream.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import theblockbox.huntersdream.util.exceptions.EntityIDNotFoundException;
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

	public static Entity readEntity(ByteBuf buf) {
		int entityID = buf.readInt();
		int entityWorld = buf.readInt();
		Entity entity = DimensionManager.getWorld(entityWorld).getEntityByID(entityID);
		if (entity == null)
			throw new EntityIDNotFoundException("Cannot find entity in the given ByteBuf object", entityID,
					entityWorld);
		return entity;
	}

	public static EntityPlayer readPlayer(ByteBuf buf) {
		return (EntityPlayer) readEntity(buf);
	}

	public static EntityCreature readCreature(ByteBuf buf) {
		return (EntityCreature) readEntity(buf);
	}

	public static void writeEntity(ByteBuf buf, Entity entity) {
		buf.writeInt(entity.getEntityId());
		buf.writeInt(entity.world.provider.getDimension());
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
				System.out.println(name() + " packet received on side " + ctx.side.toString());
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
