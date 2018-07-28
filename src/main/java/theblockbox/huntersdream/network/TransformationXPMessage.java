package theblockbox.huntersdream.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import theblockbox.huntersdream.util.helpers.TransformationHelper;
import theblockbox.huntersdream.util.interfaces.ITransformationPlayer;

public class TransformationXPMessage extends PlayerMessageBase<TransformationXPMessage> {

	private int xp;

	public TransformationXPMessage() {
		super(DEFAULT_ENTITY_ID);
	}

	public TransformationXPMessage(int xp, int entityID) {
		super(entityID);
		this.xp = xp;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.xp = buf.readInt();
		this.entityID = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(xp);
		buf.writeInt(entityID);
	}

	@Override
	public IMessage onMessageReceived(TransformationXPMessage message, MessageContext ctx, EntityPlayer player) {
		if (ctx.side == Side.CLIENT) {
			Minecraft.getMinecraft().addScheduledTask(() -> {
				ITransformationPlayer cap = TransformationHelper.getCap(player);
				cap.setXP(message.xp);
			});
		}
		return null;
	}

	@Override
	public String getName() {
		return "Transformation XP";
	}
}
