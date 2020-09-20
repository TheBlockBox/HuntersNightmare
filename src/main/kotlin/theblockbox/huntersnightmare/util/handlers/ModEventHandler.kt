package theblockbox.huntersnightmare.util.handlers

import net.alexwells.kottle.FMLKotlinModLoadingContext
import net.minecraft.block.Block
import net.minecraft.entity.EntityType
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.RegistryObject
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import net.minecraftforge.registries.RegistryBuilder
import theblockbox.huntersnightmare.HuntersNightmare
import theblockbox.huntersnightmare.api.init.*
import theblockbox.huntersnightmare.api.transformation.Transformation
import theblockbox.huntersnightmare.item.ModdedSpawnEggItem


@Mod.EventBusSubscriber(modid = HuntersNightmare.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
object ModEventHandler {
    @SubscribeEvent
    fun onRegisterItems(event: RegistryEvent.Register<Item>) {
        BlockInit.entries.map(RegistryObject<Block>::get).forEach {
            val item = BlockItem(it, Item.Properties().group(ItemGroupInit.itemGroup))
            item.registryName = it.registryName
            event.registry.register(item)
        }
    }

    @SubscribeEvent
    fun onNewRegistryEvent(event: RegistryEvent.NewRegistry) {
        RegistryBuilder<Transformation>().setName(Transformation.resourceLocation).setType(Transformation::class.java)
                .setDefaultKey(Transformation.defaultKey).addCallback(Transformation.Callbacks).create()
        TransformationInit.register(FMLKotlinModLoadingContext.get().modEventBus)
    }

    @SubscribeEvent
    fun onFMLCommonSetupEvent(event: FMLCommonSetupEvent) {
        CapabilityInit.registerCapabilities()
        PacketHandler.registerMessages()
    }

    @SubscribeEvent
    fun onClientSetup(event: FMLClientSetupEvent) {
        EntityInit.registerRenderers()
    }

    @SubscribeEvent
    fun onRegisterEntityType(event: RegistryEvent.Register<EntityType<*>>) {
        EntityInit.register(event)
    }
}