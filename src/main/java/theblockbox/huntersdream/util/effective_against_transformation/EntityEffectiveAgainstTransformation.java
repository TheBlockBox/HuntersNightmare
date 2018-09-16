package theblockbox.huntersdream.util.effective_against_transformation;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import theblockbox.huntersdream.util.enums.Transformations;

public class EntityEffectiveAgainstTransformation extends EffectiveAgainstTransformation<Entity> {
	private static final Set<EntityEffectiveAgainstTransformation> OBJECTS = new HashSet<>();

	private EntityEffectiveAgainstTransformation(Predicate<Entity> isForObject, boolean effectiveAgainstUndead,
			EnumMap<Transformations, Float> effectivenessMap) {
		super(isForObject, effectiveAgainstUndead, effectivenessMap);
	}

	public static EntityEffectiveAgainstTransformation of(Predicate<Entity> isForObject, boolean effectiveAgainstUndead,
			EnumMap<Transformations, Float> effectivenessMap) {
		EntityEffectiveAgainstTransformation eeat = new EntityEffectiveAgainstTransformation(isForObject,
				effectiveAgainstUndead, effectivenessMap);
		OBJECTS.add(eeat);
		return eeat;
	}

	@Nullable
	public static EntityEffectiveAgainstTransformation getFromEntity(Entity entity) {
		for (EntityEffectiveAgainstTransformation eeat : OBJECTS)
			if (eeat.isForObject(entity))
				return eeat;
		return null;
	}

	public static Set<EntityEffectiveAgainstTransformation> getObjects() {
		return Collections.unmodifiableSet(OBJECTS);
	}
}
