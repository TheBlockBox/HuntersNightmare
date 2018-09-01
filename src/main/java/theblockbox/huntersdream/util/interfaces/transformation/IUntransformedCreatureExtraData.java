package theblockbox.huntersdream.util.interfaces.transformation;

import java.util.ArrayList;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.entity.EntityCreature;
import theblockbox.huntersdream.util.enums.Transformations;

/**
 * This interface is for all entities that can transform and need to save extra
 * data
 */
public interface IUntransformedCreatureExtraData<T extends EntityCreature> {
	public static final ArrayList<IUntransformedCreatureExtraData<?>> OBJECTS = new ArrayList<>();

	public static <T extends EntityCreature> IUntransformedCreatureExtraData<T> of(
			@Nullable BiConsumer<T, byte[]> applyExtraData, @Nullable Function<T, byte[]> getExtraData,
			@Nonnull Predicate<EntityCreature> forCreature, @Nullable Transformations... transformationsNotImmuneTo) {
		Transformations[] notImmuneTo = (transformationsNotImmuneTo != null) ? transformationsNotImmuneTo
				: new Transformations[0];
		BiConsumer<T, byte[]> applyData = (applyExtraData != null) ? applyExtraData : (b, t) -> {
		};
		Function<T, byte[]> getData = (getExtraData != null) ? getExtraData : t -> new byte[0];

		IUntransformedCreatureExtraData<T> uced = new IUntransformedCreatureExtraData<T>() {

			@Override
			public Transformations[] getTransformationsNotImmuneTo() {
				return notImmuneTo;
			}

			@Override
			public void applyExtraData(T creature, byte[] extraData) {
				applyData.accept(creature, extraData);
			}

			@Override
			public byte[] getExtraData(T creature) {
				return getData.apply(creature);
			}

			@Override
			public boolean forCreature(EntityCreature creature) {
				return forCreature.test(creature);
			}
		};
		OBJECTS.add(uced);
		return uced;
	}

	@SuppressWarnings("unchecked")
	public static <T extends EntityCreature> IUntransformedCreatureExtraData<T> getFromEntity(T creature) {
		for (IUntransformedCreatureExtraData<?> uced : OBJECTS)
			if (uced.forCreature(creature))
				return (IUntransformedCreatureExtraData<T>) uced;
		return null;
	}

	/** Returns the transformations against which the creature isn't immune to */
	public Transformations[] getTransformationsNotImmuneTo();

	/** Applies the extra data in form of a byte array to the given entity */
	public void applyExtraData(T creature, byte[] extraData);

	/** Reads the extra data from a creature and returns it in a byte array */
	public byte[] getExtraData(T creature);

	/** Returns true when this instance is made for the given entity */
	public boolean forCreature(EntityCreature creature);
}
