package theblockbox.huntersdream.api.event.effectiveness;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

/**
 * EntityEffectivenessEvent is fired when a transformed entity is hurt by a
 * different entity (can also be transformed). The damage should change
 * according to the attacker when it is "effective" against the hurt entity.
 * (The entity may also be something like an arrow!) <br>
 * This event is fired while {@link LivingHurtEvent} is firing on the normal
 * priority.<br>
 * <br>
 * This event is {@link Cancelable}.<br>
 * If this event is canceled, the set actions take effect (the event is also
 * canceled in ALL set methods, so you shouldn't have to manually cancel it),
 * otherwise nothing happens<br>
 * <br>
 * This event does not have a result.
 * {@link net.minecraftforge.fml.common.eventhandler.Event.HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
public class EntityEffectivenessEvent extends EffectivenessEvent {

    public EntityEffectivenessEvent(EntityLivingBase hurt, Entity attacker, float damage) {
        super(hurt, attacker, damage);
    }

    /**
     * Returns the entity that attacked. Will never be null, but may be the
     * immediate source (an arrow for example).
     */
    @Override
    public Entity getAttacker() {
        return super.getAttacker();
    }
}
