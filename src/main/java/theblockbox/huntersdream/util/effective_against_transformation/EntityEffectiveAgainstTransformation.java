package theblockbox.huntersdream.util.effective_against_transformation;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import theblockbox.huntersdream.util.collection.TransformationToFloatMap;

public class EntityEffectiveAgainstTransformation extends EffectiveAgainstTransformation<Entity> {
	private static final Set<EntityEffectiveAgainstTransformation> OBJECTS = new HashSet<>();

	public EntityEffectiveAgainstTransformation(Predicate<Entity> isForObject, boolean effectiveAgainstUndead,
			TransformationToFloatMap effectivenessMap) {
		super(isForObject, effectiveAgainstUndead, effectivenessMap);
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

	@Override
	public boolean register() {
		return OBJECTS.add(this);
	}
}
