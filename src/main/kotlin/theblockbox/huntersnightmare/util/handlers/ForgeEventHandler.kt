package theblockbox.huntersnightmare.util.handlers

import com.google.gson.JsonParser
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.merchant.villager.VillagerEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.text.TextFormatting
import net.minecraft.util.text.TranslationTextComponent
import net.minecraftforge.event.entity.EntityJoinWorldEvent
import net.minecraftforge.event.entity.player.PlayerEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.server.FMLServerStartingEvent
import theblockbox.huntersnightmare.HuntersNightmare
import theblockbox.huntersnightmare.api.event.TransformationEvent
import theblockbox.huntersnightmare.api.init.CommandInit
import theblockbox.huntersnightmare.api.init.TransformationInit
import theblockbox.huntersnightmare.api.transformation.TransformationHelper.setTransformation
import theblockbox.huntersnightmare.entity.WerewolfEntity
import java.io.InputStreamReader
import java.net.URL

@Mod.EventBusSubscriber(modid = HuntersNightmare.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
object ForgeEventHandler {
    var outdatedStatus = HuntersNightmare.MODID + ".status.unknown"
        private set

    @SubscribeEvent
    fun onFMLServerStartingEvent(event: FMLServerStartingEvent) {
        CommandInit.registerCommands(event.commandDispatcher)
    }

    @SubscribeEvent
    fun onPlayerJoin(event: PlayerEvent.PlayerLoggedInEvent) {
        // TODO: Use coroutine
        // testing for outdated versions in its own thread so that this won't block the whole main thread
        object : Thread(HuntersNightmare.MODID + ":getIsOutdatedVersion") {
            override fun run() {
                try {
                    // TODO: Can you access this using the data forge has already pulled?
                    val jsonObject = JsonParser().parse(InputStreamReader(URL(HuntersNightmare.UPDATE_JSON).openStream())).asJsonObject
                    for (jsonElement in jsonObject["supportedmcversions"].asJsonArray) {
                        if (jsonElement.asString == HuntersNightmare.MC_VERSION) {
                            outdatedStatus = HuntersNightmare.MODID + ".status.uptodate"
                            return
                        }
                    }
                    event.player.server?.deferTask {
                        event.player.sendMessage(TranslationTextComponent(HuntersNightmare.MODID + ".versionNotSupported", HuntersNightmare.MC_VERSION)
                                .also { it.style.setColor(TextFormatting.DARK_RED) })
                    }

                    outdatedStatus = HuntersNightmare.MODID + ".status.outdated"
                } catch (e: Exception) {
                    HuntersNightmare.logger.error("Something went wrong while trying to test for supported minecraft versions")
                }
            }
        }.start()
    }

    @SubscribeEvent
    fun onEntityJoinWorld(event: EntityJoinWorldEvent) {
        // TODO: Wait does this also affect existing entities?
        val entity = event.entity
        if (entity is LivingEntity) {
            entity.setTransformation(when (entity) {
                is PlayerEntity, is VillagerEntity -> TransformationInit.human.get()
                is WerewolfEntity -> TransformationInit.werewolf.get()
                else -> return
            }, TransformationEvent.Reason.spawn)
        }
    }
}