package theblockbox.huntersdream.api.event;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

/**
 * WerewolfTransformingEvent is fired when an entity transforms (back). The
 * entity's transformation is always
 * {@link theblockbox.huntersdream.api.Transformation#WEREWOLF}. <br>
 * <br>
 * This event is {@link Cancelable}.<br>
 * If this event is canceled, the entity won't transform (back). <br>
 * <br>
 * This event does not have a result.
 * {@link net.minecraftforge.fml.common.eventhandler.Event.HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
@Cancelable
public class WerewolfTransformingEvent extends LivingEvent {
    private final boolean transformingBack;
    private final WerewolfTransformingEvent.WerewolfTransformingReason reason;

    public WerewolfTransformingEvent(EntityLivingBase entity, boolean transformingBack,
                                     WerewolfTransformingEvent.WerewolfTransformingReason reason) {
        super(entity);
        this.transformingBack = transformingBack;
        this.reason = reason;
        if (entity instanceof EntityPlayer) {
            this.setCanceled(MinecraftForge.EVENT_BUS
                    .post(new WerewolfTransformingEvent.PlayerWerewolfTransformingEvent((EntityPlayer) entity, transformingBack, reason)));
        }
    }

    /**
     * Returns the reason why the werewolf transformed.
     */
    public WerewolfTransformingEvent.WerewolfTransformingReason getTransformingEventReason() {
        return this.reason;
    }

    /**
     * Returns true if the werewolf is transforming back into its normal form, false
     * if the entity is transforming into its werewolf form.
     */
    public boolean transformingBack() {
        return this.transformingBack;
    }

    /**
     * The reason why the werewolf transformed
     */
    public static class WerewolfTransformingReason {
        /**
         * Caused by full moon. Is posted after the werewolf has started transforming. (So if the event is canceled,
         * the entity won't transform, but players will still have gone through the whole transformation process.)
         * Therefore, this is posted after {@link #FULL_MOON_STARTING}. Note that this event works for mobs and players,
         * while {@link #FULL_MOON_STARTING} only works for the latter.
         */
        public static final WerewolfTransformingEvent.WerewolfTransformingReason FULL_MOON = new WerewolfTransformingEvent.WerewolfTransformingReason();
        /**
         * Caused by full moon. Is posted before the werewolf has started transforming. (So if the event is canceled,
         * no signs of transformation will be shown.) Therefore, this is posted before {@link #FULL_MOON}. Note that this
         * event only works for players, so if you want to cancel transformations for mobs, you need to test for {@link #FULL_MOON}.
         */
        public static final WerewolfTransformingEvent.WerewolfTransformingReason FULL_MOON_STARTING = new WerewolfTransformingEvent.WerewolfTransformingReason(WerewolfTransformingEvent.WerewolfTransformingReason.FULL_MOON);
        /**
         * Caused by full moon ending. If canceled, always also skips the transformation (ìf any would otherwise be shown.)
         * Works for both mobs and player.
         */
        public static final WerewolfTransformingEvent.WerewolfTransformingReason FULL_MOON_END = new WerewolfTransformingEvent.WerewolfTransformingReason();
        /**
         * Caused by the player wilfully transforming. Is posted after the werewolf has started transforming. (So if the
         * event is canceled, the player won't transform, but will still gone through the whole transformation process.)
         * Therefore, this is posted after {@link #WILFUL_TRANSFORMATION_STARTING}. Works for players only.
         */
        public static final WerewolfTransformingEvent.WerewolfTransformingReason WILFUL_TRANSFORMATION = new WerewolfTransformingEvent.WerewolfTransformingReason();
        /**
         * Caused by a player wilfully transforming. Is posted before the werewolf has started transforming. (So if the
         * event is canceled, no signs of transformation will be shown.) Therefore, this is posted before
         * {@link #WILFUL_TRANSFORMATION}. Works for players only.
         */
        public static final WerewolfTransformingEvent.WerewolfTransformingReason WILFUL_TRANSFORMATION_STARTING = new WerewolfTransformingEvent.WerewolfTransformingReason(WerewolfTransformingEvent.WerewolfTransformingReason.FULL_MOON);
        /**
         * Caused by the player wilfully ending their transformed state that they'd started themselves. Works for players only.
         */
        public static final WerewolfTransformingEvent.WerewolfTransformingReason WILFUL_TRANSFORMATION_ENDING = new WerewolfTransformingEvent.WerewolfTransformingReason();
        /**
         * Caused by a player that has wilfully transformed going out of time and transforming back. Works for players only.
         */
        public static final WerewolfTransformingEvent.WerewolfTransformingReason WILFUL_TRANSFORMATION_FORCED_ENDING = new WerewolfTransformingEvent.WerewolfTransformingReason();

        private final WerewolfTransformingEvent.WerewolfTransformingReason actualTransformingReason;

        public WerewolfTransformingReason() {
            this.actualTransformingReason = this;
        }

        public WerewolfTransformingReason(WerewolfTransformingEvent.WerewolfTransformingReason actualTransformingReason) {
            this.actualTransformingReason = actualTransformingReason;
        }

        /**
         * Returns the reason that should be used to fire the event for the actual changing of the transformation.
         * (Actual transformation does not include the transformation process for players, but only is the part when
         * isTransformed is set to true via a call to
         * {@link theblockbox.huntersdream.api.helpers.WerewolfHelper#setTransformed(EntityLivingBase, boolean)}.)
         * <br>
         * Default return value is {@code this}.
         */
        public WerewolfTransformingEvent.WerewolfTransformingReason getActualTransformingReason() {
            return this.actualTransformingReason;
        }
    }

    /**
     * This event is the player version of {@link WerewolfTransformingEvent}
     */
    @Cancelable
    public static class PlayerWerewolfTransformingEvent extends PlayerEvent {
        private final boolean isTransformingBack;
        private final WerewolfTransformingEvent.WerewolfTransformingReason transformationReason;

        public PlayerWerewolfTransformingEvent(EntityPlayer player, boolean transformingBack,
                                               WerewolfTransformingEvent.WerewolfTransformingReason reason) {
            super(player);
            this.isTransformingBack = transformingBack;
            this.transformationReason = reason;
        }

        /**
         * Returns the reason why the werewolf transformed.
         */
        public WerewolfTransformingEvent.WerewolfTransformingReason getTransformingEventReason() {
            return this.transformationReason;
        }

        /**
         * Returns true if the werewolf is transforming back into its normal form, false
         * if the entity is transforming into its werewolf form.
         */
        public boolean transformingBack() {
            return this.isTransformingBack;
        }
    }
}
