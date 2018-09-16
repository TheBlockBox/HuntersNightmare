package theblockbox.huntersdream.util.interfaces.transformation;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import theblockbox.huntersdream.util.annotations.CapabilityInterface;
import theblockbox.huntersdream.util.enums.Transformations;

/** For player vampires */
@CapabilityInterface
public interface IVampire {
	default public Transformations getTransformation() {
		return Transformations.VAMPIRE;
	}

	/** Returns a vampires current blood. One blood is half a blood drop */
	public int getBlood();

	public void setBlood(int blood);

	default public void incrementBlood() {
		this.setBlood(this.getBlood() + 1);
	}

	default public void decrementBlood() {
		this.setBlood(this.getBlood() - 1);
	}

	public int getTimeDrinking();

	public void setTimeDrinking(int time);

	public static class Vampire implements IVampire {
		private int blood = 0;
		private int timeDrinking = 0;

		@Override
		public int getBlood() {
			return this.blood;
		}

		@Override
		public void setBlood(int blood) {
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

	public static class VampireStorage implements IStorage<IVampire> {
		public static final String BLOOD = "blood";

		@Override
		public NBTBase writeNBT(Capability<IVampire> capability, IVampire instance, EnumFacing side) {
			NBTTagCompound compound = new NBTTagCompound();
			compound.setInteger(BLOOD, instance.getBlood());
			return compound;
		}

		@Override
		public void readNBT(Capability<IVampire> capability, IVampire instance, EnumFacing side, NBTBase nbt) {
			if (nbt instanceof NBTTagCompound) {
				NBTTagCompound compound = (NBTTagCompound) nbt;
				instance.setBlood(compound.getInteger(BLOOD));
			}
		}
	}
}
