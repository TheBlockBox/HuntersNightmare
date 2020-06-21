package theblockbox.huntersnightmare.api.event

import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.eventbus.api.Event
import theblockbox.huntersnightmare.api.transformation.Transformation


/**
 * TransformationRegistryEvent is fired when the transformations are being
 * registered.
 *
 * This event is not [net.minecraftforge.eventbus.api.Cancelable].<br>
 *
 * This event does not have a result.
 * [net.minecraftforge.fml.common.eventhandler.Event.HasResult]<br>
 *
 * This event is fired on the [MinecraftForge.EVENT_BUS].
 **/
// TODO: Make this a proper registry or nah?
// TODO: Add correct event bus
class TransformationRegistryEvent : Event() {
    private val transformationMap = HashMap<String, Transformation>()

    /**
     * Registers a transformation. Returns true if the adding was successful
     * (meaning that the transformation hasn't already been registered).
     *
     * @return Returns true if the transformation has been added successfully.
     */
    fun registerTransformation(transformation: Transformation) =
            if (transformationMap.containsKey(transformation.registryName.toString())) {
                transformationMap[transformation.registryName.toString()] = transformation
                true
            } else false

    /**
     * Does the same as [registerTransformation] except that
     * it registers multiple transformations at once.
     */
    fun registerTransformations(vararg transformations: Transformation): Boolean {
        var flag = true
        for (transformation in transformations)
            if (!registerTransformation(transformation))
                flag = false
        return flag
    }

    /**
     * Returns all transformations that have been registered so far.
     * @return Returns an immutable map of registry names as keys and transformations as values.
     */
    fun getTransformationMap() = transformationMap.toMap()
}