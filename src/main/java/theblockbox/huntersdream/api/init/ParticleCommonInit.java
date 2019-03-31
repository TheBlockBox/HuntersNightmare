package theblockbox.huntersdream.api.init;

import net.minecraft.util.EnumParticleTypes;
import theblockbox.huntersdream.api.helpers.GeneralHelper;

/**
 * An init class for particles. Can be called on either client or server. For the client side only version, see
 * {@link ParticleClientInit}.
 *
 * @see ParticleClientInit
 */
public class ParticleCommonInit {
    public static final EnumParticleTypes BLOOD_PARTICLE = GeneralHelper.addParticle(GeneralHelper.newResLoc("blood"), false);

    public static void init() {
    }
}
