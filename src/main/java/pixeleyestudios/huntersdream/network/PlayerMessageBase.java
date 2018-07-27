package pixeleyestudios.huntersdream.network;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Extend this class if you want to include a player id in your packet/message
 */
public abstract class PlayerMessageBase<T extends PlayerMessageBase<T>> extends MessageBase<T> {
	public static final int DEFAULT_ENTITY_ID = Integer.MAX_VALUE;
	/** Use this id only for the player */
	protected int entityID;

	public PlayerMessageBase() {
		this.entityID = DEFAULT_ENTITY_ID;
	}

	public PlayerMessageBase(int entID) {
		this.entityID = entID;
	}

	@Override
	public final IMessage onMessageReceived(T message, MessageContext ctx) {
		EntityPlayer player = null;
		try {
			if (ctx.side == Side.CLIENT) {
				player = (EntityPlayer) Minecraft.getMinecraft().world.getEntityByID(message.entityID);
			} else if (ctx.side == Side.SERVER) {
				player = (EntityPlayer) ctx.getServerHandler().player.world.getEntityByID(message.entityID);
			}

			if (player == null) {
				throw new NullPointerException("EntityPlayer is null");
			}

			return onMessageReceived(message, ctx, player);
		} catch (NullPointerException e) {
			System.err.println(getName() + " packet couldn't find player with id " + message.entityID
					+ (message.entityID == DEFAULT_ENTITY_ID ? " = default id" : " = not default id"));
		} catch (ClassCastException e) {
			System.err.println(getName() + " packet tried to cast non-player entity to player");
		}

		return null;
	}

	public abstract IMessage onMessageReceived(T message, MessageContext ctx, EntityPlayer player);
}
