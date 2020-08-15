package theblockbox.huntersnightmare.network

import net.minecraft.network.PacketBuffer
import net.minecraftforge.fml.network.NetworkEvent
import java.util.function.Supplier

abstract class BaseMessage {
    open fun encode(buf: PacketBuffer) {}

    abstract fun handle(context: Supplier<NetworkEvent.Context>)
}