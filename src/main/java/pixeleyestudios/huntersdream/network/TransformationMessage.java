package pixeleyestudios.huntersdream.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import pixeleyestudios.huntersdream.util.helpers.TransformationHelper;
import pixeleyestudios.huntersdream.util.interfaces.ITransformationPlayer;

public class TransformationMessage extends MessageBase<TransformationMessage> {

	private int xp;
	private boolean transformed;
	private int transformationID;
	private int entityID;
	private int textureIndex;

	public TransformationMessage() {
	}

	public TransformationMessage(int xp, boolean transformed, int transformationID, int entityID, int textureIndex) {
		this.xp = xp;
		this.transformed = transformed;
		this.transformationID = transformationID;
		this.entityID = entityID;
		this.textureIndex = textureIndex;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.xp = buf.readInt();
		this.transformed = buf.readBoolean();
		this.transformationID = buf.readInt();
		this.entityID = buf.readInt();
		this.textureIndex = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(xp);
		buf.writeBoolean(transformed);
		buf.writeInt(transformationID);
		buf.writeInt(entityID);
		buf.writeInt(textureIndex);
	}

	@Override
	public IMessage onMessageReceived(TransformationMessage message, MessageContext ctx) {
		if (ctx.side == Side.CLIENT) {
			Entity entity = Minecraft.getMinecraft().world.getEntityByID(message.entityID);
			if (entity instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer) entity;
				Minecraft.getMinecraft().addScheduledTask(() -> {
					ITransformationPlayer cap = TransformationHelper.getCap(player);
					cap.setXP(message.xp);
					cap.setTransformed(message.transformed);
					cap.setTransformationID(message.transformationID);
					cap.setTextureIndex(message.textureIndex);
				});
			}
		}
		return null;
	}

	@Override
	public String getName() {
		return "Transformation";
	}
}
