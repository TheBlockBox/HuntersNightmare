package theblockbox.huntersdream.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import theblockbox.huntersdream.Main;
import theblockbox.huntersdream.api.helpers.WerewolfHelper;
import theblockbox.huntersdream.util.handlers.PacketHandler;

public class UseBiteMessage extends MessageBase<UseBiteMessage> {
    private int entity;

    public UseBiteMessage() {
    }

    public UseBiteMessage(Entity entity) {
        this.entity = entity.getEntityId();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.entity = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.entity);
    }

    @Override
    public String getName() {
        return "Use Bite Message";
    }

    @Override
    public MessageBase.MessageHandler<UseBiteMessage, ? extends IMessage> getMessageHandler() {
        return new UseBiteMessage.Handler();
    }

    public static class Handler extends MessageBase.MessageHandler<UseBiteMessage, IMessage> {

        @Override
        public IMessage onMessageReceived(UseBiteMessage message, MessageContext ctx) {
            if (ctx.side == Side.SERVER) {
                MessageBase.addScheduledTask(ctx, () -> {
                    EntityPlayerMP player = ctx.getServerHandler().player;
                    Entity entity = player.world.getEntityByID(message.entity);
                    long totalWorldTime = player.world.getTotalWorldTime();

                    if (WerewolfHelper.canPlayerBiteAgain(player)) {
                        // TODO: Not hardcode distance?
                        if ((entity != null) && (player.getPositionVector().distanceTo(entity.getPositionVector()) < 5.0D)) {
                            WerewolfHelper.setBiteTicks(player, totalWorldTime);
                            WerewolfHelper.setLastAttackBite(player, true);
                            PacketHandler.sendTransformationMessage(player);
                            player.attackTargetEntityWithCurrentItem(entity);
                        } else {
                            Main.getLogger().error("The player " + player + " tried to use the bite skill but couldn't since " +
                                    "either the attacked entity (" + entity + ") was out of their reach (5 blocks maximum) or was null");
                        }
                    } else {
                        Main.getLogger().error("The player " + player + " tried to use the bite skill but couldn't since " +
                                "either the cooldown hasn't ended yet or they weren't transformed (Current time: "
                                + totalWorldTime + " Cooldown end: " + (totalWorldTime - WerewolfHelper.getBiteTicks(player)) + ")");
                    }
                });
            }
            return null;
        }
    }
}
