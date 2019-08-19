package theblockbox.huntersdream.api.init;

import net.minecraft.util.SoundEvent;
import theblockbox.huntersdream.api.helpers.GeneralHelper;

import java.util.ArrayList;
import java.util.List;

public class SoundInit {
    public static final List<SoundEvent> SOUND_EVENTS = new ArrayList<>();

    public static final SoundEvent HEART_BEAT;
    public static final SoundEvent VAMPIRE_GULP;
    public static final SoundEvent FLINTLOCK_FIRE;
    public static final SoundEvent FLINTLOCK_RELOAD;
    public static final SoundEvent REVOLVER_FIRE;
    public static final SoundEvent REVOLVER_RELOAD;
    public static final SoundEvent RIFLE_FIRE;
    public static final SoundEvent RIFLE_RELOAD;
    public static final SoundEvent SHOTGUN_FIRE;
    public static final SoundEvent SHOTGUN_RELOAD;
    public static final SoundEvent BULLET_HIT;

    static {
        HEART_BEAT = new SoundInit.SoundEventBase("transformation.heartbeat");
        VAMPIRE_GULP = new SoundInit.SoundEventBase("transformation.vampire.gulp");
        FLINTLOCK_FIRE = new SoundInit.SoundEventBase("gun.flintlock.fire");
        FLINTLOCK_RELOAD = new SoundInit.SoundEventBase("gun.flintlock.reload");
        REVOLVER_FIRE = new SoundInit.SoundEventBase("gun.revolver.fire");
        REVOLVER_RELOAD = new SoundInit.SoundEventBase("gun.revolver.reload");
        RIFLE_FIRE = new SoundInit.SoundEventBase("gun.rifle.fire");
        RIFLE_RELOAD = new SoundInit.SoundEventBase("gun.rifle.reload");
        SHOTGUN_FIRE = new SoundInit.SoundEventBase("gun.shotgun.fire");
        SHOTGUN_RELOAD = new SoundInit.SoundEventBase("gun.shotgun.reload");
        BULLET_HIT = new SoundInit.SoundEventBase("gun.bullet_hit");
    }

    public static class SoundEventBase extends SoundEvent {
        public SoundEventBase(String soundNameIn) {
            super(GeneralHelper.newResLoc(soundNameIn));
            SoundInit.SOUND_EVENTS.add(this);
            this.setRegistryName(GeneralHelper.newResLoc(soundNameIn));
        }
    }
}
