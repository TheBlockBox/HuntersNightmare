package theblockbox.huntersdream.init;

        import theblockbox.huntersdream.util.WerewolfTransformationOverlay;
        import theblockbox.huntersdream.util.helpers.GeneralHelper;

/**
 * An init class for different kinds of objects that aren't enough to have their own init class
 */
public class GeneralInit {
    public static final WerewolfTransformationOverlay BLOODSHOT_HEARTBEAT = new WerewolfTransformationOverlay(
            GeneralHelper.newResLoc("gui/werewolf_transformation/bloodshot_heartbeat"), 32, 32)
            .addOverlay(1).addOverlay(2).addOverlay(3).addOverlay(5);
    public static final WerewolfTransformationOverlay HYPERSENSITIVE_VISION = new WerewolfTransformationOverlay(
            GeneralHelper.newResLoc("gui/werewolf_transformation/hypersensitive_vision"),
            16, 16).addOverlay(5);

    public static void preInit() {
    }
}
