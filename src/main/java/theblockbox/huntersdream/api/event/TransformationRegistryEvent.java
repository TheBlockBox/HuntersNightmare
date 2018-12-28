package theblockbox.huntersdream.api.event;

import static theblockbox.huntersdream.api.Transformation.HUMAN;
import static theblockbox.huntersdream.api.Transformation.HUNTER;
import static theblockbox.huntersdream.api.Transformation.NONE;
import static theblockbox.huntersdream.api.Transformation.VAMPIRE;
import static theblockbox.huntersdream.api.Transformation.WEREWOLF;

import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.Validate;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;
import theblockbox.huntersdream.api.Transformation;

/**
 * TransformationRegistryEvent is fired when the transformations are being
 * registered. <br>
 * <br>
 * This event is not {@link Cancelable}.<br>
 * <br>
 * This event does not have a result.
 * {@link net.minecraftforge.fml.common.eventhandler.Event.HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
public class TransformationRegistryEvent extends Event {
	private final Set<Transformation> transformationSet = new LinkedHashSet<>();

	public TransformationRegistryEvent() {
		this.registerTransformations(NONE, HUMAN, WEREWOLF, VAMPIRE, HUNTER);
	}

	/**
	 * Registers a transformation. Returns true if the adding was successful
	 * (meaning that the transformation hasn't already been registered).
	 * 
	 * @throws IllegalArgumentException If a transformation has already been
	 *                                  registered with the same registry name
	 */
	public boolean registerTransformation(Transformation transformation) {
		Validate.notNull(transformation);
		for (Transformation t : this.transformationSet) {
			ResourceLocation registryName = t.getRegistryName();
			if (transformation.getRegistryName().equals(registryName)) {
				throw new IllegalArgumentException(
						"Found duplicate registry name \"" + registryName + "\" while registering transformations");
			}
		}
		return this.transformationSet.add(transformation);
	}

	/**
	 * Does the same as {@link #registerTransformation(Transformation)} except that
	 * it registers multiple transformations at once.
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

	/** Returns all transformations that have been registered so far. */
	public Transformation[] getTransformations() {
		return this.transformationSet.toArray(new Transformation[0]);
	}
}
