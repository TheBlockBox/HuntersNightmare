package theblockbox.huntersdream.event;

import static theblockbox.huntersdream.init.TransformationInit.CLOCKWORKANDROID;
import static theblockbox.huntersdream.init.TransformationInit.HUMAN;
import static theblockbox.huntersdream.init.TransformationInit.HUNTER;
import static theblockbox.huntersdream.init.TransformationInit.HYBRID;
import static theblockbox.huntersdream.init.TransformationInit.NONE;
import static theblockbox.huntersdream.init.TransformationInit.VAMPIRE;
import static theblockbox.huntersdream.init.TransformationInit.WEREWOLF;
import static theblockbox.huntersdream.init.TransformationInit.WITCH;

import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.Validate;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import theblockbox.huntersdream.util.Transformation;

/**
 * Called when transformations are registered. The event is posted on the
 * {@link MinecraftForge#EVENT_BUS} and is not cancelable
 */
public class TransformationRegistryEvent extends Event {
	private final Set<Transformation> transformationSet = new LinkedHashSet<>();

	public TransformationRegistryEvent() {
		this.registerTransformations(NONE, HUMAN, WEREWOLF, VAMPIRE, WITCH, CLOCKWORKANDROID, HYBRID, HUNTER);
	}

	/**
	 * Adds a transformations to be registered. Returns true if the adding was
	 * successful (meaning that the transformations hasn't already been registered).
	 * 
	 * @throws IllegalArgumentException If a transformation has already been
	 *                                  registered with the same registry name
	 */
	public boolean registerTransformation(Transformation transformation) {
		Validate.notNull(transformation);
		transformationSet.stream().map(Transformation::getRegistryName).forEach(name -> {
			if (transformation.getRegistryName().equals(name))
				throw new IllegalArgumentException(
						"Found duplicate registry name \"" + name + "\" while registering transformations");
		});
		return transformationSet.add(transformation);
	}

	/**
	 * Does the same as {@link #registerTransformation(Transformation)} except that
	 * it registers more than one
	 */
	public boolean registerTransformations(Transformation... transformations) {
		Validate.noNullElements(transformations,
				"Null transformations not allowed (null transformation in array %s at index %d)",
				ArrayUtils.toString(transformations));
		boolean flag = false;
		for (Transformation transformation : transformations)
			if (registerTransformation(transformation) && !flag)
				flag = true;
		return flag;
	}

	/** Returns all transformations that have been registered yet */
	public Transformation[] getTransformations() {
		return transformationSet.toArray(new Transformation[0]);
	}
}
