package pixeleyestudios.huntersdream.potions;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

public class PotionFear extends PotionBase {

	public PotionFear() {
		super(true, 787878, "fear");
	}

	// TODO: Test if this even works
	@Override
	public void performEffect(EntityLivingBase entity, int amplifier) {

	}

	// TODO: Add curative items
	@Override
	public List<ItemStack> getCurativeItems() {
		ArrayList<ItemStack> items = new ArrayList<>();

		return items;
	}

}
