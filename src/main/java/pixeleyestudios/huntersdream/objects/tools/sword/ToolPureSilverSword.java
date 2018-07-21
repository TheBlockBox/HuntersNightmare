package pixeleyestudios.huntersdream.objects.tools.sword;

import pixeleyestudios.huntersdream.init.ItemInit;
import pixeleyestudios.huntersdream.util.interfaces.IEffectiveAgainstWerewolf;

public class ToolPureSilverSword extends ToolSword implements IEffectiveAgainstWerewolf {

	public ToolPureSilverSword() {
		super("sword_pure_silver", ItemInit.TOOL_PURE_SILVER);
	}
}
