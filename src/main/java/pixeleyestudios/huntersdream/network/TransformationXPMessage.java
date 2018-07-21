package pixeleyestudios.huntersdream.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import pixeleyestudios.huntersdream.util.helpers.TransformationHelper;
import pixeleyestudios.huntersdream.util.interfaces.ITransformationPlayer;

public class TransformationXPMessage extends MessageBase<TransformationXPMessage> {

	private int xp;
	private int entityID;

	public TransformationXPMessage() {
	}

	public TransformationXPMessage(int xp, int entityID) {
		this.xp = xp;
		this.entityID = entityID;
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
	public IMessage onMessageReceived(TransformationXPMessage message, MessageContext ctx) {
		if (ctx.side == Side.CLIENT) {
			Entity entity = Minecraft.getMinecraft().world.getEntityByID(message.entityID);
			if (entity instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer) entity;
				Minecraft.getMinecraft().addScheduledTask(() -> {
					ITransformationPlayer cap = TransformationHelper.getCap(player);
					cap.setXP(message.xp);
				});
			}
		}
		return null;
	}

	@Override
	public String getName() {
		return "Transformation XP";
	}
}
