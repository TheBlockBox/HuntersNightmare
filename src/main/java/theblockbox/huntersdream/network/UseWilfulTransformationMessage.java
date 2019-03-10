package theblockbox.huntersdream.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import theblockbox.huntersdream.Main;
import theblockbox.huntersdream.api.event.WerewolfTransformingEvent;
import theblockbox.huntersdream.api.helpers.TransformationHelper;
import theblockbox.huntersdream.api.helpers.WerewolfHelper;
import theblockbox.huntersdream.util.handlers.PacketHandler;

public class UseWilfulTransformationMessage extends MessageBase<UseWilfulTransformationMessage> {
    public UseWilfulTransformationMessage() {
    }

    @Override
    public void fromBytes(ByteBuf buf) {
    }

    @Override
    public void toBytes(ByteBuf buf) {
    }

    @Override
    public String getName() {
        return "Use Wilful Transformation Message";
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
                    if (WerewolfHelper.isTransformed(player)) {
                        if (WerewolfHelper.canPlayerWilfullyTransformBack(player)) {
                            WerewolfHelper.transformWerewolfBack(player, TransformationHelper.getITransformationPlayer(player),
                                    WerewolfTransformingEvent.WerewolfTransformingReason.WILFUL_TRANSFORMATION_ENDING);
                        } else {
                            Main.getLogger().error("The player " + player + " tried to transform back via deactivating " +
                                    "wilful transformation but wasn't allowed to");
                        }
                    } else {
                        if (WerewolfHelper.canPlayerWilfullyTransform(player)) {
                            if (player.world.provider.getCurrentMoonPhaseFactor() != 1.0F) {
                                WerewolfHelper.setWilfulTransformationTicks(player, player.world.getTotalWorldTime());
                                PacketHandler.sendTransformationMessage(player);
                            }
                        } else {
                            Main.getLogger().error("The player " + player + " tried to activate wilful transformation but " +
                                    "wasn't allowed to");
                        }
                    }
                });
            }
            return null;
        }
    }
}
