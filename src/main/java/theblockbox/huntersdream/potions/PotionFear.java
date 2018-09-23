package theblockbox.huntersdream.potions;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;

public class PotionFear extends PotionBase {

	public PotionFear() {
		super(true, 787878, 1, "fear");
	}

	// TODO: Add curative items
	@Override
	public List<ItemStack> getCurativeItems() {
		List<ItemStack> items = new ArrayList<>();
		return items;
	}
}
