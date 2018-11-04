package theblockbox.huntersdream.event;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import theblockbox.huntersdream.util.helpers.TransformationHelper;

/**
 * Posted when {@link TransformationHelper#isInfected(EntityLivingBase)} tests
 * if an entity is infected but doesn't find an infection. If the event is
 * canceled, the method will return that the entity is infected, otherwise the
 * method will return that the entity is not infected. Posted on
 * {@link MinecraftForge#EVENT_BUS}
 */
@Cancelable
public class IsLivingInfectedEvent extends LivingEvent {
	public IsLivingInfectedEvent(EntityLivingBase entity) {
		super(entity);
	}
}
