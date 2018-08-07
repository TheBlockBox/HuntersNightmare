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
import theblockbox.huntersdream.util.interfaces.transformation.ITransformationCreature;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformationPlayer;

public class CapabilitiesInit {
	@CapabilityInject(ITransformationPlayer.class)
	public static final Capability<ITransformationPlayer> CAPABILITY_TRANSFORMATION_PLAYER = null;
	@CapabilityInject(ITransformationCreature.class)
	public static final Capability<ITransformationCreature> CAPABILITY_TRANSFORMATION_CREATURE = null;
	@CapabilityInject(IInfectInTicks.class)
	public static final Capability<IInfectInTicks> CAPABILITY_INFECT_IN_TICKS = null;

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
		CapabilityManager.INSTANCE.register(IInfectInTicks.class, new InfectionInTicksStorage(),
				new Callable<IInfectInTicks>() {

					@Override
					public IInfectInTicks call() throws Exception {
						return new InfectInTicks();
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

			String transformations = "";
			for (Transformations transformation : instance.getTransformationsNotImmuneTo()) {
				if (transformation == null)
					throw new NullPointerException("The transformation is null. Length of the transformations: "
							+ instance.getTransformationsNotImmuneTo().length);
				transformations.concat(transformation.toString() + "\n");
			}
			if (transformations.endsWith("\n")) {
				transformations.substring(0, transformations.length() - 2);
			}
			compound.setString("transformationidsnotimmuneto", transformations);

			return compound;
		}

		@Override
		public void readNBT(Capability<ITransformationCreature> capability, ITransformationCreature instance,
				EnumFacing side, NBTBase nbt) {
			if (nbt instanceof NBTTagCompound) {
				NBTTagCompound compound = (NBTTagCompound) nbt;
				instance.setTextureIndex(compound.getInteger("textureindex"));
				instance.setTransformation(Transformations.fromName(compound.getString("transformation")));

				String[] transformationNames = compound.getString("transformationidsnotimmuneto").split("\n");
				Transformations[] transformations = new Transformations[transformationNames.length];
				for (int i = 0; i < transformationNames.length; i++) {
					transformations[i] = Transformations.fromName(transformationNames[i]);
					if (transformations[i] == null)
						throw new NullPointerException(
								"The given string " + transformationNames[i] + " is not a transformation");

				}
				instance.setTransformationsNotImmuneTo(transformations);
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

	private static class InfectionInTicksStorage implements IStorage<IInfectInTicks> {

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
}
