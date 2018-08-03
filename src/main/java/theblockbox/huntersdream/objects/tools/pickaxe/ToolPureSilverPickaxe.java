package theblockbox.huntersdream.objects.tools.pickaxe;

import theblockbox.huntersdream.init.ItemInit;
import theblockbox.huntersdream.util.interfaces.effective.ISilverEffectiveAgainstTransformation;

public class ToolPureSilverPickaxe extends ToolPickaxe implements ISilverEffectiveAgainstTransformation {

	public ToolPureSilverPickaxe(String name) {
		super(name, ItemInit.TOOL_PURE_SILVER);
	}

}
