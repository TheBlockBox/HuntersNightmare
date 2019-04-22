package theblockbox.huntersdream.api.interfaces.transformation;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import theblockbox.huntersdream.api.Transformation;
import theblockbox.huntersdream.api.helpers.GeneralHelper;
import theblockbox.huntersdream.util.annotations.CapabilityInterface;
import theblockbox.huntersdream.util.collection.TransformationSet;

import java.util.Set;

/**
 * This interface is for creatures that can transform (for the capability)
 */
@CapabilityInterface
public interface ITransformationCreature extends ITransformation {
    public Transformation[] getTransformationsNotImmuneTo();

    public default void setTransformationsNotImmuneTo(Transformation... transformationsNotImmuneTo) {
        throw new UnsupportedOperationException("Can't set transformations not immune to");
    }

    public boolean notImmuneToTransformation(Transformation transformation);

    public static class TransformationCreature implements ITransformationCreature {
        private static final NBTTagCompound DEFAULT_TRANSFORMATION_DATA = new NBTTagCompound();

        static {
            ITransformationCreature.TransformationCreature.DEFAULT_TRANSFORMATION_DATA.setString("transformation", Transformation.WEREWOLF.toString());
        }

        private Set<Transformation> transformationsNotImmuneTo = new TransformationSet();
        private Transformation transformation = Transformation.HUMAN;
        private int textureIndex = 0;
        private NBTTagCompound transformationData = ITransformationCreature.TransformationCreature.DEFAULT_TRANSFORMATION_DATA;

        @Override
        public int getTextureIndex() {
            return this.textureIndex;
        }

        @Override
        public Transformation[] getTransformationsNotImmuneTo() {
            Set<Transformation> var = this.transformationsNotImmuneTo;
            return var.toArray(new Transformation[0]);
        }

        @Override
        public void setTransformationsNotImmuneTo(Transformation... transformationsNotImmuneTo) {
            this.transformationsNotImmuneTo = new TransformationSet(transformationsNotImmuneTo);
        }

        @Override
        public Transformation getTransformation() {
            return this.transformation;
        }

        @Override
        public void setTransformation(Transformation transformation) {
            transformation.validateIsTransformation();
            this.transformation = transformation;
        }

        @Override
        public void setTextureIndex(int textureIndex) {
            this.textureIndex = textureIndex;
        }

        @Override
        public boolean notImmuneToTransformation(Transformation t) {
            return this.transformationsNotImmuneTo.contains(t);
        }

        @Override
        public NBTTagCompound getTransformationData() {
            return this.transformationData;
        }

        @Override
        public void setTransformationData(NBTTagCompound transformationData) {
            this.transformationData = transformationData;
        }
    }

    public static class TransformationCreatureStorage implements Capability.IStorage<ITransformationCreature> {
        public static final String TEXTURE_INDEX = "textureindex";
        public static final String TRANSFORMATION = "transformation";
        public static final String TRANSFORMATIONS_NOT_IMMUNE_TO = "notimmuneto";
        public static final String TRANSFORMATION_DATA = "transformationdata";

        @Override
        public NBTBase writeNBT(Capability<ITransformationCreature> capability, ITransformationCreature instance,
                                EnumFacing side) {
            NBTTagCompound compound = new NBTTagCompound();
            compound.setInteger(ITransformationCreature.TransformationCreatureStorage.TEXTURE_INDEX, instance.getTextureIndex());
            compound.setString(ITransformationCreature.TransformationCreatureStorage.TRANSFORMATION, instance.getTransformation().toString());
            GeneralHelper.writeArrayToNBT(compound, instance.getTransformationsNotImmuneTo(),
                    ITransformationCreature.TransformationCreatureStorage.TRANSFORMATIONS_NOT_IMMUNE_TO, Transformation::toString);
            compound.setTag(ITransformationCreature.TransformationCreatureStorage.TRANSFORMATION_DATA, instance.getTransformationData());
            return compound;
        }

        @Override
        public void readNBT(Capability<ITransformationCreature> capability, ITransformationCreature instance,
                            EnumFacing side, NBTBase nbt) {
            NBTTagCompound compound = (NBTTagCompound) nbt;
            instance.setTextureIndex(compound.getInteger(ITransformationCreature.TransformationCreatureStorage.TEXTURE_INDEX));
            instance.setTransformation(Transformation.fromName(compound.getString(ITransformationCreature.TransformationCreatureStorage.TRANSFORMATION)));
            instance.setTransformationsNotImmuneTo(GeneralHelper.readArrayFromNBT(compound,
                    ITransformationCreature.TransformationCreatureStorage.TRANSFORMATIONS_NOT_IMMUNE_TO, Transformation::fromName, Transformation[]::new));
            instance.setTransformationData((NBTTagCompound) compound.getTag(ITransformationCreature.TransformationCreatureStorage.TRANSFORMATION_DATA));
        }
    }
}
