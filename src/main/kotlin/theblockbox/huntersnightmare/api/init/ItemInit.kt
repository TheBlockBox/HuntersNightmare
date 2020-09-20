package theblockbox.huntersnightmare.api.init

import net.minecraft.item.Item
import net.minecraft.item.SpawnEggItem
import net.minecraftforge.fml.RegistryObject
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import theblockbox.huntersnightmare.HuntersNightmare
import theblockbox.huntersnightmare.api.init.ItemInit.register

object ItemInit : DeferredRegister<Item>(ForgeRegistries.ITEMS, HuntersNightmare.MODID) {
    val journal: RegistryObject<Item> = register("journal") {
        Item(Item.Properties().group(ItemGroupInit.itemGroup))
    }

    val werewolfSpawnEgg: RegistryObject<Item> = register("werewolf_spawn_egg") {
        SpawnEggItem(EntityInit.werewolf, 0, 6636321, Item.Properties().group(ItemGroupInit.itemGroup))
    }
}