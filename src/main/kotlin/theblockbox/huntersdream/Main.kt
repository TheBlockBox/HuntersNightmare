package theblockbox.huntersdream

import net.alexwells.kottle.FMLKotlinModLanguageProvider
import net.minecraftforge.fml.common.Mod
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import theblockbox.huntersdream.utils.Reference

@Mod(Reference.MODID)
class Main {

    companion object {
        private val LOGGER = LogManager.getLogger(Reference.MODID)

        @Synchronized
        fun getLogger() : Logger {
            return LOGGER
        }
    }

    constructor() {

    }

}