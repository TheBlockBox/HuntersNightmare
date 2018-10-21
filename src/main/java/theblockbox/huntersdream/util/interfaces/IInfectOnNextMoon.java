package theblockbox.huntersdream.util.interfaces;

import org.apache.commons.lang3.Validate;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import theblockbox.huntersdream.init.TransformationInit;
import theblockbox.huntersdream.util.Transformation;
import theblockbox.huntersdream.util.annotations.CapabilityInterface;

@CapabilityInterface
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
	 * {@link Transformation#WEREWOLF} or {@link Transformation#HUMAN}
	 */
	public Transformation getInfectionTransformation();

	public void setInfectionTransformation(Transformation transformation);

	default public boolean isInfected() {
		return !(this.getInfectionStatus() == InfectionStatus.NOT_INFECTED);
	}

	public static class InfectOnNextMoon implements IInfectOnNextMoon {
		private InfectionStatus status = InfectionStatus.NOT_INFECTED;
		private int infectionTick = -1;
		private Transformation infectionTransformation = TransformationInit.HUMAN;

		@Override
		public InfectionStatus getInfectionStatus() {
			return this.status;
		}

		@Override
		public void setInfectionStatus(InfectionStatus status) {
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

	public static class InfectOnNextMoonStorage implements IStorage<IInfectOnNextMoon> {
		public static final String INFECTION_STATUS = "infectionstatus";
		public static final String INFECTION_TICK = "infectiontick";
		public static final String INFECTION_TRANSFORMTION = "infectiontransformation";

		@Override
		public NBTBase writeNBT(Capability<IInfectOnNextMoon> capability, IInfectOnNextMoon instance, EnumFacing side) {
			NBTTagCompound compound = new NBTTagCompound();
			compound.setString(INFECTION_STATUS, instance.getInfectionStatus().toString());
			compound.setInteger(INFECTION_TICK, instance.getInfectionTick());
			compound.setString(INFECTION_TRANSFORMTION, instance.getInfectionTransformation().toString());
			return compound;
		}

		@Override
		public void readNBT(Capability<IInfectOnNextMoon> capability, IInfectOnNextMoon instance, EnumFacing side,
				NBTBase nbt) {
			NBTTagCompound compound = (NBTTagCompound) nbt;
			instance.setInfectionStatus(InfectionStatus.fromString(compound.getString(INFECTION_STATUS)));
			instance.setInfectionTick(compound.getInteger(INFECTION_TICK));
			instance.setInfectionTransformation(Transformation.fromName(compound.getString(INFECTION_TRANSFORMTION)));
		}
	}
}
