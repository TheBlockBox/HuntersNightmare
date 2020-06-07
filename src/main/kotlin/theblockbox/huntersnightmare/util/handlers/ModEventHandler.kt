package theblockbox.huntersnightmare.util.handlers

import net.minecraft.block.Block
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.RegistryObject
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import theblockbox.huntersnightmare.HuntersNightmare
import theblockbox.huntersnightmare.api.init.BlockInit
import theblockbox.huntersnightmare.api.init.CapabilitiesInit
import theblockbox.huntersnightmare.api.init.ItemGroupInit

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
    fun onFMLCommonSetupEvent(event: FMLCommonSetupEvent) {
        CapabilitiesInit.registerCapabilities()
    }
}