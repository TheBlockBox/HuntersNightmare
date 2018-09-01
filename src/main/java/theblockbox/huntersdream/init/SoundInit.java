package theblockbox.huntersdream.init;

import java.util.ArrayList;

import net.minecraft.util.SoundEvent;
import theblockbox.huntersdream.util.helpers.GeneralHelper;

public class SoundInit {
	public static final ArrayList<SoundEvent> SOUND_EVENTS = new ArrayList<>();

	public static final SoundEvent HEART_BEAT;
	public static final SoundEvent WEREWOLF_HOWLING;

	static {
		HEART_BEAT = new SoundEventBase("transformation.heartbeat");
		HEART_BEAT.setRegistryName(GeneralHelper.newResLoc("transformation.heartbeat"));
		WEREWOLF_HOWLING = new SoundEventBase("transformation.werewolf.howling");
		WEREWOLF_HOWLING.setRegistryName(GeneralHelper.newResLoc("transformation.werewolf.howling"));
	}

	public static class SoundEventBase extends SoundEvent {
		public SoundEventBase(String soundNameIn) {
			super(GeneralHelper.newResLoc(soundNameIn));
			SoundInit.SOUND_EVENTS.add(this);
		}
	}
}
