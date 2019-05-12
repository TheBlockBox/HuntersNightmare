package theblockbox.huntersdream.api.init;

import net.minecraft.util.SoundEvent;
import theblockbox.huntersdream.api.helpers.GeneralHelper;

import java.util.ArrayList;
import java.util.List;

public class SoundInit {
    public static final List<SoundEvent> SOUND_EVENTS = new ArrayList<>();

    public static final SoundEvent HEART_BEAT;
    public static final SoundEvent WEREWOLF_HOWLING;
    public static final SoundEvent VAMPIRE_GULP;
    public static final SoundEvent FLINTLOCK_FIRE;
    public static final SoundEvent FLINTLOCK_RELOAD;

    static {
        HEART_BEAT = new SoundInit.SoundEventBase("transformation.heartbeat");
        WEREWOLF_HOWLING = new SoundInit.SoundEventBase("transformation.werewolf.howling");
        VAMPIRE_GULP = new SoundInit.SoundEventBase("transformation.vampire.gulp");
        FLINTLOCK_FIRE = new SoundInit.SoundEventBase("gun.flintlock.fire");
        FLINTLOCK_RELOAD = new SoundInit.SoundEventBase("gun.flintlock.reload");
    }

    public static class SoundEventBase extends SoundEvent {
        public SoundEventBase(String soundNameIn) {
            super(GeneralHelper.newResLoc(soundNameIn));
            SoundInit.SOUND_EVENTS.add(this);
            this.setRegistryName(GeneralHelper.newResLoc(soundNameIn));
        }
    }
}
