package theblockbox.huntersdream.util.interfaces;

import org.apache.commons.lang3.Validate;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import theblockbox.huntersdream.api.Transformation;
import theblockbox.huntersdream.util.annotations.CapabilityInterface;

@CapabilityInterface
public interface IInfectOnNextMoon {
	public enum InfectionStatus {
		NOT_INFECTED, MOON_ON_INFECTION, AFTER_INFECTION;

		public static IInfectOnNextMoon.InfectionStatus fromString(String string) {
			for (IInfectOnNextMoon.InfectionStatus status : IInfectOnNextMoon.InfectionStatus.values()) {
				if (status.toString().equals(string))
					return status;
			}
			throw new NullPointerException(
					"The given string " + string + " does not have an InfectionStatus with the same name");
		}
	}

	public IInfectOnNextMoon.InfectionStatus getInfectionStatus();

	public void setInfectionStatus(IInfectOnNextMoon.InfectionStatus status);

	public int getInfectionTick();

	public void setInfectionTick(int tick);

	/**
	 * This interface is only for werewolves so this should either return
	 * {@link Transformation#WEREWOLF} or {@link Transformation#HUMAN}
	 */
	public Transformation getInfectionTransformation();

	public void setInfectionTransformation(Transformation transformation);

	public default boolean isInfected() {
		return this.getInfectionStatus() != IInfectOnNextMoon.InfectionStatus.NOT_INFECTED;
	}

	public static class InfectOnNextMoon implements IInfectOnNextMoon {
		private IInfectOnNextMoon.InfectionStatus status = IInfectOnNextMoon.InfectionStatus.NOT_INFECTED;
		private int infectionTick = -1;
		private Transformation infectionTransformation = Transformation.HUMAN;

		@Override
		public IInfectOnNextMoon.InfectionStatus getInfectionStatus() {
			return this.status;
		}

		@Override
		public void setInfectionStatus(IInfectOnNextMoon.InfectionStatus status) {
			this.status = status;
		}

		@Override
		public int getInfectionTick() {
			return this.infectionTick;
		}

		@Override
		public void setInfectionTick(int tick) {
			this.infectionTick = tick;
		}

		@Override
		public Transformation getInfectionTransformation() {
			return this.infectionTransformation;
		}

		@Override
		public void setInfectionTransformation(Transformation transformation) {
			Validate.notNull(transformation, "The transformation isn't allowed to be null");
			this.infectionTransformation = transformation;
		}
	}

	public static class InfectOnNextMoonStorage implements Capability.IStorage<IInfectOnNextMoon> {
		public static final String INFECTION_STATUS = "infectionstatus";
		public static final String INFECTION_TICK = "infectiontick";
		public static final String INFECTION_TRANSFORMTION = "infectiontransformation";

		@Override
		public NBTBase writeNBT(Capability<IInfectOnNextMoon> capability, IInfectOnNextMoon instance, EnumFacing side) {
			NBTTagCompound compound = new NBTTagCompound();
			compound.setString(IInfectOnNextMoon.InfectOnNextMoonStorage.INFECTION_STATUS, instance.getInfectionStatus().toString());
			compound.setInteger(IInfectOnNextMoon.InfectOnNextMoonStorage.INFECTION_TICK, instance.getInfectionTick());
			compound.setString(IInfectOnNextMoon.InfectOnNextMoonStorage.INFECTION_TRANSFORMTION, instance.getInfectionTransformation().toString());
			return compound;
		}

		@Override
		public void readNBT(Capability<IInfectOnNextMoon> capability, IInfectOnNextMoon instance, EnumFacing side,
				NBTBase nbt) {
			NBTTagCompound compound = (NBTTagCompound) nbt;
			instance.setInfectionStatus(IInfectOnNextMoon.InfectionStatus.fromString(compound.getString(IInfectOnNextMoon.InfectOnNextMoonStorage.INFECTION_STATUS)));
			instance.setInfectionTick(compound.getInteger(IInfectOnNextMoon.InfectOnNextMoonStorage.INFECTION_TICK));
			instance.setInfectionTransformation(Transformation.fromName(compound.getString(IInfectOnNextMoon.InfectOnNextMoonStorage.INFECTION_TRANSFORMTION)));
		}
	}
}
