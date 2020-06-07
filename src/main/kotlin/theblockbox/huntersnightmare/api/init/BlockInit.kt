package theblockbox.huntersnightmare.api.init

import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import theblockbox.huntersnightmare.HuntersNightmare

object BlockInit : DeferredRegister<Block>(ForgeRegistries.BLOCKS, HuntersNightmare.MODID) {
    val testBlock = this.register("test_block") {
        Block(Block.Properties.create(Material.AIR))
    }
}