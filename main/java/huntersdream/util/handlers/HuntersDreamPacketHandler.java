package huntersdream.util.handlers;

import huntersdream.network.TransformationMessage;
import huntersdream.util.Reference;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class HuntersDreamPacketHandler {
	public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MODID);
	public static int id = 0;

	public static void register() {
		INSTANCE.registerMessage(TransformationMessage.MessageHandler.class, TransformationMessage.class, id++,
				Side.CLIENT);
	}

}
