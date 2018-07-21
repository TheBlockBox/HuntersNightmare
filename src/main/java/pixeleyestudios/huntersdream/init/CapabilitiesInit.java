package pixeleyestudios.huntersdream.init;

import java.util.concurrent.Callable;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import pixeleyestudios.huntersdream.util.interfaces.ITransformationPlayer;

public class CapabilitiesInit {
	@CapabilityInject(ITransformationPlayer.class)
	public static final Capability<ITransformationPlayer> CAPABILITY_TRANSFORMATION_PLAYER = null;

	public static void registerCapabilities() {
		CapabilityManager.INSTANCE.register(ITransformationPlayer.class, new TransformationPlayerStorage(),
				new Callable<ITransformationPlayer>() {

					@Override
					public ITransformationPlayer call() throws Exception {
						return new TransformationPlayer();
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
				instance.setTransformationID(compound.getInteger("transformationID"));
				instance.setXP(compound.getInteger("xp"));
				instance.setTransformed(compound.getBoolean("transformed"));
				instance.setTextureIndex(compound.getInteger("textureIndex"));
			}
		}
	}
}
