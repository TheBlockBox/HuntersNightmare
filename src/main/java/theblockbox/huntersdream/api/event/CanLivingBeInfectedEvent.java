package theblockbox.huntersdream.api.event;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import theblockbox.huntersdream.api.Transformation;
import theblockbox.huntersdream.api.helpers.TransformationHelper;

/**
 * CanLivingBeInfectedEvent is fired when it is being tested if an entity can be
 * infected with a specific transformation. <br>
 * This event is fired while
 * {@link TransformationHelper#canBeInfectedWith(Transformation, EntityLivingBase)}
 * tests if an entity can be infected with a specific transformation and can't
 * find anything.<br>
 * <br>
 * This event is {@link Cancelable}.<br>
 * If this event is canceled, the method will return that the entity can be
 * infected.<br>
 * <br>
 * This event does not have a result.
 * {@link net.minecraftforge.fml.common.eventhandler.Event.HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
@Cancelable
public class CanLivingBeInfectedEvent extends LivingEvent {
	private final Transformation infection;

	public CanLivingBeInfectedEvent(EntityLivingBase entity, Transformation infection) {
		super(entity);
		this.infection = infection;
	}

	/** Returns the infection with which the entity should be infected. */
	public Transformation getInfection() {
		return this.infection;
	}
}
