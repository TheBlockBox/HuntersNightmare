package theblockbox.huntersdream.event;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import theblockbox.huntersdream.util.Transformation;
import theblockbox.huntersdream.util.helpers.TransformationHelper;

/**
 * Posted when
 * {@link TransformationHelper#canBeInfectedWith(Transformation, EntityLivingBase)}
 * tests if an entity can be infected with a specific transformation and can't
 * doesn't find anything. If the event is canceled, the method will return that
 * the entity can be infected, otherwise the method will return that the entity
 * can't be infected. Posted on {@link MinecraftForge#EVENT_BUS}
 */
@Cancelable
public class CanLivingBeInfectedEvent extends LivingEvent {
	private final Transformation infection;

	public CanLivingBeInfectedEvent(EntityLivingBase entity, Transformation infection) {
		super(entity);
		this.infection = infection;
	}

	public Transformation getInfection() {
		return this.infection;
	}
}
