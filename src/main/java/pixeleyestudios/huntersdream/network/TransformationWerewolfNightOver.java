package pixeleyestudios.huntersdream.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class TransformationWerewolfNightOver implements IMessage {

	private int entityID;

	public TransformationWerewolfNightOver() {
	}

	public TransformationWerewolfNightOver(int entityID) {
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

	public static class MessageHandler implements IMessageHandler<TransformationWerewolfNightOver, IMessage> {

		@Override
		public IMessage onMessage(TransformationWerewolfNightOver message, MessageContext ctx) {
			if (ctx.side == Side.CLIENT) {
				Minecraft mc = Minecraft.getMinecraft();
				mc.addScheduledTask(() -> {
					mc.setRenderViewEntity(mc.player);
				});
			}
			System.out.println("Transformation Werewolf Night Over package received on side " + ctx.side.toString());
			return null;
		}

	}
}
