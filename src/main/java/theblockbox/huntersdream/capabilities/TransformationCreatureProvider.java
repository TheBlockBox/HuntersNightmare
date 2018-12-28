package theblockbox.huntersdream.capabilities;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.Validate;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import theblockbox.huntersdream.api.Transformation;
import theblockbox.huntersdream.init.CapabilitiesInit;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformationCreature;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformationCreature.TransformationCreature;

public class TransformationCreatureProvider implements ICapabilitySerializable<NBTTagCompound> {
	public static final Capability<ITransformationCreature> CAP = CapabilitiesInit.CAPABILITY_TRANSFORMATION_CREATURE;
	private final ITransformationCreature instance;

	public TransformationCreatureProvider(Transformation... notImmuneTo) {
		this.instance = new TransformationCreatureImplementation(notImmuneTo);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return (capability == CAP);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		return capability == CAP ? CAP.cast(this.instance) : null;
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
		private Transformation[] transformationsNotImmuneTo = new Transformation[0];

		public TransformationCreatureImplementation(Transformation[] notImmuneTo) {
			Validate.noNullElements(notImmuneTo,
					"Null transformations not allowed (null transformation in array %s at index %d)",
					ArrayUtils.toString(notImmuneTo));
			this.transformationsNotImmuneTo = notImmuneTo;
		}

		@Override
		public Transformation[] getTransformationsNotImmuneTo() {
			return this.transformationsNotImmuneTo;
		}

		@Override
		public void setTransformationsNotImmuneTo(Transformation... transformationsNotImmuneTo) {
			Validate.noNullElements(transformationsNotImmuneTo,
					"Null transformations not allowed (null transformation in array %s at index %d)",
					ArrayUtils.toString(transformationsNotImmuneTo));
			this.transformationsNotImmuneTo = transformationsNotImmuneTo;
		}
	}
}
