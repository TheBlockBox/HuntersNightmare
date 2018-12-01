package theblockbox.huntersdream.util;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import net.minecraft.util.ResourceLocation;
import theblockbox.huntersdream.util.collection.TransformationSet;

/** Represents a skill (like speed) */
// W.I.P.
public class Skill {
	private static final Map<String, Skill> SKILLS = new HashMap<>();
	private final ResourceLocation resourceLocation;
	private final TransformationSet forTransformations;

	public Skill(ResourceLocation resourceLocation, TransformationSet forTransformations) {
		this.resourceLocation = resourceLocation;
		this.forTransformations = forTransformations.clone();
		SKILLS.put(resourceLocation.toString(), this);
	}

	public static Collection<Skill> getAllSkills() {
		return Collections.unmodifiableCollection(SKILLS.values());
	}

	/**
	 * Tries to get a Skill with the given string. Returns null if no Skill was
	 * found
	 */
	@Nullable
	public static Skill fromName(String name) {
		return SKILLS.get(name);
	}

	/**
	 * Does exactly the same as {@link #fromName(String)} except that it accepts a
	 * ResourceLocation.
	 * 
	 * @see #fromName(String)
	 */
	@Nullable
	public static Skill fromName(ResourceLocation resourceLocation) {
		return fromName(resourceLocation.toString());
	}

	public Set<Transformation> getTransformations() {
		return Collections.unmodifiableSet(this.forTransformations);
	}

	public Transformation[] getTransformationsAsArray() {
		return this.forTransformations.toArray();
	}

	public boolean isForTransformation(Transformation transformation) {
		return this.forTransformations.contains(transformation);
	}

	@Override
	public String toString() {
		return this.resourceLocation.toString();
	}
}