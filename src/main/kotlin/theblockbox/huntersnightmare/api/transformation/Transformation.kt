package theblockbox.huntersnightmare.api.transformation

import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.TranslationTextComponent
import theblockbox.huntersnightmare.HuntersNightmare

/**
 * A class to represent transformations and their properties.
 */
// TODO: Add custom transformations by other mods using events
data class Transformation(val registryName: ResourceLocation, val transformationType: TransformationType = TransformationType.PHYSICAL_SUPERNATURAL,
                          val isUndead: Boolean = false, val isWIP: Boolean = false) {
    val translation: TranslationTextComponent

    init {
        translation = TranslationTextComponent(registryName.namespace + "." + registryName.path)
    }

    enum class TransformationType(val isSupernatural: Boolean) {
        NORMAL(false), PHYSICAL_SUPERNATURAL(true), UNPHYSICAL_SUPERNATURAL(true);
    }

    companion object {
        /**
         * Used to indicate that no transformation is present and it won't change.
         */
        val none: Transformation = Transformation(ResourceLocation(HuntersNightmare.MODID, "none"))

        /**
         * Used to indicate that no transformation is currently present but this could change.
         */
        val human: Transformation = Transformation(ResourceLocation(HuntersNightmare.MODID, "human"))

        /**
         * The transformation for werewolves.
         */
        val werewolf: Transformation = Transformation(ResourceLocation(HuntersNightmare.MODID, "werewolf"), isWIP = true)

        /**
         * The transformation for vampires.
         */
        val vampire: Transformation = Transformation(ResourceLocation(HuntersNightmare.MODID, "vampire"), isWIP = true)

        /**
         * The transformation for ghosts.
         */
        val ghost: Transformation = Transformation(ResourceLocation(HuntersNightmare.MODID, "ghost"), transformationType = TransformationType.UNPHYSICAL_SUPERNATURAL, isWIP = true)

        fun getFromName(name: String?): Transformation {
            TODO("Implement event system for this")
        }
    }
}