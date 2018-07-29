package theblockbox.huntersdream.init;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

/**
 * @author Mr. Random
 */

public class CreativeTabInit {

	public static final CreativeTabs HUNTERSDREAM_TOOLS_AND_WEAPONS = new CreativeTabs("huntersDreamToolsCombat") {
		@Override
		public ItemStack getTabIconItem() {
			return new ItemStack(ItemInit.SWORD_PURE_SILVER);
		}
	};

	public static final CreativeTabs HUNTERSDREAM_MISC = new CreativeTabs("huntersDreamMisc") {
		@Override
		public ItemStack getTabIconItem() {
			return new ItemStack(ItemInit.PURE_SILVER);
		}
	};
}
