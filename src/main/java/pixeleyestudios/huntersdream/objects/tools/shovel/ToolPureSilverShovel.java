package pixeleyestudios.huntersdream.objects.tools.shovel;

import pixeleyestudios.huntersdream.init.ItemInit;
import pixeleyestudios.huntersdream.util.interfaces.IEffectiveAgainstWerewolf;

public class ToolPureSilverShovel extends ToolShovel implements IEffectiveAgainstWerewolf {

	public ToolPureSilverShovel(String name) {
		super(name, ItemInit.TOOL_PURE_SILVER);
	}

}
