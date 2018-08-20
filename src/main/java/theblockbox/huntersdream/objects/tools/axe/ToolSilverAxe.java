package theblockbox.huntersdream.objects.tools.axe;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import theblockbox.huntersdream.init.ItemInit;

public class ToolSilverAxe extends ToolAxe {

	public ToolSilverAxe(String name) {
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
