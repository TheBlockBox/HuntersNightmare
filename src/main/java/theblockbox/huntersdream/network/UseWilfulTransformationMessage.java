package theblockbox.huntersdream.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import theblockbox.huntersdream.Main;
import theblockbox.huntersdream.api.Transformation;
import theblockbox.huntersdream.api.event.WerewolfTransformingEvent;
import theblockbox.huntersdream.init.SkillInit;
import theblockbox.huntersdream.util.handlers.PacketHandler;
import theblockbox.huntersdream.util.handlers.WerewolfEventHandler;
import theblockbox.huntersdream.util.helpers.TransformationHelper;
import theblockbox.huntersdream.util.helpers.WerewolfHelper;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformationPlayer;

public class UseWilfulTransformationMessage extends MessageBase<UseWilfulTransformationMessage> {
    private boolean activate;

    public UseWilfulTransformationMessage() {
    }

    public UseWilfulTransformationMessage(boolean activate) {
        this.activate = activate;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.activate = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(this.activate);
    }

    @Override
    public String getName() {
        return "Activate Wilful Transformation Message";
    }

    @Override
    public MessageBase.MessageHandler<UseWilfulTransformationMessage, ? extends IMessage> getMessageHandler() {
        return new UseWilfulTransformationMessage.Handler();
    }

    public static class Handler extends MessageBase.MessageHandler<UseWilfulTransformationMessage, IMessage> {

        @Override
        public IMessage onMessageReceived(UseWilfulTransformationMessage message, MessageContext ctx) {
            if (ctx.side == Side.SERVER) {
                MessageBase.addScheduledTask(ctx, () -> {
                    EntityPlayerMP player = ctx.getServerHandler().player;
                    if (message.activate) {
                        if (WerewolfHelper.canPlayerWilfullyTransform(player)) {
                            if (player.world.provider.getCurrentMoonPhaseFactor() != 1.0F) {
                                WerewolfHelper.setWilfulTransformationTicks(player, player.ticksExisted);
                                PacketHandler.sendTransformationMessage(player);
                            }
                        } else {
                            Main.getLogger().error("The player " + player + " tried to activate wilful transformation but " +
                                    "wasn't allowed to");
                        }
                    } else {
                        int ticks = WerewolfHelper.getWilfulTransformationTicks(player);
                        ITransformationPlayer transformation = TransformationHelper.getITransformationPlayer(player);
                        if ((transformation.getTransformation() == Transformation.WEREWOLF) && player.getActiveItemStack().isEmpty()
                                && (transformation.getActiveSkill().orElse(null) == SkillInit.WILFUL_TRANSFORMATION)
                                && WerewolfHelper.isTransformed(player) && (ticks >= 0)) {
                            WerewolfEventHandler.notWerewolfTimeTransformed(player, transformation,
                                    WerewolfTransformingEvent.WerewolfTransformingReason.WILFUL_TRANSFORMATION_ENDING);
                        } else {
                            Main.getLogger().error("The player " + player + " tried to transform back via deactivating " +
                                    "wilful transformation but wasn't allowed to");
                        }
                    }
                });
            }
            return null;
        }
    }
}
