package pixeleyestudios.huntersdream.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class TransformationWerewolfNightOverMessage extends MessageBase<TransformationWerewolfNightOverMessage> {

	private int entityID;

	public TransformationWerewolfNightOverMessage() {
	}

	public TransformationWerewolfNightOverMessage(int entityID) {
		this.entityID = entityID;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.entityID = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(entityID);
	}

	@Override
	public IMessage onMessageReceived(TransformationWerewolfNightOverMessage message, MessageContext ctx) {
		if (ctx.side == Side.CLIENT) {
			Minecraft mc = Minecraft.getMinecraft();
			mc.addScheduledTask(() -> {
				mc.setRenderViewEntity(mc.player);
			});
		}
		return null;
	}

	@Override
	public String getName() {
		return "Transformation Werewolf Night Over";
	}
}
