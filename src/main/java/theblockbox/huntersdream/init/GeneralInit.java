package theblockbox.huntersdream.init;

import theblockbox.huntersdream.api.WerewolfTransformationOverlay;
import theblockbox.huntersdream.util.helpers.GeneralHelper;

/**
 * An init class for different kinds of objects that aren't enough to have their own init class
 */
public class GeneralInit {
    public static final WerewolfTransformationOverlay BLOODSHOT_HEARTBEAT = new WerewolfTransformationOverlay(
            GeneralHelper.newResLoc("gui/werewolf_transformation/bloodshot_heartbeat"))
            .addOverlay(1).addOverlay(2).addOverlay(3).addOverlay(5);
    public static final WerewolfTransformationOverlay HYPERSENSITIVE_VISION = new WerewolfTransformationOverlay(
            GeneralHelper.newResLoc("gui/werewolf_transformation/hypersensitive_vision")).addOverlay(5);
    public static final WerewolfTransformationOverlay BLINKING = new WerewolfTransformationOverlay(
            GeneralHelper.newResLoc("gui/werewolf_transformation/blinking"))
            .addOverlay(1).addOverlay(2).addOverlay(3).addOverlay(4).addOverlay(5).addOverlay(6);

    public static void preInit() {
    }
}
