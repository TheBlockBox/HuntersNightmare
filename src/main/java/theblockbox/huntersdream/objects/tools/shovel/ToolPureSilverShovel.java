package theblockbox.huntersdream.objects.tools.shovel;

import theblockbox.huntersdream.init.ItemInit;
import theblockbox.huntersdream.util.interfaces.IEffectiveAgainstWerewolf;

public class ToolPureSilverShovel extends ToolShovel implements IEffectiveAgainstWerewolf {

	public ToolPureSilverShovel(String name) {
		super(name, ItemInit.TOOL_PURE_SILVER);
	}

}
