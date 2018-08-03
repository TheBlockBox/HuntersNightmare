package theblockbox.huntersdream.objects.tools.shovel;

import theblockbox.huntersdream.init.ItemInit;
import theblockbox.huntersdream.util.interfaces.effective.ISilverEffectiveAgainstTransformation;

public class ToolPureSilverShovel extends ToolShovel implements ISilverEffectiveAgainstTransformation {

	public ToolPureSilverShovel(String name) {
		super(name, ItemInit.TOOL_PURE_SILVER);
	}

}
