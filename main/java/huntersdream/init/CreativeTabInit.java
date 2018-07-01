package huntersdream.init;

import java.util.ArrayList;

import huntersdream.tabs.Tab;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class CreativeTabInit {
	public static final ArrayList<CreativeTabs> TABS = new ArrayList<>();

	// CreativeTabs HUNTERSDREAM_MOBS = new Tab("Hunter's Dream Mobs", new
	// ItemStack();
	CreativeTabs HUNTERSDREAM_MISC = new Tab("Hunter's Dream Miscellaneous", new ItemStack(ItemInit.INGOT_PURE_SILVER));
}
