package theblockbox.huntersdream.capabilities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nonnull;

public class CapabilityProvider<T> implements ICapabilitySerializable<NBTTagCompound> {

    private final Capability<T> cap;
    public final T instance;

    public CapabilityProvider(Capability<T> capability) {
        this.cap = capability;
        this.instance = capability.getDefaultInstance();
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, EnumFacing facing) {
        return (capability == this.cap);
    }

    @Override
    public <E> E getCapability(@Nonnull Capability<E> capability, EnumFacing facing) {
        return capability == this.cap ? this.cap.cast(this.instance) : null;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        return (NBTTagCompound) this.cap.getStorage().writeNBT(this.cap, this.instance, null);
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        this.cap.getStorage().readNBT(this.cap, this.instance, null, nbt);
    }
}
