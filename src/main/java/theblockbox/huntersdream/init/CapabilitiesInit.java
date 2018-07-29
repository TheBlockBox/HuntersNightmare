package theblockbox.huntersdream.init;

import java.util.concurrent.Callable;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import theblockbox.huntersdream.Main;
import theblockbox.huntersdream.util.enums.Transformations;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformationCreature;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformationPlayer;

public class CapabilitiesInit {
	@CapabilityInject(ITransformationPlayer.class)
	public static final Capability<ITransformationPlayer> CAPABILITY_TRANSFORMATION_PLAYER = null;
	@CapabilityInject(ITransformationCreature.class)
	public static final Capability<ITransformationCreature> CAPABILITY_TRANSFORMATION_CREATURE = null;

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
	}

	private static class TransformationPlayer implements ITransformationPlayer {
		private boolean transformed = false;
		private int transformationInt = 0;
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
		public int getTransformationInt() {
			return transformationInt;
		}

		@Override
		public void setTransformationID(int id) {
			this.transformationInt = id;
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
			compound.setInteger("transformationID", instance.getTransformationInt());
			compound.setInteger("textureIndex", instance.getTextureIndex());
			return compound;
		}

		@Override
		public void readNBT(Capability<ITransformationPlayer> capability, ITransformationPlayer instance,
				EnumFacing side, NBTBase nbt) {
			if (nbt instanceof NBTTagCompound) {
				NBTTagCompound compound = (NBTTagCompound) nbt;
				instance.setTransformationID(compound.getInteger("transformationid"));
				instance.setXP(compound.getInteger("xp"));
				instance.setTransformed(compound.getBoolean("transformed"));
				instance.setTextureIndex(compound.getInteger("textureIndex"));
			}
		}
	}

	private static class TransformationCreature implements ITransformationCreature {
		private int textureIndex = Main.RANDOM.nextInt(getCurrentTransformation().TEXTURES.length);
		private int[] transformationsNotImmuneTo = new int[0];
		private int transformation = Transformations.HUMAN.ID;

		@Override
		public int getTextureIndex() {
			return this.textureIndex;
		}

		@Override
		public Transformations[] getTransformationsNotImmuneTo() {
			Transformations[] transformations = new Transformations[transformationsNotImmuneTo.length];
			for (int i = 0; i < transformationsNotImmuneTo.length; i++) {
				transformations[i] = Transformations.fromID(transformationsNotImmuneTo[i]);
			}
			return transformations;
		}

		@Override
		public void setTransformationsNotImmuneTo(int... transformationsNotImmuneTo) {
			this.transformationsNotImmuneTo = transformationsNotImmuneTo;
		}

		@Override
		public int getCurrentTransformationInt() {
			return this.transformation;
		}

		@Override
		public void setCurrentTransformationInt(int transformation) {
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
			compound.setInteger("textureIndex", instance.getTextureIndex());
			compound.setInteger("transformation", instance.getCurrentTransformationInt());
			compound.setIntArray("transformationidsnotimmuneto", instance.getTransformationIntsNotImmuneTo());
			return compound;
		}

		@Override
		public void readNBT(Capability<ITransformationCreature> capability, ITransformationCreature instance,
				EnumFacing side, NBTBase nbt) {
			if (nbt instanceof NBTTagCompound) {
				NBTTagCompound compound = (NBTTagCompound) nbt;
				instance.setTextureIndex(compound.getInteger("textureIndex"));
				instance.setCurrentTransformationInt(compound.getInteger("transformation"));
				instance.setTransformationsNotImmuneTo(compound.getIntArray("transformationidsnotimmuneto"));
			}
		}

	}
}
