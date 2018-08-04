package theblockbox.huntersdream.util.interfaces;

import theblockbox.huntersdream.util.enums.Transformations;

public interface IInfectInTicks {
	/** get time in ticks set until infection (final value) */
	public int getTime();

	public void setTime(int time);

	public int getTimeUntilInfection();

	public void setTimeUntilInfection(int time);

	public int getInfectionTransformationID();

	public void setInfectionTransformationID(int id);

	default public Transformations getInfectionTransformation() {
		return Transformations.fromID(getInfectionTransformationID());
	}

	default public void setInfectionTransformation(Transformations transformation) {
		this.setInfectionTransformationID(transformation.getID());
	}

	public boolean currentlyInfected();

	public void setCurrentlyInfected(boolean infected);
}
