package theblockbox.huntersdream.event;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import theblockbox.huntersdream.entity.EntityWerewolf;
import theblockbox.huntersdream.util.Transformation;
import theblockbox.huntersdream.util.helpers.TransformationHelper;

/**
 * Called when an entity changes its transformation. If the event is canceled,
 * the player won't change transformation. Before canceling the event, also look
 * what the reason the event was posted was by looking at
 * {@link #getTransformationEventReason()}. Posted on
 * {@link MinecraftForge#EVENT_BUS}
 */
@Cancelable
public class TransformationEvent extends LivingEvent {
	private Transformation transformationBefore;
	private Transformation transformationAfter;
	private TransformationEventReason reason;

	public TransformationEvent(EntityLivingBase entity, Transformation transformationAfter,
			TransformationEventReason reason) {
		super(entity);
		this.transformationBefore = TransformationHelper.getTransformation(entity);
		this.transformationAfter = transformationAfter;
		this.transformationBefore.validateIsTransformation();
		this.transformationAfter.validateIsTransformation();
		this.reason = reason;
	}

	public Transformation getTransformationBefore() {
		return transformationBefore;
	}

	public Transformation getTransformationAfter() {
		return transformationAfter;
	}

	public TransformationEventReason getTransformationEventReason() {
		return reason;
	}

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
		BESTIARY;
	}
}
