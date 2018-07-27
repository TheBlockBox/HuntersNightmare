package pixeleyestudios.huntersdream.objects.tools.axe;

import pixeleyestudios.huntersdream.init.ItemInit;
import pixeleyestudios.huntersdream.util.interfaces.IEffectiveAgainstWerewolf;

public class ToolPureSilverAxe extends ToolAxe implements IEffectiveAgainstWerewolf {

	public ToolPureSilverAxe(String name) {
		super(name, ItemInit.TOOL_PURE_SILVER);
	}
}
