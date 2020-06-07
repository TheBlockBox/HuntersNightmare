package theblockbox.huntersnightmare.api.init

import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.CompoundNBT
import net.minecraft.util.Direction
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityInject
import net.minecraftforge.common.capabilities.CapabilityManager
import net.minecraftforge.common.capabilities.ICapabilitySerializable
import net.minecraftforge.common.util.LazyOptional
import net.minecraftforge.common.util.NonNullConsumer
import net.minecraftforge.event.AttachCapabilitiesEvent
import net.minecraftforge.event.entity.player.PlayerEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import theblockbox.huntersnightmare.HuntersNightmare
import theblockbox.huntersnightmare.api.transformation.ITransformation


@Mod.EventBusSubscriber(modid = HuntersNightmare.MODID)
object CapabilitiesInit {
    @CapabilityInject(ITransformation::class)
    val transformationCapability: Capability<ITransformation>? = null

    val transformationCapabilityIdentifier = ResourceLocation(HuntersNightmare.MODID, "transformation")

    fun registerCapabilities() {
        CapabilityManager.INSTANCE.register(ITransformation::class.java, ITransformation.TransformationStorage) {
            ITransformation.TransformationImpl()
        }
    }

    fun <T> LivingEntity?.getCapIfPresent(capability: Capability<T>?, consumer: NonNullConsumer<T>) {
        if (capability != null)
            this?.getCapability(capability)?.ifPresent(consumer)
    }

    @SubscribeEvent
    fun onPlayerClone(event: PlayerEvent.Clone) {
        // restore capabilities after death
        if (event.isWasDeath) {
            // TODO: Can this be done more easily?
            event.player.getCapIfPresent(transformationCapability,
                    NonNullConsumer { currentInstance: ITransformation ->
                        event.original.getCapIfPresent(transformationCapability, NonNullConsumer { oldInstance: ITransformation ->
                            currentInstance.transformation = oldInstance.transformation
                            currentInstance.transformationData = oldInstance.transformationData
                        })
                    })
        }
    }

    @SubscribeEvent
    fun onCapabilityAttach(event: AttachCapabilitiesEvent<Entity>) {
        val entity: Entity? = event.getObject()
        if ((entity is PlayerEntity) && (transformationCapability != null)) {
            event.addCapability(transformationCapabilityIdentifier, CapabilityProvider(transformationCapability))
        }
    }

    class CapabilityProvider<T>(val capability: Capability<T>) : ICapabilitySerializable<CompoundNBT> {
        val instance: T? = capability.defaultInstance
        private val optionalInstance: LazyOptional<T> = if (instance != null) LazyOptional.of {
            instance // TODO: Is it possible to remove the "unnecessary" cast without compiler issues?
        } else LazyOptional.empty()

        override fun <T : Any?> getCapability(cap: Capability<T>, side: Direction?): LazyOptional<T> =
                if (capability == cap) optionalInstance.cast() else LazyOptional.empty()

        override fun serializeNBT() = capability.storage.writeNBT(this.capability, this.instance, null) as CompoundNBT

        override fun deserializeNBT(nbt: CompoundNBT?) = capability.storage.readNBT(this.capability, this.instance, null, nbt)
    }
}