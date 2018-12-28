package theblockbox.huntersdream.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import theblockbox.huntersdream.Main;
import theblockbox.huntersdream.api.Skill;
import theblockbox.huntersdream.util.handlers.PacketHandler;
import theblockbox.huntersdream.util.helpers.TransformationHelper;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformationPlayer;

public class SkillUnlockMessage extends MessageBase<SkillUnlockMessage> {
	private String skill;

	public SkillUnlockMessage() {
	}

	public SkillUnlockMessage(Skill skillToUnlock) {
		this.skill = skillToUnlock.toString();
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.skill = readString(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		writeString(buf, this.skill);
	}

	@Override
	public String getName() {
		return "Skill Unlock Message";
	}

	@Override
	public MessageHandler<SkillUnlockMessage, ? extends IMessage> getMessageHandler() {
		return new Handler();
	}

	public static class Handler extends MessageHandler<SkillUnlockMessage, IMessage> {
		public Handler() {
		}

		@Override
		public IMessage onMessageReceived(SkillUnlockMessage message, MessageContext ctx) {
			if (ctx.side == Side.SERVER) {
				addScheduledTask(ctx, () -> {
					EntityPlayerMP player = ctx.getServerHandler().player;
					Skill skill = Skill.fromName(message.skill);
					if(skill.unlockSkillForPlayer(player)){
						player.sendMessage(new TextComponentTranslation("huntersdream.unlockedSkill",
								new TextComponentTranslation(skill.getTranslationKeyName()), skill.getNeededExperienceLevels()));
					} else {
						Main.getLogger().error("The player " + player + " tried to unlock the skill " + message.skill
								+ " but wasn't allowed to unlock it");
					}
				});
			}
			return null;
		}
	}
}
