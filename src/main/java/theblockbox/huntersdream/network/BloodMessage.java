package theblockbox.huntersdream.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import theblockbox.huntersdream.api.helpers.VampireHelper;

public class BloodMessage extends MessageBase<BloodMessage> {
    private int player;
    private double blood;

    public BloodMessage() {
    }

    public BloodMessage(EntityPlayer vampire) {
        this.player = vampire.getEntityId();
        this.blood = VampireHelper.getIVampire(vampire).getBloodDouble();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.player = buf.readInt();
        this.blood = buf.readDouble();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.player);
        buf.writeDouble(this.blood);
    }

    @Override
    public String getName() {
        return "Vampire Blood";
    }

    @Override
    public MessageBase.MessageHandler<BloodMessage, ? extends IMessage> getMessageHandler() {
        return new BloodMessage.Handler();
    }

    public static class Handler extends MessageBase.MessageHandler<BloodMessage, IMessage> {

        @Override
        public IMessage onMessageReceived(BloodMessage message, MessageContext ctx) {
            if (ctx.side == Side.CLIENT) {
                MessageBase.addScheduledTask(ctx, () -> VampireHelper.getIVampire(MessageBase.getPlayerFromID(message.player)).setBlood(message.blood));
            }
            return null;
        }
    }
}
