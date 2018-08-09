package theblockbox.huntersdream.objects.tools.sword;

import theblockbox.huntersdream.init.ItemInit;
import theblockbox.huntersdream.util.interfaces.effective.ISilverEffectiveAgainstTransformation;

public class ToolSilverSword extends ToolSword implements ISilverEffectiveAgainstTransformation {

	public ToolSilverSword(String name) {
		super(name, ItemInit.TOOL_SILVER);
	}
}
