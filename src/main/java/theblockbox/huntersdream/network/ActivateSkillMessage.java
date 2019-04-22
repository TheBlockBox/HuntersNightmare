package theblockbox.huntersdream.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import theblockbox.huntersdream.Main;
import theblockbox.huntersdream.api.helpers.TransformationHelper;
import theblockbox.huntersdream.api.interfaces.transformation.ITransformationPlayer;
import theblockbox.huntersdream.api.skill.Skill;
import theblockbox.huntersdream.util.handlers.PacketHandler;

import java.util.Objects;

public class ActivateSkillMessage extends MessageBase<ActivateSkillMessage> {
    private String skill;

    public ActivateSkillMessage() {
    }

    public ActivateSkillMessage(Skill skillToUnlock) {
        this.skill = Objects.toString(skillToUnlock, "");
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
        return "Activate Skill Message";
    }

    @Override
    public MessageBase.MessageHandler<ActivateSkillMessage, ? extends IMessage> getMessageHandler() {
        return new ActivateSkillMessage.Handler();
    }

    public static class Handler extends MessageBase.MessageHandler<ActivateSkillMessage, IMessage> {

        @Override
        public IMessage onMessageReceived(ActivateSkillMessage message, MessageContext ctx) {
            if (ctx.side == Side.SERVER) {
                MessageBase.addScheduledTask(ctx, () -> {
                    EntityPlayerMP player = ctx.getServerHandler().player;
                    Skill skill = Skill.fromName(message.skill);
                    ITransformationPlayer tp = TransformationHelper.getITransformationPlayer(player);

                    if (skill == null) {
                        tp.setActiveSkill(null);
                        PacketHandler.sendTransformationMessage(player);
                        return;
                    }

                    if (skill.shouldShowSkillInSkillBar(player)) {
                        if (skill.isAlwaysActive()) {
                            Main.getLogger().error("The player " + player + " tried to activate the skill "
                                    + message.skill + " but couldn't because it is always active");
                        } else {
                            skill.onSkillActivated(player);
                            tp.setActiveSkill(skill);
                            PacketHandler.sendTransformationMessage(player);
                        }
                    } else {
                        Main.getLogger().error("The player " + player + " tried to activate the skill "
                                + message.skill + " but wasn't allowed to");
                    }
                });
            }
            return null;
        }
    }
}