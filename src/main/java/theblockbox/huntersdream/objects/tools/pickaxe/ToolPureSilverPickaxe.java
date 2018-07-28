package theblockbox.huntersdream.objects.tools.pickaxe;

import theblockbox.huntersdream.init.ItemInit;
import theblockbox.huntersdream.util.interfaces.IEffectiveAgainstWerewolf;

public class ToolPureSilverPickaxe extends ToolPickaxe implements IEffectiveAgainstWerewolf {

	public ToolPureSilverPickaxe(String name) {
		super(name, ItemInit.TOOL_PURE_SILVER);
	}

}
