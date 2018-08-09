package theblockbox.huntersdream.util.interfaces.transformation;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import theblockbox.huntersdream.util.annotations.CapabilityInterface;
import theblockbox.huntersdream.util.enums.Transformations;
import theblockbox.huntersdream.util.helpers.TransformationHelper;;

/**
 * ITransform for players (players can have xp)
 */
@CapabilityInterface
public interface ITransformationPlayer extends ITransformation {
	public int getXP();

	/**
	 * Use {@link TransformationHelper#setXP(EntityPlayerMP, int)} for automatic
	 * packets and level up messages
	 */
	public void setXP(int xp);

	// getTextureIndex() already defined in ITransformation

	public void setTextureIndex(int textureIndex);

	public static class TransformationPlayer implements ITransformationPlayer {
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

	public static class TransformationPlayerStorage implements IStorage<ITransformationPlayer> {
		public static final String TRANSFORMED = "transformed";
		public static final String XP = "xp";
		public static final String TRANSFORMATION = "transformation";
		public static final String TEXTURE_INDEX = "textureindex";

		@Override
		public NBTBase writeNBT(Capability<ITransformationPlayer> capability, ITransformationPlayer instance,
				EnumFacing side) {
			NBTTagCompound compound = new NBTTagCompound();
			compound.setBoolean(TRANSFORMED, instance.transformed());
			compound.setInteger(XP, instance.getXP());
			compound.setString(TRANSFORMATION, instance.getTransformation().toString());
			compound.setInteger(TEXTURE_INDEX, instance.getTextureIndex());
			return compound;
		}

		@Override
		public void readNBT(Capability<ITransformationPlayer> capability, ITransformationPlayer instance,
				EnumFacing side, NBTBase nbt) {
			NBTTagCompound compound = (NBTTagCompound) nbt;
			instance.setTransformed(compound.getBoolean(TRANSFORMED));
			instance.setXP(compound.getInteger(XP));
			instance.setTransformation(Transformations.fromName(compound.getString(TRANSFORMATION)));
			instance.setTextureIndex(compound.getInteger(TEXTURE_INDEX));
		}
	}
}
