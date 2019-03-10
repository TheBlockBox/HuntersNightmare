package theblockbox.huntersdream.api.event;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import theblockbox.huntersdream.api.helpers.TransformationHelper;

/**
 * IsLivingInfectedEvent is used to determine if an entity is infected. <br>
 * This event is fired while when
 * {@link TransformationHelper#isInfected(EntityLivingBase)} doesn't find an
 * infection. <br>
 * <br>
 * This event is {@link Cancelable}.<br>
 * If this event is canceled, the method will return that the entity is
 * infected.<br>
 * <br>
 * This event does not have a result.
 * {@link net.minecraftforge.fml.common.eventhandler.Event.HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
@Cancelable
public class IsLivingInfectedEvent extends LivingEvent {
	public IsLivingInfectedEvent(EntityLivingBase entity) {
		super(entity);
	}
}
