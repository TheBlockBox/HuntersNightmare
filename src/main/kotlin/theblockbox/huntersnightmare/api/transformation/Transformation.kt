package theblockbox.huntersnightmare.api.transformation

import net.minecraft.util.ResourceLocation
import net.minecraft.util.Util
import net.minecraft.util.text.TranslationTextComponent
import net.minecraftforge.registries.ForgeRegistryEntry
import net.minecraftforge.registries.IForgeRegistry
import net.minecraftforge.registries.IForgeRegistryInternal
import net.minecraftforge.registries.RegistryManager
import theblockbox.huntersnightmare.HuntersNightmare
import theblockbox.huntersnightmare.api.init.TransformationInit
import java.util.*
import kotlin.collections.HashMap

/**
 * Every [net.minecraft.entity.LivingEntity] can have a Transformation which is a certain state that gives them special abilities.
 */
class Transformation(val transformationType: TransformationType = TransformationType.NORMAL, val wip: Boolean = false) : ForgeRegistryEntry<Transformation>() {
    /**
     * The unlocalized name of this Transformation.
     */
    val translationKey: TranslationTextComponent by lazy { TranslationTextComponent(Util.makeTranslationKey("transformation", this.registryName)) }

    /**
     * This method returns true if the transformation exists (meaning
     * it's not {@link Transformation#NONE})
     */
    fun exists() = (this.registryName != null) && !(TransformationInit.none.get().registryName?.equals(this.registryName)?:true)

    override fun toString() = (registryName ?: defaultKey).toString()

    enum class TransformationType {
        NORMAL, PHYSICAL_SUPERNATURAL, UNPHYSICAL_SUPERNATURAL;
    }

    companion object {
        /**
         * The internal and mutable backing map of [transformations].
         */
        private val _transformations: MutableMap<String, Transformation> = HashMap()

        /**
         * An immutable map of all registered transformations with their registry names as keys.
         */
        val transformations: Map<String, Transformation> = Collections.unmodifiableMap(_transformations)

        /**
         * The [ResourceLocation] used to identify instances of this class (for registries).
         */
        val resourceLocation: ResourceLocation = ResourceLocation(HuntersNightmare.MODID, "transformation")

        /**
         * The default key that is given to instances without a [ResourceLocation].
         */
        val defaultKey: ResourceLocation = ResourceLocation(HuntersNightmare.MODID, "none")

        /**
         * Gets a Transformation when given its registry name as a string.
         * @return Returns a non-null Transformation with the given registry name. If none can be found, [TransformationInit.none] is returned instead.
         */
        fun getFromName(registryName: String?) = transformations[registryName] ?: TransformationInit.none.get()

        /**
         * Gets a Transformation when given its registry name as a [ResourceLocation].
         * @return Returns a non-null Transformation with the given registry name. If none can be found, [TransformationInit.none] is returned instead.
         */
        fun getFromName(registryName: ResourceLocation?) = getFromName(registryName?.toString())
    }

    object Callbacks : IForgeRegistry.AddCallback<Transformation>, IForgeRegistry.ClearCallback<Transformation> {
        override fun onAdd(owner: IForgeRegistryInternal<Transformation>?, stage: RegistryManager?, id: Int, obj: Transformation?, oldObj: Transformation?) {
            if ((obj != null) && (obj.registryName != null)) {
                _transformations[obj.toString()] = obj
            }
        }

        override fun onClear(owner: IForgeRegistryInternal<Transformation>?, stage: RegistryManager?) {
            _transformations.clear()
        }
    }
}