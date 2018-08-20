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
	public static final float DEFAULT_EFFECTIVENESS = 1.5F;

	public EffectiveAgainstTransformation(T object, float effectiveness, @Nonnull Transformations... effectiveAgainst) {
		this.effectiveness = effectiveness;
		this.effectiveAgainst = effectiveAgainst;
		this.object = object;

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

	public static class ItemEffectiveAgainstTransformation extends EffectiveAgainstTransformation<Item> {
		public static final ArrayList<EffectiveAgainstTransformation<Item>> ITEMS = new ArrayList<>();

		public ItemEffectiveAgainstTransformation(Item object, float effectiveness,
				Transformations... effectiveAgainst) {
			super(object, effectiveness, effectiveAgainst);
			ITEMS.add(this);
		}

		public ItemEffectiveAgainstTransformation(Block object, float effectiveness,
				Transformations... effectiveAgainst) {
			this(Item.getItemFromBlock(object), effectiveness, effectiveAgainst);
		}

		@Override
		public ArrayList<EffectiveAgainstTransformation<Item>> getObjects() {
			return ITEMS;
		}
	}

	public static class EntityEffectiveAgainstTransformation extends EffectiveAgainstTransformation<Entity> {
		public static final ArrayList<EffectiveAgainstTransformation<Entity>> ENTITIES = new ArrayList<>();

		public EntityEffectiveAgainstTransformation(Entity object, float effectiveness,
				Transformations... effectiveAgainst) {
			super(object, effectiveness, effectiveAgainst);
			ENTITIES.add(this);
		}

		@Override
		public ArrayList<EffectiveAgainstTransformation<Entity>> getObjects() {
			return ENTITIES;
		}
	}
}
