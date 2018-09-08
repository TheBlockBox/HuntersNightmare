package theblockbox.huntersdream.util.interfaces.transformation;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import theblockbox.huntersdream.util.annotations.CapabilityInterface;
import theblockbox.huntersdream.util.enums.Transformations;
import theblockbox.huntersdream.util.helpers.GeneralHelper;

/**
 * This interface is for creatures that can transform (for the capability)
 */
@CapabilityInterface
public interface ITransformationCreature extends ITransformation {
	public Transformations[] getTransformationsNotImmuneTo();

	default public void setTransformationsNotImmuneTo(Transformations... transformationsNotImmuneTo) {
		throw new UnsupportedOperationException("Can't set transformations not immune to");
	}

	default public boolean notImmuneToTransformation(Transformations transformation) {
		for (Transformations t : getTransformationsNotImmuneTo())
			if (t == transformation)
				return true;
		return false;
	}

	@Override
	default boolean transformed() {
		return false;
	}

	@Override
	default void setTransformed(boolean transformed) {
		throw new UnsupportedOperationException("Entity is always not transformed");
	}

	public static class TransformationCreature implements ITransformationCreature {
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

	public static class TransformationCreatureStorage implements IStorage<ITransformationCreature> {
		public static final String TEXTURE_INDEX = "textureindex";
		public static final String TRANSFORMATION = "transformation";
		public static final String TRANSFORMATIONS_NOT_IMMUNE_TO = "notimmuneto";

		@Override
		public NBTBase writeNBT(Capability<ITransformationCreature> capability, ITransformationCreature instance,
				EnumFacing side) {
			NBTTagCompound compound = new NBTTagCompound();
			compound.setInteger(TEXTURE_INDEX, instance.getTextureIndex());
			compound.setString(TRANSFORMATION, instance.getTransformation().toString());
			GeneralHelper.writeArrayToNBT(compound, instance.getTransformationsNotImmuneTo(),
					TRANSFORMATIONS_NOT_IMMUNE_TO, Transformations::toString);
			return compound;
		}

		@Override
		public void readNBT(Capability<ITransformationCreature> capability, ITransformationCreature instance,
				EnumFacing side, NBTBase nbt) {
			NBTTagCompound compound = (NBTTagCompound) nbt;
			instance.setTextureIndex(compound.getInteger(TEXTURE_INDEX));
			instance.setTransformation(Transformations.fromName(compound.getString(TRANSFORMATION)));
			instance.setTransformationsNotImmuneTo(GeneralHelper.readArrayFromNBT(compound,
					TRANSFORMATIONS_NOT_IMMUNE_TO, Transformations::fromName, Transformations[]::new));
		}
	}
}
