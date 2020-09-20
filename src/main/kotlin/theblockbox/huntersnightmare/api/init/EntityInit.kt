package theblockbox.huntersnightmare.api.init

import net.minecraft.entity.EntityClassification
import net.minecraft.entity.EntityType
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.client.registry.RenderingRegistry
import theblockbox.huntersnightmare.HuntersNightmare
import theblockbox.huntersnightmare.entity.WerewolfEntity
import theblockbox.huntersnightmare.entity.renderer.WerewolfRenderer

object EntityInit {
    val werewolf: EntityType<WerewolfEntity> by lazy {
        EntityType.Builder.create(::WerewolfEntity, EntityClassification.MONSTER).size(2F, 1F).setShouldReceiveVelocityUpdates(false).build("werewolf")
    }

    fun register(event: RegistryEvent.Register<EntityType<*>>) {
        event.registry.registerAll(werewolf.setRegistryName("${HuntersNightmare.MODID}:werewolf"))
    }

    fun registerRenderers() {
        RenderingRegistry.registerEntityRenderingHandler<WerewolfEntity>(werewolf, ::WerewolfRenderer)
    }
}