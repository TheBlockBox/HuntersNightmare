package theblockbox.huntersdream.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import theblockbox.huntersdream.util.helpers.TransformationHelper;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformationPlayer;

public class TransformationXPMessage extends MessageBase<TransformationXPMessage> {
	private int xp;
	private int player;
	private double level;

	public TransformationXPMessage() {
	}

	public TransformationXPMessage(int xp, EntityPlayer player) {
		this.player = player.getEntityId();
		this.xp = xp;
		this.level = TransformationHelper.getCap(player).getTransformation().getLevel((EntityPlayerMP) player);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.xp = buf.readInt();
		this.player = buf.readInt();
		this.level = buf.readDouble();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(xp);
		buf.writeInt(player);
		buf.writeDouble(level);
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
		public Handler() {
		}

		@Override
		public IMessage onMessageReceived(TransformationXPMessage message, MessageContext ctx) {
			if (ctx.side == Side.CLIENT) {
				addScheduledTask(ctx, () -> {
					ITransformationPlayer cap = TransformationHelper
							.getCap((EntityPlayer) Minecraft.getMinecraft().world.getEntityByID(message.player));
					cap.setXP(message.xp);
					cap.setLevel(message.level);
				});
			}
			return null;
		}
	}
}