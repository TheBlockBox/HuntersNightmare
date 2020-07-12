package theblockbox.huntersnightmare.api.init

import net.minecraftforge.fml.RegistryObject
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.RegistryManager
import theblockbox.huntersnightmare.HuntersNightmare
import theblockbox.huntersnightmare.api.transformation.Transformation

object TransformationInit : DeferredRegister<Transformation>(RegistryManager.ACTIVE.getRegistry(Transformation::class.java), HuntersNightmare.MODID) {

    /**
     * Used to indicate that no transformation is present and it won't change.
     */
    val none: RegistryObject<Transformation> = this.register("none") {
        Transformation()
    }

    /**
     * Used to indicate that no transformation is currently present but this could change.
     */
    val human: RegistryObject<Transformation> = this.register("human") {
        Transformation()
    }

    /**
     * The transformation for werewolves.
     */
    val werewolf: RegistryObject<Transformation> = this.register("werewolf") {
        Transformation(Transformation.TransformationType.PHYSICAL_SUPERNATURAL)
    }

    /**
     * The transformation for vampires.
     */
    val vampire: RegistryObject<Transformation> = this.register("vampire") {
        Transformation(Transformation.TransformationType.PHYSICAL_SUPERNATURAL)
    }

    /**
     * The transformation for ghosts.
     */
    val ghost: RegistryObject<Transformation> = this.register("ghost") {
        Transformation(Transformation.TransformationType.UNPHYSICAL_SUPERNATURAL)
    }
}