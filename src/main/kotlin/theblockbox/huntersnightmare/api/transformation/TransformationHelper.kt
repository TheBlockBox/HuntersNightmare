package theblockbox.huntersnightmare.api.transformation

import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.CompoundNBT
import net.minecraft.util.text.TextFormatting
import net.minecraft.util.text.TranslationTextComponent
import net.minecraft.world.server.ServerWorld
import net.minecraftforge.fml.common.Mod
import theblockbox.huntersnightmare.api.event.TransformationEvent
import theblockbox.huntersnightmare.api.init.CapabilityInit
import theblockbox.huntersnightmare.api.init.TransformationInit
import theblockbox.huntersnightmare.network.TransformationMessage
import theblockbox.huntersnightmare.util.handlers.PacketHandler.sendToClients

object TransformationHelper {
    fun LivingEntity?.getITransformation(): ITransformation? {
        return this?.getCapability(CapabilityInit.transformationCapability)?.orElse(null)
    }

    /**
     * Returns an entity's transformation
     */
    fun LivingEntity?.getTransformation(): Transformation {
        return this?.getITransformation()?.transformation?: TransformationInit.none.get()
    }

    /**
     * Changes the [Transformation] of the given [Entity] to the one given.
     * @return Returns whether the entity's transformation has successfully been changed.
     */
    fun LivingEntity?.setTransformation(newTransformation: Transformation?, reason: TransformationEvent.Reason): Boolean {
        if ((this != null) && (newTransformation?.exists() == true)) {
            this.getITransformation()?.let {
                val oldTransformation = it.transformation
                it.transformation = newTransformation
                Mod.EventBusSubscriber.Bus.FORGE.bus()?.get()?.post(TransformationEvent(this, oldTransformation, reason))
                it.transformationData = CompoundNBT()
                if (newTransformation.wip) {
                    if (this is PlayerEntity) {
                        TransformationMessage(this).sendToClients(this.world as ServerWorld)
                    }
                    val message = TranslationTextComponent("transformation.huntersnightmare.wip", newTransformation.translationKey)
                    message.style.color = TextFormatting.RED
                    this.sendMessage(message)
                }
                return@setTransformation true
            }
        }
        return false
    }

    /**
     * Changes the [Transformation] of the given [Entity] to the one given. This works the same as [setTransformation],
     * except that the Transformation is only set when the entity allows for it (so it's not forced).
     * @return Returns whether the entity's transformation has successfully been changed.
     */
    fun LivingEntity?.setTransformationIfPossible(newTransformation: Transformation?, reason: TransformationEvent.Reason): Boolean {
        if ((this != null) && (newTransformation?.exists() == true)) {
            if (this.getITransformation()?.canChangeTransformation(newTransformation) == true) {
                this.setTransformation(newTransformation, reason)
                return true
            }
        }
        return false
    }
}