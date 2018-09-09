package theblockbox.huntersdream.capabilities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class CapabilityProvider<T> implements ICapabilitySerializable<NBTTagCompound> {

	private final Capability<T> cap;
	public final T instance;

	public CapabilityProvider(Capability<T> capability) {
		this.cap = capability;
		this.instance = capability.getDefaultInstance();
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return (capability == cap);
	}

	@Override
	public <E> E getCapability(Capability<E> capability, EnumFacing facing) {
		return capability == cap ? cap.<E>cast(this.instance) : null;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		return (NBTTagCompound) cap.getStorage().writeNBT(cap, instance, null);
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		cap.getStorage().readNBT(cap, instance, null, nbt);
	}
}
