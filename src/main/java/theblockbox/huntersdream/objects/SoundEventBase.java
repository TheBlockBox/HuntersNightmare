package theblockbox.huntersdream.objects;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import theblockbox.huntersdream.init.SoundInit;

public class SoundEventBase extends SoundEvent {

	public SoundEventBase(ResourceLocation soundNameIn) {
		super(soundNameIn);
		SoundInit.SOUND_EVENTS.add(this);
	}
}
