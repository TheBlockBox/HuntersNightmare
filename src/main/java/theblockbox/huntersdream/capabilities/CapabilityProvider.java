package theblockbox.huntersdream.capabilities;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class CapabilityProvider<T> implements ICapabilitySerializable<NBTBase> {

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
	public NBTBase serializeNBT() {
		return CAP.getStorage().writeNBT(CAP, INSTANCE, null);
	}

	@Override
	public void deserializeNBT(NBTBase nbt) {
		CAP.getStorage().readNBT(CAP, INSTANCE, null, nbt);
	}

}
