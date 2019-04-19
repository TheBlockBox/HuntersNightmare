package theblockbox.huntersdream.api.event.effectiveness;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import theblockbox.huntersdream.api.Transformation;
import theblockbox.huntersdream.api.helpers.TransformationHelper;

/**
 * EffectivenessEvent is fired when a transformed entity is hurt by a different
 * entity (can also be transformed). <br>
 * This event is fired while {@link LivingHurtEvent} is firing on the normal or
 * low priority.<br>
 * <br>
 * This event is {@link Cancelable}.<br>
 * If this event is canceled, the hurt entity won't reduce the damage with
 * {@link Transformation#getReducedDamage(EntityLivingBase, float)} and the set
 * actions take effect (the event is also canceled in ALL set methods, so you
 * shouldn't have to manually cancel it), otherwise nothing happens<br>
 * <br>
 * This event does not have a result.
 * {@link net.minecraftforge.fml.common.eventhandler.Event.HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 *
 * @see ArmorEffectivenessEvent
 * @see EntityEffectivenessEvent
 * @see ItemEffectivenessEvent
 **/
@Cancelable
public class EffectivenessEvent extends LivingEvent {
    private final Entity attacker;
    private float damage;
    private final Transformation hurtTransformation;
    private final Transformation attackerTransformation;

    public EffectivenessEvent(EntityLivingBase hurt, Entity attacker, float damage) {
        super(hurt);
        this.attacker = attacker;
        this.damage = damage;
        this.hurtTransformation = TransformationHelper.getTransformation(hurt);
        this.attackerTransformation = (attacker instanceof EntityLivingBase)
                ? TransformationHelper.getTransformation((EntityLivingBase) attacker)
                : Transformation.NONE;
    }

    /**
     * Returns the entity that attacked. Will never be null.
     */
    public Entity getAttacker() {
        return this.attacker;
    }

    /**
     * Returns the entity that was hurt/attacked.
     */
    @Override
    public EntityLivingBase getEntityLiving() {
        return super.getEntityLiving();
    }

    /**
     * Returns the transformation of the hurt entity.<br>
     * Should always return the same as
     * {@link TransformationHelper#getTransformation(EntityLivingBase)} with the
     * hurt entity as an argument does. Is always guaranteed not to be
     * {@link Transformation#NONE}
     */
    public Transformation getHurtTransformation() {
        return this.hurtTransformation;
    }

    /**
     * Returns the transformation of the attacking entity.<br>
     * Should always return the same as
     * {@link TransformationHelper#getTransformation(EntityLivingBase)} with the
     * attacking entity as an argument does. May be {@link Transformation#NONE}
     */
    public Transformation getAttackerTransformation() {
        return this.attackerTransformation;
    }

    /**
     * Returns the damage in half hearts that the attacked entity will get after
     * this event has been fired. The protection of the entity has already been
     * added to this value.
     */
    public float getDamage() {
        return this.damage;
    }

    /**
     * Sets the damage that the attacked entity should get and cancels this event.
     * Not supported by all implementations
     */
    public void setDamage(float damage) {
        this.damage = damage;
        this.setCanceled(true);
    }
}
