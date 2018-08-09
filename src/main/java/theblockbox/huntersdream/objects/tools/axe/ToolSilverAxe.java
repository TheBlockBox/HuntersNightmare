package theblockbox.huntersdream.objects.tools.axe;

import theblockbox.huntersdream.init.ItemInit;
import theblockbox.huntersdream.util.interfaces.effective.ISilverEffectiveAgainstTransformation;

public class ToolSilverAxe extends ToolAxe implements ISilverEffectiveAgainstTransformation {

	public ToolSilverAxe(String name) {
		super(name, ItemInit.TOOL_SILVER);
	}
}
