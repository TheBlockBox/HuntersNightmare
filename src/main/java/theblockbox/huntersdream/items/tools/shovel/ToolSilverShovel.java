package theblockbox.huntersdream.items.tools.shovel;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import theblockbox.huntersdream.init.ItemInit;

public class ToolSilverShovel extends ToolShovel {

	public ToolSilverShovel(String name) {
		super(name, ItemInit.TOOL_SILVER);
	}

	@Override
	public boolean isRepairable() {
		return true;
	}

	@Override
	public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
		int[] repairOredict = OreDictionary.getOreIDs(repair);
		int oredict = OreDictionary.getOreID("ingotSilver");
		for (int i : repairOredict)
			if (i == oredict)
				return true;
		return false;
	}
}
