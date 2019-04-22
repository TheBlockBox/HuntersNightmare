package theblockbox.huntersdream.capabilities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.Validate;
import theblockbox.huntersdream.api.Transformation;
import theblockbox.huntersdream.api.init.CapabilitiesInit;
import theblockbox.huntersdream.api.interfaces.transformation.ITransformationCreature;

public class TransformationCreatureProvider implements ICapabilitySerializable<NBTTagCompound> {
    public static final Capability<ITransformationCreature> CAP = CapabilitiesInit.CAPABILITY_TRANSFORMATION_CREATURE;
    private final ITransformationCreature instance;

    public TransformationCreatureProvider(Transformation... notImmuneTo) {
        this.instance = new TransformationCreatureProvider.TransformationCreatureImplementation(notImmuneTo);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return (capability == TransformationCreatureProvider.CAP);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        return capability == TransformationCreatureProvider.CAP ? TransformationCreatureProvider.CAP.cast(this.instance) : null;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        return (NBTTagCompound) TransformationCreatureProvider.CAP.getStorage().writeNBT(TransformationCreatureProvider.CAP, this.instance, null);
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        TransformationCreatureProvider.CAP.getStorage().readNBT(TransformationCreatureProvider.CAP, this.instance, null, nbt);
    }

    private static class TransformationCreatureImplementation extends ITransformationCreature.TransformationCreature {
        private Transformation[] transformationsNotImmuneTo = new Transformation[0];

        private TransformationCreatureImplementation(Transformation[] notImmuneTo) {
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
