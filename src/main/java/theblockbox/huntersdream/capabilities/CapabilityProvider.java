package theblockbox.huntersdream.capabilities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class CapabilityProvider<T> implements ICapabilitySerializable<NBTTagCompound> {

	private final Capability<T> CAP;
	public final T INSTANCE;

	public CapabilityProvider(Capability<T> capability) {
		this.CAP = capability;
		this.INSTANCE = capability.getDefaultInstance();
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return (capability == CAP);
	}

	@Override
	public <E> E getCapability(Capability<E> capability, EnumFacing facing) {
		return capability == CAP ? CAP.<E>cast(this.INSTANCE) : null;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		return (NBTTagCompound) CAP.getStorage().writeNBT(CAP, INSTANCE, null);
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		CAP.getStorage().readNBT(CAP, INSTANCE, null, nbt);
	}
}
