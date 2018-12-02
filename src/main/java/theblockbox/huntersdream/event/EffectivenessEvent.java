package theblockbox.huntersdream.event;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import theblockbox.huntersdream.util.Transformation;
import theblockbox.huntersdream.util.helpers.TransformationHelper;

/**
 * EffectivenessEvent is fired when a transformed entity is hurt by a different
 * entity (can also be transformed). <br>
 * This event is fired while {@link LivingHurtEvent} is firing on the normal or
 * low priority.<br>
 * <br>
 * This event is {@link Cancelable}.<br>
 * If this event is canceled, the set actions take effect (the event is also
 * canceled in ALL set methods, so you shouldn't have to manually cancel it),
 * otherwise nothing happens<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
@Cancelable
public class EffectivenessEvent extends LivingEvent {
	private final EntityLivingBase attacker;
	private float damage;
	private Transformation attackerTransformation;

	public EffectivenessEvent(EntityLivingBase hurt, EntityLivingBase attacker, float damage) {
		super(hurt);
		this.attacker = attacker;
		this.damage = damage;
		this.attackerTransformation = TransformationHelper.getTransformation(attacker);
		this.attackerTransformation.validateIsTransformation();
	}

	/**
	 * Returns the entity that attacked
	 */
	public EntityLivingBase getAttacker() {
		return this.attacker;
	}

	/** Returns the entity that was hurt/attacked */
	@Override
	public EntityLivingBase getEntityLiving() {
		return super.getEntityLiving();
	}

	/**
	 * Returns the transformation of the attacking entity.<br>
	 * Should always return the same as
	 * {@link TransformationHelper#getTransformation(EntityLivingBase)} with the
	 * attacking entity as an argument does
	 */
	public Transformation getAttackerTransformation() {
		return this.attackerTransformation;
	}

	/**
	 * Returns the damage in half hearts that the attacked entity will get after
	 * this event has been fired
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
