package theblockbox.huntersdream.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class TransformationReplyMessage extends MessageBase<TransformationReplyMessage> {
	public static final String WERE_FP_PICKED = "transformations.werewolf.fp.picked";
	public static final String WERE_FP_TOUCHED = "transformations.werewolf.fp.touched";
	public static final String WERE_TP_PICKED = "transformations.werewolf.tp.picked";
	public static final String WERE_TP_TOUCHED = "transformations.werewolf.tp.touched";
	private String reply = null;
	private String pickedUp = null;
	/** The touched player */
	private int player;

	public TransformationReplyMessage() {
	}

	public TransformationReplyMessage(String reply, EntityPlayer player, Item pickedUp) {
		this.reply = reply;
		this.player = player.getEntityId();
		this.pickedUp = pickedUp.getUnlocalizedName() + ".name";
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.reply = readString(buf);
		this.player = buf.readInt();
		this.pickedUp = readString(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		writeString(buf, reply);
		buf.writeInt(player);
		writeString(buf, pickedUp);
	}

	@Override
	public String getName() {
		return "Transformation Reply Message";
	}

	@Override
	public MessageHandler<TransformationReplyMessage, ? extends IMessage> getMessageHandler() {
		return new Handler();
	}

	public static class Handler extends MessageHandler<TransformationReplyMessage, IMessage> {

		public Handler() {
		}

		@Override
		public IMessage onMessageReceived(TransformationReplyMessage message, MessageContext ctx) {
			if (ctx.side == Side.CLIENT) {
				addScheduledTask(ctx, () -> {
					if ((!(message.reply == null)) && (!message.reply.equals(""))) {
						EntityPlayer player = (EntityPlayer) Minecraft.getMinecraft().world
								.getEntityByID(message.player);
						String reply = message.reply;
						String pickedUp = I18n.format(message.pickedUp);
						TextComponentTranslation tct = null;

						if (reply.contains("fp")) {
							if (reply.contains("picked")) {
								tct = new TextComponentTranslation(reply, pickedUp);
							} else if (reply.contains("touched")) {
								tct = new TextComponentTranslation(reply, pickedUp);
							}
						} else if (reply.contains("tp")) {
							if (reply.contains("picked")) {
								tct = new TextComponentTranslation(reply, player.getName(), pickedUp);
							} else if (reply.contains("touched")) {
								tct = new TextComponentTranslation(reply, player.getName(), pickedUp);
							}
						}

						final TextComponentTranslation translation = tct;
						Minecraft.getMinecraft().player.sendMessage(translation);
					}
				});
			}
			return null;
		}
	}
}
