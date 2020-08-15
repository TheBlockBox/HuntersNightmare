package theblockbox.huntersnightmare.network

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.CompoundNBT
import net.minecraft.network.PacketBuffer
import net.minecraft.world.dimension.DimensionType
import net.minecraftforge.fml.network.NetworkDirection
import net.minecraftforge.fml.network.NetworkEvent
import theblockbox.huntersnightmare.api.transformation.Transformation
import theblockbox.huntersnightmare.api.transformation.TransformationHelper.getITransformation
import theblockbox.huntersnightmare.api.transformation.TransformationHelper.getTransformation
import java.util.*
import java.util.function.Supplier

class TransformationMessage(private val playerUUID: UUID, private val dimensionType: DimensionType?, private val transformation: Transformation, private val transformationData: CompoundNBT?) : BaseMessage() {
    constructor(buf: PacketBuffer) : this(buf.readUniqueId(), DimensionType.getById(buf.readInt()), Transformation.getFromName(buf.readResourceLocation()), buf.readCompoundTag())
    constructor(player: PlayerEntity): this(player.uniqueID, player.world.dimension.type, player.getTransformation(),
            player.getITransformation()?.transformationData)

    override fun encode(buf: PacketBuffer) {
        buf.writeResourceLocation(transformation.registryName!!)
        buf.writeCompoundTag(transformationData)
    }

    override fun handle(context: Supplier<NetworkEvent.Context>) {
        if (context.get().direction == NetworkDirection.PLAY_TO_CLIENT) {
            context.get().enqueueWork {
                val entity = (if (dimensionType == null) null else context.get().sender?.world?.server?.getWorld(dimensionType))?.getEntityByUuid(playerUUID)
                if ((entity is PlayerEntity) && transformation.exists()) {
                    entity.getITransformation()?.let {
                        it.transformation = transformation
                        if (transformationData != null) {
                            it.transformationData = transformationData
                        }
                    }
                }
            }
        }
    }
}