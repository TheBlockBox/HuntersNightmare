package theblockbox.huntersdream.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import theblockbox.huntersdream.entity.EntityWerewolf;

public class TransformationWerewolfNoControlMessage extends MessageBase<TransformationWerewolfNoControlMessage> {

	private EntityPlayer player;
	private EntityWerewolf werewolf;

	public TransformationWerewolfNoControlMessage() {
	}

	public TransformationWerewolfNoControlMessage(EntityPlayer player, EntityWerewolf werewolf) {
		this.player = player;
		this.werewolf = werewolf;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.player = readPlayer(buf);
		this.werewolf = (EntityWerewolf) readEntity(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		writeEntity(buf, player);
		writeEntity(buf, werewolf);
	}

	@Override
	public String getName() {
		return "Transformation Player Werewolf No Control";
	}

	@Override
	public MessageHandler<TransformationWerewolfNoControlMessage, ? extends IMessage> getMessageHandler() {
		return new Handler();
	}

	public static class Handler extends MessageHandler<TransformationWerewolfNoControlMessage, IMessage> {
		public Handler() {
		}

		@Override
		public IMessage onMessageReceived(TransformationWerewolfNoControlMessage message, MessageContext ctx) {
			if (ctx.side == Side.CLIENT) {
				addScheduledTask(ctx, () -> {
					Minecraft mc = Minecraft.getMinecraft();
					EntityWerewolf werewolf = message.werewolf;
					mc.setRenderViewEntity(werewolf);
					mc.gameSettings.thirdPersonView = 3;
				});
			}
			return null;
		}

	}
}
