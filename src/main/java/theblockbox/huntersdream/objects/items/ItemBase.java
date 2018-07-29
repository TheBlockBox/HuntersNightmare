package theblockbox.huntersdream.objects.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import theblockbox.huntersdream.Main;
import theblockbox.huntersdream.init.CreativeTabInit;
import theblockbox.huntersdream.init.ItemInit;
import theblockbox.huntersdream.util.interfaces.IHasModel;

public class ItemBase extends Item implements IHasModel {

	public ItemBase(String name) {
		this(name, CreativeTabInit.HUNTERSDREAM_MISC);
	}

	public ItemBase(String name, CreativeTabs creativeTab) {
		setUnlocalizedName(name);
		setRegistryName(name);
		setCreativeTab(creativeTab);

		ItemInit.ITEMS.add(this);
	}

	@Override
	public void registerModels() {
		Main.proxy.registerItemRenderer(this, 0, "inventory");
	}

}
