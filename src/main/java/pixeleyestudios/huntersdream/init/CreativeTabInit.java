package pixeleyestudios.huntersdream.init;

import static pixeleyestudios.huntersdream.init.ItemInit.INGOT_PURE_SILVER;
import static pixeleyestudios.huntersdream.init.ItemInit.SWORD_PURE_SILVER;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class CreativeTabInit {

	// CreativeTabs HUNTERSDREAM_MOBS = new Tab("Hunter's Dream Mobs", new
	// ItemStack());
	public static final CreativeTabs HUNTERSDREAM_TOOLS_AND_WEAPONS = new Tab("huntersDreamToolsCombat",
			SWORD_PURE_SILVER);
	public static final CreativeTabs HUNTERSDREAM_MISC = new Tab("huntersDreamMisc", INGOT_PURE_SILVER);

	public static class Tab extends CreativeTabs {
		public final ItemStack TAB_ICON;

		public Tab(String name, ItemStack tabIcon) {
			super(name);
			this.TAB_ICON = tabIcon;
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
}
