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

import java.util.Optional;

public class UseSkillMessage extends MessageBase<UseSkillMessage> {
    public UseSkillMessage() {
    }

    @Override
    public void fromBytes(ByteBuf buf) {
    }

    @Override
    public void toBytes(ByteBuf buf) {
    }

    @Override
    public String getName() {
        return "Use Skill Message";
    }

    @Override
    public MessageBase.MessageHandler<UseSkillMessage, ? extends IMessage> getMessageHandler() {
        return new UseSkillMessage.Handler();
    }

    public static class Handler extends MessageBase.MessageHandler<UseSkillMessage, IMessage> {

        @Override
        public IMessage onMessageReceived(UseSkillMessage message, MessageContext ctx) {
            if (ctx.side == Side.SERVER) {
                MessageBase.addScheduledTask(ctx, () -> {
                    EntityPlayerMP player = ctx.getServerHandler().player;
                    ITransformationPlayer transformation = TransformationHelper.getITransformationPlayer(player);
                    transformation.getActiveSkill().ifPresent(s -> s.onSkillUse(player));
                    Optional<Skill> skill = transformation.getActiveSkill();
                    if (skill.isPresent() && player.getActiveItemStack().isEmpty()) {
                        skill.get().onSkillUse(player);
                    } else {
                        Main.getLogger().error("The player " + player + " tried to use the skill " + skill
                                .map(Skill::toString).orElse("null") + " but couldn't since they either didn't have" +
                                " any skill selected or they had an item in their hand");
                    }
                });
            }
            return null;
        }
    }
}
