package theblockbox.huntersnightmare.util.handlers

import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.network.PacketBuffer
import net.minecraft.util.ResourceLocation
import net.minecraft.world.server.ServerWorld
import net.minecraftforge.fml.network.NetworkDirection
import net.minecraftforge.fml.network.NetworkRegistry
import net.minecraftforge.fml.network.simple.SimpleChannel
import theblockbox.huntersnightmare.HuntersNightmare
import theblockbox.huntersnightmare.network.BaseMessage
import theblockbox.huntersnightmare.network.TransformationMessage

object PacketHandler {
    const val PROTOCOL_VERSION = "1.0"
    private lateinit var instance: SimpleChannel
    private var networkID = 0

    fun registerMessages() {
        instance = NetworkRegistry.newSimpleChannel(ResourceLocation(HuntersNightmare.MODID, "main"),
                { PROTOCOL_VERSION }, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals)
        registerMessage(TransformationMessage::class.java) { TransformationMessage(it) }
    }

    private fun <T : BaseMessage> registerMessage(clazz: Class<T>, decoder: (PacketBuffer) -> T) {
        instance.messageBuilder(clazz, networkID++)
                .encoder { packet: T, packetBuffer: PacketBuffer -> packet.encode(packetBuffer) }
                .decoder(decoder).consumer(BaseMessage::handle).add()
    }

    fun BaseMessage.sendToClient(player: ServerPlayerEntity) {
        instance.sendTo(this, player.connection.netManager, NetworkDirection.PLAY_TO_CLIENT)
    }

    fun BaseMessage.sendToClients(world: ServerWorld) {
        for (player in world.players) {
            if (player != null) {
                sendToClient(player)
            }
        }
    }

    fun BaseMessage.sendToServer() {
        instance.sendToServer(this)
    }
}