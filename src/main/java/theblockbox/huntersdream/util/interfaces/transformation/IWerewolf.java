package theblockbox.huntersdream.util.interfaces.transformation;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import theblockbox.huntersdream.init.TransformationInit;
import theblockbox.huntersdream.util.Transformation;
import theblockbox.huntersdream.util.annotations.CapabilityInterface;
import theblockbox.huntersdream.util.helpers.WerewolfHelper;

/** For player werewolves */
@CapabilityInterface
public interface IWerewolf {
	/**
	 * Returns true when the player is currently in werewolf form, otherwise returns
	 * false
	 * 
	 * @deprecated Prefer to call
	 *             {@link WerewolfHelper#isTransformed(EntityLivingBase)} or
	 *             {@link WerewolfHelper#isTransformedWerewolf(EntityLivingBase)}
	 *             instead to get a version that works with all entities extending
	 *             EntityLivingBase. Try to avoid calling this method.
	 */
	@Deprecated
	public boolean isTransformed();

	public void setTransformed(boolean transformed);

	/** Returns the player.ticksExisted when the transformation has started */
	public int getTimeSinceTransformation();

	public void setTimeSinceTransformation(int time);

	/** 0 is no stage and 6 is transformed */
	public int getTransformationStage();

	public void setTransformationStage(int stage);

	default public Transformation getTransformation() {
		return TransformationInit.WEREWOLF;
	}

	public static class Werewolf implements IWerewolf {
		private boolean transformed = false;
		private int timeSinceTransformation = -1;
		private int transformationStage = 0;

		@Override
		public boolean isTransformed() {
			return this.transformed;
		}

		@Override
		public void setTransformed(boolean transformed) {
			this.transformed = transformed;
		}

		@Override
		public void setTimeSinceTransformation(int time) {
			this.timeSinceTransformation = time;
		}

		@Override
		public int getTimeSinceTransformation() {
			return this.timeSinceTransformation;
		}

		@Override
		public int getTransformationStage() {
			return this.transformationStage;
		}

		@Override
		public void setTransformationStage(int stage) {
			this.transformationStage = stage;
		}
	}

	public static class WerewolfStorage implements IStorage<IWerewolf> {
		public static final String TRANSFORMED = "transformed";
		public static final String TIME_SINCE_TRANSFORMATION = "timesincetransformation";
		public static final String TRANSFORMATION_STAGE = "transformationstage";

		@Override
		public NBTBase writeNBT(Capability<IWerewolf> capability, IWerewolf instance, EnumFacing side) {
			NBTTagCompound compound = new NBTTagCompound();
			compound.setInteger(TIME_SINCE_TRANSFORMATION, instance.getTimeSinceTransformation());
			compound.setInteger(TRANSFORMATION_STAGE, instance.getTransformationStage());
			compound.setBoolean(TRANSFORMED, instance.isTransformed());
			return compound;
		}

		@Override
		public void readNBT(Capability<IWerewolf> capability, IWerewolf instance, EnumFacing side, NBTBase nbt) {
			NBTTagCompound compound = (NBTTagCompound) nbt;
			instance.setTimeSinceTransformation(compound.getInteger(TIME_SINCE_TRANSFORMATION));
			instance.setTransformationStage(compound.getInteger(TRANSFORMATION_STAGE));
			instance.setTransformed(compound.getBoolean(TRANSFORMED));
		}
	}
}
