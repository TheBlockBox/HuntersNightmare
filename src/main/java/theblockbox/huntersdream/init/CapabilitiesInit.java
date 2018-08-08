package theblockbox.huntersdream.init;

import java.util.concurrent.Callable;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import theblockbox.huntersdream.util.enums.Transformations;
import theblockbox.huntersdream.util.interfaces.IInfectInTicks;
import theblockbox.huntersdream.util.interfaces.IInfectOnNextMoon;
import theblockbox.huntersdream.util.interfaces.IInfectOnNextMoon.InfectionStatus;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformationCreature;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformationPlayer;

public class CapabilitiesInit {
	@CapabilityInject(ITransformationPlayer.class)
	public static final Capability<ITransformationPlayer> CAPABILITY_TRANSFORMATION_PLAYER = null;
	@CapabilityInject(ITransformationCreature.class)
	public static final Capability<ITransformationCreature> CAPABILITY_TRANSFORMATION_CREATURE = null;
	@CapabilityInject(IInfectInTicks.class)
	public static final Capability<IInfectInTicks> CAPABILITY_INFECT_IN_TICKS = null;
	@CapabilityInject(IInfectOnNextMoon.class)
	public static final Capability<IInfectOnNextMoon> CAPABILITY_INFECT_ON_NEXT_MOON = null;

	public static void registerCapabilities() {
		CapabilityManager.INSTANCE.register(ITransformationPlayer.class, new TransformationPlayerStorage(),
				new Callable<ITransformationPlayer>() {

					@Override
					public ITransformationPlayer call() throws Exception {
						return new TransformationPlayer();
					}
				});
		CapabilityManager.INSTANCE.register(ITransformationCreature.class, new TransformationCreatureStorage(),
				new Callable<ITransformationCreature>() {

					@Override
					public ITransformationCreature call() throws Exception {
						return new TransformationCreature();
					}
				});
		CapabilityManager.INSTANCE.register(IInfectInTicks.class, new InfectInTicksStorage(),
				new Callable<IInfectInTicks>() {

					@Override
					public IInfectInTicks call() throws Exception {
						return new InfectInTicks();
					}
				});
		CapabilityManager.INSTANCE.register(IInfectOnNextMoon.class, new InfectOnNextMoonStorage(),
				new Callable<IInfectOnNextMoon>() {

					@Override
					public IInfectOnNextMoon call() throws Exception {
						return new InfectOnNextMoon();
					}
				});
	}

	private static class TransformationPlayer implements ITransformationPlayer {
		private boolean transformed = false;
		private Transformations transformation = Transformations.HUMAN;
		private int xp = 0;
		private int textureIndex = 0;

		@Override
		public boolean transformed() {
			return transformed;
		}

		@Override
		public void setTransformed(boolean transformed) {
			this.transformed = transformed;
		}

		@Override
		public Transformations getTransformation() {
			return this.transformation;
		}

		@Override
		public void setTransformation(Transformations transformation) {
			this.transformation = transformation;
		}

		@Override
		public int getXP() {
			return this.xp;
		}

		@Override
		public void setXP(int xp) {
			this.xp = xp;
		}

		@Override
		public int getTextureIndex() {
			return textureIndex;
		}

		@Override
		public void setTextureIndex(int textureIndex) {
			this.textureIndex = textureIndex;
		}

	}

	private static class TransformationPlayerStorage implements IStorage<ITransformationPlayer> {

		@Override
		public NBTBase writeNBT(Capability<ITransformationPlayer> capability, ITransformationPlayer instance,
				EnumFacing side) {
			NBTTagCompound compound = new NBTTagCompound();
			compound.setBoolean("transformed", instance.transformed());
			compound.setInteger("xp", instance.getXP());
			compound.setString("transformation", instance.getTransformation().toString());
			compound.setInteger("textureindex", instance.getTextureIndex());
			return compound;
		}

		@Override
		public void readNBT(Capability<ITransformationPlayer> capability, ITransformationPlayer instance,
				EnumFacing side, NBTBase nbt) {
			if (nbt instanceof NBTTagCompound) {
				NBTTagCompound compound = (NBTTagCompound) nbt;
				instance.setTransformation(Transformations.fromName(compound.getString("transformed")));
				instance.setXP(compound.getInteger("xp"));
				instance.setTransformed(compound.getBoolean("transformed"));
				instance.setTextureIndex(compound.getInteger("textureindex"));
			}
		}
	}

	private static class TransformationCreature implements ITransformationCreature {
		private Transformations[] transformationsNotImmuneTo = new Transformations[] { Transformations.HUMAN };
		private Transformations transformation = Transformations.HUMAN;
		private int textureIndex = 0;

		@Override
		public int getTextureIndex() {
			return this.textureIndex;
		}

		@Override
		public Transformations[] getTransformationsNotImmuneTo() {
			return this.transformationsNotImmuneTo;
		}

		@Override
		public void setTransformationsNotImmuneTo(Transformations... transformationsNotImmuneTo) {
			this.transformationsNotImmuneTo = transformationsNotImmuneTo;
		}

		@Override
		public Transformations getTransformation() {
			return this.transformation;
		}

		@Override
		public void setTransformation(Transformations transformation) {
			this.transformation = transformation;
		}

		@Override
		public void setTextureIndex(int textureIndex) {
			this.textureIndex = textureIndex;
		}
	}

	private static class TransformationCreatureStorage implements IStorage<ITransformationCreature> {

