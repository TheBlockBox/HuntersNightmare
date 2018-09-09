package theblockbox.huntersdream.capabilities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import theblockbox.huntersdream.init.CapabilitiesInit;
import theblockbox.huntersdream.util.enums.Transformations;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformationCreature;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformationCreature.TransformationCreature;

public class TransformationCreatureProvider implements ICapabilitySerializable<NBTTagCompound> {
	public static final Capability<ITransformationCreature> CAP = CapabilitiesInit.CAPABILITY_TRANSFORMATION_CREATURE;
	private final ITransformationCreature instance;

	public TransformationCreatureProvider(Transformations... notImmuneTo) {
		this.instance = new TransformationCreatureImplementation(notImmuneTo);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return (capability == CAP);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		return capability == CAP ? CAP.<T>cast(this.instance) : null;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		return (NBTTagCompound) CAP.getStorage().writeNBT(CAP, this.instance, null);
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		CAP.getStorage().readNBT(CAP, this.instance, null, nbt);
	}

	private static class TransformationCreatureImplementation extends TransformationCreature {
		private Transformations[] transformationsNotImmuneTo = new Transformations[0];

		public TransformationCreatureImplementation(Transformations[] notImmuneTo) {
			this.transformationsNotImmuneTo = notImmuneTo;
		}

		@Override
		public Transformations[] getTransformationsNotImmuneTo() {
			return this.transformationsNotImmuneTo;
		}

		@Override
		public void setTransformationsNotImmuneTo(Transformations... transformationsNotImmuneTo) {
			this.transformationsNotImmuneTo = transformationsNotImmuneTo;
		}
	}
}
