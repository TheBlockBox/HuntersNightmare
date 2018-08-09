package theblockbox.huntersdream.objects.tools.pickaxe;

import theblockbox.huntersdream.init.ItemInit;
import theblockbox.huntersdream.util.interfaces.effective.ISilverEffectiveAgainstTransformation;

public class ToolSilverPickaxe extends ToolPickaxe implements ISilverEffectiveAgainstTransformation {

	public ToolSilverPickaxe(String name) {
		super(name, ItemInit.TOOL_SILVER);
	}

}
