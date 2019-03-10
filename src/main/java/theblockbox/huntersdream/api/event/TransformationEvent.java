package theblockbox.huntersdream.api.event;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import theblockbox.huntersdream.api.Transformation;
import theblockbox.huntersdream.api.helpers.TransformationHelper;
import theblockbox.huntersdream.entity.EntityWerewolf;

/**
 * TransformationEvent is fired when an entity changes its transformation. It is
 * recommended to take a look at the {@link TransformationEvent.TransformationEventReason} returned
 * by {@link #getTransformationEventReason()} before deciding what to do. <br>
 * <br>
 * This event is {@link Cancelable}.<br>
 * If this event is canceled, the entity won't change its transformation.<br>
 * <br>
 * This event does not have a result.
 * {@link net.minecraftforge.fml.common.eventhandler.Event.HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
@Cancelable
public class TransformationEvent extends LivingEvent {
	private final Transformation transformationBefore;
	private final Transformation transformationAfter;
	private final TransformationEvent.TransformationEventReason reason;

	public TransformationEvent(EntityLivingBase entity, Transformation transformationAfter,
			TransformationEvent.TransformationEventReason reason) {
		super(entity);
		this.transformationBefore = TransformationHelper.getTransformation(entity);
		this.transformationAfter = transformationAfter;
		this.transformationBefore.validateIsTransformation();
		this.transformationAfter.validateIsTransformation();
		this.reason = reason;
	}

	/** Returns the entity's current transformation. */
	public Transformation getTransformationBefore() {
		return this.transformationBefore;
	}

	/** Returns the transformation that the entity should get. */
	public Transformation getTransformationAfter() {
		return this.transformationAfter;
	}

	/** Returns the reason why the transformation should be changed. */
	public TransformationEvent.TransformationEventReason getTransformationEventReason() {
		return this.reason;
	}

	/** The reason for the transformation change. */
	public enum TransformationEventReason {
		/** Caused by an infection */
		INFECTION,
		/**
		 * When an entity spawns with the given transformation (may not always work, for
		 * example there's no call for {@link EntityWerewolf}
		 */
		SPAWN,
		/** When a command is executed */
		COMMAND,
		/** When the bestiary item is clicked in creative mode */
		BESTIARY,
		/**
		 * When an aconite potion is used to cure a werewolf
		 */
		ACONITE,
		/**
		 * Other cause that doesn't fit into one of these categories
		 */
		OTHER
	}
}
