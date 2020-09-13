package theblockbox.huntersnightmare.api.init

import net.minecraft.entity.Entity
import net.minecraft.entity.EntityClassification
import net.minecraft.entity.EntityType
import net.minecraftforge.fml.RegistryObject
import net.minecraftforge.fml.client.registry.RenderingRegistry
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import theblockbox.huntersnightmare.HuntersNightmare
import theblockbox.huntersnightmare.api.init.EntityInit.register
import theblockbox.huntersnightmare.entity.WerewolfEntity
import theblockbox.huntersnightmare.entity.renderer.WerewolfRenderer

// TODO: Spawn eggs
object EntityInit : DeferredRegister<EntityType<out Entity>>(ForgeRegistries.ENTITIES, HuntersNightmare.MODID) {
    val werewolf: RegistryObject<EntityType<WerewolfEntity>> = register("werewolf") {
        EntityType.Builder.create(::WerewolfEntity, EntityClassification.MONSTER).size(2F, 1F).setShouldReceiveVelocityUpdates(false).build("werewolf")
    }

    fun registerRenderers() {
        RenderingRegistry.registerEntityRenderingHandler<WerewolfEntity>(werewolf.get(), ::WerewolfRenderer)
    }
}