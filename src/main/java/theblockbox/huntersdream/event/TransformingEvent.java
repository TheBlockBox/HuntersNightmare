package theblockbox.huntersdream.event;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import theblockbox.huntersdream.util.Transformation;
import theblockbox.huntersdream.util.helpers.TransformationHelper;

/**
 * Called when an entity transforms (back). If the event is canceled, the entity
 * won't transform back. Posted on {@link MinecraftForge#EVENT_BUS}
 */
@Cancelable
public class TransformingEvent extends LivingEvent {
	private boolean transformingBack;
	private Transformation transformation;
	private TransformingEventReason reason;

	public TransformingEvent(EntityLivingBase entity, boolean transformingBack, TransformingEventReason reason) {
		super(entity);
		this.transformingBack = transformingBack;
		this.transformation = TransformationHelper.getTransformation(entity);
		this.transformation.validateIsTransformation();
		this.reason = reason;
		if (entity instanceof EntityPlayer) {
			this.setCanceled(MinecraftForge.EVENT_BUS
					.post(new PlayerTransformingEvent((EntityPlayer) entity, transformingBack, reason)));
		}
	}

	public TransformingEventReason getTransformingEventReason() {
		return reason;
	}

	public boolean transformingBack() {
		return transformingBack;
	}

	public Transformation getTransformation() {
		return transformation;
	}

	public enum TransformingEventReason {
		/**
		 * Caused by the enviroment (for example when werewolf transforms on full moon)
		 */
		ENVIROMENT;
	}

	/** This event is called when a player gets transformed */
	@Cancelable
	public class PlayerTransformingEvent extends PlayerEvent {
		private boolean isTransformingBack;
		private Transformation playerTransformation;
		private TransformingEventReason transformationReason;

		public PlayerTransformingEvent(EntityPlayer player, boolean transformingBack, TransformingEventReason reason) {
			super(player);
			this.isTransformingBack = transformingBack;
			this.playerTransformation = TransformationHelper.getTransformation(player);
			this.transformationReason = reason;
		}

		public TransformingEventReason getTransformingEventReason() {
			return transformationReason;
		}

		public boolean transformingBack() {
			return isTransformingBack;
		}

		public Transformation getTransformation() {
			return playerTransformation;
		}
	}
}
