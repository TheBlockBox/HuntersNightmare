package theblockbox.huntersdream.api.init;

import theblockbox.huntersdream.api.WerewolfTransformationOverlay;
import theblockbox.huntersdream.api.helpers.GeneralHelper;

/**
 * An init class for all overlays for the werewolf transformation
 */
public class WerewolfTransformationOverlayInit {
    public static final WerewolfTransformationOverlay BLOODSHOT_HEARTBEAT = new WerewolfTransformationOverlay(
            GeneralHelper.newResLoc("gui/werewolf_transformation/bloodshot_heartbeat"))
            .addOverlay(1).addOverlay(2).addOverlay(3).addOverlay(5);
    public static final WerewolfTransformationOverlay BLINKING = new WerewolfTransformationOverlay(
            GeneralHelper.newResLoc("gui/werewolf_transformation/blinking"))
            .addOverlay(1).addOverlay(2).addOverlay(3).addOverlay(4).addOverlay(5).addOverlay(6);

    public static void preInit() {
    }
}
