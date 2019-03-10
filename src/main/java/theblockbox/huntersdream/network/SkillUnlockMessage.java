package theblockbox.huntersdream.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import theblockbox.huntersdream.Main;
import theblockbox.huntersdream.api.skill.Skill;
import theblockbox.huntersdream.util.handlers.PacketHandler;

public class SkillUnlockMessage extends MessageBase<SkillUnlockMessage> {
	private String skill;

	public SkillUnlockMessage() {
	}

	public SkillUnlockMessage(Skill skillToUnlock) {
		this.skill = skillToUnlock.toString();
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.skill = MessageBase.readString(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
        MessageBase.writeString(buf, this.skill);
	}

	@Override
	public String getName() {
		return "Skill Unlock Message";
	}

	@Override
	public MessageBase.MessageHandler<SkillUnlockMessage, ? extends IMessage> getMessageHandler() {
		return new SkillUnlockMessage.Handler();
	}

	public static class Handler extends MessageBase.MessageHandler<SkillUnlockMessage, IMessage> {

        @Override
		public IMessage onMessageReceived(SkillUnlockMessage message, MessageContext ctx) {
			if (ctx.side == Side.SERVER) {
                MessageBase.addScheduledTask(ctx, () -> {
					EntityPlayerMP player = ctx.getServerHandler().player;
					Skill skill = Skill.fromName(message.skill);
					if (skill == null) {
						Main.getLogger().error("The player " + player + " tried to unlock a null skill");
						return;
					}
					if(skill.unlockSkillForPlayer(player)){
						player.sendStatusMessage(new TextComponentTranslation("huntersdream.unlockedSkill",
								new TextComponentTranslation(skill.getTranslationKeyName()), skill.getNeededExperienceLevels()),
								true);
						PacketHandler.sendTransformationMessage(player);
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
