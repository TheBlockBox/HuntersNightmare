package theblockbox.huntersdream.api;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnegative;
import javax.annotation.Nullable;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.Validate;

import com.google.common.base.Preconditions;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import theblockbox.huntersdream.util.collection.TransformationSet;
import theblockbox.huntersdream.util.helpers.TransformationHelper;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformationPlayer;

/**
 * Represents a skill (like speed, jump etc.) for one or more transformations
 * that can be unlocked by players.
 */
public class Skill {
	private static final Map<String, Skill> SKILLS = new HashMap<>();
	private final ResourceLocation registryName;
	private final int neededExperienceLevels;
	private final TransformationSet forTransformations;
	private final Skill[] requiredSkills;

	// Hunter's Dream Skills
	// TODO: Add skills here

	/**
	 * Creates a new Skill instance with the given arguments.
	 * 
	 * @param registryName           A unique {@link ResourceLocation} for this
	 *                               skill.
	 * @param forTransformations     A {@link TransformationSet} that contains all
	 *                               Transformations that should be able to unlock
	 *                               this skill. Mustn't be null or empty.
	 * @param neededExperienceLevels The levels that are removed from the player
	 *                               when the skill is unlocked. Is not allowed to
	 *                               be negative.
	 * @param requiredSkills         An array of Skills that a player needs to have
	 *                               unlocked before this Skill can be unlocked.
	 * @throws IllegalArgumentException If the forTransformations argument is null
	 *                                  or empty or if a Skill with the same
	 *                                  registry name already exists.
	 */
	public Skill(ResourceLocation registryName, @Nonnegative int neededExperienceLevels,
			TransformationSet forTransformations, Skill... requiredSkills) {
		this.registryName = registryName;
		Preconditions.checkArgument(neededExperienceLevels >= 0,
				"The argument neededExperienceLevels should be positive but had the value %s", neededExperienceLevels);
		this.neededExperienceLevels = neededExperienceLevels;
		String registryNameString = registryName.toString();
		Validate.isTrue(SKILLS.get(registryNameString) == null,
				"The skill \"" + registryNameString + "\" has already been registered");
		Validate.isTrue(!forTransformations.isEmpty(), "The skill \"" + registryNameString
				+ "\" should have at least one Transformation in the TransformationSet");
		this.forTransformations = forTransformations.clone();
		this.requiredSkills = requiredSkills;
		SKILLS.put(registryNameString, this);
	}

	/** Returns an unmodifiable collection of all skills. */
	public static Collection<Skill> getAllSkills() {
		return Collections.unmodifiableCollection(SKILLS.values());
	}

	/**
	 * Tries to get a Skill with the given string. Returns null if no Skill was
	 * found.
	 * 
	 * @see #fromRegistryName(ResourceLocation)
	 * @see #getRegistryName()
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
	 * @see #toString()
	 */
	@Nullable
	public static Skill fromRegistryName(ResourceLocation registryName) {
		return fromName(registryName.toString());
	}

	/**
	 * Returns the transformations that can unlock this Skill as a set.
	 * 
	 * @see #getTransformationsAsArray()
	 * @see #isForTransformation(Transformation)
	 */
	public Set<Transformation> getTransformations() {
		return Collections.unmodifiableSet(this.forTransformations);
	}

	/**
	 * Returns the transformations that can unlock this Skill as an array.
	 * 
	 * @see #getTransformations()
	 * @see #isForTransformation(Transformation)
	 */
	public Transformation[] getTransformationsAsArray() {
		return this.forTransformations.toArray();
	}

	/**
	 * Returns the xp levels that will be removed from the player when this Skill is
	 * unlocked.
	 */
	public int getNeededExperienceLevels() {
		return this.neededExperienceLevels;
	}

	/**
	 * Returns true if the given transformation can unlock this Skill.
	 * 
	 * @see #getTransformations()
	 * @see #getTransformationsAsArray()
	 */
	public boolean isForTransformation(Transformation transformation) {
		return this.forTransformations.contains(transformation);
	}

	/**
	 * Returns the Skills that have to be unlocked so that this Skill can be
	 * unlocked, too.
	 * 
	 * @see #requiresSkill(Skill)
	 * @see #doesPlayerHaveRequiredSkills(EntityPlayer)
	 */
	public Skill[] getRequiredSkills() {
		return (this.requiredSkills.length > 0) ? this.requiredSkills.clone() : this.requiredSkills;
	}

	/**
	 * Returns true if the given Skill is required to unlock this Skill.
	 * 
	 * @see #getRequiredSkills()
	 * @see #doesPlayerHaveRequiredSkills(EntityPlayer)
	 */
	public boolean requiresSkill(Skill skill) {
		return ArrayUtils.contains(this.requiredSkills, skill);
	}

	/**
	 * Returns true when the given player has all Skills that are required to unlock
	 * this one.
	 * 
	 * @see #getRequiredSkills()
	 * @see #requiresSkill(Skill)
	 */
	public boolean doesPlayerHaveRequiredSkills(EntityPlayer player) {
		ITransformationPlayer transformation = TransformationHelper.getITransformationPlayer(player);
		// loop through all skills
		for (Skill s : this.requiredSkills)
			// if the player does not have one skill,
			if (!transformation.hasSkill(s))
				// return false
				return false;
		// if the player has all skills, return true
		return true;
	}

	/**
	 * Returns the Skill's registry name that is used to save and load it with
	 * {@link #fromRegistryName(ResourceLocation)}.
	 * 
	 * @see #fromRegistryName(ResourceLocation)
	 */
	public ResourceLocation getRegistryName() {
		return this.registryName;
	}

	/**
	 * Returns a String representation of the Skill's registry name.
	 * 
	 * @see #getRegistryName()
	 */
	@Override
	public String toString() {
		return this.registryName.toString();
	}
}