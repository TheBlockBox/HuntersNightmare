package pixeleyestudios.huntersdream.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import pixeleyestudios.huntersdream.entity.EntityWerewolf;

public class TransformationWerewolfNoControlMessage extends MessageBase<TransformationWerewolfNoControlMessage> {

	private int entityID;
	private int werewolfEntityID;

	public TransformationWerewolfNoControlMessage() {
	}

	public TransformationWerewolfNoControlMessage(int entityID, int werewolfEntityID) {
		this.entityID = entityID;
		this.werewolfEntityID = werewolfEntityID;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.entityID = buf.readInt();
		this.werewolfEntityID = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(entityID);
		buf.writeInt(werewolfEntityID);
	}

	@Override
	public IMessage onMessageReceived(TransformationWerewolfNoControlMessage message, MessageContext ctx) {
		if (ctx.side == Side.CLIENT) {
			Entity entity = Minecraft.getMinecraft().world.getEntityByID(message.entityID);
			if (entity instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer) entity;
				Minecraft mc = Minecraft.getMinecraft();
				mc.addScheduledTask(() -> {
					EntityWerewolf werewolf = (EntityWerewolf) player.world.getEntityByID(message.werewolfEntityID);
					mc.setRenderViewEntity(werewolf);
				});
			}
		}
		return null;
	}

	@Override
	public String getName() {
		return "Transformation Player Werewolf No Control";
	}
}
