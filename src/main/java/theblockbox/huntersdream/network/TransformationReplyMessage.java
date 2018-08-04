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
	private EntityPlayer player = null;

	public TransformationReplyMessage() {
	}

	public TransformationReplyMessage(String reply, EntityPlayer player, Item pickedUp) {
		this.reply = reply;
		this.player = player;
		this.pickedUp = pickedUp.getUnlocalizedName() + ".name";
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.reply = readString(buf);
		this.player = readPlayer(buf);
		this.pickedUp = readString(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		writeString(buf, reply);
		writeEntity(buf, player);
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
				if ((!(message.reply == null)) && (!message.reply.equals(""))) {
					Minecraft mc = Minecraft.getMinecraft();
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
							tct = new TextComponentTranslation(reply, message.player.getName(), pickedUp);
						} else if (reply.contains("touched")) {
							tct = new TextComponentTranslation(reply, message.player.getName(), pickedUp);
						}
					}

					final TextComponentTranslation translation = tct;
					mc.addScheduledTask(() -> mc.player.sendMessage(translation));
				}
			}
			return null;
		}
	}
}
