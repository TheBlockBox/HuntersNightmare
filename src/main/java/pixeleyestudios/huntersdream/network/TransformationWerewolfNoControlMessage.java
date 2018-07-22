package pixeleyestudios.huntersdream.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import pixeleyestudios.huntersdream.entity.EntityWerewolf;

public class TransformationWerewolfNoControlMessage extends PlayerMessageBase<TransformationWerewolfNoControlMessage> {

	private int werewolfEntityID;

	public TransformationWerewolfNoControlMessage() {
		super(DEFAULT_ENTITY_ID);
	}

	public TransformationWerewolfNoControlMessage(int entityID, int werewolfEntityID) {
		super(entityID);
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
	public IMessage onMessageReceived(TransformationWerewolfNoControlMessage message, MessageContext ctx,
			EntityPlayer player) {
		if (ctx.side == Side.CLIENT) {
			Minecraft mc = Minecraft.getMinecraft();
			mc.addScheduledTask(() -> {
				EntityWerewolf werewolf = (EntityWerewolf) player.world.getEntityByID(message.werewolfEntityID);
				mc.setRenderViewEntity(werewolf);
			});
		}
		return null;
	}

	@Override
	public String getName() {
		return "Transformation Player Werewolf No Control";
	}
}
