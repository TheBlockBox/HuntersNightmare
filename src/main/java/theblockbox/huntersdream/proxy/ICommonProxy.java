package theblockbox.huntersdream.proxy;

import net.minecraft.item.Item;

public interface ICommonProxy {
	public void registerItemRenderer(Item item, int meta, String id);

	public void preInit();

	public void init();

	public void postInit();
}
