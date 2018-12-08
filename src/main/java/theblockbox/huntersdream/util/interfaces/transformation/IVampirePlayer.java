package theblockbox.huntersdream.util.interfaces.transformation;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import theblockbox.huntersdream.api.Transformation;
import theblockbox.huntersdream.util.annotations.CapabilityInterface;

// TODO: Remove
/** For player vampires */
@CapabilityInterface
public interface IVampirePlayer {
	default public Transformation getTransformation() {
		return Transformation.VAMPIRE;
	}

	/** Returns a vampires current blood. One blood is half a blood drop */
	default public int getBlood() {
		return MathHelper.ceil(this.getBloodDouble());
	}

	public double getBloodDouble();

	public void setBlood(double blood);

	default public void incrementBlood() {
		this.setBlood(this.getBloodDouble() + 1.0D);
	}

	default public void decrementBlood() {
		this.setBlood(this.getBloodDouble() - 1.0D);
	}

	public int getTimeDrinking();

	public void setTimeDrinking(int time);

	public static class Vampire implements IVampirePlayer {
		private double blood = 0;
		private int timeDrinking = 0;

		@Override
		public double getBloodDouble() {
			return this.blood;
		}

		@Override
		public void setBlood(double blood) {
			this.blood = blood;
		}

		@Override
		public int getTimeDrinking() {
			return this.timeDrinking;
		}

		@Override
		public void setTimeDrinking(int time) {
			this.timeDrinking = time;
		}
	}

	public static class VampireStorage implements IStorage<IVampirePlayer> {
		public static final String BLOOD = "blood";

		@Override
		public NBTBase writeNBT(Capability<IVampirePlayer> capability, IVampirePlayer instance, EnumFacing side) {
			NBTTagCompound compound = new NBTTagCompound();
			compound.setDouble(BLOOD, instance.getBlood());
			return compound;
		}

		@Override
		public void readNBT(Capability<IVampirePlayer> capability, IVampirePlayer instance, EnumFacing side,
				NBTBase nbt) {
			if (nbt instanceof NBTTagCompound) {
				NBTTagCompound compound = (NBTTagCompound) nbt;
				instance.setBlood(compound.getDouble(BLOOD));
			}
		}
	}
}
