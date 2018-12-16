package theblockbox.huntersdream.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
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
					int neededExperience = skill.getNeededExperienceLevels();
					ITransformationPlayer transformation = TransformationHelper.getITransformationPlayer(player);
					if (player.experienceLevel >= neededExperience) {
						// test if player has all the skills that are needed to unlock the skill
						for (Skill s : skill.getRequiredSkills())
							if (!transformation.hasSkill(s))
								return;
						player.addExperienceLevel(-neededExperience);
						transformation.addSkill(skill);
						PacketHandler.sendTransformationMessage(player);
					}
				});
			}
			return null;
		}
	}
}
