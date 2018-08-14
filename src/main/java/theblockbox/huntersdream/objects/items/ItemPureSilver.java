package theblockbox.huntersdream.objects.items;

import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import theblockbox.huntersdream.init.CreativeTabInit;
import theblockbox.huntersdream.util.interfaces.effective.ISilverEffectiveAgainstTransformation;

public class ItemPureSilver extends ItemBase implements ISilverEffectiveAgainstTransformation {

	public ItemPureSilver(String name) {
		super(name, CreativeTabInit.HUNTERSDREAM_MISC);
	}

	@Override
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		super.addInformation(stack, worldIn, tooltip, flagIn);
		tooltip.add(getTooltipEffectiveness());
	}
}
