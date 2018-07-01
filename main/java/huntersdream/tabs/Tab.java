package huntersdream.tabs;

import huntersdream.init.CreativeTabInit;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class Tab extends CreativeTabs {
	public final ItemStack TAB_ICON;

	public Tab(String name, ItemStack tabIcon) {
		super(name);
		this.TAB_ICON = tabIcon;
		CreativeTabInit.TABS.add(this);
	}

	@Override
	public ItemStack getTabIconItem() {
		return TAB_ICON;
	}

}
