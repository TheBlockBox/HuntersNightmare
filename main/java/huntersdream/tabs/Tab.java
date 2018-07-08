package huntersdream.tabs;

import huntersdream.init.CreativeTabInit;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class Tab extends CreativeTabs {
	public final ItemStack TAB_ICON;

	public Tab(String name, ItemStack tabIcon) {
		super(name);
		this.TAB_ICON = tabIcon;
		CreativeTabInit.TABS.add(this);
	}

	public Tab(String name, Item tabIcon) {
		this(name, new ItemStack(tabIcon));
	}

	public Tab(String name, Block tabIcon) {
		this(name, new ItemStack(tabIcon));
	}

	@Override
	public ItemStack getTabIconItem() {
		return TAB_ICON;
	}

}
