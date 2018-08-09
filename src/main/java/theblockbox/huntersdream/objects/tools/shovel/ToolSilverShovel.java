package theblockbox.huntersdream.objects.tools.shovel;

import theblockbox.huntersdream.init.ItemInit;
import theblockbox.huntersdream.util.interfaces.effective.ISilverEffectiveAgainstTransformation;

public class ToolSilverShovel extends ToolShovel implements ISilverEffectiveAgainstTransformation {

	public ToolSilverShovel(String name) {
		super(name, ItemInit.TOOL_SILVER);
	}

}
