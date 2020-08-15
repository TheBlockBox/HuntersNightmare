package theblockbox.huntersnightmare.api.event

import net.minecraft.entity.LivingEntity
import net.minecraftforge.event.entity.living.LivingEvent
import net.minecraftforge.fml.common.Mod
import theblockbox.huntersnightmare.api.transformation.Transformation
import theblockbox.huntersnightmare.api.transformation.TransformationHelper.getTransformation

/**
 * TransformationEvent is fired when a [LivingEntity] changes its [Transformation]. It is
 * recommended to take a look at the [transformation before deciding what to do. <br>
 * <br>
 * This event is {@link Cancelable}.<br>
 * If this event is canceled, the entity won't change its transformation.<br>
 * <br>
 * This event does not have a result.
 * {@link net.minecraftforge.fml.common.eventhandler.Event.HasResult}<br>
 * <br>
 * This event is fired on [Mod.EventBusSubscriber.Bus.FORGE].
 **/
class TransformationEvent(entity: LivingEntity, val transformationBefore: Transformation, val reason: Reason): LivingEvent(entity) {
    val transformationAfter: Transformation = entity.getTransformation()


    class Reason {
        companion object {
            /**
             * Caused by an infection
             */
            val infection = Reason()
            /**
             * When an entity spawns with the given transformation (may not always work, for
             * example there's no call for {@link EntityWerewolf}
             */
            val spawn = Reason()
            /**
             * When a command is executed
             */
            val command = Reason()
            /**
             * When an entity becomes another entity and the new entities transformation needs to
             * be set to the old one's transformation (for example when a villager is hit by
             * lightning and becomes a witch).
             */
            val entityChange = Reason()
        }
    }
}