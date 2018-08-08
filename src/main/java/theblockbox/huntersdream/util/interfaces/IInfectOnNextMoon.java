package theblockbox.huntersdream.util.interfaces;

import theblockbox.huntersdream.util.enums.Transformations;

public interface IInfectOnNextMoon {
	public enum InfectionStatus {
		NOT_INFECTED, MOON_ON_INFECTION, AFTER_INFECTION;

		public static InfectionStatus fromString(String string) {
			for (InfectionStatus status : values()) {
				if (status.toString().equals(string))
					return status;
			}
			throw new NullPointerException(
					"The given string " + string + " does not have an InfectionStatus with the same name");
		}
	}

	public InfectionStatus getInfectionStatus();

	public void setInfectionStatus(InfectionStatus status);

	public int getInfectionTick();

	public void setInfectionTick(int tick);

	/**
	 * This interface is only for werewolves so this should either return
	 * {@link Transformations#WEREWOLF} or {@link Transformations#HUMAN}
	 */
	public Transformations getInfectionTransformation();

	public void setInfectionTransformation(Transformations transformation);

	default public boolean isInfected() {
		return !(this.getInfectionStatus() == InfectionStatus.NOT_INFECTED);
	}
}
