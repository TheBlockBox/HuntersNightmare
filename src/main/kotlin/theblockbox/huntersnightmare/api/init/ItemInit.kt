package theblockbox.huntersnightmare.api.init

import net.minecraft.item.Item
import net.minecraftforge.fml.RegistryObject
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import theblockbox.huntersnightmare.HuntersNightmare
import theblockbox.huntersnightmare.api.init.ItemInit.register

// TODO: Custom item tab (item group)
object ItemInit : DeferredRegister<Item>(ForgeRegistries.ITEMS, HuntersNightmare.MODID) {
    val journal: RegistryObject<Item> = register("journal") {
        Item(Item.Properties().group(ItemGroupInit.itemGroup))
    }
}