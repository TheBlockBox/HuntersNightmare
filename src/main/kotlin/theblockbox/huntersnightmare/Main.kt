package theblockbox.huntersnightmare

import net.minecraftforge.fml.common.Mod
import org.apache.logging.log4j.LogManager
import theblockbox.huntersnightmare.util.Reference

@Mod(Reference.MODID)
class Main {
    companion object {
        val logger = LogManager.getLogger(Reference.MODID) @Synchronized get
    }
}