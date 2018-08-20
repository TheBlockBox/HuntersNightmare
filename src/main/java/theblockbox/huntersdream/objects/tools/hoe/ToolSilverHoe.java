package theblockbox.huntersdream.objects.tools.hoe;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import theblockbox.huntersdream.init.ItemInit;

public class ToolSilverHoe extends ToolHoe {

	public ToolSilverHoe(String name) {
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
