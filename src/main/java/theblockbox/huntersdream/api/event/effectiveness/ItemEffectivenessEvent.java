package theblockbox.huntersdream.api.event.effectiveness;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

/**
 * ItemEffectivenessEvent is fired when a transformed entity is hurt by a
 * different entity (can also be transformed) while holding an item. The damage
 * should change according to the item when it is "effective" against the hurt
 * entity. <br>
 * This event is fired while {@link LivingHurtEvent} is firing on the normal
 * priority.<br>
 * <br>
 * This event is {@link Cancelable}.<br>
 * If this event is canceled, the hurt entity won't reduce the damage with
 * {@link theblockbox.huntersdream.api.Transformation#getReducedDamage(EntityLivingBase, float)}
 * and the set actions take effect (the event is also canceled in ALL set
 * methods, so you shouldn't have to manually cancel it), otherwise nothing
 * happens<br>
 * <br>
 * This event does not have a result.
 * {@link net.minecraftforge.fml.common.eventhandler.Event.HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
@Cancelable
public class ItemEffectivenessEvent extends EffectivenessEvent {
    private final ItemStack itemStack;

    public ItemEffectivenessEvent(EntityLivingBase hurt, EntityLivingBase attacker, float damage, ItemStack itemStack) {
        super(hurt, attacker, damage);
        this.itemStack = itemStack;
    }

    /**
     * Returns the item stack that was used to attack the entity. Is guaranteed to
     * never be empty
     */
    public ItemStack getItemStack() {
        return this.itemStack;
    }
}
