package theblockbox.huntersdream.util.interfaces.effective;

import java.util.ArrayList;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import theblockbox.huntersdream.util.enums.Transformations;

/**
 * Create a new instance of this class (preferably from the subclasses) to make
 * a specific item, entity etc. effective against a specific transformation
 * (currently only item and entity are supported)
 */
public abstract class EffectiveAgainstTransformation<T> implements IEffective {
	public static final ArrayList<EffectiveAgainstTransformation<?>> OBJECTS = new ArrayList<>();
	private float effectiveness;
	private Transformations[] effectiveAgainst;
	private T object;
	private boolean effectiveAgainstUndead;
	public static final float DEFAULT_EFFECTIVENESS = 1.5F;

	/**
	 * Registers an object (currently either item or entity) as effective against
	 * specific transformations
	 * 
	 * @param object                 The object which should be effective against
	 *                               specific transformations
	 * @param effectiveness          The damage multiplier (for example against a
	 *                               werewolf effectiveness 1.5 on a sword with 4
	 *                               attack damage would deal 6 half hearts. Also
	 *                               note that when you'd attack a werewolf with
	 *                               that sword, the werewolf won't have its natural
	 *                               armor, so even effectiveness 1 would deal more
	 *                               damage than another item with the same attack
	 *                               damage). Default value is 1.5
	 * @param effectiveAgainstUndead If the item should also be effective against
	 *                               undead, default for silver items. (effective
	 *                               against undead = deals 2.5 half hearts more
	 *                               damage against undead)
	 * @param effectiveAgainst       The transformations against which the item
	 *                               should be effective
	 */
	public EffectiveAgainstTransformation(T object, float effectiveness, boolean effectiveAgainstUndead,
			@Nonnull Transformations... effectiveAgainst) {
		this.effectiveness = effectiveness;
		this.effectiveAgainst = effectiveAgainst;
		this.object = object;
		this.effectiveAgainstUndead = effectiveAgainstUndead;

		if (object == null || effectiveAgainst == null || effectiveAgainst.length < 1) {
			throw new NullPointerException("Object and transformation parameter aren't allowed to be null");
		}

		for (EffectiveAgainstTransformation<?> eat : OBJECTS)
			if (eat.getObject() == object)
				throw new IllegalArgumentException("Object already registered");
		OBJECTS.add(this);
	}

	@Override
	public Transformations[] transformations() {
		return this.effectiveAgainst;
	}

	/** The damage multiplier when used against the specified creature */
	public float getEffectiveness() {
		return this.effectiveness;
	}

	/**
	 * Returns the object that is effective against the transformations returned in
	 * {@link #transformations()}
	 */
	public T getObject() {
		return object;
	}

	/**
	 * Returns the ArrayList which stores all instances of that class
	 */
	public abstract ArrayList<EffectiveAgainstTransformation<T>> getObjects();

	@SuppressWarnings("unchecked")
	public static <T> EffectiveAgainstTransformation<T> getFromObject(T object) {
		for (EffectiveAgainstTransformation<?> eat : OBJECTS)
			if (eat.getObject().equals(object))
				return (EffectiveAgainstTransformation<T>) eat;

		return null;
	}

	/**
	 * Returns all instances of this class and subclasses
	 */
	public static ArrayList<EffectiveAgainstTransformation<?>> getAllObjects() {
		return OBJECTS;
	}

	/**
	 * Returns true when the object is effective against undead
	 */
	public boolean effectiveAgainstUndead() {
		return this.effectiveAgainstUndead;
	}

	public static class ItemEffectiveAgainstTransformation extends EffectiveAgainstTransformation<Item> {
		public static final ArrayList<EffectiveAgainstTransformation<Item>> ITEMS = new ArrayList<>();

		public ItemEffectiveAgainstTransformation(Item object, float effectiveness, boolean effectiveAgainstUndead,
				Transformations... effectiveAgainst) {
			super(object, effectiveness, effectiveAgainstUndead, effectiveAgainst);
			ITEMS.add(this);
		}

		public ItemEffectiveAgainstTransformation(Block object, float effectiveness, boolean effectiveAgainstUndead,
				Transformations... effectiveAgainst) {
			this(Item.getItemFromBlock(object), effectiveness, effectiveAgainstUndead, effectiveAgainst);
		}

		@Override
		public ArrayList<EffectiveAgainstTransformation<Item>> getObjects() {
			return ITEMS;
		}
	}

	public static class EntityEffectiveAgainstTransformation extends EffectiveAgainstTransformation<Entity> {
		public static final ArrayList<EffectiveAgainstTransformation<Entity>> ENTITIES = new ArrayList<>();

		public EntityEffectiveAgainstTransformation(Entity object, float effectiveness, boolean effectiveAgainstUndead,
				Transformations... effectiveAgainst) {
			super(object, effectiveness, effectiveAgainstUndead, effectiveAgainst);
			ENTITIES.add(this);
		}

		@Override
		public ArrayList<EffectiveAgainstTransformation<Entity>> getObjects() {
			return ENTITIES;
		}
	}
}
