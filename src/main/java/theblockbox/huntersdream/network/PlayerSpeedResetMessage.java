package theblockbox.huntersdream.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import theblockbox.huntersdream.util.helpers.WerewolfHelper;

public class PlayerSpeedResetMessage extends MessageBase<PlayerSpeedResetMessage> {

	@Override
	public void fromBytes(ByteBuf buf) {
	}

	@Override
	public void toBytes(ByteBuf buf) {
	}

	@Override
	public String getName() {
		return "Player Speed Reset";
	}

	@Override
	public MessageHandler<PlayerSpeedResetMessage, ? extends IMessage> getMessageHandler() {
		return new Handler();
	}

	public static class Handler extends MessageHandler<PlayerSpeedResetMessage, IMessage> {
		public Handler() {
		}

		@Override
		public IMessage onMessageReceived(PlayerSpeedResetMessage message, MessageContext ctx) {
			Minecraft.getMinecraft().player.capabilities.setPlayerWalkSpeed(
					WerewolfHelper.getIWerewolf(Minecraft.getMinecraft().player).getStandardSpeed());
			return null;
		}
	}
}
