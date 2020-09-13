package theblockbox.huntersnightmare

import net.alexwells.kottle.FMLKotlinModLoadingContext
import net.minecraftforge.fml.common.Mod
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import theblockbox.huntersnightmare.api.init.BlockInit
import theblockbox.huntersnightmare.api.init.EntityInit
import theblockbox.huntersnightmare.api.init.ItemInit

@Mod(HuntersNightmare.MODID)
object HuntersNightmare {
    // TODO: Can these be set using gradle?
    const val MODID = "huntersnightmare"
    const val NAME = "Hunter's Nightmare"
    const val VERSION = "2.0.0"
    const val UPDATE_JSON = "https://raw.githubusercontent.com/TheGamingLord/HuntersDream/master/updatechecker.json"
    const val DOWNLOAD_LINK = "https://www.curseforge.com/minecraft/mc-mods/hunters-nightmare"
    const val MC_VERSION = "1.15.2"
    val logger: Logger = LogManager.getLogger(MODID) @Synchronized get

    init {
        val eventBus = FMLKotlinModLoadingContext.get().modEventBus
        BlockInit.register(eventBus)
        ItemInit.register(eventBus)
        EntityInit.register(eventBus)
    }
}