package theblockbox.huntersdream.network;

import java.util.Set;

import com.google.common.collect.Sets;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import theblockbox.huntersdream.api.Skill;
import theblockbox.huntersdream.api.Transformation;
import theblockbox.huntersdream.util.HuntersJournalPage;
import theblockbox.huntersdream.util.VampireFoodStats;
import theblockbox.huntersdream.util.helpers.TransformationHelper;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformationPlayer;

public class TransformationMessage extends MessageBase<TransformationMessage> {
	private Transformation transformation;
	private int textureIndex;
	private int player;
	private Skill[] skills;
	private HuntersJournalPage[] pages;
	private NBTTagCompound transformationData;

	public TransformationMessage() {
	}

	public TransformationMessage(Transformation transformation, EntityPlayer player, int textureIndex,
			Set<Skill> skills, HuntersJournalPage[] pages, NBTTagCompound transformationData) {
		this.transformation = transformation;
		this.textureIndex = textureIndex;
		this.player = player.getEntityId();
		this.skills = skills.toArray(new Skill[skills.size()]);
		this.pages = pages;
		this.transformationData = transformationData;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.transformation = readTransformation(buf);
		this.player = buf.readInt();
		this.textureIndex = buf.readInt();
		this.skills = readArray(buf, Skill::fromName, Skill[]::new);
		this.pages = readArray(buf, HuntersJournalPage::fromName, HuntersJournalPage[]::new);
		this.transformationData = readTag(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		writeTransformation(buf, this.transformation);
		buf.writeInt(this.player);
		buf.writeInt(this.textureIndex);
		writeArray(buf, this.skills, Skill::toString);
		writeArray(buf, this.pages, HuntersJournalPage::toString);
		writeTag(buf, this.transformationData);
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
					ITransformationPlayer cap = TransformationHelper.getITransformationPlayer(player);
					cap.setTransformation(message.transformation);
					cap.setTextureIndex(message.textureIndex);
					cap.setSkills(Sets.newHashSet(message.skills));
					cap.setUnlockedPages(message.pages);
					cap.setTransformationData(message.transformationData);
					if (message.transformation == Transformation.VAMPIRE)
						player.foodStats = VampireFoodStats.INSTANCE;
				});
			}
			return null;
		}
	}
}
