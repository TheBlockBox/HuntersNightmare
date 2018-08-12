package theblockbox.huntersdream.init;

import java.util.ArrayList;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import theblockbox.huntersdream.objects.SoundEventBase;
import theblockbox.huntersdream.util.Reference;

public class SoundInit {
	public static final ArrayList<SoundEvent> SOUND_EVENTS = new ArrayList<>();

	public static final SoundEvent HEART_BEAT;
	/**
	 * Couldn't find the right one in mc, so I just created this one and used the mc
	 * sound
	 */
	public static final SoundEvent GHAST_SCREAM;
	public static final SoundEvent WEREWOLF_HOWLING;

	static {
		HEART_BEAT = new SoundEventBase(new ResourceLocation(Reference.MODID, "transformation.heartbeat"));
		HEART_BEAT.setRegistryName(new ResourceLocation(Reference.MODID, "transformation.heartbeat"));
		GHAST_SCREAM = new SoundEventBase(new ResourceLocation(Reference.MODID, "mob.ghast.scream"));
		GHAST_SCREAM.setRegistryName(new ResourceLocation(Reference.MODID, "mob.ghast.scream"));
		WEREWOLF_HOWLING = new SoundEventBase(new ResourceLocation(Reference.MODID, "transformation.werewolf.howling"));
		WEREWOLF_HOWLING.setRegistryName(new ResourceLocation(Reference.MODID, "transformation.werewolf.howling"));
	}
}
