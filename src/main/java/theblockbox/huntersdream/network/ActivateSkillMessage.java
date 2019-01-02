package theblockbox.huntersdream.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import theblockbox.huntersdream.Main;
import theblockbox.huntersdream.api.Skill;
import theblockbox.huntersdream.util.handlers.PacketHandler;
import theblockbox.huntersdream.util.helpers.TransformationHelper;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformationPlayer;

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

                    if(skill == null) {
                        tp.setActiveSkill(null);
                        PacketHandler.sendTransformationMessage(player);
                        return;
                    }

                    if(tp.hasSkill(skill)) {
                        if(skill.isAlwaysActive()) {
                            Main.getLogger().error("The player " + player + " tried to activate the skill "
                                    + message.skill + " but couldn't because it is always active");
                        } else {
                            tp.setActiveSkill(skill);
                            PacketHandler.sendTransformationMessage(player);
                        }
                    } else {
                        Main.getLogger().error("The player " + player + " tried to activate the skill "
                                + message.skill + " but hasn't unlocked it yet");
                    }
                });
            }
            return null;
        }
    }
}