package theblockbox.huntersdream.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
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
	private int entity;

	public TransformationReplyMessage() {
	}

	public TransformationReplyMessage(String reply, EntityLivingBase player, Item pickedUp) {
		this.reply = reply;
		this.entity = player.getEntityId();
		this.pickedUp = pickedUp.getUnlocalizedName() + ".name";
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.reply = readString(buf);
		this.entity = buf.readInt();
		this.pickedUp = readString(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		writeString(buf, reply);
		buf.writeInt(entity);
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
						EntityLivingBase entity = (EntityLivingBase) Minecraft.getMinecraft().world
								.getEntityByID(message.entity);
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
								tct = new TextComponentTranslation(reply, entity.getDisplayName().getFormattedText(),
										pickedUp);
							} else if (reply.contains("touched")) {
								tct = new TextComponentTranslation(reply, entity.getDisplayName().getFormattedText(),
										pickedUp);
							}
						}

						Minecraft.getMinecraft().player.sendMessage(tct);
					}
				});
			}
			return null;
		}
	}
}
