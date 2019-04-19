package theblockbox.huntersdream.api.event.effectiveness;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

/**
 * ArmorEffectivenessEvent is fired when a transformed entity is hurt by a
 * different entity (can also be transformed) while wearing armor. <br>
 * This event is fired while {@link LivingHurtEvent} is firing on the low
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
// TODO: Doesn't it reduce the damage beforehand?
@Cancelable
public class ArmorEffectivenessEvent extends EffectivenessEvent {
    private final ItemStack armor;
    private float thorns = 0.0F;
    private float removedDamage = 0.0F;
    private int armorDamage = 0;

    public ArmorEffectivenessEvent(EntityLivingBase hurt, EntityLivingBase attacker, float initialDamage,
                                   ItemStack armor) {
        super(hurt, attacker, initialDamage);
        this.armor = armor;
    }

    /**
     * Returns the armor part of the hurt entity. Is guaranteed to never be
     * empty<br>
     * Don't damage the returned ItemStack but use {@link #setArmorDamage(int)}
     * instead
     */
    public ItemStack getArmor() {
        return this.armor;
    }

    /**
     * Returns the damage the armor part will get after the event was fired. Default
     * value is 0
     */
    public int getArmorDamage() {
        return this.armorDamage;
    }

    /**
     * Sets the armor damage the armor part will get after this event was fired and
     * cancels it
     */
    public void setArmorDamage(int armorDamage) {
        this.armorDamage = armorDamage;
        this.setCanceled(true);
    }

    /**
     * Returns the thorns damage in half hearts that the attacker will get after
     * this event was fired. If it is 0, the attacker won't be attacked. <br>
     * Default value is 0.<br>
     * Thorns of multiple events will add up (for example if the helmet event
     * returns 5 thorns and the chestplate one 4, the thorns value will be 9)
     */
    public float getThorns() {
        return this.thorns;
    }

    /**
     * Sets the thorns damage that the attacker will get after this event was fired
     * and cancels it
     */
    public void setThorns(float thorns) {
        this.thorns = thorns;
        this.setCanceled(true);
    }

    /**
     * Returns the damage in half hearts that will be removed from the initial
     * damage to get the actual damage the hurt entity will get. (For example if the
     * intial damage was 7 and the removed damage was 3, the entity would get 4
     * damage)<br>
     * Default value is 0 (no damage change)
     */
    public float getRemovedDamage() {
        return this.removedDamage;
    }

    /**
     * Sets the removed damage and cancels this event
     */
    public void setRemovedDamage(float removedDamage) {
        this.removedDamage = removedDamage;
        this.setCanceled(true);
    }

    /**
     * This method does not work in this implementation. Throws an
     * {@link UnsupportedOperationException} if called.
     */
    @Override
    public void setDamage(float damage) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
}
