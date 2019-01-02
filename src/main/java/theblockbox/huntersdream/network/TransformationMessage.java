package theblockbox.huntersdream.network;

import java.util.Objects;
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

import javax.annotation.Nullable;

public class TransformationMessage extends MessageBase<TransformationMessage> {
	private Transformation transformation;
	private int textureIndex;
	private int player;
	private Skill[] skills;
	private HuntersJournalPage[] pages;
	private NBTTagCompound transformationData;
	private String activeSkill;

	public TransformationMessage() {
	}

	public TransformationMessage(Transformation transformation, EntityPlayer player, int textureIndex,
								 Set<Skill> skills, HuntersJournalPage[] pages, NBTTagCompound transformationData,
								 @Nullable Skill activeSkill) {
		this.transformation = transformation;
		this.textureIndex = textureIndex;
		this.player = player.getEntityId();
		this.skills = skills.toArray(new Skill[0]);
		this.pages = pages;
		this.transformationData = transformationData;
		this.activeSkill = Objects.toString(activeSkill,"");
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.transformation = MessageBase.readTransformation(buf);
		this.player = buf.readInt();
		this.textureIndex = buf.readInt();
		this.skills = MessageBase.readArray(buf, Skill::fromName, Skill[]::new);
		this.pages = MessageBase.readArray(buf, HuntersJournalPage::fromName, HuntersJournalPage[]::new);
		this.transformationData = MessageBase.readTag(buf);
		this.activeSkill = MessageBase.readString(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		MessageBase.writeTransformation(buf, this.transformation);
		buf.writeInt(this.player);
		buf.writeInt(this.textureIndex);
		MessageBase.writeArray(buf, this.skills, Skill::toString);
		MessageBase.writeArray(buf, this.pages, HuntersJournalPage::toString);
		MessageBase.writeTag(buf, this.transformationData);
		MessageBase.writeString(buf, this.activeSkill);
	}

	@Override
	public String getName() {
		return "Transformation";
	}

	@Override
	public MessageBase.MessageHandler<TransformationMessage, ? extends IMessage> getMessageHandler() {
		return new TransformationMessage.Handler();
	}

	public static class Handler extends MessageBase.MessageHandler<TransformationMessage, IMessage> {

        @Override
		public IMessage onMessageReceived(TransformationMessage message, MessageContext ctx) {
			if (ctx.side == Side.CLIENT) {
				MessageBase.addScheduledTask(ctx, () -> {
					EntityPlayer player = MessageBase.getPlayerFromID(message.player);
					ITransformationPlayer cap = TransformationHelper.getITransformationPlayer(player);
					cap.setTransformation(message.transformation);
					cap.setTextureIndex(message.textureIndex);
					cap.setSkills(Sets.newHashSet(message.skills));
					cap.setUnlockedPages(message.pages);
					cap.setTransformationData(message.transformationData);
					cap.setActiveSkill(Skill.fromName(message.activeSkill));
					if (message.transformation == Transformation.VAMPIRE)
						player.foodStats = VampireFoodStats.INSTANCE;
				});
			}
			return null;
		}
	}
}
