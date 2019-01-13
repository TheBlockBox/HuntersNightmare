package theblockbox.huntersdream.api.event;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.Validate;
import theblockbox.huntersdream.api.Skill;

import java.util.HashMap;
import java.util.Map;

import static theblockbox.huntersdream.init.SkillInit.*;

/**
 * SkillRegistryEvent is fired when the skills are being registered. <br>
 * <br>
 * This event is not {@link Cancelable}.<br>
 * <br>
 * This event does not have a result.
 * {@link net.minecraftforge.fml.common.eventhandler.Event.HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
public class SkillRegistryEvent extends Event {
	private final Map<String, Skill> skillMap = new HashMap<>();

	public SkillRegistryEvent() {
		this.registerSkills(BITE_0, BITE_1, BITE_2, SPEED_0, SPEED_1, SPEED_2, JUMP_0, JUMP_1, JUMP_2, UNARMED_0,
				UNARMED_1, UNARMED_2, ARMOR_0, ARMOR_1, ARMOR_2, WILFUL_TRANSFORMATION);
	}

	/**
	 * Registers a skill. Returns true if the adding was successful (meaning that
	 * the skill hasn't already been registered).
	 * 
	 * @throws IllegalArgumentException If a skill has already been registered with
	 *                                  the same registry name
	 * @see #registerSkills(Skill...)
	 */
	// TODO: Remove the boolean return type
	public boolean registerSkill(Skill skill) throws IllegalArgumentException {
		Validate.notNull(skill);
		String registryName = skill.toString();
		Skill s = this.skillMap.get(registryName);
		if (s != null) {
			throw new IllegalArgumentException(
					"Found duplicate registry name \"" + registryName + "\" while registering skills");
		}
		return this.skillMap.put(skill.toString(), skill) == null;
	}

	/**
	 * Does the same as {@link #registerSkill(Skill)} except that it registers
	 * multiple skills at once.
	 * 
	 * @throws IllegalArgumentException If a skill has already been registered with
	 *                                  the same registry name
	 * @see #registerSkill(Skill)
	 */
	public boolean registerSkills(Skill... skills) throws IllegalArgumentException {
		Validate.noNullElements(skills, "Null skills not allowed (null skill in array %s at index %d)",
				ArrayUtils.toString(skills));
		boolean flag = false;
		for (Skill skill : skills)
			if (this.registerSkill(skill) && !flag)
				flag = true;
		return flag;
	}

	/**
	 * Returns a map of registry names and their corresponding skills. (Only
	 * contains skills that have already been registered.)
	 */
	public Map<String, Skill> getSkills() {
		return new HashMap<>(this.skillMap);
	}
}