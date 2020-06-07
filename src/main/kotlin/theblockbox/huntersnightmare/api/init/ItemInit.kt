package theblockbox.huntersnightmare.api.init

import net.minecraft.item.Item
import net.minecraftforge.fml.RegistryObject
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import theblockbox.huntersnightmare.HuntersNightmare

object ItemInit : DeferredRegister<Item>(ForgeRegistries.ITEMS, HuntersNightmare.MODID) {
    val journal: RegistryObject<Item> = this.register("journal") {
        Item(Item.Properties().group(ItemGroupInit.itemGroup))
    }
}