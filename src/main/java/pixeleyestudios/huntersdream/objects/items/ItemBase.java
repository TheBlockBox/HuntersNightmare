package pixeleyestudios.huntersdream.objects.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import pixeleyestudios.huntersdream.Main;
import pixeleyestudios.huntersdream.init.CreativeTabInit;
import pixeleyestudios.huntersdream.init.ItemInit;
import pixeleyestudios.huntersdream.util.interfaces.IHasModel;

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
