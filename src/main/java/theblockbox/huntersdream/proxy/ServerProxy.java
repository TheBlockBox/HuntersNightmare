package theblockbox.huntersdream.proxy;

import net.minecraft.item.Item;
import theblockbox.huntersdream.util.handlers.RegistryHandler;

public class ServerProxy implements ICommonProxy {
	@Override
	public void registerItemRenderer(Item item, int meta, String id) {
	}

	@Override
	public void preInit() {
		RegistryHandler.preInitServer();
	}

	@Override
	public void init() {
		RegistryHandler.initServer();
	}

	@Override
	public void postInit() {
		RegistryHandler.postInitServer();
	}

}
