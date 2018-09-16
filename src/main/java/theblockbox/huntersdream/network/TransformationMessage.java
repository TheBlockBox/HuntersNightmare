package theblockbox.huntersdream.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import theblockbox.huntersdream.util.HuntersJournalPage;
import theblockbox.huntersdream.util.VampireFoodStats;
import theblockbox.huntersdream.util.enums.Rituals;
import theblockbox.huntersdream.util.enums.Transformations;
import theblockbox.huntersdream.util.helpers.TransformationHelper;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformationPlayer;

public class TransformationMessage extends MessageBase<TransformationMessage> {

	private int xp;
	private boolean transformed;
	private Transformations transformation;
	private int textureIndex;
	private int player;
	private double level;
	private Rituals[] rituals;
	private HuntersJournalPage[] pages;

	public TransformationMessage() {
	}

	public TransformationMessage(int xp, boolean transformed, Transformations transformation, EntityPlayer player,
			int textureIndex, Rituals[] rituals, HuntersJournalPage[] pages) {
		this.xp = xp;
		this.transformed = transformed;
		this.transformation = transformation;
		this.textureIndex = textureIndex;
		this.player = player.getEntityId();
		this.level = transformation.getLevel((EntityPlayerMP) player);
		this.rituals = rituals;
		this.pages = pages;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.xp = buf.readInt();
		this.transformed = buf.readBoolean();
		this.transformation = readTransformation(buf);
		this.player = buf.readInt();
		this.textureIndex = buf.readInt();
		this.level = buf.readDouble();
		this.rituals = readArray(buf, Rituals::fromName, Rituals[]::new);
		this.pages = readArray(buf, HuntersJournalPage::fromName, HuntersJournalPage[]::new);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(xp);
		buf.writeBoolean(transformed);
		writeTransformation(buf, transformation);
		buf.writeInt(player);
		buf.writeInt(textureIndex);
		buf.writeDouble(level);
		writeArray(buf, rituals, Rituals::toString);
		writeArray(buf, pages, HuntersJournalPage::toString);
	}

	@Override
	public String getName() {
		return "Transformation";
	}

	@Override
	public MessageHandler<TransformationMessage, ? extends IMessage> getMessageHandler() {
		return new Handler();
	}

	public static class Handler extends MessageHandler<TransformationMessage, IMessage> {
		public Handler() {
		}

		@Override
		public IMessage onMessageReceived(final TransformationMessage message, MessageContext ctx) {
			if (ctx.side == Side.CLIENT) {
				addScheduledTask(ctx, () -> {
					EntityPlayer player = (EntityPlayer) Minecraft.getMinecraft().world.getEntityByID(message.player);
					ITransformationPlayer cap = TransformationHelper.getCap(player);
					cap.setXP(message.xp);
					cap.setTransformed(message.transformed);
					cap.setTransformation(message.transformation);
					cap.setTextureIndex(message.textureIndex);
					cap.setLevel(message.level);
					cap.setRituals(message.rituals);
					cap.setUnlockedPages(message.pages);
					if (message.transformation == Transformations.VAMPIRE)
						player.foodStats = VampireFoodStats.INSTANCE;
				});
			}
			return null;
		}
	}
}