		@Override
		public NBTBase writeNBT(Capability<ITransformationCreature> capability, ITransformationCreature instance,
				EnumFacing side) {
			NBTTagCompound compound = new NBTTagCompound();
			compound.setInteger("textureindex", instance.getTextureIndex());
			compound.setString("transformation", instance.getTransformation().toString());
			Transformations[] transformationsNotImmuneTo = instance.getTransformationsNotImmuneTo();
			// set transformations not immune to
			NBTTagCompound transformations = new NBTTagCompound();
			transformations.setInteger("length", transformationsNotImmuneTo.length);
			for (int i = 0; i < transformationsNotImmuneTo.length; i++) {
				transformations.setString("t" + i, transformationsNotImmuneTo[i].toString());
			}
			compound.setTag("transformationsnotimmuneto", transformations);
			return compound;
		}

		@Override
		public void readNBT(Capability<ITransformationCreature> capability, ITransformationCreature instance,
				EnumFacing side, NBTBase nbt) {
			if (nbt instanceof NBTTagCompound) {
				NBTTagCompound compound = (NBTTagCompound) nbt;
				instance.setTextureIndex(compound.getInteger("textureindex"));
				instance.setTransformation(Transformations.fromName(compound.getString("transformation")));
				// get transformations not immune to
				NBTTagCompound transformations = (NBTTagCompound) compound.getTag("transformationsnotimmuneto");
				int length = transformations.getInteger("length");
				Transformations[] transformationsNotImmuneTo = new Transformations[length];
				for (int i = 0; i < length; i++) {
					transformationsNotImmuneTo[i] = Transformations.fromName(transformations.getString("t" + i));
				}
				instance.setTransformationsNotImmuneTo(transformationsNotImmuneTo);
			}
		}

	}

	private static class InfectInTicks implements IInfectInTicks {
		private int time = -1;
		private int timeUntilInfection = -1;
		private Transformations infectionTransformation = Transformations.HUMAN;
		private boolean currentlyInfected = false;

		@Override
		public int getTime() {
			return this.time;
		}

		public void setTime(int time) {
			this.time = time;
			this.timeUntilInfection = time;
		}

		@Override
		public int getTimeUntilInfection() {
			return this.timeUntilInfection;
		}

		@Override
		public void setTimeUntilInfection(int timeUntilInfection) {
			this.timeUntilInfection = timeUntilInfection;
		}

		@Override
		public Transformations getInfectionTransformation() {
			return infectionTransformation;
		}

		@Override
		public void setInfectionTransformation(Transformations transformation) {
			this.infectionTransformation = transformation;
		}

		@Override
		public boolean currentlyInfected() {
			return this.currentlyInfected;
		}

		@Override
		public void setCurrentlyInfected(boolean currentlyInfected) {
			this.currentlyInfected = currentlyInfected;
		}
	}

	private static class InfectInTicksStorage implements IStorage<IInfectInTicks> {

		@Override
		public NBTBase writeNBT(Capability<IInfectInTicks> capability, IInfectInTicks instance, EnumFacing side) {
			NBTTagCompound compound = new NBTTagCompound();
			compound.setInteger("time", instance.getTime());
			compound.setInteger("timeuntilinfection", instance.getTime());
			compound.setString("infectiontransformation", instance.getInfectionTransformation().toString());
			compound.setBoolean("currentlyinfected", instance.currentlyInfected());
			return compound;
		}

		@Override
		public void readNBT(Capability<IInfectInTicks> capability, IInfectInTicks instance, EnumFacing side,
				NBTBase nbt) {
			if (nbt instanceof NBTTagCompound) {
				NBTTagCompound compound = (NBTTagCompound) nbt;
				instance.setTime(compound.getInteger("time"));
				instance.setTimeUntilInfection(compound.getInteger("timeuntilinfection"));
				instance.setInfectionTransformation(
						Transformations.fromName(compound.getString("infectiontransformation")));
				instance.setCurrentlyInfected(compound.getBoolean("currentlyinfected"));
			}
		}
	}

	private static class InfectOnNextMoon implements IInfectOnNextMoon {
		private InfectionStatus status = InfectionStatus.NOT_INFECTED;
		private int infectionTick = -1;
		private Transformations infectionTransformation = Transformations.HUMAN;

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
		public Transformations getInfectionTransformation() {
			return this.infectionTransformation;
		}

		@Override
		public void setInfectionTransformation(Transformations transformation) {
			this.infectionTransformation = transformation;
		}
	}

	private static class InfectOnNextMoonStorage implements IStorage<IInfectOnNextMoon> {

		@Override
		public NBTBase writeNBT(Capability<IInfectOnNextMoon> capability, IInfectOnNextMoon instance, EnumFacing side) {
			NBTTagCompound compound = new NBTTagCompound();
			compound.setString("infectionstatus", instance.getInfectionStatus().toString());
			compound.setInteger("infectiontick", instance.getInfectionTick());
			compound.setString("infectiontransformation", instance.getInfectionTransformation().toString());
			return compound;
		}

		@Override
		public void readNBT(Capability<IInfectOnNextMoon> capability, IInfectOnNextMoon instance, EnumFacing side,
				NBTBase nbt) {
			if (nbt instanceof NBTTagCompound) {
				NBTTagCompound compound = (NBTTagCompound) nbt;
				instance.setInfectionStatus(InfectionStatus.fromString(compound.getString("infectionstatus")));
				instance.setInfectionTick(compound.getInteger("infectiontick"));
				instance.setInfectionTransformation(
						Transformations.fromName(compound.getString("infectiontransformation")));
			}
		}

	}
}
