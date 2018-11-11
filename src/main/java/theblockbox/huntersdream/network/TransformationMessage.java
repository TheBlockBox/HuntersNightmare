package theblockbox.huntersdream.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import theblockbox.huntersdream.init.TransformationInit;
import theblockbox.huntersdream.util.HuntersJournalPage;
import theblockbox.huntersdream.util.Transformation;
import theblockbox.huntersdream.util.VampireFoodStats;
import theblockbox.huntersdream.util.enums.Rituals;
import theblockbox.huntersdream.util.helpers.TransformationHelper;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformationPlayer;

public class TransformationMessage extends MessageBase<TransformationMessage> {
	private Transformation transformation;
	private int textureIndex;
	private int player;
	private Rituals[] rituals;
	private HuntersJournalPage[] pages;

	public TransformationMessage() {
	}

	public TransformationMessage(Transformation transformation, EntityPlayer player, int textureIndex,
			Rituals[] rituals, HuntersJournalPage[] pages) {
		this.transformation = transformation;
		this.textureIndex = textureIndex;
		this.player = player.getEntityId();
		this.rituals = rituals;
		this.pages = pages;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.transformation = readTransformation(buf);
		this.player = buf.readInt();
		this.textureIndex = buf.readInt();
		this.rituals = readArray(buf, Rituals::fromName, Rituals[]::new);
		this.pages = readArray(buf, HuntersJournalPage::fromName, HuntersJournalPage[]::new);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		writeTransformation(buf, this.transformation);
		buf.writeInt(this.player);
		buf.writeInt(this.textureIndex);
		writeArray(buf, this.rituals, Rituals::toString);
		writeArray(buf, this.pages, HuntersJournalPage::toString);
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
					EntityPlayer player = getPlayerFromID(message.player);
					ITransformationPlayer cap = TransformationHelper.getCap(player);
					cap.setTransformation(message.transformation);
					cap.setTextureIndex(message.textureIndex);
					cap.setRituals(message.rituals);
					cap.setUnlockedPages(message.pages);
					if (message.transformation == TransformationInit.VAMPIRE)
						player.foodStats = VampireFoodStats.INSTANCE;
				});
			}
			return null;
		}
	}
}
