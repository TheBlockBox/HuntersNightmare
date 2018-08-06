package theblockbox.huntersdream.util.interfaces;

import net.minecraft.util.ResourceLocation;
import theblockbox.huntersdream.util.enums.Transformations;

public interface IInfectInTicks {
	/** get time in ticks set until infection (final value) */
	public int getTime();

	public void setTime(int time);

	public int getTimeUntilInfection();

	public void setTimeUntilInfection(int time);

	public ResourceLocation getInfectionTransformationRL();

	public void setInfectionTransformationRL(ResourceLocation rl);

	default public Transformations getInfectionTransformation() {
		return Transformations.fromResourceLocation(getInfectionTransformationRL());
	}

	default public void setInfectionTransformation(Transformations transformation) {
		this.setInfectionTransformationRL(transformation.getResourceLocation());
	}

	public boolean currentlyInfected();

	public void setCurrentlyInfected(boolean infected);
}
