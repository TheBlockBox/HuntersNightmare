package theblockbox.huntersdream.init;

import java.util.ArrayList;

import net.minecraft.util.SoundEvent;
import theblockbox.huntersdream.util.helpers.GeneralHelper;

public class SoundInit {
	public static final ArrayList<SoundEvent> SOUND_EVENTS = new ArrayList<>();

	public static final SoundEvent HEART_BEAT;
	public static final SoundEvent WEREWOLF_HOWLING;
	public static final SoundEvent VAMPIRE_GULP;

	static {
		HEART_BEAT = new SoundEventBase("transformation.heartbeat");
		WEREWOLF_HOWLING = new SoundEventBase("transformation.werewolf.howling");
		VAMPIRE_GULP = new SoundEventBase("transformation.vampire.gulp");
	}

	public static class SoundEventBase extends SoundEvent {
		public SoundEventBase(String soundNameIn) {
			super(GeneralHelper.newResLoc(soundNameIn));
			SoundInit.SOUND_EVENTS.add(this);
			this.setRegistryName(GeneralHelper.newResLoc(soundNameIn));
		}
	}
}
