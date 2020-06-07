package theblockbox.huntersnightmare

import net.alexwells.kottle.FMLKotlinModLoadingContext
import net.minecraftforge.fml.common.Mod
import org.apache.logging.log4j.LogManager
import theblockbox.huntersnightmare.api.init.BlockInit
import theblockbox.huntersnightmare.api.init.ItemInit

@Mod(HuntersNightmare.MODID)
object HuntersNightmare {
    const val MODID = "huntersnightmare"
    const val NAME = "Hunter's Nightmare"
    val logger = LogManager.getLogger(MODID) @Synchronized get

    init {
        val eventBus = FMLKotlinModLoadingContext.get().modEventBus
        BlockInit.register(eventBus)
        ItemInit.register(eventBus)
    }
}