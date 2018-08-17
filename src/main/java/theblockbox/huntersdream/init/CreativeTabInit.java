package theblockbox.huntersdream.init;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

/**
 * @author Mr. Random
 */

public class CreativeTabInit {

	public static final CreativeTabs HUNTERSDREAM_TOOLS_AND_WEAPONS = new CreativeTabs("huntersdream.toolsandcombat") {
		@Override
		public ItemStack getTabIconItem() {
			return new ItemStack(ItemInit.SWORD_SILVER);
		}
	};

	public static final CreativeTabs HUNTERSDREAM_MISC = new CreativeTabs("huntersdream.misc") {
		@Override
		public ItemStack getTabIconItem() {
			return new ItemStack(ItemInit.INGOT_SILVER);
		}
	};

	public static final CreativeTabs HUNTERSDREAM_FURNITURE = new CreativeTabs("huntersdream.furniture") {
		@Override
		public ItemStack getTabIconItem() {
			return new ItemStack(BlockInit.ORE_SILVER);
		}
	};
}
