package theblockbox.huntersdream.event;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

/**
 * Called when an entity transforms (back). If the event is canceled, the entity
 * won't transform back. The entity's transformation is always
 * {@link theblockbox.huntersdream.init.TransformationInit#WEREWOLF} Posted on
 * {@link MinecraftForge#EVENT_BUS}.
 */
@Cancelable
public class WerewolfTransformingEvent extends LivingEvent {
	private boolean transformingBack;
	private WerewolfTransformingReason reason;

	public WerewolfTransformingEvent(EntityLivingBase entity, boolean transformingBack,
			WerewolfTransformingReason reason) {
		super(entity);
		this.transformingBack = transformingBack;
		this.reason = reason;
		if (entity instanceof EntityPlayer) {
			this.setCanceled(MinecraftForge.EVENT_BUS
					.post(new PlayerWerewolfTransformingEvent((EntityPlayer) entity, transformingBack, reason)));
		}
	}

	public WerewolfTransformingReason getTransformingEventReason() {
		return this.reason;
	}

	public boolean transformingBack() {
		return this.transformingBack;
	}

	public static class WerewolfTransformingReason {
		/** Caused by full moon */
		public static final WerewolfTransformingReason FULL_MOON = new WerewolfTransformingReason();
		/** Caused by full moon ending */
		public static final WerewolfTransformingReason FULL_MOON_END = new WerewolfTransformingReason();
	}

	/** This event is called when a player werewolf transforms */
	@Cancelable
	public class PlayerWerewolfTransformingEvent extends PlayerEvent {
		private boolean isTransformingBack;
		private WerewolfTransformingReason transformationReason;

		public PlayerWerewolfTransformingEvent(EntityPlayer player, boolean transformingBack,
				WerewolfTransformingReason reason) {
			super(player);
			this.isTransformingBack = transformingBack;
			this.transformationReason = reason;
		}

		public WerewolfTransformingReason getTransformingEventReason() {
			return this.transformationReason;
		}

		public boolean transformingBack() {
			return this.isTransformingBack;
		}
	}
}
