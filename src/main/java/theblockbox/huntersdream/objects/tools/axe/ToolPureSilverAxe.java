package theblockbox.huntersdream.objects.tools.axe;

import theblockbox.huntersdream.init.ItemInit;
import theblockbox.huntersdream.util.interfaces.IEffectiveAgainstWerewolf;

public class ToolPureSilverAxe extends ToolAxe implements IEffectiveAgainstWerewolf {

	public ToolPureSilverAxe(String name) {
		super(name, ItemInit.TOOL_PURE_SILVER);
	}
}
