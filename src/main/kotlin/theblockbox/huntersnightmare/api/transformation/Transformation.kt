package theblockbox.huntersnightmare.api.transformation

import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.TranslationTextComponent
import net.minecraftforge.eventbus.EventBus
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import theblockbox.huntersnightmare.HuntersNightmare
import theblockbox.huntersnightmare.api.event.TransformationRegistryEvent


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
         * An immutable map of all registered transformations with their registry names as keys.
         */
        lateinit var transformations: Map<String, Transformation>
            private set

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

        /**
         * Is called when [FMLCommonSetupEvent] is fired.
         *
         * This method should **not** be called outside of Hunter's Nightmare.
         */
        fun onCommonSetup() {
            val event = TransformationRegistryEvent()
            event.registerTransformations(none, human, werewolf, vampire, ghost)
           // Mod.EventBusSubscriber.Bus.MOD.bus().get().post(event)
            // TODO: Make this work pls
            transformations = event.getTransformationMap()
        }

        fun getFromName(registryName: String?) = transformations[registryName] ?: none

        fun getFromName(registryName: ResourceLocation?) = getFromName(registryName?.toString())
    }
}