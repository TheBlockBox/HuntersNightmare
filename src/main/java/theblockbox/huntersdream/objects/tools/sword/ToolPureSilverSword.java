package theblockbox.huntersdream.objects.tools.sword;

import theblockbox.huntersdream.init.ItemInit;
import theblockbox.huntersdream.util.interfaces.effective.ISilverEffectiveAgainstTransformation;

public class ToolPureSilverSword extends ToolSword implements ISilverEffectiveAgainstTransformation {

	public ToolPureSilverSword(String name) {
		super(name, ItemInit.TOOL_PURE_SILVER);
	}
}
