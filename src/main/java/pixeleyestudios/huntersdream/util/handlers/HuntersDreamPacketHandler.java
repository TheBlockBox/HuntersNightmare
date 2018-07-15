package pixeleyestudios.huntersdream.util.handlers;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import pixeleyestudios.huntersdream.network.TransformationMessage;
import pixeleyestudios.huntersdream.network.TransformationTextureIndexMessage;
import pixeleyestudios.huntersdream.network.TransformationWerewolfNightOver;
import pixeleyestudios.huntersdream.network.TransformationWerewolfNoControlMessage;
import pixeleyestudios.huntersdream.network.TransformationXPMessage;
import pixeleyestudios.huntersdream.util.Reference;

public class HuntersDreamPacketHandler {
	public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MODID);
	public static int id = 0;

	public static void register() {
		INSTANCE.registerMessage(TransformationMessage.MessageHandler.class, TransformationMessage.class, id++,
				Side.CLIENT);
		INSTANCE.registerMessage(TransformationTextureIndexMessage.MessageHandler.class,
				TransformationTextureIndexMessage.class, id++, Side.SERVER);
		INSTANCE.registerMessage(TransformationXPMessage.MessageHandler.class, TransformationXPMessage.class, id++,
				Side.CLIENT);
		INSTANCE.registerMessage(TransformationXPMessage.MessageHandler.class, TransformationXPMessage.class, id++,
				Side.SERVER);
		INSTANCE.registerMessage(TransformationWerewolfNoControlMessage.MessageHandler.class,
				TransformationWerewolfNoControlMessage.class, id++, Side.CLIENT);
		INSTANCE.registerMessage(TransformationWerewolfNightOver.MessageHandler.class,
				TransformationWerewolfNightOver.class, id++, Side.CLIENT);
	}

}
