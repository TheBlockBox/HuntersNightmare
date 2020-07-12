package theblockbox.huntersnightmare.api.init

import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import theblockbox.huntersnightmare.HuntersNightmare
import java.util.function.Supplier


object ItemGroupInit {
    val itemGroup: ItemGroup = ModItemGroup(HuntersNightmare.MODID, Supplier { ItemStack(ItemInit.journal.get()) })

    private class ModItemGroup(name: String, val iconSupplier: Supplier<ItemStack>) : ItemGroup(name) {
        override fun createIcon() = iconSupplier.get()
    }
}