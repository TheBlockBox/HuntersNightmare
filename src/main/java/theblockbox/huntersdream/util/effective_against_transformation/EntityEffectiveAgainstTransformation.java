package theblockbox.huntersdream.util.effective_against_transformation;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;

public class EntityEffectiveAgainstTransformation extends EffectiveAgainstTransformation<Entity> {
	private static final Set<EntityEffectiveAgainstTransformation> OBJECTS = new HashSet<>();

	private EntityEffectiveAgainstTransformation(Predicate<Entity> isForObject, boolean effectiveAgainstUndead,
			TEArray values) {
		super(isForObject, effectiveAgainstUndead, values);
	}

	public static EntityEffectiveAgainstTransformation of(Predicate<Entity> isForObject, boolean effectiveAgainstUndead,
			TEArray values) {
		EntityEffectiveAgainstTransformation eeat = new EntityEffectiveAgainstTransformation(isForObject,
				effectiveAgainstUndead, values);
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
