package theblockbox.huntersdream.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import theblockbox.huntersdream.util.helpers.TransformationHelper;
import theblockbox.huntersdream.util.interfaces.ITransformationPlayer;

public class TransformationMessage extends PlayerMessageBase<TransformationMessage> {

	private int xp;
	private boolean transformed;
	private int transformationID;
	private int textureIndex;

	public TransformationMessage() {
		super(DEFAULT_ENTITY_ID);
	}

	public TransformationMessage(int xp, boolean transformed, int transformationID, int entID, int textureIndex) {
		super(entID);
		this.xp = xp;
		this.transformed = transformed;
		this.transformationID = transformationID;
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
	public IMessage onMessageReceived(TransformationMessage message, MessageContext ctx, EntityPlayer player) {
		if (ctx.side == Side.CLIENT) {
			Minecraft.getMinecraft().addScheduledTask(() -> {
				ITransformationPlayer cap = TransformationHelper.getCap(player);
				cap.setXP(message.xp);
				cap.setTransformed(message.transformed);
				cap.setTransformationID(message.transformationID);
				cap.setTextureIndex(message.textureIndex);
			});
		}
		return null;
	}

	@Override
	public String getName() {
		return "Transformation";
	}
}
