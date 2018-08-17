package theblockbox.huntersdream.objects;

import net.minecraft.util.SoundEvent;
import theblockbox.huntersdream.init.SoundInit;
import theblockbox.huntersdream.util.helpers.GeneralHelper;

public class SoundEventBase extends SoundEvent {

	public SoundEventBase(String soundNameIn) {
		super(GeneralHelper.newResLoc(soundNameIn));
		SoundInit.SOUND_EVENTS.add(this);
	}
}
