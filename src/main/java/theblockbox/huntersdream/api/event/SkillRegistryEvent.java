package theblockbox.huntersdream.api.event;

import static theblockbox.huntersdream.api.Skill.ARMOR_0;
import static theblockbox.huntersdream.api.Skill.ARMOR_1;
import static theblockbox.huntersdream.api.Skill.ARMOR_2;
import static theblockbox.huntersdream.api.Skill.BITE_0;
import static theblockbox.huntersdream.api.Skill.BITE_1;
import static theblockbox.huntersdream.api.Skill.BITE_2;
import static theblockbox.huntersdream.api.Skill.JUMP_0;
import static theblockbox.huntersdream.api.Skill.JUMP_1;
import static theblockbox.huntersdream.api.Skill.JUMP_2;
import static theblockbox.huntersdream.api.Skill.SPEED_0;
import static theblockbox.huntersdream.api.Skill.SPEED_1;
import static theblockbox.huntersdream.api.Skill.SPEED_2;
import static theblockbox.huntersdream.api.Skill.UNARMED_0;
import static theblockbox.huntersdream.api.Skill.UNARMED_1;
import static theblockbox.huntersdream.api.Skill.UNARMED_2;
import static theblockbox.huntersdream.api.Skill.WILFUL_TRANSFORMATION;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.Validate;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;
import theblockbox.huntersdream.api.Skill;

/**
 * Called when skills should be registered. The event is posted on the
 * {@link MinecraftForge#EVENT_BUS} and is not cancelable.
 */
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
			if (registerSkill(skill) && !flag)
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