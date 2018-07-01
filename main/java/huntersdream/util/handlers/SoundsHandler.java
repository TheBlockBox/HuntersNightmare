package huntersdream.util.handlers;

import java.util.ArrayList;

import huntersdream.util.Reference;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class SoundsHandler {
	private static final ArrayList<Sounds> SOUNDS = new ArrayList<>();

	public static void registerSounds() {
		for (Sounds sound : SOUNDS) {
			sound.soundEvent = registerSound(sound.NAME);
		}
	}

	private static SoundEvent registerSound(String name) {
		ResourceLocation location = new ResourceLocation(Reference.MODID + name);
		SoundEvent event = new SoundEvent(location);
		event.setRegistryName(name);
		ForgeRegistries.SOUND_EVENTS.register(event);

		return event;
	}

	public static class Sounds {
		public final String NAME;
		public SoundEvent soundEvent;

		public Sounds(String name) {
			this.NAME = name;
			SOUNDS.add(this);
		}
	}
}
