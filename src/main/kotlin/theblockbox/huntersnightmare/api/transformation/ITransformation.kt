package theblockbox.huntersnightmare.api.transformation

import net.minecraft.nbt.CompoundNBT
import net.minecraft.nbt.INBT
import net.minecraft.util.Direction
import net.minecraftforge.common.capabilities.Capability
import theblockbox.huntersnightmare.api.init.TransformationInit


interface ITransformation {
    var transformation: Transformation
    var transformationData: CompoundNBT

    /**
     * Returns true if this entity can change its [Transformation] from the current one to the one given.
     */
    fun canChangeTransformation(newTransformation: Transformation) : Boolean

    class TransformationImpl : ITransformation {
        override var transformation: Transformation = TransformationInit.human.get()
        override var transformationData: CompoundNBT = CompoundNBT()

        override fun canChangeTransformation(newTransformation: Transformation) = true
    }

    object TransformationStorage : Capability.IStorage<ITransformation> {
        const val TRANSFORMATION = "transformation"
        const val TRANSFORMATION_DATA = "transformationData"

        override fun writeNBT(capability: Capability<ITransformation>, instance: ITransformation, side: Direction?): INBT? {
            val compound = CompoundNBT()
            compound.putString(TRANSFORMATION, instance.transformation.registryName.toString())
            compound.put(TRANSFORMATION_DATA, instance.transformationData)
            return compound
        }

        override fun readNBT(capability: Capability<ITransformation>, instance: ITransformation, side: Direction?, nbt: INBT?) {
            if (nbt is CompoundNBT) {
                instance.transformation = Transformation.getFromName(nbt.getString(TRANSFORMATION))
                val transformationData = nbt.get(TRANSFORMATION_DATA)
                instance.transformationData = if(transformationData is CompoundNBT) transformationData else CompoundNBT()
            }
        }
    }
}