package theblockbox.huntersdream.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import theblockbox.huntersdream.init.SoundInit;

public class PlaySoundMessage extends MessageBase<PlaySoundMessage> {
	private String sound;
	private int volume;
	private int pitch;

	public PlaySoundMessage() {
	}

	public PlaySoundMessage(String sound, int volume, int pitch) {
		this.sound = sound;
		this.volume = volume;
		this.pitch = pitch;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.sound = readString(buf);
		this.volume = buf.readInt();
		this.pitch = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		writeString(buf, sound);
		buf.writeInt(volume);
		buf.writeInt(pitch);
	}

	@Override
	public String getName() {
		return "Play Sound";
	}

	@Override
	public MessageHandler<PlaySoundMessage, ? extends IMessage> getMessageHandler() {
		return new Handler();
	}

	public static class Handler extends MessageHandler<PlaySoundMessage, IMessage> {
		public Handler() {
		}

		@Override
		public IMessage onMessageReceived(PlaySoundMessage message, MessageContext ctx) {
			if (ctx.side == Side.CLIENT) {
				SoundEvent sound = null;
				switch (message.sound) {
				case "heartbeat":
					sound = SoundInit.HEART_BEAT;
					break;
				case "howl":
					sound = SoundInit.WEREWOLF_HOWLING;
					break;
				default:
					break;
				}
				Minecraft.getMinecraft().player.playSound(sound, message.volume, message.pitch);
			}
			return null;
		}

	}
}
