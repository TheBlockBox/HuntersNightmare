package theblockbox.huntersdream.objects.tools.hoe;

import theblockbox.huntersdream.init.ItemInit;
import theblockbox.huntersdream.util.interfaces.effective.ISilverEffectiveAgainstTransformation;

public class ToolSilverHoe extends ToolHoe implements ISilverEffectiveAgainstTransformation {

	public ToolSilverHoe(String name) {
		super(name, ItemInit.TOOL_SILVER);
	}

}
