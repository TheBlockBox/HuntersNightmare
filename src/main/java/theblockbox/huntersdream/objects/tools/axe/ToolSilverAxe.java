package theblockbox.huntersdream.objects.tools.axe;

import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import theblockbox.huntersdream.init.ItemInit;
import theblockbox.huntersdream.util.interfaces.effective.ISilverEffectiveAgainstTransformation;

public class ToolSilverAxe extends ToolAxe implements ISilverEffectiveAgainstTransformation {

	public ToolSilverAxe(String name) {
		super(name, ItemInit.TOOL_SILVER);
	}

	@Override
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		super.addInformation(stack, worldIn, tooltip, flagIn);
		tooltip.add(getTooltipEffectiveness());
	}
}
