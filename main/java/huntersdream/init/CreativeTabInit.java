package huntersdream.init;

import static huntersdream.init.ItemInit.INGOT_PURE_SILVER;
import static huntersdream.init.ItemInit.SWORD_PURE_SILVER;

import java.util.ArrayList;

import huntersdream.tabs.Tab;
import net.minecraft.creativetab.CreativeTabs;

public class CreativeTabInit {
	public static final ArrayList<CreativeTabs> TABS = new ArrayList<>();

	// CreativeTabs HUNTERSDREAM_MOBS = new Tab("Hunter's Dream Mobs", new
	// ItemStack());
	public static final CreativeTabs HUNTERSDREAM_TOOLS_AND_WEAPONS = new Tab("huntersDreamToolsCombat",
			SWORD_PURE_SILVER);
	public static final CreativeTabs HUNTERSDREAM_MISC = new Tab("huntersDreamMisc", INGOT_PURE_SILVER);
}
