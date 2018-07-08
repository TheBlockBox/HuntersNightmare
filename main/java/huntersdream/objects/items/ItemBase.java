package huntersdream.objects.items;

import huntersdream.Main;
import huntersdream.init.CreativeTabInit;
import huntersdream.init.ItemInit;
import huntersdream.util.interfaces.IHasModel;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class ItemBase extends Item implements IHasModel {

	public ItemBase(String name) {
		this(name, CreativeTabInit.HUNTERSDREAM_MISC);
		System.out.println("Caution! No creative tab set!");
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
